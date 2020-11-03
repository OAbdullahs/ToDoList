package com.abdullahalomair.todolist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.abdullahalomair.todolist.TasksDB

@Database(entities = [ TasksDB::class ], version=1)
@TypeConverters(ToDoTypeConverter::class)
abstract class ToDoListDataBase: RoomDatabase(){
    abstract fun tasksDao(): TasksDao
}