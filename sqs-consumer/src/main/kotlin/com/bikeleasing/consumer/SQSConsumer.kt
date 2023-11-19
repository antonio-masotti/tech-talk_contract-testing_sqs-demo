package com.bikeleasing.consumer

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.DeleteMessageRequest
import aws.sdk.kotlin.services.sqs.model.Message
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageRequest
import com.bikeleasing.slack.SlackService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

class SQSConsumer(sqsClient: SqsClient? = null) {
    private val client = sqsClient ?: SqsClient { region = "eu-central-1" }
    private val slackService: SlackService = SlackService()
    private val logger = LoggerFactory.getLogger(javaClass)

    fun pollMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    val messages = getMessages()
                    logger.info("Received ${messages?.size} messages")
                    messages?.forEach { message ->
                        logger.info("Message: ${message.body}")

                        message.body?.let {
                            slackService.sendMessage(it)
                        }

                        message.receiptHandle?.let {
                            deleteMessage(it)
                        }
                    }
                } catch (e: Exception) {
                    logger.error("Error polling messages: ${e.message}", e)
                }
                delay(3000)
            }
        }
    }

    private suspend fun getMessages(): List<Message>? {

        val receiveMessageRequest = ReceiveMessageRequest {
            queueUrl = "https://sqs.eu-central-1.amazonaws.com/697345274579/contract-testing"
            maxNumberOfMessages = 10
            waitTimeSeconds = 10
        }

        val resp = client.receiveMessage(receiveMessageRequest)
        return resp.messages
    }

    private suspend fun deleteMessage(receiptHandle: String) {
        val deleteMessageRequest = DeleteMessageRequest {
            queueUrl = "https://sqs.eu-central-1.amazonaws.com/697345274579/contract-testing"
            this.receiptHandle = receiptHandle
        }
        client.deleteMessage(deleteMessageRequest)
    }



}
