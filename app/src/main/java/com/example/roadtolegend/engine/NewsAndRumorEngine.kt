package com.example.roadtolegend.engine

import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.NewsItem
import java.util.UUID
import kotlin.random.Random

object NewsAndRumorEngine {

    private val reputableSources = listOf(
        "Báo Thể Thao & Văn Hóa",
        "Kênh Thể Thao Quốc Gia",
        "Tạp Chí Bóng Đá Chuyên Nghiệp",
        "Nhà Báo Uy Tín David Ornstein VN"
    )

    private val tabloidSources = listOf(
        "Kênh Hóng Biến Sân Cỏ",
        "Tin Nhanh Chuyển Nhượng 24/7",
        "Báo Lá Cải Đất Thủ",
        "Diễn Đàn Người Hâm Mộ Bóng Đá"
    )

    /**
     * Sinh tin đồn chuyển nhượng về cầu thủ người chơi.
     * Tin đồn có thể là "thật" (dẫn đến lời đề nghị thật tuần tới) hoặc "tin vịt" (thổi phồng).
     */
    fun rollTransferRumor(state: CareerState, random: Random = Random) {
        // Chỉ xuất hiện tin đồn trong hoặc ngay trước kỳ chuyển nhượng
        if (!TransferConfig.isTransferWindowOpen(state.currentWeek) && state.currentWeek !in 22..23 && state.currentWeek !in 10..11) {
            return
        }

        // 35% cơ hội có tin đồn mỗi tuần
        if (random.nextInt(100) > 35) return

        val player = state.player
        val currentClub = state.currentClub
        val allClubs = DefaultDatabase.createVLeague1Clubs() +
                DefaultDatabase.createVLeague2Clubs() +
                (if (player.ovr >= 68) DefaultDatabase.createChampionshipClubs() else emptyList())

        val interestedClubs = allClubs.filter { it.id != currentClub.id }
        if (interestedClubs.isEmpty()) return

        val targetClub = interestedClubs[random.nextInt(interestedClubs.size)]
        val isReputable = random.nextBoolean()
        val source = if (isReputable) {
            reputableSources[random.nextInt(reputableSources.size)]
        } else {
            tabloidSources[random.nextInt(tabloidSources.size)]
        }

        val reliabilityTier = if (isReputable) 2 else 3
        // Nguồn uy tín thì tỉ lệ thật cao (75%), nguồn lá cải tỉ lệ thật thấp (30%)
        val isRumorTrue = if (isReputable) random.nextInt(100) < 75 else random.nextInt(100) < 30

        val headlineTemplates = listOf(
            "TIN ĐỒN: ${targetClub.shortName} đang theo dõi sát sao sự tiến bộ của ${player.name}!",
            "NÓNG: Người đại diện của ${player.name} bị bắt gặp ăn tối cùng phái đoàn ${targetClub.shortName}?",
            "TRUYỀN THÔNG: ${targetClub.name} sẵn sàng chi đậm để phá vỡ hợp đồng của ${player.name}!",
            "TIN CHUYỂN NHƯỢNG: Liệu ${player.name} có rời ${currentClub.shortName} để cập bến ${targetClub.shortName}?",
            "GÓC PHÂN TÍCH: Tại sao ${player.name} là mảnh ghép hoàn hảo cho lối chơi của ${targetClub.shortName}."
        )

        val bodyTemplates = listOf(
            "Các tuyển trạch viên của ${targetClub.shortName} đã có mặt trên khán đài ở những trận gần nhất để chấm điểm trực tiếp phong độ của cầu thủ ${player.age} tuổi.",
            "Nguồn tin thân cận cho biết ban lãnh đạo ${targetClub.shortName} coi ${player.name} là mục tiêu hàng đầu nhằm củng cố đội hình trong phiên chợ sắp tới.",
            "Dù HLV trưởng ${currentClub.shortName} khẳng định cầu thủ không phải để bán, sức ép tài chính và lời mời gọi hấp dẫn có thể thay đổi cục diện.",
            "Cộng đồng người hâm mộ đang bàn tán sôi nổi về khả năng ngôi sao trẻ này sẽ chuyển sang một bến đỗ mới giàu tham vọng hơn."
        )

        val headline = headlineTemplates[random.nextInt(headlineTemplates.size)]
        val body = bodyTemplates[random.nextInt(bodyTemplates.size)]

        state.newsFeed.add(
            0,
            NewsItem(
                id = UUID.randomUUID().toString(),
                week = state.currentWeek,
                headline = headline,
                body = body,
                category = "RUMOR",
                sourceName = source,
                reliabilityTier = reliabilityTier,
                isRead = false,
                linkedClubId = targetClub.id,
                isRumorTrue = isRumorTrue,
                rumorTargetClubId = targetClub.id
            )
        )

        // Tin đồn tác động nhẹ lên Morale và lòng tin HLV
        if (isRumorTrue) {
            player.morale = (player.morale + 3).coerceAtMost(100)
        } else {
            // Tin đồn vô căn cứ từ báo lá cải làm xao nhãng
            player.managerTrust = (player.managerTrust - 2).coerceAtLeast(20)
        }
    }
}
