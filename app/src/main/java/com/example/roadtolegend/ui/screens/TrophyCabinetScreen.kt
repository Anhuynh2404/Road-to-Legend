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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.TrophyCategory
import com.example.roadtolegend.model.TrophyItem
import com.example.roadtolegend.ui.viewmodel.CareerViewModel
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TrophyCabinetScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf<TrophyCategory?>(null) }
    var inspectingTrophy by remember { mutableStateOf<TrophyItem?>(null) }

    val allTrophies = careerState.trophiesCabinet
    val filteredTrophies = if (selectedCategoryFilter == null) {
        allTrophies
    } else {
        allTrophies.filter { it.category == selectedCategoryFilter }
    }

    val individualCount = allTrophies.count { it.category == TrophyCategory.INDIVIDUAL }
    val clubCount = allTrophies.count { it.category == TrophyCategory.CLUB }
    val nationalCount = allTrophies.count { it.category == TrophyCategory.NATIONAL_TEAM }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
    ) {
        // 1. Tiêu đề Tủ Trưng Bày
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TỦ TRƯNG BÀY DANH HIỆU",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Nơi lưu giữ vinh quang và những chiếc cúp huyền thoại",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🏆", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${allTrophies.size} CÚP",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Thẻ Thống Kê Các Ngăn Tủ
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkSurface,
            shape = RoundedCornerShape(14.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TrophyStatItem("Cá nhân", "$individualCount", "⭐", GoldLight)
                TrophyStatItem("Cùng CLB", "$clubCount", "🏆", PitchGreen)
                TrophyStatItem("ĐTQG", "$nationalCount", "🎖️", Color(0xFF64B5F6))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Bộ lọc Ngăn Tủ (Tất cả / Cá nhân / CLB / ĐTQG)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategoryFilter == null,
                onClick = { selectedCategoryFilter = null },
                label = { Text("Tất cả (${allTrophies.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldPrimary,
                    selectedLabelColor = DarkBg,
                    containerColor = DarkSurface,
                    labelColor = TextMuted
                )
            )
            FilterChip(
                selected = selectedCategoryFilter == TrophyCategory.INDIVIDUAL,
                onClick = { selectedCategoryFilter = TrophyCategory.INDIVIDUAL },
                label = { Text("⭐ Cá nhân ($individualCount)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldPrimary,
                    selectedLabelColor = DarkBg,
                    containerColor = DarkSurface,
                    labelColor = TextMuted
                )
            )
            FilterChip(
                selected = selectedCategoryFilter == TrophyCategory.CLUB,
                onClick = { selectedCategoryFilter = TrophyCategory.CLUB },
                label = { Text("🏆 CLB ($clubCount)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldPrimary,
                    selectedLabelColor = DarkBg,
                    containerColor = DarkSurface,
                    labelColor = TextMuted
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Ngăn Tủ Kính Trưng Bày Danh Hiệu (Grid)
        if (filteredTrophies.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(DarkSurface, RoundedCornerShape(16.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(72.dp),
                        color = DarkSurfaceVariant,
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "NGĂN TỦ HIỆN ĐANG TRỐNG",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Hãy nỗ lực tập luyện, thi đấu xuất sắc, ghi thật nhiều bàn thắng và cùng CLB vô địch các giải đấu để lấp đầy tủ danh hiệu của bạn!",
                        fontSize = 12.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTrophies) { trophy ->
                    TrophyDisplayCard(trophy = trophy, onClick = { inspectingTrophy = trophy })
                }
            }
        }
    }

    // Dialog Xem Chi Tiết Bằng Khen / Cúp
    inspectingTrophy?.let { trophy ->
        TrophyDetailDialog(trophy = trophy, onDismiss = { inspectingTrophy = null })
    }
}

@Composable
fun TrophyStatItem(label: String, value: String, icon: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Text(label, fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Composable
fun TrophyDisplayCard(trophy: TrophyItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .border(
                1.dp,
                Brush.verticalGradient(listOf(GoldPrimary.copy(alpha = 0.6f), DarkCardBorder)),
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(DarkSurfaceVariant.copy(alpha = 0.5f), DarkSurface)
                    )
                )
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Biểu tượng Cúp với hiệu ứng hào quang
            Surface(
                modifier = Modifier.size(54.dp),
                color = GoldDark.copy(alpha = 0.25f),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(trophy.iconEmoji, fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = trophy.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = GoldLight,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = DarkBg,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "${trophy.competitionOrOrganization} • ${trophy.year}",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TrophyDetailDialog(trophy: TrophyItem, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, GoldPrimary, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Surface(
                    modifier = Modifier.size(72.dp),
                    color = GoldDark.copy(alpha = 0.3f),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(2.dp, GoldPrimary)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(trophy.iconEmoji, fontSize = 38.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = trophy.name.uppercase(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${trophy.category.titleVi} • Mùa ${trophy.seasonNumber} (Năm ${trophy.year})",
                    fontSize = 12.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("ĐƠN VỊ TỔ CHỨC / GIẢI ĐẤU:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(trophy.competitionOrOrganization, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Ý NGHĨA & CHI TIẾT:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(trophy.description, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ĐÓNG", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
