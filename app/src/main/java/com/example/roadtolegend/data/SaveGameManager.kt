package com.example.roadtolegend.data

import android.content.Context
import com.example.roadtolegend.model.CareerMilestone
import com.example.roadtolegend.model.CareerState
import com.example.roadtolegend.model.CareerStats
import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.Contract
import com.example.roadtolegend.model.Foot
import com.example.roadtolegend.model.League
import com.example.roadtolegend.model.NewsItem
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.Position
import com.example.roadtolegend.model.SeasonRecord
import com.example.roadtolegend.model.TeamStanding
import com.example.roadtolegend.model.TrainingFocus
import com.example.roadtolegend.model.TrainingIntensity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

data class SaveSlotSummary(
    val slotIndex: Int,
    val title: String,
    val playerName: String,
    val ovr: Int,
    val position: String,
    val clubName: String,
    val season: Int,
    val week: Int,
    val dateModifiedMillis: Long
)

object SaveGameManager {

    private const val PREF_NAME = "road_to_legend_saves"

    fun saveGame(context: Context, slot: Int, state: CareerState): Boolean {
        return try {
            val json = JSONObject()
            json.put("seasonNumber", state.seasonNumber)
            json.put("currentYear", state.currentYear)
            json.put("currentWeek", state.currentWeek)
            json.put("totalWeeksInSeason", state.totalWeeksInSeason)
            json.put("selectedTrainingFocus", state.selectedTrainingFocus.name)
            json.put("selectedTrainingIntensity", state.selectedTrainingIntensity.name)
            json.put("isSeasonEnded", state.isSeasonEnded)
            json.put("isFictionalMode", state.isFictionalMode)
            json.put("isInteractiveMomentsEnabled", state.isInteractiveMomentsEnabled)

            // Purchased items
            val itemsArray = JSONArray()
            state.purchasedItemIds.forEach { itemsArray.put(it) }
            json.put("purchasedItemIds", itemsArray)

            // Player
            val p = state.player
            val playerJson = JSONObject()
            playerJson.put("id", p.id)
            playerJson.put("name", p.name)
            playerJson.put("age", p.age)
            playerJson.put("birthYear", p.birthYear)
            playerJson.put("nationality", p.nationality)
            playerJson.put("heightCm", p.heightCm)
            playerJson.put("preferredFoot", p.preferredFoot.name)
            playerJson.put("primaryPosition", p.primaryPosition.name)
            playerJson.put("pac", p.pac)
            playerJson.put("sho", p.sho)
            playerJson.put("pas", p.pas)
            playerJson.put("dri", p.dri)
            playerJson.put("sta", p.sta)
            playerJson.put("def", p.def)
            playerJson.put("hiddenPotential", p.hiddenPotential)
            playerJson.put("energy", p.energy)
            playerJson.put("fatigue", p.fatigue)
            playerJson.put("morale", p.morale)
            playerJson.put("form", p.form)
            playerJson.put("domesticReputation", p.domesticReputation)
            playerJson.put("globalReputation", p.globalReputation)
            playerJson.put("managerTrust", p.managerTrust)
            playerJson.put("teamChemistry", p.teamChemistry)
            playerJson.put("balance", p.balance)
            playerJson.put("injuryWeeksRemaining", p.injuryWeeksRemaining)
            playerJson.put("injuryName", p.injuryName ?: "")
            playerJson.put("peakMarketValue", p.peakMarketValue)

            // Career stats
            val cs = p.careerStats
            val csJson = JSONObject()
            csJson.put("matchesPlayed", cs.matchesPlayed)
            csJson.put("starts", cs.starts)
            csJson.put("subs", cs.subs)
            csJson.put("goals", cs.goals)
            csJson.put("assists", cs.assists)
            csJson.put("tackles", cs.tackles)
            csJson.put("cleanSheets", cs.cleanSheets)
            csJson.put("totalRatingPoints", cs.totalRatingPoints)
            csJson.put("manOfTheMatchCount", cs.manOfTheMatchCount)
            csJson.put("totwCount", cs.totwCount)
            csJson.put("yellowCards", cs.yellowCards)
            csJson.put("redCards", cs.redCards)
            val trophiesArr = JSONArray()
            cs.trophies.forEach { trophiesArr.put(it) }
            csJson.put("trophies", trophiesArr)
            playerJson.put("careerStats", csJson)

            // Season History Records (Section F)
            val seasonRecordsArray = JSONArray()
            p.seasonRecords.forEach { sr ->
                val srObj = JSONObject()
                srObj.put("seasonNumber", sr.seasonNumber)
                srObj.put("year", sr.year)
                srObj.put("clubId", sr.clubId)
                srObj.put("clubName", sr.clubName)
                srObj.put("clubShortName", sr.clubShortName)
                srObj.put("leagueId", sr.leagueId)
                srObj.put("leagueName", sr.leagueName)
                srObj.put("matchesPlayed", sr.matchesPlayed)
                srObj.put("starts", sr.starts)
                srObj.put("subs", sr.subs)
                srObj.put("minutesPlayed", sr.minutesPlayed)
                srObj.put("goals", sr.goals)
                srObj.put("assists", sr.assists)
                srObj.put("averageRating", sr.averageRating)
                srObj.put("yellowCards", sr.yellowCards)
                srObj.put("redCards", sr.redCards)
                srObj.put("trophiesWon", sr.trophiesWon)
                srObj.put("note", sr.note)
                seasonRecordsArray.put(srObj)
            }
            playerJson.put("seasonRecords", seasonRecordsArray)

            // Milestones (Section F)
            val milestonesArray = JSONArray()
            p.milestones.forEach { m ->
                val mObj = JSONObject()
                mObj.put("id", m.id)
                mObj.put("titleVi", m.titleVi)
                mObj.put("descriptionVi", m.descriptionVi)
                mObj.put("dateAchievedText", m.dateAchievedText)
                mObj.put("iconEmoji", m.iconEmoji)
                milestonesArray.put(mObj)
            }
            playerJson.put("milestones", milestonesArray)

            json.put("player", playerJson)

            // Current Club & League
            json.put("currentClubId", state.currentClub.id)
            json.put("currentLeagueId", state.currentLeague.id)

            // Contract
            val c = state.currentContract
            val cJson = JSONObject()
            cJson.put("clubId", c.clubId)
            cJson.put("clubName", c.clubName)
            cJson.put("weeklyWage", c.weeklyWage)
            cJson.put("signingBonus", c.signingBonus)
            cJson.put("bonusPerAppearance", c.bonusPerAppearance)
            cJson.put("bonusPerGoal", c.bonusPerGoal)
            cJson.put("bonusPerAssist", c.bonusPerAssist)
            cJson.put("bonusPerCleanSheet", c.bonusPerCleanSheet)
            cJson.put("bonusTrophy", c.bonusTrophy)
            cJson.put("contractYearsRemaining", c.contractYearsRemaining)
            cJson.put("totalWeeksRemaining", c.totalWeeksRemaining)
            if (c.releaseClause != null) cJson.put("releaseClause", c.releaseClause)
            cJson.put("squadRole", c.squadRole.name)
            cJson.put("annualWageIncreasePercent", c.annualWageIncreasePercent)
            cJson.put("wageIncreaseAfterMatches", c.wageIncreaseAfterMatches)
            cJson.put("clubSatisfaction", c.clubSatisfaction)
            cJson.put("isPreContract", c.isPreContract)
            if (c.preContractClubId != null) cJson.put("preContractClubId", c.preContractClubId)
            cJson.put("startSeason", c.startSeason)
            cJson.put("endSeason", c.endSeason)
            json.put("contract", cJson)

            // Agent
            val ag = state.agent
            val agJson = JSONObject()
            agJson.put("id", ag.id)
            agJson.put("name", ag.name)
            agJson.put("tier", ag.tier.name)
            agJson.put("totalOffersBrought", ag.totalOffersBrought)
            agJson.put("extraWagesSecured", ag.extraWagesSecured)
            agJson.put("playerTrust", ag.playerTrust)
            json.put("agent", agJson)

            // Active Loan
            if (state.activeLoan != null) {
                val ln = state.activeLoan!!
                val lnJson = JSONObject()
                lnJson.put("parentClubId", ln.parentClubId)
                lnJson.put("parentClubName", ln.parentClubName)
                lnJson.put("parentClubShortName", ln.parentClubShortName)
                lnJson.put("loanClubId", ln.loanClubId)
                lnJson.put("loanClubName", ln.loanClubName)
                lnJson.put("loanClubShortName", ln.loanClubShortName)
                lnJson.put("wageSharePercent", ln.wageSharePercent)
                if (ln.buyOptionFee != null) lnJson.put("buyOptionFee", ln.buyOptionFee)
                lnJson.put("canRecallInWinter", ln.canRecallInWinter)
                lnJson.put("weeksRemaining", ln.weeksRemaining)
                lnJson.put("totalMatchesOnLoan", ln.totalMatchesOnLoan)
                lnJson.put("goalsOnLoan", ln.goalsOnLoan)
                lnJson.put("assistsOnLoan", ln.assistsOnLoan)
                json.put("activeLoan", lnJson)
            }

            // Transfer flags
            json.put("isTransferWindowOpen", state.isTransferWindowOpen)
            json.put("isDeadlineDay", state.isDeadlineDay)
            json.put("isTransferListed", state.isTransferListed)
            json.put("transferRequestSubmitted", state.transferRequestSubmitted)

            // Incoming Offers
            val offersArray = JSONArray()
            state.incomingOffers.forEach { o ->
                val oJson = JSONObject()
                oJson.put("id", o.id)
                oJson.put("buyingClubId", o.buyingClubId)
                oJson.put("buyingClubName", o.buyingClubName)
                oJson.put("buyingClubShortName", o.buyingClubShortName)
                oJson.put("sellingClubId", o.sellingClubId)
                oJson.put("sellingClubName", o.sellingClubName)
                oJson.put("transferFee", o.transferFee)
                oJson.put("offeredWage", o.offeredWage)
                oJson.put("offeredSigningBonus", o.offeredSigningBonus)
                oJson.put("offeredContractYears", o.offeredContractYears)
                oJson.put("offeredRole", o.offeredRole.name)
                oJson.put("offeredBonusPerGoal", o.offeredBonusPerGoal)
                oJson.put("offeredBonusPerAssist", o.offeredBonusPerAssist)
                if (o.offeredReleaseClause != null) oJson.put("offeredReleaseClause", o.offeredReleaseClause)
                oJson.put("status", o.status.name)
                oJson.put("isLoan", o.isLoan)
                oJson.put("loanWageSharePercent", o.loanWageSharePercent)
                if (o.loanBuyOptionFee != null) oJson.put("loanBuyOptionFee", o.loanBuyOptionFee)
                oJson.put("canRecall", o.canRecall)
                oJson.put("loanDurationWeeks", o.loanDurationWeeks)
                oJson.put("createdWeek", o.createdWeek)
                oJson.put("expiresInWeeks", o.expiresInWeeks)
                oJson.put("isDeadlineDeal", o.isDeadlineDeal)
                oJson.put("scoutNote", o.scoutNote)
                offersArray.put(oJson)
            }
            json.put("incomingOffers", offersArray)

            // Market Value History
            val marketArray = JSONArray()
            state.marketHistory.forEach { m ->
                val mJson = JSONObject()
                mJson.put("season", m.season)
                mJson.put("week", m.week)
                mJson.put("value", m.value)
                marketArray.put(mJson)
            }
            json.put("marketHistory", marketArray)

            // Finance Transactions
            val finArray = JSONArray()
            state.financeTransactions.take(50).forEach { f ->
                val fJson = JSONObject()
                fJson.put("id", f.id)
                fJson.put("week", f.week)
                fJson.put("season", f.season)
                fJson.put("type", f.type.name)
                fJson.put("amount", f.amount)
                fJson.put("balanceAfter", f.balanceAfter)
                fJson.put("descriptionVi", f.descriptionVi)
                fJson.put("timestamp", f.timestamp)
                finArray.put(fJson)
            }
            json.put("financeTransactions", finArray)

            // AI Completed Transfers
            val aiTransArray = JSONArray()
            state.aiCompletedTransfers.take(30).forEach { ai ->
                val aiJson = JSONObject()
                aiJson.put("id", ai.id)
                aiJson.put("season", ai.season)
                aiJson.put("week", ai.week)
                aiJson.put("playerName", ai.playerName)
                aiJson.put("positionCode", ai.positionCode)
                aiJson.put("ovr", ai.ovr)
                aiJson.put("fromClubId", ai.fromClubId)
                aiJson.put("fromClubName", ai.fromClubName)
                aiJson.put("toClubId", ai.toClubId)
                aiJson.put("toClubName", ai.toClubName)
                aiJson.put("fee", ai.fee)
                aiJson.put("isLoan", ai.isLoan)
                aiJson.put("isFreeAgentSigning", ai.isFreeAgentSigning)
                aiTransArray.put(aiJson)
            }
            json.put("aiCompletedTransfers", aiTransArray)

            // Next Opponent
            json.put("nextOpponentId", state.nextOpponent.id)

            // Standings
            val standingsArray = JSONArray()
            state.currentLeague.standings.forEach { s ->
                val sObj = JSONObject()
                sObj.put("clubId", s.clubId)
                sObj.put("clubName", s.clubName)
                sObj.put("played", s.played)
                sObj.put("won", s.won)
                sObj.put("drawn", s.drawn)
                sObj.put("lost", s.lost)
                sObj.put("goalsFor", s.goalsFor)
                sObj.put("goalsAgainst", s.goalsAgainst)
                sObj.put("points", s.points)
                val formArr = JSONArray()
                s.recentForm.forEach { formArr.put(it) }
                sObj.put("recentForm", formArr)
                standingsArray.put(sObj)
            }
            json.put("standings", standingsArray)

            // News feed
            val newsArray = JSONArray()
            state.newsFeed.take(20).forEach { n ->
                val nObj = JSONObject()
                nObj.put("id", n.id)
                nObj.put("week", n.week)
                nObj.put("headline", n.headline)
                nObj.put("body", n.body)
                nObj.put("category", n.category)
                nObj.put("timestamp", n.timestamp)
                newsArray.put(nObj)
            }
            json.put("newsFeed", newsArray)

            val file = File(context.filesDir, "save_slot_$slot.json")
            file.writeText(json.toString())

            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putString("slot_${slot}_summary", "${p.name}|${p.ovr}|${p.primaryPosition.code}|${state.currentClub.shortName}|Mùa ${state.seasonNumber} - Tuần ${state.currentWeek}")
                .putLong("slot_${slot}_time", System.currentTimeMillis())
                .apply()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadGame(context: Context, slot: Int): CareerState? {
        return try {
            val file = File(context.filesDir, "save_slot_$slot.json")
            if (!file.exists()) return null
            val content = file.readText()
            val json = JSONObject(content)

            val pObj = json.getJSONObject("player")
            val csObj = pObj.getJSONObject("careerStats")
            val trophies = mutableListOf<String>()
            val trArray = csObj.optJSONArray("trophies")
            if (trArray != null) {
                for (i in 0 until trArray.length()) trophies.add(trArray.getString(i))
            }

            val careerStats = CareerStats(
                matchesPlayed = csObj.optInt("matchesPlayed", 0),
                starts = csObj.optInt("starts", 0),
                subs = csObj.optInt("subs", 0),
                goals = csObj.optInt("goals", 0),
                assists = csObj.optInt("assists", 0),
                tackles = csObj.optInt("tackles", 0),
                cleanSheets = csObj.optInt("cleanSheets", 0),
                totalRatingPoints = csObj.optDouble("totalRatingPoints", 0.0),
                manOfTheMatchCount = csObj.optInt("manOfTheMatchCount", 0),
                totwCount = csObj.optInt("totwCount", 0),
                yellowCards = csObj.optInt("yellowCards", 0),
                redCards = csObj.optInt("redCards", 0),
                trophies = trophies
            )

            val player = Player(
                id = pObj.getString("id"),
                name = pObj.getString("name"),
                age = pObj.getInt("age"),
                birthYear = pObj.optInt("birthYear", 2007),
                nationality = pObj.optString("nationality", "Việt Nam"),
                heightCm = pObj.optInt("heightCm", 178),
                preferredFoot = Foot.valueOf(pObj.optString("preferredFoot", "RIGHT")),
                primaryPosition = Position.valueOf(pObj.optString("primaryPosition", "ST")),
                pac = pObj.optDouble("pac", 50.0),
                sho = pObj.optDouble("sho", 50.0),
                pas = pObj.optDouble("pas", 50.0),
                dri = pObj.optDouble("dri", 50.0),
                sta = pObj.optDouble("sta", 50.0),
                def = pObj.optDouble("def", 50.0),
                hiddenPotential = pObj.optDouble("hiddenPotential", 84.0),
                energy = pObj.optInt("energy", 100),
                fatigue = pObj.optDouble("fatigue", 0.0),
                morale = pObj.optInt("morale", 80),
                form = pObj.optInt("form", 70),
                domesticReputation = pObj.optInt("domesticReputation", 15),
                globalReputation = pObj.optInt("globalReputation", 5),
                managerTrust = pObj.optInt("managerTrust", 60),
                teamChemistry = pObj.optInt("teamChemistry", 50),
                balance = pObj.optLong("balance", 1000L),
                injuryWeeksRemaining = pObj.optInt("injuryWeeksRemaining", 0),
                injuryName = pObj.optString("injuryName").ifEmpty { null },
                careerStats = careerStats,
                peakMarketValue = pObj.optLong("peakMarketValue", 0L)
            )

            // Safe migration: Load Season Records
            val srArray = pObj.optJSONArray("seasonRecords")
            if (srArray != null) {
                for (i in 0 until srArray.length()) {
                    val srObj = srArray.getJSONObject(i)
                    player.seasonRecords.add(
                        SeasonRecord(
                            seasonNumber = srObj.optInt("seasonNumber", 1),
                            year = srObj.optInt("year", 2026),
                            clubId = srObj.optString("clubId", ""),
                            clubName = srObj.optString("clubName", ""),
                            clubShortName = srObj.optString("clubShortName", ""),
                            leagueId = srObj.optString("leagueId", ""),
                            leagueName = srObj.optString("leagueName", ""),
                            matchesPlayed = srObj.optInt("matchesPlayed", 0),
                            starts = srObj.optInt("starts", 0),
                            subs = srObj.optInt("subs", 0),
                            minutesPlayed = srObj.optInt("minutesPlayed", 0),
                            goals = srObj.optInt("goals", 0),
                            assists = srObj.optInt("assists", 0),
                            averageRating = srObj.optDouble("averageRating", 0.0),
                            yellowCards = srObj.optInt("yellowCards", 0),
                            redCards = srObj.optInt("redCards", 0),
                            trophiesWon = srObj.optString("trophiesWon", ""),
                            note = srObj.optString("note", "")
                        )
                    )
                }
            }

            // Safe migration: Load Milestones
            val mArray = pObj.optJSONArray("milestones")
            if (mArray != null) {
                for (i in 0 until mArray.length()) {
                    val mObj = mArray.getJSONObject(i)
                    player.milestones.add(
                        CareerMilestone(
                            id = mObj.optString("id", ""),
                            titleVi = mObj.optString("titleVi", ""),
                            descriptionVi = mObj.optString("descriptionVi", ""),
                            dateAchievedText = mObj.optString("dateAchievedText", ""),
                            iconEmoji = mObj.optString("iconEmoji", "⭐")
                        )
                    )
                }
            }

            val leagueId = json.optString("currentLeagueId", "vleague_2")
            val currentLeague = DefaultDatabase.initializeLeague(leagueId)

            val clubId = json.optString("currentClubId", "")
            val currentClub = currentLeague.clubs.find { it.id == clubId } ?: currentLeague.clubs.first()

            val oppId = json.optString("nextOpponentId", "")
            val nextOpponent = currentLeague.clubs.find { it.id == oppId }
                ?: currentLeague.clubs.first { it.id != currentClub.id }

            // Standings
            val standingsArray = json.optJSONArray("standings")
            if (standingsArray != null) {
                val loadedStandings = mutableListOf<TeamStanding>()
                for (i in 0 until standingsArray.length()) {
                    val sObj = standingsArray.getJSONObject(i)
                    val recentFormList = mutableListOf<String>()
                    val formArr = sObj.optJSONArray("recentForm")
                    if (formArr != null) {
                        for (f in 0 until formArr.length()) recentFormList.add(formArr.getString(f))
                    }
                    loadedStandings.add(
                        TeamStanding(
                            clubId = sObj.optString("clubId", ""),
                            clubName = sObj.optString("clubName", ""),
                            played = sObj.optInt("played", 0),
                            won = sObj.optInt("won", 0),
                            drawn = sObj.optInt("drawn", 0),
                            lost = sObj.optInt("lost", 0),
                            goalsFor = sObj.optInt("goalsFor", 0),
                            goalsAgainst = sObj.optInt("goalsAgainst", 0),
                            points = sObj.optInt("points", 0),
                            recentForm = recentFormList
                        )
                    )
                }
                if (loadedStandings.isNotEmpty()) {
                    currentLeague.standings = loadedStandings
                }
            }

            // Contract (Tương thích ngược an toàn)
            val cObj = json.getJSONObject("contract")
            val contractRole = try {
                com.example.roadtolegend.model.SquadRole.valueOf(cObj.optString("squadRole", "PROSPECT"))
            } catch (e: Exception) {
                com.example.roadtolegend.model.SquadRole.PROSPECT
            }
            val contractYears = cObj.optInt("contractYearsRemaining", 2)
            val contract = Contract(
                clubId = cObj.optString("clubId", currentClub.id),
                clubName = cObj.optString("clubName", currentClub.name),
                weeklyWage = cObj.optLong("weeklyWage", 350L),
                signingBonus = cObj.optLong("signingBonus", 0L),
                bonusPerAppearance = cObj.optLong("bonusPerAppearance", 50L),
                bonusPerGoal = cObj.optLong("bonusPerGoal", 80L),
                bonusPerAssist = cObj.optLong("bonusPerAssist", 40L),
                bonusPerCleanSheet = cObj.optLong("bonusPerCleanSheet", 80L),
                bonusTrophy = cObj.optLong("bonusTrophy", 5_000L),
                contractYearsRemaining = contractYears,
                totalWeeksRemaining = cObj.optInt("totalWeeksRemaining", contractYears * 26),
                releaseClause = if (cObj.has("releaseClause") && !cObj.isNull("releaseClause")) cObj.optLong("releaseClause") else null,
                squadRole = contractRole,
                annualWageIncreasePercent = cObj.optInt("annualWageIncreasePercent", 0),
                wageIncreaseAfterMatches = cObj.optInt("wageIncreaseAfterMatches", 0),
                clubSatisfaction = cObj.optInt("clubSatisfaction", 75),
                isPreContract = cObj.optBoolean("isPreContract", false),
                preContractClubId = cObj.optString("preContractClubId").ifEmpty { null },
                startSeason = cObj.optInt("startSeason", 1),
                endSeason = cObj.optInt("endSeason", 1 + contractYears)
            )

            // Agent (Tương thích ngược)
            val agObj = json.optJSONObject("agent")
            val agentTier = try {
                com.example.roadtolegend.model.AgentTier.valueOf(agObj?.optString("tier") ?: "NONE")
            } catch (e: Exception) {
                com.example.roadtolegend.model.AgentTier.NONE
            }
            val agent = com.example.roadtolegend.model.Agent(
                id = agObj?.optString("id") ?: "agent_default",
                name = agObj?.optString("name") ?: "Tự đại diện",
                tier = agentTier,
                totalOffersBrought = agObj?.optInt("totalOffersBrought", 0) ?: 0,
                extraWagesSecured = agObj?.optLong("extraWagesSecured", 0L) ?: 0L,
                playerTrust = agObj?.optInt("playerTrust", 80) ?: 80
            )

            // Active Loan
            val lnObj = json.optJSONObject("activeLoan")
            val activeLoan = if (lnObj != null) {
                com.example.roadtolegend.model.LoanRecord(
                    parentClubId = lnObj.optString("parentClubId", ""),
                    parentClubName = lnObj.optString("parentClubName", ""),
                    parentClubShortName = lnObj.optString("parentClubShortName", ""),
                    loanClubId = lnObj.optString("loanClubId", ""),
                    loanClubName = lnObj.optString("loanClubName", ""),
                    loanClubShortName = lnObj.optString("loanClubShortName", ""),
                    wageSharePercent = lnObj.optInt("wageSharePercent", 100),
                    buyOptionFee = if (lnObj.has("buyOptionFee") && !lnObj.isNull("buyOptionFee")) lnObj.optLong("buyOptionFee") else null,
                    canRecallInWinter = lnObj.optBoolean("canRecallInWinter", false),
                    weeksRemaining = lnObj.optInt("weeksRemaining", 13),
                    totalMatchesOnLoan = lnObj.optInt("totalMatchesOnLoan", 0),
                    goalsOnLoan = lnObj.optInt("goalsOnLoan", 0),
                    assistsOnLoan = lnObj.optInt("assistsOnLoan", 0)
                )
            } else null

            // Incoming Offers
            val incomingOffers = mutableListOf<com.example.roadtolegend.model.TransferOffer>()
            val offersArr = json.optJSONArray("incomingOffers")
            if (offersArr != null) {
                for (i in 0 until offersArr.length()) {
                    val oObj = offersArr.getJSONObject(i)
                    val oRole = try {
                        com.example.roadtolegend.model.SquadRole.valueOf(oObj.optString("offeredRole", "KEY_PLAYER"))
                    } catch (e: Exception) {
                        com.example.roadtolegend.model.SquadRole.KEY_PLAYER
                    }
                    val oStatus = try {
                        com.example.roadtolegend.model.TransferOfferStatus.valueOf(oObj.optString("status", "APPROVED_AWAITING_PLAYER"))
                    } catch (e: Exception) {
                        com.example.roadtolegend.model.TransferOfferStatus.APPROVED_AWAITING_PLAYER
                    }
                    incomingOffers.add(
                        com.example.roadtolegend.model.TransferOffer(
                            id = oObj.optString("id", java.util.UUID.randomUUID().toString()),
                            buyingClubId = oObj.optString("buyingClubId", ""),
                            buyingClubName = oObj.optString("buyingClubName", ""),
                            buyingClubShortName = oObj.optString("buyingClubShortName", ""),
                            sellingClubId = oObj.optString("sellingClubId", ""),
                            sellingClubName = oObj.optString("sellingClubName", ""),
                            transferFee = oObj.optLong("transferFee", 0L),
                            offeredWage = oObj.optLong("offeredWage", 500L),
                            offeredSigningBonus = oObj.optLong("offeredSigningBonus", 2000L),
                            offeredContractYears = oObj.optInt("offeredContractYears", 3),
                            offeredRole = oRole,
                            offeredBonusPerGoal = oObj.optLong("offeredBonusPerGoal", 100L),
                            offeredBonusPerAssist = oObj.optLong("offeredBonusPerAssist", 50L),
                            offeredReleaseClause = if (oObj.has("offeredReleaseClause") && !oObj.isNull("offeredReleaseClause")) oObj.optLong("offeredReleaseClause") else null,
                            status = oStatus,
                            isLoan = oObj.optBoolean("isLoan", false),
                            loanWageSharePercent = oObj.optInt("loanWageSharePercent", 100),
                            loanBuyOptionFee = if (oObj.has("loanBuyOptionFee") && !oObj.isNull("loanBuyOptionFee")) oObj.optLong("loanBuyOptionFee") else null,
                            canRecall = oObj.optBoolean("canRecall", false),
                            loanDurationWeeks = oObj.optInt("loanDurationWeeks", 13),
                            createdWeek = oObj.optInt("createdWeek", 1),
                            expiresInWeeks = oObj.optInt("expiresInWeeks", 2),
                            isDeadlineDeal = oObj.optBoolean("isDeadlineDeal", false),
                            scoutNote = oObj.optString("scoutNote", "")
                        )
                    )
                }
            }

            // Market Value History
            val marketHistory = mutableListOf<com.example.roadtolegend.model.MarketValuePoint>()
            val marketArr = json.optJSONArray("marketHistory")
            if (marketArr != null) {
                for (i in 0 until marketArr.length()) {
                    val mObj = marketArr.getJSONObject(i)
                    marketHistory.add(
                        com.example.roadtolegend.model.MarketValuePoint(
                            season = mObj.optInt("season", 1),
                            week = mObj.optInt("week", 1),
                            value = mObj.optLong("value", 50_000L)
                        )
                    )
                }
            }

            // Finance Transactions
            val financeTransactions = mutableListOf<com.example.roadtolegend.model.FinanceTransaction>()
            val finArr = json.optJSONArray("financeTransactions")
            if (finArr != null) {
                for (i in 0 until finArr.length()) {
                    val fObj = finArr.getJSONObject(i)
                    val tType = try {
                        com.example.roadtolegend.model.TransactionType.valueOf(fObj.optString("type", "WAGE"))
                    } catch (e: Exception) {
                        com.example.roadtolegend.model.TransactionType.WAGE
                    }
                    financeTransactions.add(
                        com.example.roadtolegend.model.FinanceTransaction(
                            id = fObj.optString("id", java.util.UUID.randomUUID().toString()),
                            week = fObj.optInt("week", 1),
                            season = fObj.optInt("season", 1),
                            type = tType,
                            amount = fObj.optLong("amount", 0L),
                            balanceAfter = fObj.optLong("balanceAfter", 0L),
                            descriptionVi = fObj.optString("descriptionVi", ""),
                            timestamp = fObj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            // AI Completed Transfers
            val aiCompletedTransfers = mutableListOf<com.example.roadtolegend.model.AITransferRecord>()
            val aiArr = json.optJSONArray("aiCompletedTransfers")
            if (aiArr != null) {
                for (i in 0 until aiArr.length()) {
                    val aiObj = aiArr.getJSONObject(i)
                    aiCompletedTransfers.add(
                        com.example.roadtolegend.model.AITransferRecord(
                            id = aiObj.optString("id", java.util.UUID.randomUUID().toString()),
                            season = aiObj.optInt("season", 1),
                            week = aiObj.optInt("week", 1),
                            playerName = aiObj.optString("playerName", ""),
                            positionCode = aiObj.optString("positionCode", "ST"),
                            ovr = aiObj.optInt("ovr", 60),
                            fromClubId = aiObj.optString("fromClubId", ""),
                            fromClubName = aiObj.optString("fromClubName", ""),
                            toClubId = aiObj.optString("toClubId", ""),
                            toClubName = aiObj.optString("toClubName", ""),
                            fee = aiObj.optLong("fee", 0L),
                            isLoan = aiObj.optBoolean("isLoan", false),
                            isFreeAgentSigning = aiObj.optBoolean("isFreeAgentSigning", false)
                        )
                    )
                }
            }

            // Purchased items
            val purchasedItemIds = mutableSetOf<String>()
            val itemsArr = json.optJSONArray("purchasedItemIds")
            if (itemsArr != null) {
                for (i in 0 until itemsArr.length()) purchasedItemIds.add(itemsArr.getString(i))
            }

            // News
            val newsFeed = mutableListOf<NewsItem>()
            val newsArr = json.optJSONArray("newsFeed")
            if (newsArr != null) {
                for (i in 0 until newsArr.length()) {
                    val nObj = newsArr.getJSONObject(i)
                    newsFeed.add(
                        NewsItem(
                            id = nObj.optString("id", ""),
                            week = nObj.optInt("week", 1),
                            headline = nObj.optString("headline", ""),
                            body = nObj.optString("body", ""),
                            category = nObj.optString("category", "MATCH"),
                            sourceName = nObj.optString("sourceName", "Bản Tin Thể Thao"),
                            reliabilityTier = nObj.optInt("reliabilityTier", 1),
                            isRead = nObj.optBoolean("isRead", false),
                            linkedClubId = nObj.optString("linkedClubId").ifEmpty { null },
                            isRumorTrue = nObj.optBoolean("isRumorTrue", false),
                            rumorTargetClubId = nObj.optString("rumorTargetClubId").ifEmpty { null },
                            timestamp = nObj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
            }

            val trainingFocusName = json.optString("selectedTrainingFocus", "SHOOTING")
            val trainingFocus = try {
                TrainingFocus.valueOf(trainingFocusName)
            } catch (e: Exception) {
                TrainingFocus.SHOOTING
            }

            val intensityName = json.optString("selectedTrainingIntensity", "MEDIUM")
            val trainingIntensity = try {
                TrainingIntensity.valueOf(intensityName)
            } catch (e: Exception) {
                TrainingIntensity.MEDIUM
            }

            CareerState(
                seasonNumber = json.optInt("seasonNumber", 1),
                currentYear = json.optInt("currentYear", 2026),
                currentWeek = json.optInt("currentWeek", 1),
                totalWeeksInSeason = json.optInt("totalWeeksInSeason", 26),
                player = player,
                currentClub = currentClub,
                currentLeague = currentLeague,
                currentContract = contract,
                nextOpponent = nextOpponent,
                selectedTrainingFocus = trainingFocus,
                selectedTrainingIntensity = trainingIntensity,
                purchasedItemIds = purchasedItemIds,
                newsFeed = newsFeed,
                isSeasonEnded = json.optBoolean("isSeasonEnded", false),
                isFictionalMode = json.optBoolean("isFictionalMode", false),
                isInteractiveMomentsEnabled = json.optBoolean("isInteractiveMomentsEnabled", true),

                agent = agent,
                activeLoan = activeLoan,
                incomingOffers = incomingOffers,
                marketHistory = marketHistory,
                financeTransactions = financeTransactions,
                isTransferWindowOpen = json.optBoolean("isTransferWindowOpen", false),
                isDeadlineDay = json.optBoolean("isDeadlineDay", false),
                isTransferListed = json.optBoolean("isTransferListed", false),
                transferRequestSubmitted = json.optBoolean("transferRequestSubmitted", false),
                aiCompletedTransfers = aiCompletedTransfers
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getSlotSummaries(context: Context): List<SaveSlotSummary> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val list = mutableListOf<SaveSlotSummary>()

        for (slot in 0..3) {
            val summaryStr = prefs.getString("slot_${slot}_summary", null)
            val time = prefs.getLong("slot_${slot}_time", 0L)
            val slotTitle = if (slot == 0) "Tự Động Lưu (Auto-Save)" else "Vị Trí Lưu #$slot"

            if (summaryStr != null) {
                val parts = summaryStr.split("|")
                if (parts.size >= 5) {
                    list.add(
                        SaveSlotSummary(
                            slotIndex = slot,
                            title = slotTitle,
                            playerName = parts[0],
                            ovr = parts[1].toIntOrNull() ?: 50,
                            position = parts[2],
                            clubName = parts[3],
                            season = 1,
                            week = 1,
                            dateModifiedMillis = time
                        )
                    )
                    continue
                }
            }
            list.add(
                SaveSlotSummary(
                    slotIndex = slot,
                    title = slotTitle,
                    playerName = "Ô trống",
                    ovr = 0,
                    position = "",
                    clubName = "",
                    season = 0,
                    week = 0,
                    dateModifiedMillis = 0L
                )
            )
        }
        return list
    }
}
