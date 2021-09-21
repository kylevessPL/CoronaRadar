buildscript {
    val androidPluginVersion by rootProject.extra { "7.0.2" }

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$androidPluginVersion")
        classpath(kotlin("gradle-plugin", version = "1.5.31"))
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
        classpath("de.mannodermaus.gradle.plugins:android-junit5:1.8.0.0")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}