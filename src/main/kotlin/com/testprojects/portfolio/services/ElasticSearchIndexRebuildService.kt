package com.testprojects.portfolio.services

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.testprojects.portfolio.createIndex
import com.testprojects.portfolio.deleteIndex
import com.testprojects.portfolio.entities.Recipe
import com.testprojects.portfolio.getAliases
import com.testprojects.portfolio.mappers.RecipeMapper
import com.testprojects.portfolio.moveAlias
import com.testprojects.portfolio.repositories.RecipeRepository
import io.searchbox.client.JestClient
import io.searchbox.core.Bulk
import io.searchbox.core.Index
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Service
class ElasticSearchIndexRebuildService(
    val jestClient: JestClient,
    val objectMapper: ObjectMapper,
    val recipeRepository: RecipeRepository,
    val entityManager: EntityManager,
    val recipeMapper: RecipeMapper
) {

    fun recreateIndex(indexPrefix: String = INDEX_NAME) {
        val oldIndexNames = jestClient.getAliases(objectMapper).filterKeys { it.startsWith(indexPrefix) }.keys.toList()
        val newIndexName = indexPrefix + "-" + System.currentTimeMillis()
        val indexAlias = "$indexPrefix-alias"

        jestClient.createIndex(newIndexName)

        fillIndex(newIndexName)

        jestClient.moveAlias(indexAlias, oldIndexNames, newIndexName)

        oldIndexNames.forEach { indexName -> jestClient.deleteIndex(indexName) }

    }
    fun fillIndex(newIndexName: String) {
        var lastId: Long? = 0L
        do {
            lastId = recipeRepository
                .findFirst2000ByIdGreaterThanOrderByIdAsc(lastId ?: 0)
                .also { writeBatch(it, newIndexName) }
                .also { println { "Wrote ${it.size} recipes to index $newIndexName, lastId = ${it.lastOrNull()?.id}" } }
                .also { entityManager.clear() }
                .lastOrNull()?.id
        } while (lastId != null)
    }

    private fun writeBatch(recipes: List<Recipe>, indexName: String) {
        val writer = objectMapper
            .writer().withDefaultPrettyPrinter()

        objectMapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION)

        recipes.forEach {

            val index = Index.Builder(writer.writeValueAsString(recipeMapper.recipeToElasticDTO(it)))
                .index(indexName)
                .type(TYPE_NAME)
                .id(it.id.toString())
                .build()

            val result = jestClient.execute(index)

            if (!result.isSucceeded) {
                throw RuntimeException(result.errorMessage)
            }
        }
    }
}
