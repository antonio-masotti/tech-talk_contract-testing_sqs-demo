package com.bikeleasing.consumer

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.LambdaDsl.newJsonBody
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.messaging.MessagePact
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(value = [PactConsumerTestExt::class])
@PactTestFor(providerName = "contract-testing", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V3)
class SQSConsumerContractTest {


    private val jsonBody = newJsonBody { root ->
        root.stringValue("body", "Hello World!")
    }.build()

    @Pact(consumer = "sqs-consumer", provider = "contract-testing")
    fun getMessages(builder: MessagePactBuilder): MessagePact =
        builder
            .given("a message")
            .expectsToReceive("a message")
            .withMetadata(mapOf("Content-Type" to "application/json"))
            .withContent(jsonBody)
            .toPact()

    @Test
    @PactTestFor(pactMethod = "getMessages")
    fun testGetMessages(pact: MessagePact) {
        assert(pact.messages.isNotEmpty())
        pact.messages.forEach { message ->
            println(message.contents.valueAsString())
            assert(message.contents.valueAsString() == "{\"body\":\"Hello World!\"}")
            assert(message.metadata.containsValue("application/json"))
        }
    }


}
