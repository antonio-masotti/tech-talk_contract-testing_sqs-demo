package com.bikeleasing.producer

import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit5.MessageTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.VerificationReports
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory


@Provider("contract-testing")
//@PactFolder("pacts")
@PactBroker(
    tags = ["main"],
)
@VerificationReports(value = ["console", "markdown", "json"], reportDir = "build/pact/reports")
class SQSPactTest {

    private val logger = LoggerFactory.getLogger(SQSPactTest::class.java)
    private var params = mapOf<String,String>()

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @BeforeEach
    fun before(context: PactVerificationContext) {
        context.target = MessageTestTarget(packagesToScan = listOf(this::class.java.packageName))
    }

    @State("the provider has sent a message")
    fun `the provider has sent a message`(params: Map<String,String>) {
        logger.info("the provider has sent a message")
        this.params = params
    }


    @PactVerifyProvider("a test message with the body Hello World!")
    fun `produces a well formed message`(): String {
        logger.info("the provider has sent a message with params $params")

        val m = Message(this.params["body"]!!).toJsonString()
        logger.info("produces a well formed message: ${m}")
        return m
    }

}
