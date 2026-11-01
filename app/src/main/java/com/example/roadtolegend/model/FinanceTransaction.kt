package com.example.roadtolegend.model

enum class TransactionType(val labelVi: String, val isIncome: Boolean) {
    WAGE("Lương tuần", true),
    MATCH_BONUS("Thưởng thi đấu & bàn thắng", true),
    SIGNING_BONUS("Thưởng ký hợp đồng (Lót tay)", true),
    TROPHY_BONUS("Thưởng vô địch danh hiệu", true),
    AGENT_COMMISSION("Hoa hồng người đại diện", false),
    AGENT_RETAINER("Phí duy trì người đại diện", false),
    SHOP_PURCHASE("Mua sắm phong cách sống", false),
    SHOP_MAINTENANCE("Bảo dưỡng tài sản", false),
    FINE("Tiền phạt kỷ luật", false),
    OTHER("Khoản thu chi khác", true)
}

data class FinanceTransaction(
    val id: String,
    val week: Int,
    val season: Int,
    val type: TransactionType,
    val amount: Long,
    val balanceAfter: Long,
    val descriptionVi: String,
    val timestamp: Long = System.currentTimeMillis()
)
