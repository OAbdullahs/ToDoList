package com.abdullahalomair.todolist

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class TasksDB(@PrimaryKey val id: UUID = UUID.randomUUID(),
                   var title:String = "",
                   var description:String = "",
                   var date: Date = Date(),
                   var isDone: Boolean = false)