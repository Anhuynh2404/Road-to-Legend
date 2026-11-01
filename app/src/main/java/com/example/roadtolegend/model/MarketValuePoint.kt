package com.example.roadtolegend.model

data class MarketValuePoint(
    val season: Int,
    val week: Int,
    val value: Long,
    val labelText: String = "Mùa $season - T$week"
)
