plugins {
    kotlin("jvm") version "2.0.20"
}

group = "com.bartoszwesolowski"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.javamoney:moneta:1.4.4")
    implementation("nl.hiddewieringa:money-kotlin:2.0.0")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}