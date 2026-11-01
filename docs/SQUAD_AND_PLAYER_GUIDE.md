# HƯỚNG DẪN QUẢN LÝ, THÊM CẦU THỦ & HÌNH ẢNH (FIFA / EA SPORTS FC)
**Dự án:** Road to Legend (Football Player Career Simulator)

Tài liệu này hướng dẫn chi tiết cách tự tay thêm mới, chỉnh sửa dữ liệu cầu thủ và chèn ảnh chân dung (face cutout) cho từng cầu thủ của các câu lạc bộ trong game.

---

## 1. NƠI LƯU TRỮ DỮ LIỆU CẦU THỦ

Toàn bộ danh sách cầu thủ của tất cả các CLB được quản lý tập trung tại file:
📁 **`app/src/main/java/com/example/roadtolegend/data/SquadDatabase.kt`**

File định nghĩa model dữ liệu cầu thủ:
📁 **`app/src/main/java/com/example/roadtolegend/model/ClubPlayer.kt`**

---

## 2. CẤU TRÚC DỮ LIỆU CẦU THỦ (`ClubPlayer`)

Mỗi cầu thủ được khai báo bằng đối tượng `ClubPlayer` chuẩn FIFA:

```kotlin
ClubPlayer(
    id = "vn_cong_phuong",         // ID duy nhất, viết thường, không dấu, nối bằng dấu gạch dưới
    name = "Nguyễn Công Phượng",   // Tên đầy đủ hiển thị trong game
    clubId = "vn_binh_phuoc",      // Mã ID của CLB chủ quản (tra ở bảng mục 5)
    number = 70,                   // Số áo thi đấu chính thức
    age = 29,                      // Tuổi
    nationality = "Việt Nam",      // Tên quốc gia
    nationalityCode = "VN",        // Mã quốc gia hiển thị trên góc thẻ (VN, BR, EN, FR, JP...)
    position = Position.ST,        // Vị trí thi đấu (GK, CB, LB, RB, CDM, CM, CAM, LW, RW, ST)
    ovr = 66,                      // Chỉ số tổng quát (OVR) từ 1 đến 99
    pac = 68,                      // Tốc độ (Pace)
    sho = 67,                      // Dứt điểm (Shooting)
    pas = 65,                      // Chuyền bóng (Passing)
    dri = 72,                      // Rê bóng / Kỹ thuật (Dribbling)
    def = 36,                      // Phòng ngự (Defending)
    phy = 58,                      // Thể lực & Sức mạnh (Physicality)
    marketValue = 220_000,         // Định giá thị trường (€ hoặc USD)
    weeklyWage = 3_000,            // Lương tuần (€ hoặc USD)
    preferredFoot = Foot.RIGHT,    // Chân thuận: Foot.RIGHT, Foot.LEFT hoặc Foot.BOTH
    heightCm = 168,                // Chiều cao (cm)
    avatarResName = "player_vn_cong_phuong" // Tên file ảnh (không bao gồm đuôi .png)
)
```

---

## 3. CÁCH THÊM CẦU THỦ MỚI VÀO CLB

1. Mở file `app/src/main/java/com/example/roadtolegend/data/SquadDatabase.kt`.
2. Tìm đến khối `baseSquadsByClub = mapOf(...)`.
3. Tìm đến ID của CLB bạn muốn bổ sung (ví dụ: `"vn_binh_phuoc"` hoặc `"vn_cahn"`).
4. Thêm một dòng `ClubPlayer(...)` vào danh sách `listOf(...)`.

### Mẫu Copy & Paste nhanh:

```kotlin
// Thêm thủ môn
ClubPlayer("vn_cahn_filip", "Filip Nguyễn", "vn_cahn", 1, 32, "Việt Nam", "VN", Position.GK, 70, 45, 25, 65, 52, 73, 76, 500_000, 6_500, Foot.RIGHT, 192),

// Thêm trung vệ
ClubPlayer("vn_cahn_viet_anh", "Bùi Hoàng Việt Anh", "vn_cahn", 68, 25, "Việt Nam", "VN", Position.CB, 69, 64, 46, 62, 60, 74, 77, 450_000, 5_800, Foot.RIGHT, 184),

// Thêm tiền vệ
ClubPlayer("vn_cahn_quang_hai", "Nguyễn Quang Hải", "vn_cahn", 19, 27, "Việt Nam", "VN", Position.CAM, 72, 70, 74, 77, 76, 46, 64, 650_000, 8_000, Foot.LEFT, 168),

// Thêm tiền đạo
ClubPlayer("vn_nd_xuan_son", "Nguyễn Xuân Son (Rafaelson)", "vn_nam_dinh", 14, 27, "Việt Nam", "VN", Position.ST, 74, 76, 78, 68, 74, 52, 82, 800_000, 9_000, Foot.RIGHT, 185)
```

---

## 4. HƯỚNG DẪN THÊM ẢNH CHÂN DUNG CẦU THỦ

Hệ thống hỗ trợ cơ chế nạp ảnh **tự động và linh hoạt**:

### 4.1. Thư mục chứa ảnh
Đặt tất cả ảnh chân dung cầu thủ vào thư mục:
📁 **`app/src/main/res/drawable/`**

### 4.2. Quy tắc đặt tên file ảnh
- Tên file bắt đầu bằng tiền tố `player_` theo sau là ID của cầu thủ.
- Ví dụ:
  - Cầu thủ có `id = "vn_cong_phuong"` ➔ Tên file: `player_vn_cong_phuong.png`
  - Cầu thủ có `id = "vn_hoang_duc"` ➔ Tên file: `player_vn_hoang_duc.png`
  - Cầu thủ có `id = "en_haaland"` ➔ Tên file: `player_en_haaland.png`
- **Lưu ý tên file trong Android:** Chỉ dùng chữ cái thường (`a-z`), số (`0-9`) và dấu gạch dưới (`_`). Tuyệt đối không dùng chữ hoa, dấu cách hoặc dấu tiếng Việt.

### 4.3. Thông số kỹ thuật khuyến nghị cho ảnh
- **Định dạng:** `.png` hoặc `.webp` (nền trong suốt - Transparent background).
- **Kích thước:** `256 x 256 px` hoặc `512 x 512 px`.
- **Tỉ lệ khung hình:** `1:1` (hình vuông), cắt chân dung từ ngực đến đầu (headshot/cutout).

### 4.4. Cơ chế Fallback tự động khi chưa có ảnh
Nếu bạn chưa kịp thêm ảnh vào thư mục `res/drawable/`, giao diện sẽ **không bao giờ bị lỗi hoặc khoảng trắng**:
- Game tự động render **Huy hiệu Khiên thẻ cầu thủ FIFA** với màu chuyển sắc (gradient) theo đúng vị trí:
  - Vàng / Cam: Thủ môn (GK)
  - Lam đậm: Hậu vệ (CB, LB, RB)
  - Lục bảo: Tiền vệ (CDM, CM, CAM)
  - Đỏ san hô: Tiền đạo (ST, LW, RW)
  - Vàng ánh kim cao cấp: Cầu thủ người chơi (BẠN)
- Hiển thị chữ cái viết tắt tên cầu thủ (ví dụ: `CP` cho Công Phượng, `HĐ` cho Hoàng Đức) cùng số áo chính thức và cờ quốc gia.

---

## 5. BẢNG MÃ ID CÂU LẠC BỘ TRONG HỆ THỐNG

| Giải đấu | ID Câu Lạc Bộ (`clubId`) | Tên Câu Lạc Bộ |
| :--- | :--- | :--- |
| **V-League 2** | `vn_binh_phuoc` | Trường Tươi Bình Phước |
| | `vn_ninh_binh` | Phù Đổng Ninh Bình |
| | `vn_pvf` | PVF-CAND |
| | `vn_hue` | CLB Bóng Đá Huế |
| | `vn_dong_thap` | Đồng Tháp FC |
| | `vn_long_an` | Long An FC |
| | `vn_brvt` | Bà Rịa Vũng Tàu |
| | `vn_hoa_binh` | Hoà Bình FC |
| **V-League 1** | `vn_nam_dinh` | Thép Xanh Nam Định |
| | `vn_hanoi_fc` | Hà Nội FC |
| | `vn_cahn` | Công An Hà Nội |
| | `vn_viettel` | Thể Công Viettel |
| | `vn_binh_duong` | Becamex Bình Dương |
| | `vn_slna` | Sông Lam Nghệ An |
| | `vn_hagl` | Hoàng Anh Gia Lai |
| | `vn_hai_phong` | Hải Phòng FC |
| **EFL Championship** | `en_sunderland` | Sunderland AFC |
| | `en_leeds` | Leeds United |
| | `en_burnley` | Burnley FC |
| | `en_sheffield_utd` | Sheffield United |
| **Premier League** | `en_mancity` | Manchester City |
| | `en_arsenal` | Arsenal FC |
| | `en_liverpool` | Liverpool FC |
| | `en_man_utd` | Manchester United |

---

## 6. HƯỚNG DẪN CÂN ĐỐI CHỈ SỐ THEO TIÊU CHUẨN FIFA

| Hạng Cầu Thủ | Mức OVR Khuyến Nghị | Ví Dụ |
| :--- | :---: | :--- |
| **Hạng Nhất (V-League 2)** | 50 – 63 | Đa số nội binh trẻ |
| **Sao Hạng Nhất / Tuyển thủ** | 64 – 70 | Công Phượng (66), Hoàng Đức (70), Đặng Văn Lâm (68) |
| **V-League 1 tiêu chuẩn** | 60 – 68 | Cầu thủ đá chính V-League |
| **Ngôi sao V-League 1 / Ngoại binh** | 69 – 75 | Quang Hải (72), Rafaelson (74), Filip Nguyễn (70) |
| **EFL Championship (Anh)** | 70 – 76 | Jobe Bellingham (74), Piroe (76), Gnonto (76) |
| **Siêu sao Ngoại Hạng Anh** | 84 – 91 | Haaland (91), De Bruyne (90), Saka (87) |
