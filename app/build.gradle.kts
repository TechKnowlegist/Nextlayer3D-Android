plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.nextlayer3d.app"
    // Bumped from 34 to 36 (and AGP 8.5.2 -> 8.9.1 in the root build file)
    // after CI's first real run: Amplify's current release pulls in
    // AndroidX transitives (core 1.17.0, lifecycle 2.10.0, compose-ui
    // 1.10.6) that require compiling against API 36 with a new-enough AGP.
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nextlayer3d.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        // Amplify's core modules now require this (they use java.time /
        // streams APIs that need desugaring to run on minSdk 26).
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // Extended set, not just material-icons-core — LocalShipping/Widgets
    // aren't in the small curated core set bundled with material3.
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Amplify doesn't publish a BOM the way Compose/Stripe do — every
    // module needs the same version applied individually. Left as "+"
    // (latest) rather than a guessed pin: this was written without a
    // local Gradle to verify exact current version numbers against, and
    // "does it resolve and compile" from CI is the real check.
    implementation("com.amplifyframework:core:+")
    implementation("com.amplifyframework:core-kotlin:+")
    implementation("com.amplifyframework:aws-auth-cognito:+")
    implementation("com.amplifyframework:aws-api:+")
    implementation("com.amplifyframework:aws-storage-s3:+")

    implementation("com.google.code.gson:gson:2.11.0")

    // Same reasoning as Amplify above — left dynamic rather than a guessed pin.
    implementation("com.stripe:stripe-android:+")

    implementation("io.coil-kt:coil-compose:2.7.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}
