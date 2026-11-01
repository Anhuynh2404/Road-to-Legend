package com.example.roadtolegend.engine

import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.Contract
import com.example.roadtolegend.model.GoalRecord
import com.example.roadtolegend.model.InteractiveChoice
import com.example.roadtolegend.model.InteractiveChoiceType
import com.example.roadtolegend.model.InteractiveMoment
import com.example.roadtolegend.model.LifestyleItem
import com.example.roadtolegend.model.Match
import com.example.roadtolegend.model.MatchEvent
import com.example.roadtolegend.model.MatchEventType
import com.example.roadtolegend.model.MatchPerformance
import com.example.roadtolegend.model.MatchUserRole
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.PositionCategory
import com.example.roadtolegend.model.TeamMatchStats
import java.util.UUID
import kotlin.random.Random

object MatchSimulator {

    fun generateMatchTimeline(
        matchId: String,
        roundNumber: Int,
        player: Player,
        contract: Contract,
        homeClub: Club,
        awayClub: Club,
        isUserHomeTeam: Boolean,
        purchasedItems: List<LifestyleItem> = emptyList(),
        seed: Long = System.currentTimeMillis(),
        isInteractiveMomentsEnabled: Boolean = true
    ): Match {
        val random = Random(seed)
        val userClub = if (isUserHomeTeam) homeClub else awayClub
        val opponentClub = if (isUserHomeTeam) awayClub else homeClub

        // 1. Xác định vai trò của người chơi
        val userRole = when {
            player.isInjured -> MatchUserRole.BENCHED
            else -> {
                val score = BalanceConfig.calculateSelectionScore(player)
                when {
                    score >= 64.0 -> MatchUserRole.STARTER
                    score >= 48.0 -> MatchUserRole.SUBSTITUTE
                    else -> MatchUserRole.BENCHED
                }
            }
        }

        val homeStats = TeamMatchStats()
        val awayStats = TeamMatchStats()
        val events = mutableListOf<MatchEvent>()
        val goalsList = mutableListOf<GoalRecord>()
        val interactiveMoments = mutableListOf<InteractiveMoment>()

        var homeScore = 0
        var awayScore = 0

        // Thống kê cá nhân của người chơi
        var userGoals = 0
        var userAssists = 0
        var userShots = 0
        var userShotsOnTarget = 0
        var userPasses = 0
        var userTackles = 0
        var userDribbles = 0
        var userFouls = 0
        var userYellowCard = false
        var userRedCard = false

        var lastInteractiveMinute = -20
        var interactiveCount = 0
        val maxInteractive = if (random.nextInt(100) < 30) 0 else when (player.primaryPosition.category) {
            PositionCategory.FORWARD -> random.nextInt(1, 4)
            PositionCategory.MIDFIELDER -> random.nextInt(1, 3)
            PositionCategory.DEFENDER, PositionCategory.GOALKEEPER -> random.nextInt(0, 2)
        }

        // Tỉ lệ kiểm soát bóng dựa trên sức mạnh đội hình và sân nhà (+4% cho đội nhà)
        val strengthDiff = homeClub.squadOvrAverage - awayClub.squadOvrAverage
        val homePossession = (50 + strengthDiff * 1.2 + 4).toInt().coerceIn(35, 68)
        val awayPossession = 100 - homePossession
        homeStats.possessionPercent = homePossession
        awayStats.possessionPercent = awayPossession

        // Khai cuộc
        events.add(
            MatchEvent(
                id = UUID.randomUUID().toString(),
                minute = 1,
                type = MatchEventType.KICKOFF,
                clubId = homeClub.id,
                clubShortName = homeClub.shortName,
                description = "Trọng tài nổi hồi còi bắt đầu trận đấu giữa ${homeClub.shortName} và ${awayClub.shortName} trên sân ${homeClub.shortName}!",
                isUserInvolved = false
            )
        )

        // Phút thi đấu từ 2 đến 93 (45+2, 90+3)
        var minute = 2
        while (minute <= 93) {
            // Hiệp 1 nghỉ giữa hiệp ở phút 45
            if (minute == 46) {
                events.add(
                    MatchEvent(
                        id = UUID.randomUUID().toString(),
                        minute = 45,
                        type = MatchEventType.HALF_TIME,
                        clubId = homeClub.id,
                        clubShortName = homeClub.shortName,
                        description = "HẾT HIỆP 1! Tỉ số tạm thời là ${homeClub.shortName} $homeScore - $awayScore ${awayClub.shortName}.",
                        isUserInvolved = false
                    )
                )
            }

            // Kiểm tra người chơi có đang thi đấu trên sân ở phút này không
            val isUserOnPitch = when (userRole) {
                MatchUserRole.STARTER -> minute <= 90 && !userRedCard
                MatchUserRole.SUBSTITUTE -> minute >= 60 && !userRedCard
                MatchUserRole.BENCHED -> false
            }

            // Người chơi vào sân thay người ở phút 60
            if (userRole == MatchUserRole.SUBSTITUTE && minute == 60) {
                events.add(
                    MatchEvent(
                        id = UUID.randomUUID().toString(),
                        minute = 60,
                        type = MatchEventType.SUBSTITUTION,
                        clubId = userClub.id,
                        clubShortName = userClub.shortName,
                        playerName = player.name,
                        description = "THAY NGƯỜI: ${player.name} được HLV tung vào sân để tạo đột biến!",
                        isUserInvolved = true
                    )
                )
            }

            // Xác định đội đang tấn công trong pha bóng này
            val isHomeAttacking = random.nextInt(100) < homePossession
            val attackingClub = if (isHomeAttacking) homeClub else awayClub
            val defendingClub = if (isHomeAttacking) awayClub else homeClub
            val attackingStats = if (isHomeAttacking) homeStats else awayStats
            val defendingStats = if (isHomeAttacking) awayStats else homeStats
            val isUserTeamAttacking = (isHomeAttacking == isUserHomeTeam)

            val eventRoll = random.nextInt(100)

            // Pha bóng: Phạm lỗi & Thẻ phạt (8%)
            if (eventRoll < 8) {
                defendingStats.fouls++
                val isCard = random.nextInt(100) < 20
                if (isCard) {
                    defendingStats.yellowCards++
                    events.add(
                        MatchEvent(
                            id = UUID.randomUUID().toString(),
                            minute = minute,
                            type = MatchEventType.YELLOW_CARD,
                            clubId = defendingClub.id,
                            clubShortName = defendingClub.shortName,
                            description = "Thẻ vàng! Cầu thủ bên phía ${defendingClub.shortName} phải nhận thẻ cảnh cáo sau pha phạm lỗi nguy hiểm.",
                            isUserInvolved = false
                        )
                    )
                }
                minute += random.nextInt(1, 3)
                continue
            }

            // Pha bóng: Phạt góc (7%)
            if (eventRoll < 15) {
                attackingStats.corners++
                events.add(
                    MatchEvent(
                        id = UUID.randomUUID().toString(),
                        minute = minute,
                        type = MatchEventType.CORNER_KICK,
                        clubId = attackingClub.id,
                        clubShortName = attackingClub.shortName,
                        description = "${attackingClub.shortName} được hưởng một quả đá phạt góc nguy hiểm bên cánh.",
                        isUserInvolved = false
                    )
                )
                minute += random.nextInt(1, 3)
                continue
            }

            // Pha bóng: CƠ HỘI SÚT BÓNG (53% cơ hội, từ 15 đến 68)
            if (eventRoll < 68) {
                attackingStats.shotsTotal++

                // Trường hợp người chơi tham gia vào tình huống
                val isUserInvolved = isUserOnPitch && isUserTeamAttacking && (random.nextInt(100) < 45)

                // Kiểm tra xem có kích hoạt Khoảnh khắc quyết định tương tác không
                val canTriggerInteractive = isInteractiveMomentsEnabled &&
                        isUserInvolved &&
                        (interactiveCount < maxInteractive) &&
                        (minute - lastInteractiveMinute >= 15) &&
                        (minute in 15..88) &&
                        (player.primaryPosition.category == PositionCategory.FORWARD || player.primaryPosition.category == PositionCategory.MIDFIELDER || random.nextInt(100) < 40)

                if (canTriggerInteractive) {
                    lastInteractiveMinute = minute
                    interactiveCount++

                    val situation = when (player.primaryPosition.category) {
                        PositionCategory.FORWARD -> "Phút $minute: Bạn khéo léo phá bẫy việt vị, một mình băng xuống đối mặt trực tiếp với thủ môn đối phương!"
                        PositionCategory.MIDFIELDER -> "Phút $minute: Bạn nhận bóng ở cự ly khoảng 22m chếch góc thuận lợi, hàng thủ đối phương chưa kịp áp sát!"
                        PositionCategory.DEFENDER, PositionCategory.GOALKEEPER -> "Phút $minute: Quả phạt góc treo vào vòng cấm, bóng bật đến đúng tầm chân của bạn ở vị trí cận thành!"
                    }

                    val gkOvr = (opponentClub.squadOvrAverage * 0.9).coerceIn(50.0, 85.0)
                    val finesseChance = ((player.sho * 0.55 + player.dri * 0.35 + player.form * 0.10) - (gkOvr * 0.25)).toInt().coerceIn(30, 90)
                    val powerChance = ((player.sho * 0.70 + player.sta * 0.20 + player.pac * 0.10) - (gkOvr * 0.25)).toInt().coerceIn(25, 88)
                    val passChance = ((player.pas * 0.65 + player.dri * 0.25 + player.teamChemistry * 0.10) - (gkOvr * 0.20)).toInt().coerceIn(35, 92)
                    val dribbleChance = ((player.dri * 0.65 + player.pac * 0.25) - (gkOvr * 0.25)).toInt().coerceIn(25, 85)

                    val choices = listOf(
                        InteractiveChoice(InteractiveChoiceType.FINESSE_SHOT, "Cứa lòng vào góc xa (Finesse)", "Uốn cong quả bóng đưa bóng về góc xa khung thành.", finesseChance),
                        InteractiveChoice(InteractiveChoiceType.POWER_SHOT, "Sút mu chính diện gầm xà (Power)", "Tung cú nã đại bác xé gió với lực bóng căng tối đa.", powerChance),
                        InteractiveChoice(InteractiveChoiceType.PASS_TO_TEAMMATE, "Chuyền dọn cỗ cho đồng đội (Pass)", "Hút hậu vệ rồi chuyền ngang cho tiền đạo đệm bóng lưới trống.", passChance),
                        InteractiveChoice(InteractiveChoiceType.DRIBBLE_PAST_GK, "Lừa bóng qua thủ môn (Skill)", "Đảo chân giả sút ngoặt qua thủ môn để ghi bàn vào lưới trống.", dribbleChance)
                    )

                    val moment = InteractiveMoment(
                        id = UUID.randomUUID().toString(),
                        minute = minute,
                        situationText = situation,
                        contextDescription = "${attackingClub.shortName} đang dồn ép thế trận.",
                        choices = choices
                    )
                    interactiveMoments.add(moment)

                    // Tạo event tương tác trên timeline
                    events.add(
                        MatchEvent(
                            id = UUID.randomUUID().toString(),
                            minute = minute,
                            type = MatchEventType.INTERACTIVE_DECISION,
                            clubId = userClub.id,
                            clubShortName = userClub.shortName,
                            playerName = player.name,
                            description = "KHOẢNH KHẮC QUYẾT ĐỊNH: $situation",
                            isUserInvolved = true
                        )
                    )
                    minute += random.nextInt(2, 4)
                    continue
                }

                // Cú sút thông thường
                val shotAccuracyRoll = random.nextInt(100)
                val isShotOnTarget = shotAccuracyRoll < 50

                if (!isShotOnTarget) {
                    // Sút chệch cột hoặc bị chặn
                    if (isUserInvolved) userShots++
                    val isBlocked = random.nextBoolean()
                    val shooterName = if (isUserInvolved) player.name else pickShooterName(attackingClub, random)
                    events.add(
                        MatchEvent(
                            id = UUID.randomUUID().toString(),
                            minute = minute,
                            type = if (isBlocked) MatchEventType.SHOT_BLOCKED else MatchEventType.SHOT_OFF_TARGET,
                            clubId = attackingClub.id,
                            clubShortName = attackingClub.shortName,
                            playerName = shooterName,
                            description = if (isBlocked) {
                                "Cú dứt điểm của $shooterName đã bị hậu vệ ${defendingClub.shortName} dũng cảm lăn xả cản phá."
                            } else {
                                "$shooterName tung cú sút từ cự ly thuận lợi nhưng bóng đi chệch cột dọc trong gang tấc."
                            },
                            isUserInvolved = isUserInvolved
                        )
                    )
                } else {
                    // Sút trúng đích!
                    attackingStats.shotsOnTarget++
                    if (isUserInvolved) {
                        userShots++
                        userShotsOnTarget++
                    }

                    // Tỉ lệ thành bàn thắng (Goals distribution ~ 2.5 - 3.2 bàn/trận)
                    val isGoal = random.nextInt(100) < 26

                    if (isGoal) {
                        // GHI BÀN THẮNG CHÍNH THỨC!
                        if (isHomeAttacking) homeScore++ else awayScore++

                        val scorerName: String
                        val assistName: String?
                        val isUserScorer: Boolean
                        val isUserAssist: Boolean

                        if (isUserInvolved) {
                            if (player.primaryPosition.category == PositionCategory.FORWARD || random.nextInt(100) < 65) {
                                scorerName = player.name
                                assistName = pickAssisterName(attackingClub, random)
                                userGoals++
                                isUserScorer = true
                                isUserAssist = false
                            } else {
                                scorerName = pickShooterName(attackingClub, random)
                                assistName = player.name
                                userAssists++
                                isUserScorer = false
                                isUserAssist = true
                            }
                        } else {
                            scorerName = pickShooterName(attackingClub, random)
                            assistName = if (random.nextInt(100) < 70) pickAssisterName(attackingClub, random) else null
                            isUserScorer = false
                            isUserAssist = false
                        }

                        val goalRecord = GoalRecord(
                            minute = minute,
                            scorerName = scorerName,
                            assistName = assistName,
                            clubId = attackingClub.id,
                            clubShortName = attackingClub.shortName,
                            isPenalty = false,
                            isUserScorer = isUserScorer,
                            isUserAssist = isUserAssist
                        )
                        goalsList.add(goalRecord)

                        val desc = if (assistName != null) {
                            "VÀO OOO! $scorerName đệm bóng chính xác sau đường kiến tạo dọn cỗ của $assistName! (${homeClub.shortName} $homeScore - $awayScore ${awayClub.shortName})"
                        } else {
                            "SIÊU PHẨM VÀO OOO! $scorerName độc diễn rồi tung cú dứt điểm hiểm hóc tung lưới ${defendingClub.shortName}! (${homeClub.shortName} $homeScore - $awayScore ${awayClub.shortName})"
                        }

                        events.add(
                            MatchEvent(
                                id = UUID.randomUUID().toString(),
                                minute = minute,
                                type = MatchEventType.GOAL,
                                clubId = attackingClub.id,
                                clubShortName = attackingClub.shortName,
                                playerName = scorerName,
                                secondaryPlayerName = assistName,
                                description = desc,
                                isUserInvolved = isUserInvolved,
                                isGoalScored = true
                            )
                        )
                    } else {
                        // Thủ môn cản phá xuất sắc
                        val shooterName = if (isUserInvolved) player.name else pickShooterName(attackingClub, random)
                        events.add(
                            MatchEvent(
                                id = UUID.randomUUID().toString(),
                                minute = minute,
                                type = MatchEventType.SHOT_SAVED,
                                clubId = defendingClub.id,
                                clubShortName = defendingClub.shortName,
                                playerName = shooterName,
                                description = "KHÔNG VÀO! $shooterName dứt điểm hiểm hóc nhưng thủ môn ${defendingClub.shortName} đã đổ người cứu thua ngoạn mục!",
                                isUserInvolved = isUserInvolved
                            )
                        )
                    }
                }
                minute += random.nextInt(1, 3)
                continue
            }

            // Trường hợp người chơi phòng ngự / phân phối bóng
            if (isUserOnPitch && !isUserTeamAttacking && random.nextInt(100) < 30) {
                userTackles++
                events.add(
                    MatchEvent(
                        id = UUID.randomUUID().toString(),
                        minute = minute,
                        type = MatchEventType.POSSESSION_PHASE,
                        clubId = userClub.id,
                        clubShortName = userClub.shortName,
                        playerName = player.name,
                        description = "${player.name} tranh chấp quyết liệt và thu hồi bóng thành công cho ${userClub.shortName}.",
                        isUserInvolved = true
                    )
                )
            } else if (isUserOnPitch && isUserTeamAttacking && random.nextInt(100) < 35) {
                userPasses += random.nextInt(2, 5)
            }

            minute += random.nextInt(1, 3)
        }

        // Hồi còi kết thúc trận đấu
        events.add(
            MatchEvent(
                id = UUID.randomUUID().toString(),
                minute = 93,
                type = MatchEventType.FULL_TIME,
                clubId = homeClub.id,
                clubShortName = homeClub.shortName,
                description = "HẾT GIỜ! Trọng tài nổi hồi còi mãn cuộc: ${homeClub.shortName} $homeScore - $awayScore ${awayClub.shortName}.",
                isUserInvolved = false
            )
        )

        // Sắp xếp timeline theo thứ tự thời gian
        val sortedTimeline = events.sortedBy { it.minute }

        // BẢO ĐẢM TÍNH BẤT BIẾN CỦA TỈ SỐ (Assertion check)
        val verifiedHomeGoals = goalsList.count { it.clubId == homeClub.id }
        val verifiedAwayGoals = goalsList.count { it.clubId == awayClub.id }
        check(homeScore == verifiedHomeGoals) { "Score invariant failed: homeScore=$homeScore, verified=$verifiedHomeGoals" }
        check(awayScore == verifiedAwayGoals) { "Score invariant failed: awayScore=$awayScore, verified=$verifiedAwayGoals" }

        // Tính điểm đánh giá (Rating) & Thống kê cá nhân
        val minutesPlayed = when (userRole) {
            MatchUserRole.STARTER -> 90
            MatchUserRole.SUBSTITUTE -> 33
            MatchUserRole.BENCHED -> 0
        }

        val userTeamWon = if (isUserHomeTeam) homeScore > awayScore else awayScore > homeScore
        val userTeamLost = if (isUserHomeTeam) homeScore < awayScore else awayScore < homeScore

        val baseRating = if (userRole == MatchUserRole.STARTER) 6.4 else if (userRole == MatchUserRole.SUBSTITUTE) 6.0 else 5.0
        val ratingCalc = baseRating +
                (userGoals * 1.3) +
                (userAssists * 0.9) +
                (userTackles * 0.25) +
                (userPasses * 0.02) +
                (if (userTeamWon) 0.5 else if (userTeamLost) -0.5 else 0.0)

        val finalRating = String.format("%.1f", ratingCalc.coerceIn(4.0, 10.0)).toDouble()
        val isMOM = finalRating >= 8.6 && userRole != MatchUserRole.BENCHED

        val appearanceBonus = if (userRole != MatchUserRole.BENCHED) contract.bonusPerAppearance else 0L
        val isOpponentCleanSheet = if (isUserHomeTeam) awayScore == 0 else homeScore == 0
        val isDefensivePosition = player.primaryPosition.category == PositionCategory.DEFENDER || player.primaryPosition.category == PositionCategory.GOALKEEPER
        val cleanSheetBonus = if (userRole != MatchUserRole.BENCHED && isDefensivePosition && isOpponentCleanSheet) contract.bonusPerCleanSheet else 0L
        val bonusEarned = appearanceBonus + (userGoals * contract.bonusPerGoal) + (userAssists * contract.bonusPerAssist) + cleanSheetBonus
        val wageEarned = 0L // Lương tuần được quyết toán định kỳ hàng tuần qua FinanceEngine

        val fatigueReduction = purchasedItems.filter { it.isPurchased }.sumOf { it.fatigueReductionBonus }
        val energyConsumed = if (userRole == MatchUserRole.STARTER) 28 else if (userRole == MatchUserRole.SUBSTITUTE) 15 else 5
        val fatigueAdded = if (userRole != MatchUserRole.BENCHED) {
            val baseFatigue = (32.0 - (player.sta * 0.20)).coerceAtLeast(6.0)
            baseFatigue * (1.0 - fatigueReduction.coerceAtMost(0.5))
        } else -10.0

        val performance = MatchPerformance(
            userRole = userRole,
            minutesPlayed = minutesPlayed,
            goals = userGoals,
            assists = userAssists,
            shotsTotal = userShots,
            shotsOnTarget = userShotsOnTarget,
            passesCompleted = userPasses,
            tacklesWon = userTackles,
            dribblesCompleted = userDribbles,
            rating = finalRating,
            isManOfTheMatch = isMOM,
            wageEarned = wageEarned,
            bonusEarned = bonusEarned,
            energyConsumed = energyConsumed,
            fatigueAdded = fatigueAdded,
            injurySustainedWeeks = 0,
            injuryName = null
        )

        val match = Match(
            id = matchId,
            roundNumber = roundNumber,
            homeClub = homeClub,
            awayClub = awayClub,
            homeScore = homeScore,
            awayScore = awayScore,
            goalsList = goalsList,
            homeStats = homeStats,
            awayStats = awayStats,
            timelineEvents = sortedTimeline,
            performance = performance,
            interactiveMoments = interactiveMoments,
            isCompleted = false,
            seed = seed
        )

        match.verifyScoreInvariant()
        return match
    }

    private fun pickShooterName(club: Club, random: Random): String {
        return club.keyPlayers.firstOrNull() ?: "${club.shortName} ST"
    }

    private fun pickAssisterName(club: Club, random: Random): String {
        return club.keyPlayers.getOrNull(1) ?: "${club.shortName} MF"
    }

    fun applyInteractiveChoice(
        match: Match,
        moment: InteractiveMoment,
        choiceType: InteractiveChoiceType,
        player: Player,
        contract: Contract,
        isUserHomeTeam: Boolean,
        random: Random = Random(System.currentTimeMillis())
    ): Pair<Boolean, String> {
        val choice = moment.choices.first { it.type == choiceType }
        val roll = random.nextInt(100)
        val success = roll < choice.hiddenSuccessChancePercent

        moment.resolved = true
        moment.selectedChoice = choiceType
        moment.isGoal = success

        val resultMsg: String
        if (success) {
            val userClub = if (isUserHomeTeam) match.homeClub else match.awayClub
            val oppClub = if (isUserHomeTeam) match.awayClub else match.homeClub

            if (isUserHomeTeam) match.homeScore++ else match.awayScore++

            val isUserGoal = choiceType != InteractiveChoiceType.PASS_TO_TEAMMATE
            val goalRecord = GoalRecord(
                minute = moment.minute,
                scorerName = if (isUserGoal) player.name else (userClub.keyPlayers.firstOrNull() ?: "Đồng đội"),
                assistName = if (!isUserGoal) player.name else null,
                clubId = userClub.id,
                clubShortName = userClub.shortName,
                isPenalty = false,
                isUserScorer = isUserGoal,
                isUserAssist = !isUserGoal
            )
            match.goalsList.add(goalRecord)

            resultMsg = when (choiceType) {
                InteractiveChoiceType.FINESSE_SHOT -> "VÀO OOO! Bạn vẽ nên một đường cong tuyệt mỹ vào góc xa, bóng găm thẳng góc chữ A!"
                InteractiveChoiceType.POWER_SHOT -> "VÀO OOO! Cú nã đại bác sấm sét xé toang nóc lưới đối phương!"
                InteractiveChoiceType.PASS_TO_TEAMMATE -> "KIẾN TẠO HOÀN HẢO! Bạn thu hút thủ môn rồi căng ngang dọn cỗ để đồng đội đệm bóng vào lưới trống!"
                InteractiveChoiceType.DRIBBLE_PAST_GK -> "SIÊU PHẨM! Bạn làm động tác giả lừa qua thủ môn rồi nhẹ nhàng đưa bóng vào lưới trống!"
                InteractiveChoiceType.DIRECT_FREEKICK -> "VÀO OOO! Cú đá phạt hiểm hóc vượt qua hàng rào găm thẳng vào góc chết!"
                InteractiveChoiceType.THROUGH_BALL -> "KIẾN TẠO MẮT THẦN! Đường chọc khe xé toang hàng thủ để đồng đội ghi bàn!"
            }
            moment.resultText = resultMsg
        } else {
            resultMsg = when (choiceType) {
                InteractiveChoiceType.FINESSE_SHOT -> "RẤT TIẾC! Cú cứa lòng của bạn đi chệch mép ngoài cột dọc trong gang tấc!"
                InteractiveChoiceType.POWER_SHOT -> "KHÔNG VÀO! Cú sút sấm sét dội xà ngang bật ra ngoài đầy tiếc nuối!"
                InteractiveChoiceType.PASS_TO_TEAMMATE -> "BỊ CHẶN! Hậu vệ đối phương đã kịp thời lùi về cắt đường chuyền của bạn!"
                InteractiveChoiceType.DRIBBLE_PAST_GK -> "BỊ BẮT BÀI! Thủ môn đối phương đã đổ người ôm gọn bóng trong chân bạn!"
                InteractiveChoiceType.DIRECT_FREEKICK -> "BÓNG CHỆCH CỘT! Cú sút phạt bay sạt xà ngang ra ngoài!"
                InteractiveChoiceType.THROUGH_BALL -> "HƠI MẠNH! Đường chọc khe đi hơi sâu và thủ môn đối phương đã kịp lao ra ôm bóng!"
            }
            moment.resultText = resultMsg
        }

        match.verifyScoreInvariant()
        return success to resultMsg
    }
}
