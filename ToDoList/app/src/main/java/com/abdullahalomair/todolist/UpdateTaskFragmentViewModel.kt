package com.abdullahalomair.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import java.util.*

class UpdateTaskFragmentViewModel: ViewModel(){
    private val tasksRepository = TasksRepository.get()
    fun getTask(id: UUID): LiveData<TasksDB?> = tasksRepository.getTasks(id)
    fun updateTask(task: TasksDB){
        tasksRepository.updateTask(task)
    }
    fun deleteTask(task: TasksDB){
        tasksRepository.deleteTask(task)
    }

}