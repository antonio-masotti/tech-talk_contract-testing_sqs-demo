val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.20"
    id("io.ktor.plugin") version "2.3.6"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
}

val producerVersion = "0.0.3"

group = "com.bikeleasing"
version = producerVersion

application {
    mainClass.set("com.bikeleasing.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
//    repositories {
//        maven { url = uri("https://jitpack.io") }
//    }
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-default-headers-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("aws.sdk.kotlin:sqs:0.24.0-beta")
    implementation("io.github.smiley4:ktor-swagger-ui:2.7.1")



    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    //testImplementation("au.com.dius:pact-jvm-provider:4.2.14")
    implementation("au.com.dius.pact.provider:junit5:4.3.19")
}

tasks.withType<Test> {
    useJUnitPlatform()
    // https://docs.pact.io/implementation_guides/jvm/docs/system-properties
    //systemProperties["pact.rootDir"] = "$projectDir/pacts"
    systemProperties["pactbroker.url"] = System.getenv("PACT_BROKER_URL")
    systemProperties["pactbroker.auth.token"] = System.getenv("PACT_BROKER_TOKEN")
    systemProperties["pact.verifier.publishResults"] = true
    systemProperties["pact.provider.version"] = producerVersion
}

tasks.register("prepareKotlinBuildScriptModel"){}
