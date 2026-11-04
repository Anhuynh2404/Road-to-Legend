# HƯỚNG DẪN CHI TIẾT TỰ THÊM ASSETS (LOGO, GIẢI ĐẤU, CẦU THỦ) & MODDING
**Dự án:** Road to Legend (Football Player Career Simulator) • **Phiên bản:** Version 1.0

Tài liệu này là cẩm nang hướng dẫn đầy đủ từ A-Z dành cho nhà phát triển và người chơi muốn tự tay thêm logo CLB, giải đấu mới, cầu thủ và hình ảnh chân dung vào mã nguồn trò chơi.

---

## 1. TẠI SAO BẠN THÊM ẢNH VÀO `res/drawable` NHƯNG CHƯA THẤY HIỂN THỊ?

Khi bạn tải file ảnh vào thư mục `app/src/main/res/drawable/` nhưng giao diện vẫn hiển thị huy hiệu khiên màu mặc định, nguyên nhân là:

### 1.1. Lệch mã định danh (Prefix ID)
Trong mã nguồn `DefaultDatabase.kt`, mã định danh (ID) của các câu lạc bộ thường có tiền tố quốc gia:
- Hà Nội FC: ID là `vn_hanoi_fc` ➔ Code cũ tìm kiếm file: `logo_vn_hanoi_fc.png`.
- Becamex Bình Dương: ID là `vn_binh_duong` ➔ Code cũ tìm kiếm: `logo_vn_binh_duong.png`.
- PVF-CAND: ID là `vn_pvf` ➔ Code cũ tìm kiếm: `logo_vn_pvf.png`.

Trong khi đó, bạn đặt tên file là: `logo_hanoi.png`, `logo_binh_duong.png`, `logo_pvf_cand.png`. Do không khớp tên, hàm `getIdentifier` trả về `0` và kích hoạt huy hiệu vẽ bằng Canvas.

### 1.2. Giải pháp đã cập nhật trong Version 1:
Hàm `findClubLogoResId(context, club)` trong `ClubBadge.kt` đã được nâng cấp thông minh:
1. Tự động kiểm tra tên có prefix quốc gia: `logo_vn_...`
2. Tự động kiểm tra tên bỏ prefix: `logo_binh_duong`, `logo_nam_dinh`, `logo_viettel`...
3. Tự động nhận diện bí danh rút gọn: `logo_hanoi`, `logo_pvf_cand`, `logo_tphcm`, `logo_da_nang`...
4. Tự động kiểm tra theo `shortName` viết thường gạch dưới.

Nhờ vậy, **tất cả 16 file logo bạn vừa thêm đều đã được nhận diện tự động và hiển thị chính xác** trên mọi màn hình: Dashboard, BXH, Lịch thi đấu, Hộp thư chuyển nhượng!

---

## 2. QUY CHUẨN ĐẶT TÊN & ĐỊNH DẠNG HÌNH ẢNH TRONG ANDROID

Hệ thống tài nguyên Android (`res/drawable`) tuân thủ nghiêm ngặt các quy tắc sau:

1. **Quy tắc đặt tên file (bắt buộc):**
   - Chỉ được dùng **chữ thường** (`a-z`), **chữ số** (`0-9`) và **dấu gạch dưới** (`_`).
   - ❌ **SAI:** `Logo_Hanoi.PNG`, `logo ha noi.png`, `logo-nam-dinh.png`, `logo_đà_nẵng.png`.
   - ✅ **ĐÚNG:** `logo_hanoi.png`, `logo_nam_dinh.png`, `logo_da_nang.png`.
2. **Định dạng khuyến nghị:**
   - Sử dụng `.png` hoặc `.webp` có **nền trong suốt (Transparent background)** để logo hòa trộn đẹp mắt với nền giao diện Dark Mode.
3. **Kích thước ảnh đề xuất:**
   - **Logo CLB:** `256 x 256 px` hoặc `512 x 512 px` (Hình vuông, tỉ lệ 1:1).
   - **Ảnh chân dung cầu thủ:** `256 x 256 px` hoặc `512 x 512 px` (Cắt headshot/cutout từ ngực đến đầu, nền trong suốt).

---

## 3. BẢNG TRA CỨU FILE LOGO 16 CÂU LẠC BỘ VIỆT NAM (ĐÃ TÍCH HỢP)

Dưới đây là danh sách đối chiếu chính xác giữa file logo bạn đã thêm và câu lạc bộ trong game:

| Tên Câu Lạc Bộ | Giải đấu | Mã ID trong code | Tên file logo trong `res/drawable` |
| :--- | :--- | :--- | :--- |
| **Thép Xanh Nam Định** | V-League 1 | `vn_nam_dinh` | `logo_nam_dinh.png` |
| **Hà Nội FC** | V-League 1 | `vn_hanoi_fc` | `logo_hanoi.png` |
| **Công An Hà Nội (CAHN)** | V-League 1 | `vn_cahn` | `logo_cahn.png` |
| **Thể Công Viettel** | V-League 1 | `vn_viettel` | `logo_viettel.png` |
| **Becamex Bình Dương** | V-League 1 | `vn_binh_duong` | `logo_binh_duong.png` |
| **Sông Lam Nghệ An (SLNA)**| V-League 1 | `vn_slna` | `logo_slna.png` |
| **Hoàng Anh Gia Lai (HAGL)**| V-League 1 | `vn_hagl` | `logo_hagl.png` |
| **Hải Phòng FC** | V-League 1 | `vn_hai_phong` | `logo_hai_phong.png` |
| **Đông Á Thanh Hóa** | V-League 1 | `vn_thanh_hoa` | `logo_thanh_hoa.png` |
| **CLB TP. Hồ Chí Minh** | V-League 1 | `vn_tphcm` | `logo_tphcm.png` |
| **SHB Đà Nẵng** | V-League 1 | `vn_da_nang` | `logo_da_nang.png` |
| **Huế FC** | V-League 2 | `vn_hue` | `logo_hue.png` |
| **Trường Tươi Bình Phước** | V-League 2 | `vn_binh_phuoc` | `logo_binh_phuoc.png` |
| **Phù Đổng Ninh Bình** | V-League 2 | `vn_ninh_binh` | `logo_ninh_binh.png` |
| **PVF-CAND** | V-League 2 | `vn_pvf` | `logo_pvf_cand.png` |
| **Bà Rịa Vũng Tàu** | V-League 2 | `vn_brvt` | `logo_brvt.png` |

---

## 4. HƯỚNG DẪN THÊM CÂU LẠC BỘ & GIẢI ĐẤU MỚI

Nếu bạn muốn tạo thêm một giải đấu mới (ví dụ: La Liga Tây Ban Nha, Cúp C1 Châu Âu, V-League 3...):

### Bước 4.1. Khai báo danh sách CLB trong `DefaultDatabase.kt`
Mở file `app/src/main/java/com/example/roadtolegend/data/DefaultDatabase.kt`:

```kotlin
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
    ),
    Club(
        id = "es_barcelona",
        name = "FC Barcelona",
        shortName = "Barcelona",
        country = "Tây Ban Nha",
        leagueId = "la_liga",
        tier = 1,
        reputation = 93,
        budget = 80_000_000,
        trainingFacilityLevel = 5,
        coachQuality = 5,
        squadOvrAverage = 85,
        primaryColorHex = "#004D98",
        secondaryColorHex = "#A50044",
        keyPlayers = listOf("Lamine Yamal", "Robert Lewandowski", "Pedri"),
        fictionalName = "Gã Khổng Lồ Xứ Catalan",
        fictionalShortName = "Barca"
    )
)
```

### Bước 4.2. Đăng ký vào hàm `getAllClubs()`
Cũng trong file `DefaultDatabase.kt`, tìm hàm `getAllClubs()` và thêm danh sách mới:
```kotlin
fun getAllClubs(): List<Club> =
    createVLeague2Clubs() +
    createVLeague1Clubs() +
    createSpanishLaLigaClubs() + // <-- Thêm vào đây
    ...
```

### Bước 4.3. Khai báo giải đấu trong `initializeLeague(leagueId)`
Tìm hàm `initializeLeague(leagueId)` trong `DefaultDatabase.kt` và thêm nhánh `when`:
```kotlin
"la_liga" -> Quadruple("La Liga Tây Ban Nha", "Tây Ban Nha", 1, createSpanishLaLigaClubs())
```

### Bước 4.4. Thêm file logo cho CLB mới
Đặt file logo vào `app/src/main/res/drawable/`:
- `logo_real_madrid.png`
- `logo_barcelona.png`

---

## 5. HƯỚNG DẪN THÊM CẦU THỦ THỦ CÔNG & GÁN ẢNH CHÂN DUNG

### Bước 5.1. Thêm cầu thủ vào `SquadDatabase.kt`
Mở file `app/src/main/java/com/example/roadtolegend/data/SquadDatabase.kt`. Tìm đến `baseSquadsByClub` và thêm:

```kotlin
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
        avatarResName = "player_courtois" // <-- Tên file ảnh (không bao gồm đuôi .png)
    ),
    ClubPlayer(
        id = "es_vinicius",
        name = "Vinicius Junior",
        clubId = "es_real_madrid",
        number = 7,
        age = 24,
        nationality = "Brazil",
        nationalityCode = "BR",
        position = Position.LW,
        ovr = 90, pac = 95, sho = 84, pas = 81, dri = 91, def = 32, phy = 69,
        marketValue = 180_000_000,
        weeklyWage = 350_000,
        preferredFoot = Foot.RIGHT,
        heightCm = 176,
        avatarResName = "player_vinicius"
    )
)
```

### Bước 5.2. Thêm ảnh chân dung vào `res/drawable`
Đặt file ảnh:
- `app/src/main/res/drawable/player_courtois.png`
- `app/src/main/res/drawable/player_vinicius.png`

**Cơ chế bảo vệ (Fallback):** Nếu cầu thủ chưa có file ảnh thật, game sẽ tự động dựng hình đại diện thể thao vector hoặc khiên thẻ FUT có số áo và chỉ số OVR chuẩn mực, không bao giờ bị lỗi hiển thị.

---

## 6. QUY TRÌNH BUILD & PHÁT HÀNH (VERSION 1)

1. Mọi thay đổi logic đều được bảo vệ bằng hệ thống Unit Test tự động (`gradle :app:testDebugUnitTest`).
2. Mã nguồn tuân thủ kiến trúc Clean Architecture: Tách biệt rõ ràng tầng logic Game Engine (`engine/`) khỏi tầng giao diện Jetpack Compose (`ui/`).
3. Giao diện trung tâm sự nghiệp (Career Management Hub) đã được tinh chỉnh với bố cục 2 cột thoáng đãng, chỉ báo trạng thái trực quan và không bị tràn chữ trên mọi kích thước màn hình điện thoại.
