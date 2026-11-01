package com.example.roadtolegend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.roadtolegend.data.AvatarManager
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.Player
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EnergyHigh
import com.example.ui.theme.EnergyLow
import com.example.ui.theme.EnergyMid
import com.example.ui.theme.GoalCoral
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FUTPlayerCard(
    player: Player,
    club: Club,
    modifier: Modifier = Modifier,
    onAvatarClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(GoldPrimary, GoldDark.copy(alpha = 0.6f), DarkCardBorder)
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DarkSurfaceVariant.copy(alpha = 0.9f),
                            DarkSurface
                        )
                    )
                )
                .padding(16.dp)
        ) {
            // Phần trên: OVR, Vị trí, Quốc tịch, CLB và Silhouette
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Khối OVR & Position
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = player.ovr.toString(),
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary,
                        lineHeight = 44.sp
                    )
                    Text(
                        text = player.primaryPosition.code,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PitchGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "🇻🇳 ${player.nationality}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ClubBadge(club = club, size = 18.dp)
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = club.shortName,
                            fontSize = 12.sp,
                            color = GoldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Cầu thủ Avatar / Silhouette với viền vàng
                val customBitmap = remember(player.customAvatarPath) { AvatarManager.loadBitmapFromFile(player.customAvatarPath) }
                val preset = remember(player.customAvatarPath) { AvatarManager.getPresetById(player.customAvatarPath) }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .then(
                            if (onAvatarClick != null) Modifier.clickable { onAvatarClick() } else Modifier
                        )
                        .background(
                            Brush.radialGradient(
                                colors = listOf(GoldDark.copy(alpha = 0.5f), DarkSurfaceVariant)
                            )
                        )
                        .border(2.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        customBitmap != null -> {
                            Image(
                                bitmap = customBitmap.asImageBitmap(),
                                contentDescription = "Avatar cầu thủ",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        preset != null -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(preset.themeColor, preset.accentColor)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = preset.icon,
                                    contentDescription = preset.name,
                                    tint = Color.White,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }
                        else -> {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar cầu thủ",
                                modifier = Modifier.size(52.dp),
                                tint = GoldPrimary
                            )
                        }
                    }

                    // Huy hiệu Camera nhỏ báo hiệu có thể đổi ảnh khi chạm
                    if (onAvatarClick != null) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(GoldPrimary)
                                .border(1.5.dp, DarkSurface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Đổi ảnh đại diện",
                                tint = DarkBg,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tên cầu thủ & Chiều cao, Tuổi, Chân thuận
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = player.name.uppercase(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${player.age} tuổi • ${player.heightCm}cm • ${player.preferredFoot.labelVi}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Giá trị chuyển nhượng ước tính
                Surface(
                    color = GoldDark.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Text(
                        text = "€${String.format("%,d", player.marketValue)}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Lưới 6 chỉ số cốt lõi (PAC, SHO, PAS, DRI, STA, DEF)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatBadgeItem("PAC", player.pac.toInt())
                StatBadgeItem("SHO", player.sho.toInt())
                StatBadgeItem("PAS", player.pas.toInt())
                StatBadgeItem("DRI", player.dri.toInt())
                StatBadgeItem("STA", player.sta.toInt())
                StatBadgeItem("DEF", player.def.toInt())
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Thanh trạng thái: Năng lượng, Mệt mỏi, Phong độ, Tinh thần
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatusMeterItem(
                    icon = Icons.Default.Bolt,
                    label = "Thể lực",
                    value = "${player.energy}%",
                    progress = player.energy / 100f,
                    activeColor = if (player.energy > 60) EnergyHigh else if (player.energy > 30) EnergyMid else EnergyLow,
                    modifier = Modifier.weight(1f)
                )

                StatusMeterItem(
                    icon = Icons.Default.FitnessCenter,
                    label = "Độ mỏi",
                    value = "${player.fatigue.toInt()}%",
                    progress = (player.fatigue / 100.0).toFloat().coerceIn(0f, 1f),
                    activeColor = if (player.fatigue < 40) EnergyHigh else if (player.fatigue < 70) EnergyMid else EnergyLow,
                    modifier = Modifier.weight(1f)
                )

                StatusMeterItem(
                    icon = Icons.Default.LocalFireDepartment,
                    label = "Phong độ",
                    value = "${player.form}%",
                    progress = player.form / 100f,
                    activeColor = GoldPrimary,
                    modifier = Modifier.weight(1f)
                )

                StatusMeterItem(
                    icon = Icons.Default.Star,
                    label = "Tinh thần",
                    value = "${player.morale}%",
                    progress = player.morale / 100f,
                    activeColor = PitchGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nhận xét Tuyển Trạch Viên (Scout Report - Football Manager Style)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = DarkCardBorder.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MilitaryTech,
                        contentDescription = "Scout",
                        tint = GoldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scout: ${player.scoutReport}",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatBadgeItem(label: String, value: Int) {
    val statColor = when {
        value >= 80 -> GoldPrimary
        value >= 70 -> PitchGreen
        value >= 55 -> Color(0xFFFFB74D)
        else -> GoalCoral
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            text = value.toString(),
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            color = statColor
        )
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted
        )
    }
}

@Composable
fun StatusMeterItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    progress: Float,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = activeColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = value,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = activeColor,
            trackColor = DarkCardBorder,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = TextMuted
        )
    }
}
