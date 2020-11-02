package com.abdullahalomair.todolist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

class MainActivity : AppCompatActivity(), CallBacks {

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
        val fragment = LetsGetStartedFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_manager,fragment)
                .commit()
        }
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
        }
    }
}