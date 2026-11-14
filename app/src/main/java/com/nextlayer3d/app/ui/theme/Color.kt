package com.nextlayer3d.app.ui.theme

import androidx.compose.ui.graphics.Color

// Nextlayer's own palette (same hex values as the website's --accent-1/
// --accent-2/--heading-1/--heading-2 and the iOS app's AccentColors) —
// kept here as the single source so both color schemes below derive from
// the same three brand hues instead of drifting.
val NlGreen = Color(0xFF22C55E)
val NlSky = Color(0xFF38BDF8)
val NlBlue = Color(0xFF3B82F6)
val NlPurple = Color(0xFFA855F7)

// The website and iOS app are permanently dark (it's the brand's actual
// identity, not just a dark-mode option) — these are that same near-black,
// green-leaning background/surface pair, not a generic Material neutral.
val NlNearBlack = Color(0xFF0A120D)
val NlSurfaceDark = Color(0xFF111B15)
val NlSurfaceDarkElevated = Color(0xFF17221B)

// Light mode isn't "the same thing inverted" — a plain off-white keeps the
// green lean of the dark surface instead of going neutral gray.
val NlOffWhite = Color(0xFFF6FAF7)
val NlSurfaceLight = Color(0xFFFFFFFF)
val NlSurfaceLightDim = Color(0xFFEAF3EC)
