plugins {
    id("com.android.application") version "8.9.1" apply false
    // Bumped from 2.0.20 after CI hit a Kotlin K2 compiler internal crash
    // ("FileAnalysisException ... source must not be null" while analyzing
    // MainActivity.kt) - classic symptom of the Compose compiler plugin
    // (which ships in lockstep with the Kotlin version) being paired with
    // a much newer compose-ui than it was built for. Amplify's transitive
    // deps resolved compose-ui to 1.10.6; 2.0.20's bundled compose compiler
    // predates that by a long way.
    //
    // 2.1.20 (the next attempt) overshot the other way: D8 (bundled in
    // AGP 8.9.1) warned "parsing kotlin metadata... normally happens when
    // using a newer version of kotlin than the kotlin version released
    // when this version of R8 was created" and compileDebugKotlin failed.
    // Settling on 2.1.0 - the first 2.1.x release, likeliest to have
    // shipped in the same window as AGP 8.9.1 and be within its R8's
    // supported metadata range, while still well past 2.0.20.
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
}
