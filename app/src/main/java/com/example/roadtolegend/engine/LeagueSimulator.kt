package com.example.roadtolegend.engine

import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.GoalRecord
import com.example.roadtolegend.model.League
import com.example.roadtolegend.model.Match
import com.example.roadtolegend.model.PlayerStatLeader
import com.example.roadtolegend.model.RoundFixture
import java.util.UUID
import kotlin.random.Random

object LeagueSimulator {

    fun generateRoundFixtures(
        league: League,
        roundNumber: Int,
        userClubId: String,
        opponentClubId: String,
        userMatch: Match?
    ): MutableList<RoundFixture> {
        val list = mutableListOf<RoundFixture>()

        // 1. Trận đấu của người chơi
        val homeClub = userMatch?.homeClub ?: league.clubs.first { it.id == userClubId }
        val awayClub = userMatch?.awayClub ?: league.clubs.first { it.id == opponentClubId }

        list.add(
            RoundFixture(
                id = userMatch?.id ?: "user_round_$roundNumber",
                roundNumber = roundNumber,
                homeClub = homeClub,
                awayClub = awayClub,
                homeScore = userMatch?.homeScore,
                awayScore = userMatch?.awayScore,
                goals = userMatch?.goalsList ?: mutableListOf(),
                isUserMatch = true,
                isSimulated = userMatch?.isCompleted ?: false
            )
        )

        // 2. Các cặp đấu còn lại trong vòng
        val otherClubs = league.clubs.filter { it.id != userClubId && it.id != opponentClubId }
        val shuffled = otherClubs.shuffled(Random(roundNumber * 31L))

        for (i in 0 until shuffled.size - 1 step 2) {
            val teamA = shuffled[i]
            val teamB = shuffled[i + 1]
            list.add(
                RoundFixture(
                    id = "round_${roundNumber}_fixture_$i",
                    roundNumber = roundNumber,
                    homeClub = teamA,
                    awayClub = teamB,
                    isUserMatch = false,
                    isSimulated = false
                )
            )
        }

        league.currentRoundFixtures = list
        return list
    }

    fun simulateOtherFixturesAndSyncLeague(
        league: League,
        fixtures: List<RoundFixture>,
        userMatch: Match,
        userName: String,
        seed: Long = System.currentTimeMillis()
    ) {
        val random = Random(seed)

        // 1. Cập nhật kết quả trận của người chơi vào BXH và vào fixtures list
        updateStandingWithMatch(league, userMatch.homeClub.id, userMatch.awayClub.id, userMatch.homeScore, userMatch.awayScore)

        fixtures.find { it.isUserMatch }?.let { userFixture ->
            userFixture.homeScore = userMatch.homeScore
            userFixture.awayScore = userMatch.awayScore
            userFixture.isSimulated = true
            userFixture.goals.clear()
            userFixture.goals.addAll(userMatch.goalsList)
        }

        // Đồng bộ các bàn thắng của trận đấu người chơi vào danh sách Vua phá lưới & Vua kiến tạo
        userMatch.goalsList.forEach { goal ->
            recordGoalInLeaderboard(league, goal.scorerName, goal.clubId, goal.clubShortName, isScorer = true)
            goal.assistName?.let { assistPlayer ->
                recordGoalInLeaderboard(league, assistPlayer, goal.clubId, goal.clubShortName, isScorer = false)
            }
        }

        // 2. Mô phỏng tất cả các trận khác trong vòng
        fixtures.filter { !it.isUserMatch && !it.isSimulated }.forEach { fixture ->
            val diff = (fixture.homeClub.squadOvrAverage - fixture.awayClub.squadOvrAverage).coerceIn(-15, 15)
            // Phân phối bàn thắng thực tế: 0-3 bàn
            val homeGoalsCount = when {
                random.nextInt(100) < 30 -> 0
                random.nextInt(100) < 68 -> 1
                random.nextInt(100) < 90 -> 2
                else -> 3
            } + if (diff > 8) 1 else 0

            val awayGoalsCount = when {
                random.nextInt(100) < 35 -> 0
                random.nextInt(100) < 70 -> 1
                random.nextInt(100) < 92 -> 2
                else -> 3
            } + if (diff < -8) 1 else 0

            fixture.homeScore = homeGoalsCount
            fixture.awayScore = awayGoalsCount
            fixture.isSimulated = true
            fixture.goals.clear()

            // Tạo người ghi bàn thực tế cho đội nhà
            for (g in 0 until homeGoalsCount) {
                val scorer = fixture.homeClub.keyPlayers.getOrNull(g % fixture.homeClub.keyPlayers.size)
                    ?: "${fixture.homeClub.shortName} ST"
                val assister = fixture.homeClub.keyPlayers.getOrNull((g + 1) % fixture.homeClub.keyPlayers.size)
                val goalRecord = GoalRecord(
                    minute = random.nextInt(8, 88),
                    scorerName = scorer,
                    assistName = assister,
                    clubId = fixture.homeClub.id,
                    clubShortName = fixture.homeClub.shortName
                )
                fixture.goals.add(goalRecord)
                recordGoalInLeaderboard(league, scorer, fixture.homeClub.id, fixture.homeClub.shortName, isScorer = true)
                if (assister != null) {
                    recordGoalInLeaderboard(league, assister, fixture.homeClub.id, fixture.homeClub.shortName, isScorer = false)
                }
            }

            // Tạo người ghi bàn thực tế cho đội khách
            for (g in 0 until awayGoalsCount) {
                val scorer = fixture.awayClub.keyPlayers.getOrNull(g % fixture.awayClub.keyPlayers.size)
                    ?: "${fixture.awayClub.shortName} ST"
                val assister = fixture.awayClub.keyPlayers.getOrNull((g + 1) % fixture.awayClub.keyPlayers.size)
                val goalRecord = GoalRecord(
                    minute = random.nextInt(8, 88),
                    scorerName = scorer,
                    assistName = assister,
                    clubId = fixture.awayClub.id,
                    clubShortName = fixture.awayClub.shortName
                )
                fixture.goals.add(goalRecord)
                recordGoalInLeaderboard(league, scorer, fixture.awayClub.id, fixture.awayClub.shortName, isScorer = true)
                if (assister != null) {
                    recordGoalInLeaderboard(league, assister, fixture.awayClub.id, fixture.awayClub.shortName, isScorer = false)
                }
            }

            // Cập nhật BXH cho cặp đấu này
            updateStandingWithMatch(league, fixture.homeClub.id, fixture.awayClub.id, homeGoalsCount, awayGoalsCount)
        }
    }

    private fun updateStandingWithMatch(
        league: League,
        homeClubId: String,
        awayClubId: String,
        homeScore: Int,
        awayScore: Int
    ) {
        val homeStanding = league.standings.find { it.clubId == homeClubId }
        val awayStanding = league.standings.find { it.clubId == awayClubId }

        if (homeStanding != null && awayStanding != null) {
            homeStanding.played++
            awayStanding.played++
            homeStanding.goalsFor += homeScore
            homeStanding.goalsAgainst += awayScore
            awayStanding.goalsFor += awayScore
            awayStanding.goalsAgainst += homeScore

            when {
                homeScore > awayScore -> {
                    homeStanding.won++
                    homeStanding.points += 3
                    homeStanding.addForm("W")
                    awayStanding.lost++
                    awayStanding.addForm("L")
                }
                homeScore < awayScore -> {
                    awayStanding.won++
                    awayStanding.points += 3
                    awayStanding.addForm("W")
                    homeStanding.lost++
                    homeStanding.addForm("L")
                }
                else -> {
                    homeStanding.drawn++
                    homeStanding.points += 1
                    homeStanding.addForm("D")
                    awayStanding.drawn++
                    awayStanding.points += 1
                    awayStanding.addForm("D")
                }
            }
        }
    }

    private fun recordGoalInLeaderboard(
        league: League,
        playerName: String,
        clubId: String,
        clubShortName: String,
        isScorer: Boolean
    ) {
        val list = if (isScorer) league.topScorers else league.topAssists
        val existing = list.find { it.playerName == playerName }

        if (existing != null) {
            existing.count++
        } else {
            list.add(
                PlayerStatLeader(
                    playerId = UUID.randomUUID().toString(),
                    playerName = playerName,
                    clubId = clubId,
                    clubShortName = clubShortName,
                    count = 1,
                    matchesPlayed = 1,
                    isUser = false
                )
            )
        }

        // Luôn sắp xếp lại theo số lượng giảm dần
        list.sortByDescending { it.count }
    }
}
