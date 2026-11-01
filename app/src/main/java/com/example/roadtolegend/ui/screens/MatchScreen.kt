package com.example.roadtolegend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.GoalRecord
import com.example.roadtolegend.model.Match
import com.example.roadtolegend.model.MatchEvent
import com.example.roadtolegend.model.MatchEventType
import com.example.roadtolegend.model.MatchPerformance
import com.example.roadtolegend.model.RoundFixture
import com.example.roadtolegend.ui.components.ClubBadge
import com.example.roadtolegend.ui.components.InteractiveMomentDialog
import com.example.roadtolegend.ui.viewmodel.CareerViewModel
import com.example.roadtolegend.ui.viewmodel.SimulationSpeed
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
fun MatchScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    currentMatch: Match?,
    displayedMinute: Int,
    visibleEvents: List<MatchEvent>,
    isSimulating: Boolean,
    modifier: Modifier = Modifier
) {
    val displayedHomeScore by viewModel.displayedHomeScore.collectAsState()
    val displayedAwayScore by viewModel.displayedAwayScore.collectAsState()
    val simulationSpeed by viewModel.simulationSpeed.collectAsState()
    val interactiveMoment by viewModel.currentInteractiveMoment.collectAsState()
    val roundFixtures by viewModel.roundFixtures.collectAsState()

    val isFictional = careerState.isFictionalMode

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. BẢNG TỶ SỐ TRỰC TIẾP (LIVE SCOREBOARD VỚI LOGO CLB)
            item {
                val homeClub = currentMatch?.homeClub ?: careerState.currentClub
                val awayClub = currentMatch?.awayClub ?: careerState.nextOpponent

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, GoldDark.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(DarkSurfaceVariant, DarkSurface)
                                )
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Vòng đấu & Trạng thái phút
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${careerState.currentLeague.name} • Vòng ${careerState.currentWeek}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                            Surface(
                                color = if (displayedMinute in 1..90) GoalCoral else GoldPrimary,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (displayedMinute == 0) "CHƯA ĐẤU" else if (displayedMinute >= 91) "HẾT GIỜ" else "$displayedMinute'",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkBg
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Logo, Tên CLB và Tỷ số thực tế
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Đội nhà
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                ClubBadge(club = homeClub, size = 52.dp, isFictionalMode = isFictional)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = homeClub.getDisplayShortName(isFictional),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text("Đội nhà", fontSize = 10.sp, color = TextSecondary)
                            }

                            // Tỉ số trực tiếp (Chỉ tăng khi có bàn thắng trong timeline)
                            Text(
                                text = "$displayedHomeScore - $displayedAwayScore",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary,
                                letterSpacing = 2.sp
                            )

                            // Đội khách
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                ClubBadge(club = awayClub, size = 52.dp, isFictionalMode = isFictional)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = awayClub.getDisplayShortName(isFictional),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = TextPrimary
                                )
                                Text("Đội khách", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Điều khiển tốc độ phát (Chậm / Vừa / Nhanh)
                        if (isSimulating) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Tốc độ phát: ", fontSize = 11.sp, color = TextMuted)
                                SimulationSpeed.values().forEach { speed ->
                                    val isSelected = simulationSpeed == speed
                                    Surface(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { viewModel.setSimulationSpeed(speed) },
                                        color = if (isSelected) GoldPrimary else DarkSurfaceVariant
                                    ) {
                                        Text(
                                            text = speed.titleVi,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) DarkBg else TextPrimary
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Điều khiển trận đấu (Bắt đầu / Bỏ qua / Sang tuần)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (currentMatch == null || !currentMatch.isCompleted) {
                                Button(
                                    onClick = { viewModel.startMatchdaySimulation() },
                                    enabled = !isSimulating,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isSimulating) "ĐANG THI ĐẤU..." else "BẮT ĐẦU TRẬN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                OutlinedButton(
                                    onClick = { viewModel.skipMatchSimulation() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.horizontalGradient(listOf(DarkCardBorder, GoldPrimary)))
                                ) {
                                    Icon(Icons.Default.FastForward, contentDescription = "Skip", tint = GoldLight, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("BỎ QUA / KẾT QUẢ", color = GoldLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.advanceToNextWeek() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = PitchGreen, contentColor = DarkBg),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("KẾT THÚC TRẬN ĐẤU & SANG TUẦN MỚI", fontWeight = FontWeight.Black, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }

            // 2. MÀN HÌNH TỔNG KẾT SAU TRẬN ĐẤU (KHI ĐÃ KẾT THÚC)
            if (currentMatch != null && currentMatch.isCompleted) {
                item {
                    PostMatchDetailedSummary(
                        match = currentMatch,
                        playerName = careerState.player.name,
                        isFictional = isFictional
                    )
                }
            }

            // 3. DIỄN BIẾN TRẬN ĐẤU CHI TIẾT (LIVE COMMENTARY)
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.SportsSoccer, contentDescription = "Live", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "TƯỜNG THUẬT TRẬN ĐẤU THEO THỜI GIAN",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            if (visibleEvents.isEmpty()) {
                item {
                    Text(
                        text = "Nhấn 'BẮT ĐẦU TRẬN' hoặc 'BỎ QUA' để theo dõi các tình huống bóng đá trực tiếp.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(visibleEvents) { event ->
                    MatchEventCommentaryItem(event = event, userName = careerState.player.name)
                }
            }

            // 4. MỤC "CÁC TRẬN KHÁC TRONG VÒNG" (SECTION B)
            if (roundFixtures.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "CÁC TRẬN KHÁC TRONG VÒNG ${careerState.currentWeek}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                }

                items(roundFixtures.filter { !it.isUserMatch }) { fixture ->
                    RoundFixtureCard(fixture = fixture, isFictional = isFictional)
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        // Tình huống tương tác (Không lộ %)
        interactiveMoment?.let { moment ->
            InteractiveMomentDialog(
                moment = moment,
                onChoiceSelected = { choiceType ->
                    viewModel.submitInteractiveChoice(choiceType)
                }
            )
        }
    }
}

@Composable
fun MatchEventCommentaryItem(event: MatchEvent, userName: String) {
    val isUser = event.isUserInvolved || (event.playerName != null && event.playerName.contains(userName))

    val badgeColor = when (event.type) {
        MatchEventType.GOAL -> GoalCoral
        MatchEventType.YELLOW_CARD -> Color(0xFFFFD54F)
        MatchEventType.RED_CARD -> GoalCoral
        MatchEventType.CORNER_KICK, MatchEventType.SHOT_SAVED -> PitchGreen
        MatchEventType.INTERACTIVE_DECISION -> GoldPrimary
        else -> DarkCardBorder
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(
                width = if (isUser) 1.dp else 0.dp,
                color = if (isUser) GoldPrimary else DarkCardBorder,
                shape = RoundedCornerShape(8.dp)
            ),
        color = if (isUser) DarkSurfaceVariant else DarkSurface
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                color = badgeColor.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "${event.type.icon} ${event.minute}'",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isUser) GoldPrimary else TextSecondary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = event.description,
                fontSize = 12.sp,
                color = if (isUser) TextPrimary else TextSecondary,
                fontWeight = if (isUser) FontWeight.SemiBold else FontWeight.Normal,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun RoundFixtureCard(fixture: RoundFixture, isFictional: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    ClubBadge(club = fixture.homeClub, size = 26.dp, isFictionalMode = isFictional)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = fixture.homeClub.getDisplayShortName(isFictional),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (fixture.isSimulated && fixture.homeScore != null) "${fixture.homeScore} - ${fixture.awayScore}" else "vs",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (fixture.isSimulated) GoldPrimary else TextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = fixture.awayClub.getDisplayShortName(isFictional),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    ClubBadge(club = fixture.awayClub, size = 26.dp, isFictionalMode = isFictional)
                }
            }

            // Danh sách người ghi bàn nếu trận đã đấu
            if (fixture.goals.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                val scorersText = fixture.goals.joinToString(" • ") { "${it.minute}' ${it.scorerName}" }
                Text(
                    text = "Ghi bàn: $scorersText",
                    fontSize = 10.sp,
                    color = TextMuted,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

@Composable
fun PostMatchDetailedSummary(match: Match, playerName: String, isFictional: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("TỔNG KẾT & THỐNG KÊ CHI TIẾT TRẬN ĐẤU", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
            Spacer(modifier = Modifier.height(10.dp))

            // Danh sách người ghi bàn của trận
            if (match.goalsList.isNotEmpty()) {
                Text("CẦU THỦ GHI BÀN:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                match.goalsList.forEach { goal ->
                    Row(
                        modifier = Modifier.padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚽ ${goal.minute}'", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoalCoral)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${goal.scorerName} (${goal.clubShortName})${if (goal.assistName != null) " - Kiến tạo: ${goal.assistName}" else ""}",
                            fontSize = 11.sp,
                            color = if (goal.isUserScorer || goal.isUserAssist) GoldPrimary else TextPrimary,
                            fontWeight = if (goal.isUserScorer) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Thống kê hai đội
            Text("THỐNG KÊ HAI ĐỘI:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            StatCompareRow("Kiểm soát bóng", "${match.homeStats.possessionPercent}%", "${match.awayStats.possessionPercent}%")
            StatCompareRow("Tổng số cú sút", match.homeStats.shotsTotal.toString(), match.awayStats.shotsTotal.toString())
            StatCompareRow("Sút trúng đích", match.homeStats.shotsOnTarget.toString(), match.awayStats.shotsOnTarget.toString())
            StatCompareRow("Phạt góc", match.homeStats.corners.toString(), match.awayStats.corners.toString())
            StatCompareRow("Phạm lỗi", match.homeStats.fouls.toString(), match.awayStats.fouls.toString())

            Spacer(modifier = Modifier.height(14.dp))

            // Đóng góp cá nhân của người chơi
            match.performance?.let { perf ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ĐÁNH GIÁ: $playerName (${perf.userRole.titleVi})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("ĐIỂM: ${perf.rating}", fontSize = 14.sp, fontWeight = FontWeight.Black, color = if (perf.rating >= 7.5) GoldPrimary else PitchGreen)
                        }

                        if (perf.isManOfTheMatch) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = "MOM", tint = GoldPrimary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CẦU THỦ XUẤT SẮC NHẤT TRẬN ĐẤU (MOM)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Bàn thắng: ${perf.goals}", fontSize = 11.sp, color = TextSecondary)
                            Text("Kiến tạo: ${perf.assists}", fontSize = 11.sp, color = TextSecondary)
                            Text("Sút: ${perf.shotsTotal}", fontSize = 11.sp, color = TextSecondary)
                            Text("Tắc bóng: ${perf.tacklesWon}", fontSize = 11.sp, color = TextSecondary)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Lương & Thưởng kiếm được: +€${String.format("%,d", perf.wageEarned + perf.bonusEarned)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PitchGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCompareRow(label: String, homeVal: String, awayVal: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(homeVal, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(label, fontSize = 10.sp, color = TextMuted)
        Text(awayVal, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
