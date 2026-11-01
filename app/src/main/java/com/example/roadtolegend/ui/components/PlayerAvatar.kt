package com.example.roadtolegend.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.ClubPlayer
import com.example.roadtolegend.model.PositionCategory
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoalCoral
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PitchGreen

/**
 * Hiển thị ảnh đại diện cầu thủ:
 * - Tự động tìm kiếm file ảnh trong drawable theo quy ước "player_<id>" (hoặc avatarResName).
 * - Nếu người dùng chưa thêm ảnh PNG, hiển thị giao diện thẻ cầu thủ / áo đấu chuẩn FIFA
 *   với số áo, ký hiệu viết tắt tên và màu sắc theo vị trí thi đấu.
 */
@Composable
fun PlayerAvatar(
    player: ClubPlayer,
    size: Dp = 50.dp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resId = remember(player.avatarResName) {
        val resourceName = player.avatarResName.lowercase().replace("-", "_")
        context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
    val customBitmap = remember(player.customAvatarPath) { com.example.roadtolegend.data.AvatarManager.loadBitmapFromFile(player.customAvatarPath) }
    val preset = remember(player.customAvatarPath) { com.example.roadtolegend.data.AvatarManager.getPresetById(player.customAvatarPath) }

    if (customBitmap != null) {
        // Ảnh do người chơi chụp từ Camera hoặc chọn từ Thư viện
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.22f))
                .border(
                    width = if (player.isUser) 2.dp else 1.dp,
                    color = if (player.isUser) GoldPrimary else DarkCardBorder,
                    shape = RoundedCornerShape(size * 0.22f)
                )
        ) {
            Image(
                bitmap = customBitmap.asImageBitmap(),
                contentDescription = player.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    } else if (preset != null) {
        // Bộ avatar phong cách tuyển chọn
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.22f))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(preset.themeColor, preset.accentColor)
                    )
                )
                .border(
                    width = if (player.isUser) 2.dp else 1.dp,
                    color = if (player.isUser) GoldPrimary else DarkCardBorder,
                    shape = RoundedCornerShape(size * 0.22f)
                ),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = preset.icon,
                contentDescription = preset.name,
                tint = Color.White,
                modifier = Modifier.size(size * 0.6f)
            )
        }
    } else if (resId != 0) {
        // Ảnh thật người dùng thêm vào res/drawable/
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.22f))
                .border(
                    width = if (player.isUser) 2.dp else 1.dp,
                    color = if (player.isUser) GoldPrimary else DarkCardBorder,
                    shape = RoundedCornerShape(size * 0.22f)
                )
        ) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = player.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        // Fallback Vector Avatar thẻ cầu thủ FIFA sang trọng
        val (gradientTop, gradientBottom) = when {
            player.isUser -> GoldPrimary to Color(0xFF8A6D1C)
            player.positionCategory == PositionCategory.GOALKEEPER -> Color(0xFFFFB300) to Color(0xFFE65100)
            player.positionCategory == PositionCategory.DEFENDER -> Color(0xFF1E88E5) to Color(0xFF0D47A1)
            player.positionCategory == PositionCategory.MIDFIELDER -> PitchGreen to Color(0xFF1B5E20)
            else -> GoalCoral to Color(0xFFB71C1C) // FORWARD
        }

        val initials = remember(player.name) {
            val parts = player.name.replace("★", "").trim().split(" ")
            if (parts.size >= 2) {
                "${parts.first().take(1)}${parts.last().take(1)}".uppercase()
            } else {
                player.name.take(2).uppercase()
            }
        }

        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.24f))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientTop, gradientBottom)
                    )
                )
                .border(
                    width = if (player.isUser) 2.dp else 1.dp,
                    color = if (player.isUser) GoldPrimary else Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(size * 0.24f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(2.dp)
            ) {
                // Tên viết tắt hoặc huy hiệu số áo
                Text(
                    text = if (player.isUser) "★" else initials,
                    fontSize = (size.value * 0.32f).sp,
                    fontWeight = FontWeight.Black,
                    color = if (player.isUser) Color.Black else Color.White,
                    letterSpacing = 0.5.sp
                )

                // Số áo nhỏ bên dưới
                Text(
                    text = "#${player.number}",
                    fontSize = (size.value * 0.20f).sp,
                    fontWeight = FontWeight.Bold,
                    color = if (player.isUser) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.9f)
                )
            }

            // Huy hiệu quốc gia nhỏ ở góc trên
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(DarkSurface.copy(alpha = 0.75f))
                    .padding(horizontal = 2.dp, vertical = 0.5.dp)
            ) {
                Text(
                    text = player.nationalityCode,
                    fontSize = (size.value * 0.16f).sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}
