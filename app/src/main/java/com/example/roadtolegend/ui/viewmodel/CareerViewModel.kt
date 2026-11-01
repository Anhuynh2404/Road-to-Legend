package com.example.roadtolegend.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadtolegend.data.DefaultDatabase
import com.example.roadtolegend.data.SaveGameManager
import com.example.roadtolegend.data.SaveSlotSummary
import com.example.roadtolegend.engine.LeagueSimulator
import com.example.roadtolegend.engine.MatchSimulator
import com.example.roadtolegend.engine.TrainingEngine
import com.example.roadtolegend.engine.TrainingResult
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.Contract
import com.example.roadtolegend.model.Foot
import com.example.roadtolegend.model.InteractiveChoiceType
import com.example.roadtolegend.model.InteractiveMoment
import com.example.roadtolegend.model.LifestyleItem
import com.example.roadtolegend.model.Match
import com.example.roadtolegend.model.MatchEvent
import com.example.roadtolegend.model.NewsItem
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.Position
import com.example.roadtolegend.model.RoundFixture
import com.example.roadtolegend.model.SeasonRecord
import com.example.roadtolegend.model.TrainingFocus
import com.example.roadtolegend.model.TrainingIntensity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

enum class AppScreen {
    WELCOME,
    CREATION,
    MAIN_HUB
}

enum class MainTab {
    DASHBOARD,
    TRAINING,
    MATCHDAY,
    SHOP,
    LEAGUE_TABLE,
    PROFILE_SAVE
}

enum class SimulationSpeed(val titleVi: String, val delayMs: Long) {
    SLOW("Chậm", 900L),
    NORMAL("Vừa", 450L),
    FAST("Nhanh", 120L)
}

class CareerViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _currentScreen = MutableStateFlow(AppScreen.WELCOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentTab = MutableStateFlow(MainTab.DASHBOARD)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    private val _careerState = MutableStateFlow<CareerState?>(null)
    val careerState: StateFlow<CareerState?> = _careerState.asStateFlow()

    private val _shopItems = MutableStateFlow<List<LifestyleItem>>(DefaultDatabase.getShopItems())
    val shopItems: StateFlow<List<LifestyleItem>> = _shopItems.asStateFlow()

    private val _saveSlots = MutableStateFlow<List<SaveSlotSummary>>(emptyList())
    val saveSlots: StateFlow<List<SaveSlotSummary>> = _saveSlots.asStateFlow()

    // Trạng thái mô phỏng trận đấu trực tiếp
    private val _currentMatch = MutableStateFlow<Match?>(null)
    val currentMatch: StateFlow<Match?> = _currentMatch.asStateFlow()

    private val _displayedMinute = MutableStateFlow(0)
    val displayedMinute: StateFlow<Int> = _displayedMinute.asStateFlow()

    private val _displayedHomeScore = MutableStateFlow(0)
    val displayedHomeScore: StateFlow<Int> = _displayedHomeScore.asStateFlow()

    private val _displayedAwayScore = MutableStateFlow(0)
    val displayedAwayScore: StateFlow<Int> = _displayedAwayScore.asStateFlow()

    private val _visibleMatchEvents = MutableStateFlow<List<MatchEvent>>(emptyList())
    val visibleMatchEvents: StateFlow<List<MatchEvent>> = _visibleMatchEvents.asStateFlow()

    private val _isSimulatingMatch = MutableStateFlow(false)
    val isSimulatingMatch: StateFlow<Boolean> = _isSimulatingMatch.asStateFlow()

    private val _simulationSpeed = MutableStateFlow(SimulationSpeed.NORMAL)
    val simulationSpeed: StateFlow<SimulationSpeed> = _simulationSpeed.asStateFlow()

    private val _currentInteractiveMoment = MutableStateFlow<InteractiveMoment?>(null)
    val currentInteractiveMoment: StateFlow<InteractiveMoment?> = _currentInteractiveMoment.asStateFlow()

    private val _lastTrainingResult = MutableStateFlow<TrainingResult?>(null)
    val lastTrainingResult: StateFlow<TrainingResult?> = _lastTrainingResult.asStateFlow()

    private val _roundFixtures = MutableStateFlow<List<RoundFixture>>(emptyList())
    val roundFixtures: StateFlow<List<RoundFixture>> = _roundFixtures.asStateFlow()

    private val _statusNotification = MutableStateFlow<String?>(null)
    val statusNotification: StateFlow<String?> = _statusNotification.asStateFlow()

    private var matchSimulationJob: Job? = null
    private val random = Random(System.currentTimeMillis())

    init {
        loadSaveSlots()
        val autoSave = SaveGameManager.loadGame(context, 0)
        if (autoSave != null) {
            _careerState.value = autoSave
            syncShopItems(autoSave.purchasedItemIds)
            prepareRoundFixtures(autoSave)
        }
    }

    fun loadSaveSlots() {
        _saveSlots.value = SaveGameManager.getSlotSummaries(context)
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    fun setSimulationSpeed(speed: SimulationSpeed) {
        _simulationSpeed.value = speed
    }

    fun toggleFictionalMode() {
        val state = _careerState.value ?: return
        state.isFictionalMode = !state.isFictionalMode
        _careerState.value = state.copy(isFictionalMode = state.isFictionalMode)
        showToast(if (state.isFictionalMode) "Đã bật Chế độ Tên & Logo Giả lập" else "Đã tắt Chế độ Giả lập")
        SaveGameManager.saveGame(context, 0, state)
    }

    fun toggleInteractiveMoments() {
        val state = _careerState.value ?: return
        state.isInteractiveMomentsEnabled = !state.isInteractiveMomentsEnabled
        _careerState.value = state.copy(isInteractiveMomentsEnabled = state.isInteractiveMomentsEnabled)
        showToast(if (state.isInteractiveMomentsEnabled) "Đã bật Khoảnh khắc tương tác" else "Đã tắt Khoảnh khắc tương tác (Tự động quyết định)")
        SaveGameManager.saveGame(context, 0, state)
    }

    fun showToast(message: String) {
        _statusNotification.value = message
    }

    fun clearNotification() {
        _statusNotification.value = null
    }

    private fun prepareRoundFixtures(state: CareerState) {
        _roundFixtures.value = LeagueSimulator.generateRoundFixtures(
            league = state.currentLeague,
            roundNumber = state.currentWeek,
            userClubId = state.currentClub.id,
            opponentClubId = state.nextOpponent.id,
            userMatch = state.lastMatch
        )
    }

    // ---------------------------------------------------------
    // TẠO NHÂN VẬT & KHỞI ĐẦU SỰ NGHIỆP
    // ---------------------------------------------------------
    fun startCareer(
        playerName: String,
        age: Int,
        nationality: String,
        heightCm: Int,
        foot: Foot,
        position: Position,
        pac: Double,
        sho: Double,
        pas: Double,
        dri: Double,
        sta: Double,
        def: Double,
        startingClub: Club
    ) {
        val hiddenPot = (80.0 + random.nextDouble() * 14.0)

        val player = Player(
            id = UUID.randomUUID().toString(),
            name = playerName.trim().ifEmpty { "Nguyễn Văn Hùng" },
            age = age,
            birthYear = 2026 - age,
            nationality = nationality,
            heightCm = heightCm,
            preferredFoot = foot,
            primaryPosition = position,
            pac = pac,
            sho = sho,
            pas = pas,
            dri = dri,
            sta = sta,
            def = def,
            hiddenPotential = hiddenPot,
            balance = 1_500
        )

        // Cột mốc khởi đầu
        player.addMilestoneIfNew(
            title = "Bắt đầu sự nghiệp chuyên nghiệp",
            description = "Gia nhập ${startingClub.name} với vị trí ${position.fullNameVi}.",
            dateText = "Năm 2026 (Tuổi $age)",
            icon = "⚽"
        )

        val league = DefaultDatabase.initializeLeague(startingClub.leagueId)
        val opponent = league.clubs.firstOrNull { it.id != startingClub.id } ?: league.clubs.last()

        val contract = Contract(
            clubId = startingClub.id,
            clubName = startingClub.name,
            weeklyWage = 350,
            bonusPerGoal = 80,
            bonusPerAssist = 40,
            contractYearsRemaining = 2,
            releaseClause = 400_000
        )

        val state = CareerState(
            seasonNumber = 1,
            currentYear = 2026,
            currentWeek = 1,
            player = player,
            currentClub = startingClub,
            currentLeague = league,
            currentContract = contract,
            nextOpponent = opponent,
            selectedTrainingFocus = TrainingFocus.SHOOTING,
            selectedTrainingIntensity = TrainingIntensity.MEDIUM
        )

        state.newsFeed.add(
            NewsItem(
                id = UUID.randomUUID().toString(),
                week = 1,
                headline = "Tài năng trẻ ${player.name} gia nhập ${startingClub.shortName}!",
                body = "Câu lạc bộ ${startingClub.name} chính thức công bố bản hợp đồng đào tạo chuyên nghiệp với cầu thủ trẻ ${player.name} (${player.age} tuổi) thi đấu ở vị trí ${player.primaryPosition.fullNameVi}."
            )
        )

        _careerState.value = state
        syncShopItems(emptySet())
        prepareRoundFixtures(state)
        SaveGameManager.saveGame(context, 0, state)
        loadSaveSlots()
        _currentScreen.value = AppScreen.MAIN_HUB
        _currentTab.value = MainTab.DASHBOARD
    }

    // ---------------------------------------------------------
    // HỆ THỐNG TẬP LUYỆN
    // ---------------------------------------------------------
    fun selectTrainingFocus(focus: TrainingFocus) {
        val state = _careerState.value ?: return
        state.selectedTrainingFocus = focus
        _careerState.value = state.copy(selectedTrainingFocus = focus)
    }

    fun selectTrainingIntensity(intensity: TrainingIntensity) {
        val state = _careerState.value ?: return
        state.selectedTrainingIntensity = intensity
        _careerState.value = state.copy(selectedTrainingIntensity = intensity)
    }

    fun executeTrainingNow() {
        val state = _careerState.value ?: return
        val cost = state.selectedTrainingFocus.getCost(state.selectedTrainingIntensity)

        if (state.player.balance < cost) {
            showToast("Không đủ tiền chi trả buổi tập (€$cost)! Bạn chỉ còn €${state.player.balance}.")
            return
        }

        if (state.player.energy < 20 && state.selectedTrainingFocus != TrainingFocus.REST_AND_RECOVERY) {
            showToast("Thể lực quá thấp (<20%)! Hãy chọn nghỉ ngơi để tránh kiệt sức và chấn thương.")
            return
        }

        val result = TrainingEngine.executeWeeklyTraining(
            player = state.player,
            club = state.currentClub,
            focus = state.selectedTrainingFocus,
            intensity = state.selectedTrainingIntensity,
            purchasedItems = _shopItems.value,
            random = random
        )

        _lastTrainingResult.value = result
        showToast(result.summaryMessage)
        _careerState.value = state.copy()
        SaveGameManager.saveGame(context, 0, state)
    }

    // ---------------------------------------------------------
    // MÔ PHỎNG TRẬN ĐẤU TUẦN TỰ (SEQUENTIAL TIMELINE PLAYBACK)
    // ---------------------------------------------------------
    fun startMatchdaySimulation() {
        val state = _careerState.value ?: return
        if (_isSimulatingMatch.value) return

        val isUserHome = random.nextBoolean()
        val homeClub = if (isUserHome) state.currentClub else state.nextOpponent
        val awayClub = if (isUserHome) state.nextOpponent else state.currentClub

        // Sinh toàn bộ timeline trận đấu DUY NHẤT một lần (Deterministic / Seeded)
        val matchSeed = System.currentTimeMillis()
        val match = MatchSimulator.generateMatchTimeline(
            matchId = UUID.randomUUID().toString(),
            roundNumber = state.currentWeek,
            player = state.player,
            contract = state.currentContract,
            homeClub = homeClub,
            awayClub = awayClub,
            isUserHomeTeam = isUserHome,
            purchasedItems = _shopItems.value,
            seed = matchSeed,
            isInteractiveMomentsEnabled = state.isInteractiveMomentsEnabled
        )

        _currentMatch.value = match
        _displayedMinute.value = 0
        _displayedHomeScore.value = 0
        _displayedAwayScore.value = 0
        _visibleMatchEvents.value = emptyList()
        _isSimulatingMatch.value = true

        runTimelinePlayback(match, isUserHome)
    }

    private fun runTimelinePlayback(match: Match, isUserHome: Boolean) {
        matchSimulationJob?.cancel()
        matchSimulationJob = viewModelScope.launch {
            val events = match.timelineEvents

            for (event in events) {
                _displayedMinute.value = event.minute

                // Cập nhật sự kiện hiển thị
                _visibleMatchEvents.value = events.filter { it.minute <= event.minute }

                // TỈ SỐ CHỈ ĐƯỢC TÍNH TỪ DANH SÁCH BÀN THẮNG TRONG TIMELINE ĐÃ XẢY RA ĐẾN PHÚT NÀY
                _displayedHomeScore.value = match.goalsList.count { it.minute <= event.minute && it.clubId == match.homeClub.id }
                _displayedAwayScore.value = match.goalsList.count { it.minute <= event.minute && it.clubId == match.awayClub.id }

                // Nếu gặp khoảnh khắc tương tác, tạm dừng chờ người chơi chọn
                if (event.type == com.example.roadtolegend.model.MatchEventType.INTERACTIVE_DECISION) {
                    val moment = match.interactiveMoments.find { it.minute == event.minute && !it.resolved }
                    if (moment != null) {
                        _currentInteractiveMoment.value = moment
                        while (_currentInteractiveMoment.value != null) {
                            delay(150)
                        }
                        // Cập nhật lại tỉ số sau khi lựa chọn tương tác được giải quyết
                        _displayedHomeScore.value = match.goalsList.count { it.minute <= event.minute && it.clubId == match.homeClub.id }
                        _displayedAwayScore.value = match.goalsList.count { it.minute <= event.minute && it.clubId == match.awayClub.id }
                    }
                }

                delay(_simulationSpeed.value.delayMs)
            }

            finalizeMatch(match, isUserHome)
        }
    }

    fun skipMatchSimulation() {
        val match = _currentMatch.value ?: return
        val state = _careerState.value ?: return
        matchSimulationJob?.cancel()

        // Tự động giải quyết các tương tác còn lại nếu có
        match.interactiveMoments.filter { !it.resolved }.forEach { moment ->
            MatchSimulator.applyInteractiveChoice(
                match = match,
                moment = moment,
                choiceType = InteractiveChoiceType.FINESSE_SHOT,
                player = state.player,
                contract = state.currentContract,
                isUserHomeTeam = (match.homeClub.id == state.currentClub.id),
                random = random
            )
        }
        _currentInteractiveMoment.value = null

        val isUserHome = (match.homeClub.id == state.currentClub.id)
        _displayedMinute.value = 93
        _visibleMatchEvents.value = match.timelineEvents
        _displayedHomeScore.value = match.homeScore
        _displayedAwayScore.value = match.awayScore

        finalizeMatch(match, isUserHome)
    }

    fun submitInteractiveChoice(choiceType: InteractiveChoiceType) {
        val match = _currentMatch.value ?: return
        val moment = _currentInteractiveMoment.value ?: return
        val state = _careerState.value ?: return
        val isUserHome = (match.homeClub.id == state.currentClub.id)

        val (success, text) = MatchSimulator.applyInteractiveChoice(
            match = match,
            moment = moment,
            choiceType = choiceType,
            player = state.player,
            contract = state.currentContract,
            isUserHomeTeam = isUserHome,
            random = random
        )

        _currentInteractiveMoment.value = null
        showToast(if (success) "🎉 $text" else "❌ $text")
    }

    private fun finalizeMatch(match: Match, isUserHome: Boolean) {
        _isSimulatingMatch.value = false
        match.isCompleted = true
        val state = _careerState.value ?: return
        state.lastMatch = match

        // 1. Áp dụng các thay đổi từ kết quả trận đấu lên Player
        val perf = match.performance
        if (perf != null && perf.userRole != com.example.roadtolegend.model.MatchUserRole.BENCHED) {
            state.player.balance += (perf.wageEarned + perf.bonusEarned)
            state.player.energy = (state.player.energy - perf.energyConsumed).coerceAtLeast(5)
            state.player.fatigue = (state.player.fatigue + perf.fatigueAdded).coerceIn(0.0, 100.0)

            val formDelta = ((perf.rating - 6.5) * 3).toInt()
            state.player.form = (state.player.form + formDelta).coerceIn(20, 100)

            val won = if (isUserHome) match.homeScore > match.awayScore else match.awayScore > match.homeScore
            val lost = if (isUserHome) match.homeScore < match.awayScore else match.awayScore < match.homeScore
            val moraleDelta = if (won) 5 else if (lost) -4 else 1
            state.player.morale = (state.player.morale + moraleDelta).coerceIn(20, 100)

            // Cập nhật careerStats
            state.player.careerStats.matchesPlayed++
            if (perf.userRole == com.example.roadtolegend.model.MatchUserRole.STARTER) state.player.careerStats.starts++ else state.player.careerStats.subs++
            state.player.careerStats.goals += perf.goals
            state.player.careerStats.assists += perf.assists
            state.player.careerStats.tackles += perf.tacklesWon
            state.player.careerStats.totalRatingPoints += perf.rating
            if (perf.isManOfTheMatch) state.player.careerStats.manOfTheMatchCount++
            if (perf.rating >= 8.2) state.player.careerStats.totwCount++

            // Ghi nhận cột mốc
            if (state.player.careerStats.matchesPlayed == 1) {
                state.player.addMilestoneIfNew("Trận ra mắt chuyên nghiệp", "Thi đấu trận đầu tiên cho ${state.currentClub.shortName}.", "Tuần ${state.currentWeek}")
            }
            if (perf.goals > 0 && state.player.careerStats.goals == perf.goals) {
                state.player.addMilestoneIfNew("Bàn thắng đầu tiên trong sự nghiệp", "Ghi bàn thắng lịch sử đầu tiên vào lưới ${if (isUserHome) match.awayClub.shortName else match.homeClub.shortName}.", "Tuần ${state.currentWeek}", "⚽")
            }
            if (perf.goals >= 3) {
                state.player.addMilestoneIfNew("Cú Hat-trick lịch sử", "Ghi 3 bàn thắng trong một trận đấu đỉnh cao!", "Tuần ${state.currentWeek}", "🔥")
            }
            if (perf.isManOfTheMatch) {
                state.player.addMilestoneIfNew("Cầu thủ xuất sắc nhất trận (MOM)", "Được ban chuyên môn bình chọn là cầu thủ hay nhất trận đấu.", "Tuần ${state.currentWeek}", "⭐")
            }
        }

        // 2. Mô phỏng tất cả các trận khác trong vòng & Đồng bộ BXH + Danh sách Vua phá lưới
        LeagueSimulator.simulateOtherFixturesAndSyncLeague(
            league = state.currentLeague,
            fixtures = _roundFixtures.value,
            userMatch = match,
            userName = state.player.name,
            seed = match.seed
        )

        // Cập nhật tin tức sau trận
        val headline = if (perf != null && perf.goals > 0) {
            "${state.player.name} rực sáng với ${perf.goals} bàn thắng đưa ${state.currentClub.shortName} vượt khó!"
        } else if (perf != null && perf.rating >= 7.5) {
            "${state.player.name} nhận điểm số ấn tượng (${perf.rating}) trong trận gặp ${match.awayClub.shortName}."
        } else {
            "${match.homeClub.shortName} ${match.homeScore} - ${match.awayScore} ${match.awayClub.shortName}: Khép lại vòng đấu đầy kịch tính."
        }
        state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), state.currentWeek, headline, "Thu nhập: €${(perf?.wageEarned ?: 0) + (perf?.bonusEarned ?: 0)}."))

        _careerState.value = state.copy()
        SaveGameManager.saveGame(context, 0, state)
    }

    // ---------------------------------------------------------
    // VÒNG LẶP TIẾN HÀNH TUẦN MỚI
    // ---------------------------------------------------------
    fun advanceToNextWeek() {
        val state = _careerState.value ?: return

        // 1. Tự động chạy tập luyện nếu tuần đó chưa tập
        if (_lastTrainingResult.value == null) {
            executeTrainingNow()
        }
        _lastTrainingResult.value = null

        // 2. Trừ phí duy trì vật phẩm
        val purchased = _shopItems.value.filter { it.isPurchased }
        val weeklyCost = purchased.sumOf { it.weeklyMaintenance }
        state.player.balance = (state.player.balance - weeklyCost).coerceAtLeast(0)

        // 3. Giảm tuần chấn thương nếu có
        if (state.player.isInjured) {
            state.player.injuryWeeksRemaining--
            if (state.player.injuryWeeksRemaining <= 0) {
                state.player.injuryName = null
                state.player.morale = (state.player.morale + 10).coerceAtMost(100)
                state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), state.currentWeek, "Tin mừng: ${state.player.name} đã hoàn toàn bình phục!", "Bác sĩ xác nhận cầu thủ đã sẵn sàng trở lại thi đấu."))
            }
        }

        // 4. Chuyển tuần hoặc kết thúc mùa giải
        if (state.currentWeek >= state.totalWeeksInSeason) {
            state.isSeasonEnded = true
            archiveSeasonRecord(state)
            evaluateEndOfSeason(state)
        } else {
            state.currentWeek++
            val otherClubs = state.currentLeague.clubs.filter { it.id != state.currentClub.id }
            state.nextOpponent = otherClubs[(state.currentWeek - 1) % otherClubs.size]
            prepareRoundFixtures(state)
        }

        _currentMatch.value = null
        _displayedMinute.value = 0
        _displayedHomeScore.value = 0
        _displayedAwayScore.value = 0
        _visibleMatchEvents.value = emptyList()

        _careerState.value = state.copy()
        SaveGameManager.saveGame(context, 0, state)
        loadSaveSlots()
    }

    private fun archiveSeasonRecord(state: CareerState) {
        val topTeam = state.currentLeague.sortedStandings().firstOrNull()
        val isChampion = topTeam?.clubId == state.currentClub.id
        val trophyText = if (isChampion) "Vô địch ${state.currentLeague.name}" else ""

        val record = SeasonRecord(
            seasonNumber = state.seasonNumber,
            year = state.currentYear,
            clubId = state.currentClub.id,
            clubName = state.currentClub.name,
            clubShortName = state.currentClub.shortName,
            leagueId = state.currentLeague.id,
            leagueName = state.currentLeague.name,
            matchesPlayed = state.player.careerStats.matchesPlayed,
            starts = state.player.careerStats.starts,
            subs = state.player.careerStats.subs,
            minutesPlayed = state.player.careerStats.matchesPlayed * 80,
            goals = state.player.careerStats.goals,
            assists = state.player.careerStats.assists,
            averageRating = state.player.careerStats.averageRating,
            yellowCards = state.player.careerStats.yellowCards,
            redCards = state.player.careerStats.redCards,
            trophiesWon = trophyText,
            note = if (isChampion) "Thăng hạng lên hạng đấu cao hơn!" else "Trụ hạng thành công"
        )
        state.player.seasonRecords.add(record)
    }

    private fun evaluateEndOfSeason(state: CareerState) {
        val topTeam = state.currentLeague.sortedStandings().firstOrNull()
        val isUserChampion = (topTeam?.clubId == state.currentClub.id)
        if (isUserChampion) {
            val trophyName = "Vô địch ${state.currentLeague.name} (Mùa ${state.seasonNumber})"
            state.player.careerStats.trophies.add(trophyName)
            state.player.domesticReputation = (state.player.domesticReputation + 15).coerceAtMost(100)
            state.player.balance += 20_000
            state.player.addMilestoneIfNew("Chiếc cúp đầu tiên", trophyName, "Mùa ${state.seasonNumber}", "🏆")
            showToast("🏆 XUẤT SẮC! Bạn đã giành chức VÔ ĐỊCH ${state.currentLeague.name}!")
        }
    }

    fun startNextSeason() {
        val state = _careerState.value ?: return
        state.seasonNumber++
        state.currentYear++
        state.currentWeek = 1
        state.isSeasonEnded = false
        state.player.age++
        state.player.energy = 100
        state.player.fatigue = 0.0

        val newLeague = DefaultDatabase.initializeLeague(state.currentLeague.id)
        state.currentLeague = newLeague
        state.nextOpponent = newLeague.clubs.first { it.id != state.currentClub.id }
        prepareRoundFixtures(state)

        state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), 1, "Mùa giải mới ${state.seasonNumber} (${state.currentYear}) khởi tranh!", "${state.player.name} bước sang tuổi ${state.player.age}."))
        _careerState.value = state.copy()
        SaveGameManager.saveGame(context, 0, state)
    }

    // ---------------------------------------------------------
    // CỬA HÀNG (LIFESTYLE)
    // ---------------------------------------------------------
    fun purchaseItem(item: LifestyleItem) {
        val state = _careerState.value ?: return
        if (state.player.balance < item.price) {
            showToast("Số dư tài khoản không đủ (€${String.format("%,d", state.player.balance)})!")
            return
        }

        state.player.balance -= item.price
        state.purchasedItemIds.add(item.id)
        state.player.morale = (state.player.morale + item.moraleBonus).coerceAtMost(100)
        state.player.domesticReputation = (state.player.domesticReputation + item.reputationBonus).coerceAtMost(100)

        syncShopItems(state.purchasedItemIds)
        showToast("Đã mua thành công: ${item.nameVi}!")
        _careerState.value = state.copy()
        SaveGameManager.saveGame(context, 0, state)
    }

    private fun syncShopItems(purchasedIds: Set<String>) {
        val items = DefaultDatabase.getShopItems().map {
            it.copy(isPurchased = it.id in purchasedIds)
        }
        _shopItems.value = items
    }

    // ---------------------------------------------------------
    // LƯU & TẢI GAME
    // ---------------------------------------------------------
    fun saveToSlot(slotIndex: Int) {
        val state = _careerState.value ?: return
        val success = SaveGameManager.saveGame(context, slotIndex, state)
        if (success) {
            showToast("Đã lưu sự nghiệp thành công vào Vị trí #$slotIndex!")
            loadSaveSlots()
        } else {
            showToast("Lỗi khi lưu game!")
        }
    }

    fun loadFromSlot(slotIndex: Int) {
        val loaded = SaveGameManager.loadGame(context, slotIndex)
        if (loaded != null) {
            _careerState.value = loaded
            syncShopItems(loaded.purchasedItemIds)
            prepareRoundFixtures(loaded)
            _currentScreen.value = AppScreen.MAIN_HUB
            _currentTab.value = MainTab.DASHBOARD
            showToast("Đã tải thành công sự nghiệp của ${loaded.player.name}!")
        } else {
            showToast("Không tìm thấy dữ liệu ở Vị trí #$slotIndex!")
        }
    }

    // ---------------------------------------------------------
    // DEBUG PANEL
    // ---------------------------------------------------------
    fun debugAddFunds(amount: Long) {
        val state = _careerState.value ?: return
        state.player.balance += amount
        _careerState.value = state.copy()
        showToast("Debug: Đã cộng €${String.format("%,d", amount)}")
    }

    fun debugFullRecovery() {
        val state = _careerState.value ?: return
        state.player.energy = 100
        state.player.fatigue = 0.0
        state.player.morale = 100
        state.player.injuryWeeksRemaining = 0
        state.player.injuryName = null
        _careerState.value = state.copy()
        showToast("Debug: Phục hồi 100% Thể lực & Tinh thần, xoá chấn thương!")
    }
}
