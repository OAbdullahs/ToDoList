package com.abdullahalomair.todolist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val DIALOG_Time = "DialogTime"
private const val REQUEST_TIME = 1
private const val TAG = "AddNewTaskFragment"
class AddNewTaskFragment: Fragment(), TimePickerFragment.Callbacks{
    private var callbacks: CallBacks? = null
    private lateinit var titleEditView: EditText
    private lateinit var descEditView: EditText
    private lateinit var calendarView: CalendarView
    private lateinit var addTaskButton: Button
    private lateinit var startTaskText: TextView
    private lateinit var taskDate: Date
    private lateinit var addNewTaskViewModel: AddNewTaskViewModel
    private var task: TasksDB = TasksDB()



    override fun onStart() {
        super.onStart()
        callbacks = context as CallBacks
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addNewTaskViewModel = ViewModelProvider(this).get(AddNewTaskViewModel::class.java)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.add_new_task, container, false)
        titleEditView = view.findViewById(R.id.new_task_title)
        descEditView = view.findViewById(R.id.new_title_description)
        calendarView = view.findViewById(R.id.calender_date_picker)
        addTaskButton = view.findViewById(R.id.add_new_task_button)
        startTaskText = view.findViewById(R.id.date_and_time_picker)
        calenderDatePicker()
        getStartAndEndDate()

        calendarView.setBackgroundColor(context?.resources?.getColor(R.color.dark_gray)!!)

        addTaskButton.setOnClickListener {
            if (!titleEditView.text.isNullOrEmpty()){
                task.title = titleEditView.text.toString()
                task.description= descEditView.text.toString()
                addNewTaskViewModel.addTask(task)
                Toast.makeText(context,"Task have been added successfully",Toast.LENGTH_SHORT).show()
                callbacks?.callBacks("ToDoListMain")
            }
            else{
                Toast.makeText(context,"Please add title to the task",Toast.LENGTH_SHORT).show()
            }

        }
        return view

    }
    private fun getStartAndEndDate(){
        val timeFormat = "hh:mm a"
        val localDate = LocalDate.now()
        val localDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(timeFormat))
        startTaskText.text = "$localDate $localDateTime"

    }
    private fun calenderDatePicker(){
        calendarView.setOnDateChangeListener{ calendarView, year, months, days  ->
            Toast.makeText(context,"$year-${months+1}-$days",Toast.LENGTH_SHORT).show()
             val inputFormat =SimpleDateFormat("yyyy-MM-dd")
            val date:Date = inputFormat.parse("$year-${months+1}-$days")
            TimePickerFragment.newInstance(date).apply {
                setTargetFragment(this@AddNewTaskFragment, REQUEST_TIME)
                show(this@AddNewTaskFragment.requireFragmentManager(), DIALOG_Time)
            }

        }
    }

    companion object{
        fun newInstance(): AddNewTaskFragment{
            return AddNewTaskFragment()
        }
    }

    override fun onStop() {
        super.onStop()
        callbacks = null
        task = TasksDB()
    }

    override fun onTimeSelected(date: Date) {
       taskDate = date
        startTaskText.text = SimpleDateFormat("yyyy-MM-dd hh:mm aa").format(date)
        task.date = date

    }
}