plugins {
    kotlin("jvm") version "1.5.0"
}

group = "me.tsukakei"
version = "1.0-SNAPSHOT"

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    repositories {
        mavenCentral()
    }
    
    dependencies {
        "implementation"("mysql", "mysql-connector-java", "8.0.24")
    }
}