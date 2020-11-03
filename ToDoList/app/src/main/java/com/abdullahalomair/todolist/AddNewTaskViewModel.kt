package com.abdullahalomair.todolist

import androidx.lifecycle.ViewModel

class AddNewTaskViewModel: ViewModel(){
    private val tasksRepository = TasksRepository.get()
    fun addTask(task: TasksDB) {
        tasksRepository.addTask(task)
    }
}