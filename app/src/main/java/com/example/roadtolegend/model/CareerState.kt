package com.example.roadtolegend.model

data class NewsItem(
    val id: String,
    val week: Int,
    val headline: String,
    val body: String,
    val category: String = "MATCH", // MATCH, TRANSFER, RUMOR, CONTRACT, AGENT, INJURY, AWARDS
    val sourceName: String = "Bản Tin Thể Thao",
    val reliabilityTier: Int = 1,   // 1: Chính thức / Rất uy tín (Tier 1), 2: Truyền thông uy tín (Tier 2), 3: Tin đồn lá cải (Tier 3)
    var isRead: Boolean = false,
    val linkedClubId: String? = null,
    val isRumorTrue: Boolean = false,
    val rumorTargetClubId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class CareerState(
    var seasonNumber: Int = 1,
    var currentYear: Int = 2026,
    var currentWeek: Int = 1,
    val totalWeeksInSeason: Int = 26,
    var player: Player,
    var currentClub: Club,
    var currentLeague: League,
    var currentContract: Contract,
    var nextOpponent: Club,
    var selectedTrainingFocus: TrainingFocus = TrainingFocus.SHOOTING,
    var selectedTrainingIntensity: TrainingIntensity = TrainingIntensity.MEDIUM,
    var purchasedItemIds: MutableSet<String> = mutableSetOf(),
    var lastMatch: Match? = null,
    val newsFeed: MutableList<NewsItem> = mutableListOf(),
    var isSeasonEnded: Boolean = false,
    var isFictionalMode: Boolean = false,
    var isInteractiveMomentsEnabled: Boolean = true,

    // ==========================================
    // CÁC TRƯỜNG MỚI CHO GIAI ĐOẠN 2
    // ==========================================
    var agent: Agent = Agent("agent_default", "Tự đại diện", AgentTier.NONE),
    var activeLoan: LoanRecord? = null,
    val incomingOffers: MutableList<TransferOffer> = mutableListOf(),
    var activeNegotiation: NegotiationSession? = null,
    val marketHistory: MutableList<MarketValuePoint> = mutableListOf(),
    val financeTransactions: MutableList<FinanceTransaction> = mutableListOf(),
    var isTransferWindowOpen: Boolean = false,
    var isDeadlineDay: Boolean = false,
    var isTransferListed: Boolean = false,
    var transferRequestSubmitted: Boolean = false,
    val aiCompletedTransfers: MutableList<AITransferRecord> = mutableListOf(),
    val freeAgents: MutableList<ClubPlayer> = mutableListOf(),

    // ==========================================
    // TỦ DANH HIỆU & LỄ TRAO GIẢI CUỐI MÙA
    // ==========================================
    val trophiesCabinet: MutableList<TrophyItem> = mutableListOf(),
    var pendingCeremonyResult: SeasonAwardsCeremony? = null
)

