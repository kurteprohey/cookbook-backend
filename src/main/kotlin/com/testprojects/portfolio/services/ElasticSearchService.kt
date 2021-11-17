package com.testprojects.portfolio.services

import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.testprojects.portfolio.configuration.ElasticSearchConfig
import com.testprojects.portfolio.controllers.RecipeSearchDTO
import com.testprojects.portfolio.entities.Recipe
import com.testprojects.portfolio.mappers.RecipeMapper
import io.searchbox.client.JestClient
import io.searchbox.core.Delete
import io.searchbox.core.Index
import io.searchbox.core.Search
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

const val INDEX_ALIAS_NAME = "cookbook-alias"
const val INDEX_NAME = "cookbook"
const val TYPE_NAME = "_doc"

@Service
class ElasticSearchService(
    val recipeMapper: RecipeMapper
) {
    @Autowired
    lateinit var jestClient: JestClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var elasticSearchConfig: ElasticSearchConfig

    fun save(recipe: Recipe) {
        objectMapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION)
        val writer = objectMapper
            .writer().withDefaultPrettyPrinter()
        val index = Index.Builder(writer.writeValueAsString(recipeMapper.recipeToElasticDTO(recipe)))
            .index(INDEX_ALIAS_NAME)
            .type(TYPE_NAME)
            .id(recipe.id.toString())
            .setParameter("refresh", elasticSearchConfig.refresh)
            .build()
        val result = jestClient.execute(index)

        if (!result.isSucceeded) {
            throw RuntimeException(result.errorMessage)
        }
    }

    private fun searchStringByIngredients(recipeSearchDTO: RecipeSearchDTO, offset: Long, size: Long) = "{" +
        "  \"from\": $offset," +
        "  \"size\": $size," +
        "  \"query\": {" +
        "    \"nested\": {" +
        "      \"path\": \"ingredients\"," +
        "      \"query\": {" +
        "        \"bool\": {" +
        "          \"must\": [{" +
        "            \"match\": { \"ingredients.name\": { \"query\": \"${recipeSearchDTO.ingredients.joinToString(" ")}\", \"fuzziness\": \"auto\"}}" +
        "          }]" +
        "        }" +
        "      }" +
        "    }" +
        "  }," +
        "  \"sort\": [" +
        "    {" +
        "      \"id\": {" +
        "        \"order\": \"desc\"" +
        "      }" +
        "    }" +
        "  ]" +
        "}"
    private fun searchStringByIngredientsAndCategory(recipeSearchDTO: RecipeSearchDTO, offset: Long, size: Long) = "{" +
        "  \"from\": $offset," +
        "  \"size\": $size," +
        "  \"query\": {" +
        "    \"bool\": {" +
        "      \"must\": [" +
        "        {" +
        "          \"nested\": {" +
        "            \"path\": \"ingredients\"," +
        "            \"query\": {" +
        "              \"bool\": {" +
        "                \"must\": [" +
        "                  {" +
        "                    \"match\": {" +
        "                      \"ingredients.name\": {" +
        "                        \"query\": \"${recipeSearchDTO.ingredients.joinToString(" ")}\"," +
        "                        \"fuzziness\": \"auto\"" +
        "                      }" +
        "                    }" +
        "                  }" +
        "                ]" +
        "              }" +
        "            }" +
        "          }" +
        "        }," +
        "        {" +
        "          \"nested\": {" +
        "            \"path\": \"category\"," +
        "            \"query\": {" +
        "              \"bool\": {" +
        "                \"filter\": [" +
        "                  {" +
        "                    \"term\": {" +
        "                      \"category.title.keyword\": \"${recipeSearchDTO.category}\"" +
        "                    }" +
        "                  }" +
        "                ]" +
        "              }" +
        "            }" +
        "          }" +
        "        }" +
        "      ]" +
        "    }" +
        "  }" +
        "}"
    private fun searchStringByCategory(recipeSearchDTO: RecipeSearchDTO, offset: Long, size: Long) = "{" +
        "  \"from\": $offset," +
        "  \"size\": $size," +
        "  \"query\": {" +
        "    \"nested\": {" +
        "      \"path\": \"category\"," +
        "      \"query\": {" +
        "        \"term\": {" +
        "          \"category.title.keyword\": \"${recipeSearchDTO.category}\"" +
        "        }" +
        "      }" +
        "    }" +
        "  }," +
        "  \"sort\": [" +
        "    {\"id\": {\"order\": \"desc\"}}" +
        "  ]" +
        "}"

    fun searchRecipes(recipeSearchDTO: RecipeSearchDTO): List<Recipe> {
        val pageOffset = ((recipeSearchDTO.page - 1) * recipeSearchDTO.size).toLong()
        val searchQuery =
            if (recipeSearchDTO.category.isNotEmpty() && recipeSearchDTO.ingredients.isNotEmpty())
                searchStringByIngredientsAndCategory(recipeSearchDTO, pageOffset, recipeSearchDTO.size)
            else if (recipeSearchDTO.category.isNotEmpty())
                searchStringByCategory(recipeSearchDTO, pageOffset, recipeSearchDTO.size)
            else searchStringByIngredients(recipeSearchDTO, pageOffset, recipeSearchDTO.size)
        val result = jestClient.execute(
            Search.Builder(searchQuery)
                .addIndex(INDEX_ALIAS_NAME)
                .build()
        )
        if (!result.isSucceeded) {
            throw RuntimeException(result.errorMessage)
        }
        return result
            .sourceAsStringList
            .map { objectMapper.readValue(it, Recipe::class.java) }
            .filterNotNull()
    }

    fun delete(id: Long) {
        val result = jestClient.execute(
            Delete.Builder(id.toString())
                .index(INDEX_ALIAS_NAME)
                .type(TYPE_NAME)
                .build()
        )
        if (!result.isSucceeded) {
            throw RuntimeException(result.errorMessage)
        }
    }
}
