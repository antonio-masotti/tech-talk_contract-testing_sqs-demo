package com.bikeleasing.producer

import aws.sdk.kotlin.services.sqs.SqsClient
import org.slf4j.LoggerFactory

class SQSProducer(sqsClient: SqsClient? = null) {

    private val client = sqsClient ?: SqsClient { region = "eu-central-1" }
    private val logger = LoggerFactory.getLogger(SQSProducer::class.java)

    suspend fun sendMessage(body: String): String {

        val message = Message(body)

        try {
            val resp = client.sendMessage(message.toSQSRequest())

            logger.info("Message sent: ${message.toJsonString()}")
            logger.info("Message id: ${resp.messageId}")
            return message.toJsonString()
        } catch (e: Exception) {
            logger.error("Error sending message: ${e.message}")
            throw e
        }
    }
}

/**
 * curl -X POST -H 'Content-Type: application/json' \
 * -d '{"text": "hallo-world-from-curl"}' \
 * http://localhost:8080/send
 */
