package com.example.roadtolegend.model

data class RoundFixture(
    val id: String,
    val roundNumber: Int,
    val homeClub: Club,
    val awayClub: Club,
    var homeScore: Int? = null,
    var awayScore: Int? = null,
    val goals: MutableList<GoalRecord> = mutableListOf(),
    var isUserMatch: Boolean = false,
    var isSimulated: Boolean = false
)

data class PlayerStatLeader(
    val playerId: String,
    val playerName: String,
    val clubId: String,
    val clubShortName: String,
    var count: Int,
    var matchesPlayed: Int = 0,
    val isUser: Boolean = false
)
