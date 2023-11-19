package com.bikeleasing.plugins

import com.bikeleasing.producer.SQSProducer
import io.ktor.server.application.*
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("hello", {
            tags = listOf("Basics")
            description = "Simple 'Hello World'- Route"
            response {
                HttpStatusCode.OK to {
                    description = "Successful Response"
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Successful Response"
                    body<String> {
                        example("plain", "Hello World!")
                    }
                    headers {
                        header<String>("X-Engine").apply {
                            description = "Name of the engine"
                            body<String> {
                                example("plain", "Ktor")
                            }
                        }
                    }
                }
                HttpStatusCode.ServiceUnavailable to {
                    description = "Service Unavailable"
                }
            }
        }) {
            call.respondText("Hello World!")
        }
        post("/send", {
            tags = listOf("Queue Operations")
            summary = "Send message to SQS"
            description = "Send messages to SQS"
            request {
                body<String> {
                    description = "Message to send"
                    example("json", "{\"name\": \"Toni\", \"message\": \"Hey everybody\"}")
                    example("plain", "Hello World!")
                }
            }
            response {
                HttpStatusCode.OK to {
                    description = "Successful Response with the ID of the sent message"
                    body<String> {
                        example("plain", "Message sent with id: 123456789")
                    }
                    headers {
                        header<String>(HttpHeaders.ContentType)
                    }
                }
                HttpStatusCode.ServiceUnavailable to {
                    description = "Service Unavailable"
                    body<String> {
                        example("plain", "Service Unavailable")
                    }
                    headers {
                        header<String>(HttpHeaders.ContentType)
                    }
                }
            }
        }) {
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
