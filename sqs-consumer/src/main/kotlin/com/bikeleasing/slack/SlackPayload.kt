package com.bikeleasing.slack

import kotlinx.serialization.Serializable

@Serializable
data class SlackPayload(val text: String)
