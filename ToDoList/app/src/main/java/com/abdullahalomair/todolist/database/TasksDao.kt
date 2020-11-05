package com.abdullahalomair.todolist.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.abdullahalomair.todolist.TasksDB
import java.util.*

@Dao
interface TasksDao{
    @Query("SELECT * FROM TasksDB")
    fun getTasks(): LiveData<List<TasksDB>>

    @Query("SELECT * FROM TasksDB")
    fun getTasksBackground(): List<TasksDB>

    @Query("SELECT * FROM TasksDB WHERE id=(:id)")
    fun getTasks(id: UUID): LiveData<TasksDB?>

    @Update
    fun updateTask(task: TasksDB)
    @Insert
    fun addTask(task: TasksDB)
    @Delete
    fun deleteTask(task: TasksDB)
}