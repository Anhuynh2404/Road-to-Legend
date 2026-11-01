package com.example.roadtolegend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.Foot
import com.example.roadtolegend.model.Position
import com.example.roadtolegend.ui.viewmodel.AppScreen
import com.example.roadtolegend.ui.viewmodel.CareerViewModel
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterCreationScreen(
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    var playerName by remember { mutableStateOf("Nguyễn Văn Hùng") }
    var age by remember { mutableIntStateOf(17) }
    var heightCm by remember { mutableIntStateOf(178) }
    var selectedFoot by remember { mutableStateOf(Foot.RIGHT) }
    var selectedPosition by remember { mutableStateOf(Position.ST) }

    // 6 chỉ số khởi tạo (Mỗi chỉ số 25 - 55, Tổng = 300)
    var pac by remember { mutableDoubleStateOf(52.0) }
    var sho by remember { mutableDoubleStateOf(55.0) }
    var pas by remember { mutableDoubleStateOf(48.0) }
    var dri by remember { mutableDoubleStateOf(52.0) }
    var sta by remember { mutableDoubleStateOf(50.0) }
    var def by remember { mutableDoubleStateOf(43.0) }

    val lowTierClubs = remember { DefaultDatabase.createVLeague2Clubs() }
    var selectedClub by remember { mutableStateOf<Club>(lowTierClubs.first()) }

    fun autoAllocatePoints(pos: Position) {
        when (pos) {
            Position.ST -> { pac = 53.0; sho = 55.0; pas = 46.0; dri = 52.0; sta = 52.0; def = 42.0 }
            Position.LW, Position.RW -> { pac = 55.0; sho = 50.0; pas = 48.0; dri = 54.0; sta = 51.0; def = 42.0 }
            Position.CAM -> { pac = 48.0; sho = 52.0; pas = 55.0; dri = 53.0; sta = 49.0; def = 43.0 }
            Position.CM -> { pac = 48.0; sho = 47.0; pas = 55.0; dri = 51.0; sta = 53.0; def = 46.0 }
            Position.CDM -> { pac = 46.0; sho = 42.0; pas = 52.0; dri = 47.0; sta = 55.0; def = 55.0 }
            Position.CB -> { pac = 48.0; sho = 35.0; pas = 46.0; dri = 43.0; sta = 54.0; def = 55.0 }
            Position.LB, Position.RB -> { pac = 54.0; sho = 38.0; pas = 48.0; dri = 48.0; sta = 54.0; def = 52.0 }
            Position.GK -> { pac = 45.0; sho = 25.0; pas = 48.0; dri = 40.0; sta = 55.0; def = 57.0 }
        }
    }

    val totalAllocated = (pac + sho + pas + dri + sta + def).toInt()
    val pointsRemaining = 300 - totalAllocated
    val estimatedOvr = selectedPosition.calculateOvr(pac, sho, pas, dri, sta, def)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Thanh tiêu đề phía trên
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { viewModel.navigateTo(AppScreen.WELCOME) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Back", tint = GoldPrimary)
                }
                Text(
                    text = "KHỞI TẠO CẦU THỦ MỚI",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = GoldPrimary,
                    letterSpacing = 1.sp
                )
            }
        }

        // 1. Thông tin cá nhân
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("1. HỒ SƠ CÁ NHÂN", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = playerName,
                        onValueChange = { playerName = it },
                        label = { Text("Họ và Tên Cầu Thủ") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tuổi (16 - 19)
                    Text("Độ tuổi khởi đầu: $age tuổi", fontSize = 13.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(16, 17, 18, 19).forEach { a ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { age = a },
                                color = if (age == a) GoldPrimary else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "$a tuổi",
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    fontWeight = FontWeight.Bold,
                                    color = if (age == a) DarkBg else TextPrimary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Chiều cao & Chân thuận
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Chiều cao: ${heightCm}cm", fontSize = 13.sp, color = TextSecondary)
                            Slider(
                                value = heightCm.toFloat(),
                                onValueChange = { heightCm = it.toInt() },
                                valueRange = 165f..195f,
                                colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("Chân thuận", fontSize = 13.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Foot.values().forEach { f ->
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { selectedFoot = f },
                                        color = if (selectedFoot == f) PitchGreen else DarkSurfaceVariant
                                    ) {
                                        Text(
                                            text = if (f == Foot.RIGHT) "Phải" else if (f == Foot.LEFT) "Trái" else "Cả hai",
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedFoot == f) DarkBg else TextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Vị trí thi đấu sở trường
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
                        Text("2. VỊ TRÍ SỞ TRƯỜNG", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                        Text(selectedPosition.fullNameVi, color = PitchGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Position.values().forEach { pos ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedPosition = pos
                                        autoAllocatePoints(pos)
                                    },
                                color = if (selectedPosition == pos) GoldPrimary else DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = pos.code,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    fontWeight = FontWeight.Black,
                                    color = if (selectedPosition == pos) DarkBg else TextPrimary,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Phân bổ 300 điểm chỉ số khởi tạo
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
                        Column {
                            Text("3. PHÂN BỔ 300 ĐIỂM CHỈ SỐ", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            Text(
                                text = "Điểm còn lại: $pointsRemaining",
                                color = if (pointsRemaining == 0) PitchGreen else if (pointsRemaining > 0) GoldPrimary else GoalCoral,
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp
                            )
                        }

                        Surface(
                            color = GoldPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "OVR $estimatedOvr",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { autoAllocatePoints(selectedPosition) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Auto", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tự động phân bổ tối ưu cho ${selectedPosition.code}", color = GoldPrimary, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    StatSliderItem("PAC (Tốc độ)", pac) { pac = it }
                    StatSliderItem("SHO (Dứt điểm)", sho) { sho = it }
                    StatSliderItem("PAS (Chuyền bóng)", pas) { pas = it }
                    StatSliderItem("DRI (Kỹ thuật)", dri) { dri = it }
                    StatSliderItem("STA (Thể lực)", sta) { sta = it }
                    StatSliderItem("DEF (Phòng ngự)", def) { def = it }
                }
            }
        }

        // 4. Lựa chọn CLB khởi nghiệp (V-League 2)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("4. HỢP ĐỒNG KHỞI NGHIỆP (V-LEAGUE 2)", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Chọn một câu lạc bộ chuyên nghiệp để bắt đầu sự nghiệp của bạn:", fontSize = 12.sp, color = TextSecondary)

                    Spacer(modifier = Modifier.height(10.dp))

                    lowTierClubs.forEach { club ->
                        val isSelected = selectedClub.id == club.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedClub = club }
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) GoldPrimary else DarkCardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            color = if (isSelected) DarkSurfaceVariant else DarkSurface
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    com.example.roadtolegend.ui.components.ClubBadge(club = club, size = 32.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(club.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                        Text("CSVC: ${club.trainingFacilityLevel}★ • HLV: ${club.coachQuality}★ • Đội hình: OVR ${club.squadOvrAverage}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = GoldPrimary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Chi tiết hợp đồng
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("ĐIỀU KHOẢN HỢP ĐỒNG:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            Text("• Lương tuần: €350/tuần (thời hạn 2 năm)", fontSize = 12.sp, color = TextSecondary)
                            Text("• Thưởng bàn thắng: €80 • Thưởng kiến tạo: €40", fontSize = 12.sp, color = TextSecondary)
                            Text("• Phí phá vỡ hợp đồng: €400,000", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Nút Ký hợp đồng & Khởi đầu
        item {
            Button(
                onClick = {
                    viewModel.startCareer(
                        playerName = playerName,
                        age = age,
                        nationality = "Việt Nam",
                        heightCm = heightCm,
                        foot = selectedFoot,
                        position = selectedPosition,
                        pac = pac,
                        sho = sho,
                        pas = pas,
                        dri = dri,
                        sta = sta,
                        def = def,
                        startingClub = selectedClub
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg)
            ) {
                Icon(Icons.Default.Handshake, contentDescription = "Sign", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("KÝ HỢP ĐỒNG VỚI ${selectedClub.shortName.uppercase()}", fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StatSliderItem(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.width(130.dp), fontSize = 12.sp, color = TextSecondary)
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toDouble()) },
            valueRange = 25f..55f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(thumbColor = GoldPrimary, activeTrackColor = GoldPrimary)
        )
        Text(
            text = value.toInt().toString(),
            modifier = Modifier.width(36.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = GoldPrimary
        )
    }
}
