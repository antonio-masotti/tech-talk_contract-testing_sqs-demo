package com.bikeleasing.consumer

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.DeleteMessageRequest
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageRequest
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageResponse
import com.bikeleasing.slack.SlackService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

class SQSConsumer(sqsClient: SqsClient? = null) {
    private val client = sqsClient ?: SqsClient { region = "eu-central-1" }
    private val slackService: SlackService = SlackService()
    private val logger = LoggerFactory.getLogger(javaClass)

    fun processMessages() {
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                try {
                    val messages = pollMessages()
                    logger.info("----------------------------------------")
                    logger.info("Received ${messages.size} messages")
                    messages.forEach { message ->
                        logger.info("Message: ${message.body}")
                        message.body.let {
                            slackService.sendMessage(it)
                        }

                        message.receiptHandle?.let {
                            deleteMessage(it)
                        }
                    }
                } catch (e: Exception) {
                    logger.error("Error polling messages: ${e.message}", e)
                }
                logger.info("----------------------------------------")
                delay(3000)
            }
        }
    }

    private suspend fun pollMessages(): List<SQSMessage> {

        val receiveMessageRequest = ReceiveMessageRequest {
            queueUrl = System.getenv("QUEUE_URL")
            maxNumberOfMessages = 2
            waitTimeSeconds = 1
            messageAttributeNames = listOf("destination")
        }

        val resp = client.receiveMessage(receiveMessageRequest)
        return parseMessages(resp)
    }

    /**
     * Deletes a message from the SQS queue after it has been processed
     */
    private suspend fun deleteMessage(receiptHandle: String) {
        val deleteMessageRequest = DeleteMessageRequest {
            queueUrl = System.getenv("QUEUE_URL")
            this.receiptHandle = receiptHandle
        }
        client.deleteMessage(deleteMessageRequest)
    }

    /**
     * Parses the messages from the response into my own SQSMessage class
     * @param resp The response from the SQS queue
     * @return A list of SQSMessage objects or an empty list if no messages were found
     */
    private fun parseMessages(resp: ReceiveMessageResponse): List<SQSMessage> {
        val messages = resp.messages?.map { message ->

            val attributes = message.messageAttributes?.map { (key, value) ->
                key to MessageAttribute(
                    stringValue = value.stringValue ?: "",
                    dataType = value.dataType ?: ""
                )
            }?.toMap()

            SQSMessage(
                attributes = message.attributes,
                body = message.body ?: "",
                messageAttributes = attributes ?: emptyMap(),
                md5OfBody = message.md5OfBody ?: "",
                messageId = message.messageId ?: "",
                receiptHandle = message.receiptHandle
            )
        }
        return messages ?: emptyList()
    }





}
