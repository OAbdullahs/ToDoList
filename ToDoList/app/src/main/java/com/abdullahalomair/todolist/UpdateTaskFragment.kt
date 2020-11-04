package com.abdullahalomair.todolist

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "UpdateTaskDialogFragment"
private const val DIALOG_Time = "DialogTime"
private const val REQUEST_TIME = 1
class UpdateTaskDialogFragment: Fragment(), TimePickerFragment.Callbacks{
    private lateinit var titleText: EditText
    private lateinit var descEditText: EditText
    private lateinit var completeTaskButton: Button
    private lateinit var deleteTaskButton: Button
    private lateinit var confirmUpdateButton:Button
    private lateinit var greenBallImage: ImageView
    private lateinit var donAnimationImage: ImageView
    private lateinit var updateTaskFragmentViewModel: UpdateTaskFragmentViewModel
    private lateinit var updateDateCalender: CalendarView
    private var callbacks: CallBacks? = null
    private lateinit var taskObject: TasksDB

    override fun onStart() {
        super.onStart()
        callbacks = context as CallBacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        updateTaskFragmentViewModel = ViewModelProvider(this)
            .get(UpdateTaskFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.update_task,container,false)
        titleText = view.findViewById(R.id.update_task_title)
        descEditText = view.findViewById(R.id.update_task_desc)
        completeTaskButton = view.findViewById(R.id.task_complete_button)
        deleteTaskButton =view.findViewById(R.id.delete_task_button)
        confirmUpdateButton = view.findViewById(R.id.confirm_update)
        greenBallImage = view.findViewById(R.id.green_ball_image)
        donAnimationImage = view.findViewById(R.id.done_animation_image)
        updateDateCalender = view.findViewById(R.id.update_date_calender)
        greenBallImage.visibility = View.GONE
        donAnimationImage.visibility = View.GONE

            updateTaskFragmentViewModel.getTask(taskId).observe(
                viewLifecycleOwner, { task ->
                    if (task != null) {
                        taskObject = task
                    }
                 updateUI()
                }
            )

        return view
    }
    private fun updateUI(){
        if (taskObject != null) {
            titleText.setText(taskObject.title)
            descEditText.setText(taskObject.description)
            if (taskObject.isDone) {
                completeTaskButton.setText(R.string.task_complete_info)
                completeTaskButton.backgroundTintList = ContextCompat.getColorStateList(context!!,R.color.light_green)
            }
            completeTaskButton.setOnClickListener {
                taskObject.isDone = !taskObject.isDone
                if (taskObject.isDone){
                    completeTaskButton.text = context?.resources?.getString(R.string.task_complete_info)
                    completeTaskButton.backgroundTintList = ContextCompat.getColorStateList(context!!,R.color.light_green)
                    // try to animate success logo
                    val drawable: Drawable = donAnimationImage.drawable
                    try {
                        donAnimationImage.visibility = View.VISIBLE
                        greenBallImage.visibility = View.VISIBLE
                        updateDateCalender.visibility = View.GONE
                    val av1: AnimatedVectorDrawableCompat = drawable as AnimatedVectorDrawableCompat
                    av1.start()
                    displayCalenderAfterAnimation()
                    }catch (e:Exception){
                        updateDateCalender.visibility = View.VISIBLE
                        try {
                            updateDateCalender.visibility = View.GONE
                            val av2: AnimatedVectorDrawable = drawable as AnimatedVectorDrawable
                            av2.start()
                            displayCalenderAfterAnimation()
                        }catch (e:Exception){
                            updateDateCalender.visibility = View.VISIBLE
                        }
                    }
                }
                else{
                    greenBallImage.visibility = View.GONE
                    donAnimationImage.visibility = View.GONE
                    completeTaskButton.text = context?.resources?.getString(R.string.task_not_complete_info)
                    completeTaskButton.backgroundTintList = ContextCompat.getColorStateList(context!!,R.color.red)
                }
            }
            confirmUpdateButton.setOnClickListener {
                taskObject.title = titleText.text.toString()
                taskObject.description = descEditText.text.toString()
                updateTaskFragmentViewModel.updateTask(taskObject)
                callbacks?.callBacks("ToDoListMain")
            }
            deleteTaskButton.setOnClickListener {
                updateTaskFragmentViewModel.deleteTask(taskObject)
                callbacks?.callBacks("ToDoListMain")
            }
            updateDateCalender.setDate(taskObject.date.time,true,true)
            updateDateCalender.setOnDateChangeListener { calendarView, year, months, days ->
                val inputFormat = SimpleDateFormat("yyyy-MM-dd")
                val date:Date = inputFormat.parse("$year-${months+1}-$days")
                TimePickerFragment.newInstance(date).apply {
                    setTargetFragment(this@UpdateTaskDialogFragment, REQUEST_TIME)
                    show(this@UpdateTaskDialogFragment.requireFragmentManager(), DIALOG_Time)
                    taskObject.date = date
                }
            }
        }
    }
    //hide the photos and display the calender again
    private fun displayCalenderAfterAnimation(){
        val timeHandler: Handler = Handler(Looper.getMainLooper())
        timeHandler.postDelayed(object : Runnable {
            override fun run() {
                greenBallImage.visibility = View.GONE
                donAnimationImage.visibility = View.GONE
                updateDateCalender.visibility = View.VISIBLE

            }
        }, 1200)
    }

    override fun onStop() {
        super.onStop()
        callbacks = null
    }
    companion object{
        private lateinit var taskId: UUID
        fun newInstance(task: UUID): UpdateTaskDialogFragment {
            taskId = task
            return UpdateTaskDialogFragment()
        }
    }

    override fun onTimeSelected(date: Date) {
        taskObject.date = date
    }
}