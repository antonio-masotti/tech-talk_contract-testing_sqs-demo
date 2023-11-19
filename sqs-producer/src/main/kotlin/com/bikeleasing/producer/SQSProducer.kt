package com.bikeleasing.producer

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.SendMessageRequest

class SQSProducer(sqsClient: SqsClient? = null) {

    private val client = sqsClient ?: SqsClient { region = "eu-central-1" }

    suspend fun sendMessage(message: String): String {

        val sendRequest = SendMessageRequest {
            queueUrl = "https://sqs.eu-central-1.amazonaws.com/697345274579/contract-testing"
            messageBody = message
            delaySeconds = 0
        }

        val resp = client.sendMessage(sendRequest)
        return resp.messageId ?: "No message id"
    }
}
