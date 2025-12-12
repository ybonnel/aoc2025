plugins {
    kotlin("jvm") version "2.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.ortools:ortools-java:9.8.3296")
}

kotlin {
    jvmToolchain(24)
}
