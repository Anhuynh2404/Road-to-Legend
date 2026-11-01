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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.ui.viewmodel.CareerViewModel
import com.example.roadtolegend.ui.viewmodel.MainTab
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

data class MoreMenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val targetTab: MainTab,
    val badgeCount: Int = 0
)

@Composable
fun MoreMenuScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    val pendingOffersCount = careerState.incomingOffers.count { it.isPendingPlayerAction }
    val unreadNewsCount = careerState.newsFeed.count { !it.isRead }

    val menuItems = listOf(
        MoreMenuItem(
            title = "Sự nghiệp",
            subtitle = "Lịch sử mùa giải & cột mốc",
            icon = Icons.Default.Assessment,
            iconColor = GoldPrimary,
            targetTab = MainTab.CAREER_STATS
        ),
        MoreMenuItem(
            title = "Hợp đồng",
            subtitle = "Lương thưởng & điều khoản",
            icon = Icons.Default.Assignment,
            iconColor = PitchGreen,
            targetTab = MainTab.CONTRACT
        ),
        MoreMenuItem(
            title = "Chuyển nhượng",
            subtitle = "Đề nghị & thị trường",
            icon = Icons.Default.SwapHoriz,
            iconColor = Color(0xFF64B5F6),
            targetTab = MainTab.TRANSFER,
            badgeCount = pendingOffersCount
        ),
        MoreMenuItem(
            title = "Người đại diện",
            subtitle = "Đàm phán & hợp tác",
            icon = Icons.Default.BusinessCenter,
            iconColor = Color(0xFFFFB74D),
            targetTab = MainTab.AGENT
        ),
        MoreMenuItem(
            title = "Đời sống & Mua sắm",
            subtitle = "Nhà cửa, xe cộ, xa xỉ",
            icon = Icons.Default.ShoppingBag,
            iconColor = Color(0xFFBA68C8),
            targetTab = MainTab.SHOP
        ),
        MoreMenuItem(
            title = "Tài chính",
            subtitle = "Ngân sách & thu chi",
            icon = Icons.Default.AccountBalanceWallet,
            iconColor = Color(0xFF4DB6AC),
            targetTab = MainTab.FINANCE
        ),
        MoreMenuItem(
            title = "Tin tức & Báo chí",
            subtitle = "Tin đồn & truyền thông",
            icon = Icons.Default.Campaign,
            iconColor = Color(0xFFFF8A65),
            targetTab = MainTab.NEWS,
            badgeCount = unreadNewsCount
        ),
        MoreMenuItem(
            title = "Cài đặt & Lưu game",
            subtitle = "Quản lý save slot",
            icon = Icons.Default.Save,
            iconColor = Color(0xFF90A4AE),
            targetTab = MainTab.PROFILE_SAVE
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MENU MỞ RỘNG",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary
                )
                Text(
                    text = "Các tính năng quản lý sự nghiệp & đời sống cầu thủ",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid Menu
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(menuItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
                        .clickable { viewModel.selectTab(item.targetTab) },
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(DarkSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = item.iconColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            if (item.badgeCount > 0) {
                                Badge(
                                    containerColor = GoalCoral,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "${item.badgeCount}",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = item.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = item.subtitle,
                            fontSize = 10.sp,
                            color = TextMuted,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }
    }
}
