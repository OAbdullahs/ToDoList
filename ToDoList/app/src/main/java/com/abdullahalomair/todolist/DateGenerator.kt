package com.abdullahalomair.todolist

import java.time.Month
import java.util.*

data class DateGenerator(
    var id: UUID = UUID.randomUUID(),
    var year: Int = 2020,
    var month: Int = 1,
    var day: List<Int>
)



