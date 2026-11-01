package com.example.roadtolegend.engine

import com.example.roadtolegend.model.Agent
import com.example.roadtolegend.model.AgentTier
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.Contract
import com.example.roadtolegend.model.NegotiationRoundRecord
import com.example.roadtolegend.model.NegotiationSession
import com.example.roadtolegend.model.NegotiationStatus
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.SquadRole
import com.example.roadtolegend.model.TransferOffer
import java.util.UUID
import kotlin.random.Random

object ContractNegotiationEngine {

    /**
     * Kiểm tra điều kiện mở đàm phán gia hạn hợp đồng với CLB chủ quản.
     * Mở khi: còn dưới 18 tháng (39 tuần) hợp đồng, hoặc phong độ/danh tiếng tăng cao, hoặc OVR vượt trội.
     */
    fun canInitiateRenewal(state: CareerState): Pair<Boolean, String> {
        if (state.currentContract.isFreeAgent) {
            return false to "Bạn hiện đang là cầu thủ tự do, hãy tìm CLB mới từ thị trường chuyển nhượng!"
        }
        val weeksRemaining = state.currentContract.totalWeeksRemaining
        val player = state.player
        val club = state.currentClub

        if (weeksRemaining <= 39) {
            return true to "Hợp đồng còn dưới 18 tháng ($weeksRemaining tuần), CLB sẵn sàng thảo luận gia hạn."
        }
        if (player.form >= 80) {
            return true to "Phong độ thi đấu xuất sắc (${player.form}/100) khiến ban lãnh đạo mong muốn trói chân bạn lâu dài."
        }
        if (player.ovr >= club.squadOvrAverage + 3) {
            return true to "Trình độ của bạn (OVR ${player.ovr}) vượt trội so với mặt bằng CLB, mở đàm phán nâng tầm hợp đồng."
        }
        if (player.domesticReputation >= 45) {
            return true to "Danh tiếng và tầm ảnh hưởng của bạn đủ lớn để yêu cầu điều chỉnh mức đãi ngộ."
        }
        return false to "Hợp đồng của bạn còn dài ($weeksRemaining tuần) và CLB chưa có kế hoạch mở gia hạn lúc này."
    }

    /**
     * Kiểm tra điều khoản phá vỡ hợp đồng (Release Clause).
     * Nếu mức phí đưa ra >= release clause, CLB buộc phải cho phép cầu thủ đàm phán cá nhân.
     */
    fun isReleaseClauseTriggered(offerFee: Long, contract: Contract): Boolean {
        val clause = contract.releaseClause ?: return false
        return offerFee >= clause
    }

    /**
     * Bắt đầu một phiên đàm phán hợp đồng mới (gia hạn hoặc ký hợp đồng mới).
     */
    fun startNegotiation(
        player: Player,
        club: Club,
        currentContract: Contract?,
        linkedOffer: TransferOffer?,
        agent: Agent,
        isRenewal: Boolean
    ): NegotiationSession {
        val recommendedWage = TransferConfig.calculateRecommendedWage(
            playerOvr = player.ovr,
            playerReputation = player.domesticReputation,
            clubTier = club.tier,
            clubBudget = club.budget
        )

        val initialClubWage = if (linkedOffer != null) {
            linkedOffer.offeredWage
        } else if (currentContract != null) {
            (currentContract.weeklyWage * 1.15).toLong().coerceAtLeast(recommendedWage)
        } else {
            recommendedWage
        }

        val initialSigningBonus = if (linkedOffer != null) {
            linkedOffer.offeredSigningBonus
        } else {
            (initialClubWage * 8).coerceAtLeast(2_000L)
        }

        val initialRole = if (linkedOffer != null) {
            linkedOffer.offeredRole
        } else if (player.ovr >= club.squadOvrAverage + 2) {
            SquadRole.KEY_PLAYER
        } else if (player.ovr >= club.squadOvrAverage - 3) {
            SquadRole.ROTATION
        } else {
            SquadRole.PROSPECT
        }

        val initialYears = linkedOffer?.offeredContractYears ?: 3

        return NegotiationSession(
            id = UUID.randomUUID().toString(),
            clubId = club.id,
            clubName = club.name,
            isRenewal = isRenewal,
            linkedOfferId = linkedOffer?.id,
            currentWageDemand = (initialClubWage * 1.25).toLong(),
            currentSigningBonusDemand = (initialSigningBonus * 1.30).toLong(),
            currentContractYearsDemand = initialYears,
            currentRoleDemand = initialRole,
            currentReleaseClauseDemand = if (club.tier == 1) initialClubWage * 150 else null,
            clubOfferedWage = initialClubWage,
            clubOfferedSigningBonus = initialSigningBonus,
            clubOfferedContractYears = initialYears,
            clubOfferedRole = initialRole,
            clubOfferedReleaseClause = linkedOffer?.offeredReleaseClause,
            hiddenClubPatience = TransferConfig.INITIAL_NEGOTIATION_PATIENCE + agent.tier.negotiationBonus,
            roundsCount = 0,
            status = NegotiationStatus.IN_PROGRESS,
            latestFeedbackMessageVi = "Ban lãnh đạo ${club.shortName} đã gửi bản thảo đầu tiên và sẵn sàng thảo luận."
        )
    }

    /**
     * Người chơi gửi yêu cầu mặc cả (Counter-proposal).
     * Engine tính toán độ chênh lệch, độ kiên nhẫn ngầm và đưa ra phản hồi bằng chữ.
     */
    fun submitPlayerCounterProposal(
        session: NegotiationSession,
        player: Player,
        club: Club,
        agent: Agent,
        demandedWage: Long,
        demandedSigningBonus: Long,
        demandedYears: Int,
        demandedRole: SquadRole,
        demandedReleaseClause: Long?,
        random: Random = Random
    ): NegotiationSession {
        session.roundsCount++
        session.currentWageDemand = demandedWage
        session.currentSigningBonusDemand = demandedSigningBonus
        session.currentContractYearsDemand = demandedYears
        session.currentRoleDemand = demandedRole
        session.currentReleaseClauseDemand = demandedReleaseClause

        // Mức trần tối đa CLB có thể chi trả cho cầu thủ này
        val baseMaxWage = TransferConfig.calculateRecommendedWage(
            playerOvr = player.ovr,
            playerReputation = player.domesticReputation,
            clubTier = club.tier,
            clubBudget = club.budget
        )
        val clubMaxWage = (baseMaxWage * (1.18 + (agent.tier.negotiationBonus * 0.012) + (player.managerTrust * 0.001))).toLong()
        val clubMaxBonus = (clubMaxWage * 14).coerceAtLeast(5_000L)

        // 1. Trường hợp CHẤP THUẬN NGAY: Yêu cầu của người chơi nằm trong giới hạn CLB sẵn sàng đáp ứng
        if (demandedWage <= clubMaxWage && demandedSigningBonus <= clubMaxBonus) {
            session.clubOfferedWage = demandedWage
            session.clubOfferedSigningBonus = demandedSigningBonus
            session.clubOfferedContractYears = demandedYears
            session.clubOfferedRole = demandedRole
            session.clubOfferedReleaseClause = demandedReleaseClause
            session.status = NegotiationStatus.AGREED
            session.latestFeedbackMessageVi = "Tuyệt vời! Đại diện ${club.shortName} mỉm cười gật đầu và đồng ý toàn bộ điều khoản bạn đưa ra."

            session.history.add(
                NegotiationRoundRecord(
                    roundNumber = session.roundsCount,
                    playerDemandedWage = demandedWage,
                    playerDemandedSigningBonus = demandedSigningBonus,
                    playerDemandedYears = demandedYears,
                    playerDemandedRole = demandedRole,
                    clubCounterWage = demandedWage,
                    clubCounterBonus = demandedSigningBonus,
                    clubFeedbackMessageVi = session.latestFeedbackMessageVi
                )
            )
            return session
        }

        // 2. Yêu cầu vượt trần -> CLB bị giảm độ kiên nhẫn ngầm
        val penalty = TransferConfig.calculatePatiencePenalty(
            demandedWage = demandedWage,
            clubMaxWage = clubMaxWage,
            demandedSigningBonus = demandedSigningBonus,
            clubMaxBonus = clubMaxBonus,
            agentBonus = agent.tier.negotiationBonus
        )

        session.hiddenClubPatience = (session.hiddenClubPatience - penalty).coerceAtLeast(0)

        // Nếu kiên nhẫn cạn kiệt hoặc đã quá 5 vòng mặc cả
        if (session.hiddenClubPatience <= TransferConfig.MIN_PATIENCE_THRESHOLD || session.roundsCount >= session.maxRounds) {
            session.status = NegotiationStatus.TERMINATED_WALKOUT
            session.latestFeedbackMessageVi = if (agent.tier != AgentTier.NONE) {
                "${agent.name} thở dài lắc đầu: 'CLB cho rằng yêu cầu của bạn quá xa vời và đã chính thức rút khỏi bàn đàm phán!'"
            } else {
                "Ban lãnh đạo ${club.shortName} tỏ vẻ thất vọng sâu sắc và tuyên bố hủy bỏ đàm phán do không tìm được tiếng nói chung."
            }

            session.history.add(
                NegotiationRoundRecord(
                    roundNumber = session.roundsCount,
                    playerDemandedWage = demandedWage,
                    playerDemandedSigningBonus = demandedSigningBonus,
                    playerDemandedYears = demandedYears,
                    playerDemandedRole = demandedRole,
                    clubCounterWage = session.clubOfferedWage,
                    clubCounterBonus = session.clubOfferedSigningBonus,
                    clubFeedbackMessageVi = session.latestFeedbackMessageVi
                )
            )
            return session
        }

        // 3. CLB nhượng bộ một phần và đưa ra PHẢN ĐỀ NGHỊ (Counter-offer)
        val wageStep = ((clubMaxWage - session.clubOfferedWage) * (0.45 + (agent.tier.negotiationBonus * 0.015))).toLong().coerceAtLeast(100L)
        session.clubOfferedWage = (session.clubOfferedWage + wageStep).coerceAtMost(clubMaxWage)

        val bonusStep = ((clubMaxBonus - session.clubOfferedSigningBonus) * 0.40).toLong().coerceAtLeast(500L)
        session.clubOfferedSigningBonus = (session.clubOfferedSigningBonus + bonusStep).coerceAtMost(clubMaxBonus)

        session.clubOfferedContractYears = demandedYears
        session.clubOfferedRole = if (demandedRole == SquadRole.STAR && player.ovr < club.squadOvrAverage + 3) {
            SquadRole.KEY_PLAYER // Không thể cam kết ngôi sao số 1 nếu OVR chưa vượt trội
        } else {
            demandedRole
        }

        val patienceHint = TransferConfig.getPatienceFeedbackText(
            patience = session.hiddenClubPatience,
            isAgentInvolved = (agent.tier != AgentTier.NONE)
        )
        session.latestFeedbackMessageVi = "CLB nâng mức đề nghị lên €${session.clubOfferedWage}/tuần kèm €${session.clubOfferedSigningBonus} tiền lót tay. $patienceHint"

        session.history.add(
            NegotiationRoundRecord(
                roundNumber = session.roundsCount,
                playerDemandedWage = demandedWage,
                playerDemandedSigningBonus = demandedSigningBonus,
                playerDemandedYears = demandedYears,
                playerDemandedRole = demandedRole,
                clubCounterWage = session.clubOfferedWage,
                clubCounterBonus = session.clubOfferedSigningBonus,
                clubFeedbackMessageVi = session.latestFeedbackMessageVi
            )
        )

        return session
    }

    /**
     * Chấp nhận bản đề nghị hiện tại của CLB và tạo hợp đồng mới.
     */
    fun finalizeContractAgreement(
        session: NegotiationSession,
        currentSeason: Int
    ): Contract {
        return Contract(
            clubId = session.clubId,
            clubName = session.clubName,
            weeklyWage = session.clubOfferedWage,
            signingBonus = session.clubOfferedSigningBonus,
            bonusPerAppearance = (session.clubOfferedWage * 0.12).toLong().coerceAtLeast(50L),
            bonusPerGoal = (session.clubOfferedWage * 0.20).toLong().coerceAtLeast(100L),
            bonusPerAssist = (session.clubOfferedWage * 0.10).toLong().coerceAtLeast(50L),
            bonusPerCleanSheet = (session.clubOfferedWage * 0.15).toLong().coerceAtLeast(80L),
            bonusTrophy = (session.clubOfferedWage * 10).coerceAtLeast(5_000L),
            contractYearsRemaining = session.clubOfferedContractYears,
            totalWeeksRemaining = session.clubOfferedContractYears * 26, // 26 tuần mỗi mùa
            releaseClause = session.clubOfferedReleaseClause,
            squadRole = session.clubOfferedRole,
            clubSatisfaction = 85,
            startSeason = currentSeason,
            endSeason = currentSeason + session.clubOfferedContractYears
        )
    }
}
