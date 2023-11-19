package com.bikeleasing

import com.bikeleasing.consumer.SQSConsumer
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.launch

fun main() {
    embeddedServer(Netty, port = 8888, host = "127.0.0.1") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    val consumer = SQSConsumer() // Consider injecting this
    launch {
        consumer.processMessages()
    }
}
