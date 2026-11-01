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
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.roadtolegend.model.Agent
import com.example.roadtolegend.model.AgentTier
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
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AgentScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.GERMANY) }
    val currentAgent = careerState.agent
    val player = careerState.player
    var showAdviceDialog by remember { mutableStateOf(false) }
    var adviceText by remember { mutableStateOf("") }

    if (showAdviceDialog) {
        AlertDialog(
            onDismissRequest = { showAdviceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("TƯ VẤN THỊ TRƯỜNG TỪ ĐẠI DIỆN", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Text(
                    text = adviceText,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showAdviceDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg)
                ) {
                    Text("ĐÃ HIỂU", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface
        )
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
                        text = "NGƯỜI ĐẠI DIỆN (AGENT)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Quản lý hợp đồng, mối quan hệ và tiếp cận CLB lớn",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // 2. Hồ sơ người đại diện hiện tại
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldPrimary.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BusinessCenter,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentAgent.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = currentAgent.tier.titleVi,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GoldLight
                            )
                        }

                        Surface(
                            color = PitchGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Đang hợp tác",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PitchGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentAgent.tier.descriptionVi,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Bảng số liệu năng lực
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Hoa hồng hợp đồng", fontSize = 9.sp, color = TextMuted)
                                Text("${currentAgent.tier.commissionPercent}%", fontSize = 13.sp, fontWeight = FontWeight.Black, color = GoldLight)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Phí duy trì tuần", fontSize = 9.sp, color = TextMuted)
                                Text("€${currencyFormatter.format(currentAgent.tier.weeklyRetainer)}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = if (currentAgent.tier.weeklyRetainer > 0) WarningAmber else PitchGreen)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("Đề nghị mang về", fontSize = 9.sp, color = TextMuted)
                                Text("${currentAgent.totalOffersBrought} đề nghị", fontSize = 13.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Nút Hành động với Đại diện
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                adviceText = viewModel.askAgentMarketAdvice()
                                showAdviceDialog = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("HỎI TÌNH HÌNH THỊ TRƯỜNG", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (currentAgent.tier != AgentTier.NONE) {
                            OutlinedButton(
                                onClick = { viewModel.fireAgent() },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = GoalCoral, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SA THẢI", fontSize = 11.sp, color = GoalCoral)
                            }
                        }
                    }
                }
            }
        }

        // 3. Danh sách các Đại diện / Cấp bậc có thể thuê
        item {
            Text("CÁC CÔNG TY & NGƯỜI ĐẠI DIỆN TRÊN THỊ TRƯỜNG", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))

            val availableAgents = TransferConfig.getAvailableAgents()

            availableAgents.forEach { ag ->
                val isCurrent = ag.tier == currentAgent.tier
                val canHireOvr = player.ovr >= ag.tier.minOvrRequired
                val canHireRep = player.domesticReputation >= ag.tier.minReputationRequired
                val isEligible = canHireOvr && canHireRep

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, PitchGreen) else CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ag.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(ag.tier.titleVi, fontSize = 11.sp, color = GoldLight)
                            }

                            if (isCurrent) {
                                Surface(
                                    color = PitchGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Hiện tại", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PitchGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.hireAgent(ag) },
                                    enabled = isEligible,
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(if (ag.tier.ordinal > currentAgent.tier.ordinal) "NÂNG CẤP" else "THUÊ", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(ag.tier.descriptionVi, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Điều kiện và Chi phí
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hoa hồng: ${ag.tier.commissionPercent}% • Duy trì: €${currencyFormatter.format(ag.tier.weeklyRetainer)}/tuần",
                                fontSize = 10.sp,
                                color = TextMuted
                            )

                            if (!isEligible && !isCurrent) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = GoalCoral, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Yêu cầu OVR ${ag.tier.minOvrRequired}+ & Danh tiếng ${ag.tier.minReputationRequired}+", fontSize = 10.sp, color = GoalCoral)
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }
}
