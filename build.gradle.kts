plugins {
    // Bumped from 8.9.1: that version bundles R8 8.9.32, which has a real
    // metadata-parsing bug on newer Kotlin annotation classes (crashes with
    // "Should never be called" while rewriting kotlin.ExperimentalSubclassOptIn's
    // metadata — not something a Kotlin *version* change on our side can work
    // around, since kotlin-stdlib itself carries that class). Per Google's own
    // compatibility table (developer.android.com/build/kotlin-support), R8's
    // Kotlin-metadata support only improves going forward, so the fix is a
    // newer AGP/R8, not a different Kotlin plugin version. 8.13.0 is confirmed
    // released (has its own official release notes page) and bundles a much
    // newer R8 that postdates this bug.
    id("com.android.application") version "8.13.0" apply false
    // Bumped from 2.0.20 after CI hit a Kotlin K2 compiler internal crash
    // ("FileAnalysisException ... source must not be null" while analyzing
    // MainActivity.kt) - classic symptom of the Compose compiler plugin
    // (which ships in lockstep with the Kotlin version) being paired with
    // a much newer compose-ui than it was built for. Amplify's transitive
    // deps resolved compose-ui to 1.10.6; 2.0.20's bundled compose compiler
    // predates that by a long way. 2.1.0 fixed that and is well within AGP
    // 8.13.0's supported range, so it stays as-is here.
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
}
