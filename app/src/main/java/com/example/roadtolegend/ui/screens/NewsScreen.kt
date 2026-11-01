package com.example.roadtolegend.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.ui.viewmodel.CareerViewModel
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
fun NewsScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("ALL") }

    val filteredNews = careerState.newsFeed.filter { news ->
        when (selectedCategory) {
            "ALL" -> true
            "TRANSFER" -> news.category == "TRANSFER" || news.category == "RUMOR"
            "MATCH" -> news.category == "MATCH"
            "CONTRACT" -> news.category == "CONTRACT" || news.category == "AGENT"
            else -> true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Tiêu đề
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TIN TỨC & TRUYỀN THÔNG",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Bản tin chuyển nhượng, tin đồn sân cỏ và sự kiện tuần",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { viewModel.markAllNewsAsRead() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = GoldLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Đã đọc tất cả", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                }
            }
        }

        // 2. Bộ lọc chuyên mục
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == "ALL",
                    onClick = { selectedCategory = "ALL" },
                    label = { Text("Tất cả", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedCategory == "TRANSFER",
                    onClick = { selectedCategory = "TRANSFER" },
                    label = { Text("Chuyển nhượng & Tin đồn", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedCategory == "MATCH",
                    onClick = { selectedCategory = "MATCH" },
                    label = { Text("Trận đấu", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedCategory == "CONTRACT",
                    onClick = { selectedCategory = "CONTRACT" },
                    label = { Text("Hợp đồng", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        // 3. Danh sách tin tức
        if (filteredNews.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Newspaper, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Chưa có bản tin nào thuộc chuyên mục này", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        } else {
            items(filteredNews.size) { index ->
                val news = filteredNews[index]

                val sourceBadgeColor = when (news.reliabilityTier) {
                    1 -> PitchGreen
                    2 -> GoldPrimary
                    else -> GoalCoral
                }

                val sourceBadgeText = when (news.reliabilityTier) {
                    1 -> "Tier 1: Chính thức / Rất uy tín"
                    2 -> "Tier 2: Truyền thông uy tín"
                    else -> "Tier 3: Tin đồn lá cải"
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.markNewsAsRead(news.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (!news.isRead) DarkSurfaceVariant else DarkSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = if (!news.isRead) androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)) else CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (!news.isRead) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(GoalCoral, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(news.sourceName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                            }

                            Surface(
                                color = sourceBadgeColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = sourceBadgeText,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = sourceBadgeColor,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = news.headline,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (news.category == "RUMOR") WarningAmber else TextPrimary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = news.body,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tuần ${news.week}",
                                fontSize = 10.sp,
                                color = TextMuted
                            )

                            Text(
                                text = when (news.category) {
                                    "TRANSFER" -> "Chuyển nhượng"
                                    "RUMOR" -> "Tin đồn"
                                    "MATCH" -> "Trận đấu"
                                    "CONTRACT" -> "Hợp đồng"
                                    "AGENT" -> "Người đại diện"
                                    else -> "Tin thể thao"
                                },
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
