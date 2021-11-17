package com.testprojects.portfolio.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import io.searchbox.client.JestClient
import io.searchbox.client.JestClientFactory
import io.searchbox.client.config.HttpClientConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent

@Configuration
class ElasticSearchConfig(
    @Value("\${elasticsearch.url}") val elasticSearchURL: String,
    @Value("\${elasticsearch.refresh:false}") val refresh: String,
    @Value("\${elasticsearch.connectionsPerRoute:75}") val connnectionsPerRoute: Int,
    @Value("\${elasticsearch.maxTotalConnections:100}") val maxTotalConnections: Int,
    @Value("\${elasticsearch.connectionTimeout:2000}") val connectionTimeout: Int,
    @Value("\${elasticsearch.readTimeout:60000}") val readTimeout: Int,
    val mapper: ObjectMapper
) {
    // : ApplicationListener<ContextRefreshedEvent>
    @Bean
    fun jestClient(): JestClient {
        val factory = JestClientFactory()
        factory.setHttpClientConfig(
            HttpClientConfig
                .Builder(elasticSearchURL)
                .multiThreaded(true)
                .defaultMaxTotalConnectionPerRoute(connnectionsPerRoute)
                .maxTotalConnection(maxTotalConnections)
                .connTimeout(connectionTimeout)
                .readTimeout(readTimeout)
                .build()
        )
        return factory.getObject()
    }

    // override fun onApplicationEvent(event: ContextRefreshedEvent) {
    //     with(jestClient()) {
    //
    //         val existingIndexes = getAliases(mapper)
    //
    //         ES_INDEXES
    //             .forEach {
    //                 if (existingIndexes.keys.find { indexName -> indexName.startsWith(it.key + "-") } == null) {
    //                     // an index we need does not exist at all
    //                     val indexName = it.key + "-" + System.currentTimeMillis()
    //                     createIndex(indexName, it.value)
    //                     createAlias(indexName, it.key + "-alias")
    //                 }
    //             }
    //
    //         // If a newer index exists already, switch to it
    //         ES_INDEXES.forEach { idx ->
    //             val currentIndex = getCurrentIndex(idx.key, mapper)
    //             if (currentIndex != null) {
    //                 getNewerIndexIfExists(idx.key, mapper)
    //                     ?.also { logger.info { "Found newer index $it" } }
    //                     ?.also { moveAlias(idx.key + "-alias", listOf(currentIndex), it) }
    //                     ?.also { deleteIndex(currentIndex) }
    //             }
    //         }
    //     }
    // }
}
