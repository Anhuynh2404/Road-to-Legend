package com.example.roadtolegend.model

data class LoanRecord(
    val parentClubId: String,
    val parentClubName: String,
    val parentClubShortName: String,
    val loanClubId: String,
    val loanClubName: String,
    val loanClubShortName: String,
    val wageSharePercent: Int,         // Tỉ lệ % lương do CLB mượn chi trả (ví dụ: 100%, 70%)
    val buyOptionFee: Long?,           // Mức giá quyền mua đứt (nếu có)
    val canRecallInWinter: Boolean,    // CLB mẹ có quyền triệu hồi vào kỳ chuyển nhượng mùa đông không
    var weeksRemaining: Int,           // Số tuần thi đấu theo diện cho mượn còn lại
    var totalMatchesOnLoan: Int = 0,
    var goalsOnLoan: Int = 0,
    var assistsOnLoan: Int = 0
)
