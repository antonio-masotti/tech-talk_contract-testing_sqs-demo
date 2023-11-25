package com.bikeleasing.consumer

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.messaging.Message
import au.com.dius.pact.core.model.messaging.MessagePact
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(value = [PactConsumerTestExt::class])
@PactTestFor(providerName = "contract-testing", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V3)
class SQSConsumerContractTest {


    private val jsonBody = PactDslJsonBody().apply {
        stringValue("body", "Hello World!")
        stringValue("md5OfBody", "ed076287532e86365e841e92bfc50d8c")
        //eachLike("messageAttributes", mapOf("destination" to mapOf("stringValue" to "slack", "dataType" to "String")))
    }


    @Pact(consumer = "sqs-consumer", provider = "contract-testing")
    fun createPact(builder: MessagePactBuilder): MessagePact =
        builder
            .given("the provider has sent a message", mapOf("body" to "Hello World!"))
            .expectsToReceive("a test message with the body Hello World!")
            .hasPactWith("contract-testing")
            .withContent(jsonBody)
            .toPact()

    @Test
    @PactTestFor(pactMethod = "createPact", providerName = "contract-testing")
    fun testMessages(messages: List<Message>) {
        assert(messages.isNotEmpty())

        val message = messages.first()
        val sqsMessage = Json.decodeFromString<SQSMessage>(message.contents.valueAsString())

        assert(sqsMessage.md5OfBody == "ed076287532e86365e841e92bfc50d8c")
        assert(sqsMessage.body == "Hello World!")

        if (sqsMessage.messageAttributes.isNotEmpty()) {
            assert(sqsMessage.messageAttributes["destination"]?.stringValue == "slack")
        }
    }


}
