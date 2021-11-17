package com.testprojects.portfolio.management

import com.google.common.base.Stopwatch
import com.testprojects.portfolio.services.ElasticSearchIndexRebuildService
import mu.KotlinLogging
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import org.springframework.lang.Nullable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Endpoint(id = "indexrebuild")
@Component
class IndexRebuildEndpoint(
    val indexRebuildService: ElasticSearchIndexRebuildService
) {

    @WriteOperation
    @Transactional
    @Async
    fun refreshIndexes(@Nullable index: String?) {
        val timer = Stopwatch.createStarted()

        logger.info { "Starting rebuild of index ${index ?: "-all-"}" }

        indexRebuildService.recreateIndex()

        logger.info { "Index rebuild complete after $timer" }
    }
}
