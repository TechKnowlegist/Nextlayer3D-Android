plugins {
    id("com.android.application") version "8.9.1" apply false
    // Bumped from 2.0.20 after CI hit a Kotlin K2 compiler internal crash
    // ("FileAnalysisException ... source must not be null" while analyzing
    // MainActivity.kt) - classic symptom of the Compose compiler plugin
    // (which ships in lockstep with the Kotlin version) being paired with
    // a much newer compose-ui than it was built for. Amplify's transitive
    // deps resolved compose-ui to 1.10.6; 2.0.20's bundled compose compiler
    // predates that by a long way.
    id("org.jetbrains.kotlin.android") version "2.1.20" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" apply false
}
