package com.example.roadtolegend.engine

import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.model.AITransferRecord
import com.example.roadtolegend.model.AgentTier
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.Contract
import com.example.roadtolegend.model.LoanRecord
import com.example.roadtolegend.model.NewsItem
import com.example.roadtolegend.model.SquadRole
import com.example.roadtolegend.model.TransactionType
import com.example.roadtolegend.model.TransferOffer
import com.example.roadtolegend.model.TransferOfferStatus
import java.util.UUID
import kotlin.random.Random

object TransferMarketEngine {

    /**
     * Sinh các lời đề nghị chuyển nhượng hoặc mượn cầu thủ đến người chơi
     * chỉ diễn ra khi CỬA SỔ CHUYỂN NHƯỢNG đang mở.
     */
    fun evaluateAndGeneratePlayerOffers(
        state: CareerState,
        random: Random = Random
    ) {
        if (!TransferConfig.isTransferWindowOpen(state.currentWeek)) {
            state.isTransferWindowOpen = false
            state.isDeadlineDay = false
            return
        }

        state.isTransferWindowOpen = true
        state.isDeadlineDay = TransferConfig.isDeadlineDay(state.currentWeek)

        // Không tạo quá 4 lời đề nghị cùng lúc để tránh làm phiền
        if (state.incomingOffers.count { it.isPendingPlayerAction } >= 4) {
            return
        }

        val player = state.player
        val currentClub = state.currentClub
        val agent = state.agent
        val marketValue = TransferConfig.calculateMarketValue(player, state.currentContract.totalWeeksRemaining)

        // Tỉ lệ cơ sở nhận được offer trong tuần (0 - 100)
        var offerChance = 20
        if (player.form >= 80) offerChance += 25
        if (player.careerStats.goals >= 5 || player.careerStats.assists >= 4) offerChance += 20
        if (state.isTransferListed) offerChance += 35 // Đã gửi đơn xin đi
        if (state.currentContract.isExpiringSoon) offerChance += 30 // Sắp hết hạn hợp đồng
        if (state.isDeadlineDay) offerChance += 30 // Ngày chốt sổ nhộn nhịp

        // Điểm thưởng từ mạng lưới người đại diện
        offerChance += when (agent.tier) {
            AgentTier.NONE -> 0
            AgentTier.ROOKIE -> 15
            AgentTier.EXPERIENCED -> 30
            AgentTier.ELITE -> 45
            AgentTier.SUPER_AGENT -> 60
        }

        if (random.nextInt(100) >= offerChance.coerceAtMost(90)) {
            return
        }

        // Chọn CLB tiềm năng đưa ra lời đề nghị
        val candidateClubs = mutableListOf<Club>()
        candidateClubs.addAll(DefaultDatabase.createVLeague1Clubs())
        candidateClubs.addAll(DefaultDatabase.createVLeague2Clubs())

        // Nếu Agent cấp cao hoặc cầu thủ OVR cao -> mở rộng sang CLB Châu Âu / Anh
        if (agent.tier == AgentTier.ELITE || agent.tier == AgentTier.SUPER_AGENT || player.ovr >= 70) {
            candidateClubs.addAll(DefaultDatabase.createChampionshipClubs())
            if (player.ovr >= 78) {
                candidateClubs.addAll(DefaultDatabase.createPremierLeagueClubs())
            }
        }

        val eligibleClubs = candidateClubs.filter { it.id != currentClub.id }
        if (eligibleClubs.isEmpty()) return

        val buyerClub = eligibleClubs[random.nextInt(eligibleClubs.size)]

        // Xác định loại hợp đồng: Chuyển nhượng mua đứt hay Mượn cầu thủ (Loan)
        val isLoanOffer = (player.age <= 21 && player.ovr < buyerClub.squadOvrAverage - 2) || (random.nextInt(100) < 25)

        val fee = if (isLoanOffer) 0L else (marketValue * (0.85 + random.nextDouble(0.40))).toLong()
        val offeredWage = TransferConfig.calculateRecommendedWage(
            playerOvr = player.ovr,
            playerReputation = player.domesticReputation,
            clubTier = buyerClub.tier,
            clubBudget = buyerClub.budget
        )
        val offeredBonus = (offeredWage * random.nextInt(6, 16)).coerceAtLeast(3_000L)

        val offeredRole = when {
            player.ovr >= buyerClub.squadOvrAverage + 3 -> SquadRole.STAR
            player.ovr >= buyerClub.squadOvrAverage -> SquadRole.KEY_PLAYER
            player.ovr >= buyerClub.squadOvrAverage - 3 -> SquadRole.ROTATION
            else -> SquadRole.PROSPECT
        }

        val offer = TransferOffer(
            id = UUID.randomUUID().toString(),
            buyingClubId = buyerClub.id,
            buyingClubName = buyerClub.name,
            buyingClubShortName = buyerClub.shortName,
            sellingClubId = currentClub.id,
            sellingClubName = currentClub.name,
            transferFee = fee,
            offeredWage = offeredWage,
            offeredSigningBonus = offeredBonus,
            offeredContractYears = if (isLoanOffer) 1 else random.nextInt(2, 5),
            offeredRole = offeredRole,
            offeredBonusPerGoal = (offeredWage * 0.18).toLong().coerceAtLeast(100L),
            offeredBonusPerAssist = (offeredWage * 0.10).toLong().coerceAtLeast(50L),
            offeredReleaseClause = if (buyerClub.tier == 1) fee * 2 else null,
            status = TransferOfferStatus.APPROVED_AWAITING_PLAYER,
            isLoan = isLoanOffer,
            loanWageSharePercent = if (isLoanOffer) random.nextInt(70, 101) else 100,
            loanBuyOptionFee = if (isLoanOffer && random.nextBoolean()) (marketValue * 1.2).toLong() else null,
            canRecall = isLoanOffer && random.nextBoolean(),
            loanDurationWeeks = if (isLoanOffer) 13 else 26,
            createdWeek = state.currentWeek,
            expiresInWeeks = if (state.isDeadlineDay) 1 else 2,
            isDeadlineDeal = state.isDeadlineDay,
            scoutNote = if (isLoanOffer) {
                "${buyerClub.shortName} muốn mượn bạn để tăng cường hàng công và cam kết trao cơ hội thi đấu thường xuyên."
            } else {
                "${buyerClub.shortName} rất ấn tượng với chỉ số OVR ${player.ovr} và sẵn sàng chi trả mức lương hấp dẫn."
            }
        )

        state.incomingOffers.add(0, offer)
        agent.totalOffersBrought++

        // Ghi tin tức thông báo
        val newsHeadline = if (isLoanOffer) {
            "TIN CHUYỂN NHƯỢNG: ${buyerClub.shortName} gửi đề nghị mượn tài năng trẻ ${player.name}!"
        } else {
            "HOT: ${buyerClub.shortName} chính thức gửi lời đề nghị chiêu mộ ${player.name} với giá trị hấp dẫn!"
        }
        state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), state.currentWeek, newsHeadline, offer.scoutNote, "TRANSFER", "Chuyển Nhượng 24h", 1, false, buyerClub.id))
    }

    /**
     * Hoàn tất thương vụ chuyển nhượng chính thức sang CLB mới.
     */
    fun executePlayerTransfer(
        state: CareerState,
        offer: TransferOffer,
        finalContract: Contract
    ): Boolean {
        val player = state.player
        val oldClub = state.currentClub
        val agent = state.agent

        // 1. Tìm thông tin CLB mua trong cơ sở dữ liệu
        val allClubs = DefaultDatabase.createVLeague2Clubs() +
                DefaultDatabase.createVLeague1Clubs() +
                DefaultDatabase.createChampionshipClubs() +
                DefaultDatabase.createPremierLeagueClubs()

        val newClub = allClubs.find { it.id == offer.buyingClubId } ?: return false

        // 2. Chuyển đổi giải đấu nếu sang giải khác
        if (newClub.leagueId != state.currentLeague.id) {
            state.currentLeague = DefaultDatabase.initializeLeague(newClub.leagueId)
        }

        state.currentClub = newClub
        state.currentContract = finalContract
        state.isTransferListed = false
        state.transferRequestSubmitted = false

        // 3. Reset quan hệ với HLV về mức trung tính kỳ vọng mới
        player.managerTrust = when (finalContract.squadRole) {
            SquadRole.STAR -> 85
            SquadRole.KEY_PLAYER -> 75
            SquadRole.ROTATION -> 65
            SquadRole.PROSPECT -> 55
            SquadRole.BACKUP -> 45
        }
        player.teamChemistry = 45 // Cần thời gian hòa nhập đội mới
        player.morale = (player.morale + 10).coerceAtMost(100)

        // 4. Nhận tiền lót tay (Signing Bonus) trừ đi hoa hồng của Agent
        val rawBonus = finalContract.signingBonus
        val agentCommission = (rawBonus * (agent.tier.commissionPercent / 100.0)).toLong()
        val netSigningBonus = rawBonus - agentCommission

        player.balance += netSigningBonus
        agent.extraWagesSecured += (finalContract.weeklyWage * 26)

        // Ghi sổ tài chính
        state.financeTransactions.add(
            0,
            com.example.roadtolegend.model.FinanceTransaction(
                id = UUID.randomUUID().toString(),
                week = state.currentWeek,
                season = state.seasonNumber,
                type = TransactionType.SIGNING_BONUS,
                amount = netSigningBonus,
                balanceAfter = player.balance,
                descriptionVi = "Tiền thưởng ký hợp đồng với ${newClub.shortName} (đã trừ hoa hồng agent)"
            )
        )

        if (agentCommission > 0) {
            state.financeTransactions.add(
                0,
                com.example.roadtolegend.model.FinanceTransaction(
                    id = UUID.randomUUID().toString(),
                    week = state.currentWeek,
                    season = state.seasonNumber,
                    type = TransactionType.AGENT_COMMISSION,
                    amount = agentCommission,
                    balanceAfter = player.balance,
                    descriptionVi = "Trích ${agent.tier.commissionPercent}% hoa hồng cho đại diện ${agent.name}"
                )
            )
        }

        // 5. Cập nhật trạng thái offer
        offer.status = TransferOfferStatus.COMPLETED

        // 6. Ghi nhận lịch sử và tin tức bom tấn
        val transferNews = "CHÍNH THỨC: ${player.name} chia tay ${oldClub.shortName} để gia nhập ${newClub.name} với mức phí thỏa thuận!"
        state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), state.currentWeek, transferNews, "Cầu thủ cam kết hợp đồng ${finalContract.contractYearsRemaining} năm với mức đãi ngộ hấp dẫn.", "TRANSFER", "Chính Thức CLB", 1, false, newClub.id))
        player.addMilestoneIfNew("Chuyển nhượng sang ${newClub.shortName}", "Gia nhập ${newClub.name} với mức phí chuyển nhượng chuyên nghiệp.", "Mùa ${state.seasonNumber} - Tuần ${state.currentWeek}", "✈️")

        return true
    }

    /**
     * Hoàn tất việc đi mượn (Loan).
     */
    fun executePlayerLoan(
        state: CareerState,
        offer: TransferOffer
    ): Boolean {
        val player = state.player
        val parentClub = state.currentClub

        val allClubs = DefaultDatabase.createVLeague2Clubs() +
                DefaultDatabase.createVLeague1Clubs() +
                DefaultDatabase.createChampionshipClubs() +
                DefaultDatabase.createPremierLeagueClubs()

        val loanClub = allClubs.find { it.id == offer.buyingClubId } ?: return false

        state.activeLoan = LoanRecord(
            parentClubId = parentClub.id,
            parentClubName = parentClub.name,
            parentClubShortName = parentClub.shortName,
            loanClubId = loanClub.id,
            loanClubName = loanClub.name,
            loanClubShortName = loanClub.shortName,
            wageSharePercent = offer.loanWageSharePercent,
            buyOptionFee = offer.loanBuyOptionFee,
            canRecallInWinter = offer.canRecall,
            weeksRemaining = offer.loanDurationWeeks
        )

        // Chuyển sang thi đấu cho CLB mượn
        if (loanClub.leagueId != state.currentLeague.id) {
            state.currentLeague = DefaultDatabase.initializeLeague(loanClub.leagueId)
        }
        state.currentClub = loanClub
        player.managerTrust = 75 // CLB mượn cam kết tạo điều kiện ra sân

        offer.status = TransferOfferStatus.COMPLETED

        val loanNews = "CHO MƯỢN: ${player.name} tạm rời ${parentClub.shortName} để khoác áo ${loanClub.shortName} theo dạng cho mượn."
        state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), state.currentWeek, loanNews, "Mục tiêu nhằm tích lũy số phút thi đấu và kinh nghiệm cọ xát đỉnh cao.", "TRANSFER", "Tin Nội Bộ", 1, false, loanClub.id))
        player.addMilestoneIfNew("Chuyển nhượng dạng cho mượn", "Khoác áo ${loanClub.shortName} theo bản hợp đồng cho mượn.", "Tuần ${state.currentWeek}", "🔄")

        return true
    }

    /**
     * Mô phỏng thị trường chuyển nhượng của các CLB máy (AI Transfers)
     * Đảm bảo tính nhất quán: ngân sách CLB hợp lý, đội hình luôn đủ người và có thủ môn.
     */
    fun simulateAITransferMarket(state: CareerState, random: Random = Random) {
        if (!TransferConfig.isTransferWindowOpen(state.currentWeek)) return

        // Mỗi tuần mở cửa sổ có 1 - 3 thương vụ AI diễn ra
        val numDeals = if (state.isDeadlineDay) random.nextInt(2, 5) else random.nextInt(1, 3)
        val allClubs = state.currentLeague.clubs

        for (i in 0 until numDeals) {
            val buyer = allClubs[random.nextInt(allClubs.size)]
            val seller = allClubs[random.nextInt(allClubs.size)]
            if (buyer.id == seller.id || buyer.id == state.currentClub.id) continue

            val sellerPlayers = com.example.roadtolegend.data.SquadDatabase.getSquadForClub(seller.id)
            // Không chuyển nhượng nếu CLB bán chỉ còn đúng 1 thủ môn
            val outfieldPlayers = sellerPlayers.filter { it.position != com.example.roadtolegend.model.Position.GK }
            if (outfieldPlayers.isEmpty()) continue

            val targetPlayer = outfieldPlayers[random.nextInt(outfieldPlayers.size)]
            val fee = (targetPlayer.marketValue * random.nextDouble(0.85, 1.35)).toLong()

            // Kiểm tra ngân sách CLB mua
            if (buyer.budget >= fee) {
                buyer.budget -= fee
                seller.budget += fee

                val record = AITransferRecord(
                    id = UUID.randomUUID().toString(),
                    season = state.seasonNumber,
                    week = state.currentWeek,
                    playerName = targetPlayer.name,
                    positionCode = targetPlayer.position.code,
                    ovr = targetPlayer.ovr,
                    fromClubId = seller.id,
                    fromClubName = seller.shortName,
                    toClubId = buyer.id,
                    toClubName = buyer.shortName,
                    fee = fee
                )
                state.aiCompletedTransfers.add(0, record)

                // Thêm bản tin chuyển nhượng AI nổi bật
                if (targetPlayer.ovr >= 65 || state.isDeadlineDay) {
                    val headline = "THƯƠNG VỤ HOÀN TẤT: ${buyer.shortName} chiêu mộ thành công ${targetPlayer.name} từ ${seller.shortName}!"
                    state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), state.currentWeek, headline, "Mức phí chuyển nhượng được tiết lộ vào khoảng €${fee}.", "TRANSFER", "Thị Trường AI", 2, false, buyer.id))
                }
            }
        }
    }
}
