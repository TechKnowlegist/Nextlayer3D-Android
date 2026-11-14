package com.nextlayer3d.app.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Same wireframe cube as the website's logo.svg and the iOS app's icon —
// nine lines in a 0..100 coordinate space, gradient-stroked green to
// sky-blue to purple. Drawn on a Canvas instead of shipping an SVG/VectorDrawable
// asset so it stays pixel-identical at any size and the gradient direction
// matches the original exactly (top-left to bottom-right).
private val cubeLines = listOf(
    Offset(50f, 20f) to Offset(80f, 35f),
    Offset(50f, 20f) to Offset(20f, 35f),
    Offset(80f, 35f) to Offset(50f, 50f),
    Offset(20f, 35f) to Offset(50f, 50f),
    Offset(80f, 35f) to Offset(80f, 65f),
    Offset(20f, 35f) to Offset(20f, 65f),
    Offset(50f, 50f) to Offset(50f, 80f),
    Offset(20f, 65f) to Offset(50f, 80f),
    Offset(80f, 65f) to Offset(50f, 80f),
)

@Composable
fun NextlayerLogo(modifier: Modifier = Modifier, size: Dp = 64.dp) {
    Canvas(modifier = modifier.size(size)) {
        val scale = this.size.minDimension / 100f
        val strokeWidth = 3f * scale
        val brush = Brush.linearGradient(
            colors = listOf(NlGreen, NlSky, NlPurple),
            start = Offset(0f, 0f),
            end = Offset(this.size.width, this.size.height),
        )
        cubeLines.forEach { (start, end) ->
            drawLine(
                brush = brush,
                start = Offset(start.x * scale, start.y * scale),
                end = Offset(end.x * scale, end.y * scale),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}
