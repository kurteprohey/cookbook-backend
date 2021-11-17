package com.testprojects.portfolio

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.testprojects.portfolio.errors.ElasticSearchException
import io.searchbox.client.JestClient
import io.searchbox.client.JestResult
import io.searchbox.indices.CreateIndex
import io.searchbox.indices.DeleteIndex
import io.searchbox.indices.aliases.AddAliasMapping
import io.searchbox.indices.aliases.GetAliases
import io.searchbox.indices.aliases.ModifyAliases
import io.searchbox.indices.aliases.RemoveAliasMapping
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun JestClient.getAliases(mapper: ObjectMapper): Map<String, List<String>> {
    val aliasesJson = GetAliases.Builder()
        .build()
        .let { execute(it) }
        .assertSuccess()
        .jsonString

    val jsonNode: JsonNode = mapper.readTree(aliasesJson)
    return jsonNode.fields().asSequence().map {
        it.key to it.value["aliases"].fields().asSequence().map { it.key }.toList()
    }.toMap()
}

fun JestClient.createIndex(indexName: String) {
    println("creating index")
    CreateIndex.Builder(indexName)
        .settings("""{
            "mappings": {
                "properties": {
                    "category": {
                        "type": "nested",
                        "properties": {
                            "id": {
                                "type": "long"
                            },
                            "imageUrl": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "title": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            }
                        }
                    },
                    "complexity": {
                        "type": "long"
                    },
                    "created": {
                        "type": "date"
                    },
                    "createdByUser": {
                        "type": "nested",
                        "properties": {
                            "email": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            }
                        }
                    },
                    "description": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "id": {
                        "type": "long"
                    },
                    "imageUrl": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "ingredients": {
                        "type": "nested",
                        "properties": {
                            "amount": {
                                "type": "float"
                            },
                            "name": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            },
                            "unit": {
                                "type": "text",
                                "fields": {
                                    "keyword": {
                                        "type": "keyword",
                                        "ignore_above": 256
                                    }
                                }
                            }
                        }
                    },
                    "name": {
                        "type": "text",
                        "fields": {
                            "keyword": {
                                "type": "keyword",
                                "ignore_above": 256
                            }
                        }
                    },
                    "timeToCook": {
                        "type": "long"
                    }
                }
            }
        }""".trimIndent())
        .build()
        .let { execute(it) }
        .assertSuccess()
        .also { println { "Sucessfully created index $indexName" } }
}

fun JestClient.moveAlias(aliasName: String, oldIndexNames: List<String>, newIndexName: String) {
    ModifyAliases.Builder(
        RemoveAliasMapping.Builder(oldIndexNames, aliasName).build()
    ).addAlias(
        AddAliasMapping.Builder(newIndexName, aliasName).build()
    ).build()
        .let { execute(it) }
        .assertSuccess()
        .also { logger.info { "Moved alias succesfully to Index $newIndexName from old indexes $oldIndexNames" } }
}

fun JestResult.assertSuccess() = takeIf { isSucceeded } ?: throw ElasticSearchException(errorMessage)

fun JestClient.deleteIndex(indexName: String) =
    execute(DeleteIndex.Builder(indexName).build())
        .assertSuccess()
        .also { logger.info { "Successfully deleted old index $indexName" } }
