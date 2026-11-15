package com.nextlayer3d.app.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// The website's two brand gradients (Nextlayer.css): headings use
// linear-gradient(90deg, accent-1, accent-2, heading-2) = green -> sky ->
// purple, while buttons/pills use just accent-1 -> accent-2 (green -> sky,
// no purple). Kept here as the single source so the app's headings and
// primary CTAs actually look branded instead of falling back to flat
// Material primary (the "buttons are only green, headings have no
// gradient" feedback).
val NextlayerHeadingBrush = Brush.linearGradient(listOf(NlGreen, NlSky, NlPurple))
val NextlayerButtonBrush = Brush.linearGradient(listOf(NlGreen, NlSky))
private val NextlayerDisabledButtonBrush = Brush.linearGradient(listOf(Color.Gray, Color.Gray))

/** Drop-in replacement for `Text` on anything that should read as a
 * "heading" the way the website treats them — same text, same default
 * style (so it still picks up TopAppBar's title style, etc.), just with
 * the brand gradient painted on instead of a flat color. */
@Composable
fun GradientText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.copy(brush = NextlayerHeadingBrush),
        textAlign = textAlign,
    )
}

/** Drop-in replacement for `Button` for primary calls-to-action — paints
 * the website's green-to-sky button gradient instead of a flat Material
 * `primary` fill. Mirrors `Button`'s own API (a `RowScope` content slot)
 * so existing call sites only need their `Button(...)` renamed. */
@Composable
fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(12.dp),
    content: @Composable () -> Unit,
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = Color.Transparent,
        modifier = modifier.height(52.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = if (enabled) NextlayerButtonBrush else NextlayerDisabledButtonBrush,
                    shape = shape,
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ProvideTextStyle(TextStyle(color = Color.White, fontWeight = FontWeight.Bold)) {
                content()
            }
        }
    }
}
