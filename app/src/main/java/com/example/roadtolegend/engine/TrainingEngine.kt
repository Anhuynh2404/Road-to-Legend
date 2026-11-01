package com.example.roadtolegend.engine

import com.example.roadtolegend.core.config.TrainingConfig
import com.example.roadtolegend.model.Club
import com.example.roadtolegend.model.LifestyleItem
import com.example.roadtolegend.model.Player
import com.example.roadtolegend.model.TrainingFocus
import com.example.roadtolegend.model.TrainingIntensity
import kotlin.random.Random

data class TrainingResult(
    val focus: TrainingFocus,
    val intensity: TrainingIntensity,
    val costPaid: Long,
    val statChangedName: String,
    val statDelta: Double,
    val energyDelta: Int,
    val fatigueDelta: Double,
    val formDelta: Int,
    val summaryMessage: String,
    val injuryName: String? = null,
    val injuryWeeks: Int = 0
)

object TrainingEngine {

    fun executeWeeklyTraining(
        player: Player,
        club: Club,
        focus: TrainingFocus,
        intensity: TrainingIntensity = TrainingIntensity.MEDIUM,
        purchasedItems: List<LifestyleItem> = emptyList(),
        random: Random = Random(System.currentTimeMillis())
    ): TrainingResult {
        val cost = focus.getCost(intensity)

        // Trừ chi phí tập luyện nếu đủ tiền
        val actualCost = if (player.balance >= cost) {
            player.balance -= cost
            cost
        } else {
            val paid = player.balance
            player.balance = 0
            paid
        }

        val itemMultiplierBonus = purchasedItems.filter { it.isPurchased }.sumOf { it.trainingMultiplierBonus }
        val fatigueReductionBonus = purchasedItems.filter { it.isPurchased }.sumOf { it.fatigueReductionBonus }

        // Trường hợp 1: Nghỉ ngơi & Hồi phục
        if (focus == TrainingFocus.REST_AND_RECOVERY) {
            val oldEnergy = player.energy
            val baseRecovered = (TrainingConfig.REST_ENERGY_RECOVERED_BASE + (player.sta * 0.15)) * intensity.energyCostMultiplier
            val energyRecovered = baseRecovered.toInt()
            player.energy = (player.energy + energyRecovered).coerceAtMost(100)
            val actualEnergyDelta = player.energy - oldEnergy

            val fatigueReduced = (TrainingConfig.REST_FATIGUE_REDUCED_BASE * intensity.fatigueMultiplier * (1.0 + fatigueReductionBonus))
            val oldFatigue = player.fatigue
            player.fatigue = (player.fatigue - fatigueReduced).coerceAtLeast(0.0)
            val actualFatigueDelta = player.fatigue - oldFatigue

            player.morale = (player.morale + TrainingConfig.MORALE_BOOST_ON_REST).coerceAtMost(100)

            return TrainingResult(
                focus = focus,
                intensity = intensity,
                costPaid = actualCost,
                statChangedName = "Hồi phục",
                statDelta = 0.0,
                energyDelta = actualEnergyDelta,
                fatigueDelta = actualFatigueDelta,
                formDelta = 0,
                summaryMessage = "Bạn đã có tuần hồi phục [Cường độ ${intensity.titleVi}]. Thể lực +$actualEnergyDelta, độ mỏi giảm ${String.format("%.1f", -actualFatigueDelta)}%, chi phí: €$actualCost."
            )
        }

        // Trường hợp 2: Tập luyện kỹ năng chuyên môn
        val ageMult = BalanceConfig.getAgeGrowthMultiplier(player.age)
        val facilityBonus = 1.0 + (club.trainingFacilityLevel * 0.06) + (club.coachQuality * 0.04)
        val totalEquipmentMultiplier = 1.0 + itemMultiplierBonus
        val moraleFactor = (player.morale / 100.0).coerceIn(0.7, 1.25)

        val (currentStatValue, statCode) = when (focus) {
            TrainingFocus.PACE -> player.pac to "PAC"
            TrainingFocus.SHOOTING -> player.sho to "SHO"
            TrainingFocus.PASSING -> player.pas to "PAS"
            TrainingFocus.DRIBBLING -> player.dri to "DRI"
            TrainingFocus.STAMINA -> player.sta to "STA"
            TrainingFocus.DEFENDING -> player.def to "DEF"
            TrainingFocus.REST_AND_RECOVERY -> 0.0 to ""
        }

        val potentialGap = BalanceConfig.getPotentialGapFactor(currentStatValue, player.hiddenPotential)
        val baseGrowth = TrainingConfig.BASE_STAT_GROWTH + (random.nextDouble() * 0.20) // 0.50 - 0.70

        val calculatedGain = if (ageMult >= 0) {
            baseGrowth * ageMult * potentialGap * facilityBonus * totalEquipmentMultiplier * moraleFactor * intensity.growthMultiplier
        } else {
            // Khi đã lớn tuổi, chỉ số bứt tốc hoặc thể lực có thể bị suy giảm
            ageMult * 0.3 * intensity.growthMultiplier
        }

        // Áp dụng tăng trưởng
        when (focus) {
            TrainingFocus.PACE -> player.pac = (player.pac + calculatedGain).coerceIn(1.0, 99.0)
            TrainingFocus.SHOOTING -> player.sho = (player.sho + calculatedGain).coerceIn(1.0, 99.0)
            TrainingFocus.PASSING -> player.pas = (player.pas + calculatedGain).coerceIn(1.0, 99.0)
            TrainingFocus.DRIBBLING -> player.dri = (player.dri + calculatedGain).coerceIn(1.0, 99.0)
            TrainingFocus.STAMINA -> player.sta = (player.sta + calculatedGain).coerceIn(1.0, 99.0)
            TrainingFocus.DEFENDING -> player.def = (player.def + calculatedGain).coerceIn(1.0, 99.0)
            TrainingFocus.REST_AND_RECOVERY -> Unit
        }

        // Tiêu hao thể lực & tích lũy mệt mỏi
        val energyCost = (focus.baseEnergyCost * intensity.energyCostMultiplier).toInt()
        player.energy = (player.energy - energyCost).coerceAtLeast(5)

        val fatigueAdded = (focus.baseFatigue * intensity.fatigueMultiplier * (1.0 - fatigueReductionBonus.coerceAtMost(0.60))).coerceAtLeast(1.5)
        player.fatigue = (player.fatigue + fatigueAdded).coerceAtMost(100.0)

        // Tập luyện tốt tăng Form nhẹ (giúp điểm số trận tuần đó nhích lên)
        val formDelta = if (calculatedGain > 0.40) TrainingConfig.FORM_BOOST_ON_GOOD_TRAINING else 2
        player.form = (player.form + formDelta).coerceAtMost(100)

        // Kiểm tra nguy cơ chấn thương trong buổi tập (nhất là khi mệt mỏi cao + tập nặng)
        var injuryName: String? = null
        var injuryWeeks = 0
        val trainingInjuryChance = intensity.injuryRiskPercent + (if (player.fatigue > TrainingConfig.INJURY_FATIGUE_THRESHOLD) 5.0 else 0.0)
        if (random.nextDouble() * 100.0 < trainingInjuryChance) {
            val (rolledName, rolledWeeks) = BalanceConfig.rollInjury(random)
            injuryName = rolledName
            injuryWeeks = (rolledWeeks / 2).coerceAtLeast(1) // Chấn thương tập luyện nhẹ hơn trận đấu
            player.injuryWeeksRemaining = injuryWeeks
            player.injuryName = rolledName
            player.morale = (player.morale - 10).coerceAtLeast(10)
        }

        val formattedGain = String.format("%+.2f", calculatedGain)
        val msg = if (injuryName != null) {
            "CẢNH BÁO: Bạn bị $injuryName trong buổi tập ${focus.titleVi} ($intensity) và phải nghỉ $injuryWeeks tuần! ($statCode $formattedGain, chi phí €$actualCost)."
        } else {
            "Buổi tập ${focus.titleVi} (${intensity.titleVi}) hoàn tất: $statCode $formattedGain • Chi phí €$actualCost • Thể lực -$energyCost • Phong độ +$formDelta."
        }

        return TrainingResult(
            focus = focus,
            intensity = intensity,
            costPaid = actualCost,
            statChangedName = statCode,
            statDelta = calculatedGain,
            energyDelta = -energyCost,
            fatigueDelta = fatigueAdded,
            formDelta = formDelta,
            summaryMessage = msg,
            injuryName = injuryName,
            injuryWeeks = injuryWeeks
        )
    }
}
