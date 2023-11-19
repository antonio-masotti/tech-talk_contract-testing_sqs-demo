package com.bikeleasing.producer

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.MessageAttributeValue
import aws.sdk.kotlin.services.sqs.model.SendMessageRequest

class SQSProducer(sqsClient: SqsClient? = null) {

    private val client = sqsClient ?: SqsClient { region = "eu-central-1" }

    suspend fun sendMessage(message: String): String {

        val slackDestination = MessageAttributeValue {
            stringValue = "slack"
            dataType = "String"
        }

        val sendRequest = SendMessageRequest {
            queueUrl = System.getenv("QUEUE_URL")
            messageBody = message
            messageAttributes = mapOf("destination" to slackDestination)
            delaySeconds = 0
        }

        val resp = client.sendMessage(sendRequest)
        return resp.messageId ?: "No message id"
    }
}
