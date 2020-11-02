package com.abdullahalomair.todolist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.time.LocalDate
import java.time.Month

class ToDoListMainViewModel(private val app:Application): AndroidViewModel(app){
    var currentMonthSelected: LocalDate = LocalDate.now()
    var getSelectedMonth = currentMonthSelected.month
    var getSelectedYear = currentMonthSelected.year

    fun incrementMonth() {
        currentMonthSelected = currentMonthSelected.plusMonths(1)
        getSelectedMonth = currentMonthSelected.month
        getSelectedYear = currentMonthSelected.year
    }
    fun decrementMonth(){
        currentMonthSelected = currentMonthSelected.minusMonths(1)
        getSelectedMonth = currentMonthSelected.month
        getSelectedYear = currentMonthSelected.year
    }



}