package com.example.roadtolegend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.roadtolegend.model.BallonDorCandidate
import com.example.roadtolegend.model.SeasonAwardWinner
import com.example.roadtolegend.model.SeasonAwardsCeremony
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
import com.example.ui.theme.WarningAmber

@Composable
fun AwardsCeremonyDialog(
    ceremony: SeasonAwardsCeremony,
    onAcceptAndContinue: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Giải Đấu Mùa Giải, 1: Gala Quả Bóng Vàng

    Dialog(
        onDismissRequest = { /* Phải nhấn nút tiếp tục */ },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .border(2.dp, GoldPrimary, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Gala Đỉnh Cao
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(GoldDark.copy(alpha = 0.8f), DarkSurface)
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Celebration, contentDescription = null, tint = GoldLight, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LỄ TRAO GIẢI BÓNG ĐÁ CUỐI MÙA",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldLight,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldLight, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "MÙA GIẢI ${ceremony.seasonNumber} • NĂM ${ceremony.year}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    // Tab chuyển đổi
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = DarkSurfaceVariant,
                        contentColor = GoldPrimary
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("DANH HIỆU MÙA GIẢI", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("GALA QUẢ BÓNG VÀNG 🟡", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                        )
                    }

                    // Danh sách giải thưởng
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (selectedTab == 0) {
                            // Cúp Vô Địch
                            item {
                                ChampionBanner(ceremony = ceremony)
                            }

                            // Huy chương đã giành được
                            if (ceremony.userEarnedTrophies.isNotEmpty()) {
                                item {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = GoldPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("🎉", fontSize = 24.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "CHÚC MỪNG! BẠN ĐÃ ĐẠT ${ceremony.userEarnedTrophies.size} DANH HIỆU:",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = GoldLight
                                                )
                                                ceremony.userEarnedTrophies.forEach { trophy ->
                                                    Text(
                                                        text = "• ${trophy.iconEmoji} ${trophy.name}",
                                                        fontSize = 11.sp,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                Text(
                                                    text = "Đã tự động cất vào Tủ trưng bày danh hiệu!",
                                                    fontSize = 10.sp,
                                                    color = PitchGreen
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            items(ceremony.awards) { award ->
                                AwardWinnerCard(award = award)
                            }
                        } else {
                            // GALA QUẢ BÓNG VÀNG THẾ GIỚI
                            item {
                                BallonDorHeader(winner = ceremony.ballonDorWinner, year = ceremony.year)
                            }

                            item {
                                Text(
                                    text = "BẢNG XẾP HẠNG TOP 5 ỨNG VIÊN QUẢ BÓNG VÀNG",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }

                            items(ceremony.ballonDorTopCandidates) { candidate ->
                                BallonDorCandidateCard(candidate = candidate)
                            }
                        }
                    }

                    // Nút tiếp tục
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkSurfaceVariant,
                        tonalElevation = 6.dp
                    ) {
                        Button(
                            onClick = onAcceptAndContinue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("LƯU VÀO TỦ DANH HIỆU & SANG MÙA GIẢI MỚI", fontSize = 12.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChampionBanner(ceremony: SeasonAwardsCeremony) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (ceremony.isUserChampion) PitchGreen.copy(alpha = 0.2f) else DarkSurfaceVariant,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, if (ceremony.isUserChampion) PitchGreen else DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🏆 CÚP VÔ ĐỊCH ${ceremony.leagueName.uppercase()} 🏆", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = ceremony.leagueChampionClubName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = if (ceremony.isUserChampion) PitchGreen else TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (ceremony.isUserChampion) "VÔ ĐỊCH XỨNG ĐÁNG! Bạn và câu lạc bộ đã bước lên đỉnh vinh quang!" else "Đội bóng tân vương của giải đấu mùa này.",
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AwardWinnerCard(award: SeasonAwardWinner) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (award.isUser) GoldPrimary.copy(alpha = 0.12f) else DarkSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (award.isUser) GoldPrimary else DarkCardBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                color = if (award.isUser) GoldPrimary else DarkSurface,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(award.iconEmoji, fontSize = 22.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = award.awardNameVi,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (award.isUser) GoldLight else TextMuted
                    )
                    if (award.isUser) {
                        Surface(
                            color = PitchGreen,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "BẠN ĐẠT GIẢI",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = DarkBg
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${award.winnerName} (${award.clubName})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = award.statsText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PitchGreen
                )

                Text(
                    text = award.descriptionVi,
                    fontSize = 10.sp,
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun BallonDorHeader(winner: BallonDorCandidate, year: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = GoldDark.copy(alpha = 0.3f),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🟡 BALLON D'OR $year 🟡", fontSize = 14.sp, fontWeight = FontWeight.Black, color = GoldLight)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = winner.playerName.uppercase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = GoldPrimary
            )
            Text(
                text = "${winner.clubName} • ${winner.nationality}",
                fontSize = 12.sp,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = winner.statsSummary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = PitchGreen
            )
        }
    }
}

@Composable
fun BallonDorCandidateCard(candidate: BallonDorCandidate) {
    val rankBadgeColor = when (candidate.rank) {
        1 -> GoldPrimary
        2 -> Color(0xFFE0E0E0)
        3 -> Color(0xFFCD7F32)
        else -> DarkCardBorder
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (candidate.isUser) GoldPrimary.copy(alpha = 0.15f) else DarkSurfaceVariant,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (candidate.isUser) GoldPrimary else DarkCardBorder)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                color = rankBadgeColor,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "#${candidate.rank}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (candidate.rank <= 3) DarkBg else TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = candidate.playerName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (candidate.isUser) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(color = GoldPrimary, shape = RoundedCornerShape(4.dp)) {
                            Text("BẠN", modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp), fontSize = 8.sp, fontWeight = FontWeight.Black, color = DarkBg)
                        }
                    }
                }
                Text(
                    text = "${candidate.clubName} • ${candidate.nationality} (OVR ${candidate.ovr})",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = candidate.statsSummary,
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
