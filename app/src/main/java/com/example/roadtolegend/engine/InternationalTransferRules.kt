package com.example.roadtolegend.engine

import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.model.Agent
import com.example.roadtolegend.model.AgentTier
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.NewsItem
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.SquadRole
import com.example.roadtolegend.model.TransferOffer
import com.example.roadtolegend.model.TransferOfferStatus
import java.util.UUID
import kotlin.random.Random

/**
 * QUY TẮC CHUYỂN NHƯỢNG QUỐC TẾ & XUẤT NGOẠI
 * Quản lý:
 * 1. Đánh giá Giấy phép lao động (Work Permit / Visa) theo quy định các quốc gia
 * 2. Hiệu ứng Hòa nhập văn hóa & Ngôn ngữ (Culture Shock / Adaptation)
 * 3. Đại diện thăm dò thị trường nước ngoài (Scout Foreign Market)
 * 4. Tỉ giá quy đổi & Hệ số kinh tế của các giải đấu
 *
 * NGUYÊN TẮC BẮT BUỘC: Không hiển thị số % hay xác suất cho người dùng,
 * mọi phản hồi đều diễn đạt bằng câu chữ bóng đá thực tế.
 */
object InternationalTransferRules {

    data class WorkPermitResult(
        val isApproved: Boolean,
        val reasonVi: String? = null
    )

    data class AdaptationImpact(
        val moraleDelta: Int,
        val teamChemistryStart: Int,
        val descriptionVi: String
    )

    /**
     * Kiểm tra điều kiện cấp Giấy phép lao động (Work Permit) / Visa cầu thủ chuyên nghiệp.
     */
    fun evaluateWorkPermit(
        player: Player,
        buyingClub: Club,
        agent: Agent
    ): WorkPermitResult {
        // Cùng quốc tịch thì tự động có giấy phép
        if (buyingClub.country == player.nationality) {
            return WorkPermitResult(isApproved = true)
        }

        // Ưu đãi giảm bớt yêu cầu nhờ năng lực và quan hệ của Agent
        val agentOvrBonus = when (agent.tier) {
            AgentTier.SUPER_AGENT -> 4
            AgentTier.ELITE -> 2
            else -> 0
        }
        val agentRepBonus = when (agent.tier) {
            AgentTier.SUPER_AGENT -> 10
            AgentTier.ELITE -> 5
            else -> 0
        }

        val effectiveOvr = player.ovr + agentOvrBonus
        val effectiveRep = player.globalReputation + agentRepBonus
        val matchesPlayed = player.careerStats.matchesPlayed

        return when (buyingClub.country) {
            "Thái Lan" -> {
                // Suất ngoại binh Đông Nam Á (ASEAN Quota)
                if (effectiveOvr >= 58 || player.domesticReputation >= 20 || matchesPlayed >= 10) {
                    WorkPermitResult(isApproved = true)
                } else {
                    WorkPermitResult(
                        isApproved = false,
                        reasonVi = "Hạn ngạch ngoại binh ASEAN của Thai League từ chối hồ sơ: Cầu thủ cần có thêm số trận thi đấu chuyên nghiệp hoặc đạt OVR tối thiểu 58."
                    )
                }
            }

            "Hàn Quốc" -> {
                // Hạn ngạch cầu thủ Châu Á (K League Asian Quota)
                if (effectiveOvr >= 65 && (effectiveRep >= 12 || matchesPlayed >= 15)) {
                    WorkPermitResult(isApproved = true)
                } else {
                    WorkPermitResult(
                        isApproved = false,
                        reasonVi = "Hiệp hội Bóng đá Hàn Quốc (KFA) từ chối thị thực lao động E-6: Cầu thủ chưa đạt ngưỡng danh tiếng toàn cầu và kinh nghiệm cọ xát tối thiểu của giải đấu."
                    )
                }
            }

            "Bồ Đào Nha" -> {
                // Cửa ngõ Châu Âu - Thẻ cư trú thể thao ngoài EU
                if (effectiveOvr >= 68 && effectiveRep >= 18) {
                    WorkPermitResult(isApproved = true)
                } else {
                    WorkPermitResult(
                        isApproved = false,
                        reasonVi = "Cơ quan Nhập cư Bồ Đào Nha (AIMA) từ chối cấp thị thực thể thao chuyên nghiệp cho cầu thủ ngoài EU do chưa đạt tiêu chuẩn trình độ quốc tế."
                    )
                }
            }

            "Đức" -> {
                // Quy chuẩn DFL Đức
                if (effectiveOvr >= 74 && effectiveRep >= 32) {
                    WorkPermitResult(isApproved = true)
                } else {
                    WorkPermitResult(
                        isApproved = false,
                        reasonVi = "Cục Nhập cư Liên bang Đức từ chối cấp giấy phép thi đấu Bundesliga: Tiêu chuẩn kỹ thuật và danh tiếng của bạn chưa đáp ứng yêu cầu của giải hàng đầu nước Đức."
                    )
                }
            }

            "Ý" -> {
                // Suất cầu thủ Non-EU khắt khe của Serie A
                if (effectiveOvr >= 74 && effectiveRep >= 35) {
                    WorkPermitResult(isApproved = true)
                } else {
                    WorkPermitResult(
                        isApproved = false,
                        reasonVi = "Ủy ban Olympic Ý (CONI) không phân bổ suất ngoại binh Non-EU: Hạn ngạch năm nay của CLB chỉ ưu tiên những tuyển thủ quốc gia có danh tiếng lớn."
                    )
                }
            }

            "Pháp" -> {
                // Ligue 1 Sports Visa
                if (effectiveOvr >= 73 && effectiveRep >= 30) {
                    WorkPermitResult(isApproved = true)
                } else {
                    WorkPermitResult(
                        isApproved = false,
                        reasonVi = "Bộ Nội vụ Pháp từ chối cấp giấy phép cư trú thể thao: Bạn cần thêm danh tiếng toàn cầu và số trận ra sân cấp độ cao."
                    )
                }
            }

            "Anh" -> {
                // Hệ thống cấp phép GBE (Governing Body Endorsement) cực kỳ khắt khe của FA & Home Office
                if (effectiveOvr >= 77 && effectiveRep >= 42 && matchesPlayed >= 25) {
                    WorkPermitResult(isApproved = true)
                } else {
                    WorkPermitResult(
                        isApproved = false,
                        reasonVi = "Hệ thống tính điểm GBE của Bộ Nội vụ Vương quốc Anh không thông qua: Cầu thủ chưa đạt đủ điểm số xếp hạng quốc tế và số trận thi đấu theo quy chuẩn Ngoại Hạng Anh."
                    )
                }
            }

            else -> {
                // Các quốc gia khác mặc định yêu cầu OVR 60+
                if (effectiveOvr >= 60) {
                    WorkPermitResult(isApproved = true)
                } else {
                    WorkPermitResult(
                        isApproved = false,
                        reasonVi = "Đại sứ quán từ chối cấp thị thực lao động: Chưa đạt ngưỡng trình độ chuyên môn tối thiểu."
                    )
                }
            }
        }
    }

    /**
     * Tính toán cú sốc hòa nhập văn hóa (Culture Shock) khi cầu thủ chuyển sang quốc gia mới.
     */
    fun calculateAdaptation(
        player: Player,
        oldCountry: String,
        newCountry: String,
        agent: Agent,
        hasSameNationalityTeammate: Boolean = false
    ): AdaptationImpact {
        if (oldCountry == newCountry) {
            return AdaptationImpact(
                moraleDelta = 8,
                teamChemistryStart = 45,
                descriptionVi = "Thi đấu trong nước giúp bạn nhanh chóng hòa nhập với đồng đội mới."
            )
        }

        // Tác động văn hóa cơ bản theo quốc gia
        var baseMoraleLoss: Int
        var baseChemistry: Int
        val baseDescription: String

        when (newCountry) {
            "Thái Lan" -> {
                baseMoraleLoss = -4
                baseChemistry = 40
                baseDescription = "Khí hậu và văn hóa Đông Nam Á tương đồng, bạn chỉ mất ít thời gian làm quen với nhịp độ bóng đá nhanh của Thái Lan."
            }

            "Hàn Quốc" -> {
                baseMoraleLoss = -8
                baseChemistry = 35
                baseDescription = "Rào cản tiếng Hàn và tính kỷ luật thép tại K League đòi hỏi bạn phải nỗ lực rất lớn để hòa nhập vào lối chơi tập thể."
            }

            "Bồ Đào Nha" -> {
                baseMoraleLoss = -10
                baseChemistry = 32
                baseDescription = "Lần đầu đặt chân tới Châu Âu, sự khác biệt về lối sống và văn hóa phương Tây khiến bạn hơi bỡ ngỡ trong những tuần đầu."
            }

            "Đức", "Anh" -> {
                baseMoraleLoss = -14
                baseChemistry = 28
                baseDescription = "Thời tiết lạnh giá, ngôn ngữ mới và áp lực truyền thông khổng lồ tạo nên cú sốc ban đầu. Bạn cần nỗ lực khẳng định mình."
            }

            "Ý", "Pháp" -> {
                baseMoraleLoss = -13
                baseChemistry = 30
                baseDescription = "Môi trường chiến thuật cực kỳ chặt chẽ và lối sống mới tại Châu Âu đặt ra thử thách lớn cho sự thích ứng của bạn."
            }

            else -> {
                baseMoraleLoss = -10
                baseChemistry = 32
                baseDescription = "Bạn đang phải thích nghi với môi trường sống và văn hóa hoàn toàn mới lạ."
            }
        }

        // Giảm bớt sốc nhờ người đại diện cấp cao (hỗ trợ nhà ở, phiên dịch, hậu cần)
        when (agent.tier) {
            AgentTier.SUPER_AGENT -> {
                baseMoraleLoss += 6
                baseChemistry += 8
            }
            AgentTier.ELITE -> {
                baseMoraleLoss += 3
                baseChemistry += 4
            }
            AgentTier.EXPERIENCED -> {
                baseMoraleLoss += 1
                baseChemistry += 2
            }
            else -> {}
        }

        // Nếu còn quá trẻ (dưới 20 tuổi) dễ bị nhớ nhà
        if (player.age <= 20) {
            baseMoraleLoss -= 3
        }

        // Nếu có đồng hương trong CLB mới giúp đỡ hòa nhập
        if (hasSameNationalityTeammate) {
            baseMoraleLoss += 4
            baseChemistry += 8
        }

        return AdaptationImpact(
            moraleDelta = baseMoraleLoss.coerceAtMost(5),
            teamChemistryStart = baseChemistry.coerceIn(15, 60),
            descriptionVi = baseDescription
        )
    }

    /**
     * Người chơi chủ động yêu cầu Người đại diện (Agent) thăm dò thị trường nước ngoài.
     */
    fun scoutForeignMarket(
        state: CareerState,
        regionName: String,
        random: Random = Random
    ): Pair<Boolean, String> {
        val player = state.player
        val agent = state.agent
        val currentClub = state.currentClub

        if (state.currentContract.isFreeAgent) {
            return false to "Bạn hiện đang là cầu thủ tự do, hãy tìm bến đỗ trong danh sách chuyển nhượng tự do."
        }

        // Lọc danh sách CLB tiềm năng theo khu vực người chơi chọn
        val candidateClubs: List<Club> = when (regionName) {
            "THAI_LEAGUE" -> DefaultDatabase.createThaiLeagueClubs()
            "K_LEAGUE" -> DefaultDatabase.createKLeagueClubs()
            "PRIMEIRA_LIGA" -> DefaultDatabase.createPrimeiraLigaClubs()
            "EUROPE_TOP5" -> DefaultDatabase.createBundesligaClubs() +
                    DefaultDatabase.createSerieAClubs() +
                    DefaultDatabase.createLigue1Clubs() +
                    DefaultDatabase.createChampionshipClubs() +
                    DefaultDatabase.createPremierLeagueClubs()
            else -> DefaultDatabase.createThaiLeagueClubs() + DefaultDatabase.createKLeagueClubs()
        }.filter { it.id != currentClub.id }

        if (candidateClubs.isEmpty()) {
            return false to "Không tìm thấy CLB phù hợp tại khu vực này."
        }

        // Chọn CLB mục tiêu phù hợp nhất với tầm vóc cầu thủ
        val sortedByFit = candidateClubs.sortedBy { Math.abs(it.squadOvrAverage - player.ovr) }
        val targetClub = sortedByFit.firstOrNull() ?: candidateClubs[random.nextInt(candidateClubs.size)]

        // Đánh giá Giấy phép lao động
        val workPermit = evaluateWorkPermit(player, targetClub, agent)
        if (!workPermit.isApproved) {
            val agentFeedback = "${agent.name} báo cáo: 'Ban tuyển trạch ${targetClub.name} rất đánh giá cao phong cách của bạn, nhưng cơ quan chức năng ${targetClub.country} thông báo: ${workPermit.reasonVi}'"
            return false to agentFeedback
        }

        // Kiểm tra khả năng CLB quan tâm thực tế
        val repDiff = targetClub.reputation - player.domesticReputation
        if (repDiff > 35 && agent.tier <= AgentTier.ROOKIE) {
            return false to "${agent.name} phản hồi: '${targetClub.name} là CLB quá lớn so với danh tiếng hiện tại của bạn. Bạn cần người đại diện đẳng cấp hơn để mở cánh cửa này.'"
        }

        // Tạo lời đề nghị chính thức từ CLB nước ngoài
        val marketValue = TransferConfig.calculateMarketValue(player, state.currentContract.totalWeeksRemaining)
        val fee = (marketValue * (0.90 + random.nextDouble(0.35))).toLong()
        val offeredWage = TransferConfig.calculateRecommendedWage(
            playerOvr = player.ovr,
            playerReputation = player.globalReputation,
            clubTier = targetClub.tier,
            clubBudget = targetClub.budget,
            clubReputation = targetClub.reputation
        )
        val signingBonus = (offeredWage * random.nextInt(8, 16)).coerceAtLeast(5_000L)

        val offerRole = when {
            player.ovr >= targetClub.squadOvrAverage + 2 -> SquadRole.STAR
            player.ovr >= targetClub.squadOvrAverage -> SquadRole.KEY_PLAYER
            player.ovr >= targetClub.squadOvrAverage - 3 -> SquadRole.ROTATION
            else -> SquadRole.PROSPECT
        }

        val offer = TransferOffer(
            id = UUID.randomUUID().toString(),
            buyingClubId = targetClub.id,
            buyingClubName = targetClub.name,
            buyingClubShortName = targetClub.shortName,
            sellingClubId = currentClub.id,
            sellingClubName = currentClub.name,
            transferFee = fee,
            offeredWage = offeredWage,
            offeredSigningBonus = signingBonus,
            offeredContractYears = random.nextInt(2, 5),
            offeredRole = offerRole,
            offeredBonusPerGoal = (offeredWage * 0.15).toLong().coerceAtLeast(100L),
            offeredBonusPerAssist = (offeredWage * 0.10).toLong().coerceAtLeast(60L),
            offeredReleaseClause = fee * 2,
            status = TransferOfferStatus.APPROVED_AWAITING_PLAYER,
            isLoan = false,
            createdWeek = state.currentWeek,
            expiresInWeeks = 2,
            scoutNote = "Sau buổi làm việc với đại diện ${agent.name}, ${targetClub.name} đã chính thức phê duyệt hồ sơ giấy phép lao động và gửi hợp đồng chuyển nhượng!",
            buyingClubCountry = targetClub.country,
            isWorkPermitApproved = true
        )

        state.incomingOffers.add(0, offer)
        agent.totalOffersBrought++

        val newsHeadline = "NÓNG TỪ QUỐC TẾ: ${targetClub.name} (${targetClub.country}) gửi đề nghị chiêu mộ ${player.name}!"
        state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), state.currentWeek, newsHeadline, offer.scoutNote, "TRANSFER", "Thị Trường Quốc Tế", 1, false, targetClub.id))

        return true to "Thành công rực rỡ! Đại diện ${agent.name} đã chốt được lời đề nghị chính thức từ ${targetClub.name} (${targetClub.country}). Vui lòng kiểm tra Hộp thư chuyển nhượng!"
    }
}
