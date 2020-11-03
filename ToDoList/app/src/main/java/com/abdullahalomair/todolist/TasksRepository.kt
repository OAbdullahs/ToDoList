package com.abdullahalomair.todolist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.abdullahalomair.todolist.database.TasksDao
import com.abdullahalomair.todolist.database.ToDoListDataBase
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "tasks-database"
class TasksRepository private constructor(context: Context){
    private val database : ToDoListDataBase = Room.databaseBuilder(
        context.applicationContext,
        ToDoListDataBase::class.java,
        DATABASE_NAME
    ).build()
    private val tasksDao = database.tasksDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun updateTask(task: TasksDB) {
        executor.execute {
            tasksDao.updateTask(task)
        }
    }
    fun addTask(task: TasksDB) {
        executor.execute {
            tasksDao.addTask(task)
        }
    }
    fun deleteTask(task: TasksDB ) {
        executor.execute {
            tasksDao.deleteTask(task)
        }
    }
    fun getTasks(): LiveData<List<TasksDB>> = tasksDao.getTasks()
    fun getTasks(id: UUID): LiveData<TasksDB?> = tasksDao.getTasks(id)
    fun getTaskDate(date: Date): LiveData<TasksDB?> = tasksDao.getDate(date)


    companion object {
        private var INSTANCE: TasksRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TasksRepository(context)
            }
        }
        fun get(): TasksRepository {
            return INSTANCE ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}