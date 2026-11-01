package com.example.roadtolegend.model

enum class NegotiationStatus {
    IN_PROGRESS,
    AGREED,
    CLUB_REJECTED_OFFER,
    TERMINATED_WALKOUT
}

data class NegotiationRoundRecord(
    val roundNumber: Int,
    val playerDemandedWage: Long,
    val playerDemandedSigningBonus: Long,
    val playerDemandedYears: Int,
    val playerDemandedRole: SquadRole,
    val clubCounterWage: Long,
    val clubCounterBonus: Long,
    val clubFeedbackMessageVi: String
)

data class NegotiationSession(
    val id: String,
    val clubId: String,
    val clubName: String,
    val isRenewal: Boolean,            // True: gia hạn với CLB hiện tại; False: đàm phán với CLB mới
    val linkedOfferId: String? = null,
    var currentWageDemand: Long,
    var currentSigningBonusDemand: Long,
    var currentContractYearsDemand: Int,
    var currentRoleDemand: SquadRole,
    var currentReleaseClauseDemand: Long?,
    var clubOfferedWage: Long,
    var clubOfferedSigningBonus: Long,
    var clubOfferedContractYears: Int,
    var clubOfferedRole: SquadRole,
    var clubOfferedReleaseClause: Long?,
    var hiddenClubPatience: Int = 100, // Thang 0 - 100 (tính ngầm trong engine, không hiển thị số ra UI)
    var roundsCount: Int = 0,
    var maxRounds: Int = 5,
    var status: NegotiationStatus = NegotiationStatus.IN_PROGRESS,
    var latestFeedbackMessageVi: String = "Ban lãnh đạo CLB đã sẵn sàng lắng nghe nguyện vọng cá nhân của bạn.",
    val history: MutableList<NegotiationRoundRecord> = mutableListOf()
)
