package com.bikeleasing.consumer

import kotlinx.serialization.Serializable

@Serializable
data class SQSMessage(
    val attributes: Map<String, String>? = null,
    val body: String,
    val messageAttributes: Map<String, MessageAttribute> = emptyMap(),
    val md5OfBody: String,
    val messageId: String = "",
    val receiptHandle: String? = null
)

@Serializable
data class MessageAttribute(
    val stringValue: String,
    val dataType: String
)
