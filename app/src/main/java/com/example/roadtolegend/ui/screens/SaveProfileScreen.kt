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
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import com.example.roadtolegend.data.AvatarManager
import com.example.roadtolegend.ui.components.AvatarSelectionDialog
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.data.SaveSlotSummary
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.ui.components.ClubBadge
import com.example.roadtolegend.ui.viewmodel.CareerViewModel
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SaveProfileScreen(
    careerState: CareerState?,
    saveSlots: List<SaveSlotSummary>,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    var showAvatarDialog by remember { mutableStateOf(false) }

    if (showAvatarDialog && careerState != null) {
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
        // Thẻ thông tin đại diện cầu thủ & nút đổi ảnh
        if (careerState != null) {
            val player = careerState.player

            item {
                val customBitmap = remember(player.customAvatarPath) { AvatarManager.loadBitmapFromFile(player.customAvatarPath) }
                val preset = remember(player.customAvatarPath) { AvatarManager.getPresetById(player.customAvatarPath) }

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .clickable { showAvatarDialog = true }
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(GoldPrimary.copy(alpha = 0.4f), DarkSurfaceVariant)
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
                                            tint = androidx.compose.ui.graphics.Color.White,
                                            modifier = Modifier.size(34.dp)
                                        )
                                    }
                                }
                                else -> {
                                    Text("★", fontSize = 28.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = player.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Text(
                                text = "${player.age} tuổi • ${player.primaryPosition.fullNameVi} • OVR ${player.ovr}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { showAvatarDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = DarkBg, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Đổi ảnh đại diện", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkBg)
                                }
                            }
                        }
                    }
                }
            }
        }

        // 1. Thống kê tổng quan và Cột mốc sự nghiệp
        if (careerState != null) {
            val stats = careerState.player.careerStats

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
                            Text("TỔNG QUAN SỰ NGHIỆP", fontSize = 14.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                            Text("Điểm TB: ${stats.averageRating}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatMini("Trận đấu", stats.matchesPlayed.toString())
                            StatMini("Bàn thắng", stats.goals.toString())
                            StatMini("Kiến tạo", stats.assists.toString())
                            StatMini("MOM", stats.manOfTheMatchCount.toString())
                            StatMini("ĐHTB", stats.totwCount.toString())
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Giá trị thị trường cao nhất: €${String.format("%,d", careerState.player.peakMarketValue.coerceAtLeast(careerState.player.marketValue))}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }
                }
            }

            // 2. BẢNG LỊCH SỬ TỪNG MÙA GIẢI (SECTION F)
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = "History", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LỊCH SỬ THI ĐẤU QUA CÁC MÙA GIẢI", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }
            }

            if (careerState.player.seasonRecords.isEmpty()) {
                item {
                    Text("Bạn đang thi đấu mùa giải đầu tiên. Bảng tổng kết sẽ được lưu lại sau khi kết thúc mùa.", fontSize = 11.sp, color = TextMuted)
                }
            } else {
                items(careerState.player.seasonRecords) { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("MÙA ${record.seasonNumber} (${record.year})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                Text(record.leagueName, fontSize = 11.sp, color = TextMuted)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${record.clubName} • ${record.matchesPlayed} trận (${record.starts} đá chính, ${record.subs} dự bị)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Bàn thắng: ${record.goals} • Kiến tạo: ${record.assists}", fontSize = 11.sp, color = TextSecondary)
                                Text("Điểm TB: ${record.averageRating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
                            }
                            if (record.trophiesWon.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("🏆 ${record.trophiesWon}", fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 3. CỘT MỐC SỰ NGHIỆP (MILESTONES)
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = "Milestones", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("CỘT MỐC ĐÁNG NHỚ", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                }
            }

            if (careerState.player.milestones.isEmpty()) {
                item {
                    Text("Chưa có cột mốc đặc biệt nào được ghi nhận.", fontSize = 11.sp, color = TextMuted)
                }
            } else {
                items(careerState.player.milestones) { milestone ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(10.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(milestone.iconEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(milestone.titleVi, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                Text(milestone.descriptionVi, fontSize = 11.sp, color = TextSecondary)
                                Text(milestone.dateAchievedText, fontSize = 9.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }

            // 4. CÀI ĐẶT & CHẾ ĐỘ GIẢ LẬP
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TÙY CHỈNH & BẢN QUYỀN", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tùy chọn 1: Fictional Mode (Chế độ giả lập)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Chế độ Tên & Logo Giả lập", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Thay thế tên thật bằng tên hư cấu để tránh vấn đề bản quyền.", fontSize = 10.sp, color = TextMuted)
                            }
                            Switch(
                                checked = careerState.isFictionalMode,
                                onCheckedChange = { viewModel.toggleFictionalMode() },
                                colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = DarkSurfaceVariant)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tùy chọn 2: Interactive Moments Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Khoảnh khắc tương tác trong trận", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Tạm dừng khi có cơ hội quyết định để tự tay chọn phương án dứt điểm.", fontSize = 10.sp, color = TextMuted)
                            }
                            Switch(
                                checked = careerState.isInteractiveMomentsEnabled,
                                onCheckedChange = { viewModel.toggleInteractiveMoments() },
                                colors = SwitchDefaults.colors(checkedThumbColor = GoldPrimary, checkedTrackColor = DarkSurfaceVariant)
                            )
                        }
                    }
                }
            }
        }

        // 5. Quản lý lưu trữ (Save / Load Slots)
        item {
            Text("QUẢN LÝ CÁC VỊ TRÍ LƯU (SAVE SLOTS):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        saveSlots.forEach { slot ->
            item {
                SaveSlotRowCard(
                    slot = slot,
                    canSave = careerState != null,
                    onSaveClick = { viewModel.saveToSlot(slot.slotIndex) },
                    onLoadClick = { viewModel.loadFromSlot(slot.slotIndex) }
                )
            }
        }

        // 6. Debug / Tester Panel
        if (careerState != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BugReport, contentDescription = "Debug", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("BẢNG ĐIỀU KHIỂN DEBUG (TESTER PANEL)", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.debugAddFunds(50_000) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("+€50,000", fontSize = 11.sp, color = GoldPrimary)
                            }

                            OutlinedButton(
                                onClick = { viewModel.debugFullRecovery() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Hồi phục 100%", fontSize = 11.sp, color = PitchGreen)
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
private fun StatMini(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        Text(label, fontSize = 10.sp, color = TextMuted)
    }
}

@Composable
private fun SaveSlotRowCard(
    slot: SaveSlotSummary,
    canSave: Boolean,
    onSaveClick: () -> Unit,
    onLoadClick: () -> Unit
) {
    val isEmpty = slot.playerName.isEmpty()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateStr = if (slot.dateModifiedMillis > 0) dateFormat.format(Date(slot.dateModifiedMillis)) else ""

    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "VỊ TRÍ #${slot.slotIndex + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary
                    )
                    if (!isEmpty) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OVR: ${slot.ovr}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PitchGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (isEmpty) {
                    Text(
                        text = "Ô trống (Chưa có dữ liệu lưu)",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                } else {
                    Text(
                        text = "${slot.playerName} (${slot.position})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${slot.clubName} • Mùa ${slot.season} - Tuần ${slot.week}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    if (dateStr.isNotEmpty()) {
                        Text(
                            text = "Lần lưu cuối: $dateStr",
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End
            ) {
                Button(
                    onClick = onSaveClick,
                    enabled = canSave,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = "Save", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lưu", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onLoadClick,
                    enabled = !isEmpty,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = "Load", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tải", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

