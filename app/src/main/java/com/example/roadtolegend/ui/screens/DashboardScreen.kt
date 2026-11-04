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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EmojiEvents
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

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.example.ui.theme.WarningAmber
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.roadtolegend.ui.components.AvatarSelectionDialog

@Composable
fun DashboardScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    var showAvatarDialog by remember { mutableStateOf(false) }

    if (showAvatarDialog) {
        AvatarSelectionDialog(
            playerId = careerState.player.id,
            currentAvatarPath = careerState.player.customAvatarPath,
            onAvatarSelected = { newPath ->
                viewModel.updatePlayerAvatar(newPath)
            },
            onDismiss = { showAvatarDialog = false }
        )
    }

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

        // Banner Cảnh báo có lời đề nghị chuyển nhượng đang chờ
        val pendingOffersCount = careerState.incomingOffers.count { it.isPendingPlayerAction }
        if (pendingOffersCount > 0) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = GoldPrimary.copy(alpha = 0.20f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📬 BẠN CÓ $pendingOffersCount ĐỀ NGHỊ CHUYỂN NHƯỢNG MỚI!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldLight
                            )
                            Text(
                                text = "Các CLB đang chờ câu trả lời đàm phán hợp đồng từ bạn.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Button(
                            onClick = { viewModel.selectTab(MainTab.TRANSFER) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("XEM NGAY", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Cảnh báo Cửa sổ chuyển nhượng / Deadline Day
        item {
            val isWindowOpen = careerState.isTransferWindowOpen
            val isDeadline = careerState.isDeadlineDay
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = if (isDeadline) GoalCoral.copy(alpha = 0.15f) else if (isWindowOpen) PitchGreen.copy(alpha = 0.12f) else DarkSurfaceVariant,
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when {
                            isDeadline -> "🔥 DEADLINE: Hôm nay chốt sổ chuyển nhượng!"
                            isWindowOpen -> "⚡ Cửa sổ chuyển nhượng đang mở"
                            careerState.currentWeek < 12 -> "⏳ Cửa sổ đông mở Tuần 12 (còn ${12 - careerState.currentWeek} tuần)"
                            else -> "⏳ Cửa sổ mở Tuần 24 (còn ${24 - careerState.currentWeek} tuần)"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDeadline) GoalCoral else if (isWindowOpen) PitchGreen else TextSecondary,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Surface(
                        color = GoldPrimary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.clickable { viewModel.selectTab(MainTab.TRANSFER) }
                    ) {
                        Text(
                            text = "Chi tiết ➔",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
                club = careerState.currentClub,
                onAvatarClick = { showAvatarDialog = true }
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

        // 5. Lối tắt Quản lý sự nghiệp (Career Management Hub)
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LỐI TẮT TRUNG TÂM SỰ NGHIỆP",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldLight,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Xem tất cả",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GoldPrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { viewModel.selectTab(MainTab.MORE) }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val unreadNewsCount = careerState.newsFeed.count { !it.isRead }
                val contractWeeksLeft = careerState.currentContract.totalWeeksRemaining
                val isExpiring = careerState.currentContract.isExpiringSoon

                // Hàng 1: Hợp đồng & Chuyển nhượng
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareerHubShortcutCard(
                        modifier = Modifier.weight(1f),
                        title = "Hợp đồng",
                        subtitle = "Lương & Điều khoản",
                        badgeText = if (isExpiring) "Hết hạn ${contractWeeksLeft}w" else "Còn ${contractWeeksLeft} tuần",
                        badgeColor = if (isExpiring) GoalCoral else PitchGreen,
                        icon = Icons.Default.Assignment,
                        iconTint = PitchGreen,
                        onClick = { viewModel.selectTab(MainTab.CONTRACT) }
                    )

                    CareerHubShortcutCard(
                        modifier = Modifier.weight(1f),
                        title = "Chuyển nhượng",
                        subtitle = "Thị trường & Đề nghị",
                        badgeText = when {
                            pendingOffersCount > 0 -> "$pendingOffersCount đề nghị"
                            careerState.isDeadlineDay -> "Deadline Day"
                            careerState.isTransferWindowOpen -> "Cửa sổ mở"
                            else -> "Đang đóng"
                        },
                        badgeColor = when {
                            pendingOffersCount > 0 -> GoalCoral
                            careerState.isDeadlineDay -> GoldPrimary
                            careerState.isTransferWindowOpen -> PitchGreen
                            else -> TextMuted
                        },
                        icon = Icons.Default.SwapHoriz,
                        iconTint = Color(0xFF42A5F5),
                        onClick = { viewModel.selectTab(MainTab.TRANSFER) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hàng 2: Người đại diện & Tin tức 24h
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareerHubShortcutCard(
                        modifier = Modifier.weight(1f),
                        title = "Người đại diện",
                        subtitle = careerState.agent.name,
                        badgeText = careerState.agent.tier.titleVi.take(10),
                        badgeColor = Color(0xFFFFB74D),
                        icon = Icons.Default.BusinessCenter,
                        iconTint = Color(0xFFFFB74D),
                        onClick = { viewModel.selectTab(MainTab.AGENT) }
                    )

                    CareerHubShortcutCard(
                        modifier = Modifier.weight(1f),
                        title = "Tin tức & Báo chí",
                        subtitle = "Tin đồn & Truyền thông",
                        badgeText = if (unreadNewsCount > 0) "$unreadNewsCount tin mới" else "Cập nhật",
                        badgeColor = if (unreadNewsCount > 0) GoalCoral else TextMuted,
                        icon = Icons.Default.Campaign,
                        iconTint = Color(0xFFFF7043),
                        onClick = { viewModel.selectTab(MainTab.NEWS) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hàng 3: Tài chính & Kế hoạch tập luyện
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareerHubShortcutCard(
                        modifier = Modifier.weight(1f),
                        title = "Tài chính",
                        subtitle = "Ví & Thu chi",
                        badgeText = "€${String.format("%,d", careerState.player.balance)}",
                        badgeColor = GoldPrimary,
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = GoldPrimary,
                        onClick = { viewModel.selectTab(MainTab.FINANCE) }
                    )

                    CareerHubShortcutCard(
                        modifier = Modifier.weight(1f),
                        title = "Tập luyện",
                        subtitle = "Chỉ số & Thể lực",
                        badgeText = "Tập ${careerState.selectedTrainingFocus.targetStatName}",
                        badgeColor = Color(0xFFAB47BC),
                        icon = Icons.Default.SportsSoccer,
                        iconTint = Color(0xFFAB47BC),
                        onClick = { viewModel.selectTab(MainTab.TRAINING) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hàng 4: Tủ danh hiệu & Sự nghiệp
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CareerHubShortcutCard(
                        modifier = Modifier.weight(1f),
                        title = "Tủ danh hiệu",
                        subtitle = "Cúp & Huân chương",
                        badgeText = "${careerState.trophiesCabinet.size} cúp",
                        badgeColor = GoldLight,
                        icon = Icons.Default.EmojiEvents,
                        iconTint = GoldLight,
                        onClick = { viewModel.selectTab(MainTab.TROPHY_CABINET) }
                    )

                    CareerHubShortcutCard(
                        modifier = Modifier.weight(1f),
                        title = "Sự nghiệp",
                        subtitle = "Cột mốc & Lịch sử",
                        badgeText = "${careerState.player.careerStats.matchesPlayed} trận",
                        badgeColor = Color(0xFF64B5F6),
                        icon = Icons.Default.Assessment,
                        iconTint = Color(0xFF64B5F6),
                        onClick = { viewModel.selectTab(MainTab.CAREER_STATS) }
                    )
                }
            }
        }

        // 6. Bảng điều khiển Debug mở rộng
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("BẢNG ĐIỀU KHIỂN DEBUG (MÔ PHỎNG NHIỀU MÙA)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tua nhanh sự nghiệp để kiểm tra tính nhất quán chuyển nhượng, đội hình và ngân sách thế giới AI.",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.debugFastForwardSeasons(1) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = TextPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+1 Mùa", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.debugFastForwardSeasons(5) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = TextPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+5 Mùa", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.debugFastForwardSeasons(20) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+20 Mùa", fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Khoảng trống dưới cùng
        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
private fun CareerHubShortcutCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconTint.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = badgeColor.copy(alpha = 0.16f),
                    border = BorderStroke(0.8.dp, badgeColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
