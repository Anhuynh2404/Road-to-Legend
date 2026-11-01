package com.example.roadtolegend.model

data class TeamStanding(
    val clubId: String,
    val clubName: String,
    var played: Int = 0,
    var won: Int = 0,
    var drawn: Int = 0,
    var lost: Int = 0,
    var goalsFor: Int = 0,
    var goalsAgainst: Int = 0,
    var points: Int = 0,
    var recentForm: MutableList<String> = mutableListOf() // "W", "D", "L"
) {
    val goalDifference: Int
        get() = goalsFor - goalsAgainst

    fun addForm(result: String) {
        recentForm.add(result)
        if (recentForm.size > 5) {
            recentForm.removeAt(0)
        }
    }
}

data class League(
    val id: String,
    val name: String,
    val country: String,
    val tier: Int,
    val totalRounds: Int = 26,
    val promotionSpots: Int = 2,
    val relegationSpots: Int = 2,
    val clubs: List<Club> = emptyList(),
    var standings: MutableList<TeamStanding> = mutableListOf(),
    var currentRoundFixtures: MutableList<RoundFixture> = mutableListOf(),
    var topScorers: MutableList<PlayerStatLeader> = mutableListOf(),
    var topAssists: MutableList<PlayerStatLeader> = mutableListOf()
) {
    fun sortedStandings(): List<TeamStanding> {
        return standings.sortedWith(
            compareByDescending<TeamStanding> { it.points }
                .thenByDescending { it.goalDifference }
                .thenByDescending { it.goalsFor }
        )
    }
}
