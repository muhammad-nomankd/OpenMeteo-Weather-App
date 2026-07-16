package durranitech.openmeteonews.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Produces the animated shimmer brush — reuse across all shimmer boxes. */
@Composable
fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.White.copy(alpha = 0.08f),
        Color.White.copy(alpha = 0.20f),
        Color.White.copy(alpha = 0.08f),
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue  = 1200f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslate",
    )


    return Brush.linearGradient(
        colors = shimmerColors,
        start  = Offset(translateAnim - 300f, 0f),
        end    = Offset(translateAnim, 0f),
    )
}

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(shimmerBrush()),
    )
}

/** Full skeleton of the weather home screen shown during initial load. */
@Composable
fun WeatherScreenSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(80.dp))

        // Location line
        ShimmerBox(modifier = Modifier.width(140.dp).height(20.dp))
        Spacer(Modifier.height(12.dp))

        // Big temperature
        ShimmerBox(modifier = Modifier.width(120.dp).height(72.dp))
        Spacer(Modifier.height(8.dp))

        // Condition label
        ShimmerBox(modifier = Modifier.width(100.dp).height(18.dp))
        Spacer(Modifier.height(4.dp))

        // Feels like
        ShimmerBox(modifier = Modifier.width(160.dp).height(14.dp))
        Spacer(Modifier.height(36.dp))

        // Stats row
        Row {
            repeat(3) {
                ShimmerBox(
                    modifier = Modifier.weight(1f).height(72.dp),
                    shape = RoundedCornerShape(16.dp),
                )
                if (it < 2) Spacer(Modifier.width(12.dp))
            }
        }
        Spacer(Modifier.height(24.dp))

        // Hourly row
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(100.dp))
        Spacer(Modifier.height(24.dp))

        // Daily rows
        repeat(5) {
            ShimmerBox(modifier = Modifier.fillMaxWidth().height(52.dp))
            Spacer(Modifier.height(8.dp))
        }
    }
}
