package com.example.roadtolegend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.ClubPlayer
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoalCoral
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PlayerDetailDialog(
    player: ClubPlayer,
    club: Club?,
    onDismiss: () -> Unit
) {
    val currencyFormatter = NumberFormat.getNumberInstance(Locale.GERMANY)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = if (player.isUser) 2.dp else 1.dp,
                    color = if (player.isUser) GoldPrimary else DarkCardBorder,
                    shape = RoundedCornerShape(16.dp)
                ),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Avatar, OVR và Thông tin chính
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerAvatar(player = player, size = 68.dp)

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = player.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = if (player.isUser) GoldPrimary else TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${player.position.fullNameVi} (#${player.number})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PitchGreen
                        )

                        Text(
                            text = "${club?.name ?: "CLB"} • ${player.nationality}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    // Khối OVR Card Badge
                    Surface(
                        color = when {
                            player.ovr >= 80 -> GoldPrimary
                            player.ovr >= 70 -> PitchGreen
                            player.ovr >= 60 -> DarkSurfaceVariant
                            else -> DarkBg
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.border(
                            width = 1.dp,
                            color = GoldPrimary,
                            shape = RoundedCornerShape(8.dp)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = player.ovr.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = if (player.ovr >= 80) DarkBg else TextPrimary
                            )
                            Text(
                                text = player.position.code,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (player.ovr >= 80) DarkBg else GoldPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Thông tin cá nhân & tài chính (2 cột)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = DarkBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text("Tuổi: ${player.age}", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("Chiều cao: ${player.heightCm} cm", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text("Chân thuận: ${player.preferredFoot.labelVi}", fontSize = 11.sp, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = "Định giá: € ${currencyFormatter.format(player.marketValue)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Lương tuần: € ${currencyFormatter.format(player.weeklyWage)}",
                                fontSize = 11.sp,
                                color = PitchGreen
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${player.matchesPlayed} trận • ${player.goals} bàn • ${player.assists} kiến tạo",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (player.isUser) GoldPrimary else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bảng 6 chỉ số chi tiết chuẩn FIFA
                Text(
                    text = "CHỈ SỐ EA FC / FIFA (1 - 99)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMuted,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                FifaStatBar("PAC (Tốc độ)", player.pac)
                FifaStatBar("SHO (Dứt điểm)", player.sho)
                FifaStatBar("PAS (Chuyền bóng)", player.pas)
                FifaStatBar("DRI (Kỹ thuật/Rê bóng)", player.dri)
                FifaStatBar("DEF (Phòng ngự)", player.def)
                FifaStatBar("PHY (Thể lực/Sức mạnh)", player.phy)

                Spacer(modifier = Modifier.height(14.dp))

                // Gợi ý cho người dùng biết tên file ảnh để thêm vào code
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = DarkSurfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📷 File ảnh:", fontSize = 10.sp, color = TextMuted)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "res/drawable/${player.avatarResName.lowercase()}.png",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("ĐÓNG", fontWeight = FontWeight.Bold, color = DarkBg)
                }
            }
        }
    }
}

@Composable
private fun FifaStatBar(label: String, value: Int) {
    val color = when {
        value >= 80 -> PitchGreen
        value >= 70 -> Color(0xFF66BB6A)
        value >= 60 -> GoldPrimary
        value >= 50 -> WarningAmber
        else -> GoalCoral
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.width(135.dp)
        )

        LinearProgressIndicator(
            progress = { (value / 99f).coerceIn(0f, 1f) },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = DarkBg
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = value.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            color = color,
            modifier = Modifier.width(26.dp)
        )
    }
}
