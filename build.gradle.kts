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
        val gradleBuildToolsVersion = "7.2.2"
        val gradlePluginVersion = "1.5.31"
        val kotlinVersion = "1.7.10"
        val safeArgsVersion = "2.6.0-alpha01"
        val googleServicesVersion = "4.3.13"
        val firebasePerfPluginVersion = "1.4.1"
        val firebaseAppDistributionVersion = "3.0.3"
        val ktLintVersion = "11.0.0"
        val hiltPluginVersion = "2.43.2"
        val chaquopyVersion = "12.0.1"

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
