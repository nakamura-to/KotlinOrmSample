import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    application
    kotlin("jvm")
    id("com.google.devtools.ksp") version "1.5.0-1.0.0-alpha09"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("org.komapper:komapper-starter:0.6.1")
    ksp("org.komapper:komapper-processor:0.6.1")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

val generatedSourcePath = "build/generated/ksp/main/kotlin"

sourceSets {
    main {
        java {
            srcDir(generatedSourcePath)
        }
    }
}

idea.module {
    generatedSourceDirs.add(file(generatedSourcePath))
}

application {
    mainClass.set("komapper.KomapperKt")
}
