plugins {
    kotlin("jvm") version "1.9.23"
    application
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lib"))
    implementation("org.junit.jupiter:junit-jupiter:5.8.1")
    implementation("org.jetbrains.kotlinx:atomicfu:0.23.2")
    implementation("org.jetbrains.kotlinx:lincheck:2.30")
}

application {
    mainClass = "benchmark.MainKt"
}