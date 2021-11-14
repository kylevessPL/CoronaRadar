buildscript {

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            setUrl("https://chaquo.com/maven")
        }
        maven {
            setUrl("https://jitpack.io")
        }
    }

    dependencies {
        val kotlinVersion = "1.5.31"

        classpath("com.android.tools.build:gradle:7.0.3")
        classpath(kotlin("gradle-plugin", version = "1.5.31"))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.4.0-beta02")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:perf-plugin:1.4.0")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:10.2.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.1")
        classpath("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("com.chaquo.python:gradle:10.0.1")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.8.0.0")
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint").version("10.2.0").apply(false)
    id("org.jlleitschuh.gradle.ktlint-idea").version("10.2.0")
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        android.set(true)
        enableExperimentalRules.set(true)
        outputColorName.set("RED")
        outputToConsole.set(true)
    }

    tasks {
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions {
                jvmTarget = "11"
            }
        }

        withType<org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask> {
            workerMaxHeapSize.set("512m")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
