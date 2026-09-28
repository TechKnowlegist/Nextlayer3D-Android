# Nextlayer3D Android

Native Jetpack Compose companion app for [Nextlayer3D](https://nextlayer3d.app)
— shares the exact same AWS Amplify backend as the website and the iOS app
(same Cognito user pool, same AppSync GraphQL API, same S3 bucket), so an
account created in any of the three works in all of them, and
products/orders are the same data everywhere.

## Status: Phase 1 — Core Commerce

Same scope as the iOS app's first phase:

- Sign up / sign in / sign out (same Cognito user pool as the web and iOS apps)
- Pre-Built product browsing + product detail
- Cart (local state) + Stripe checkout (PaymentSheet)
- Order confirmation + Track Order (by order number)
- Account page + saved addresses

Not yet ported (planned for later phases): the 3D Editor, Minecraft
voxelizer, AI (Layerai), AR viewer, Showroom, Build Plate, Hall of Fame,
Admin panel.

## Testing this one is easier than iOS

Unlike the iOS app, **you can run this directly on your own computer for
free** — Android Studio and its emulator work natively on Windows, Linux,
and Mac. No cloud Mac rental, no Apple-style signing dance needed just to
see it running. See `SETUP.md`.

## Why this repo has no Gradle wrapper checked in

`gradlew`/`gradlew.bat`/`gradle-wrapper.jar` aren't committed. Android
Studio generates them automatically the first time you open this project,
and that's the recommended way to get them since this was built without a
local Android/Gradle install to verify a hand-placed wrapper jar is valid.
CI (`.github/workflows/android-ci.yml`) sidesteps this by having
`gradle/actions/setup-gradle` provision Gradle directly instead of relying
on a wrapper.

## CI

Every push builds a debug APK on a plain `ubuntu-latest` runner — no
Apple-style OS/toolchain juggling, no Apple Developer account, nothing to
pay for. The built APK is uploaded as a workflow artifact.
