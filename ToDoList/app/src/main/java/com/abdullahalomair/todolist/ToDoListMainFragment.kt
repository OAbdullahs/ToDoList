package com.abdullahalomair.todolist

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


private const val TAG = "ToDoListMainFragment"
class ToDoListMainFragment: Fragment(){
    private var callbacks: CallBacks? = null
    private lateinit var dateExplorerRecyclerView: RecyclerView
    private lateinit var dateTaskRecyclerView: RecyclerView
    private lateinit var monthAndYearSelected: TextView
    private lateinit var toDoListMainViewModel: ToDoListMainViewModel
    private lateinit var adapter: DateAdapter
    private  var adapterExplorer: TaskAdapter = TaskAdapter(emptyList())
    private lateinit var nextMonthButton: ImageView
    private lateinit var backMonthButton: ImageView
    private lateinit var addNewTaskButton: ImageButton
    private var tasksDB: List<TasksDB> = emptyList()



    override fun onStart() {
        super.onStart()
        callbacks = context as CallBacks

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toDoListMainViewModel = ViewModelProvider(this).get(ToDoListMainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.to_do_list_main, container, false)

        monthAndYearSelected = view.findViewById(R.id.calender_month_year)
        nextMonthButton = view.findViewById(R.id.next_month_button)
        backMonthButton = view.findViewById(R.id.back_month_button)
        addNewTaskButton = view.findViewById(R.id.add_to_do_list_button)

        //go to Add new task fragment
        addNewTaskButton.setOnClickListener {
            callbacks?.callBacks("AddNewTaskFragment")
        }

        //observe the data from the database
        toDoListMainViewModel.tasksLiveData.observe(
            viewLifecycleOwner, { data ->
                tasksDB = data
                Log.i(TAG, tasksDB.toString())
            }
        )


        //get the generated Dates
        val dateGenerated = toDoListMainViewModel.currentMonthSelected
        adapter = DateAdapter(dateGenerated)

        //Horizontal recycle view settings
        dateExplorerRecyclerView = view.findViewById(R.id.date_explorer_recyclerview)
        dateExplorerRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        dateExplorerRecyclerView.adapter = adapter
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        itemDecoration.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.item_divider)!!)
        dateExplorerRecyclerView.addItemDecoration(itemDecoration)

        //setting the tasks Recycler View
        dateTaskRecyclerView = view.findViewById(R.id.date_task_recycleview)
        dateTaskRecyclerView.layoutManager = LinearLayoutManager(context)
        dateTaskRecyclerView.adapter = adapterExplorer

        //getting list of the tasks
        toDoListMainViewModel.tasksLiveData.observe(
            viewLifecycleOwner, { tasks ->
                tasksDB = tasks.sortedBy { tasksDB ->
                    tasksDB.date
                }
            }
        )

        //get Year and month selected
        setDayAndMonthText()

        //back and next button
        nextMonthButton.setOnClickListener {
        updateUI('+')
        }
        backMonthButton.setOnClickListener {
            updateUI('-')
        }
        return view
    }
    private fun updateUI(op: Char){
        when(op){
            '+' -> {
                toDoListMainViewModel.incrementMonth()
                setDayAndMonthText()
                val dateGenerated = toDoListMainViewModel.currentMonthSelected
                adapter = DateAdapter(dateGenerated)
                dateExplorerRecyclerView.adapter = adapter

            }
            else ->{
                toDoListMainViewModel.decrementMonth()
                setDayAndMonthText()
                val dateGenerated = toDoListMainViewModel.currentMonthSelected
                adapter = DateAdapter(dateGenerated)
                dateExplorerRecyclerView.adapter = adapter
            }
        }
    }

    override fun onStop() {
        super.onStop()
        callbacks = null
        tasksDB = emptyList()
    }

    override fun onResume() {
        super.onResume()

    }

    private fun setDayAndMonthText(){
        val monthAndYear = "${toDoListMainViewModel.getSelectedMonth} ${toDoListMainViewModel.getSelectedYear}"
        monthAndYearSelected.text = monthAndYear
    }

    private inner class DateHolder(view: View)
        :RecyclerView.ViewHolder(view){
        val dayName: TextView = itemView.findViewById(R.id.day_name)
        val dayNumber:TextView = itemView.findViewById(R.id.month_number)
        fun bind(month: DayOfWeek, num: Int){
            val smallMonth = month.toString().substring(0..2)
            dayName.text = smallMonth
            dayNumber.text = num.toString()
        }

    }
    private inner class DateAdapter(var dateTime: LocalDate)
        : RecyclerView.Adapter<DateHolder>() {
        private var listOfDatesTasks: List<TasksDB> = emptyList()
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateHolder {
            val view = layoutInflater.inflate(R.layout.horizontal_date_explorer, parent, false)
            return DateHolder(view)
        }

        override fun onBindViewHolder(holder: DateHolder, position: Int) {
            val monthDate = LocalDate.of(dateTime.year, dateTime.monthValue, position + 1)
            dateTime = monthDate
            for (tasks in tasksDB){
                val formatDate = SimpleDateFormat("yyyy-MM-dd").format(tasks.date)
                val nowFormatted  = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    .format(monthDate)
                if (formatDate == nowFormatted){
                    listOfDatesTasks += tasks
                    holder.dayNumber.background = getDrawable(context!!, R.drawable.task_requaired)
                }
            }

            holder.itemView.setOnClickListener {
                var list: List<TasksDB> = emptyList()
                for (tasks in tasksDB){
                    val formatDate = SimpleDateFormat("yyyy-MM-dd").format(tasks.date)
                    val nowFormatted  = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        .format(monthDate)
                    if (formatDate == nowFormatted){
                        list += tasks
                        holder.dayNumber.background = getDrawable(
                            context!!,
                            R.drawable.task_requaired
                        )
                    }
                }
                adapterExplorer = TaskAdapter(list)
                dateTaskRecyclerView.adapter = adapterExplorer
            }
            if (dateTime == LocalDate.now()){
                holder.apply {
                    this.dayName.setTypeface(null, Typeface.BOLD)
                    this.dayNumber.setTypeface(null, Typeface.BOLD)
                }
            }
            holder.bind(dateTime.dayOfWeek, position + 1)
        }

        override fun getItemCount(): Int = dateTime.lengthOfMonth()


    }

    private inner class TaskHolder(view: View)
        :RecyclerView.ViewHolder(view){
       val taskTitle: TextView = view.findViewById(R.id.to_do_list_title)
       val taskDesc: TextView = view.findViewById(R.id.to_do_list_description)
        val timeFrom: TextView = view.findViewById(R.id.time_from)
        val cardViewHolder: CardView = view.findViewById(R.id.cardView_holder)

        fun bind(time: String, task: TasksDB){
            val title = task.title
            val desc = task.description
            val id = task.id
            val isDone = task.isDone
            if (isDone){
                taskTitle.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                taskDesc.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            }
            taskTitle.text = title
            taskDesc.text = desc
            timeFrom.text = time

            val androidColors = resources.getIntArray(R.array.androidcolors)
            val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
            cardViewHolder.setBackgroundColor(randomAndroidColor)

            cardViewHolder.setOnClickListener {
               callbacks?.passIDCallBack(id)
            }

        }


    }
    private inner class TaskAdapter(var tasksDB: List<TasksDB>)
        :RecyclerView.Adapter<TaskHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
          val view = layoutInflater.inflate(R.layout.to_do_list_details, parent, false)
            return TaskHolder(view)
        }

        override fun onBindViewHolder(holder: TaskHolder, position: Int) {
            val calender = Calendar.getInstance()
            calender.time = tasksDB[position].date
            val getTime = calender.get(Calendar.HOUR_OF_DAY)
            val getAmPm = if(calender.get(Calendar.AM_PM) == 0) "$getTime am" else "$getTime pm"

        holder.bind(getAmPm, tasksDB[position])

        }

        override fun getItemCount(): Int = tasksDB.size

    }




    companion object{
        fun newInstance(): ToDoListMainFragment {
            return ToDoListMainFragment()
        }
    }
}