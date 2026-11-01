package com.example.roadtolegend.model

data class Club(
    val id: String,
    val name: String,
    val shortName: String,
    val country: String,
    val leagueId: String,
    val tier: Int,                     // 1 = Hạng Nhất/V-League 1, 2 = Hạng Nhì/V-League 2...
    val reputation: Int,               // 1 - 100
    var budget: Long,
    val trainingFacilityLevel: Int,    // 1 - 5 (ảnh hưởng hiệu suất tập luyện)
    val coachQuality: Int,             // 1 - 5
    val squadOvrAverage: Int,          // Sức mạnh tương đối của đồng đội
    val primaryColorHex: String = "#FFC837",
    val secondaryColorHex: String = "#0A0F1D",
    val keyPlayers: List<String> = emptyList(),
    val fictionalName: String = name,
    val fictionalShortName: String = shortName
) {
    fun getDisplayName(isFictional: Boolean = false): String =
        if (isFictional) fictionalName else name

    fun getDisplayShortName(isFictional: Boolean = false): String =
        if (isFictional) fictionalShortName else shortName
}
