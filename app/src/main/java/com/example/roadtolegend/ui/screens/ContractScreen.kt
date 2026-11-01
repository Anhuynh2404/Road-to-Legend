package com.example.roadtolegend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
fun ContractScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.GERMANY) }
    val contract = careerState.currentContract
    val player = careerState.player
    val currentClub = careerState.currentClub

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Tiêu đề màn hình
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HỢP ĐỒNG CHUYÊN NGHIỆP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Chi tiết cam kết, mức đãi ngộ và giá trị thị trường",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // 2. Thẻ Trạng thái Hợp đồng chính
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ClubBadge(club = currentClub, size = 48.dp, isFictionalMode = careerState.isFictionalMode)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (contract.isFreeAgent) "Cầu thủ tự do (Không có CLB)" else currentClub.getDisplayName(careerState.isFictionalMode),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Vai trò: ${contract.squadRole.titleVi}",
                                fontSize = 12.sp,
                                color = GoldLight
                            )
                        }

                        // Badge trạng thái
                        val statusText = when {
                            contract.isFreeAgent -> "Tự do"
                            contract.isExpiringSoon -> "Sắp hết hạn"
                            else -> "Đang hiệu lực"
                        }
                        val statusColor = when {
                            contract.isFreeAgent -> GoalCoral
                            contract.isExpiringSoon -> WarningAmber
                            else -> PitchGreen
                        }
                        Surface(
                            color = statusColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = statusText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lưới thông số cốt lõi
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Lương tuần", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "€${currencyFormatter.format(contract.weeklyWage)}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PitchGreen
                                )
                                Text("~€${currencyFormatter.format(contract.weeklyWage * 52)}/năm", fontSize = 9.sp, color = TextSecondary)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Thời hạn còn lại", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = "${contract.totalWeeksRemaining} tuần",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (contract.isExpiringSoon) WarningAmber else TextPrimary
                                )
                                Text("~${contract.contractYearsRemaining} mùa giải", fontSize = 9.sp, color = TextSecondary)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Phí phá vỡ HĐ", fontSize = 10.sp, color = TextMuted)
                                Text(
                                    text = contract.releaseClause?.let { "€${currencyFormatter.format(it)}" } ?: "Không có",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = GoldPrimary
                                )
                                Text("Release Clause", fontSize = 9.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Phản hồi mức độ hài lòng của CLB bằng CHỮ (Nguyên tắc: Không hiện phần trăm)
                    val satisfactionHint = when {
                        contract.clubSatisfaction >= 85 -> "Ban lãnh đạo CLB vô cùng hài lòng với phong độ và sự cống hiến của bạn."
                        contract.clubSatisfaction >= 65 -> "CLB tin tưởng vào kế hoạch phát triển cùng bạn ở mùa giải này."
                        contract.clubSatisfaction >= 40 -> "Ban lãnh đạo mong đợi bạn nỗ lực nhiều hơn để xứng đáng với mức đãi ngộ hiện tại."
                        else -> "CLB đang cân nhắc tương lai của bạn và có thể đưa vào danh sách chuyển nhượng."
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBg, RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = GoldLight, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = satisfactionHint,
                            fontSize = 11.sp,
                            color = TextSecondary,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Nút Đàm phán gia hạn hợp đồng
                    if (!contract.isFreeAgent) {
                        Button(
                            onClick = { viewModel.startContractRenewal() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("MỞ ĐÀM PHÁN GIA HẠN HỢP ĐỒNG", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // 3. Chi tiết các điều khoản phụ & Tiền thưởng
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CÁC ĐIỀU KHOẢN PHỤ & TIỀN THƯỞNG", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val bonusItems = listOf(
                        "Thưởng ra sân mỗi trận" to "€${currencyFormatter.format(contract.bonusPerAppearance)}",
                        "Thưởng mỗi bàn thắng" to "€${currencyFormatter.format(contract.bonusPerGoal)}",
                        "Thưởng mỗi pha kiến tạo" to "€${currencyFormatter.format(contract.bonusPerAssist)}",
                        "Thưởng giữ sạch lưới (HV/TM)" to "€${currencyFormatter.format(contract.bonusPerCleanSheet)}",
                        "Thưởng chức vô địch giải đấu" to "€${currencyFormatter.format(contract.bonusTrophy)}",
                        "Tự động tăng lương hàng năm" to if (contract.annualWageIncreasePercent > 0) "+${contract.annualWageIncreasePercent}% mỗi mùa" else "Không áp dụng"
                    )

                    bonusItems.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, fontSize = 12.sp, color = TextSecondary)
                            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                        }
                    }
                }
            }
        }

        // 4. Giá trị thị trường & Lịch sử biến động
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = PitchGreen, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GIÁ TRỊ THỊ TRƯỜNG (MARKET VALUE)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val currentVal = com.example.roadtolegend.engine.TransferConfig.calculateMarketValue(player, contract.totalWeeksRemaining)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Định giá hiện tại", fontSize = 10.sp, color = TextMuted)
                            Text(
                                "€${currencyFormatter.format(currentVal)}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = PitchGreen
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Đỉnh cao sự nghiệp", fontSize = 10.sp, color = TextMuted)
                            Text(
                                "€${currencyFormatter.format(player.peakMarketValue.coerceAtLeast(currentVal))}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Lịch sử định giá gần đây:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    if (careerState.marketHistory.isEmpty()) {
                        Text("Dữ liệu định giá sẽ được cập nhật hàng tuần qua các trận đấu.", fontSize = 11.sp, color = TextMuted)
                    } else {
                        careerState.marketHistory.takeLast(6).reversed().forEach { point ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Mùa ${point.season} - Tuần ${point.week}", fontSize = 11.sp, color = TextSecondary)
                                Text("€${currencyFormatter.format(point.value)}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
