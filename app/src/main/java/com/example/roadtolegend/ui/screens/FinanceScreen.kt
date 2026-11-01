package com.example.roadtolegend.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
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
import com.example.roadtolegend.engine.TransferConfig
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.TransactionType
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FinanceScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.GERMANY) }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredTransactions = careerState.financeTransactions.filter { tx ->
        when (selectedFilter) {
            "ALL" -> true
            "WAGE" -> tx.type == TransactionType.WAGE
            "BONUS" -> tx.type == TransactionType.SIGNING_BONUS || tx.type == TransactionType.MATCH_BONUS || tx.type == TransactionType.TROPHY_BONUS
            "AGENT" -> tx.type == TransactionType.AGENT_COMMISSION || tx.type == TransactionType.AGENT_RETAINER
            "SHOP" -> tx.type == TransactionType.SHOP_PURCHASE || tx.type == TransactionType.SHOP_MAINTENANCE
            else -> true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        text = "SỔ TÀI CHÍNH & THU NHẬP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Theo dõi dòng tiền, lương thưởng, hoa hồng và chi phí",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // 2. Thẻ Tổng quan Ví tiền & Thu nhập
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, PitchGreen.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SỐ DƯ TÀI KHOẢN HIỆN TẠI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Text(
                        text = "€${currencyFormatter.format(careerState.player.balance)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = PitchGreen
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Lương tuần (thực nhận)", fontSize = 9.sp, color = TextMuted)
                                val netWage = TransferConfig.calculateNetIncome(careerState.currentContract.weeklyWage)
                                Text("€${currencyFormatter.format(netWage)}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldLight)
                                Text("Sau thuế ${TransferConfig.TAX_RATE_PERCENT}%", fontSize = 8.sp, color = TextSecondary)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Phí đại diện/tuần", fontSize = 9.sp, color = TextMuted)
                                Text("€${currencyFormatter.format(careerState.agent.tier.weeklyRetainer)}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = if (careerState.agent.tier.weeklyRetainer > 0) WarningAmber else TextSecondary)
                                Text("Hoa hồng: ${careerState.agent.tier.commissionPercent}%", fontSize = 8.sp, color = TextSecondary)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Thu nhập ước tính/năm", fontSize = 9.sp, color = TextMuted)
                                val estAnnual = TransferConfig.calculateNetIncome(careerState.currentContract.weeklyWage) * 52
                                Text("€${currencyFormatter.format(estAnnual)}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = PitchGreen)
                                Text("52 tuần thi đấu", fontSize = 8.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // 3. Bộ lọc giao dịch
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("Tất cả", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedFilter == "WAGE",
                    onClick = { selectedFilter = "WAGE" },
                    label = { Text("Lương tuần", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedFilter == "BONUS",
                    onClick = { selectedFilter = "BONUS" },
                    label = { Text("Tiền thưởng", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedFilter == "AGENT",
                    onClick = { selectedFilter = "AGENT" },
                    label = { Text("Phí Agent", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedFilter == "SHOP",
                    onClick = { selectedFilter = "SHOP" },
                    label = { Text("Chi tiêu", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        // 4. Lịch sử giao dịch
        if (filteredTransactions.isEmpty()) {
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
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = TextMuted, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Chưa có bản ghi giao dịch nào cho bộ lọc này.", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        } else {
            items(filteredTransactions.size) { index ->
                val tx = filteredTransactions[index]

                val isIncome = tx.type.isIncome

                val icon = when (tx.type) {
                    TransactionType.WAGE -> Icons.Default.Payment
                    TransactionType.SIGNING_BONUS, TransactionType.TROPHY_BONUS -> Icons.Default.CardGiftcard
                    TransactionType.AGENT_COMMISSION, TransactionType.AGENT_RETAINER -> Icons.Default.BusinessCenter
                    TransactionType.SHOP_PURCHASE, TransactionType.SHOP_MAINTENANCE -> Icons.Default.ShoppingBag
                    else -> Icons.Default.AttachMoney
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isIncome) PitchGreen.copy(alpha = 0.15f) else GoalCoral.copy(alpha = 0.15f)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isIncome) PitchGreen else GoalCoral,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(tx.descriptionVi, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Mùa ${tx.season} - Tuần ${tx.week} • Số dư sau GD: €${currencyFormatter.format(tx.balanceAfter)}", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        Text(
                            text = "${if (isIncome) "+" else "-"}€${currencyFormatter.format(tx.amount)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isIncome) PitchGreen else GoalCoral
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
