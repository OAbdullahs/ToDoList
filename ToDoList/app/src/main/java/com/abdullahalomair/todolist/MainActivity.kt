package com.abdullahalomair.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.TextView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class MainActivity : AppCompatActivity(), CallBacks {
    private lateinit var currentMonth: TextView
    private lateinit var currentTime: TextView
    override fun onStart() {
        super.onStart()
        try {
        supportActionBar?.hide()
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }catch (e: NullPointerException){

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_manager)


        if (currentFragment == null){
            val toDoListMain = ToDoListMainFragment.newInstance()
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_manager,toDoListMain)
                    .commit()
        }

        currentMonth = findViewById(R.id.current_month)
        currentMonth.text = LocalDate.now().month.toString()
        currentTime = findViewById(R.id.current_time)
        //Update time every minute
        updateTimeFrequently()
    }

    override fun callBacks(fragmentName: String) {
        when(fragmentName){
            "ToDoListMain" ->{
                val toDoListMain = ToDoListMainFragment.newInstance()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_manager,toDoListMain)
                    .commit()
            }
            "AddNewTaskFragment" ->{
                val toDoListMain = AddNewTaskFragment.newInstance()
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_manager,toDoListMain)
                        .addToBackStack(null)
                        .commit()
            }
        }
    }

    override fun passIDCallBack(id: UUID) {
        val updateFragment = UpdateTaskDialogFragment.newInstance(id)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_manager,updateFragment)
            .commit()

    }

    private fun updateTimeFrequently(){
        val timeHandler: Handler = Handler(Looper.getMainLooper())
        timeHandler.postDelayed(object : Runnable {
            override fun run() {
                currentTime.text = SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())
                timeHandler.postDelayed(this, 1000)
            }
        }, 10)
    }
}