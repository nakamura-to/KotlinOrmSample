import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("org.ktorm:ktorm-core:3.3.0")
    runtimeOnly("ch.qos.logback:logback-classic:1.2.3")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("KtormKt")
}
