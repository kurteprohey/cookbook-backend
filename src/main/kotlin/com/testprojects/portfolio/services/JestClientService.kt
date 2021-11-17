package com.testprojects.portfolio.services

import io.searchbox.client.JestClient
import io.searchbox.client.JestClientFactory
import io.searchbox.client.config.HttpClientConfig
import org.springframework.stereotype.Service

@Service
class JestClientService (
    var client: JestClient? = null
) {
    fun jestClient(): JestClient? {
        if (client == null) {
            val factory = JestClientFactory()
            factory.setHttpClientConfig(
                HttpClientConfig.Builder("http://localhost:9200")
                    .multiThreaded(true)
                    .defaultMaxTotalConnectionPerRoute(2)
                    .maxTotalConnection(10)
                    .build()
            )
            return factory.getObject()
        } else return client
    }
}
