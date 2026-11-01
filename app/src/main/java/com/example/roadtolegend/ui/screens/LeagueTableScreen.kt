package com.example.roadtolegend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.data.SquadDatabase
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.ClubPlayer
import com.example.roadtolegend.model.PlayerStatLeader
import com.example.roadtolegend.model.PositionCategory
import com.example.roadtolegend.ui.components.ClubBadge
import com.example.roadtolegend.ui.components.PlayerAvatar
import com.example.roadtolegend.ui.components.PlayerDetailDialog
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun LeagueTableScreen(
    careerState: CareerState,
    modifier: Modifier = Modifier
) {
    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: Bảng Xếp Hạng, 1: Đội Hình CLB, 2: Thống Kê Giải Đấu
    var selectedLeagueIndex by remember { mutableIntStateOf(0) } // 0: League chính, 1: Cúp Quốc Gia
    var selectedClubId by remember { mutableStateOf(careerState.currentClub.id) }
    var selectedPositionCategory by remember { mutableStateOf<PositionCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var inspectingPlayer by remember { mutableStateOf<ClubPlayer?>(null) }

    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.GERMANY) }
    val isFictional = careerState.isFictionalMode

    val leaguesList = remember(careerState.currentLeague.id) {
        val allOther = DefaultDatabase.getAllLeagues().filter { it.id != careerState.currentLeague.id }
        listOf(careerState.currentLeague) + allOther
    }
    val activeLeague = leaguesList.getOrElse(selectedLeagueIndex) { careerState.currentLeague }
    val sortedStandings = activeLeague.sortedStandings()

    // Đảm bảo selectedClubId luôn thuộc giải đấu đang chọn
    val availableClubs = activeLeague.clubs
    val currentSelectedClub = availableClubs.find { it.id == selectedClubId } ?: availableClubs.firstOrNull() ?: careerState.currentClub

    // Lấy danh sách cầu thủ cho CLB đang chọn
    val currentSquad = remember(currentSelectedClub.id, careerState.player, careerState.currentClub.id) {
        SquadDatabase.getSquadForClub(
            clubId = currentSelectedClub.id,
            userPlayer = careerState.player,
            userClubId = careerState.currentClub.id,
            userWeeklyWage = careerState.currentContract.weeklyWage
        )
    }

    val filteredSquad = remember(currentSquad, selectedPositionCategory, searchQuery) {
        currentSquad.filter { player ->
            val matchesCategory = selectedPositionCategory == null || player.positionCategory == selectedPositionCategory
            val matchesQuery = searchQuery.isBlank() ||
                    player.name.contains(searchQuery.trim(), ignoreCase = true) ||
                    player.position.code.contains(searchQuery.trim(), ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    val allScorers = remember(careerState.player.careerStats.goals, activeLeague.topScorers) {
        val list = activeLeague.topScorers.toMutableList()
        if (list.none { it.playerName == careerState.player.name }) {
            list.add(
                PlayerStatLeader(
                    playerId = careerState.player.id,
                    playerName = careerState.player.name,
                    clubId = careerState.currentClub.id,
                    clubShortName = careerState.currentClub.shortName,
                    count = careerState.player.careerStats.goals,
                    matchesPlayed = careerState.player.careerStats.matchesPlayed,
                    isUser = true
                )
            )
        }
        list.sortByDescending { it.count }
        list
    }

    val allAssists = remember(careerState.player.careerStats.assists, activeLeague.topAssists) {
        val list = activeLeague.topAssists.toMutableList()
        if (list.none { it.playerName == careerState.player.name }) {
            list.add(
                PlayerStatLeader(
                    playerId = careerState.player.id,
                    playerName = careerState.player.name,
                    clubId = careerState.currentClub.id,
                    clubShortName = careerState.currentClub.shortName,
                    count = careerState.player.careerStats.assists,
                    matchesPlayed = careerState.player.careerStats.matchesPlayed,
                    isUser = true
                )
            )
        }
        list.sortByDescending { it.count }
        list
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Tab chuyển đổi chính: 3 TAB
            item {
                TabRow(
                    selectedTabIndex = selectedMainTab,
                    containerColor = DarkSurface,
                    contentColor = GoldPrimary
                ) {
                    Tab(
                        selected = selectedMainTab == 0,
                        onClick = { selectedMainTab = 0 },
                        text = { Text("BẢNG XẾP HẠNG", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                    )
                    Tab(
                        selected = selectedMainTab == 1,
                        onClick = { selectedMainTab = 1 },
                        text = { Text("ĐỘI HÌNH CLB", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                    )
                    Tab(
                        selected = selectedMainTab == 2,
                        onClick = { selectedMainTab = 2 },
                        text = { Text("THỐNG KÊ", fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                    )
                }
            }

            // Thanh chọn giải đấu (V-League 1 / V-League 2 / Cúp Quốc Gia...)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    leaguesList.forEachIndexed { idx, league ->
                        val isSelected = selectedLeagueIndex == idx
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    selectedLeagueIndex = idx
                                    selectedClubId = league.clubs.firstOrNull()?.id ?: careerState.currentClub.id
                                }
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) GoldPrimary else DarkCardBorder,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            color = if (isSelected) DarkSurfaceVariant else DarkSurface
                        ) {
                            Text(
                                text = league.name,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                                color = if (isSelected) GoldPrimary else TextSecondary
                            )
                        }
                    }
                }
            }

            // ==========================================
            // TAB 0: BẢNG XẾP HẠNG
            // ==========================================
            if (selectedMainTab == 0) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("#", modifier = Modifier.width(22.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("CLB (Chạm xem đội hình)", modifier = Modifier.weight(1f), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("TR", modifier = Modifier.width(26.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("T", modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("H", modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("B", modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("HS", modifier = Modifier.width(26.dp), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text("Đ", modifier = Modifier.width(28.dp), textAlign = TextAlign.End, fontSize = 10.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                        Text("5 TRẬN", modifier = Modifier.width(50.dp), textAlign = TextAlign.Center, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    }
                }

                itemsIndexed(sortedStandings) { index, team ->
                    val rank = index + 1
                    val isUserClub = team.clubId == careerState.currentClub.id
                    val isPromotionZone = rank <= 2
                    val isRelegationZone = rank >= (sortedStandings.size - 1)
                    val club = activeLeague.clubs.find { it.id == team.clubId }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (isUserClub) 1.5.dp else 0.dp,
                                color = if (isUserClub) GoldPrimary else DarkCardBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedClubId = team.clubId
                                selectedMainTab = 1 // Chuyển sang Tab Đội Hình CLB
                            },
                        color = if (isUserClub) DarkSurfaceVariant else DarkSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rank.toString(),
                                modifier = Modifier.width(22.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPromotionZone) PitchGreen else if (isRelegationZone) GoalCoral else TextSecondary
                            )

                            // Logo và Tên CLB
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                if (club != null) {
                                    ClubBadge(club = club, size = 20.dp, isFictionalMode = isFictional)
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = (club?.getDisplayShortName(isFictional) ?: team.clubName) + if (isUserClub) " ★" else "",
                                    fontSize = 11.sp,
                                    fontWeight = if (isUserClub) FontWeight.Black else FontWeight.SemiBold,
                                    color = if (isUserClub) GoldPrimary else TextPrimary,
                                    maxLines = 1
                                )
                            }

                            Text(team.played.toString(), modifier = Modifier.width(26.dp), textAlign = TextAlign.Center, fontSize = 11.sp, color = TextSecondary)
                            Text(team.won.toString(), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, fontSize = 11.sp, color = TextSecondary)
                            Text(team.drawn.toString(), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, fontSize = 11.sp, color = TextSecondary)
                            Text(team.lost.toString(), modifier = Modifier.width(20.dp), textAlign = TextAlign.Center, fontSize = 11.sp, color = TextSecondary)
                            Text(
                                text = (if (team.goalDifference > 0) "+${team.goalDifference}" else team.goalDifference.toString()),
                                modifier = Modifier.width(26.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                                color = if (team.goalDifference > 0) PitchGreen else TextMuted
                            )
                            Text(
                                text = team.points.toString(),
                                modifier = Modifier.width(28.dp),
                                textAlign = TextAlign.End,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isUserClub) GoldPrimary else TextPrimary
                            )

                            // 5 trận gần nhất (chấm màu W/D/L)
                            Row(
                                modifier = Modifier.width(50.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                team.recentForm.takeLast(5).forEach { result ->
                                    val dotColor = when (result) {
                                        "W" -> PitchGreen
                                        "D" -> WarningAmber
                                        else -> GoalCoral
                                    }
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 1.dp)
                                            .size(5.5.dp)
                                            .clip(CircleShape)
                                            .background(dotColor)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "💡 Chạm vào bất kỳ CLB nào để mở danh sách toàn bộ cầu thủ & chỉ số FIFA.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }

            // ==========================================
            // TAB 1: ĐỘI HÌNH CÁC CÂU LẠC BỘ (YÊU CẦU MỚI)
            // ==========================================
            if (selectedMainTab == 1) {
                // Thanh trượt ngang chọn Câu Lạc Bộ
                item {
                    Text("CHỌN CÂU LẠC BỘ:", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableClubs.forEach { club ->
                            val isSelected = club.id == currentSelectedClub.id
                            val isUserClub = club.id == careerState.currentClub.id

                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedClubId = club.id }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) GoldPrimary else if (isUserClub) PitchGreen.copy(alpha = 0.6f) else DarkCardBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                color = if (isSelected) DarkSurfaceVariant else DarkSurface
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    ClubBadge(club = club, size = 24.dp, isFictionalMode = isFictional)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = club.getDisplayShortName(isFictional) + if (isUserClub) " ★" else "",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                            color = if (isSelected) GoldPrimary else TextPrimary
                                        )
                                        Text(
                                            text = "OVR ${club.squadOvrAverage}",
                                            fontSize = 9.sp,
                                            color = if (isSelected) PitchGreen else TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Thẻ thông tin tổng quan CLB
                item {
                    val totalValue = currentSquad.sumOf { it.marketValue }
                    val avgOvr = if (currentSquad.isNotEmpty()) currentSquad.map { it.ovr }.average().toInt() else currentSelectedClub.squadOvrAverage

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp)),
                        color = DarkSurface
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ClubBadge(club = currentSelectedClub, size = 36.dp, isFictionalMode = isFictional)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = currentSelectedClub.getDisplayName(isFictional),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "${currentSelectedClub.country} • Hạng ${currentSelectedClub.tier}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Surface(
                                    color = DarkSurfaceVariant,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "${currentSquad.size} CẦU THỦ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GoldPrimary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkBg, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("OVR Trung bình", fontSize = 9.sp, color = TextMuted)
                                    Text("$avgOvr", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PitchGreen)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Giá trị đội hình", fontSize = 9.sp, color = TextMuted)
                                    Text("€ ${currencyFormatter.format(totalValue)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("HLV / Cơ sở", fontSize = 9.sp, color = TextMuted)
                                    Text("Lv.${currentSelectedClub.coachQuality} / Lv.${currentSelectedClub.trainingFacilityLevel}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                            }
                        }
                    }
                }

                // Thanh tìm kiếm và Bộ lọc Vị Trí
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Tìm theo tên hoặc vị trí (vd: Phượng, ST, CB...)", fontSize = 11.sp, color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm", tint = TextMuted, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkSurface,
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = DarkCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val categories = listOf(
                                null to "Tất cả (${currentSquad.size})",
                                PositionCategory.GOALKEEPER to "Thủ môn (GK)",
                                PositionCategory.DEFENDER to "Hậu vệ (DEF)",
                                PositionCategory.MIDFIELDER to "Tiền vệ (MID)",
                                PositionCategory.FORWARD to "Tiền đạo (FWD)"
                            )

                            categories.forEach { (cat, label) ->
                                val isSelected = selectedPositionCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedPositionCategory = cat },
                                    label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldPrimary,
                                        selectedLabelColor = DarkBg,
                                        containerColor = DarkSurface,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = DarkCardBorder,
                                        selectedBorderColor = GoldPrimary
                                    )
                                )
                            }
                        }
                    }
                }

                // Danh sách cầu thủ trong đội hình
                if (filteredSquad.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            color = DarkSurface,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Không tìm thấy cầu thủ nào khớp với bộ lọc.",
                                textAlign = TextAlign.Center,
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(filteredSquad) { player ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    width = if (player.isUser) 1.5.dp else 1.dp,
                                    color = if (player.isUser) GoldPrimary else DarkCardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { inspectingPlayer = player },
                            color = if (player.isUser) DarkSurfaceVariant else DarkSurface
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar cầu thủ (dynamic hoặc vector shield fallback)
                                PlayerAvatar(player = player, size = 46.dp)

                                Spacer(modifier = Modifier.width(10.dp))

                                // Thông tin cơ bản
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = player.name,
                                            fontSize = 12.sp,
                                            fontWeight = if (player.isUser) FontWeight.Black else FontWeight.Bold,
                                            color = if (player.isUser) GoldPrimary else TextPrimary,
                                            maxLines = 1
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = when (player.positionCategory) {
                                                PositionCategory.GOALKEEPER -> Color(0xFFFFB300)
                                                PositionCategory.DEFENDER -> Color(0xFF1E88E5)
                                                PositionCategory.MIDFIELDER -> PitchGreen
                                                PositionCategory.FORWARD -> GoalCoral
                                            },
                                            shape = RoundedCornerShape(3.dp)
                                        ) {
                                            Text(
                                                text = player.position.code,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(6.dp))

                                        Text(
                                            text = "#${player.number} • ${player.age}t • ${player.matchesPlayed} trận (${player.goals} bàn, ${player.assists} kiến tạo)",
                                            fontSize = 10.sp,
                                            fontWeight = if (player.isUser) FontWeight.Bold else FontWeight.Normal,
                                            color = if (player.isUser) GoldPrimary else TextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // 6 chỉ số mini FIFA
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        MiniStatPill("PAC", player.pac)
                                        MiniStatPill("SHO", player.sho)
                                        MiniStatPill("PAS", player.pas)
                                        MiniStatPill("DRI", player.dri)
                                        MiniStatPill("DEF", player.def)
                                        MiniStatPill("PHY", player.phy)
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // OVR Badge & Giá trị chuyển nhượng
                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        color = when {
                                            player.ovr >= 80 -> GoldPrimary
                                            player.ovr >= 70 -> PitchGreen
                                            player.ovr >= 60 -> DarkSurfaceVariant
                                            else -> DarkBg
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.border(
                                            1.dp,
                                            if (player.ovr >= 70) GoldPrimary else DarkCardBorder,
                                            RoundedCornerShape(6.dp)
                                        )
                                    ) {
                                        Text(
                                            text = "${player.ovr}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (player.ovr >= 80) DarkBg else TextPrimary,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "€ ${currencyFormatter.format(player.marketValue)}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GoldPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // TAB 2: THỐNG KÊ GIẢI ĐẤU
            // ==========================================
            if (selectedMainTab == 2) {
                val userScorerIndex = allScorers.indexOfFirst { it.playerName == careerState.player.name || it.isUser }
                val isUserInTopScorers = userScorerIndex in 0 until 8

                val userAssistIndex = allAssists.indexOfFirst { it.playerName == careerState.player.name || it.isUser }
                val isUserInTopAssists = userAssistIndex in 0 until 8

                item {
                    Text("VUA PHÁ LƯỚI (TOP GHI BÀN)", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                }

                itemsIndexed(allScorers.take(8)) { rankIdx, leader ->
                    val isUser = leader.playerName == careerState.player.name || leader.isUser
                    val club = activeLeague.clubs.find { it.id == leader.clubId } ?: careerState.currentClub

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (isUser) 1.5.dp else 0.dp,
                                color = if (isUser) GoldPrimary else DarkCardBorder,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        color = if (isUser) DarkSurfaceVariant else DarkSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${rankIdx + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rankIdx == 0) GoldPrimary else TextSecondary,
                                    modifier = Modifier.width(24.dp)
                                )
                                ClubBadge(club = club, size = 24.dp, isFictionalMode = isFictional)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = leader.playerName + if (isUser) " (Bạn)" else "",
                                        fontSize = 12.sp,
                                        fontWeight = if (isUser) FontWeight.Black else FontWeight.Bold,
                                        color = if (isUser) GoldPrimary else TextPrimary
                                    )
                                    Text(
                                        text = "${club.getDisplayShortName(isFictional)} • ${leader.matchesPlayed} trận",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Text(
                                text = "${leader.count} bàn",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isUser) GoldPrimary else TextPrimary
                            )
                        }
                    }
                }

                // Nếu người chơi không nằm trong top 8, luôn hiển thị vị trí của người chơi
                if (!isUserInTopScorers && userScorerIndex >= 0) {
                    val userScorer = allScorers[userScorerIndex]
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.5.dp, GoldPrimary, RoundedCornerShape(8.dp)),
                            color = DarkSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${userScorerIndex + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GoldPrimary,
                                        modifier = Modifier.width(32.dp)
                                    )
                                    ClubBadge(club = careerState.currentClub, size = 24.dp, isFictionalMode = isFictional)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${careerState.player.name} (Vị trí của bạn)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = GoldPrimary
                                        )
                                        Text(
                                            text = "${careerState.currentClub.getDisplayShortName(isFictional)} • ${userScorer.matchesPlayed} trận",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Text(
                                    text = "${userScorer.count} bàn",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("VUA KIẾN TẠO (TOP KIẾN TẠO)", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PitchGreen)
                }

                itemsIndexed(allAssists.take(8)) { rankIdx, leader ->
                    val isUser = leader.playerName == careerState.player.name || leader.isUser
                    val club = activeLeague.clubs.find { it.id == leader.clubId } ?: careerState.currentClub

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (isUser) 1.5.dp else 0.dp,
                                color = if (isUser) PitchGreen else DarkCardBorder,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        color = if (isUser) DarkSurfaceVariant else DarkSurface
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${rankIdx + 1}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rankIdx == 0) PitchGreen else TextSecondary,
                                    modifier = Modifier.width(24.dp)
                                )
                                ClubBadge(club = club, size = 24.dp, isFictionalMode = isFictional)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = leader.playerName + if (isUser) " (Bạn)" else "",
                                        fontSize = 12.sp,
                                        fontWeight = if (isUser) FontWeight.Black else FontWeight.Bold,
                                        color = if (isUser) PitchGreen else TextPrimary
                                    )
                                    Text(
                                        text = "${club.getDisplayShortName(isFictional)} • ${leader.matchesPlayed} trận",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Text(
                                text = "${leader.count} kiến tạo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isUser) PitchGreen else TextPrimary
                            )
                        }
                    }
                }

                // Nếu người chơi không nằm trong top 8 kiến tạo, luôn hiển thị vị trí của người chơi
                if (!isUserInTopAssists && userAssistIndex >= 0) {
                    val userAssist = allAssists[userAssistIndex]
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.5.dp, PitchGreen, RoundedCornerShape(8.dp)),
                            color = DarkSurfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${userAssistIndex + 1}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PitchGreen,
                                        modifier = Modifier.width(32.dp)
                                    )
                                    ClubBadge(club = careerState.currentClub, size = 24.dp, isFictionalMode = isFictional)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${careerState.player.name} (Vị trí của bạn)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = PitchGreen
                                        )
                                        Text(
                                            text = "${careerState.currentClub.getDisplayShortName(isFictional)} • ${userAssist.matchesPlayed} trận",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Text(
                                    text = "${userAssist.count} kiến tạo",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PitchGreen
                                )
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        // Hộp thoại xem chi tiết thẻ cầu thủ khi chạm vào
        inspectingPlayer?.let { player ->
            val playerClub = activeLeague.clubs.find { it.id == player.clubId } ?: currentSelectedClub
            PlayerDetailDialog(
                player = player,
                club = playerClub,
                onDismiss = { inspectingPlayer = null }
            )
        }
    }
}

@Composable
private fun MiniStatPill(label: String, value: Int) {
    val color = when {
        value >= 70 -> PitchGreen
        value >= 60 -> GoldPrimary
        else -> TextMuted
    }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 8.sp, color = TextMuted)
        Spacer(modifier = Modifier.width(1.dp))
        Text(text = "$value", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
