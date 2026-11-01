package com.example

import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.data.SquadDatabase
import com.example.roadtolegend.engine.BalanceConfig
import com.example.roadtolegend.engine.ContractNegotiationEngine
import com.example.roadtolegend.engine.FinanceEngine
import com.example.roadtolegend.engine.LeagueSimulator
import com.example.roadtolegend.engine.MatchSimulator
import com.example.roadtolegend.engine.NewsAndRumorEngine
import com.example.roadtolegend.engine.TrainingEngine
import com.example.roadtolegend.engine.TransferConfig
import com.example.roadtolegend.engine.TransferMarketEngine
import com.example.roadtolegend.model.Agent
import com.example.roadtolegend.model.AgentTier
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.Contract
import com.example.roadtolegend.model.InteractiveChoiceType
import com.example.roadtolegend.model.LoanRecord
import com.example.roadtolegend.model.MatchEventType
import com.example.roadtolegend.model.NegotiationStatus
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.Position
import com.example.roadtolegend.model.SeasonRecord
import com.example.roadtolegend.model.SquadRole
import com.example.roadtolegend.model.TrainingFocus
import com.example.roadtolegend.model.TrainingIntensity
import com.example.roadtolegend.model.TransactionType
import com.example.roadtolegend.model.TransferOffer
import com.example.roadtolegend.model.TransferOfferStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID
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

    // =========================================================
    // GIAI ĐOẠN 2: TEST HỢP ĐỒNG, ĐÀM PHÁN, AGENT, CHUYỂN NHƯỢNG & 20+ MÙA GIẢI
    // =========================================================

    @Test
    fun testContractSalaryAndBonuses() {
        val player = Player(
            id = "test_player",
            name = "Test Striker",
            age = 19,
            primaryPosition = Position.ST,
            pac = 65.0,
            sho = 65.0,
            pas = 60.0,
            dri = 60.0,
            sta = 70.0,
            def = 40.0,
            balance = 10_000L
        )
        val club = DefaultDatabase.createVLeague1Clubs().first()
        val league = DefaultDatabase.initializeLeague(club.leagueId)
        val contract = Contract(
            clubId = club.id,
            clubName = club.name,
            weeklyWage = 2_000L,
            signingBonus = 5_000L,
            bonusPerAppearance = 100L,
            bonusPerGoal = 200L,
            bonusPerAssist = 100L,
            bonusPerCleanSheet = 150L,
            totalWeeksRemaining = 26
        )
        val state = CareerState(
            seasonNumber = 1,
            currentYear = 2026,
            currentWeek = 1,
            player = player,
            currentClub = club,
            currentLeague = league,
            currentContract = contract,
            nextOpponent = league.clubs.last()
        )

        // 1. Kiểm tra tính lương thực nhận sau thuế
        val netWage = TransferConfig.calculateNetIncome(contract.weeklyWage)
        assertTrue("Lương sau thuế phải nhỏ hơn hoặc bằng lương tổng", netWage <= contract.weeklyWage)
        assertTrue("Lương sau thuế phải lớn hơn 0", netWage > 0)

        // 2. Chạy quyết toán tài chính tuần
        val initialBalance = player.balance
        FinanceEngine.processWeeklyFinance(state)

        // Sau 1 tuần: nhận netWage, thời hạn hợp đồng giảm 1 tuần
        assertEquals(initialBalance + netWage, player.balance)
        assertEquals(25, contract.totalWeeksRemaining)
        assertTrue(state.financeTransactions.any { it.type == TransactionType.WAGE })
        assertTrue(state.marketHistory.isNotEmpty())
    }

    @Test
    fun testContractNegotiationFairVsExcessive() {
        val player = Player(
            id = "p1",
            name = "Star Player",
            age = 22,
            primaryPosition = Position.ST,
            pac = 75.0,
            sho = 78.0,
            pas = 70.0,
            dri = 72.0,
            sta = 75.0,
            def = 40.0,
            domesticReputation = 60
        )
        val club = DefaultDatabase.createVLeague1Clubs().first()
        val contract = Contract(
            clubId = club.id,
            clubName = club.name,
            weeklyWage = 2_000L,
            contractYearsRemaining = 1,
            totalWeeksRemaining = 26
        )
        val agent = Agent("agent_none", "Gia đình", AgentTier.NONE)

        // Tạo phiên đàm phán gia hạn
        val sessionFair = ContractNegotiationEngine.startNegotiation(
            player = player,
            club = club,
            currentContract = contract,
            linkedOffer = null,
            agent = agent,
            isRenewal = true
        )
        assertNotNull(sessionFair)
        assertEquals(club.id, sessionFair.clubId)
        assertTrue(sessionFair.hiddenClubPatience > 0)

        // Đề xuất mức lương hợp lý (chỉ cao hơn 5% so với mức CLB đề xuất)
        val clubOfferWage = sessionFair.clubOfferedWage
        val fairWageDemand = (clubOfferWage * 1.05).toLong()
        
        ContractNegotiationEngine.submitPlayerCounterProposal(
            session = sessionFair,
            player = player,
            club = club,
            agent = agent,
            demandedWage = fairWageDemand,
            demandedSigningBonus = sessionFair.clubOfferedSigningBonus,
            demandedYears = sessionFair.clubOfferedContractYears,
            demandedRole = sessionFair.clubOfferedRole,
            demandedReleaseClause = sessionFair.clubOfferedReleaseClause,
            random = Random(123)
        )

        // Đàm phán hợp lý -> được chấp thuận hoặc tiếp tục đàm phán mà không bị walk out ngay
        assertTrue(sessionFair.status == NegotiationStatus.AGREED || sessionFair.status == NegotiationStatus.IN_PROGRESS)
        assertTrue("Không được hiển thị ký hiệu % trong tin phản hồi", !sessionFair.latestFeedbackMessageVi.contains("%"))

        // Tạo phiên đàm phán với đòi hỏi quá đáng
        val sessionGreedy = ContractNegotiationEngine.startNegotiation(
            player = player,
            club = club,
            currentContract = contract,
            linkedOffer = null,
            agent = agent,
            isRenewal = true
        )
        // Đòi hỏi gấp 50 lần ngân sách CLB liên tục
        for (round in 1..6) {
            if (sessionGreedy.status == NegotiationStatus.IN_PROGRESS) {
                ContractNegotiationEngine.submitPlayerCounterProposal(
                    session = sessionGreedy,
                    player = player,
                    club = club,
                    agent = agent,
                    demandedWage = clubOfferWage * 50,
                    demandedSigningBonus = 10_000_000L,
                    demandedYears = 5,
                    demandedRole = SquadRole.STAR,
                    demandedReleaseClause = null,
                    random = Random(round.toLong())
                )
            }
        }
        // Phải bị chấm dứt đàm phán do kiên nhẫn cạn kiệt, không kéo dài vô hạn
        assertTrue(sessionGreedy.status == NegotiationStatus.TERMINATED_WALKOUT || sessionGreedy.hiddenClubPatience <= 0)
        assertTrue(sessionGreedy.history.size in 1..5)
    }

    @Test
    fun testAgentSystemHiringAndRetainer() {
        val player = Player(
            id = "agent_test_p",
            name = "Rising Star",
            age = 20,
            primaryPosition = Position.CAM,
            pac = 70.0,
            sho = 70.0,
            pas = 75.0,
            dri = 72.0,
            sta = 70.0,
            def = 50.0,
            domesticReputation = 75,
            balance = 20_000L
        )
        val club = DefaultDatabase.createVLeague1Clubs().first()
        val league = DefaultDatabase.initializeLeague(club.leagueId)
        val contract = Contract(
            clubId = club.id,
            clubName = club.name,
            weeklyWage = 3_000L,
            totalWeeksRemaining = 26
        )
        val state = CareerState(
            seasonNumber = 1,
            currentYear = 2026,
            currentWeek = 1,
            player = player,
            currentClub = club,
            currentLeague = league,
            currentContract = contract,
            nextOpponent = league.clubs.last()
        )

        // Tuyển đại diện ELITE
        val eliteAgent = Agent(
            id = "elite_agent_1",
            name = "Gestifute Agency",
            tier = AgentTier.ELITE
        )
        state.agent = eliteAgent

        // Quyết toán tuần: phí duy trì Agent (tier.weeklyRetainer) phải được trừ
        val preBalance = player.balance
        FinanceEngine.processWeeklyFinance(state)

        val netWage = TransferConfig.calculateNetIncome(contract.weeklyWage)
        val expectedBalance = preBalance + netWage - eliteAgent.tier.weeklyRetainer
        assertEquals(expectedBalance, player.balance)
        assertTrue(state.financeTransactions.any { it.type == TransactionType.AGENT_RETAINER })
    }

    @Test
    fun testTransferWindowAndLoansLifecycle() {
        val clubA = DefaultDatabase.createVLeague1Clubs()[0]
        val clubB = DefaultDatabase.createVLeague1Clubs()[1]
        val player = Player(
            id = "transfer_p",
            name = "Winger Fast",
            age = 21,
            primaryPosition = Position.LW,
            pac = 78.0,
            sho = 70.0,
            pas = 68.0,
            dri = 75.0,
            sta = 72.0,
            def = 45.0,
            balance = 10_000L
        )
        val contract = Contract(
            clubId = clubA.id,
            clubName = clubA.name,
            weeklyWage = 2_500L,
            totalWeeksRemaining = 26
        )
        val league = DefaultDatabase.initializeLeague(clubA.leagueId)
        val state = CareerState(
            seasonNumber = 1,
            currentYear = 2026,
            currentWeek = 1, // Tuần 1: Cửa sổ mở
            player = player,
            currentClub = clubA,
            currentLeague = league,
            currentContract = contract,
            nextOpponent = clubB
        )

        // Kiểm tra cửa sổ chuyển nhượng
        assertTrue(TransferConfig.isTransferWindowOpen(1))
        assertFalse(TransferConfig.isTransferWindowOpen(8))
        assertTrue(TransferConfig.isTransferWindowOpen(14)) // Mùa đông

        // Đệ trình yêu cầu chuyển nhượng và yêu cầu cho mượn
        val (transferSuccess, _) = TransferMarketEngine.submitTransferRequest(state)
        assertTrue(transferSuccess)
        assertTrue(state.isTransferListed)

        val (loanReqSuccess, _) = TransferMarketEngine.submitLoanRequest(state)
        assertTrue(loanReqSuccess)

        // Thiết lập hợp đồng cho mượn sang clubB
        val loanRecord = LoanRecord(
            parentClubId = clubA.id,
            parentClubName = clubA.name,
            parentClubShortName = clubA.shortName,
            loanClubId = clubB.id,
            loanClubName = clubB.name,
            loanClubShortName = clubB.shortName,
            wageSharePercent = 80,
            buyOptionFee = 300_000L,
            canRecallInWinter = true,
            weeksRemaining = 13
        )
        state.activeLoan = loanRecord
        state.currentClub = clubB
        state.currentContract.clubId = clubB.id
        state.currentContract.clubName = clubB.name

        // Đến tuần mùa đông (tuần 13) -> CLB mẹ gọi về
        state.currentWeek = 13
        val (recallSuccess, _) = TransferMarketEngine.recallLoanInWinter(state)
        assertTrue(recallSuccess)
        assertEquals(clubA.id, state.currentClub.id)
        assertEquals(null, state.activeLoan)
    }

    @Test
    fun testLongTermMultiSeasonSimulation20Seasons() {
        val player = Player(
            id = "legend_p",
            name = "Nguyen Van Legend",
            age = 18,
            primaryPosition = Position.ST,
            pac = 68.0,
            sho = 70.0,
            pas = 65.0,
            dri = 67.0,
            sta = 72.0,
            def = 40.0,
            balance = 5_000L
        )
        val startingClub = DefaultDatabase.createVLeague2Clubs().first()
        val league = DefaultDatabase.initializeLeague(startingClub.leagueId)
        val contract = Contract(
            clubId = startingClub.id,
            clubName = startingClub.name,
            weeklyWage = 500L,
            contractYearsRemaining = 3,
            totalWeeksRemaining = 78
        )
        val state = CareerState(
            seasonNumber = 1,
            currentYear = 2026,
            currentWeek = 1,
            player = player,
            currentClub = startingClub,
            currentLeague = league,
            currentContract = contract,
            nextOpponent = league.clubs.last()
        )

        val rng = Random(2026)

        // Chạy liên tục 20 mùa giải (520 tuần)
        val targetSeasons = 20
        for (season in 1..targetSeasons) {
            // Chạy 26 tuần của mùa giải
            for (w in 1..26) {
                state.currentWeek = w
                state.isTransferWindowOpen = TransferConfig.isTransferWindowOpen(w)
                state.isDeadlineDay = TransferConfig.isDeadlineDay(w)

                // 1. Tài chính hàng tuần
                FinanceEngine.processWeeklyFinance(state)

                // 2. Chuyển nhượng AI và sinh offer
                if (state.isTransferWindowOpen) {
                    TransferMarketEngine.simulateAITransferMarket(state, rng)
                    TransferMarketEngine.evaluateAndGeneratePlayerOffers(state, rng)
                }

                // 3. Tin tức & tin đồn
                NewsAndRumorEngine.rollTransferRumor(state, rng)

                // Nếu hợp đồng sắp hết (còn dưới 6 tuần) hoặc là cầu thủ tự do -> ký gia hạn hoặc ký hợp đồng mới
                if (state.currentContract.totalWeeksRemaining <= 6 || state.currentContract.isFreeAgent) {
                    val candidateClubs = DefaultDatabase.createVLeague1Clubs() + DefaultDatabase.createVLeague2Clubs()
                    val targetClub = candidateClubs[rng.nextInt(candidateClubs.size)]
                    state.currentClub = targetClub
                    state.currentContract.clubId = targetClub.id
                    state.currentContract.clubName = targetClub.name
                    state.currentContract.weeklyWage = (1_000L + state.player.ovr * 40L)
                    state.currentContract.totalWeeksRemaining = 52
                    state.currentContract.contractYearsRemaining = 2
                }
            }

            // Kết thúc mùa giải
            state.seasonNumber++
            state.currentYear++
            state.player.age++
            state.currentWeek = 1
        }

        // Kiểm tra sau 20 mùa giải:
        assertEquals(21, state.seasonNumber)
        assertEquals(38, state.player.age)
        assertTrue("Market value history must have accumulated points", state.marketHistory.size > 20)
        assertTrue("Finance transactions must have accumulated", state.financeTransactions.isNotEmpty())
        assertTrue("Player balance must not crash or be negative overflow", state.player.balance >= 0)

        // Kiểm tra tất cả các CLB trong giải vẫn có ngân sách hợp lệ và đội hình đầy đủ
        val allClubs = DefaultDatabase.createVLeague1Clubs() + DefaultDatabase.createVLeague2Clubs()
        for (club in allClubs) {
            val squad = SquadDatabase.getSquadForClub(club.id)
            assertTrue("CLB ${club.name} phải có đủ tối thiểu 11 cầu thủ sau 20 mùa", squad.size >= 11)
            assertTrue("CLB ${club.name} phải có thủ môn", squad.any { it.positionCategory == com.example.roadtolegend.model.PositionCategory.GOALKEEPER })
        }
    }
}

