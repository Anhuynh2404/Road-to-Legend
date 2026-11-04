package com.example.roadtolegend.data

import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.League
import com.example.roadtolegend.model.LifestyleCategory
import com.example.roadtolegend.model.LifestyleItem
import com.example.roadtolegend.model.PlayerStatLeader
import com.example.roadtolegend.model.TeamStanding

object DefaultDatabase {

    fun createVLeague2Clubs(): List<Club> = listOf(
        Club(
            id = "vn_hue",
            name = "CLB Bóng Đá Huế",
            shortName = "Huế FC",
            country = "Việt Nam",
            leagueId = "vleague_2",
            tier = 2,
            reputation = 28,
            budget = 50_000,
            trainingFacilityLevel = 2,
            coachQuality = 2,
            squadOvrAverage = 54,
            primaryColorHex = "#1E88E5",
            secondaryColorHex = "#FFFFFF",
            keyPlayers = listOf("Hồ Thanh Minh", "Trần Thành", "Nguyễn Văn Chiến", "Lê Ngọc Thiên"),
            fictionalName = "Cố Đô Thần Tốc",
            fictionalShortName = "Cố Đô FC"
        ),
        Club(
            id = "vn_binh_phuoc",
            name = "Trường Tươi Bình Phước",
            shortName = "Bình Phước",
            country = "Việt Nam",
            leagueId = "vleague_2",
            tier = 2,
            reputation = 35,
            budget = 120_000,
            trainingFacilityLevel = 3,
            coachQuality = 3,
            squadOvrAverage = 59,
            primaryColorHex = "#43A047",
            secondaryColorHex = "#FFEB3B",
            keyPlayers = listOf("Nguyễn Công Phượng", "Huỳnh Tấn Sinh", "Sầm Ngọc Đức", "Lưu Tự Nhân"),
            fictionalName = "Rừng Xanh Miền Đông",
            fictionalShortName = "Bình Phước FC"
        ),
        Club(
            id = "vn_ninh_binh",
            name = "Phù Đổng Ninh Bình",
            shortName = "Ninh Bình",
            country = "Việt Nam",
            leagueId = "vleague_2",
            tier = 2,
            reputation = 38,
            budget = 180_000,
            trainingFacilityLevel = 3,
            coachQuality = 3,
            squadOvrAverage = 62,
            primaryColorHex = "#D32F2F",
            secondaryColorHex = "#FFFFFF",
            keyPlayers = listOf("Nguyễn Hoàng Đức", "Đặng Văn Lâm", "Đinh Thanh Bình", "Lê Ngọc Bảo"),
            fictionalName = "Tràng An Chiến Binh",
            fictionalShortName = "Ninh Bình FC"
        ),
        Club(
            id = "vn_pvf",
            name = "PVF-CAND",
            shortName = "PVF-CAND",
            country = "Việt Nam",
            leagueId = "vleague_2",
            tier = 2,
            reputation = 36,
            budget = 100_000,
            trainingFacilityLevel = 4, // Học viện đào tạo trẻ hàng đầu
            coachQuality = 3,
            squadOvrAverage = 60,
            primaryColorHex = "#C2185B",
            secondaryColorHex = "#FFFFFF",
            keyPlayers = listOf("Nguyễn Thanh Nhàn", "Huỳnh Công Đến", "Lê Văn Đô", "Phí Minh Long"),
            fictionalName = "Học Viện Thép",
            fictionalShortName = "PVF FC"
        ),
        Club(
            id = "vn_dong_thap",
            name = "Đồng Tháp FC",
            shortName = "Đồng Tháp",
            country = "Việt Nam",
            leagueId = "vleague_2",
            tier = 2,
            reputation = 25,
            budget = 40_000,
            trainingFacilityLevel = 2,
            coachQuality = 2,
            squadOvrAverage = 52,
            primaryColorHex = "#FB8C00",
            secondaryColorHex = "#FFFFFF",
            keyPlayers = listOf("Nguyễn Tuấn Đạt", "Võ Minh Trọng", "Trần Hữu Thắng"),
            fictionalName = "Sen Hồng Miền Tây",
            fictionalShortName = "Đồng Tháp FC"
        ),
        Club(
            id = "vn_long_an",
            name = "Long An FC",
            shortName = "Long An",
            country = "Việt Nam",
            leagueId = "vleague_2",
            tier = 2,
            reputation = 27,
            budget = 45_000,
            trainingFacilityLevel = 2,
            coachQuality = 2,
            squadOvrAverage = 53,
            primaryColorHex = "#3949AB",
            secondaryColorHex = "#FFFFFF",
            keyPlayers = listOf("Nguyễn Anh Đức", "Phan Tấn Tài", "Cù Nguyễn Khánh"),
            fictionalName = "Vàm Cỏ Rồng Xanh",
            fictionalShortName = "Long An FC"
        ),
        Club(
            id = "vn_brvt",
            name = "Bà Rịa Vũng Tàu",
            shortName = "BRVT FC",
            country = "Việt Nam",
            leagueId = "vleague_2",
            tier = 2,
            reputation = 26,
            budget = 45_000,
            trainingFacilityLevel = 2,
            coachQuality = 2,
            squadOvrAverage = 53,
            primaryColorHex = "#00ACC1",
            secondaryColorHex = "#FFFFFF",
            keyPlayers = listOf("Lê Quyết Cường", "Tô Phương Thịnh", "Nguyễn Khánh Duy"),
            fictionalName = "Chiến Hạm Biển Đông",
            fictionalShortName = "Vũng Tàu FC"
        ),
        Club(
            id = "vn_hoa_binh",
            name = "Hoà Bình FC",
            shortName = "Hoà Bình",
            country = "Việt Nam",
            leagueId = "vleague_2",
            tier = 2,
            reputation = 26,
            budget = 40_000,
            trainingFacilityLevel = 2,
            coachQuality = 2,
            squadOvrAverage = 53,
            primaryColorHex = "#7CB342",
            secondaryColorHex = "#FFFFFF",
            keyPlayers = listOf("Bùi Anh Thống", "Nguyễn Nam Hải", "Trần Gia Huy"),
            fictionalName = "Đại Bàng Tây Bắc",
            fictionalShortName = "Hoà Bình FC"
        )
    )

    fun createVLeague1Clubs(): List<Club> = listOf(
        Club("vn_nam_dinh", "Thép Xanh Nam Định", "Nam Định", "Việt Nam", "vleague_1", 1, 55, 600_000, 3, 4, 69, "#FDD835", "#1565C0", listOf("Rafaelson (Nguyễn Xuân Son)", "Hendrio", "Tô Văn Vũ", "Trần Nguyên Mạnh"), "Thiên Trường Vàng", "Nam Định FC"),
        Club("vn_hanoi_fc", "Hà Nội FC", "Hà Nội FC", "Việt Nam", "vleague_1", 1, 58, 700_000, 4, 4, 70, "#7B1FA2", "#FFD700", listOf("Nguyễn Văn Quyết", "Phạm Tuấn Hải", "Đỗ Hùng Dũng", "Nguyễn Thành Chung"), "Thủ Đô Tím", "Hà Nội FC"),
        Club("vn_cahn", "Công An Hà Nội", "CAHN", "Việt Nam", "vleague_1", 1, 56, 800_000, 4, 4, 70, "#C62828", "#FFE082", listOf("Nguyễn Quang Hải", "Vũ Văn Thanh", "Đoàn Văn Hậu", "Filip Nguyễn"), "Hàng Đẫy Đỏ", "CAHN FC"),
        Club("vn_viettel", "Thể Công Viettel", "Viettel", "Việt Nam", "vleague_1", 1, 54, 550_000, 4, 4, 68, "#B71C1C", "#FFFFFF", listOf("Bùi Tiến Dũng", "Nguyễn Đức Chiến", "Khuất Văn Khang", "Trần Danh Trung"), "Áo Lính Đỏ", "Viettel FC"),
        Club("vn_binh_duong", "Becamex Bình Dương", "Bình Dương", "Việt Nam", "vleague_1", 1, 48, 450_000, 3, 3, 66, "#00838F", "#FFFFFF", listOf("Nguyễn Tiến Linh", "Quế Ngọc Hải", "Võ Hoàng Minh Khoa"), "Đất Thủ Xanh", "Bình Dương FC"),
        Club("vn_slna", "Sông Lam Nghệ An", "SLNA", "Việt Nam", "vleague_1", 1, 46, 300_000, 3, 3, 64, "#FBC02D", "#000000", listOf("Trần Đình Hoàng", "Đinh Xuân Tiến", "Trần Mạnh Quỳnh"), "Sứ Nghệ Vàng", "SLNA FC"),
        Club("vn_hagl", "Hoàng Anh Gia Lai", "HAGL", "Việt Nam", "vleague_1", 1, 50, 350_000, 3, 3, 65, "#FFFFFF", "#1E88E5", listOf("Trần Minh Vương", "Châu Ngọc Quang", "Dụng Quang Nho"), "Phố Núi Trắng", "HAGL FC"),
        Club("vn_hai_phong", "Hải Phòng FC", "Hải Phòng", "Việt Nam", "vleague_1", 1, 45, 300_000, 3, 3, 63, "#D32F2F", "#FFFFFF", listOf("Triệu Việt Hưng", "Lương Hoàng Nam", "Nguyễn Đình Triệu"), "Hoa Phượng Đỏ", "Hải Phòng FC"),
        Club("vn_thanh_hoa", "Đông Á Thanh Hóa", "Thanh Hóa", "Việt Nam", "vleague_1", 1, 52, 400_000, 3, 4, 66, "#FFD600", "#1E88E5", listOf("Rimario Gordon", "Gustavo Santos", "Doãn Ngọc Tân", "Nguyễn Thái Sơn"), "Chiến Binh Xứ Thanh", "Thanh Hóa FC"),
        Club("vn_tphcm", "CLB TP. Hồ Chí Minh", "TP.HCM", "Việt Nam", "vleague_1", 1, 48, 420_000, 3, 3, 65, "#D32F2F", "#1A237E", listOf("Võ Huy Toàn", "Patrik Lê Giang", "Hồ Tuấn Tài"), "Chiến Hạm Đỏ", "TP.HCM FC"),
        Club("vn_da_nang", "SHB Đà Nẵng", "Đà Nẵng", "Việt Nam", "vleague_1", 1, 47, 350_000, 3, 3, 64, "#FF9800", "#1565C0", listOf("Phan Văn Long", "Đặng Anh Tuấn", "Nguyễn Phi Hoàng"), "Sông Hàn Cam", "Đà Nẵng FC")
    )

    fun createChampionshipClubs(): List<Club> = listOf(
        Club("en_sunderland", "Sunderland AFC", "Sunderland", "Anh", "championship", 2, 65, 4_000_000, 4, 4, 73, "#D32F2F", "#FFFFFF", listOf("Jobe Bellingham", "Jack Clarke", "Wilson Isidor"), "Mèo Đen", "Sunderland"),
        Club("en_leeds", "Leeds United", "Leeds", "Anh", "championship", 2, 70, 7_000_000, 4, 4, 75, "#FFFFFF", "#1E88E5", listOf("Wilfried Gnonto", "Joel Piroe", "Dan James"), "Bạch Hồng", "Leeds Utd"),
        Club("en_sheffield_utd", "Sheffield United", "Sheffield Utd", "Anh", "championship", 2, 68, 5_500_000, 4, 4, 74, "#C62828", "#000000", listOf("Gustavo Hamer", "Rhian Brewster", "Kieffer Moore"), "Lưỡi Kiếm Đỏ", "Sheffield Utd"),
        Club("en_burnley", "Burnley FC", "Burnley", "Anh", "championship", 2, 69, 6_000_000, 4, 4, 74, "#6A1B9A", "#80DEEA", listOf("Lyle Foster", "Josh Brownhill", "Zian Flemming"), "Rượu Vang Đỏ", "Burnley"),
        Club("en_west_brom", "West Bromwich Albion", "West Brom", "Anh", "championship", 2, 66, 4_500_000, 4, 4, 73, "#1A237E", "#FFFFFF", listOf("Josh Maja", "John Swift", "Grady Diangana"), "Sọc Lam Trắng", "West Brom"),
        Club("en_norwich", "Norwich City", "Norwich", "Anh", "championship", 2, 65, 4_000_000, 4, 4, 72, "#FBC02D", "#2E7D32", listOf("Josh Sargent", "Borja Sainz", "Marcelino Nunez"), "Hoàng Yến Vàng", "Norwich"),
        Club("en_middlesbrough", "Middlesbrough", "Middlesbrough", "Anh", "championship", 2, 64, 3_800_000, 3, 4, 72, "#C62828", "#FFFFFF", listOf("Emmanuel Latte Lath", "Hayden Hackney", "Finn Azaz"), "Đỏ Trắng Bắc", "Middlesbrough"),
        Club("en_coventry", "Coventry City", "Coventry", "Anh", "championship", 2, 63, 3_500_000, 3, 3, 71, "#03A9F4", "#FFFFFF", listOf("Ellis Simms", "Haji Wright", "Milan van Ewijk"), "Lam Bầu Trời", "Coventry")
    )

    fun createPremierLeagueClubs(): List<Club> = listOf(
        Club("en_mancity", "Manchester City", "Man City", "Anh", "premier_league", 1, 95, 80_000_000, 5, 5, 86, "#81D4FA", "#FFFFFF", listOf("Erling Haaland", "Kevin De Bruyne", "Phil Foden", "Rodri"), "Xanh Thành Phố", "Man City"),
        Club("en_arsenal", "Arsenal FC", "Arsenal", "Anh", "premier_league", 1, 92, 70_000_000, 5, 5, 85, "#E53935", "#FFFFFF", listOf("Bukayo Saka", "Martin Odegaard", "Kai Havertz", "Declan Rice"), "Pháo Thủ Đỏ", "Arsenal"),
        Club("en_liverpool", "Liverpool FC", "Liverpool", "Anh", "premier_league", 1, 91, 65_000_000, 5, 5, 84, "#C62828", "#FFFFFF", listOf("Mohamed Salah", "Luis Diaz", "Dominik Szoboszlai", "Virgil van Dijk"), "Lữ Đoàn Đỏ", "Liverpool"),
        Club("en_chelsea", "Chelsea FC", "Chelsea", "Anh", "premier_league", 1, 88, 60_000_000, 5, 5, 82, "#1565C0", "#FFFFFF", listOf("Cole Palmer", "Nicolas Jackson", "Enzo Fernandez", "Moises Caicedo"), "The Blues", "Chelsea"),
        Club("en_aston_villa", "Aston Villa", "Aston Villa", "Anh", "premier_league", 1, 84, 45_000_000, 4, 5, 81, "#6A1B9A", "#80DEEA", listOf("Ollie Watkins", "Emiliano Martinez", "Leon Bailey"), "Sư Tử Vùng Midlands", "Aston Villa"),
        Club("en_tottenham", "Tottenham Hotspur", "Tottenham", "Anh", "premier_league", 1, 85, 48_000_000, 5, 4, 82, "#FFFFFF", "#1A237E", listOf("Son Heung-min", "James Maddison", "Dejan Kulusevski"), "Gà Trống Bắc Luân Đôn", "Tottenham"),
        Club("en_newcastle", "Newcastle United", "Newcastle", "Anh", "premier_league", 1, 83, 50_000_000, 4, 4, 80, "#212121", "#FFFFFF", listOf("Alexander Isak", "Anthony Gordon", "Bruno Guimaraes"), "Chích Chòe Đen Trắng", "Newcastle"),
        Club("en_man_utd", "Manchester United", "Man Utd", "Anh", "premier_league", 1, 86, 55_000_000, 5, 4, 81, "#D50000", "#FFD600", listOf("Bruno Fernandes", "Marcus Rashford", "Alejandro Garnacho"), "Quỷ Đỏ", "Man Utd")
    )

    // =========================================================================
    // CƠ SỞ DỮ LIỆU CÁC GIẢI ĐẤU QUỐC TẾ (CHÂU Á & CHÂU ÂU)
    // HƯỚNG DẪN THÊM CLB HOẶC GIẢI MỚI THỦ CÔNG:
    // 1. Tạo hàm `create<TênGiải>Clubs(): List<Club>` tương tự bên dưới.
    // 2. Thêm leagueId vào hàm `initializeLeague(leagueId)` và `getAllClubs()`.
    // 3. Mỗi Club cần khai báo: id, tên đầy đủ, tên viết tắt, quốc gia, leagueId,
    //    hạng đấu (tier), điểm uy tín (reputation 1-100), ngân sách chuyển nhượng (€),
    //    chất lượng sân tập (1-5), chất lượng HLV (1-5), OVR trung bình và cầu thủ ngôi sao.
    // =========================================================================

    // THÁI LAN - THAI LEAGUE 1 (Đông Nam Á)
    fun createThaiLeagueClubs(): List<Club> = listOf(
        Club("th_buriram", "Buriram United", "Buriram Utd", "Thái Lan", "thai_league", 1, 64, 2_800_000, 4, 4, 68, "#1A237E", "#FFFFFF", listOf("Supachai Chaided", "Theerathon Bunmathan", "Guilherme Bissoli"), "Lâu Đài Sét", "Buriram"),
        Club("th_bangkok", "True Bangkok United", "Bangkok Utd", "Thái Lan", "thai_league", 1, 62, 2_200_000, 3, 3, 66, "#D32F2F", "#000000", listOf("Mahmoud Eid", "Everton Goncalves", "Thitiphan Puangchan"), "Thiên Thần Đỏ", "Bangkok Utd"),
        Club("th_bg_pathum", "BG Pathum United", "BG Pathum", "Thái Lan", "thai_league", 1, 61, 2_000_000, 4, 4, 66, "#1565C0", "#FFEB3B", listOf("Chanathip Songkrasin", "Teerasil Dangda", "Sarach Yooyen"), "Thỏ Lam", "BG Pathum"),
        Club("th_port", "Port FC", "Port FC", "Thái Lan", "thai_league", 1, 59, 1_600_000, 3, 3, 64, "#E65100", "#1A237E", listOf("Bordin Phala", "Barros Tardeli", "Peeradol Chamratsamee"), "Cảng Khổng Lồ", "Port FC"),
        Club("th_muangthong", "Muangthong United", "Muangthong", "Thái Lan", "thai_league", 1, 58, 1_500_000, 3, 3, 63, "#C62828", "#212121", listOf("Poramet Arjvirai", "Tristan Do", "Picha Autra"), "Kirin Đỏ", "Muangthong")
    )

    // HÀN QUỐC - K LEAGUE 1 (Đông Á đỉnh cao)
    fun createKLeagueClubs(): List<Club> = listOf(
        Club("kr_ulsan", "Ulsan HD FC", "Ulsan HD", "Hàn Quốc", "k_league", 1, 74, 9_500_000, 4, 4, 73, "#0D47A1", "#FFD600", listOf("Joo Min-kyu", "Gustav Ludwigson", "Kim Young-gwon"), "Hổ Xanh Donghae", "Ulsan"),
        Club("kr_jeonbuk", "Jeonbuk Hyundai Motors", "Jeonbuk", "Hàn Quốc", "k_league", 1, 73, 8_800_000, 4, 4, 72, "#2E7D32", "#FFFFFF", listOf("Song Min-kyu", "Tiago Orobo", "Kim Jin-su"), "Chiến Binh Xanh Lục", "Jeonbuk"),
        Club("kr_seoul", "FC Seoul", "FC Seoul", "Hàn Quốc", "k_league", 1, 72, 7_500_000, 4, 4, 71, "#B71C1C", "#212121", listOf("Jesse Lingard", "Stanislav Iljutcenko", "Ki Sung-yueng"), "Thủ Đô Đỏ Đen", "FC Seoul"),
        Club("kr_pohang", "Pohang Steelers", "Pohang", "Hàn Quốc", "k_league", 1, 71, 6_800_000, 4, 4, 70, "#D32F2F", "#000000", listOf("Jorge Teixeira", "Wanderson", "Oberdan"), "Thép Đỏ Pohang", "Pohang"),
        Club("kr_suwon", "Suwon FC", "Suwon FC", "Hàn Quốc", "k_league", 1, 67, 4_500_000, 3, 3, 67, "#1976D2", "#C62828", listOf("Anderson Oliveira", "Ji Dong-won", "Lachlan Jackson"), "Thành Cổ Suwon", "Suwon FC")
    )

    // BỒ ĐÀO NHA - LIGA PORTUGAL (Cửa ngõ Châu Âu)
    fun createPrimeiraLigaClubs(): List<Club> = listOf(
        Club("pt_benfica", "SL Benfica", "Benfica", "Bồ Đào Nha", "primeira_liga", 1, 84, 38_000_000, 5, 5, 80, "#D50000", "#FFFFFF", listOf("Angel Di Maria", "Orkun Kokcu", "Vangelis Pavlidis", "Nicolas Otamendi"), "Đại Bàng Đỏ", "Benfica"),
        Club("pt_sporting", "Sporting CP", "Sporting CP", "Bồ Đào Nha", "primeira_liga", 1, 83, 35_000_000, 5, 5, 80, "#2E7D32", "#FFFFFF", listOf("Viktor Gyokeres", "Pedro Goncalves", "Morten Hjulmand"), "Sư Tử Xanh", "Sporting"),
        Club("pt_porto", "FC Porto", "FC Porto", "Bồ Đào Nha", "primeira_liga", 1, 82, 32_000_000, 5, 5, 79, "#1565C0", "#FFFFFF", listOf("Galeno", "Alan Varela", "Diogo Costa", "Samu Omorodion"), "Rồng Xanh", "Porto"),
        Club("pt_braga", "SC Braga", "Braga", "Bồ Đào Nha", "primeira_liga", 1, 77, 16_000_000, 4, 4, 75, "#C62828", "#FFFFFF", listOf("Ricardo Horta", "Bruma", "Joao Moutinho"), "Chiến Binh Minho", "Braga")
    )

    // ĐỨC - BUNDESLIGA
    fun createBundesligaClubs(): List<Club> = listOf(
        Club("de_bayern", "FC Bayern München", "Bayern", "Đức", "bundesliga", 1, 93, 95_000_000, 5, 5, 86, "#D50000", "#FFFFFF", listOf("Harry Kane", "Jamal Musiala", "Leroy Sane", "Joshua Kimmich"), "Hùm Xám Xứ Bavaria", "Bayern"),
        Club("de_leverkusen", "Bayer 04 Leverkusen", "Leverkusen", "Đức", "bundesliga", 1, 89, 70_000_000, 5, 5, 83, "#B71C1C", "#212121", listOf("Florian Wirtz", "Granit Xhaka", "Victor Boniface", "Jeremie Frimpong"), "Binh Đoàn Dược Phẩm", "Leverkusen"),
        Club("de_dortmund", "Borussia Dortmund", "Dortmund", "Đức", "bundesliga", 1, 88, 65_000_000, 5, 5, 82, "#FDD835", "#212121", listOf("Julian Brandt", "Serhou Guirassy", "Karim Adeyemi", "Nico Schlotterbeck"), "Vàng Đen Vùng Ruhr", "Dortmund"),
        Club("de_leipzig", "RB Leipzig", "RB Leipzig", "Đức", "bundesliga", 1, 85, 55_000_000, 5, 4, 81, "#FFFFFF", "#D32F2F", listOf("Xavi Simons", "Benjamin Sesko", "Lois Openda", "Willi Orban"), "Đàn Bò Đỏ", "RB Leipzig")
    )

    // Ý - SERIE A
    fun createSerieAClubs(): List<Club> = listOf(
        Club("it_inter", "Inter Milan", "Inter", "Ý", "serie_a", 1, 90, 70_000_000, 5, 5, 84, "#1565C0", "#212121", listOf("Lautaro Martinez", "Marcus Thuram", "Nicolo Barella", "Hakan Calhanoglu"), "Sọc Xanh Đen", "Inter"),
        Club("it_juventus", "Juventus FC", "Juventus", "Ý", "serie_a", 1, 88, 62_000_000, 5, 4, 82, "#212121", "#FFFFFF", listOf("Dusan Vlahovic", "Kenan Yildiz", "Teun Koopmeiners", "Bremer"), "Bà Đầm Già", "Juventus"),
        Club("it_milan", "AC Milan", "AC Milan", "Ý", "serie_a", 1, 87, 58_000_000, 5, 4, 82, "#C62828", "#212121", listOf("Rafael Leao", "Christian Pulisic", "Theo Hernandez", "Tijjani Reijnders"), "Ác Quỷ Đỏ Đen", "AC Milan"),
        Club("it_atalanta", "Atalanta BC", "Atalanta", "Ý", "serie_a", 1, 83, 38_000_000, 4, 5, 80, "#1976D2", "#212121", listOf("Ademola Lookman", "Mateo Retegui", "Charles De Ketelaere", "Ederson"), "Nữ Thần Bergamo", "Atalanta")
    )

    // PHÁP - LIGUE 1
    fun createLigue1Clubs(): List<Club> = listOf(
        Club("fr_psg", "Paris Saint-Germain", "PSG", "Pháp", "ligue_1", 1, 92, 105_000_000, 5, 5, 85, "#1A237E", "#C62828", listOf("Ousmane Dembele", "Bradley Barcola", "Vitinha", "Achraf Hakimi"), "Kinh Đô Ánh Sáng", "PSG"),
        Club("fr_monaco", "AS Monaco", "Monaco", "Pháp", "ligue_1", 1, 84, 42_000_000, 4, 4, 80, "#D32F2F", "#FFFFFF", listOf("Denis Zakaria", "Maghnes Akliouche", "Folarin Balogun", "Aleksandr Golovin"), "Công Quốc Đỏ Trắng", "Monaco"),
        Club("fr_marseille", "Olympique de Marseille", "Marseille", "Pháp", "ligue_1", 1, 83, 38_000_000, 4, 4, 79, "#03A9F4", "#FFFFFF", listOf("Mason Greenwood", "Elye Wahi", "Pierre-Emile Hojbjerg"), "Thành Cảng Miền Nam", "Marseille"),
        Club("fr_lille", "LOSC Lille", "Lille", "Pháp", "ligue_1", 1, 81, 30_000_000, 4, 4, 78, "#C62828", "#1A237E", listOf("Jonathan David", "Edon Zhegrova", "Angel Gomes", "Benjamin Andre"), "Chó Săn Phương Bắc", "Lille")
    )

    /**
     * Tập hợp toàn bộ danh sách CLB trong toàn bộ thế giới trò chơi.
     * Dùng cho tìm kiếm, thị trường chuyển nhượng và quan hệ đối tác.
     */
    fun getAllClubs(): List<Club> =
        createVLeague2Clubs() +
        createVLeague1Clubs() +
        createThaiLeagueClubs() +
        createKLeagueClubs() +
        createPrimeiraLigaClubs() +
        createChampionshipClubs() +
        createLigue1Clubs() +
        createSerieAClubs() +
        createBundesligaClubs() +
        createPremierLeagueClubs()

    fun getClubById(clubId: String): Club? =
        getAllClubs().find { it.id == clubId }

    fun initializeLeague(leagueId: String): League {
        val (name, country, tier, clubs) = when (leagueId) {
            "vleague_2" -> Quadruple("V-League 2 (Hạng Nhất)", "Việt Nam", 2, createVLeague2Clubs())
            "vleague_1" -> Quadruple("V-League 1", "Việt Nam", 1, createVLeague1Clubs())
            "thai_league" -> Quadruple("Thai League 1", "Thái Lan", 1, createThaiLeagueClubs())
            "k_league" -> Quadruple("K League 1", "Hàn Quốc", 1, createKLeagueClubs())
            "primeira_liga" -> Quadruple("Liga Portugal", "Bồ Đào Nha", 1, createPrimeiraLigaClubs())
            "championship" -> Quadruple("EFL Championship", "Anh", 2, createChampionshipClubs())
            "ligue_1" -> Quadruple("Ligue 1", "Pháp", 1, createLigue1Clubs())
            "serie_a" -> Quadruple("Serie A", "Ý", 1, createSerieAClubs())
            "bundesliga" -> Quadruple("Bundesliga", "Đức", 1, createBundesligaClubs())
            "premier_league" -> Quadruple("English Premier League", "Anh", 1, createPremierLeagueClubs())
            "national_cup" -> Quadruple("Cúp Quốc Gia Việt Nam", "Việt Nam", 1, createVLeague2Clubs() + createVLeague1Clubs())
            else -> Quadruple("V-League 2 (Hạng Nhất)", "Việt Nam", 2, createVLeague2Clubs())
        }

        val standings = clubs.map {
            TeamStanding(clubId = it.id, clubName = it.shortName)
        }.toMutableList()

        // Khởi tạo bảng Vua phá lưới & Vua kiến tạo ban đầu với các cầu thủ tiêu biểu
        val initialTopScorers = mutableListOf<PlayerStatLeader>()
        val initialTopAssists = mutableListOf<PlayerStatLeader>()

        clubs.take(5).forEachIndexed { idx, club ->
            val scorer = club.keyPlayers.firstOrNull() ?: "${club.shortName} ST"
            val assister = club.keyPlayers.getOrNull(1) ?: "${club.shortName} MF"
            initialTopScorers.add(PlayerStatLeader("p_s_$idx", scorer, club.id, club.shortName, 0, 0, false))
            initialTopAssists.add(PlayerStatLeader("p_a_$idx", assister, club.id, club.shortName, 0, 0, false))
        }

        return League(
            id = leagueId,
            name = name,
            country = country,
            tier = tier,
            totalRounds = if (leagueId == "national_cup") 5 else 14,
            clubs = clubs,
            standings = standings,
            topScorers = initialTopScorers,
            topAssists = initialTopAssists
        )
    }

    fun getAllLeagues(): List<League> = listOf(
        initializeLeague("vleague_2"),
        initializeLeague("vleague_1"),
        initializeLeague("thai_league"),
        initializeLeague("k_league"),
        initializeLeague("primeira_liga"),
        initializeLeague("championship"),
        initializeLeague("ligue_1"),
        initializeLeague("serie_a"),
        initializeLeague("bundesliga"),
        initializeLeague("premier_league"),
        initializeLeague("national_cup")
    )

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

    fun getShopItems(): List<LifestyleItem> = listOf(
        LifestyleItem(
            id = "item_boots_elite",
            nameVi = "Giày Thi Đấu Nike Mercurial Elite",
            category = LifestyleCategory.EQUIPMENT,
            price = 400,
            weeklyMaintenance = 10,
            descriptionVi = "Tăng độ bám sân, tăng tự tin khi dứt điểm và bứt tốc.",
            trainingMultiplierBonus = 0.05,
            moraleBonus = 5
        ),
        LifestyleItem(
            id = "item_home_gym",
            nameVi = "Bộ Thiết Bị Gym Mini Tại Nhà",
            category = LifestyleCategory.TRAINING_FACILITY,
            price = 1_500,
            weeklyMaintenance = 25,
            descriptionVi = "Giúp bạn tập thêm các bài cơ bắp tại gia, tăng 15% hiệu quả tập STA và DEF.",
            trainingMultiplierBonus = 0.15,
            moraleBonus = 3
        ),
        LifestyleItem(
            id = "item_cryo_tub",
            nameVi = "Bồn Ngâm Lạnh Phục Hồi Cryo",
            category = LifestyleCategory.TRAINING_FACILITY,
            price = 3_000,
            weeklyMaintenance = 50,
            descriptionVi = "Giảm mạnh mệt mỏi sau mỗi buổi tập và trận đấu, hạn chế chấn thương.",
            fatigueReductionBonus = 0.25,
            moraleBonus = 5
        ),
        LifestyleItem(
            id = "item_fitness_coach",
            nameVi = "Thuê HLV Thể Lực Cá Nhân Cao Cấp",
            category = LifestyleCategory.TRAINING_FACILITY,
            price = 5_000,
            weeklyMaintenance = 200,
            descriptionVi = "HLV lên giáo án chuyên biệt, tăng 20% tốc độ tăng trưởng PAC và STA.",
            trainingMultiplierBonus = 0.20,
            moraleBonus = 6
        ),
        LifestyleItem(
            id = "item_skills_coach",
            nameVi = "Thuê Chuyên Gia Kỹ Thuật Riêng",
            category = LifestyleCategory.TRAINING_FACILITY,
            price = 7_500,
            weeklyMaintenance = 250,
            descriptionVi = "Hướng dẫn động tác giả và nhãn quan chuyền bóng, tăng 25% tăng trưởng SHO, PAS, DRI.",
            trainingMultiplierBonus = 0.25,
            moraleBonus = 8
        ),
        LifestyleItem(
            id = "item_apartment",
            nameVi = "Căn Hộ Chung Cư Cao Cấp",
            category = LifestyleCategory.LIVING_COMFORT,
            price = 35_000,
            weeklyMaintenance = 150,
            descriptionVi = "Không gian sống yên tĩnh, tiện nghi, tăng phục hồi thể lực và tinh thần.",
            fatigueReductionBonus = 0.15,
            moraleBonus = 12,
            reputationBonus = 5
        ),
        LifestyleItem(
            id = "item_rolex_watch",
            nameVi = "Đồng Hồ Xa Xỉ Hublot Classic",
            category = LifestyleCategory.LUXURY,
            price = 20_000,
            weeklyMaintenance = 50,
            descriptionVi = "Biểu tượng thời trang của các cầu thủ nổi tiếng, tăng mạnh Danh tiếng và Tinh thần.",
            moraleBonus = 15,
            reputationBonus = 10
        ),
        LifestyleItem(
            id = "item_sports_car",
            nameVi = "Siêu Xe Thể Thao Porsche 911",
            category = LifestyleCategory.LUXURY,
            price = 120_000,
            weeklyMaintenance = 500,
            descriptionVi = "Chiếc xe mơ ước xuất hiện trên các trang báo thể thao, khẳng định vị thế ngôi sao.",
            moraleBonus = 25,
            reputationBonus = 20
        )
    )
}
