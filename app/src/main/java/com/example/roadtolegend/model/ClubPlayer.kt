package com.example.roadtolegend.model

/**
 * Đại diện cho một cầu thủ trong đội hình của câu lạc bộ,
 * sử dụng hệ thống chỉ số FIFA / EA Sports FC (PAC, SHO, PAS, DRI, DEF, PHY, OVR).
 */
data class ClubPlayer(
    val id: String,                         // ID định danh duy nhất (ví dụ: "vn_cong_phuong")
    val name: String,                       // Họ và tên cầu thủ
    val clubId: String,                     // ID câu lạc bộ chủ quản (khớp với Club.id)
    val number: Int,                        // Số áo thi đấu chính thức
    val age: Int,                           // Tuổi hiện tại
    val nationality: String,                // Quốc tịch (ví dụ: "Việt Nam", "Brazil", "Anh")
    val nationalityCode: String = "VN",     // Ký hiệu quốc gia trên thẻ (VN, BR, EN, FR...)
    val position: Position,                 // Vị trí thi đấu chính (GK, CB, LB, RB, CDM, CM, CAM, LW, RW, ST)
    val ovr: Int,                           // Chỉ số tổng quát (OVR) thang 1 - 99
    val pac: Int,                           // Tốc độ (Pace)
    val sho: Int,                           // Dứt điểm (Shooting)
    val pas: Int,                           // Chuyền bóng (Passing)
    val dri: Int,                           // Rê bóng (Dribbling)
    val def: Int,                           // Phòng ngự (Defending)
    val phy: Int,                           // Thể lực & Sức mạnh (Physicality)
    val marketValue: Long,                  // Giá trị chuyển nhượng ước tính (€ / USD)
    val weeklyWage: Long,                   // Mức lương tuần (€ / USD)
    val preferredFoot: Foot = Foot.RIGHT,   // Chân thuận
    val heightCm: Int = 175,                // Chiều cao (cm)
    val avatarResName: String = "player_$id", // Tên file ảnh trong res/drawable/ (ví dụ: player_vn_cong_phuong)
    val isUser: Boolean = false,            // Đánh dấu cầu thủ này có phải là nhân vật của người chơi
    val matchesPlayed: Int = 0,             // Số trận đã thi đấu mùa này
    val goals: Int = 0,                     // Số bàn thắng
    val assists: Int = 0,                   // Số kiến tạo
    val customAvatarPath: String? = null    // Đường dẫn ảnh đại diện tùy chỉnh (nếu có)
) {
    val positionCategory: PositionCategory
        get() = position.category
}
