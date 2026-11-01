package com.example.roadtolegend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.ui.components.FUTPlayerCard
import com.example.roadtolegend.ui.viewmodel.CareerViewModel
import com.example.roadtolegend.ui.viewmodel.MainTab
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoalCoral
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DashboardScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Thanh trạng thái trên cùng: Mùa giải, Tuần, Ví tiền
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MÙA ${careerState.seasonNumber} • TUẦN ${careerState.currentWeek}/${careerState.totalWeeksInSeason}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${careerState.currentLeague.name} (${careerState.currentYear})",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "€${String.format("%,d", careerState.player.balance)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                }
            }
        }

        // Cảnh báo chấn thương nếu có
        if (careerState.player.isInjured) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = GoalCoral.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = "Injury", tint = GoalCoral, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ĐANG CHẤN THƯƠNG: ${careerState.player.injuryName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoalCoral
                            )
                            Text(
                                text = "Dự kiến nghỉ thi đấu thêm ${careerState.player.injuryWeeksRemaining} tuần nữa.",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // 2. Thẻ Cầu Thủ FUT
        item {
            FUTPlayerCard(
                player = careerState.player,
                club = careerState.currentClub
            )
        }

        // 3. Khối Trận Đấu Kế Tiếp (Next Fixture)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("TRẬN ĐẤU VÒNG ${careerState.currentWeek}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(
                            text = if (careerState.player.isInjured) "Ngồi ngoài (Chấn thương)" else "Sẵn sàng thi đấu",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (careerState.player.isInjured) GoalCoral else PitchGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            com.example.roadtolegend.ui.components.ClubBadge(club = careerState.currentClub, size = 42.dp, isFictionalMode = careerState.isFictionalMode)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(careerState.currentClub.getDisplayShortName(careerState.isFictionalMode), fontWeight = FontWeight.Black, fontSize = 14.sp, color = TextPrimary)
                            Text("Sân nhà", fontSize = 10.sp, color = TextSecondary)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("VS", fontWeight = FontWeight.Black, fontSize = 16.sp, color = GoldPrimary)
                            Text("Tuần ${careerState.currentWeek}", fontSize = 10.sp, color = TextMuted)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            com.example.roadtolegend.ui.components.ClubBadge(club = careerState.nextOpponent, size = 42.dp, isFictionalMode = careerState.isFictionalMode)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(careerState.nextOpponent.getDisplayShortName(careerState.isFictionalMode), fontWeight = FontWeight.Black, fontSize = 14.sp, color = TextPrimary)
                            Text("Sân khách", fontSize = 10.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.selectTab(MainTab.MATCHDAY) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg)
                        ) {
                            Icon(Icons.Default.SportsSoccer, contentDescription = "Match", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("VÀO TRẬN ĐẤU", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { viewModel.advanceToNextWeek() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = TextPrimary)
                        ) {
                            Icon(Icons.Default.FastForward, contentDescription = "Next Week", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SANG TUẦN MỚI", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 4. Bảng Tin Tức & Truyền Thông (News Feed)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Campaign, contentDescription = "News", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("BẢN TIN BÓNG ĐÁ TRONG TUẦN", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (careerState.newsFeed.isEmpty()) {
                    Text("Chưa có tin tức mới.", fontSize = 12.sp, color = TextMuted)
                } else {
                    careerState.newsFeed.take(3).forEach { news ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = news.headline,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = news.body,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Khoảng trống dưới cùng
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
