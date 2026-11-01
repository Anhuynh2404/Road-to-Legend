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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.engine.TransferConfig
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.TransferOffer
import com.example.roadtolegend.model.TransferOfferStatus
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
fun TransferScreen(
    careerState: CareerState,
    viewModel: CareerViewModel,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember { NumberFormat.getNumberInstance(Locale.GERMANY) }
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Hộp thư, 1: Thị trường AI, 2: Hành động
    val pendingOffers = careerState.incomingOffers.filter { it.isPendingPlayerAction }

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
                        text = "THỊ TRƯỜNG CHUYỂN NHƯỢNG",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Cơ hội thăng tiến, chuyển nhượng và cho mượn",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // 2. Banner Trạng thái Cửa sổ chuyển nhượng & Deadline Day
        item {
            val isWindowOpen = careerState.isTransferWindowOpen
            val isDeadline = careerState.isDeadlineDay

            val bannerBg = when {
                isDeadline -> GoalCoral.copy(alpha = 0.20f)
                isWindowOpen -> PitchGreen.copy(alpha = 0.15f)
                else -> DarkSurfaceVariant
            }
            val bannerBorder = when {
                isDeadline -> GoalCoral
                isWindowOpen -> PitchGreen
                else -> DarkCardBorder
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = bannerBg,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, bannerBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when {
                            isDeadline -> Icons.Default.LocalFireDepartment
                            isWindowOpen -> Icons.Default.SwapHoriz
                            else -> Icons.Default.HourglassBottom
                        },
                        contentDescription = null,
                        tint = when {
                            isDeadline -> GoalCoral
                            isWindowOpen -> PitchGreen
                            else -> GoldLight
                        },
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = when {
                                isDeadline -> "🔥 DEADLINE DAY: HÔM NAY LÀ NGÀY CHỐT SỔ!"
                                isWindowOpen -> "CỬA SỔ CHUYỂN NHƯỢNG ĐANG MỞ"
                                else -> "CỬA SỔ CHUYỂN NHƯỢNG HIỆN ĐANG ĐÓNG"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = when {
                                isDeadline -> GoalCoral
                                isWindowOpen -> PitchGreen
                                else -> TextPrimary
                            }
                        )
                        val subText = when {
                            isDeadline -> "Các CLB đang gấp rút chốt các thương vụ bom tấn trước khi thị trường đóng cửa."
                            careerState.currentWeek in 1..4 -> "Kỳ chuyển nhượng hè: Diễn ra từ Tuần 1 đến Tuần 4 (Tuần 4 là Deadline Day)."
                            careerState.currentWeek in 12..15 -> "Kỳ chuyển nhượng đông: Diễn ra từ Tuần 12 đến Tuần 15 (Tuần 15 là Deadline Day)."
                            careerState.currentWeek < 12 -> "Kỳ chuyển nhượng mùa đông sẽ mở vào Tuần 12 (còn ${12 - careerState.currentWeek} tuần)."
                            else -> "Kỳ chuyển nhượng cuối mùa sẽ mở vào Tuần 24 (còn ${24 - careerState.currentWeek} tuần)."
                        }
                        Text(text = subText, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
                    }
                }
            }
        }

        // 3. Thanh chuyển phân mục (Segmented Tabs)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Hộp thư đề nghị", fontSize = 12.sp)
                            if (pendingOffers.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = GoalCoral,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "${pendingOffers.size}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    label = { Text("Thị trường AI & Tự do", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )

                FilterChip(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    label = { Text("Hành động của bạn", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldPrimary,
                        selectedLabelColor = DarkBg,
                        containerColor = DarkSurface,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        // 4. Nội dung theo phân mục
        when (selectedSection) {
            0 -> {
                // HỘP THƯ LỜI ĐỀ NGHỊ
                if (pendingOffers.isEmpty()) {
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
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Mail, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Chưa có lời đề nghị nào đang chờ phản hồi", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Hãy thi đấu xuất sắc, cải thiện OVR và thuê người đại diện cấp cao để nhận thêm nhiều lời mời gọi hấp dẫn trong kỳ chuyển nhượng.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                } else {
                    items(pendingOffers.size) { index ->
                        val offer = pendingOffers[index]
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.6f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (offer.isLoan) "ĐỀ NGHỊ MƯỢN CẦU THỦ" else "ĐỀ NGHỊ MUA ĐỨT",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = if (offer.isLoan) WarningAmber else PitchGreen
                                            )
                                            if (offer.isDeadlineDeal) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("🔥 DEADLINE", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoalCoral)
                                            }
                                        }
                                        Text(
                                            text = offer.buyingClubName,
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
                                            text = "Còn ${offer.expiresInWeeks} tuần",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldLight,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Chi tiết đãi ngộ
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
                                            Text("Lương đề nghị", fontSize = 9.sp, color = TextMuted)
                                            Text("€${currencyFormatter.format(offer.offeredWage)}/tuần", fontSize = 12.sp, fontWeight = FontWeight.Black, color = PitchGreen)
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        color = DarkSurfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text(if (offer.isLoan) "Chia lương" else "Phí chuyển nhượng", fontSize = 9.sp, color = TextMuted)
                                            val detail = if (offer.isLoan) "${offer.loanWageSharePercent}% lương" else "€${currencyFormatter.format(offer.transferFee)}"
                                            Text(detail, fontSize = 12.sp, fontWeight = FontWeight.Black, color = GoldLight)
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        color = DarkSurfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(8.dp)) {
                                            Text("Vai trò cam kết", fontSize = 9.sp, color = TextMuted)
                                            Text(offer.offeredRole.titleVi, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Lời nhắn trinh sát
                                Text(
                                    text = "Ghi chú: ${offer.scoutNote}",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Nút hành động
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (!offer.isLoan) {
                                        Button(
                                            onClick = { viewModel.startOfferNegotiation(offer) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ĐÀM PHÁN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { viewModel.acceptTransferOfferDirectly(offer) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = PitchGreen, contentColor = DarkBg),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ĐỒNG Ý", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    } else {
                                        Button(
                                            onClick = { viewModel.acceptLoanOffer(offer) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = DarkBg),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ĐỒNG Ý ĐI MƯỢN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.rejectTransferOffer(offer) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, tint = GoalCoral, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("TỪ CHỐI", fontSize = 11.sp, color = GoalCoral)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // THỊ TRƯỜNG AI & CẦU THỦ TỰ DO
                item {
                    Text("CÁC THƯƠNG VỤ AI NỔI BẬT GẦN ĐÂY", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (careerState.aiCompletedTransfers.isEmpty()) {
                        Text("Chưa có thương vụ chuyển nhượng nào hoàn tất trong kỳ này.", fontSize = 11.sp, color = TextMuted)
                    } else {
                        careerState.aiCompletedTransfers.take(10).forEach { deal ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(deal.playerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = DarkSurfaceVariant,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text("${deal.positionCode} • ${deal.ovr}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldPrimary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                        Text("${deal.fromClubName} ➔ ${deal.toClubName}", fontSize = 11.sp, color = TextSecondary)
                                    }

                                    Text(
                                        text = if (deal.isFreeAgentSigning) "Chuyển nhượng tự do" else "€${currencyFormatter.format(deal.fee)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (deal.isFreeAgentSigning) GoldLight else PitchGreen
                                    )
                                }
                            }
                        }
                    }
                }

                // Nếu người chơi đang là Cầu thủ tự do -> Cho phép chọn gia nhập CLB
                if (careerState.currentContract.isFreeAgent) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("DANH SÁCH CLB ĐANG TUYỂN DỤNG CẦU THỦ TỰ DO", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoalCoral)
                        Spacer(modifier = Modifier.height(8.dp))

                        val availableClubs = DefaultDatabase.createVLeague2Clubs() + DefaultDatabase.createVLeague1Clubs()
                        availableClubs.forEach { club ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(club.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                        Text("Giải đấu: ${club.leagueId.uppercase()} • Hạng ${club.tier}", fontSize = 11.sp, color = TextSecondary)
                                    }

                                    Button(
                                        onClick = { viewModel.signFreeAgentOffer(club) },
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("KÝ HỢP ĐỒNG", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // HÀNH ĐỘNG CÁ NHÂN
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = GoalCoral, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("GỬI YÊU CẦU CHUYỂN NHƯỢNG (TRANSFER REQUEST)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Nếu bạn không hài lòng với vai trò hoặc mong muốn tìm bến đỗ mới, bạn có thể chính thức gửi đơn xin chuyển nhượng. Hành động này sẽ làm giảm lòng tin của HLV và tinh thần toàn đội, nhưng sẽ giúp CLB đưa bạn vào danh sách bán.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.submitTransferRequest() },
                                enabled = !careerState.transferRequestSubmitted && !careerState.currentContract.isFreeAgent,
                                colors = ButtonDefaults.buttonColors(containerColor = GoalCoral, contentColor = Color.White),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    if (careerState.transferRequestSubmitted) "ĐÃ NỘP ĐƠN XIN ĐI" else "NỘP ĐƠN XIN CHUYỂN NHƯỢNG",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = GoldLight, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("YÊU CẦU CHO MƯỢN (LOAN REQUEST)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Thích hợp cho các cầu thủ trẻ hoặc chưa có nhiều cơ hội đá chính. HLV sẽ chủ động liên hệ với các CLB giải hạng dưới để tìm kiếm hợp đồng cho mượn ngắn hạn giúp bạn tích lũy kinh nghiệm.",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.submitLoanRequest() },
                                enabled = careerState.activeLoan == null && !careerState.currentContract.isFreeAgent,
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant, contentColor = GoldPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("YÊU CẦU ĐI MƯỢN ĐỂ RA SÂN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Nếu đang đi mượn
                if (careerState.activeLoan != null) {
                    val loan = careerState.activeLoan!!
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PitchGreen)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("HỢP ĐỒNG CHO MƯỢN HIỆN TẠI", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PitchGreen)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("CLB mẹ: ${loan.parentClubName}", fontSize = 12.sp, color = TextPrimary)
                                Text("Đang thi đấu cho: ${loan.loanClubName} (${loan.weeksRemaining} tuần còn lại)", fontSize = 12.sp, color = TextSecondary)

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (loan.canRecallInWinter && careerState.currentWeek in TransferConfig.WINTER_WINDOW_START_WEEK..TransferConfig.WINTER_WINDOW_END_WEEK) {
                                        Button(
                                            onClick = { viewModel.recallLoanInWinter() },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBg),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("TRIỆU HỒI VỀ CLB MẸ", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (loan.buyOptionFee != null) {
                                        Button(
                                            onClick = { viewModel.exerciseBuyOptionOnLoan() },
                                            colors = ButtonDefaults.buttonColors(containerColor = PitchGreen, contentColor = DarkBg),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("MUA ĐỨT (€${currencyFormatter.format(loan.buyOptionFee)})", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
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
