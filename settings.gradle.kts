dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

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
}

plugins {
    id("de.fayard.refreshVersions") version "0.30.2"
}

rootProject.name = "CoronaRadar"
include(":app")
