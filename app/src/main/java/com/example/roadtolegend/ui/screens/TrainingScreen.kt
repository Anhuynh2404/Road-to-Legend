package com.example.roadtolegend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Speed
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.TrainingFocus
import com.example.roadtolegend.model.TrainingIntensity
import com.example.roadtolegend.ui.viewmodel.CareerViewModel
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
import com.example.ui.theme.WarningAmber

@Composable
fun TrainingScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    val selectedIntensity = careerState.selectedTrainingIntensity

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Thẻ tổng quan Thể lực, Mệt mỏi và Số dư ví
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
                        Text("KẾ HOẠCH TẬP LUYỆN TUẦN", fontSize = 14.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                        Text("Ví: €${String.format("%,d", careerState.player.balance)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Thể lực: ${careerState.player.energy}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Độ mệt mỏi: ${careerState.player.fatigue.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (careerState.player.fatigue > 60) GoalCoral else TextPrimary)
                        Text("CSVC CLB: ${careerState.currentClub.trainingFacilityLevel}★", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }

        // 2. Chọn Cường độ tập luyện (Nhẹ / Vừa / Nặng)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = "Intensity", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CHỌN CƯỜNG ĐỘ TẬP LUYỆN:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TrainingIntensity.values().forEach { intensity ->
                            val isSelected = selectedIntensity == intensity
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.selectTrainingIntensity(intensity) }
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) GoldPrimary else DarkCardBorder,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                color = if (isSelected) GoldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = intensity.titleVi,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) GoldPrimary else TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Rủi ro: ${intensity.injuryRiskLabel}",
                                        fontSize = 10.sp,
                                        color = if (intensity == TrainingIntensity.HEAVY) GoalCoral else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Danh sách các bài tập
        item {
            Text("CHỌN NỘI DUNG TẬP LUYỆN:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        TrainingFocus.values().forEach { focus ->
            val isSelected = careerState.selectedTrainingFocus == focus
            val cost = focus.getCost(selectedIntensity)
            val canAfford = careerState.player.balance >= cost
            val (minGain, maxGain) = focus.getEstimatedGainRange(
                intensity = selectedIntensity,
                age = careerState.player.age,
                currentStat = 50.0,
                potential = careerState.player.hiddenPotential,
                facilityLevel = careerState.currentClub.trainingFacilityLevel
            )

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = canAfford) {
                            viewModel.selectTrainingFocus(focus)
                        }
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) GoldPrimary else DarkCardBorder,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    color = when {
                        !canAfford -> DarkSurface.copy(alpha = 0.5f)
                        isSelected -> DarkSurfaceVariant
                        else -> DarkSurface
                    }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = focus.titleVi,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) GoldPrimary else TextPrimary
                                )
                                if (focus.targetStatName.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = GoldPrimary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "+${focus.targetStatName}",
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldPrimary
                                        )
                                    }
                                }
                            }

                            // Giá tiền
                            Surface(
                                color = if (canAfford) PitchGreen.copy(alpha = 0.15f) else GoalCoral.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "€$cost",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (canAfford) PitchGreen else GoalCoral
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(focus.descriptionVi, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)

                        Spacer(modifier = Modifier.height(6.dp))

                        // Thông số chi tiết dự kiến
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (focus == TrainingFocus.REST_AND_RECOVERY) {
                                Text("⚡ +35 Thể lực • ❄️ Xả độ mỏi -25%", fontSize = 11.sp, color = PitchGreen, fontWeight = FontWeight.SemiBold)
                            } else {
                                Text(
                                    text = "Dự kiến: +${String.format("%.2f", minGain)} ~ +${String.format("%.2f", maxGain)}",
                                    fontSize = 11.sp,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (!canAfford) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = GoalCoral, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Không đủ tiền", fontSize = 10.sp, color = GoalCoral)
                                }
                            } else if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // 4. Nút tiến hành tập luyện
        item {
            val selectedCost = careerState.selectedTrainingFocus.getCost(selectedIntensity)
            val canPay = careerState.player.balance >= selectedCost

            Button(
                onClick = { viewModel.executeTrainingNow() },
                enabled = canPay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg)
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = "Train", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("XÁC NHẬN TẬP LUYỆN (CHI PHÍ €$selectedCost)", fontSize = 13.sp, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
