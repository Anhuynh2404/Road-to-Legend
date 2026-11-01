package com.example.roadtolegend.model

enum class MatchEventType(val icon: String) {
    KICKOFF("⏱️"),
    POSSESSION_PHASE("⚽"),
    SHOT_OFF_TARGET("↗️"),
    SHOT_BLOCKED("🛡️"),
    SHOT_SAVED("🧤"),
    GOAL("⚽"),
    CORNER_KICK("🚩"),
    OFFSIDE("⚠️"),
    FOUL("💥"),
    YELLOW_CARD("🟨"),
    RED_CARD("🟥"),
    PENALTY_AWARDED("🎯"),
    SUBSTITUTION("🔄"),
    INJURY_EVENT("🚑"),
    INTERACTIVE_DECISION("⚡"),
    HALF_TIME("⏸️"),
    EXTRA_TIME_ANNOUNCED("⏱️"),
    FULL_TIME("🏁")
}

data class GoalRecord(
    val minute: Int,
    val scorerName: String,
    val assistName: String? = null,
    val clubId: String,
    val clubShortName: String,
    val isPenalty: Boolean = false,
    val isUserScorer: Boolean = false,
    val isUserAssist: Boolean = false
)

data class TeamMatchStats(
    var possessionPercent: Int = 50,
    var shotsTotal: Int = 0,
    var shotsOnTarget: Int = 0,
    var fouls: Int = 0,
    var corners: Int = 0,
    var yellowCards: Int = 0,
    var redCards: Int = 0
)

data class MatchEvent(
    val id: String,
    val minute: Int,
    val type: MatchEventType,
    val clubId: String,
    val clubShortName: String,
    val playerName: String? = null,
    val secondaryPlayerName: String? = null,
    val description: String,
    val isUserInvolved: Boolean = false,
    val isGoalScored: Boolean = false
)

enum class InteractiveChoiceType {
    FINESSE_SHOT,     // Cứa lòng góc xa
    POWER_SHOT,       // Sút mu uy lực
    PASS_TO_TEAMMATE, // Chuyền cho đồng đội
    DRIBBLE_PAST_GK,  // Lừa qua thủ môn
    DIRECT_FREEKICK,  // Sút phạt trực tiếp hàng rào
    THROUGH_BALL      // Chọc khe vượt tuyến
}

data class InteractiveChoice(
    val type: InteractiveChoiceType,
    val titleVi: String,
    val descriptionVi: String,
    val hiddenSuccessChancePercent: Int // Ẩn khỏi UI theo yêu cầu người dùng
)

data class InteractiveMoment(
    val id: String,
    val minute: Int,
    val situationText: String,
    val contextDescription: String,
    val choices: List<InteractiveChoice>,
    var resolved: Boolean = false,
    var selectedChoice: InteractiveChoiceType? = null,
    var resultText: String? = null,
    var isGoal: Boolean = false
)

enum class MatchUserRole(val titleVi: String) {
    STARTER("Đá chính"),
    SUBSTITUTE("Dự bị vào sân"),
    BENCHED("Dự bị không vào sân")
}

data class MatchPerformance(
    val userRole: MatchUserRole,
    val minutesPlayed: Int,
    val goals: Int,
    val assists: Int,
    val shotsTotal: Int,
    val shotsOnTarget: Int,
    val passesCompleted: Int,
    val tacklesWon: Int,
    val dribblesCompleted: Int,
    val rating: Double,
    val isManOfTheMatch: Boolean,
    val wageEarned: Long,
    val bonusEarned: Long,
    val energyConsumed: Int,
    val fatigueAdded: Double,
    val injurySustainedWeeks: Int = 0,
    val injuryName: String? = null
)

data class Match(
    val id: String,
    val roundNumber: Int,
    val homeClub: Club,
    val awayClub: Club,
    var homeScore: Int = 0,
    var awayScore: Int = 0,
    val goalsList: MutableList<GoalRecord> = mutableListOf(),
    val homeStats: TeamMatchStats = TeamMatchStats(),
    val awayStats: TeamMatchStats = TeamMatchStats(),
    val timelineEvents: List<MatchEvent> = emptyList(),
    var performance: MatchPerformance? = null,
    var interactiveMoments: MutableList<InteractiveMoment> = mutableListOf(),
    var isCompleted: Boolean = false,
    val seed: Long = 0L
) {
    // Invariant verification to guarantee scores NEVER desync or jump without a goal
    fun verifyScoreInvariant() {
        val calculatedHomeGoals = goalsList.count { it.clubId == homeClub.id }
        val calculatedAwayGoals = goalsList.count { it.clubId == awayClub.id }
        check(homeScore == calculatedHomeGoals) {
            "Score invariant broken! homeScore=$homeScore but counted=$calculatedHomeGoals"
        }
        check(awayScore == calculatedAwayGoals) {
            "Score invariant broken! awayScore=$awayScore but counted=$calculatedAwayGoals"
        }
    }
}
