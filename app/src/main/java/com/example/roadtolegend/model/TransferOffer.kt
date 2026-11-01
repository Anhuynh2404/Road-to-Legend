package com.example.roadtolegend.model

enum class TransferOfferStatus {
    PENDING_SELLING_CLUB,          // Chờ CLB chủ quản xem xét mức phí chuyển nhượng
    REJECTED_BY_SELLING_CLUB,      // CLB chủ quản đã từ chối mức phí
    APPROVED_AWAITING_PLAYER,      // CLB chủ quản đã đồng ý, gửi điều khoản cá nhân cho cầu thủ
    NEGOTIATING_PLAYER_TERMS,      // Đang trong tiến trình đàm phán hợp đồng cá nhân
    ACCEPTED_BY_PLAYER,            // Cầu thủ đã chấp nhận, chuẩn bị ký chính thức
    REJECTED_BY_PLAYER,            // Cầu thủ từ chối lời đề nghị
    CANCELLED_BY_BUYER,            // CLB mua rút lại lời đề nghị (hết kiên nhẫn hoặc hết hạn)
    COMPLETED                      // Thương vụ hoàn tất thành công
}

data class TransferOffer(
    val id: String,
    val buyingClubId: String,
    val buyingClubName: String,
    val buyingClubShortName: String,
    val sellingClubId: String,
    val sellingClubName: String,
    var transferFee: Long,
    var offeredWage: Long,
    var offeredSigningBonus: Long,
    var offeredContractYears: Int = 3,
    var offeredRole: SquadRole = SquadRole.KEY_PLAYER,
    var offeredBonusPerGoal: Long = 150L,
    var offeredBonusPerAssist: Long = 80L,
    var offeredReleaseClause: Long? = null,
    var status: TransferOfferStatus = TransferOfferStatus.APPROVED_AWAITING_PLAYER,
    var isLoan: Boolean = false,
    var loanWageSharePercent: Int = 100,
    var loanBuyOptionFee: Long? = null,
    var canRecall: Boolean = false,
    var loanDurationWeeks: Int = 13,
    var createdWeek: Int = 1,
    var expiresInWeeks: Int = 2,
    var isDeadlineDeal: Boolean = false,
    var scoutNote: String = "CLB đánh giá cao tiềm năng và phong độ gần đây của bạn."
) {
    val isPendingPlayerAction: Boolean
        get() = status == TransferOfferStatus.APPROVED_AWAITING_PLAYER || status == TransferOfferStatus.NEGOTIATING_PLAYER_TERMS
}
