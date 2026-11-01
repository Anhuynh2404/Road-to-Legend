package com.example.roadtolegend.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.window.Dialog
import com.example.roadtolegend.model.NegotiationSession
import com.example.roadtolegend.model.NegotiationStatus
import com.example.roadtolegend.model.SquadRole
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoalCoral
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PitchGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ContractNegotiationDialog(
    session: NegotiationSession,
    onCounterSubmit: (wage: Long, bonus: Long, years: Int, role: SquadRole, releaseClause: Long?) -> Unit,
    onAcceptAgreement: () -> Unit,
    onDismiss: () -> Unit
) {
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.GERMANY) }

    var demandedWage by remember { mutableLongStateOf(session.currentWageDemand) }
    var demandedBonus by remember { mutableLongStateOf(session.currentSigningBonusDemand) }
    var demandedYears by remember { mutableIntStateOf(session.currentContractYearsDemand) }
    var demandedRole by remember { mutableStateOf(session.currentRoleDemand) }
    var demandedReleaseClause by remember { mutableStateOf(session.currentReleaseClauseDemand) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (session.isRenewal) "ĐÀM PHÁN GIA HẠN HỢP ĐỒNG" else "ĐÀM PHÁN ĐIỀU KHOẢN CÁ NHÂN",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = GoldPrimary
                        )
                        Text(
                            text = session.clubName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Vòng ${session.roundsCount + 1}/${session.maxRounds}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = PitchGreen,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Khối phản hồi bằng chữ của CLB (TUYỆT ĐỐI KHÔNG HIỂN THỊ PHẦN TRĂM)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = when (session.status) {
                        NegotiationStatus.AGREED -> PitchGreen.copy(alpha = 0.15f)
                        NegotiationStatus.TERMINATED_WALKOUT -> GoalCoral.copy(alpha = 0.15f)
                        else -> DarkBg
                    },
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (session.status) {
                                    NegotiationStatus.AGREED -> Icons.Default.Check
                                    NegotiationStatus.TERMINATED_WALKOUT -> Icons.Default.Close
                                    else -> Icons.Default.Handshake
                                },
                                contentDescription = null,
                                tint = when (session.status) {
                                    NegotiationStatus.AGREED -> PitchGreen
                                    NegotiationStatus.TERMINATED_WALKOUT -> GoalCoral
                                    else -> GoldPrimary
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Phản hồi từ Ban lãnh đạo:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = session.latestFeedbackMessageVi,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = when (session.status) {
                                NegotiationStatus.AGREED -> PitchGreen
                                NegotiationStatus.TERMINATED_WALKOUT -> GoalCoral
                                else -> TextPrimary
                            }
                        )

                        if (session.status == NegotiationStatus.IN_PROGRESS && session.clubOfferedWage > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "CLB hiện chấp thuận: €${currencyFormatter.format(session.clubOfferedWage)}/tuần • Lót tay: €${currencyFormatter.format(session.clubOfferedSigningBonus)} • Vai trò: ${session.clubOfferedRole.titleVi}",
                                fontSize = 10.sp,
                                color = GoldPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Nếu đàm phán vẫn đang diễn ra, cho phép người chơi điều chỉnh mức yêu cầu
                if (session.status == NegotiationStatus.IN_PROGRESS) {
                    Text(
                        text = "YÊU CẦU CỦA BẠN:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = TextMuted,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lương tuần
                    AdjustmentRow(
                        title = "Lương tuần (€)",
                        valueText = "€ ${currencyFormatter.format(demandedWage)}",
                        onDecrease = { demandedWage = (demandedWage - 100).coerceAtLeast(200L) },
                        onIncrease = { demandedWage += 100 }
                    )

                    // Tiền lót tay (Signing Bonus)
                    AdjustmentRow(
                        title = "Tiền thưởng ký (Lót tay)",
                        valueText = "€ ${currencyFormatter.format(demandedBonus)}",
                        onDecrease = { demandedBonus = (demandedBonus - 1_000).coerceAtLeast(0L) },
                        onIncrease = { demandedBonus += 1_000 }
                    )

                    // Thời hạn năm hợp đồng
                    AdjustmentRow(
                        title = "Thời hạn hợp đồng",
                        valueText = "$demandedYears năm",
                        onDecrease = { demandedYears = (demandedYears - 1).coerceAtLeast(1) },
                        onIncrease = { demandedYears = (demandedYears + 1).coerceAtMost(5) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Vai trò cam kết trong đội hình
                    Text(
                        text = "Vai trò cam kết trong đội hình:",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SquadRole.values().forEach { role ->
                            val isSelected = demandedRole == role
                            FilterChip(
                                selected = isSelected,
                                onClick = { demandedRole = role },
                                label = { Text(role.titleVi, fontSize = 9.5.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = DarkBg,
                                    containerColor = DarkBg,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Nút gửi phản đề nghị
                    Button(
                        onClick = {
                            onCounterSubmit(demandedWage, demandedBonus, demandedYears, demandedRole, demandedReleaseClause)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                    ) {
                        Text("GỬI BẢN YÊU CẦU (MẶC CẢ)", fontWeight = FontWeight.Black, color = DarkBg, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("TẠM DỪNG / RỜI BÀN ĐÀM PHÁN", fontSize = 11.sp, color = TextMuted)
                    }
                } else if (session.status == NegotiationStatus.AGREED) {
                    // Đã thỏa thuận thành công
                    Button(
                        onClick = onAcceptAgreement,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PitchGreen)
                    ) {
                        Text("ĐỒNG Ý KÝ HỢP ĐỒNG CHÍNH THỨC", fontWeight = FontWeight.Black, color = Color.White, fontSize = 12.sp)
                    }
                } else {
                    // Đàm phán đổ vỡ
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoalCoral)
                    ) {
                        Text("ĐÓNG HỘP THOẠI", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdjustmentRow(
    title: String,
    valueText: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 11.sp, color = TextSecondary)

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onDecrease,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Giảm", tint = GoldPrimary, modifier = Modifier.size(16.dp))
            }

            Surface(
                color = DarkBg,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.width(115.dp)
            ) {
                Text(
                    text = valueText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }

            IconButton(
                onClick = onIncrease,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tăng", tint = GoldPrimary, modifier = Modifier.size(16.dp))
            }
        }
    }
}
