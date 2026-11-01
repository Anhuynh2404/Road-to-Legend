package com.example.roadtolegend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.LifestyleItem
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

@Composable
fun LifestyleShopScreen(
    careerState: CareerState,
    shopItems: List<LifestyleItem>,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tiêu đề & Ví tiền
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("CỬA HÀNG & ĐỜI SỐNG", fontSize = 14.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                        Text("Trang thiết bị & Dịch vụ hỗ trợ sự nghiệp", fontSize = 11.sp, color = TextSecondary)
                    }

                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet", tint = GoldPrimary, modifier = Modifier.size(16.dp))
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
        }

        // Danh sách các vật phẩm
        items(shopItems) { item ->
            LifestyleItemCard(
                item = item,
                playerBalance = careerState.player.balance,
                onBuyClick = { viewModel.purchaseItem(item) }
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}

@Composable
fun LifestyleItemCard(
    item: LifestyleItem,
    playerBalance: Long,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.nameVi, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(item.category.titleVi, fontSize = 11.sp, color = TextMuted)
                }

                Surface(
                    color = GoldPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "€${String.format("%,d", item.price)}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(item.descriptionVi, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // Hiển thị Buff
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (item.trainingMultiplierBonus > 0) {
                    BuffBadge("+${(item.trainingMultiplierBonus * 100).toInt()}% Tập luyện")
                }
                if (item.fatigueReductionBonus > 0) {
                    BuffBadge("Giảm ${(item.fatigueReductionBonus * 100).toInt()}% Độ mỏi")
                }
                if (item.moraleBonus > 0) {
                    BuffBadge("+${item.moraleBonus} Tinh thần")
                }
                if (item.reputationBonus > 0) {
                    BuffBadge("+${item.reputationBonus} Danh tiếng")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bảo trì: €${item.weeklyMaintenance}/tuần",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                if (item.isPurchased) {
                    Surface(
                        color = PitchGreen.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Owned", tint = PitchGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ĐÃ SỞ HỮU", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
                        }
                    }
                } else {
                    Button(
                        onClick = onBuyClick,
                        enabled = playerBalance >= item.price,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg)
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = "Buy", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("MUA NGAY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BuffBadge(text: String) {
    Surface(
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = PitchGreen
        )
    }
}
