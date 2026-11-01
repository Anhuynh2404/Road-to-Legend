package com.example.roadtolegend.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.Club
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.GoldPrimary

@Composable
fun ClubBadge(
    club: Club,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier,
    isFictionalMode: Boolean = false
) {
    val context = LocalContext.current
    val resourceName = "logo_${club.id.lowercase()}"
    val resId = if (!isFictionalMode) {
        context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    } else 0

    if (resId != 0) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = resId),
            contentDescription = club.name,
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
        )
    } else {
        // High-fidelity fallback soccer crest badge with club colors and initials
        val primaryColor = try {
            Color(android.graphics.Color.parseColor(club.primaryColorHex))
        } catch (e: Exception) {
            GoldPrimary
        }

        val secondaryColor = try {
            Color(android.graphics.Color.parseColor(club.secondaryColorHex))
        } catch (e: Exception) {
            Color.White
        }

        val initials = if (club.shortName.length <= 3) {
            club.shortName
        } else {
            val words = club.shortName.split(" ").filter { it.isNotBlank() }
            if (words.size >= 2) {
                "${words[0].first()}${words[1].first()}".uppercase()
            } else {
                club.shortName.take(3).uppercase()
            }
        }

        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height

                // Shield Path
                val shieldPath = Path().apply {
                    moveTo(w * 0.15f, 0f)
                    lineTo(w * 0.85f, 0f)
                    quadraticBezierTo(w, h * 0.45f, w * 0.5f, h)
                    quadraticBezierTo(0f, h * 0.45f, w * 0.15f, 0f)
                    close()
                }

                // Background gradient
                drawPath(
                    path = shieldPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.85f))
                    ),
                    style = Fill
                )

                // Diagonal sash line with secondary color
                drawLine(
                    color = secondaryColor.copy(alpha = 0.55f),
                    start = Offset(0f, h * 0.7f),
                    end = Offset(w, h * 0.2f),
                    strokeWidth = w * 0.18f
                )

                // Golden / Silver border
                drawPath(
                    path = shieldPath,
                    color = Color.White.copy(alpha = 0.8f),
                    style = Stroke(width = w * 0.06f)
                )
            }

            // Initials
            val fontSize = (size.value * 0.32f).sp
            Text(
                text = initials,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
        }
    }
}
