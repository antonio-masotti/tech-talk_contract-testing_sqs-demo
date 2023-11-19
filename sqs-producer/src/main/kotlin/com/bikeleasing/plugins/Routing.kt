package com.bikeleasing.plugins

import com.bikeleasing.producer.SQSProducer
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        post("/send") {
            val body = call.receiveText()
            val service = SQSProducer()

            val messageId = service.sendMessage(body)
            call.respondText("Message sent with id: $messageId")

        }
    }
}

//curl -X 'POST' \
//'http://localhost:8080/send' \
//-H "Content-Type: application/json" \
//-d '{"name": "Toni", "message": "Hey everybody"}'
