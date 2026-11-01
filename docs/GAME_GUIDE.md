# ROAD TO LEGEND - HƯỚNG DẪN KỸ THUẬT & TÀI LIỆU VẬN HÀNH

Tài liệu này cung cấp toàn bộ kiến trúc sự kiện mô phỏng trận đấu, hướng dẫn bổ sung logo câu lạc bộ thủ công, bảng cân bằng tham số game (Balancing), và danh sách kiểm tra (Checklist) trải nghiệm thực tế trên Emulator.

---

## 1. KIẾN TRÚC MÔ PHỎNG TRẬN ĐẤU (EVENT-DRIVEN TIMELINE ARCHITECTURE)

### 1.1. Nguyên nhân lỗi lật ngược tỉ số ở bản cũ và giải pháp
- **Nguyên nhân cũ:** Phiên bản cũ gọi lại toàn bộ hàm sinh sự kiện ngẫu nhiên trong mỗi vòng lặp `while(displayedMinute <= 90)`, khiến các bàn thắng bị xáo trộn và chỉ số bàn thắng có lúc bị tụt lùi.
- **Giải pháp dứt điểm:** Thiết kế cấu trúc dòng thời gian tất định duy nhất (`MatchTimeline`).
  - Hàm `MatchSimulator.generateMatchTimeline(match, ...)` tính toán trọn vẹn diễn biến 90 phút trước khi trận đấu bắt đầu.
  - Danh sách bàn thắng (`goalsList`) được chốt cố định theo phút.
  - Tỉ số hiển thị trên bảng điện tử tại phút $M$ được suy ra một chiều (strictly monotonic):
    $$\text{HomeScore}(M) = \sum \{g \in \text{goalsList} \mid g.\text{clubId} == \text{homeClub.id} \land g.\text{minute} \le M\}$$
  - Hệ thống gọi `match.verifyScoreInvariant()` để đảm bảo bất biến toán học: số bàn thắng của CLB luôn bằng số phần tử trong `goalsList`.

### 1.2. Phân bổ xác suất và sự kiện nhân quả (Causal Event Distribution)
- Trận đấu được chia thành các nhịp kiểm soát bóng (Possession phases) dựa trên OVR của hàng tiền vệ và phòng ngự 2 đội.
- Từ cơ hội nguy hiểm $\rightarrow$ Dứt điểm (Shot) $\rightarrow$ Trúng đích (On Target) / Bị cản phá (Saved) / Bàn thắng (Goal).
- Tỉ lệ bàn thắng trung bình được kiểm soát chặt chẽ ở mức 2.5 - 3.2 bàn/trận (chuẩn bóng đá thế giới).
- **Tình huống quyết định tương tác (Interactive Decision Moments):**
  - Chỉ xuất hiện từ 0 đến 3 lần/trận khi người chơi có mặt trên sân và tham gia pha bóng.
  - Mỗi tình huống cách nhau tối thiểu 15 phút.
  - Giao diện KHÔNG hiển thị phần trăm xác suất nhằm duy trì cảm giác căng thẳng chân thực.
  - Người chơi có thể bật/tắt tính năng này trong phần Cài đặt (Profile & Save).

---

## 2. HƯỚNG DẪN BỔ SUNG LOGO CÂU LẠC BỘ THỦ CÔNG (CUSTOM CLUB BADGES)

Game được thiết kế cơ chế nạp logo linh hoạt 2 tầng:
1. **Tầng 1 (Resource Asset):** Tìm drawable mang tên `logo_<club_id>` trong thư mục Android resources.
2. **Tầng 2 (Vector Shield Fallback):** Nếu chưa có file ảnh PNG/WebP, game tự động vẽ một chiếc khiên huy hiệu bóng đá hoàng gia bằng Canvas vector với 2 màu chủ đạo của CLB và chữ viết tắt (`shortName`).

### Các bước thêm ảnh Logo thật vào dự án:
1. Chuẩn bị file ảnh Logo câu lạc bộ định dạng `.png` (khuyến nghị nền trong suốt PNG, kích thước chuẩn $256 \times 256$ px hoặc $512 \times 512$ px).
2. Đổi tên file theo đúng quy tắc chữ thường: `logo_<club_id>.png`.
3. Đặt file vào thư mục:
   ```text
   app/src/main/res/drawable/
   ```
4. Build lại ứng dụng. Giao diện `ClubBadge` sẽ tự động nhận diện và hiển thị logo mới thay cho khiên vector mặc định.

### Bảng mã Club ID:
| Quốc gia | Giải đấu | Tên Câu Lạc Bộ | Club ID | Tên file cần đặt |
| :--- | :--- | :--- | :--- | :--- |
| **Việt Nam** | V-League 1 | Công An Hà Nội | `cahn` | `logo_cahn.png` |
| | V-League 1 | Hà Nội FC | `hanoi` | `logo_hanoi.png` |
| | V-League 1 | Thể Công - Viettel | `viettel` | `logo_viettel.png` |
| | V-League 1 | Thép Xanh Nam Định | `nam_dinh` | `logo_nam_dinh.png` |
| | V-League 1 | Hải Phòng | `hai_phong` | `logo_hai_phong.png` |
| | V-League 1 | Becamex Bình Dương | `binh_duong` | `logo_binh_duong.png` |
| | V-League 1 | Đông Á Thanh Hóa | `thanh_hoa` | `logo_thanh_hoa.png` |
| | V-League 1 | Hoàng Anh Gia Lai | `hagl` | `logo_hagl.png` |
| | V-League 1 | Sông Lam Nghệ An | `slna` | `logo_slna.png` |
| | V-League 1 | TP. Hồ Chí Minh | `tphcm` | `logo_tphcm.png` |
| | V-League 2 | PVF-CAND | `pvf_cand` | `logo_pvf_cand.png` |
| | V-League 2 | SHB Đà Nẵng | `da_nang` | `logo_da_nang.png` |
| | V-League 2 | Trường Tươi Bình Phước | `binh_phuoc` | `logo_binh_phuoc.png` |
| | V-League 2 | Phù Đổng Ninh Bình | `ninh_binh` | `logo_ninh_binh.png` |
| | V-League 2 | Bà Rịa Vũng Tàu | `brvt` | `logo_brvt.png` |
| | V-League 2 | Huế FC | `hue` | `logo_hue.png` |
| **Anh** | Premier League | Manchester City | `man_city` | `logo_man_city.png` |
| | Premier League | Arsenal | `arsenal` | `logo_arsenal.png` |
| | Premier League | Liverpool | `liverpool` | `logo_liverpool.png` |
| | Premier League | Manchester United | `man_utd` | `logo_man_utd.png` |
| | Premier League | Chelsea | `chelsea` | `logo_chelsea.png` |
| | Premier League | Tottenham Hotspur | `tottenham` | `logo_tottenham.png` |
| | Championship | Leicester City | `leicester` | `logo_leicester.png` |
| | Championship | Leeds United | `leeds` | `logo_leeds.png` |
| | Championship | Southampton | `southampton` | `logo_southampton.png` |
| | Championship | Sunderland | `sunderland` | `logo_sunderland.png` |

---

## 3. CÂN BẰNG THAM SỐ GAME (BALANCING & FORMULA TUNING)

Toàn bộ công thức và hằng số vận hành được gom gọn trong `com.example.roadtolegend.core.config.TrainingConfig` và `com.example.roadtolegend.core.config.BalanceConfig`:

### 3.1. Tập luyện (TrainingConfig.kt)
- **Hiệu ứng giảm dần theo Tiềm năng (Potential Diminishing Returns):**
  $$\text{Gap} = \text{Potential} - \text{CurrentStat}$$
  - Khi $\text{Gap} \le 3$: Tốc độ tăng chỉ số giảm còn $20\%$ (`DIMINISHING_RETURN_FACTOR_VERY_CLOSE = 0.20f`).
  - Khi $\text{Gap} \le 8$: Tốc độ tăng chỉ số giảm còn $55\%$ (`DIMINISHING_RETURN_FACTOR_CLOSE = 0.55f`).
- **Tác động của Tuổi (Age Modifier):**
  - Tuổi $16 - 21$: Điểm tăng hệ số $\times 1.25$ (giai đoạn vàng phát triển thể chất).
  - Tuổi $22 - 25$: Điểm tăng chuẩn $\times 1.0$.
  - Tuổi $26 - 29$: Điểm tăng $\times 0.75$.
  - Tuổi $30+$: Điểm tăng $\times 0.40$.
- **Cường độ tập luyện (Intensity Multipliers):**
  - **Nhẹ (Light):** Chi phí $\times 0.6$, Điểm tăng $\times 0.65$, Tiêu hao thể lực $\times 0.5$, Nguy cơ chấn thương $\times 0.3$.
  - **Vừa (Medium):** Chi phí $\times 1.0$, Điểm tăng $\times 1.0$, Tiêu hao thể lực $\times 1.0$, Nguy cơ chấn thương $\times 1.0$.
  - **Nặng (Heavy):** Chi phí $\times 1.6$, Điểm tăng $\times 1.45$, Tiêu hao thể lực $\times 1.7$, Nguy cơ chấn thương $\times 2.2$.
- **Rủi ro chấn thương do Mệt mỏi (Fatigue):**
  - Nếu Fatigue $\ge 70$, nguy cơ chấn thương tăng thêm $18\%$.
  - Nếu Fatigue $\ge 50$, nguy cơ chấn thương tăng thêm $8\%$.

---
1. Thêm CLB và Giải đấu mới (DefaultDatabase.kt)
Mở file app/src/main/java/com/example/roadtolegend/data/DefaultDatabase.kt:
Tạo hàm danh sách CLB:
code
Kotlin
fun createSpanishLaLigaClubs(): List<Club> = listOf(
    Club(
        id = "es_real_madrid",
        name = "Real Madrid CF",
        shortName = "Real Madrid",
        country = "Tây Ban Nha",
        leagueId = "la_liga",
        tier = 1,
        reputation = 95,
        budget = 120_000_000,
        trainingFacilityLevel = 5,
        coachQuality = 5,
        squadOvrAverage = 87,
        primaryColorHex = "#FFFFFF",
        secondaryColorHex = "#FEBE10",
        keyPlayers = listOf("Vinicius Jr", "Jude Bellingham", "Kylian Mbappe"),
        fictionalName = "Kền Kền Trắng",
        fictionalShortName = "Madrid"
    )
)
Khai báo vào getAllClubs():
Thêm + createSpanishLaLigaClubs() vào hàm getAllClubs().
Khai báo vào initializeLeague(leagueId):
Thêm nhánh "la_liga" -> Quadruple("La Liga", "Tây Ban Nha", 1, createSpanishLaLigaClubs()) vào when (leagueId).
2. Thêm cầu thủ thủ công & ảnh đại diện (SquadDatabase.kt)
Mở file app/src/main/java/com/example/roadtolegend/data/SquadDatabase.kt:
Tìm đến baseSquadsByClub và thêm danh sách cầu thủ của CLB:
code
Kotlin
"es_real_madrid" to listOf(
    ClubPlayer(
        id = "es_courtois",
        name = "Thibaut Courtois",
        clubId = "es_real_madrid",
        number = 1,
        age = 32,
        nationality = "Bỉ",
        nationalityCode = "BE",
        position = Position.GK,
        ovr = 89, pac = 46, sho = 20, pas = 74, dri = 50, def = 89, phy = 88,
        marketValue = 40_000_000,
        weeklyWage = 250_000,
        preferredFoot = Foot.LEFT,
        heightCm = 200,
        avatarResName = "player_courtois" // Đặt file ảnh player_courtois.png vào app/src/main/res/drawable/
    )
)
(Nếu chưa có ảnh trong drawable, hệ thống tự động sinh Avatar Vector theo số áo và màu áo CLB).


