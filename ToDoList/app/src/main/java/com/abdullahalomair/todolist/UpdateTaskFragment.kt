package com.abdullahalomair.todolist

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import java.lang.Exception
import java.util.*

private const val TAG = "UpdateTaskDialogFragment"
private const val taskId = "task"
class UpdateTaskDialogFragment: Fragment(){
    private lateinit var titleText: EditText
    private lateinit var descEditText: EditText
    private lateinit var completeTaskButton: Button
    private lateinit var deleteTaskButton: Button
    private lateinit var confirmUpdateButton:Button
    private lateinit var greenBallImage: ImageView
    private lateinit var donAnimationImage: ImageView
    private lateinit var updateTaskFragmentViewModel: UpdateTaskFragmentViewModel
    private var callbacks: CallBacks? = null

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
        greenBallImage.visibility = View.GONE
        donAnimationImage.visibility = View.GONE

            updateTaskFragmentViewModel.getTask(taskId).observe(
                viewLifecycleOwner, { task ->
                 updateUI(task)
                }
            )
        return view
    }
    private fun updateUI(task: TasksDB?){
        if (task != null) {
            titleText.setText(task.title)
            descEditText.setText(task.description)
            if (task.isDone) {
                completeTaskButton.setText(R.string.task_complete_info)
                completeTaskButton.backgroundTintList = ContextCompat.getColorStateList(context!!,R.color.light_green)
            }
            completeTaskButton.setOnClickListener {
                task.isDone = !task.isDone
                if (task.isDone){
                    donAnimationImage.visibility = View.VISIBLE
                    greenBallImage.visibility = View.VISIBLE
                    completeTaskButton.text = context?.resources?.getString(R.string.task_complete_info)
                    completeTaskButton.backgroundTintList = ContextCompat.getColorStateList(context!!,R.color.light_green)
                    val drawable: Drawable = donAnimationImage.drawable
                    try {
                    val av1: AnimatedVectorDrawableCompat = drawable as AnimatedVectorDrawableCompat
                    av1.start()
                    }catch (e:Exception){
                        try {
                            val av2: AnimatedVectorDrawable = drawable as AnimatedVectorDrawable
                            av2.start()
                        }catch (e:Exception){}
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
                task.title = titleText.text.toString()
                task.description = descEditText.text.toString()
                updateTaskFragmentViewModel.updateTask(task)
                callbacks?.callBacks("ToDoListMain")
            }
            deleteTaskButton.setOnClickListener {
                updateTaskFragmentViewModel.deleteTask(task)
                callbacks?.callBacks("ToDoListMain")
            }

        }
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
}