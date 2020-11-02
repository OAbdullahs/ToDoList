package com.abdullahalomair.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

private const val TAG = "LetsGetStartedFragment"
class LetsGetStartedFragment:Fragment(){
    private var callbacks: CallBacks? = null
    private lateinit var letsGetStartedButton: Button

    override fun onStart() {
        super.onStart()
        callbacks = context as CallBacks
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.lets_get_started,container,false)
        letsGetStartedButton = view.findViewById(R.id.lets_get_started_button) as Button

        letsGetStartedButton.setOnClickListener {
            callbacks?.callBacks("ToDoListMain")
        }
        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        callbacks = null
    }

    companion object{
        fun newInstance(): LetsGetStartedFragment {
            return LetsGetStartedFragment()
        }
    }
}