package com.example.roadtolegend.model

import java.util.UUID

enum class TrophyCategory(val titleVi: String, val iconEmoji: String) {
    INDIVIDUAL("Danh hiệu cá nhân", "⭐"),
    CLUB("Danh hiệu cùng CLB", "🏆"),
    NATIONAL_TEAM("Danh hiệu ĐTQG", "🎖️")
}

data class TrophyItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: TrophyCategory,
    val year: Int,
    val seasonNumber: Int,
    val competitionOrOrganization: String,
    val iconEmoji: String = "🏆",
    val description: String = "",
    val badgeColorHex: String = "#FFD700"
)

data class SeasonAwardWinner(
    val awardKey: String,
    val awardNameVi: String,
    val winnerName: String,
    val clubName: String,
    val statsText: String,
    val isUser: Boolean,
    val iconEmoji: String,
    val descriptionVi: String
)

data class BallonDorCandidate(
    val rank: Int,
    val playerName: String,
    val clubName: String,
    val nationality: String,
    val ovr: Int,
    val statsSummary: String,
    val isUser: Boolean
)

data class SeasonAwardsCeremony(
    val seasonNumber: Int,
    val year: Int,
    val leagueName: String,
    val leagueChampionClubName: String,
    val isUserChampion: Boolean,
    val awards: List<SeasonAwardWinner>,
    val ballonDorWinner: BallonDorCandidate,
    val ballonDorTopCandidates: List<BallonDorCandidate>,
    val userEarnedTrophies: List<TrophyItem>
)
