# Setup

## Running it on your own computer (free, no rental needed)

1. Install [Android Studio](https://developer.android.com/studio) (Windows, Linux, or Mac all work).
2. Open this folder as a project — Android Studio will offer to generate
   the Gradle wrapper on first open (or you can let it use its own bundled
   Gradle). Accept it.
3. Let it sync — this downloads the Amplify Android SDK, Stripe Android
   SDK, and Jetpack Compose dependencies, which can take a few minutes the
   first time.
4. Create a virtual device (Device Manager → Create Device — any modern
   phone profile works) or plug in a real Android phone with USB debugging
   enabled.
5. Hit Run. That's it — no signing, no developer account, no cost, unlike
   the iOS side.

## Application ID

`app/build.gradle.kts` currently sets `applicationId` to
`com.nextlayer3d.app` as a placeholder, matching the iOS app's bundle ID
for consistency. Change it before publishing to Google Play — it needs to
be unique to your Play Console account.

## Publishing to Google Play

Whenever you're ready to actually publish (not required just to test):

1. **Register a Google Play Developer account**: https://play.google.com/console/signup
   — a one-time $25 fee, versus Apple's recurring $99/year.
2. **Generate a signing key** — this is a plain command-line step on any OS:
   ```
   keytool -genkeypair -v -keystore release.keystore -alias nextlayer3d -keyalg RSA -keysize 2048 -validity 10000
   ```
   Keep `release.keystore` and its passwords safe and out of git — losing
   it means you can never update the app under the same listing again.
3. Add the keystore + its passwords as GitHub Actions secrets, and extend
   `android-ci.yml` to run `gradle bundleRelease` (produces an `.aab`,
   what Play Store wants) signed with that keystore.
4. Upload the `.aab` to the Play Console, fill in the store listing, and
   submit for review.

Ask for help with steps 3-4 whenever you're actually ready to publish —
they're worth doing once you have a working, tested build rather than
speculatively now.
