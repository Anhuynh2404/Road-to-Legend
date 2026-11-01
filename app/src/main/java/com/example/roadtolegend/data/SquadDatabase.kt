package com.example.roadtolegend.data

import com.example.roadtolegend.model.ClubPlayer
import com.example.roadtolegend.model.Foot
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.Position

/**
 * Kho dữ liệu đội hình và danh sách cầu thủ cho tất cả các câu lạc bộ trong giải đấu.
 * Chỉ số được thiết kế và cân bằng theo tiêu chuẩn FIFA / EA Sports FC.
 *
 * HƯỚNG DẪN THÊM CẦU THỦ THỦ CÔNG:
 * - Để thêm hoặc chỉnh sửa cầu thủ cho bất kỳ CLB nào, tìm đến hàm danh sách của CLB đó bên dưới
 * - Tạo thêm một đối tượng `ClubPlayer(...)` với các thuộc tính:
 *   + id: chuỗi id duy nhất (ví dụ: "vn_cong_phuong")
 *   + name: Tên cầu thủ ("Nguyễn Công Phượng")
 *   + number: Số áo (10)
 *   + position: Position.ST / Position.CAM / Position.CB / Position.GK ...
 *   + pac, sho, pas, dri, def, phy: 6 chỉ số cốt lõi thang 1 - 99
 *   + ovr: Chỉ số tổng hợp 1 - 99
 *   + marketValue: Giá trị chuyển nhượng (€)
 *   + weeklyWage: Lương tuần (€)
 *   + avatarResName: Tên file ảnh chân dung trong app/src/main/res/drawable/ (mặc định: player_<id>)
 */
object SquadDatabase {

    fun getSquadForClub(
        clubId: String,
        userPlayer: Player? = null,
        userClubId: String? = null,
        userWeeklyWage: Long = 1_000L
    ): List<ClubPlayer> {
        val baseSquad = baseSquadsByClub[clubId] ?: generateDefaultSquad(clubId)

        // Nếu đây là CLB của người chơi, bổ sung cầu thủ người chơi vào đội hình
        if (userPlayer != null && (userClubId == clubId || clubId == "user_club")) {
            val userNumber = if (baseSquad.any { it.number == 10 }) 99 else 10
            val userAsClubPlayer = ClubPlayer(
                id = userPlayer.id,
                name = "${userPlayer.name} ★",
                clubId = clubId,
                number = userNumber,
                age = userPlayer.age,
                nationality = userPlayer.nationality,
                nationalityCode = "VN",
                position = userPlayer.primaryPosition,
                ovr = userPlayer.ovr,
                pac = userPlayer.pac.toInt().coerceIn(1, 99),
                sho = userPlayer.sho.toInt().coerceIn(1, 99),
                pas = userPlayer.pas.toInt().coerceIn(1, 99),
                dri = userPlayer.dri.toInt().coerceIn(1, 99),
                def = userPlayer.def.toInt().coerceIn(1, 99),
                phy = userPlayer.sta.toInt().coerceIn(1, 99),
                marketValue = userPlayer.marketValue,
                weeklyWage = userWeeklyWage,
                preferredFoot = userPlayer.preferredFoot,
                heightCm = userPlayer.heightCm,
                avatarResName = "player_user",
                isUser = true
            )
            return listOf(userAsClubPlayer) + baseSquad.filter { it.id != userPlayer.id }
        }

        return baseSquad
    }

    private val baseSquadsByClub: Map<String, List<ClubPlayer>> by lazy {
        mapOf(
            // ==========================================
            // V-LEAGUE 2 (HẠNG NHẤT QUỐC GIA VIỆT NAM)
            // ==========================================
            "vn_binh_phuoc" to listOf(
                ClubPlayer("vn_bp_tan_truong", "Bùi Tấn Trường", "vn_binh_phuoc", 1, 38, "Việt Nam", "VN", Position.GK, 62, 38, 20, 52, 40, 64, 66, 60_000, 1_200, Foot.RIGHT, 188),
                ClubPlayer("vn_bp_tan_sinh", "Huỳnh Tấn Sinh", "vn_binh_phuoc", 3, 26, "Việt Nam", "VN", Position.CB, 61, 58, 30, 48, 45, 62, 65, 80_000, 1_400, Foot.RIGHT, 184),
                ClubPlayer("vn_bp_ngoc_duc", "Sầm Ngọc Đức", "vn_binh_phuoc", 7, 32, "Việt Nam", "VN", Position.LB, 60, 60, 42, 58, 55, 61, 62, 70_000, 1_300, Foot.LEFT, 172),
                ClubPlayer("vn_bp_van_quy", "Nguyễn Văn Quý", "vn_binh_phuoc", 4, 25, "Việt Nam", "VN", Position.CB, 57, 56, 28, 44, 42, 58, 62, 45_000, 900, Foot.RIGHT, 180),
                ClubPlayer("vn_bp_tu_nhan", "Lưu Tự Nhân", "vn_binh_phuoc", 8, 27, "Việt Nam", "VN", Position.CM, 58, 57, 50, 60, 56, 54, 60, 55_000, 1_000, Foot.RIGHT, 174),
                ClubPlayer("vn_bp_sy_giap", "Hồ Sỹ Giáp", "vn_binh_phuoc", 14, 30, "Việt Nam", "VN", Position.RW, 58, 64, 52, 55, 58, 48, 59, 50_000, 1_000, Foot.RIGHT, 175),
                ClubPlayer("vn_bp_thanh_binh", "Lê Thanh Bình", "vn_binh_phuoc", 9, 29, "Việt Nam", "VN", Position.ST, 60, 62, 62, 50, 56, 45, 63, 75_000, 1_300, Foot.RIGHT, 178),
                ClubPlayer("vn_cong_phuong", "Nguyễn Công Phượng", "vn_binh_phuoc", 70, 29, "Việt Nam", "VN", Position.ST, 66, 68, 67, 65, 72, 36, 58, 220_000, 3_000, Foot.RIGHT, 168),
                ClubPlayer("vn_bp_hoang_duong", "Nguyễn Hoàng Dương", "vn_binh_phuoc", 11, 24, "Việt Nam", "VN", Position.LW, 57, 66, 51, 52, 60, 38, 52, 50_000, 900, Foot.LEFT, 170),
                ClubPlayer("vn_bp_xuan_huynh", "Vũ Xuân Huỳnh", "vn_binh_phuoc", 22, 23, "Việt Nam", "VN", Position.RB, 56, 62, 35, 48, 50, 56, 58, 40_000, 800, Foot.RIGHT, 173),
                ClubPlayer("vn_bp_van_nam", "Trần Văn Nam", "vn_binh_phuoc", 25, 22, "Việt Nam", "VN", Position.GK, 54, 40, 18, 45, 38, 55, 58, 30_000, 600, Foot.RIGHT, 183)
            ),

            "vn_ninh_binh" to listOf(
                ClubPlayer("vn_dang_van_lam", "Đặng Văn Lâm", "vn_ninh_binh", 1, 31, "Việt Nam", "VN", Position.GK, 68, 42, 25, 60, 48, 70, 74, 300_000, 4_000, Foot.LEFT, 188),
                ClubPlayer("vn_nb_ngoc_bao", "Lê Ngọc Bảo", "vn_ninh_binh", 5, 26, "Việt Nam", "VN", Position.CB, 62, 60, 32, 54, 52, 64, 66, 95_000, 1_500, Foot.LEFT, 178),
                ClubPlayer("vn_nb_huu_tuan", "Nguyễn Hữu Tuấn", "vn_ninh_binh", 15, 32, "Việt Nam", "VN", Position.CB, 61, 56, 30, 50, 48, 63, 67, 85_000, 1_400, Foot.RIGHT, 180),
                ClubPlayer("vn_nb_hai_duc", "Trịnh Đức Lợi", "vn_ninh_binh", 2, 28, "Việt Nam", "VN", Position.LB, 59, 64, 38, 52, 55, 60, 61, 65_000, 1_100, Foot.LEFT, 172),
                ClubPlayer("vn_nb_thuan", "Đỗ Văn Thuận", "vn_ninh_binh", 14, 32, "Việt Nam", "VN", Position.CDM, 60, 52, 45, 62, 58, 62, 64, 75_000, 1_300, Foot.RIGHT, 171),
                ClubPlayer("vn_hoang_duc", "Nguyễn Hoàng Đức", "vn_ninh_binh", 28, 26, "Việt Nam", "VN", Position.CAM, 70, 67, 68, 73, 72, 58, 68, 450_000, 5_500, Foot.LEFT, 184),
                ClubPlayer("vn_nb_minh_binh", "Lê Minh Bình", "vn_ninh_binh", 10, 24, "Việt Nam", "VN", Position.RW, 61, 69, 57, 56, 63, 40, 55, 80_000, 1_300, Foot.RIGHT, 175),
                ClubPlayer("vn_nb_thanh_binh", "Đinh Thanh Bình", "vn_ninh_binh", 9, 26, "Việt Nam", "VN", Position.ST, 63, 66, 64, 55, 62, 45, 68, 120_000, 1_800, Foot.RIGHT, 177),
                ClubPlayer("vn_nb_trong_hoa", "Nguyễn Trọng Hoá", "vn_ninh_binh", 7, 23, "Việt Nam", "VN", Position.LW, 58, 65, 52, 54, 60, 42, 54, 55_000, 950, Foot.RIGHT, 173),
                ClubPlayer("vn_nb_van_trung", "Nguyễn Văn Trung", "vn_ninh_binh", 21, 25, "Việt Nam", "VN", Position.RB, 58, 62, 36, 50, 52, 58, 60, 50_000, 900, Foot.RIGHT, 174),
                ClubPlayer("vn_nb_tien_dung", "Trần Tiến Dũng", "vn_ninh_binh", 26, 22, "Việt Nam", "VN", Position.GK, 55, 40, 20, 48, 40, 56, 58, 35_000, 650, Foot.RIGHT, 182)
            ),

            "vn_pvf" to listOf(
                ClubPlayer("vn_pvf_minh_long", "Phí Minh Long", "vn_pvf", 33, 29, "Việt Nam", "VN", Position.GK, 60, 40, 22, 54, 44, 62, 64, 70_000, 1_200, Foot.RIGHT, 180),
                ClubPlayer("vn_pvf_hieu_minh", "Nguyễn Hiểu Minh", "vn_pvf", 4, 20, "Việt Nam", "VN", Position.CB, 59, 58, 28, 48, 45, 61, 64, 75_000, 1_000, Foot.RIGHT, 182),
                ClubPlayer("vn_pvf_bao_long", "Nguyễn Bảo Long", "vn_pvf", 2, 19, "Việt Nam", "VN", Position.LB, 58, 65, 36, 52, 56, 58, 56, 70_000, 950, Foot.LEFT, 171),
                ClubPlayer("vn_pvf_duc_phu", "Nguyễn Đức Phú", "vn_pvf", 6, 21, "Việt Nam", "VN", Position.CDM, 60, 60, 48, 62, 58, 60, 62, 85_000, 1_200, Foot.RIGHT, 172),
                ClubPlayer("vn_pvf_cong_den", "Huỳnh Công Đến", "vn_pvf", 8, 23, "Việt Nam", "VN", Position.CM, 62, 63, 56, 65, 64, 52, 60, 110_000, 1_500, Foot.RIGHT, 166),
                ClubPlayer("vn_pvf_van_do", "Lê Văn Đô", "vn_pvf", 19, 23, "Việt Nam", "VN", Position.LW, 63, 72, 60, 59, 66, 46, 58, 130_000, 1_700, Foot.RIGHT, 173),
                ClubPlayer("vn_pvf_thanh_nhan", "Nguyễn Thanh Nhàn", "vn_pvf", 11, 21, "Việt Nam", "VN", Position.RW, 64, 74, 63, 58, 67, 44, 60, 150_000, 1_900, Foot.RIGHT, 174),
                ClubPlayer("vn_pvf_xuan_bac", "Nguyễn Xuân Bắc", "vn_pvf", 10, 21, "Việt Nam", "VN", Position.ST, 58, 63, 58, 50, 57, 40, 60, 60_000, 900, Foot.RIGHT, 176),
                ClubPlayer("vn_pvf_quang_thinh", "Trần Quang Thịnh", "vn_pvf", 3, 23, "Việt Nam", "VN", Position.CB, 58, 55, 26, 46, 42, 60, 63, 60_000, 950, Foot.RIGHT, 178),
                ClubPlayer("vn_pvf_hai_nam", "Nguyễn Hải Nam", "vn_pvf", 17, 20, "Việt Nam", "VN", Position.RB, 57, 64, 35, 48, 52, 57, 56, 50_000, 800, Foot.RIGHT, 172),
                ClubPlayer("vn_pvf_quang_truong", "Nguyễn Quang Trường", "vn_pvf", 1, 20, "Việt Nam", "VN", Position.GK, 55, 38, 18, 46, 38, 57, 60, 40_000, 700, Foot.RIGHT, 184)
            ),

            "vn_hue" to listOf(
                ClubPlayer("vn_hue_ngoc_thien", "Lê Ngọc Thiên", "vn_hue", 1, 24, "Việt Nam", "VN", Position.GK, 53, 36, 18, 45, 36, 55, 57, 30_000, 600, Foot.RIGHT, 181),
                ClubPlayer("vn_hue_van_trong", "Nguyễn Văn Trọng", "vn_hue", 4, 25, "Việt Nam", "VN", Position.CB, 54, 52, 28, 44, 40, 56, 61, 35_000, 700, Foot.RIGHT, 179),
                ClubPlayer("vn_hue_hoang_quan", "Bùi Hoàng Quân", "vn_hue", 5, 23, "Việt Nam", "VN", Position.CB, 53, 50, 26, 42, 38, 55, 60, 30_000, 650, Foot.RIGHT, 180),
                ClubPlayer("vn_hue_the_hung", "Võ Thế Hưng", "vn_hue", 2, 22, "Việt Nam", "VN", Position.LB, 53, 58, 34, 46, 48, 53, 55, 30_000, 600, Foot.LEFT, 170),
                ClubPlayer("vn_hue_nhat_nam", "Nguyễn Nhật Nam", "vn_hue", 8, 24, "Việt Nam", "VN", Position.CM, 54, 54, 48, 56, 54, 50, 56, 35_000, 700, Foot.RIGHT, 172),
                ClubPlayer("vn_hue_van_chien", "Nguyễn Văn Chiến", "vn_hue", 7, 25, "Việt Nam", "VN", Position.LW, 55, 64, 52, 50, 58, 38, 52, 42_000, 800, Foot.RIGHT, 171),
                ClubPlayer("vn_hue_thanh_minh", "Hồ Thanh Minh", "vn_hue", 10, 24, "Việt Nam", "VN", Position.ST, 60, 68, 61, 52, 60, 42, 64, 75_000, 1_200, Foot.RIGHT, 173),
                ClubPlayer("vn_hue_tran_thanh", "Trần Thành", "vn_hue", 9, 27, "Việt Nam", "VN", Position.ST, 57, 60, 58, 48, 54, 40, 60, 50_000, 900, Foot.RIGHT, 177),
                ClubPlayer("vn_hue_tan_dien", "Phan Tấn Điền", "vn_hue", 17, 23, "Việt Nam", "VN", Position.RB, 53, 58, 32, 45, 47, 54, 56, 30_000, 600, Foot.RIGHT, 171),
                ClubPlayer("vn_hue_van_sang", "Lê Văn Sang", "vn_hue", 14, 21, "Việt Nam", "VN", Position.CDM, 53, 52, 40, 52, 48, 54, 58, 30_000, 600, Foot.RIGHT, 174),
                ClubPlayer("vn_hue_tien_tao", "Nguyễn Tiến Tạo", "vn_hue", 25, 21, "Việt Nam", "VN", Position.GK, 51, 35, 16, 42, 34, 52, 55, 20_000, 500, Foot.RIGHT, 182)
            ),

            "vn_dong_thap" to listOf(
                ClubPlayer("vn_dt_thanh_nha", "Đoàn Thanh Nhã", "vn_dong_thap", 1, 26, "Việt Nam", "VN", Position.GK, 53, 38, 18, 46, 38, 55, 58, 30_000, 600, Foot.RIGHT, 181),
                ClubPlayer("vn_dt_minh_trong", "Võ Minh Trọng", "vn_dong_thap", 17, 23, "Việt Nam", "VN", Position.LB, 62, 68, 46, 60, 62, 60, 61, 100_000, 1_400, Foot.LEFT, 170),
                ClubPlayer("vn_dt_nhat_tan", "Trần Nhật Tân", "vn_dong_thap", 4, 25, "Việt Nam", "VN", Position.CB, 53, 52, 28, 44, 40, 55, 60, 32_000, 650, Foot.RIGHT, 179),
                ClubPlayer("vn_dt_hai_dang", "Nguyễn Hải Đăng", "vn_dong_thap", 5, 24, "Việt Nam", "VN", Position.CB, 52, 50, 25, 42, 38, 54, 59, 28_000, 600, Foot.RIGHT, 180),
                ClubPlayer("vn_dt_tuan_dat", "Nguyễn Tuấn Đạt", "vn_dong_thap", 10, 26, "Việt Nam", "VN", Position.CAM, 56, 58, 54, 60, 58, 45, 54, 45_000, 850, Foot.RIGHT, 169),
                ClubPlayer("vn_dt_huu_thang", "Trần Hữu Thắng", "vn_dong_thap", 9, 24, "Việt Nam", "VN", Position.ST, 55, 62, 56, 48, 54, 38, 58, 40_000, 800, Foot.RIGHT, 175),
                ClubPlayer("vn_dt_cong_hau", "Nguyễn Công Hậu", "vn_dong_thap", 11, 22, "Việt Nam", "VN", Position.RW, 54, 64, 48, 50, 56, 36, 50, 35_000, 700, Foot.LEFT, 171),
                ClubPlayer("vn_dt_quoc_viet", "Lê Quốc Việt", "vn_dong_thap", 8, 23, "Việt Nam", "VN", Position.CM, 53, 54, 46, 54, 52, 48, 55, 30_000, 650, Foot.RIGHT, 173),
                ClubPlayer("vn_dt_thanh_hien", "Nguyễn Thanh Hiền", "vn_dong_thap", 2, 27, "Việt Nam", "VN", Position.RB, 54, 58, 35, 48, 50, 55, 58, 35_000, 700, Foot.RIGHT, 174),
                ClubPlayer("vn_dt_van_quy", "Bùi Văn Quý", "vn_dong_thap", 14, 22, "Việt Nam", "VN", Position.CDM, 52, 50, 38, 50, 46, 54, 57, 28_000, 600, Foot.RIGHT, 176),
                ClubPlayer("vn_dt_hoang_nam", "Lê Hoàng Nam", "vn_dong_thap", 23, 20, "Việt Nam", "VN", Position.GK, 50, 35, 16, 42, 34, 52, 55, 20_000, 500, Foot.RIGHT, 183)
            ),

            "vn_long_an" to listOf(
                ClubPlayer("vn_la_nhat_ha", "Trần Nhật Hà", "vn_long_an", 1, 26, "Việt Nam", "VN", Position.GK, 53, 38, 18, 46, 38, 55, 58, 30_000, 600, Foot.RIGHT, 182),
                ClubPlayer("vn_la_quoc_loc", "Nguyễn Quốc Lộc", "vn_long_an", 3, 25, "Việt Nam", "VN", Position.CB, 53, 50, 26, 44, 40, 55, 60, 32_000, 650, Foot.RIGHT, 180),
                ClubPlayer("vn_la_van_quy", "Lê Văn Quý", "vn_long_an", 4, 24, "Việt Nam", "VN", Position.CB, 52, 50, 25, 42, 38, 54, 59, 28_000, 600, Foot.RIGHT, 178),
                ClubPlayer("vn_la_tan_tai", "Phan Tấn Tài", "vn_long_an", 8, 27, "Việt Nam", "VN", Position.CM, 55, 56, 50, 58, 56, 50, 57, 42_000, 800, Foot.RIGHT, 173),
                ClubPlayer("vn_la_nguyen_khanh", "Cù Nguyễn Khánh", "vn_long_an", 10, 25, "Việt Nam", "VN", Position.ST, 56, 62, 58, 50, 55, 40, 60, 48_000, 900, Foot.RIGHT, 176),
                ClubPlayer("vn_la_anh_duc", "Nguyễn Anh Đức", "vn_long_an", 11, 38, "Việt Nam", "VN", Position.ST, 58, 50, 64, 54, 52, 42, 68, 55_000, 1_100, Foot.RIGHT, 182),
                ClubPlayer("vn_la_trong_phu", "Nguyễn Trọng Phú", "vn_long_an", 7, 23, "Việt Nam", "VN", Position.RW, 54, 64, 48, 50, 56, 36, 50, 35_000, 700, Foot.RIGHT, 171),
                ClubPlayer("vn_la_thanh_long", "Đỗ Thanh Long", "vn_long_an", 2, 24, "Việt Nam", "VN", Position.LB, 53, 58, 34, 46, 48, 53, 55, 30_000, 600, Foot.LEFT, 172),
                ClubPlayer("vn_la_hoang_anh", "Võ Hoàng Anh", "vn_long_an", 15, 23, "Việt Nam", "VN", Position.RB, 53, 58, 32, 45, 47, 54, 56, 30_000, 600, Foot.RIGHT, 174),
                ClubPlayer("vn_la_minh_tri", "Lê Minh Trí", "vn_long_an", 6, 22, "Việt Nam", "VN", Position.CDM, 52, 50, 38, 50, 46, 54, 57, 28_000, 600, Foot.RIGHT, 175),
                ClubPlayer("vn_la_van_tien", "Hồ Văn Tiến", "vn_long_an", 22, 21, "Việt Nam", "VN", Position.GK, 50, 35, 16, 42, 34, 52, 55, 20_000, 500, Foot.RIGHT, 183)
            ),

            "vn_brvt" to listOf(
                ClubPlayer("vn_brvt_khanh_duy", "Nguyễn Khánh Duy", "vn_brvt", 1, 24, "Việt Nam", "VN", Position.GK, 53, 38, 18, 46, 38, 55, 58, 30_000, 600, Foot.RIGHT, 182),
                ClubPlayer("vn_brvt_trung_thanh", "Nguyễn Trung Thành", "vn_brvt", 4, 23, "Việt Nam", "VN", Position.CB, 54, 52, 28, 45, 42, 56, 60, 35_000, 700, Foot.RIGHT, 180),
                ClubPlayer("vn_brvt_tuan_em", "Lê Tuấn Em", "vn_brvt", 5, 22, "Việt Nam", "VN", Position.CB, 53, 50, 26, 43, 39, 55, 59, 30_000, 650, Foot.RIGHT, 181),
                ClubPlayer("vn_brvt_phuong_thinh", "Tô Phương Thịnh", "vn_brvt", 10, 23, "Việt Nam", "VN", Position.CAM, 55, 60, 52, 58, 57, 42, 52, 40_000, 800, Foot.RIGHT, 170),
                ClubPlayer("vn_brvt_quyet_cuong", "Lê Quyết Cường", "vn_brvt", 9, 24, "Việt Nam", "VN", Position.ST, 56, 62, 58, 49, 55, 40, 60, 45_000, 850, Foot.RIGHT, 175),
                ClubPlayer("vn_brvt_hoang_phuc", "Võ Hoàng Phúc", "vn_brvt", 7, 22, "Việt Nam", "VN", Position.LW, 54, 65, 49, 50, 56, 36, 50, 35_000, 700, Foot.LEFT, 169),
                ClubPlayer("vn_brvt_the_dung", "Bùi Thế Dũng", "vn_brvt", 2, 23, "Việt Nam", "VN", Position.LB, 53, 58, 34, 46, 48, 53, 55, 30_000, 600, Foot.LEFT, 171),
                ClubPlayer("vn_brvt_van_dong", "Trần Văn Đồng", "vn_brvt", 17, 24, "Việt Nam", "VN", Position.RB, 53, 58, 32, 45, 47, 54, 56, 30_000, 600, Foot.RIGHT, 173),
                ClubPlayer("vn_brvt_minh_nhat", "Lê Minh Nhật", "vn_brvt", 8, 22, "Việt Nam", "VN", Position.CM, 53, 53, 46, 54, 52, 48, 55, 30_000, 650, Foot.RIGHT, 172),
                ClubPlayer("vn_brvt_quoc_dat", "Nguyễn Quốc Đạt", "vn_brvt", 6, 21, "Việt Nam", "VN", Position.CDM, 52, 50, 38, 50, 46, 54, 57, 28_000, 600, Foot.RIGHT, 176),
                ClubPlayer("vn_brvt_van_dat", "Lê Văn Đạt", "vn_brvt", 25, 20, "Việt Nam", "VN", Position.GK, 50, 35, 16, 42, 34, 52, 55, 20_000, 500, Foot.RIGHT, 181)
            ),

            "vn_hoa_binh" to listOf(
                ClubPlayer("vn_hb_nam_hai", "Nguyễn Nam Hải", "vn_hoa_binh", 1, 23, "Việt Nam", "VN", Position.GK, 53, 38, 18, 46, 38, 55, 58, 30_000, 600, Foot.RIGHT, 183),
                ClubPlayer("vn_hb_van_dung", "Trần Văn Dũng", "vn_hoa_binh", 4, 24, "Việt Nam", "VN", Position.CB, 53, 50, 26, 44, 40, 55, 60, 32_000, 650, Foot.RIGHT, 180),
                ClubPlayer("vn_hb_quoc_dat", "Bùi Quốc Đạt", "vn_hoa_binh", 5, 22, "Việt Nam", "VN", Position.CB, 52, 50, 25, 42, 38, 54, 59, 28_000, 600, Foot.RIGHT, 179),
                ClubPlayer("vn_hb_gia_huy", "Trần Gia Huy", "vn_hoa_binh", 10, 21, "Việt Nam", "VN", Position.CAM, 56, 62, 54, 61, 62, 40, 50, 50_000, 900, Foot.LEFT, 165),
                ClubPlayer("vn_hb_anh_thong", "Bùi Anh Thống", "vn_hoa_binh", 9, 22, "Việt Nam", "VN", Position.ST, 55, 62, 57, 49, 54, 38, 59, 42_000, 800, Foot.RIGHT, 176),
                ClubPlayer("vn_hb_viet_hoang", "Nguyễn Việt Hoàng", "vn_hoa_binh", 7, 23, "Việt Nam", "VN", Position.LW, 54, 64, 48, 50, 56, 36, 50, 35_000, 700, Foot.RIGHT, 171),
                ClubPlayer("vn_hb_the_anh", "Vũ Thế Anh", "vn_hoa_binh", 2, 22, "Việt Nam", "VN", Position.LB, 53, 58, 34, 46, 48, 53, 55, 30_000, 600, Foot.LEFT, 170),
                ClubPlayer("vn_hb_hai_quan", "Lê Hải Quân", "vn_hoa_binh", 17, 23, "Việt Nam", "VN", Position.RB, 53, 58, 32, 45, 47, 54, 56, 30_000, 600, Foot.RIGHT, 172),
                ClubPlayer("vn_hb_xuan_thinh", "Trần Xuân Thịnh", "vn_hoa_binh", 8, 22, "Việt Nam", "VN", Position.CM, 53, 53, 46, 54, 52, 48, 55, 30_000, 650, Foot.RIGHT, 173),
                ClubPlayer("vn_hb_van_bach", "Nguyễn Văn Bách", "vn_hoa_binh", 6, 21, "Việt Nam", "VN", Position.CDM, 52, 50, 38, 50, 46, 54, 57, 28_000, 600, Foot.RIGHT, 175),
                ClubPlayer("vn_hb_hoang_long", "Đỗ Hoàng Long", "vn_hoa_binh", 22, 20, "Việt Nam", "VN", Position.GK, 50, 35, 16, 42, 34, 52, 55, 20_000, 500, Foot.RIGHT, 182)
            ),

            // ==========================================
            // V-LEAGUE 1 (GIẢI VÔ ĐỊCH QUỐC GIA VIỆT NAM)
            // ==========================================
            "vn_nam_dinh" to listOf(
                ClubPlayer("vn_nd_nguyen_manh", "Trần Nguyên Mạnh", "vn_nam_dinh", 26, 33, "Việt Nam", "VN", Position.GK, 68, 44, 22, 60, 48, 70, 71, 280_000, 3_800, Foot.RIGHT, 178),
                ClubPlayer("vn_nd_thanh_hao", "Dương Thanh Hào", "vn_nam_dinh", 3, 33, "Việt Nam", "VN", Position.CB, 65, 58, 30, 54, 52, 68, 72, 160_000, 2_200, Foot.RIGHT, 180),
                ClubPlayer("vn_nd_lucas", "Lucas Alves", "vn_nam_dinh", 4, 32, "Brazil", "BR", Position.CB, 69, 62, 35, 58, 55, 72, 78, 350_000, 4_500, Foot.RIGHT, 188),
                ClubPlayer("vn_nd_hong_duy", "Nguyễn Phong Hồng Duy", "vn_nam_dinh", 7, 28, "Việt Nam", "VN", Position.LB, 66, 70, 48, 65, 67, 62, 64, 220_000, 3_000, Foot.LEFT, 168),
                ClubPlayer("vn_nd_van_vu", "Tô Văn Vũ", "vn_nam_dinh", 88, 31, "Việt Nam", "VN", Position.RB, 67, 68, 55, 66, 68, 64, 68, 250_000, 3_500, Foot.RIGHT, 172),
                ClubPlayer("vn_nd_hoang_anh", "Lý Công Hoàng Anh", "vn_nam_dinh", 6, 25, "Việt Nam", "VN", Position.CM, 66, 66, 56, 67, 66, 60, 68, 240_000, 3_200, Foot.RIGHT, 170),
                ClubPlayer("vn_nd_hendrio", "Hendrio Araujo", "vn_nam_dinh", 10, 30, "Brazil", "BR", Position.CAM, 71, 68, 72, 76, 75, 48, 66, 500_000, 6_000, Foot.LEFT, 181),
                ClubPlayer("vn_xuan_son", "Nguyễn Xuân Son (Rafaelson)", "vn_nam_dinh", 14, 27, "Việt Nam", "VN", Position.ST, 74, 76, 78, 68, 74, 52, 82, 800_000, 9_000, Foot.RIGHT, 185),
                ClubPlayer("vn_nd_van_toan", "Nguyễn Văn Toàn", "vn_nam_dinh", 9, 28, "Việt Nam", "VN", Position.RW, 68, 82, 64, 62, 71, 38, 62, 350_000, 4_500, Foot.RIGHT, 169),
                ClubPlayer("vn_nd_van_dat", "Trần Văn Đạt", "vn_nam_dinh", 19, 24, "Việt Nam", "VN", Position.LW, 63, 72, 58, 58, 65, 42, 58, 140_000, 1_800, Foot.LEFT, 173),
                ClubPlayer("vn_nd_duc_dung", "Trần Đức Dũng", "vn_nam_dinh", 1, 27, "Việt Nam", "VN", Position.GK, 60, 40, 20, 52, 44, 62, 65, 80_000, 1_200, Foot.RIGHT, 182)
            ),

            "vn_hanoi_fc" to listOf(
                ClubPlayer("vn_hn_van_chuan", "Quan Văn Chuẩn", "vn_hanoi_fc", 37, 23, "Việt Nam", "VN", Position.GK, 66, 44, 22, 58, 48, 68, 70, 250_000, 3_200, Foot.RIGHT, 182),
                ClubPlayer("vn_hn_duy_manh", "Đỗ Duy Mạnh", "vn_hanoi_fc", 28, 28, "Việt Nam", "VN", Position.CB, 68, 62, 42, 60, 58, 72, 74, 380_000, 5_000, Foot.RIGHT, 180),
                ClubPlayer("vn_hn_thanh_chung", "Nguyễn Thành Chung", "vn_hanoi_fc", 16, 27, "Việt Nam", "VN", Position.CB, 68, 60, 48, 58, 56, 73, 75, 380_000, 5_000, Foot.RIGHT, 182),
                ClubPlayer("vn_hn_xuan_manh", "Phạm Xuân Mạnh", "vn_hanoi_fc", 7, 28, "Việt Nam", "VN", Position.RB, 67, 68, 52, 64, 66, 68, 72, 300_000, 4_000, Foot.RIGHT, 175),
                ClubPlayer("vn_hn_hung_dung", "Đỗ Hùng Dũng", "vn_hanoi_fc", 88, 31, "Việt Nam", "VN", Position.CM, 68, 62, 60, 72, 68, 67, 72, 350_000, 4_500, Foot.RIGHT, 170),
                ClubPlayer("vn_hn_hai_long", "Nguyễn Hai Long", "vn_hanoi_fc", 14, 24, "Việt Nam", "VN", Position.CAM, 66, 68, 63, 69, 69, 50, 62, 260_000, 3_500, Foot.RIGHT, 168),
                ClubPlayer("vn_hn_van_quyet", "Nguyễn Văn Quyết", "vn_hanoi_fc", 10, 33, "Việt Nam", "VN", Position.LW, 71, 65, 73, 74, 75, 45, 66, 450_000, 6_000, Foot.RIGHT, 172),
                ClubPlayer("vn_hn_tuan_hai", "Phạm Tuấn Hải", "vn_hanoi_fc", 9, 26, "Việt Nam", "VN", Position.ST, 70, 74, 72, 64, 70, 52, 75, 550_000, 7_000, Foot.RIGHT, 172),
                ClubPlayer("vn_hn_van_tung", "Nguyễn Văn Tùng", "vn_hanoi_fc", 89, 23, "Việt Nam", "VN", Position.ST, 64, 68, 65, 56, 64, 42, 68, 170_000, 2_200, Foot.RIGHT, 180),
                ClubPlayer("vn_hn_van_xuan", "Lê Văn Xuân", "vn_hanoi_fc", 45, 25, "Việt Nam", "VN", Position.LB, 65, 68, 45, 62, 64, 63, 65, 200_000, 2_800, Foot.RIGHT, 168),
                ClubPlayer("vn_hn_van_cong", "Nguyễn Văn Công", "vn_hanoi_fc", 1, 32, "Việt Nam", "VN", Position.GK, 62, 40, 20, 54, 42, 64, 66, 120_000, 1_800, Foot.RIGHT, 180)
            ),

            "vn_cahn" to listOf(
                ClubPlayer("vn_cahn_filip", "Filip Nguyễn", "vn_cahn", 1, 32, "Việt Nam", "VN", Position.GK, 70, 45, 25, 65, 52, 73, 76, 500_000, 6_500, Foot.RIGHT, 192),
                ClubPlayer("vn_cahn_viet_anh", "Bùi Hoàng Việt Anh", "vn_cahn", 68, 25, "Việt Nam", "VN", Position.CB, 69, 64, 46, 62, 60, 74, 77, 450_000, 5_800, Foot.RIGHT, 184),
                ClubPlayer("vn_cahn_dinh_trong", "Trần Đình Trọng", "vn_cahn", 21, 27, "Việt Nam", "VN", Position.CB, 65, 56, 35, 58, 55, 70, 68, 220_000, 3_000, Foot.RIGHT, 175),
                ClubPlayer("vn_cahn_van_thanh", "Vũ Văn Thanh", "vn_cahn", 17, 28, "Việt Nam", "VN", Position.RB, 68, 76, 58, 66, 68, 66, 72, 380_000, 5_000, Foot.RIGHT, 170),
                ClubPlayer("vn_cahn_van_hau", "Đoàn Văn Hậu", "vn_cahn", 5, 25, "Việt Nam", "VN", Position.LB, 68, 74, 55, 65, 66, 70, 78, 400_000, 5_200, Foot.LEFT, 185),
                ClubPlayer("vn_cahn_quang_hai", "Nguyễn Quang Hải", "vn_cahn", 19, 27, "Việt Nam", "VN", Position.CAM, 72, 70, 74, 77, 76, 46, 64, 650_000, 8_000, Foot.LEFT, 168),
                ClubPlayer("vn_cahn_quang_nho", "Lê Phạm Thành Long", "vn_cahn", 11, 28, "Việt Nam", "VN", Position.CM, 66, 64, 58, 69, 67, 62, 66, 250_000, 3_500, Foot.RIGHT, 168),
                ClubPlayer("vn_cahn_geovane", "Geovane Magno", "vn_cahn", 94, 30, "Brazil", "BR", Position.CAM, 71, 72, 73, 75, 76, 45, 68, 550_000, 7_000, Foot.LEFT, 188),
                ClubPlayer("vn_cahn_leo_artur", "Leo Artur", "vn_cahn", 10, 29, "Brazil", "BR", Position.ST, 72, 75, 74, 69, 74, 42, 70, 600_000, 7_500, Foot.RIGHT, 178),
                ClubPlayer("vn_cahn_dinh_bac", "Nguyễn Đình Bắc", "vn_cahn", 16, 20, "Việt Nam", "VN", Position.LW, 66, 78, 65, 62, 71, 40, 64, 280_000, 3_500, Foot.RIGHT, 180),
                ClubPlayer("vn_cahn_si_huy", "Đỗ Sỹ Huy", "vn_cahn", 33, 26, "Việt Nam", "VN", Position.GK, 60, 40, 20, 52, 44, 62, 65, 90_000, 1_300, Foot.RIGHT, 181)
            ),

            "vn_viettel" to listOf(
                ClubPlayer("vn_vt_the_tai", "Quàng Thế Tài", "vn_viettel", 25, 29, "Việt Nam", "VN", Position.GK, 62, 40, 20, 55, 45, 64, 66, 120_000, 1_800, Foot.RIGHT, 182),
                ClubPlayer("vn_vt_tien_dung", "Bùi Tiến Dũng", "vn_viettel", 4, 29, "Việt Nam", "VN", Position.CB, 68, 60, 38, 58, 55, 74, 75, 380_000, 5_000, Foot.RIGHT, 176),
                ClubPlayer("vn_vt_thanh_binh", "Nguyễn Thanh Bình", "vn_viettel", 3, 24, "Việt Nam", "VN", Position.CB, 67, 62, 35, 56, 52, 72, 76, 320_000, 4_200, Foot.RIGHT, 183),
                ClubPlayer("vn_vt_duc_chien", "Nguyễn Đức Chiến", "vn_viettel", 21, 26, "Việt Nam", "VN", Position.CDM, 67, 60, 56, 64, 60, 72, 78, 300_000, 4_000, Foot.RIGHT, 184),
                ClubPlayer("vn_vt_tuan_tai", "Phan Tuấn Tài", "vn_viettel", 12, 23, "Việt Nam", "VN", Position.LB, 66, 68, 50, 68, 65, 63, 62, 260_000, 3_500, Foot.LEFT, 173),
                ClubPlayer("vn_vt_khang", "Khuất Văn Khang", "vn_viettel", 11, 21, "Việt Nam", "VN", Position.CAM, 66, 70, 64, 70, 71, 50, 62, 280_000, 3_800, Foot.LEFT, 171),
                ClubPlayer("vn_vt_danh_trung", "Trần Danh Trung", "vn_viettel", 19, 24, "Việt Nam", "VN", Position.ST, 63, 69, 63, 56, 64, 38, 62, 140_000, 1_900, Foot.LEFT, 174),
                ClubPlayer("vn_vt_nham_manh_dung", "Nhâm Mạnh Dũng", "vn_viettel", 23, 24, "Việt Nam", "VN", Position.ST, 66, 68, 66, 60, 65, 55, 73, 250_000, 3_300, Foot.LEFT, 181),
                ClubPlayer("vn_vt_hoang_minh", "Nguyễn Hoàng Minh", "vn_viettel", 17, 25, "Việt Nam", "VN", Position.RW, 63, 73, 58, 57, 66, 42, 58, 140_000, 1_800, Foot.RIGHT, 172),
                ClubPlayer("vn_vt_xuan_kien", "Đặng Văn Trâm", "vn_viettel", 8, 29, "Việt Nam", "VN", Position.CM, 64, 62, 54, 65, 64, 62, 66, 160_000, 2_200, Foot.RIGHT, 172),
                ClubPlayer("vn_vt_xuan_son_gk", "Đoàn Huy Hoàng", "vn_viettel", 1, 21, "Việt Nam", "VN", Position.GK, 58, 38, 18, 50, 42, 60, 62, 70_000, 1_000, Foot.RIGHT, 180)
            ),

            "vn_binh_duong" to listOf(
                ClubPlayer("vn_bd_minh_toan", "Trần Minh Toàn", "vn_binh_duong", 25, 28, "Việt Nam", "VN", Position.GK, 66, 42, 22, 58, 48, 68, 70, 220_000, 3_000, Foot.RIGHT, 186),
                ClubPlayer("vn_bd_ngoc_hai", "Quế Ngọc Hải", "vn_binh_duong", 3, 31, "Việt Nam", "VN", Position.CB, 68, 60, 44, 66, 58, 74, 76, 360_000, 4_800, Foot.RIGHT, 182),
                ClubPlayer("vn_bd_janclesio", "Janclesio Almeida", "vn_binh_duong", 4, 31, "Brazil", "BR", Position.CB, 68, 56, 35, 55, 50, 75, 82, 320_000, 4_200, Foot.RIGHT, 195),
                ClubPlayer("vn_bd_minh_khoa", "Võ Hoàng Minh Khoa", "vn_binh_duong", 29, 23, "Việt Nam", "VN", Position.CM, 66, 68, 60, 68, 67, 62, 68, 260_000, 3_500, Foot.RIGHT, 173),
                ClubPlayer("vn_tien_linh", "Nguyễn Tiến Linh", "vn_binh_duong", 22, 27, "Việt Nam", "VN", Position.ST, 71, 71, 74, 64, 68, 48, 74, 600_000, 7_500, Foot.RIGHT, 180),
                ClubPlayer("vn_bd_vi_hao", "Bùi Vĩ Hào", "vn_binh_duong", 11, 21, "Việt Nam", "VN", Position.LW, 66, 77, 64, 60, 69, 44, 66, 280_000, 3_800, Foot.RIGHT, 180),
                ClubPlayer("vn_bd_tung_quoc", "Ngô Tùng Quốc", "vn_binh_duong", 2, 26, "Việt Nam", "VN", Position.RB, 65, 68, 45, 62, 64, 63, 66, 200_000, 2_800, Foot.RIGHT, 172),
                ClubPlayer("vn_bd_thanh_thao", "Nguyễn Thành Thảo", "vn_binh_duong", 17, 25, "Việt Nam", "VN", Position.LB, 64, 67, 42, 60, 62, 62, 64, 180_000, 2_500, Foot.LEFT, 171),
                ClubPlayer("vn_bd_trung_hieu", "Trần Trung Hiếu", "vn_binh_duong", 39, 31, "Uganda", "UG", Position.CDM, 66, 62, 45, 63, 60, 70, 78, 240_000, 3_200, Foot.RIGHT, 182),
                ClubPlayer("vn_bd_hai_huy", "Nguyễn Hải Huy", "vn_binh_duong", 14, 33, "Việt Nam", "VN", Position.CAM, 66, 58, 65, 70, 68, 52, 64, 220_000, 3_000, Foot.RIGHT, 172),
                ClubPlayer("vn_bd_tuan_vu", "Phan Minh Thành", "vn_binh_duong", 1, 26, "Việt Nam", "VN", Position.GK, 58, 38, 18, 50, 42, 60, 62, 70_000, 1_000, Foot.RIGHT, 183)
            ),

            "vn_slna" to listOf(
                ClubPlayer("vn_slna_van_viet", "Nguyễn Văn Việt", "vn_slna", 1, 22, "Việt Nam", "VN", Position.GK, 64, 42, 20, 56, 46, 66, 68, 180_000, 2_500, Foot.RIGHT, 180),
                ClubPlayer("vn_slna_dinh_hoang", "Trần Đình Hoàng", "vn_slna", 6, 33, "Việt Nam", "VN", Position.RB, 64, 64, 45, 62, 63, 65, 67, 160_000, 2_200, Foot.RIGHT, 172),
                ClubPlayer("vn_slna_zaracho", "Mario Zebic", "vn_slna", 4, 29, "Croatia", "HR", Position.CB, 66, 55, 30, 52, 48, 72, 78, 220_000, 3_000, Foot.RIGHT, 188),
                ClubPlayer("vn_slna_ba_quyen", "Phan Bá Quyền", "vn_slna", 19, 22, "Việt Nam", "VN", Position.CDM, 63, 62, 48, 62, 60, 64, 67, 150_000, 2_000, Foot.RIGHT, 176),
                ClubPlayer("vn_slna_xuan_tien", "Đinh Xuân Tiến", "vn_slna", 10, 21, "Việt Nam", "VN", Position.CAM, 65, 68, 64, 68, 68, 48, 62, 240_000, 3_200, Foot.RIGHT, 171),
                ClubPlayer("vn_slna_manh_quynh", "Trần Mạnh Quỳnh", "vn_slna", 11, 23, "Việt Nam", "VN", Position.LW, 63, 72, 58, 58, 65, 42, 58, 150_000, 2_000, Foot.RIGHT, 172),
                ClubPlayer("vn_slna_olaha", "Michael Olaha", "vn_slna", 7, 27, "Nigeria", "NG", Position.ST, 68, 70, 70, 65, 70, 48, 76, 350_000, 4_500, Foot.RIGHT, 186),
                ClubPlayer("vn_slna_van_luong", "Ngô Văn Lương", "vn_slna", 9, 23, "Việt Nam", "VN", Position.ST, 62, 66, 62, 54, 62, 40, 64, 120_000, 1_600, Foot.RIGHT, 177),
                ClubPlayer("vn_slna_ngoc_my", "Lê Nguyên Hoàng", "vn_slna", 3, 19, "Việt Nam", "VN", Position.CB, 62, 60, 28, 52, 48, 65, 68, 150_000, 1_800, Foot.RIGHT, 178),
                ClubPlayer("vn_slna_van_huy", "Vương Văn Huy", "vn_slna", 2, 20, "Việt Nam", "VN", Position.LB, 62, 67, 40, 58, 61, 62, 60, 130_000, 1_700, Foot.LEFT, 170),
                ClubPlayer("vn_slna_cao_van_binh", "Cao Văn Bình", "vn_slna", 25, 19, "Việt Nam", "VN", Position.GK, 59, 40, 18, 52, 42, 61, 63, 100_000, 1_300, Foot.RIGHT, 183)
            ),

            "vn_hagl" to listOf(
                ClubPlayer("vn_hagl_vu_hai", "Trần Trung Kiên", "vn_hagl", 25, 21, "Việt Nam", "VN", Position.GK, 64, 42, 20, 56, 46, 66, 68, 180_000, 2_500, Foot.RIGHT, 191),
                ClubPlayer("vn_hagl_jairo", "Jairo Rodrigues", "vn_hagl", 33, 31, "Brazil", "BR", Position.CB, 67, 56, 45, 56, 52, 73, 79, 280_000, 3_800, Foot.RIGHT, 190),
                ClubPlayer("vn_hagl_quang_nho", "Dụng Quang Nho", "vn_hagl", 86, 24, "Việt Nam", "VN", Position.RB, 66, 72, 52, 64, 67, 65, 72, 240_000, 3_200, Foot.RIGHT, 170),
                ClubPlayer("vn_hagl_chau_ngoc_quang", "Châu Ngọc Quang", "vn_hagl", 8, 28, "Việt Nam", "VN", Position.CM, 67, 66, 62, 69, 68, 60, 68, 300_000, 4_000, Foot.RIGHT, 175),
                ClubPlayer("vn_hagl_minh_vuong", "Trần Minh Vương", "vn_hagl", 10, 29, "Việt Nam", "VN", Position.CAM, 68, 65, 68, 72, 71, 52, 64, 320_000, 4_200, Foot.RIGHT, 166),
                ClubPlayer("vn_hagl_marciele", "Marciel Silva", "vn_hagl", 22, 29, "Brazil", "BR", Position.CM, 68, 65, 66, 71, 70, 64, 71, 320_000, 4_200, Foot.LEFT, 186),
                ClubPlayer("vn_hagl_jeferson", "Jeferson Elias", "vn_hagl", 9, 26, "Brazil", "BR", Position.ST, 68, 70, 69, 62, 68, 44, 75, 340_000, 4_500, Foot.RIGHT, 183),
                ClubPlayer("vn_hagl_quoc_viet", "Nguyễn Quốc Việt", "vn_hagl", 11, 21, "Việt Nam", "VN", Position.ST, 64, 74, 64, 58, 66, 38, 60, 200_000, 2_800, Foot.RIGHT, 173),
                ClubPlayer("vn_hagl_van_son", "Lê Văn Sơn", "vn_hagl", 2, 28, "Việt Nam", "VN", Position.RB, 64, 66, 44, 62, 63, 63, 64, 170_000, 2_300, Foot.RIGHT, 169),
                ClubPlayer("vn_hagl_canh_anh", "Nguyễn Cảnh Anh", "vn_hagl", 4, 24, "Việt Nam", "VN", Position.CB, 62, 58, 28, 52, 46, 65, 68, 140_000, 1_800, Foot.RIGHT, 180),
                ClubPlayer("vn_hagl_duong_van_loi", "Dương Văn Lợi", "vn_hagl", 1, 24, "Việt Nam", "VN", Position.GK, 58, 38, 18, 50, 42, 60, 62, 80_000, 1_100, Foot.RIGHT, 184)
            ),

            "vn_hai_phong" to listOf(
                ClubPlayer("vn_hp_dinh_trieu", "Nguyễn Đình Triệu", "vn_hai_phong", 1, 33, "Việt Nam", "VN", Position.GK, 67, 44, 22, 58, 48, 69, 71, 250_000, 3_400, Foot.RIGHT, 180),
                ClubPlayer("vn_hp_tien_dung", "Đặng Văn Tới", "vn_hai_phong", 5, 25, "Việt Nam", "VN", Position.CB, 65, 60, 32, 56, 52, 68, 72, 200_000, 2_700, Foot.RIGHT, 178),
                ClubPlayer("vn_hp_bicou", "Bicou Bissainthe", "vn_hai_phong", 66, 25, "Haiti", "HT", Position.CDM, 67, 66, 55, 66, 64, 71, 76, 280_000, 3_800, Foot.RIGHT, 185),
                ClubPlayer("vn_hp_viet_hung", "Triệu Việt Hưng", "vn_hai_phong", 97, 27, "Việt Nam", "VN", Position.CM, 67, 68, 63, 70, 69, 62, 68, 300_000, 4_000, Foot.LEFT, 171),
                ClubPlayer("vn_hp_hoang_nam", "Lương Hoàng Nam", "vn_hai_phong", 8, 27, "Việt Nam", "VN", Position.CAM, 65, 66, 60, 69, 68, 52, 62, 200_000, 2_700, Foot.RIGHT, 160),
                ClubPlayer("vn_hp_lucao", "Lucao do Break", "vn_hai_phong", 9, 33, "Brazil", "BR", Position.ST, 69, 68, 71, 64, 69, 45, 78, 380_000, 5_000, Foot.RIGHT, 184),
                ClubPlayer("vn_hp_huu_son", "Nguyễn Hữu Sơn", "vn_hai_phong", 77, 28, "Việt Nam", "VN", Position.RW, 65, 72, 62, 60, 67, 44, 62, 210_000, 2_800, Foot.LEFT, 173),
                ClubPlayer("vn_hp_van_minh", "Nguyễn Văn Minh", "vn_hai_phong", 17, 25, "Việt Nam", "VN", Position.LW, 63, 70, 56, 58, 65, 42, 58, 140_000, 1_900, Foot.RIGHT, 171),
                ClubPlayer("vn_hp_trung_hieu", "Phạm Trung Hiếu", "vn_hai_phong", 2, 26, "Việt Nam", "VN", Position.RB, 65, 70, 45, 62, 64, 64, 67, 210_000, 2_800, Foot.RIGHT, 172),
                ClubPlayer("vn_hp_van_toi", "Bùi Tiến Dụng", "vn_hai_phong", 16, 26, "Việt Nam", "VN", Position.CB, 64, 58, 32, 54, 50, 67, 72, 170_000, 2_300, Foot.RIGHT, 176),
                ClubPlayer("vn_hp_van_toan", "Nguyễn Văn Toản", "vn_hai_phong", 30, 25, "Việt Nam", "VN", Position.GK, 63, 40, 20, 54, 44, 65, 67, 160_000, 2_200, Foot.RIGHT, 186)
            ),

            // ==========================================
            // PREMIER LEAGUE & CHAMPIONSHIP (TIÊU BIỂU)
            // ==========================================
            "en_mancity" to listOf(
                ClubPlayer("en_ederson", "Ederson", "en_mancity", 31, 31, "Brazil", "BR", Position.GK, 88, 64, 30, 85, 75, 87, 86, 45_000_000, 180_000, Foot.LEFT, 188),
                ClubPlayer("en_dias", "Ruben Dias", "en_mancity", 3, 27, "Bồ Đào Nha", "PT", Position.CB, 88, 65, 39, 70, 68, 89, 87, 75_000_000, 220_000, Foot.RIGHT, 187),
                ClubPlayer("en_gvardiol", "Josko Gvardiol", "en_mancity", 24, 22, "Croatia", "HR", Position.LB, 84, 78, 62, 76, 80, 83, 84, 70_000_000, 160_000, Foot.LEFT, 185),
                ClubPlayer("en_walker", "Kyle Walker", "en_mancity", 2, 34, "Anh", "EN", Position.RB, 84, 87, 63, 77, 78, 80, 82, 20_000_000, 150_000, Foot.RIGHT, 183),
                ClubPlayer("en_rodri", "Rodri", "en_mancity", 16, 28, "Tây Ban Nha", "ES", Position.CDM, 91, 66, 80, 86, 84, 87, 85, 110_000_000, 250_000, Foot.RIGHT, 190),
                ClubPlayer("en_kdb", "Kevin De Bruyne", "en_mancity", 17, 33, "Bỉ", "BE", Position.CAM, 90, 67, 87, 94, 87, 65, 75, 60_000_000, 300_000, Foot.RIGHT, 181),
                ClubPlayer("en_foden", "Phil Foden", "en_mancity", 47, 24, "Anh", "EN", Position.CAM, 88, 86, 85, 87, 89, 57, 65, 120_000_000, 240_000, Foot.LEFT, 171),
                ClubPlayer("en_silva", "Bernardo Silva", "en_mancity", 20, 30, "Bồ Đào Nha", "PT", Position.CM, 88, 69, 78, 86, 92, 70, 75, 70_000_000, 210_000, Foot.LEFT, 173),
                ClubPlayer("en_doku", "Jeremy Doku", "en_mancity", 11, 22, "Bỉ", "BE", Position.LW, 82, 92, 68, 75, 88, 38, 68, 65_000_000, 120_000, Foot.RIGHT, 173),
                ClubPlayer("en_haaland", "Erling Haaland", "en_mancity", 9, 24, "Na Uy", "NO", Position.ST, 91, 89, 93, 70, 81, 45, 88, 180_000_000, 375_000, Foot.LEFT, 195),
                ClubPlayer("en_ortega", "Stefan Ortega", "en_mancity", 18, 32, "Đức", "DE", Position.GK, 80, 50, 25, 78, 65, 80, 80, 12_000_000, 80_000, Foot.RIGHT, 185)
            ),

            "en_arsenal" to listOf(
                ClubPlayer("en_raya", "David Raya", "en_arsenal", 22, 29, "Tây Ban Nha", "ES", Position.GK, 84, 52, 28, 82, 68, 84, 82, 35_000_000, 120_000, Foot.RIGHT, 183),
                ClubPlayer("en_saliba", "William Saliba", "en_arsenal", 2, 23, "Pháp", "FR", Position.CB, 87, 82, 40, 72, 78, 87, 83, 80_000_000, 190_000, Foot.RIGHT, 192),
                ClubPlayer("en_gabriel", "Gabriel Magalhaes", "en_arsenal", 6, 26, "Brazil", "BR", Position.CB, 86, 70, 45, 68, 70, 86, 85, 70_000_000, 160_000, Foot.LEFT, 190),
                ClubPlayer("en_white", "Ben White", "en_arsenal", 4, 27, "Anh", "EN", Position.RB, 82, 78, 55, 76, 78, 82, 78, 50_000_000, 140_000, Foot.RIGHT, 186),
                ClubPlayer("en_rice", "Declan Rice", "en_arsenal", 41, 25, "Anh", "EN", Position.CDM, 87, 76, 72, 82, 80, 86, 86, 110_000_000, 240_000, Foot.RIGHT, 188),
                ClubPlayer("en_odegaard", "Martin Odegaard", "en_arsenal", 8, 25, "Na Uy", "NO", Position.CAM, 89, 75, 83, 89, 88, 64, 68, 110_000_000, 240_000, Foot.LEFT, 178),
                ClubPlayer("en_saka", "Bukayo Saka", "en_arsenal", 7, 23, "Anh", "EN", Position.RW, 87, 85, 83, 83, 87, 65, 76, 130_000_000, 220_000, Foot.LEFT, 178),
                ClubPlayer("en_martinelli", "Gabriel Martinelli", "en_arsenal", 11, 23, "Brazil", "BR", Position.LW, 83, 89, 78, 76, 85, 45, 72, 70_000_000, 150_000, Foot.RIGHT, 178),
                ClubPlayer("en_havertz", "Kai Havertz", "en_arsenal", 29, 25, "Đức", "DE", Position.ST, 83, 80, 82, 79, 81, 55, 78, 65_000_000, 180_000, Foot.LEFT, 193),
                ClubPlayer("en_timber", "Jurrien Timber", "en_arsenal", 12, 23, "Hà Lan", "NL", Position.LB, 81, 80, 52, 78, 79, 80, 80, 45_000_000, 110_000, Foot.RIGHT, 179),
                ClubPlayer("en_neto", "Neto", "en_arsenal", 32, 35, "Brazil", "BR", Position.GK, 78, 48, 24, 74, 60, 78, 77, 6_000_000, 60_000, Foot.RIGHT, 190)
            ),

            "en_sunderland" to listOf(
                ClubPlayer("en_sun_patterson", "Anthony Patterson", "en_sunderland", 1, 24, "Anh", "EN", Position.GK, 72, 48, 25, 68, 55, 72, 74, 4_500_000, 18_000, Foot.RIGHT, 188),
                ClubPlayer("en_sun_ballard", "Dan Ballard", "en_sunderland", 5, 25, "Bắc Ireland", "NIR", Position.CB, 72, 64, 42, 60, 58, 73, 78, 5_000_000, 20_000, Foot.RIGHT, 187),
                ClubPlayer("en_sun_o_nien", "Luke O'Nien", "en_sunderland", 13, 29, "Anh", "EN", Position.CB, 71, 68, 55, 66, 68, 71, 75, 3_500_000, 16_000, Foot.RIGHT, 174),
                ClubPlayer("en_sun_cirkin", "Dennis Cirkin", "en_sunderland", 3, 22, "Anh", "EN", Position.LB, 72, 78, 52, 68, 71, 71, 74, 5_500_000, 22_000, Foot.LEFT, 183),
                ClubPlayer("en_sun_hume", "Trai Hume", "en_sunderland", 32, 22, "Bắc Ireland", "NIR", Position.RB, 73, 76, 54, 69, 72, 73, 76, 6_500_000, 24_000, Foot.RIGHT, 182),
                ClubPlayer("en_sun_bellingham", "Jobe Bellingham", "en_sunderland", 7, 19, "Anh", "EN", Position.CAM, 74, 75, 72, 74, 76, 65, 76, 12_000_000, 30_000, Foot.RIGHT, 191),
                ClubPlayer("en_sun_neil", "Dan Neil", "en_sunderland", 24, 22, "Anh", "EN", Position.CM, 73, 70, 68, 75, 74, 69, 73, 7_000_000, 25_000, Foot.RIGHT, 178),
                ClubPlayer("en_sun_roberts", "Patrick Roberts", "en_sunderland", 10, 27, "Anh", "EN", Position.RW, 73, 78, 70, 72, 79, 42, 60, 5_000_000, 22_000, Foot.LEFT, 168),
                ClubPlayer("en_sun_isidor", "Wilson Isidor", "en_sunderland", 18, 24, "Pháp", "FR", Position.ST, 74, 82, 74, 66, 75, 40, 74, 8_000_000, 28_000, Foot.RIGHT, 180),
                ClubPlayer("en_sun_mayenda", "Eliezer Mayenda", "en_sunderland", 12, 19, "Tây Ban Nha", "ES", Position.ST, 70, 80, 68, 62, 71, 38, 70, 4_000_000, 15_000, Foot.LEFT, 179),
                ClubPlayer("en_sun_moore", "Simon Moore", "en_sunderland", 21, 34, "Anh", "EN", Position.GK, 66, 42, 22, 58, 48, 66, 68, 800_000, 10_000, Foot.RIGHT, 191)
            ),

            "en_leeds" to listOf(
                ClubPlayer("en_lee_meslier", "Illan Meslier", "en_leeds", 1, 24, "Pháp", "FR", Position.GK, 75, 52, 28, 72, 60, 75, 76, 12_000_000, 35_000, Foot.LEFT, 197),
                ClubPlayer("en_lee_struijk", "Pascal Struijk", "en_leeds", 5, 25, "Hà Lan", "NL", Position.CB, 75, 68, 52, 68, 66, 76, 80, 10_000_000, 32_000, Foot.LEFT, 190),
                ClubPlayer("en_lee_rodon", "Joe Rodon", "en_leeds", 6, 27, "Xứ Wales", "WAL", Position.CB, 74, 66, 40, 64, 62, 75, 82, 9_000_000, 30_000, Foot.RIGHT, 193),
                ClubPlayer("en_lee_bogle", "Jayden Bogle", "en_leeds", 2, 24, "Anh", "EN", Position.RB, 74, 80, 60, 70, 74, 71, 74, 8_500_000, 28_000, Foot.RIGHT, 178),
                ClubPlayer("en_lee_firpo", "Junior Firpo", "en_leeds", 3, 28, "Cộng hòa Dominica", "DO", Position.LB, 74, 78, 62, 71, 73, 72, 76, 7_000_000, 28_000, Foot.LEFT, 184),
                ClubPlayer("en_lee_ampadu", "Ethan Ampadu", "en_leeds", 4, 24, "Xứ Wales", "WAL", Position.CDM, 75, 70, 58, 72, 71, 76, 79, 11_000_000, 35_000, Foot.RIGHT, 182),
                ClubPlayer("en_lee_tanaka", "Ao Tanaka", "en_leeds", 22, 26, "Nhật Bản", "JP", Position.CM, 74, 72, 68, 75, 75, 70, 74, 8_000_000, 30_000, Foot.RIGHT, 180),
                ClubPlayer("en_lee_gnonto", "Wilfried Gnonto", "en_leeds", 29, 21, "Ý", "IT", Position.LW, 76, 88, 74, 73, 82, 45, 70, 16_000_000, 40_000, Foot.RIGHT, 170),
                ClubPlayer("en_lee_james", "Daniel James", "en_leeds", 7, 26, "Xứ Wales", "WAL", Position.RW, 75, 93, 72, 70, 77, 50, 70, 12_000_000, 38_000, Foot.RIGHT, 171),
                ClubPlayer("en_lee_piroe", "Joel Piroe", "en_leeds", 10, 25, "Hà Lan", "NL", Position.ST, 76, 74, 78, 70, 74, 45, 74, 14_000_000, 40_000, Foot.LEFT, 185),
                ClubPlayer("en_lee_darlow", "Karl Darlow", "en_leeds", 26, 34, "Anh", "EN", Position.GK, 70, 45, 22, 64, 52, 70, 71, 1_500_000, 18_000, Foot.RIGHT, 190)
            )
        )
    }

    /**
     * Tự động sinh danh sách cầu thủ cho một CLB chưa có danh sách chi tiết,
     * đảm bảo luôn có đủ thủ môn, hậu vệ, tiền vệ và tiền đạo.
     */
    private fun generateDefaultSquad(clubId: String): List<ClubPlayer> {
        val clubName = clubId.replace("vn_", "").replace("en_", "").replace("_", " ").uppercase()
        return listOf(
            ClubPlayer("${clubId}_gk1", "$clubName Thủ Môn 1", clubId, 1, 26, "Việt Nam", "VN", Position.GK, 54, 40, 20, 48, 40, 55, 58, 40_000, 700, Foot.RIGHT, 185),
            ClubPlayer("${clubId}_cb1", "$clubName Trung Vệ 1", clubId, 4, 25, "Việt Nam", "VN", Position.CB, 54, 52, 30, 46, 42, 56, 61, 45_000, 800, Foot.RIGHT, 182),
            ClubPlayer("${clubId}_cb2", "$clubName Trung Vệ 2", clubId, 5, 24, "Việt Nam", "VN", Position.CB, 53, 50, 28, 44, 40, 55, 60, 40_000, 750, Foot.RIGHT, 180),
            ClubPlayer("${clubId}_lb", "$clubName Hậu Vệ Trái", clubId, 3, 23, "Việt Nam", "VN", Position.LB, 54, 60, 36, 48, 50, 54, 56, 42_000, 750, Foot.LEFT, 172),
            ClubPlayer("${clubId}_rb", "$clubName Hậu Vệ Phải", clubId, 2, 24, "Việt Nam", "VN", Position.RB, 54, 60, 35, 47, 49, 54, 57, 42_000, 750, Foot.RIGHT, 174),
            ClubPlayer("${clubId}_cm1", "$clubName Tiền Vệ 1", clubId, 8, 25, "Việt Nam", "VN", Position.CM, 55, 56, 50, 58, 56, 52, 58, 50_000, 900, Foot.RIGHT, 174),
            ClubPlayer("${clubId}_cm2", "$clubName Tiền Vệ 2", clubId, 6, 23, "Việt Nam", "VN", Position.CDM, 54, 52, 42, 53, 49, 55, 59, 45_000, 800, Foot.RIGHT, 176),
            ClubPlayer("${clubId}_cam", "$clubName Tiền Vệ Công", clubId, 10, 24, "Việt Nam", "VN", Position.CAM, 56, 60, 54, 61, 60, 44, 54, 55_000, 950, Foot.RIGHT, 170),
            ClubPlayer("${clubId}_lw", "$clubName Cánh Trái", clubId, 11, 22, "Việt Nam", "VN", Position.LW, 55, 66, 52, 52, 59, 38, 52, 48_000, 850, Foot.LEFT, 171),
            ClubPlayer("${clubId}_rw", "$clubName Cánh Phải", clubId, 7, 23, "Việt Nam", "VN", Position.RW, 55, 66, 52, 52, 59, 38, 52, 48_000, 850, Foot.RIGHT, 172),
            ClubPlayer("${clubId}_st", "$clubName Tiền Đạo Cắm", clubId, 9, 26, "Việt Nam", "VN", Position.ST, 57, 63, 60, 50, 56, 42, 61, 60_000, 1_100, Foot.RIGHT, 178),
            ClubPlayer("${clubId}_gk2", "$clubName Thủ Môn 2", clubId, 25, 20, "Việt Nam", "VN", Position.GK, 50, 35, 16, 42, 34, 52, 55, 25_000, 500, Foot.RIGHT, 183)
        )
    }
}
