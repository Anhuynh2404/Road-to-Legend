package com.example.roadtolegend.model

data class SeasonRecord(
    val seasonNumber: Int,
    val year: Int,
    val clubId: String,
    val clubName: String,
    val clubShortName: String,
    val leagueId: String,
    val leagueName: String,
    var matchesPlayed: Int = 0,
    var starts: Int = 0,
    var subs: Int = 0,
    var minutesPlayed: Int = 0,
    var goals: Int = 0,
    var assists: Int = 0,
    var averageRating: Double = 0.0,
    var yellowCards: Int = 0,
    var redCards: Int = 0,
    var trophiesWon: String = "",
    var note: String = ""
)

data class CareerMilestone(
    val id: String,
    val titleVi: String,
    val descriptionVi: String,
    val dateAchievedText: String,
    val iconEmoji: String = "🏆"
)
