package com.example.roadtolegend.model

data class AITransferRecord(
    val id: String,
    val season: Int,
    val week: Int,
    val playerName: String,
    val positionCode: String,
    val ovr: Int,
    val fromClubId: String,
    val fromClubName: String,
    val toClubId: String,
    val toClubName: String,
    val fee: Long,
    val isLoan: Boolean = false,
    val isFreeAgentSigning: Boolean = false
)
