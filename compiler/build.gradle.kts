import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

repositories {
    mavenCentral()
}

tasks.test.configure {
    testLogging { events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED) }
    useJUnitPlatform()
}

dependencies {
    compileOnly(libs.kotlin.compiler)
    ksp(libs.auto.ksp)
    compileOnly(libs.auto.annotations)
}
