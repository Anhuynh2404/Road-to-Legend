package com.example.roadtolegend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MilitaryTech
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.ui.components.ClubBadge
import com.example.roadtolegend.ui.viewmodel.CareerViewModel
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoalCoral
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun CareerStatsScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Lịch sử mùa giải, 1: Cột mốc đáng nhớ
    val player = careerState.player
    val isFictional = careerState.isFictionalMode
    val seasonRecords = player.seasonRecords
    val milestones = player.milestones
    val stats = player.careerStats

    // Dòng tổng cộng tích luỹ: nếu có seasonRecords thì tổng từ seasonRecords + mùa hiện tại nếu chưa kết thúc,
    // hoặc tổng từ careerStats (luôn lưu giá trị chuẩn của cầu thủ).
    val totalMatches = stats.matchesPlayed
    val totalStarts = stats.starts
    val totalSubs = stats.subs
    val totalGoals = stats.goals
    val totalAssists = stats.assists
    val totalYellow = stats.yellowCards
    val totalRed = stats.redCards
    val avgRating = stats.averageRating
    val trophiesCount = stats.trophies.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Thẻ tóm tắt sự nghiệp đỉnh cao
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
                        Column {
                            Text(
                                text = "HỒ SƠ SỰ NGHIỆP",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = TextMuted,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = player.name.uppercase(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary
                            )
                        }

                        Surface(
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClubBadge(club = careerState.currentClub, size = 18.dp, isFictionalMode = isFictional)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = careerState.currentClub.getDisplayShortName(isFictional),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBg, RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        SummaryPill("TRẬN", "$totalMatches", PitchGreen)
                        SummaryPill("BÀN THẮNG", "$totalGoals", GoldPrimary)
                        SummaryPill("KIẾN TẠO", "$totalAssists", Color(0xFF64B5F6))
                        SummaryPill("ĐIỂM TB", "$avgRating", PitchGreen)
                        SummaryPill("DANH HIỆU", "$trophiesCount", WarningAmber)
                    }
                }
            }
        }

        // 2. Tab Navigation con: Lịch sử mùa giải & Cột mốc
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = GoldPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BẢNG TỪNG MÙA", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("CỘT MỐC (${milestones.size})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                )
            }
        }

        // ==========================================
        // TAB 0: BẢNG LỊCH SỬ THI ĐẤU QUA TỪNG MÙA
        // ==========================================
        if (selectedTab == 0) {
            item {
                Text(
                    text = "BẢNG THỐNG KÊ CHI TIẾT TỪNG MÙA GIẢI",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            }

            // Bảng có thanh cuộn ngang hỗ trợ màn hình nhỏ
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurface,
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Column {
                                // Tiêu đề các cột trong bảng
                                Row(
                                    modifier = Modifier
                                        .background(DarkSurfaceVariant, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("MÙA / NĂM", modifier = Modifier.width(80.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text("CÂU LẠC BỘ", modifier = Modifier.width(130.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text("GIẢI ĐẤU", modifier = Modifier.width(110.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text("TRẬN", modifier = Modifier.width(42.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text("Đ.CHÍNH", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text("DỰ BỊ", modifier = Modifier.width(44.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text("BÀN", modifier = Modifier.width(42.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                                    Text("KIẾN TẠO", modifier = Modifier.width(54.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
                                    Text("ĐIỂM TB", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text("THẺ V/Đ", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text("DANH HIỆU", modifier = Modifier.width(120.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                }

                                // Dòng dữ liệu mùa giải hiện tại đang diễn ra
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Mùa ${careerState.seasonNumber} (HT)", modifier = Modifier.width(80.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    Text(careerState.currentClub.getDisplayShortName(isFictional), modifier = Modifier.width(130.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text(careerState.currentLeague.name, modifier = Modifier.width(110.dp), fontSize = 10.sp, color = TextSecondary)
                                    Text("${stats.matchesPlayed}", modifier = Modifier.width(42.dp), textAlign = TextAlign.Center, fontSize = 11.sp, color = TextPrimary)
                                    Text("${stats.starts}", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, color = TextSecondary)
                                    Text("${stats.subs}", modifier = Modifier.width(44.dp), textAlign = TextAlign.Center, fontSize = 10.sp, color = TextSecondary)
                                    Text("${stats.goals}", modifier = Modifier.width(42.dp), textAlign = TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    Text("${stats.assists}", modifier = Modifier.width(54.dp), textAlign = TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
                                    Text("${stats.averageRating}", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, color = PitchGreen)
                                    Text("${stats.yellowCards}/${stats.redCards}", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, color = WarningAmber)
                                    Text(if (stats.trophies.isNotEmpty()) stats.trophies.joinToString(", ") else "-", modifier = Modifier.width(120.dp), fontSize = 10.sp, color = GoldPrimary)
                                }

                                // Các mùa giải đã qua trong quá khứ
                                seasonRecords.forEach { record ->
                                    Row(
                                        modifier = Modifier
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Mùa ${record.seasonNumber} (${record.year})", modifier = Modifier.width(80.dp), fontSize = 10.sp, color = TextSecondary)
                                        Text(record.clubShortName.ifBlank { record.clubName }, modifier = Modifier.width(130.dp), fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                        Text(record.leagueName, modifier = Modifier.width(110.dp), fontSize = 10.sp, color = TextMuted)
                                        Text("${record.matchesPlayed}", modifier = Modifier.width(42.dp), textAlign = TextAlign.Center, fontSize = 11.sp, color = TextPrimary)
                                        Text("${record.starts}", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, color = TextSecondary)
                                        Text("${record.subs}", modifier = Modifier.width(44.dp), textAlign = TextAlign.Center, fontSize = 10.sp, color = TextSecondary)
                                        Text("${record.goals}", modifier = Modifier.width(42.dp), textAlign = TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                        Text("${record.assists}", modifier = Modifier.width(54.dp), textAlign = TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
                                        Text("${record.averageRating}", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, color = PitchGreen)
                                        Text("${record.yellowCards}/${record.redCards}", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, color = WarningAmber)
                                        Text(record.trophiesWon.ifBlank { "-" }, modifier = Modifier.width(120.dp), fontSize = 10.sp, color = GoldPrimary)
                                    }
                                }

                                // DÒNG TỔNG CỘNG SỰ NGHIỆP (CAREER TOTALS)
                                Row(
                                    modifier = Modifier
                                        .background(GoldPrimary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("TỔNG CỘNG", modifier = Modifier.width(80.dp), fontSize = 10.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                                    Text("${seasonRecords.size + 1} Mùa", modifier = Modifier.width(130.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    Text("Toàn sự nghiệp", modifier = Modifier.width(110.dp), fontSize = 10.sp, color = TextSecondary)
                                    Text("$totalMatches", modifier = Modifier.width(42.dp), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                                    Text("$totalStarts", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("$totalSubs", modifier = Modifier.width(44.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("$totalGoals", modifier = Modifier.width(42.dp), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                                    Text("$totalAssists", modifier = Modifier.width(54.dp), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Black, color = PitchGreen)
                                    Text("$avgRating", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 11.sp, fontWeight = FontWeight.Black, color = PitchGreen)
                                    Text("$totalYellow/$totalRed", modifier = Modifier.width(52.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WarningAmber)
                                    Text("$trophiesCount Cúp", modifier = Modifier.width(120.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==========================================
        // TAB 1: CỘT MỐC ĐÁNG NHỚ
        // ==========================================
        if (selectedTab == 1) {
            if (milestones.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        color = DarkSurface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Chưa có cột mốc đặc biệt nào được ghi nhận.",
                                textAlign = TextAlign.Center,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "Hãy thi đấu xuất sắc, ghi bàn và giành các danh hiệu để lưu vào trang sử!",
                                textAlign = TextAlign.Center,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(milestones) { milestone ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(milestone.iconEmoji, fontSize = 26.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = milestone.titleVi,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = milestone.descriptionVi,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = milestone.dateAchievedText,
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun SummaryPill(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = valueColor)
    }
}
