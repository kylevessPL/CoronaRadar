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
        val gradleBuildToolsVersion = "7.0.3"
        val gradlePluginVersion = "1.5.31"
        val kotlinVersion = "1.6.0"
        val safeArgsVersion = "2.4.0-beta02"
        val googleServicesVersion = "4.3.10"
        val firebasePerfPluginVersion = "1.4.0"
        val firebaseAppDistributionVersion = "2.2.0"
        val ktLintVersion = "10.2.0"
        val hiltPluginVersion = "2.40.4"
        val chaquopyVersion = "10.0.1"
        val jUnit5PluginVersion = "1.8.0.0"

        classpath("com.android.tools.build:gradle:$gradleBuildToolsVersion")
        classpath(kotlin("gradle-plugin", version = gradlePluginVersion))
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$safeArgsVersion")
        classpath("com.google.gms:google-services:$googleServicesVersion")
        classpath("com.google.firebase:perf-plugin:$firebasePerfPluginVersion")
        classpath(
            "com.google.firebase:firebase-appdistribution-gradle:$firebaseAppDistributionVersion"
        )
        classpath("org.jlleitschuh.gradle:ktlint-gradle:$ktLintVersion")
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltPluginVersion")
        classpath("org.jetbrains.kotlin:kotlin-noarg:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("com.chaquo.python:gradle:$chaquopyVersion")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:$jUnit5PluginVersion")
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
