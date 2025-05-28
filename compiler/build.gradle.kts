import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin.binary.compatibility) apply false
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

repositories {
    mavenCentral()
}

listOf("apiDump", "apiBuild").forEach { tasks.getByName(it) { enabled = false } }

tasks.test.configure {
    testLogging { events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED) }
    useJUnitPlatform()
}

dependencies {
    compileOnly(libs.kotlin.compiler)
    ksp(libs.google.auto.ksp)
    compileOnly(libs.google.auto.annotations)
}
