import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    kotlin("jvm") version "1.5.0"
    id("com.google.devtools.ksp") version "1.5.0-1.0.0-alpha09"
}

group = "me.tsukakei"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("org.ktorm:ktorm-core:3.3.0")
    implementation("org.jetbrains.exposed", "exposed-core", "0.31.1")
    implementation("org.jetbrains.exposed", "exposed-dao", "0.31.1")
    implementation("org.jetbrains.exposed", "exposed-jdbc", "0.31.1")
    implementation("org.jetbrains.exposed", "exposed-jodatime", "0.31.1")
    implementation("mysql", "mysql-connector-java", "8.0.24")
    implementation(kotlin("reflect"))
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
