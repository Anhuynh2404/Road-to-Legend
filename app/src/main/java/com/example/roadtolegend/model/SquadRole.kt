package com.example.roadtolegend.model

enum class SquadRole(
    val titleVi: String,
    val descriptionVi: String,
    val minStartsExpectationPercent: Int
) {
    STAR(
        titleVi = "Ngôi sao không thể thay thế",
        descriptionVi = "Là linh hồn của đội bóng, luôn đá chính trong mọi trận cầu quan trọng.",
        minStartsExpectationPercent = 90
    ),
    KEY_PLAYER(
        titleVi = "Trụ cột đội bóng",
        descriptionVi = "Cầu thủ nòng cốt trong đội hình xuất phát thường xuyên.",
        minStartsExpectationPercent = 75
    ),
    ROTATION(
        titleVi = "Cầu thủ luân phiên",
        descriptionVi = "Ra sân luân phiên theo lịch thi đấu và xoay tua thể lực.",
        minStartsExpectationPercent = 50
    ),
    PROSPECT(
        titleVi = "Cầu thủ tiềm năng",
        descriptionVi = "Tài năng trẻ được trao cơ hội vào sân từ ghế dự bị để tích lũy kinh nghiệm.",
        minStartsExpectationPercent = 25
    ),
    BACKUP(
        titleVi = "Dự bị chiến lược",
        descriptionVi = "Sẵn sàng thay thế khi các cầu thủ chính thức gặp chấn thương hoặc thẻ phạt.",
        minStartsExpectationPercent = 10
    )
}
