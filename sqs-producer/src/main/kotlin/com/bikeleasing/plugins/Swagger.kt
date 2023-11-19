package com.bikeleasing.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.server.application.*


fun Application.configureSwagger() {
    install(SwaggerUI) {
        swagger {
            swaggerUrl = "swagger"
            forwardRoot = true
        }
        info {
            title = "SQS Producer"
            version = "0.0.1"
            description = "Demo API to showcase SQS Producer and contract Testing"
        }
        server {
            url = "http://localhost:8080"
            description = "Development server"
        }
    }

}
