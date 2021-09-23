plugins {
    id("com.android.application")
    id("dagger.hilt.android.plugin")
    id("de.mannodermaus.android-junit5")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "pl.piasta.coronaradar"
        minSdk = 26
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments += mapOf("runnerBuilder" to "de.mannodermaus.junit5.AndroidJUnit5Builder")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        viewBinding = true
        mlModelBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packagingOptions {
        resources.excludes += "DebugProbesKt.bin"
    }
}

dependencies {
    val androidPluginVersion = rootProject.extra.get("androidPluginVersion")
    val activityVersion = "1.3.1"
    val fragmentVersion = "1.3.6"
    val lifecycleVersion = "2.2.0"
    val navigationVersion = "2.3.5"
    val hiltVersion = "2.38.1"
    val coroutinesVersion = "1.5.2"
    val tensorflowLiteVersion = "0.2.0"
    val koTestVersion = "5.0.0.M1"

    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.test:core-ktx:1.4.0")
    implementation("androidx.activity:activity-ktx:$activityVersion")
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.6.0")
    implementation("org.tensorflow:tensorflow-lite-support:$tensorflowLiteVersion")
    implementation("org.tensorflow:tensorflow-lite-metadata:$tensorflowLiteVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    kapt("com.android.databinding:compiler:$androidPluginVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$koTestVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:$koTestVersion")
    testImplementation("io.mockk:mockk:1.12.0")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
    androidTestRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.0")
    androidTestImplementation("de.mannodermaus.junit5:android-test-core:1.3.0")
    androidTestRuntimeOnly("de.mannodermaus.junit5:android-test-runner:1.3.0")
    androidTestImplementation("com.kaspersky.android-components:kaspresso:1.2.1")
}

kapt {
    correctErrorTypes = true
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.withType<Test> {
    useJUnitPlatform()
}