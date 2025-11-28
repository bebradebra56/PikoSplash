package com.pikosplash.hydroballs.data.model

import java.time.LocalDate

data class WaterEntry(
    val date: LocalDate,
    val glassesCount: Int = 0
)

