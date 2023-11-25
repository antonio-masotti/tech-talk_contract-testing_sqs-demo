val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.20"
    id("io.ktor.plugin") version "2.3.6"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
    id("au.com.dius.pact") version "4.0.10"
}

group = "com.bikeleasing"
version = "0.0.2"

application {
    mainClass.set("com.bikeleasing.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-default-headers-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("aws.sdk.kotlin:sqs:0.24.0-beta")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.2")
    implementation("com.slack.api:slack-api-client:1.30.0")
    implementation("io.ktor:ktor-server-swagger-jvm")

    testImplementation("au.com.dius:pact-jvm-consumer:4.0.10")
    testImplementation("au.com.dius:pact-jvm-consumer-junit5:4.0.10")

    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
    // https://docs.pact.io/implementation_guides/jvm/docs/system-properties
    systemProperties["pact.rootDir"] = "$projectDir/pacts"
    systemProperties["pactbroker.url"] = System.getenv("PACT_BROKER_URL")
    systemProperties["pactbroker.auth.token"] = System.getenv("PACT_BROKER_TOKEN")
    systemProperties["pact.verifier.publishResults"] = true
}

tasks.register("prepareKotlinBuildScriptModel"){}


fun getBranch(): String {
    val os = org.apache.commons.io.output.ByteArrayOutputStream()
    project.exec {
        commandLine = "git rev-parse --abbrev-ref HEAD".split(" ")
        standardOutput = os
    }
    return String(os.toByteArray()).trim()
}


pact {
    publish {
        pactDirectory = "$projectDir/pacts"
        pactBrokerUrl = System.getenv("PACT_BROKER_URL")
        pactBrokerToken = System.getenv("PACT_BROKER_TOKEN")
        tags = listOf(getBranch())
    }
}
