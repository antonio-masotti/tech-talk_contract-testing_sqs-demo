val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "1.9.20"
    id("io.ktor.plugin") version "2.3.6"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.20"
}

group = "com.bikeleasing"
version = "0.0.1"

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
    //systemProperties["pact.rootDir"] = "$projectDir/pacts"
}
