package com.bikeleasing.consumer

import aws.sdk.kotlin.services.sqs.SqsClient
import aws.sdk.kotlin.services.sqs.model.DeleteMessageRequest
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageRequest
import aws.sdk.kotlin.services.sqs.model.ReceiveMessageResponse
import com.bikeleasing.slack.SlackService
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

class SQSConsumer(
    private val client: SqsClient = SqsClient { region = "eu-central-1" },
    private val slackService: SlackService = SlackService())
{

    private val logger = LoggerFactory.getLogger(javaClass)

    companion object {
        private const val MAX_NO_OF_MESSAGES = 2
        private const val WAIT_TIME_SECONDS = 1
        private val QUEUE_URL = System.getenv("QUEUE_URL")
    }

    /**
     * Process incoming messages by retrieving messages from a message queue,
     * logging the received messages, sending them to a slack service, and deleting
     * processed messages from the queue.
     */
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
            queueUrl = QUEUE_URL
            maxNumberOfMessages = MAX_NO_OF_MESSAGES
            waitTimeSeconds = WAIT_TIME_SECONDS
            messageAttributeNames = listOf("destination")
        }

        val resp = client.receiveMessage(receiveMessageRequest)
        return parseMessages(resp)
    }


    /**
     * Deletes a message from the queue.
     *
     * @param receiptHandle The receipt handle of the message to be deleted.
     */
    private suspend fun deleteMessage(receiptHandle: String) {
        val deleteMessageRequest = DeleteMessageRequest {
            queueUrl = QUEUE_URL
            this.receiptHandle = receiptHandle
        }
        client.deleteMessage(deleteMessageRequest)
    }


    /**
     * Parses the received message response into a list of SQSMessage objects.
     *
     * @param resp The response containing the messages to be parsed.
     * @return A list of SQSMessage objects.
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
