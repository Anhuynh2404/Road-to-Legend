package com.example.roadtolegend.engine

import com.example.roadtolegend.model.Player
import kotlin.random.Random

object BalanceConfig {

    // Hệ số tăng trưởng theo độ tuổi
    fun getAgeGrowthMultiplier(age: Int): Double {
        return when {
            age <= 19 -> 1.45   // Tuổi trẻ bùng nổ
            age <= 22 -> 1.25   // Phát triển nhanh
            age <= 25 -> 1.05   // Hoàn thiện
            age <= 28 -> 0.90   // Đỉnh cao (Peak)
            age <= 30 -> 0.45   // Bắt đầu chững lại
            age <= 32 -> -0.35  // Suy giảm nhẹ (Tốc độ & Thể lực giảm)
            else -> -0.75       // Suy giảm rõ rệt
        }
    }

    // Khoảng cách tiềm năng ẩn: càng gần trần potential, tăng càng chậm
    fun getPotentialGapFactor(currentStat: Double, potential: Double): Double {
        val gap = (potential - currentStat) / potential
        return gap.coerceIn(0.12, 1.5)
    }

    // Tính điểm đánh giá cơ hội đá chính (0 - 100)
    fun calculateSelectionScore(player: Player): Double {
        return (player.ovr * 0.40) +
                (player.form * 0.25) +
                (player.energy * 0.20) +
                (player.managerTrust * 0.15)
    }

    // Tính xác suất chấn thương trong trận (%)
    fun calculateInjuryProbability(fatigue: Double, stamina: Double): Double {
        // Càng mệt mỏi, nguy cơ tăng theo hàm mũ; Stamina cao giúp bảo vệ cơ bắp
        val fatigueTerm = Math.pow(fatigue / 100.0, 2.0) * 14.0
        val staminaDefense = stamina / 20.0
        val baseRate = 1.2
        return (baseRate + fatigueTerm - staminaDefense).coerceIn(0.5, 30.0)
    }

    // Loại chấn thương ngẫu nhiên
    fun rollInjury(random: Random): Pair<String, Int> {
        val roll = random.nextInt(100)
        return when {
            roll < 55 -> "Căng cơ đùi sau (Gân kheo)" to random.nextInt(1, 3) // 1-2 tuần
            roll < 85 -> "Bong gân mắt cá chân" to random.nextInt(3, 6)       // 3-5 tuần
            else -> "Tổn thương dây chằng đầu gối" to random.nextInt(8, 16)   // 8-15 tuần
        }
    }
}
