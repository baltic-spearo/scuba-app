plugins {
    kotlin("jvm") version "2.4.10"
}

group = "ie.setu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")
    implementation("org.slf4j:slf4j-simple:2.0.18")
}

kotlin {
    jvmToolchain(26)
}

tasks.test {
    useJUnitPlatform()
}