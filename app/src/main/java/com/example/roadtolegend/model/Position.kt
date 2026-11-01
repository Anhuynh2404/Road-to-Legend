package com.example.roadtolegend.model

enum class PositionCategory {
    GOALKEEPER,
    DEFENDER,
    MIDFIELDER,
    FORWARD
}

enum class Position(
    val code: String,
    val fullNameVi: String,
    val category: PositionCategory,
    // Trọng số 6 chỉ số: PAC, SHO, PAS, DRI, STA, DEF
    val pacWeight: Double,
    val shoWeight: Double,
    val pasWeight: Double,
    val driWeight: Double,
    val staWeight: Double,
    val defWeight: Double
) {
    GK(
        code = "GK",
        fullNameVi = "Thủ môn",
        category = PositionCategory.GOALKEEPER,
        pacWeight = 0.10,
        shoWeight = 0.00,
        pasWeight = 0.15,
        driWeight = 0.05,
        staWeight = 0.20,
        defWeight = 0.50
    ),
    ST(
        code = "ST",
        fullNameVi = "Tiền đạo cắm",
        category = PositionCategory.FORWARD,
        pacWeight = 0.25,
        shoWeight = 0.35,
        pasWeight = 0.10,
        driWeight = 0.20,
        staWeight = 0.05,
        defWeight = 0.05
    ),
    LW(
        code = "LW",
        fullNameVi = "Tiền đạo cánh trái",
        category = PositionCategory.FORWARD,
        pacWeight = 0.35,
        shoWeight = 0.20,
        pasWeight = 0.15,
        driWeight = 0.25,
        staWeight = 0.05,
        defWeight = 0.00
    ),
    RW(
        code = "RW",
        fullNameVi = "Tiền đạo cánh phải",
        category = PositionCategory.FORWARD,
        pacWeight = 0.35,
        shoWeight = 0.20,
        pasWeight = 0.15,
        driWeight = 0.25,
        staWeight = 0.05,
        defWeight = 0.00
    ),
    CAM(
        code = "CAM",
        fullNameVi = "Tiền vệ tấn công",
        category = PositionCategory.MIDFIELDER,
        pacWeight = 0.10,
        shoWeight = 0.20,
        pasWeight = 0.30,
        driWeight = 0.30,
        staWeight = 0.05,
        defWeight = 0.05
    ),
    CM(
        code = "CM",
        fullNameVi = "Tiền vệ trung tâm",
        category = PositionCategory.MIDFIELDER,
        pacWeight = 0.05,
        shoWeight = 0.10,
        pasWeight = 0.30,
        driWeight = 0.20,
        staWeight = 0.20,
        defWeight = 0.15
    ),
    CDM(
        code = "CDM",
        fullNameVi = "Tiền vệ phòng ngự",
        category = PositionCategory.MIDFIELDER,
        pacWeight = 0.05,
        shoWeight = 0.05,
        pasWeight = 0.20,
        driWeight = 0.10,
        staWeight = 0.25,
        defWeight = 0.35
    ),
    LB(
        code = "LB",
        fullNameVi = "Hậu vệ cánh trái",
        category = PositionCategory.DEFENDER,
        pacWeight = 0.25,
        shoWeight = 0.00,
        pasWeight = 0.15,
        driWeight = 0.10,
        staWeight = 0.20,
        defWeight = 0.30
    ),
    RB(
        code = "RB",
        fullNameVi = "Hậu vệ cánh phải",
        category = PositionCategory.DEFENDER,
        pacWeight = 0.25,
        shoWeight = 0.00,
        pasWeight = 0.15,
        driWeight = 0.10,
        staWeight = 0.20,
        defWeight = 0.30
    ),
    CB(
        code = "CB",
        fullNameVi = "Trung vệ",
        category = PositionCategory.DEFENDER,
        pacWeight = 0.15,
        shoWeight = 0.00,
        pasWeight = 0.10,
        driWeight = 0.05,
        staWeight = 0.25,
        defWeight = 0.45
    );

    fun calculateOvr(pac: Double, sho: Double, pas: Double, dri: Double, sta: Double, def: Double): Int {
        val total = pac * pacWeight +
                sho * shoWeight +
                pas * pasWeight +
                dri * driWeight +
                sta * staWeight +
                def * defWeight
        return total.toInt().coerceIn(1, 99)
    }
}

enum class Foot(val labelVi: String) {
    RIGHT("Chân phải"),
    LEFT("Chân trái"),
    BOTH("Hai chân như một")
}
