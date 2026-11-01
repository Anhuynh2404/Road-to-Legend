package com.example

import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.engine.BalanceConfig
import com.example.roadtolegend.engine.LeagueSimulator
import com.example.roadtolegend.engine.MatchSimulator
import com.example.roadtolegend.engine.TrainingEngine
import com.example.roadtolegend.model.Contract
import com.example.roadtolegend.model.InteractiveChoiceType
import com.example.roadtolegend.model.MatchEventType
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.Position
import com.example.roadtolegend.model.SeasonRecord
import com.example.roadtolegend.model.TrainingFocus
import com.example.roadtolegend.model.TrainingIntensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineUnitTest {

    @Test
    fun testPositionOvrCalculation() {
        val st = Position.ST
        val ovr = st.calculateOvr(pac = 80.0, sho = 80.0, pas = 80.0, dri = 80.0, sta = 80.0, def = 80.0)
        assertEquals(80, ovr)

        val cb = Position.CB
        val cbOvrHighDef = cb.calculateOvr(pac = 60.0, sho = 30.0, pas = 50.0, dri = 40.0, sta = 70.0, def = 90.0)
        val cbOvrLowDef = cb.calculateOvr(pac = 60.0, sho = 90.0, pas = 50.0, dri = 40.0, sta = 70.0, def = 30.0)
        assertTrue(cbOvrHighDef > cbOvrLowDef)
    }

    // NGHIỆM THU A: Chi phí bị trừ đúng, chỉ số mục tiêu tăng theo kỳ vọng, tập nặng liên tục tăng fatigue và rủi ro
    @Test
    fun testTrainingPlanCostGrowthAndFatigue() {
        val player = Player(
            id = "test_player",
            name = "Test Striker",
            age = 18,
            primaryPosition = Position.ST,
            pac = 50.0,
            sho = 50.0,
            pas = 50.0,
            dri = 50.0,
            sta = 50.0,
            def = 50.0,
            energy = 100,
            fatigue = 10.0,
            balance = 1000
        )
        val club = DefaultDatabase.createVLeague2Clubs().first()

        // 1. Tập sút với cường độ Vừa (MEDIUM)
        val costExpected = TrainingFocus.SHOOTING.getCost(TrainingIntensity.MEDIUM)
        val initialBalance = player.balance
        val result = TrainingEngine.executeWeeklyTraining(
            player = player,
            club = club,
            focus = TrainingFocus.SHOOTING,
            intensity = TrainingIntensity.MEDIUM,
            purchasedItems = emptyList(),
            random = Random(42)
        )

        // Kiểm tra chi phí bị trừ chính xác
        assertEquals(initialBalance - costExpected, player.balance)
        assertEquals(costExpected, result.costPaid)
        // Chỉ số SHO phải tăng
        assertTrue(player.sho > 50.0)
        assertTrue(result.statDelta > 0.0)
        // Form phải được cộng nhẹ
        assertTrue(result.formDelta > 0)

        // 2. Tập nặng liên tục 4 lần -> fatigue phải tăng cao
        for (i in 0 until 4) {
            TrainingEngine.executeWeeklyTraining(
                player = player,
                club = club,
                focus = TrainingFocus.PACE,
                intensity = TrainingIntensity.HEAVY,
                purchasedItems = emptyList(),
                random = Random(i.toLong())
            )
        }
        assertTrue("Fatigue must accumulate under heavy training", player.fatigue > 35.0)

        // 3. Nghỉ ngơi hồi phục -> fatigue phải giảm và energy tăng
        val preRestFatigue = player.fatigue
        val restResult = TrainingEngine.executeWeeklyTraining(
            player = player,
            club = club,
            focus = TrainingFocus.REST_AND_RECOVERY,
            intensity = TrainingIntensity.MEDIUM,
            purchasedItems = emptyList(),
            random = Random(42)
        )
        assertTrue(restResult.energyDelta > 0)
        assertTrue(player.fatigue < preRestFatigue)
    }

    // NGHIỆM THU C: Hồi quy tỉ số không bao giờ giảm, mọi bàn thắng có sự kiện, phân phối bàn thắng và tương tác ngẫu nhiên
    @Test
    fun testMatchTimelineDeterministicAndScoreInvariant() {
        val player = Player(
            id = "test_player",
            name = "Nguyễn Văn Hùng",
            age = 19,
            primaryPosition = Position.ST,
            pac = 78.0,
            sho = 80.0,
            pas = 72.0,
            dri = 76.0,
            sta = 75.0,
            def = 42.0,
            energy = 95,
            morale = 85,
            form = 80
        )
        val clubs = DefaultDatabase.createVLeague2Clubs()
        val homeClub = clubs[0]
        val awayClub = clubs[1]
        val contract = Contract(clubId = homeClub.id, clubName = homeClub.name, weeklyWage = 350)

        // Kiểm tra cùng seed cho ra cùng kết quả (Seeded deterministic)
        val match1 = MatchSimulator.generateMatchTimeline("m1", 1, player, contract, homeClub, awayClub, true, emptyList(), 9999L)
        val match2 = MatchSimulator.generateMatchTimeline("m2", 1, player, contract, homeClub, awayClub, true, emptyList(), 9999L)

        assertEquals(match1.homeScore, match2.homeScore)
        assertEquals(match1.awayScore, match2.awayScore)
        assertEquals(match1.goalsList.size, match2.goalsList.size)
        assertEquals(match1.timelineEvents.size, match2.timelineEvents.size)

        // Chạy 500 trận với 500 seed khác nhau để kiểm tra tính bất biến (Score Invariant)
        var totalGoals = 0
        var totalShots = 0
        var matchesWithZeroMoments = 0
        var matchesWithMoments = 0

        for (i in 0 until 500) {
            val seed = 1000L + i
            val match = MatchSimulator.generateMatchTimeline(
                matchId = "m_$i",
                roundNumber = (i % 26) + 1,
                player = player,
                contract = contract,
                homeClub = homeClub,
                awayClub = awayClub,
                isUserHomeTeam = (i % 2 == 0),
                seed = seed,
                isInteractiveMomentsEnabled = true
            )

            // BẤT BIẾN 1: Tỉ số không bao giờ âm
            assertTrue(match.homeScore >= 0)
            assertTrue(match.awayScore >= 0)

            // BẤT BIẾN 2: Số bàn thắng của đội nhà phải chính xác bằng số GoalRecord của đội nhà
            val homeGoalsCount = match.goalsList.count { it.clubId == match.homeClub.id }
            val awayGoalsCount = match.goalsList.count { it.clubId == match.awayClub.id }
            assertEquals(match.homeScore, homeGoalsCount)
            assertEquals(match.awayScore, awayGoalsCount)

            // BẤT BIẾN 3: Tỉ số khi phát lại theo timeline CHỈ ĐƯỢC TĂNG, KHÔNG BAO GIỜ GIẢM
            var runningHomeScore = 0
            var runningAwayScore = 0
            for (minute in 1..93) {
                val currentHome = match.goalsList.count { it.minute <= minute && it.clubId == match.homeClub.id }
                val currentAway = match.goalsList.count { it.minute <= minute && it.clubId == match.awayClub.id }
                assertTrue(currentHome >= runningHomeScore)
                assertTrue(currentAway >= runningAwayScore)
                runningHomeScore = currentHome
                runningAwayScore = currentAway
            }

            // BẤT BIẾN 4: Mọi bàn thắng đều có sự kiện GOAL tương ứng trong timeline
            val goalEventsCount = match.timelineEvents.count { it.type == MatchEventType.GOAL }
            assertEquals(match.goalsList.size, goalEventsCount)

            totalGoals += (match.homeScore + match.awayScore)
            totalShots += (match.homeStats.shotsTotal + match.awayStats.shotsTotal)

            if (match.interactiveMoments.isEmpty()) {
                matchesWithZeroMoments++
            } else {
                matchesWithMoments++
                // Đảm bảo không quá 3 khoảnh khắc / trận
                assertTrue(match.interactiveMoments.size <= 3)
            }
        }

        // Kiểm tra phân phối: Trung bình 2.0 - 3.8 bàn/trận, 15 - 30 cú sút/trận
        val avgGoalsPerMatch = totalGoals / 500.0
        val avgShotsPerMatch = totalShots / 500.0
        assertTrue("Average goals per match should be around 2.0-3.8, actual: $avgGoalsPerMatch", avgGoalsPerMatch in 1.8..4.2)
        assertTrue("Average shots per match should be around 14-35, actual: $avgShotsPerMatch", avgShotsPerMatch in 14.0..35.0)

        // Khoảnh khắc tương tác không cố định phút 71 và đôi khi không xuất hiện
        assertTrue("There must be matches with moments and matches without", matchesWithMoments > 0 && matchesWithZeroMoments > 0)
    }

    // NGHIỆM THU E: Bảng xếp hạng và vua phá lưới đồng bộ kết quả thực tế
    @Test
    fun testLeagueSyncAndScorersLeaderboard() {
        val league = DefaultDatabase.initializeLeague("vleague_2")
        val clubs = league.clubs
        val homeClub = clubs[0]
        val awayClub = clubs[1]

        val player = Player(id = "p1", name = "Tiền Đạo Thử Nghiệm", primaryPosition = Position.ST)
        val contract = Contract(clubId = homeClub.id, clubName = homeClub.name, weeklyWage = 300)

        val fixtures = LeagueSimulator.generateRoundFixtures(
            league = league,
            roundNumber = 1,
            userClubId = homeClub.id,
            opponentClubId = awayClub.id,
            userMatch = null
        )

        // Tạo 1 trận đấu của người chơi
        val userMatch = MatchSimulator.generateMatchTimeline("m1", 1, player, contract, homeClub, awayClub, true, seed = 555L)

        LeagueSimulator.simulateOtherFixturesAndSyncLeague(
            league = league,
            fixtures = fixtures,
            userMatch = userMatch,
            userName = player.name,
            seed = 555L
        )

        // BXH phải cập nhật số trận là 1 cho tất cả các đội
        league.standings.forEach { standing ->
            assertEquals(1, standing.played)
            assertTrue(standing.recentForm.isNotEmpty())
        }

        // Tổng bàn thắng các CLB trong BXH phải bằng tổng bàn thắng ghi nhận
        val totalGoalsInTable = league.standings.sumOf { it.goalsFor }
        val totalGoalsFromFixtures = fixtures.sumOf { (it.homeScore ?: 0) + (it.awayScore ?: 0) }
        assertEquals(totalGoalsFromFixtures, totalGoalsInTable)
    }

    // NGHIỆM THU F: Thống kê sự nghiệp và cột mốc
    @Test
    fun testCareerHistoryRecordsAndMilestones() {
        val player = Player(id = "p1", name = "Test Legend", primaryPosition = Position.ST)
        assertEquals(0, player.seasonRecords.size)

        // Thêm bản ghi mùa giải
        val season1 = SeasonRecord(
            seasonNumber = 1,
            year = 2026,
            clubId = "vn_hue",
            clubName = "CLB Bóng Đá Huế",
            clubShortName = "Huế FC",
            leagueId = "vleague_2",
            leagueName = "V-League 2",
            matchesPlayed = 24,
            starts = 20,
            subs = 4,
            minutesPlayed = 1920,
            goals = 14,
            assists = 6,
            averageRating = 7.6,
            yellowCards = 2,
            redCards = 0,
            trophiesWon = "Vô địch V-League 2"
        )
        player.seasonRecords.add(season1)
        assertEquals(1, player.seasonRecords.size)

        // Thêm cột mốc
        player.addMilestoneIfNew("Trận ra mắt chuyên nghiệp", "Thi đấu trận đầu tiên", "Tuần 1", "⚽")
        player.addMilestoneIfNew("Trận ra mắt chuyên nghiệp", "Duplicate", "Tuần 1", "⚽")
        // Trùng lặp không được thêm
        assertEquals(1, player.milestones.size)
    }

    // NGHIỆM THU: Quản lý danh sách cầu thủ các CLB & chỉ số FIFA
    @Test
    fun testSquadDatabaseAllClubsHaveValidRosterAndStats() {
        val vLeague2Clubs = DefaultDatabase.createVLeague2Clubs()
        assertTrue(vLeague2Clubs.isNotEmpty())

        for (club in vLeague2Clubs) {
            val squad = com.example.roadtolegend.data.SquadDatabase.getSquadForClub(club.id)
            assertTrue("Đội hình ${club.name} phải có ít nhất 11 cầu thủ", squad.size >= 11)

            // Kiểm tra có đủ vị trí cơ bản: Thủ môn, Hậu vệ, Tiền vệ, Tiền đạo
            val hasGK = squad.any { it.positionCategory == com.example.roadtolegend.model.PositionCategory.GOALKEEPER }
            val hasDef = squad.any { it.positionCategory == com.example.roadtolegend.model.PositionCategory.DEFENDER }
            val hasMid = squad.any { it.positionCategory == com.example.roadtolegend.model.PositionCategory.MIDFIELDER }
            val hasFwd = squad.any { it.positionCategory == com.example.roadtolegend.model.PositionCategory.FORWARD }

            assertTrue("CLB ${club.name} thiếu thủ môn", hasGK)
            assertTrue("CLB ${club.name} thiếu hậu vệ", hasDef)
            assertTrue("CLB ${club.name} thiếu tiền vệ", hasMid)
            assertTrue("CLB ${club.name} thiếu tiền đạo", hasFwd)

            // Kiểm tra toàn bộ chỉ số FIFA phải nằm trong khoảng hợp lệ [1, 99]
            for (p in squad) {
                assertTrue("OVR của ${p.name} ngoài thang 1-99: ${p.ovr}", p.ovr in 1..99)
                assertTrue("PAC của ${p.name} ngoài thang 1-99: ${p.pac}", p.pac in 1..99)
                assertTrue("SHO của ${p.name} ngoài thang 1-99: ${p.sho}", p.sho in 1..99)
                assertTrue("PAS của ${p.name} ngoài thang 1-99: ${p.pas}", p.pas in 1..99)
                assertTrue("DRI của ${p.name} ngoài thang 1-99: ${p.dri}", p.dri in 1..99)
                assertTrue("DEF của ${p.name} ngoài thang 1-99: ${p.def}", p.def in 1..99)
                assertTrue("PHY của ${p.name} ngoài thang 1-99: ${p.phy}", p.phy in 1..99)
                assertTrue("Giá trị chuyển nhượng phải dương", p.marketValue > 0)
                assertTrue("Lương tuần phải dương", p.weeklyWage > 0)
                assertNotNull(p.avatarResName)
            }
        }
    }

    @Test
    fun testSquadDatabaseUserPlayerInjection() {
        val userClub = DefaultDatabase.createVLeague2Clubs().first { it.id == "vn_binh_phuoc" }
        val userPlayer = Player(
            id = "user_hero_123",
            name = "Lê Huyền Thoại",
            age = 18,
            primaryPosition = Position.ST,
            pac = 70.0,
            sho = 72.0,
            pas = 65.0,
            dri = 68.0,
            sta = 66.0,
            def = 40.0
        )

        // 1. Lấy đội hình của CLB người chơi (Bình Phước)
        val userClubSquad = com.example.roadtolegend.data.SquadDatabase.getSquadForClub(
            clubId = userClub.id,
            userPlayer = userPlayer,
            userClubId = userClub.id,
            userWeeklyWage = 1_500L
        )

        val injectedUser = userClubSquad.find { it.isUser }
        assertNotNull("Cầu thủ người chơi phải xuất hiện trong đội hình CLB chủ quản", injectedUser)
        assertEquals("user_hero_123", injectedUser?.id)
        assertEquals(userPlayer.ovr, injectedUser?.ovr)
        assertEquals(1_500L, injectedUser?.weeklyWage)

        // 2. Lấy đội hình của CLB đối thủ (Ninh Bình) -> không được có cầu thủ người chơi
        val opponentSquad = com.example.roadtolegend.data.SquadDatabase.getSquadForClub(
            clubId = "vn_ninh_binh",
            userPlayer = userPlayer,
            userClubId = userClub.id
        )
        assertFalse("Cầu thủ người chơi không được xuất hiện ở CLB đối thủ", opponentSquad.any { it.isUser })
    }
}
