
buildscript {
    repositories {
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
    }
}

plugins {
    `java-gradle-plugin`
    kotlin("jvm") version "1.3.61"
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://nexus.usethesource.io/content/repositories/releases/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.rascalmpl:rascal:0.15.3")
}

gradlePlugin {
    plugins {
        create("rascal-plugin") {
            id = "mleegwt.rascal.gradle.plugin"
            implementationClass = "mleegwt.rascal.gradle.plugin.RascalPlugin"
        }
    }
}

