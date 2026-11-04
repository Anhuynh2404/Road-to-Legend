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

import com.example.roadtolegend.engine.ContractNegotiationEngine
import com.example.roadtolegend.engine.FinanceEngine
import com.example.roadtolegend.engine.NewsAndRumorEngine
import com.example.roadtolegend.engine.TransferConfig
import com.example.roadtolegend.engine.TransferMarketEngine
import com.example.roadtolegend.model.Agent
import com.example.roadtolegend.model.AgentTier
import com.example.roadtolegend.model.NegotiationSession
import com.example.roadtolegend.model.NegotiationStatus
import com.example.roadtolegend.model.SquadRole
import com.example.roadtolegend.model.TransferOffer
import com.example.roadtolegend.model.TransferOfferStatus

enum class AppScreen {
    WELCOME,
    CREATION,
    MAIN_HUB
}

enum class MainTab {
    DASHBOARD,
    MATCHDAY,
    TRAINING,
    LEAGUE_TABLE,
    MORE,
    CAREER_STATS,
    TROPHY_CABINET,
    CONTRACT,
    TRANSFER,
    AGENT,
    FINANCE,
    SHOP,
    NEWS,
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

    private val _activeNegotiation = MutableStateFlow<NegotiationSession?>(null)
    val activeNegotiation: StateFlow<NegotiationSession?> = _activeNegotiation.asStateFlow()

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

    fun updatePlayerAvatar(avatarPath: String?) {
        val state = _careerState.value ?: return
        state.player.customAvatarPath = avatarPath
        val updatedPlayer = state.player.copy(customAvatarPath = avatarPath)
        _careerState.value = state.copy(player = updatedPlayer)
        SaveGameManager.saveGame(context, 0, state)
        showToast("Đã lưu ảnh đại diện thành công!")
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
        startingClub: Club,
        customAvatarPath: String? = null
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
            balance = 1_500,
            customAvatarPath = customAvatarPath
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
        val updated = state.copy(selectedTrainingFocus = focus)
        _careerState.value = updated
        showToast("Đã chọn bài tập: ${focus.titleVi}")
        SaveGameManager.saveGame(context, 0, updated)
    }

    fun selectTrainingIntensity(intensity: TrainingIntensity) {
        val state = _careerState.value ?: return
        val updated = state.copy(selectedTrainingIntensity = intensity)
        _careerState.value = updated
        showToast("Đã chọn cường độ: ${intensity.titleVi}")
        SaveGameManager.saveGame(context, 0, updated)
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

        // 4. Quyết toán tài chính tuần (Lương tuần, Thuế, Agent retainer, Giảm tuần hợp đồng & mượn, Biểu đồ Market Value)
        FinanceEngine.processWeeklyFinance(state)

        // 5. Cập nhật hạn các lời đề nghị chuyển nhượng
        state.incomingOffers.forEach { offer ->
            if (offer.isPendingPlayerAction) {
                offer.expiresInWeeks--
                if (offer.expiresInWeeks <= 0) {
                    offer.status = TransferOfferStatus.CANCELLED_BY_BUYER
                }
            }
        }

        // 6. Cập nhật trạng thái Cửa sổ chuyển nhượng
        state.isTransferWindowOpen = TransferConfig.isTransferWindowOpen(state.currentWeek)
        state.isDeadlineDay = TransferConfig.isDeadlineDay(state.currentWeek)

        // 7. Sinh offer mới từ CLB khác & Mô phỏng thị trường AI
        TransferMarketEngine.evaluateAndGeneratePlayerOffers(state, random)
        TransferMarketEngine.simulateAITransferMarket(state, random)

        // 8. Đưa tin tức & Tin đồn chuyển nhượng
        NewsAndRumorEngine.rollTransferRumor(state, random)

        // 9. Chuyển tuần hoặc kết thúc mùa giải
        if (state.currentWeek >= state.totalWeeksInSeason) {
            state.isSeasonEnded = true
            archiveSeasonRecord(state)
            evaluateEndOfSeason(state)
        } else {
            state.currentWeek++
            val otherClubs = state.currentLeague.clubs.filter { it.id != state.currentClub.id }
            if (otherClubs.isNotEmpty()) {
                state.nextOpponent = otherClubs[(state.currentWeek - 1) % otherClubs.size]
            }
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

    // ---------------------------------------------------------
    // CÁC THAO TÁC HỢP ĐỒNG & ĐÀM PHÁN (PHASE 2 - SECTION B)
    // ---------------------------------------------------------
    fun startContractRenewal() {
        val state = _careerState.value ?: return
        val (canRenew, reason) = ContractNegotiationEngine.canInitiateRenewal(state)
        if (!canRenew) {
            showToast(reason)
            return
        }

        val session = ContractNegotiationEngine.startNegotiation(
            player = state.player,
            club = state.currentClub,
            currentContract = state.currentContract,
            linkedOffer = null,
            agent = state.agent,
            isRenewal = true
        )
        _activeNegotiation.value = session
    }

    fun startOfferNegotiation(offer: TransferOffer) {
        val state = _careerState.value ?: return
        if (!offer.isWorkPermitApproved) {
            showToast("Thương vụ bị chặn: ${offer.workPermitNote ?: "Chưa có giấy phép lao động."}")
            return
        }
        val allClubs = DefaultDatabase.getAllClubs()
        val buyer = allClubs.find { it.id == offer.buyingClubId }
        if (buyer == null) {
            showToast("Không tìm thấy thông tin CLB đưa ra đề nghị.")
            return
        }

        val session = ContractNegotiationEngine.startNegotiation(
            player = state.player,
            club = buyer,
            currentContract = state.currentContract,
            linkedOffer = offer,
            agent = state.agent,
            isRenewal = false
        )
        offer.status = TransferOfferStatus.NEGOTIATING_PLAYER_TERMS
        _activeNegotiation.value = session
    }

    fun submitCounterProposal(
        demandedWage: Long,
        demandedSigningBonus: Long,
        demandedYears: Int,
        demandedRole: SquadRole,
        demandedReleaseClause: Long?
    ) {
        val session = _activeNegotiation.value ?: return
        val state = _careerState.value ?: return
        val allClubs = DefaultDatabase.getAllClubs()
        val club = allClubs.find { it.id == session.clubId } ?: state.currentClub

        val updatedSession = ContractNegotiationEngine.submitPlayerCounterProposal(
            session = session,
            player = state.player,
            club = club,
            agent = state.agent,
            demandedWage = demandedWage,
            demandedSigningBonus = demandedSigningBonus,
            demandedYears = demandedYears,
            demandedRole = demandedRole,
            demandedReleaseClause = demandedReleaseClause,
            random = random
        )
        _activeNegotiation.value = updatedSession
        showToast(updatedSession.latestFeedbackMessageVi)
    }

    fun acceptNegotiationAgreement() {
        val session = _activeNegotiation.value ?: return
        val state = _careerState.value ?: return
        if (session.status != NegotiationStatus.AGREED) {
            showToast("Chưa đạt được sự đồng thuận đầy đủ từ phía CLB.")
            return
        }

        val finalContract = ContractNegotiationEngine.finalizeContractAgreement(session, state.seasonNumber)

        if (session.isRenewal) {
            state.currentContract = finalContract
            state.player.addMilestoneIfNew(
                "Gia hạn hợp đồng với ${state.currentClub.shortName}",
                "Cam kết gắn bó thêm ${finalContract.contractYearsRemaining} năm với mức lương €${finalContract.weeklyWage}/tuần.",
                "Tuần ${state.currentWeek}",
                "🤝"
            )
            val renewNews = "CHÍNH THỨC: ${state.player.name} đặt bút gia hạn hợp đồng cùng ${state.currentClub.name}!"
            state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), state.currentWeek, renewNews, "Mức đãi ngộ mới thể hiện rõ tầm quan trọng của cầu thủ trong kế hoạch CLB.", "CONTRACT", "Chính Thức CLB", 1, false, state.currentClub.id))
            showToast("Đã hoàn tất gia hạn hợp đồng thành công!")
        } else {
            if (!state.isTransferWindowOpen && !state.currentContract.isFreeAgent) {
                showToast("Cửa sổ chuyển nhượng đã đóng, không thể hoàn tất chuyển nhượng.")
                return
            }
            val linkedOffer = state.incomingOffers.find { it.id == session.linkedOfferId }
            if (linkedOffer != null) {
                val success = TransferMarketEngine.executePlayerTransfer(state, linkedOffer, finalContract)
                if (success) {
                    showToast("Chúc mừng bạn đã chính thức chuyển sang khoác áo ${finalContract.clubName}!")
                } else {
                    showToast("Không thể hoàn tất chuyển nhượng.")
                }
            }
        }

        _activeNegotiation.value = null
        _careerState.value = state.copy()
        SaveGameManager.saveGame(context, 0, state)
    }

    fun cancelNegotiation() {
        _activeNegotiation.value = null
    }

    // ---------------------------------------------------------
    // CÁC THAO TÁC CHUYỂN NHƯỢNG & MƯỢN CẦU THỦ (SECTION D, E)
    // ---------------------------------------------------------
    fun acceptTransferOfferDirectly(offer: TransferOffer) {
        val state = _careerState.value ?: return
        if (!state.isTransferWindowOpen && !state.currentContract.isFreeAgent) {
            showToast("Cửa sổ chuyển nhượng hiện đang đóng.")
            return
        }
        if (!offer.isWorkPermitApproved) {
            showToast("Thương vụ bị chặn: ${offer.workPermitNote ?: "Chưa có giấy phép lao động."}")
            return
        }

        val contract = Contract(
            clubId = offer.buyingClubId,
            clubName = offer.buyingClubName,
            weeklyWage = offer.offeredWage,
            signingBonus = offer.offeredSigningBonus,
            bonusPerAppearance = (offer.offeredWage * 0.12).toLong().coerceAtLeast(50L),
            bonusPerGoal = offer.offeredBonusPerGoal,
            bonusPerAssist = offer.offeredBonusPerAssist,
            bonusPerCleanSheet = (offer.offeredWage * 0.15).toLong().coerceAtLeast(80L),
            bonusTrophy = (offer.offeredWage * 10).coerceAtLeast(5_000L),
            contractYearsRemaining = offer.offeredContractYears,
            totalWeeksRemaining = offer.offeredContractYears * 26,
            releaseClause = offer.offeredReleaseClause,
            squadRole = offer.offeredRole,
            clubSatisfaction = 80,
            startSeason = state.seasonNumber,
            endSeason = state.seasonNumber + offer.offeredContractYears
        )
        val success = TransferMarketEngine.executePlayerTransfer(state, offer, contract)
        if (success) {
            showToast("Gia nhập thành công ${offer.buyingClubName}!")
            _careerState.value = state.copy()
            SaveGameManager.saveGame(context, 0, state)
        } else {
            showToast("Không thể hoàn tất chuyển nhượng.")
        }
    }

    fun rejectTransferOffer(offer: TransferOffer) {
        val state = _careerState.value ?: return
        offer.status = TransferOfferStatus.REJECTED_BY_PLAYER
        showToast("Đã từ chối lời đề nghị từ ${offer.buyingClubShortName}.")
        _careerState.value = state.copy()
    }

    fun acceptLoanOffer(offer: TransferOffer) {
        val state = _careerState.value ?: return
        if (!state.isTransferWindowOpen && !state.currentContract.isFreeAgent) {
            showToast("Cửa sổ chuyển nhượng hiện đang đóng.")
            return
        }
        if (!offer.isWorkPermitApproved) {
            showToast("Thương vụ mượn bị chặn: ${offer.workPermitNote ?: "Chưa có giấy phép lao động."}")
            return
        }
        val success = TransferMarketEngine.executePlayerLoan(state, offer)
        if (success) {
            showToast("Bắt đầu thời gian thi đấu cho mượn tại ${offer.buyingClubShortName}!")
            _careerState.value = state.copy()
            SaveGameManager.saveGame(context, 0, state)
        } else {
            showToast("Không thể hoàn tất hợp đồng cho mượn.")
        }
    }

    fun scoutForeignMarket(regionKey: String) {
        val state = _careerState.value ?: return
        val (success, message) = com.example.roadtolegend.engine.InternationalTransferRules.scoutForeignMarket(
            state = state,
            regionName = regionKey,
            random = random
        )
        showToast(message)
        if (success) {
            _careerState.value = state.copy()
            SaveGameManager.saveGame(context, 0, state)
        }
    }

    fun submitTransferRequest() {
        val state = _careerState.value ?: return
        val (success, msg) = TransferMarketEngine.submitTransferRequest(state)
        showToast(msg)
        if (success) {
            _careerState.value = state.copy()
            SaveGameManager.saveGame(context, 0, state)
        }
    }

    fun submitLoanRequest() {
        val state = _careerState.value ?: return
        val (success, msg) = TransferMarketEngine.submitLoanRequest(state, random)
        showToast(msg)
        if (success) {
            _careerState.value = state.copy()
            SaveGameManager.saveGame(context, 0, state)
        }
    }

    fun recallLoanInWinter() {
        val state = _careerState.value ?: return
        val (success, msg) = TransferMarketEngine.recallLoanInWinter(state)
        showToast(msg)
        if (success) {
            _careerState.value = state.copy()
            SaveGameManager.saveGame(context, 0, state)
        }
    }

    fun exerciseBuyOptionOnLoan() {
        val state = _careerState.value ?: return
        val (success, msg) = TransferMarketEngine.exerciseBuyOption(state)
        showToast(msg)
        if (success) {
            _careerState.value = state.copy()
            SaveGameManager.saveGame(context, 0, state)
        }
    }

    fun signFreeAgentOffer(club: Club) {
        val state = _careerState.value ?: return
        TransferMarketEngine.signFreeAgentContract(state, club)
        showToast("Chào mừng bạn gia nhập ${club.name}!")
        _careerState.value = state.copy()
        SaveGameManager.saveGame(context, 0, state)
    }

    // ---------------------------------------------------------
    // THAO TÁC NGƯỜI ĐẠI DIỆN (AGENT - SECTION C)
    // ---------------------------------------------------------
    fun hireAgent(agent: Agent) {
        val state = _careerState.value ?: return
        if (state.player.ovr < agent.tier.minOvrRequired) {
            showToast("${agent.name} yêu cầu OVR tối thiểu ${agent.tier.minOvrRequired} (Hiện tại: ${state.player.ovr}).")
            return
        }
        if (state.player.domesticReputation < agent.tier.minReputationRequired) {
            showToast("${agent.name} yêu cầu Danh tiếng tối thiểu ${agent.tier.minReputationRequired} (Hiện tại: ${state.player.domesticReputation}).")
            return
        }

        state.agent = agent
        state.player.addMilestoneIfNew(
            "Hợp tác cùng ${agent.name}",
            "Ký hợp đồng dịch vụ đại diện với ${agent.tier.titleVi}.",
            "Tuần ${state.currentWeek}",
            "👔"
        )
        val news = "NGƯỜI ĐẠI DIỆN: ${state.player.name} chính thức bắt tay hợp tác cùng ${agent.name} (${agent.tier.titleVi})!"
        state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), state.currentWeek, news, "Bước đi chiến lược giúp nâng tầm vị thế đàm phán hợp đồng trong tương lai.", "AGENT", "Hậu Trường Thể Thao", 1, false))

        showToast("Đã ký hợp đồng làm việc cùng người đại diện ${agent.name}!")
        _careerState.value = state.copy()
        SaveGameManager.saveGame(context, 0, state)
    }

    fun fireAgent() {
        val state = _careerState.value ?: return
        val oldName = state.agent.name
        state.agent = Agent("agent_none", "Gia đình / Tự đại diện", AgentTier.NONE)
        showToast("Đã sa thải người đại diện $oldName. Bạn sẽ tự đàm phán hợp đồng.")
        _careerState.value = state.copy()
        SaveGameManager.saveGame(context, 0, state)
    }

    fun askAgentMarketAdvice(): String {
        val state = _careerState.value ?: return "Chưa có dữ liệu."
        val player = state.player
        val agent = state.agent
        val marketVal = TransferConfig.calculateMarketValue(player, state.currentContract.totalWeeksRemaining)

        return when (agent.tier) {
            AgentTier.NONE -> "Người thân khuyên bạn: 'Hiện tại con cứ tập trung tập luyện và thi đấu tốt, giá trị ước tính tầm €${String.format("%,d", marketVal)}. Khi cần gia hạn hãy cẩn trọng thương lượng.'"
            AgentTier.ROOKIE -> "${agent.name} chia sẻ: 'Một số CLB tại giải hạng dưới đang để ý đến bạn. Nếu duy trì phong độ trên 75, chúng ta sẽ có cơ hội tốt ở kỳ chuyển nhượng sắp tới.'"
            AgentTier.EXPERIENCED -> "${agent.name} nhận định: 'Bạn đang được định giá khoảng €${String.format("%,d", marketVal)}. Với OVR ${player.ovr}, tôi đang tích cực kết nối với các CLB hàng đầu quốc nội để sẵn sàng cho một bản hợp đồng béo bở.'"
            AgentTier.ELITE -> "${agent.name} báo cáo: 'Mạng lưới tuyển trạch viên khu vực và châu Âu đánh giá rất cao tiềm năng của bạn. Nếu có thể giành danh hiệu cá nhân mùa này, cánh cửa xuất ngoại hoàn toàn rộng mở!'"
            AgentTier.SUPER_AGENT -> "${agent.name} tự tin tuyên bố: 'Tôi đang nắm trong tay danh sách theo dõi từ các ông lớn. Việc của cậu là tỏa sáng trên sân, phần còn lại để tôi ép các CLB phải chi ra mức đãi ngộ cao nhất thế giới!'"
        }
    }

    // ---------------------------------------------------------
    // TIN TỨC & TRUYỀN THÔNG (SECTION G)
    // ---------------------------------------------------------
    fun markNewsAsRead(newsId: String) {
        val state = _careerState.value ?: return
        state.newsFeed.find { it.id == newsId }?.isRead = true
        _careerState.value = state.copy()
    }

    fun markAllNewsAsRead() {
        val state = _careerState.value ?: return
        state.newsFeed.forEach { it.isRead = true }
        _careerState.value = state.copy()
    }

    // ---------------------------------------------------------
    // DEBUG PANEL: TUA NHANH NHIỀU MÙA GIẢI
    // ---------------------------------------------------------
    fun debugFastForwardSeasons(seasonsCount: Int) {
        val state = _careerState.value ?: return
        for (s in 0 until seasonsCount) {
            while (!state.isSeasonEnded) {
                advanceToNextWeek()
            }
            startNextSeason()
        }
        showToast("Debug: Đã tua nhanh mô phỏng thành công $seasonsCount mùa giải!")
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
            
            // Thưởng danh hiệu theo điều khoản hợp đồng + thưởng giải đấu
            val trophyBonusTotal = 20_000L + state.currentContract.bonusTrophy
            state.player.balance += trophyBonusTotal
            state.financeTransactions.add(
                0,
                com.example.roadtolegend.model.FinanceTransaction(
                    id = UUID.randomUUID().toString(),
                    week = state.currentWeek,
                    season = state.seasonNumber,
                    type = com.example.roadtolegend.model.TransactionType.TROPHY_BONUS,
                    amount = trophyBonusTotal,
                    balanceAfter = state.player.balance,
                    descriptionVi = "Thưởng chức vô địch ${state.currentLeague.name} theo hợp đồng"
                )
            )

            state.player.addMilestoneIfNew("Chiếc cúp đầu tiên", trophyName, "Mùa ${state.seasonNumber}", "🏆")
            showToast("🏆 XUẤT SẮC! Bạn đã giành chức VÔ ĐỊCH ${state.currentLeague.name}!")
        }

        // TỔ CHỨC LỄ TRAO GIẢI CUỐI MÙA & GALA QUẢ BÓNG VÀNG
        val ceremony = com.example.roadtolegend.engine.AwardsEngine.generateSeasonAwards(state, random)
        state.pendingCeremonyResult = ceremony
        ceremony.userEarnedTrophies.forEach { trophy ->
            if (state.trophiesCabinet.none { it.name == trophy.name && it.seasonNumber == trophy.seasonNumber }) {
                state.trophiesCabinet.add(0, trophy)
            }
        }

        val awardsNews = "LỄ TRAO GIẢI: Khép lại mùa bóng ${state.seasonNumber} (${state.currentYear}). Các danh hiệu cá nhân và Quả bóng vàng đã được trao!"
        state.newsFeed.add(0, NewsItem(UUID.randomUUID().toString(), state.currentWeek, awardsNews, "Chi tiết các cúp và kỷ niệm chương được lưu giữ tại Tủ Danh Hiệu."))
    }

    fun dismissSeasonCeremony() {
        val state = _careerState.value ?: return
        state.pendingCeremonyResult = null
        startNextSeason()
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

        // Tự động tăng lương hàng năm nếu hợp đồng có điều khoản
        if (state.currentContract.annualWageIncreasePercent > 0) {
            val increase = (state.currentContract.weeklyWage * (state.currentContract.annualWageIncreasePercent / 100.0)).toLong()
            state.currentContract.weeklyWage += increase
            showToast("Hợp đồng được tự động tăng ${state.currentContract.annualWageIncreasePercent}% lương cho mùa giải mới!")
        }

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
