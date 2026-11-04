package com.example.roadtolegend.engine

import com.example.roadtolegend.model.BallonDorCandidate
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.SeasonAwardWinner
import com.example.roadtolegend.model.SeasonAwardsCeremony
import com.example.roadtolegend.model.TrophyCategory
import com.example.roadtolegend.model.TrophyItem
import java.util.UUID
import kotlin.random.Random

object AwardsEngine {

    fun generateSeasonAwards(state: CareerState, random: Random): SeasonAwardsCeremony {
        val player = state.player
        val currentLeague = state.currentLeague
        val sortedStandings = currentLeague.sortedStandings()
        val championTeam = sortedStandings.firstOrNull()
        val championClub = currentLeague.clubs.find { it.id == championTeam?.clubId } ?: state.currentClub
        val isUserChampion = championClub.id == state.currentClub.id

        val userTrophiesEarned = mutableListOf<TrophyItem>()
        val awardWinners = mutableListOf<SeasonAwardWinner>()

        // 1. Cúp Vô Địch Giải Đấu
        val championAward = SeasonAwardWinner(
            awardKey = "CHAMPION",
            awardNameVi = "Cúp Vô Địch ${currentLeague.name}",
            winnerName = championClub.name,
            clubName = championClub.name,
            statsText = "${championTeam?.points ?: 0} điểm (${championTeam?.won ?: 0}T - ${championTeam?.drawn ?: 0}H - ${championTeam?.lost ?: 0}B)",
            isUser = isUserChampion,
            iconEmoji = "🏆",
            descriptionVi = if (isUserChampion) "Bạn cùng các đồng đội đã xuất sắc nâng cao chiếc cúp vô địch lịch sử!" else "${championClub.name} đã thống trị giải đấu để lên ngôi vô địch."
        )
        awardWinners.add(championAward)

        if (isUserChampion) {
            userTrophiesEarned.add(
                TrophyItem(
                    name = "Vô địch ${currentLeague.name}",
                    category = TrophyCategory.CLUB,
                    year = state.currentYear,
                    seasonNumber = state.seasonNumber,
                    competitionOrOrganization = currentLeague.name,
                    iconEmoji = "🏆",
                    description = "Đăng quang ngôi vương cùng ${state.currentClub.name} mùa giải ${state.seasonNumber} (${state.currentYear}).",
                    badgeColorHex = "#FFD700"
                )
            )
        }

        // 2. Chiếc Giày Vàng (Vua Phá Lưới)
        val topScorerAI = currentLeague.topScorers.firstOrNull()
        val userGoals = player.careerStats.goals
        val isUserTopScorer = userGoals > 0 && (topScorerAI == null || userGoals >= topScorerAI.count)

        val goldenBootWinnerName = if (isUserTopScorer) player.name else (topScorerAI?.playerName ?: "Nguyễn Tiến Linh")
        val goldenBootClubName = if (isUserTopScorer) state.currentClub.name else (topScorerAI?.clubShortName ?: "B.Bình Dương")
        val goldenBootCount = if (isUserTopScorer) userGoals else (topScorerAI?.count ?: 12)

        awardWinners.add(
            SeasonAwardWinner(
                awardKey = "GOLDEN_BOOT",
                awardNameVi = "Chiếc Giày Vàng (Vua Phá Lưới)",
                winnerName = goldenBootWinnerName,
                clubName = goldenBootClubName,
                statsText = "$goldenBootCount bàn thắng",
                isUser = isUserTopScorer,
                iconEmoji = "👟",
                descriptionVi = if (isUserTopScorer) "Màn trình diễn săn bàn hủy diệt giúp bạn đoạt danh hiệu Vua phá lưới!" else "$goldenBootWinnerName đã xuất sắc giành Chiếc giày vàng giải đấu."
            )
        )

        if (isUserTopScorer) {
            userTrophiesEarned.add(
                TrophyItem(
                    name = "Chiếc Giày Vàng (Vua Phá Lưới)",
                    category = TrophyCategory.INDIVIDUAL,
                    year = state.currentYear,
                    seasonNumber = state.seasonNumber,
                    competitionOrOrganization = currentLeague.name,
                    iconEmoji = "👟",
                    description = "Chân sút số một giải đấu với $userGoals bàn thắng.",
                    badgeColorHex = "#FFA000"
                )
            )
        }

        // 3. Vua Kiến Tạo (Playmaker of the Season)
        val topAssistAI = currentLeague.topAssists.firstOrNull()
        val userAssists = player.careerStats.assists
        val isUserTopAssist = userAssists > 0 && (topAssistAI == null || userAssists >= topAssistAI.count)

        val assistWinnerName = if (isUserTopAssist) player.name else (topAssistAI?.playerName ?: "Nguyễn Hoàng Đức")
        val assistClubName = if (isUserTopAssist) state.currentClub.name else (topAssistAI?.clubShortName ?: "Thể Công Viettel")
        val assistCount = if (isUserTopAssist) userAssists else (topAssistAI?.count ?: 9)

        awardWinners.add(
            SeasonAwardWinner(
                awardKey = "TOP_ASSIST",
                awardNameVi = "Vua Kiến Tạo Mùa Giải",
                winnerName = assistWinnerName,
                clubName = assistClubName,
                statsText = "$assistCount pha kiến tạo",
                isUser = isUserTopAssist,
                iconEmoji = "🎯",
                descriptionVi = if (isUserTopAssist) "Nhãn quan chiến thuật bậc thầy mang về danh hiệu Vua kiến tạo cho bạn!" else "$assistWinnerName là chân chuyền số 1 của giải đấu."
            )
        )

        if (isUserTopAssist) {
            userTrophiesEarned.add(
                TrophyItem(
                    name = "Vua Kiến Tạo",
                    category = TrophyCategory.INDIVIDUAL,
                    year = state.currentYear,
                    seasonNumber = state.seasonNumber,
                    competitionOrOrganization = currentLeague.name,
                    iconEmoji = "🎯",
                    description = "Chân chuyền hàng đầu mùa giải với $userAssists đường dọn cỗ.",
                    badgeColorHex = "#42A5F5"
                )
            )
        }

        // 4. Cầu Thủ Xuất Sắc Nhất Giải (Player of the Season - MVP)
        val matchesPlayed = player.careerStats.matchesPlayed
        val avgRating = player.careerStats.averageRating
        val isUserMvp = matchesPlayed >= 8 && (avgRating >= 7.35 || (isUserChampion && avgRating >= 7.15))

        val mvpWinnerName = if (isUserMvp) player.name else (if (isUserChampion) "Nguyễn Quang Hải" else championClub.keyPlayers.firstOrNull() ?: "Nguyễn Văn Quyết")
        val mvpClubName = if (isUserMvp) state.currentClub.name else championClub.name
        val mvpStats = if (isUserMvp) "Điểm TB: $avgRating (${userGoals}G / ${userAssists}A)" else "Điểm TB: 7.62 (14G / 8A)"

        awardWinners.add(
            SeasonAwardWinner(
                awardKey = "MVP",
                awardNameVi = "Cầu Thủ Xuất Sắc Nhất Giải (MVP)",
                winnerName = mvpWinnerName,
                clubName = mvpClubName,
                statsText = mvpStats,
                isUser = isUserMvp,
                iconEmoji = "⭐",
                descriptionVi = if (isUserMvp) "Sự công nhận tối cao từ hội đồng chuyên môn cho mùa giải thi đấu bùng nổ của bạn!" else "$mvpWinnerName được vinh danh là Cầu thủ hay nhất mùa giải."
            )
        )

        if (isUserMvp) {
            userTrophiesEarned.add(
                TrophyItem(
                    name = "Cầu Thủ Xuất Sắc Nhất Mùa (MVP)",
                    category = TrophyCategory.INDIVIDUAL,
                    year = state.currentYear,
                    seasonNumber = state.seasonNumber,
                    competitionOrOrganization = currentLeague.name,
                    iconEmoji = "⭐",
                    description = "Cầu thủ thi đấu xuất sắc nhất mùa giải ${state.seasonNumber}.",
                    badgeColorHex = "#FFD700"
                )
            )
        }

        // 5. Cầu Thủ Trẻ Xuất Sắc Nhất (Young Player of the Year)
        val isYoung = player.age <= 21
        val isUserYoungBest = isYoung && matchesPlayed >= 6 && avgRating >= 6.9

        val youngWinnerName = if (isUserYoungBest) player.name else "Khuất Văn Khang"
        val youngClubName = if (isUserYoungBest) state.currentClub.name else "Thể Công Viettel"
        awardWinners.add(
            SeasonAwardWinner(
                awardKey = "YOUNG_PLAYER",
                awardNameVi = "Cầu Thủ Trẻ Xuất Sắc Nhất",
                winnerName = youngWinnerName,
                clubName = youngClubName,
                statsText = "Tuổi: ${if (isUserYoungBest) player.age else 21}",
                isUser = isUserYoungBest,
                iconEmoji = "💫",
                descriptionVi = if (isUserYoungBest) "Tài năng trẻ sáng giá nhất của bóng đá quốc gia mùa giải năm nay!" else "$youngWinnerName đoạt giải Cầu thủ trẻ triển vọng nhất."
            )
        )

        if (isUserYoungBest) {
            userTrophiesEarned.add(
                TrophyItem(
                    name = "Cầu Thủ Trẻ Xuất Sắc Nhất",
                    category = TrophyCategory.INDIVIDUAL,
                    year = state.currentYear,
                    seasonNumber = state.seasonNumber,
                    competitionOrOrganization = currentLeague.name,
                    iconEmoji = "💫",
                    description = "Gương mặt trẻ triển vọng thi đấu bùng nổ ở tuổi ${player.age}.",
                    badgeColorHex = "#AB47BC"
                )
            )
        }

        // 6. Thủ Môn Xuất Sắc Nhất (Golden Glove)
        val gkName = if (championClub.country == "Việt Nam") "Đặng Văn Lâm" else "Thibaut Courtois"
        awardWinners.add(
            SeasonAwardWinner(
                awardKey = "GOLDEN_GLOVE",
                awardNameVi = "Găng Tay Vàng (Thủ Môn Hay Nhất)",
                winnerName = gkName,
                clubName = championClub.shortName,
                statsText = "12 trận giữ sạch lưới",
                isUser = false,
                iconEmoji = "🧤",
                descriptionVi = "$gkName là điểm tựa vững chắc nhất trong khung gỗ mùa giải này."
            )
        )

        // 7. SỰ KIỆN QUẢ BÓNG VÀNG THẾ GIỚI (BALLON D'OR GALA)
        // Tính toán ứng viên Quả Bóng Vàng theo đẳng cấp quốc tế
        val userScore = (player.ovr * 1.0) + (player.globalReputation * 0.4) + (if (player.ovr >= 85) userGoals * 0.8 else 0.0) + (if (isUserChampion) 3.0 else 0.0)
        val isUserBallonDorWinner = player.ovr >= 89 && userScore >= 98.0
        val isUserInBallonDorTop5 = !isUserBallonDorWinner && player.ovr >= 83

        val worldCandidates = listOf(
            BallonDorCandidate(1, "Vinicius Junior", "Real Madrid", "Brazil", 91, "34 bàn, 16 kiến tạo • Vô địch Cúp C1", false),
            BallonDorCandidate(2, "Erling Haaland", "Manchester City", "Na Uy", 91, "42 bàn thắng • Chiếc giày vàng Châu Âu", false),
            BallonDorCandidate(3, "Kylian Mbappé", "Real Madrid", "Pháp", 91, "37 bàn, 12 kiến tạo • Vô địch La Liga", false),
            BallonDorCandidate(4, "Jude Bellingham", "Real Madrid", "Anh", 89, "25 bàn, 15 kiến tạo", false),
            BallonDorCandidate(5, "Lamine Yamal", "FC Barcelona", "Tây Ban Nha", 86, "18 bàn, 21 kiến tạo • Vô địch Euro", false)
        )

        val finalCandidatesList: List<BallonDorCandidate>
        val finalWinner: BallonDorCandidate

        if (isUserBallonDorWinner) {
            val userCandidate = BallonDorCandidate(
                rank = 1,
                playerName = player.name,
                clubName = state.currentClub.name,
                nationality = player.nationality,
                ovr = player.ovr,
                statsText(userGoals, userAssists, avgRating),
                isUser = true
            )
            finalWinner = userCandidate
            finalCandidatesList = listOf(
                userCandidate,
                worldCandidates[0].copy(rank = 2),
                worldCandidates[1].copy(rank = 3),
                worldCandidates[2].copy(rank = 4),
                worldCandidates[3].copy(rank = 5)
            )

            userTrophiesEarned.add(
                TrophyItem(
                    name = "Quả Bóng Vàng Thế Giới (Ballon d'Or)",
                    category = TrophyCategory.INDIVIDUAL,
                    year = state.currentYear,
                    seasonNumber = state.seasonNumber,
                    competitionOrOrganization = "France Football / FIFA",
                    iconEmoji = "🟡",
                    description = "Cầu thủ xuất sắc nhất hành tinh năm ${state.currentYear}.",
                    badgeColorHex = "#FFD700"
                )
            )
        } else if (isUserInBallonDorTop5) {
            val userCandidate = BallonDorCandidate(
                rank = 3,
                playerName = player.name,
                clubName = state.currentClub.name,
                nationality = player.nationality,
                ovr = player.ovr,
                statsText(userGoals, userAssists, avgRating),
                isUser = true
            )
            finalWinner = worldCandidates[0]
            finalCandidatesList = listOf(
                worldCandidates[0],
                worldCandidates[1],
                userCandidate,
                worldCandidates[2].copy(rank = 4),
                worldCandidates[3].copy(rank = 5)
            )
        } else {
            finalWinner = worldCandidates[0]
            finalCandidatesList = worldCandidates
        }

        return SeasonAwardsCeremony(
            seasonNumber = state.seasonNumber,
            year = state.currentYear,
            leagueName = currentLeague.name,
            leagueChampionClubName = championClub.name,
            isUserChampion = isUserChampion,
            awards = awardWinners,
            ballonDorWinner = finalWinner,
            ballonDorTopCandidates = finalCandidatesList,
            userEarnedTrophies = userTrophiesEarned
        )
    }

    private fun statsText(goals: Int, assists: Int, avgRating: Double): String {
        return "$goals bàn, $assists kiến tạo • Điểm TB: $avgRating"
    }
}
