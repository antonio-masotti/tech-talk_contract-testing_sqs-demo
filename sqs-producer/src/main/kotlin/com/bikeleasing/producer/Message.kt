package com.bikeleasing.producer

import aws.sdk.kotlin.services.sqs.model.MessageAttributeValue
import aws.sdk.kotlin.services.sqs.model.SendMessageRequest
import com.bikeleasing.extensions.md5
import com.bikeleasing.extensions.sha256

class Message(private val body: String, private val destination: String = "slack")  {
    private val md5OfBody = body.sha256()

    fun toSQSRequest(): SendMessageRequest {
        val slackDestination = MessageAttributeValue {
            stringValue = destination
            dataType = "String"
        }
        val sendRequest = SendMessageRequest {
            queueUrl = System.getenv("QUEUE_URL")
            messageBody = body
            messageAttributes = mapOf("destination" to slackDestination)
            delaySeconds = 0
        }
        return sendRequest
    }

    fun toJsonString(): String {
        return """
            {
                "body": "$body",
                "md5OfBody": "$md5OfBody",
                "messageAttributes": {
                    "destination": {
                        "stringValue": "$destination",
                        "dataType": "String"
                    }
                }
            }
        """.trimIndent()
    }
}

