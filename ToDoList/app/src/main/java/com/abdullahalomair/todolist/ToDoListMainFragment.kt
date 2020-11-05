package com.abdullahalomair.todolist

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.AvailableNetworkInfo
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
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.*
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


private const val TAG = "ToDoListMainFragment"
private const val BUNDLE = "ToDoListMainFragmentBundle"
private const val POLL_WORK = "POLL_WORK"
class ToDoListMainFragment: Fragment(){
    private var callbacks: CallBacks? = null
    private lateinit var dateExplorerRecyclerView: RecyclerView
    private lateinit var dateTaskRecyclerView: RecyclerView
    private lateinit var monthAndYearSelected: TextView
    private lateinit var toDoListMainViewModel: ToDoListMainViewModel
    private lateinit var adapter: DateAdapter
    private  var adapterExplorer: TaskAdapter = TaskAdapter(emptyList(), false)
    private lateinit var nextMonthButton: ImageView
    private lateinit var backMonthButton: ImageView
    private lateinit var addNewTaskButton: ImageButton
    private lateinit var allTimeText: TextView
    private lateinit var noTasksAvailable : TextView



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
        allTimeText = view.findViewById(R.id.all_day_text)
        noTasksAvailable = view.findViewById(R.id.no_data_avilabale)


        //go to Add new task fragment
        addNewTaskButton.setOnClickListener {
            callbacks?.callBacks("AddNewTaskFragment")
        }

        //Horizontal recycle view settings
        dateExplorerRecyclerView = view.findViewById(R.id.date_explorer_recyclerview)
        dateExplorerRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        itemDecoration.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.item_divider)!!)
        dateExplorerRecyclerView.addItemDecoration(itemDecoration)

        //setting the tasks Recycler View
        dateTaskRecyclerView = view.findViewById(R.id.date_task_recycleview)
        dateTaskRecyclerView.layoutManager = LinearLayoutManager(context)

        //getting list of the tasks and applying passes the tasks from the database
        toDoListMainViewModel.tasksLiveData.observe(
            viewLifecycleOwner, { tasks ->
                val tasksDB = tasks.sortedBy { tasksDB ->
                    tasksDB.date
                }
            val dateGenerated = toDoListMainViewModel.currentMonthSelected
            adapter = DateAdapter(dateGenerated, tasksDB)
            dateExplorerRecyclerView.adapter = adapter

            val allTasksNotDone= tasks.filter { data-> !data.isDone }
                    .sortedBy {
                        it.date
                    }
                allTimeText.text = getString(R.string.all_time)
            if (allTasksNotDone.isNullOrEmpty()){
                noTasksAvailable.visibility = View.VISIBLE
            }else {
                noTasksAvailable.visibility = View.GONE
                adapterExplorer = TaskAdapter(allTasksNotDone, true)
                dateTaskRecyclerView.adapter = adapterExplorer
            }

            //back and next button
            nextMonthButton.setOnClickListener {
                updateUI('+', tasksDB, allTasksNotDone)
            }
            backMonthButton.setOnClickListener {
                updateUI('-',tasksDB, allTasksNotDone)
            }

            }
        )

        //setting a background thread
        val periodicRequest = PeriodicWorkRequest
            .Builder(PollWorker::class.java, 1,TimeUnit.HOURS)
            .build()
        WorkManager.getInstance()
            .enqueueUniquePeriodicWork(POLL_WORK,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest)

        //get Year and month selected
        setDayAndMonthText()


        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(BUNDLE,outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
       this.arguments = savedInstanceState?.getBundle(BUNDLE)
    }

    //update the ui if clicked on < or >
    private fun updateUI(op: Char, tasks: List<TasksDB>, noTasks:List<TasksDB>){
        when(op){
            '+' -> {
                toDoListMainViewModel.incrementMonth()
                setDayAndMonthText()
                val dateGenerated = toDoListMainViewModel.currentMonthSelected
                adapter = DateAdapter(dateGenerated,tasks)
                dateExplorerRecyclerView.adapter = adapter
                allTimeText.text = getString(R.string.all_time)
                if (noTasks.isNullOrEmpty()){
                    noTasksAvailable.visibility = View.VISIBLE
                    dateTaskRecyclerView.visibility = View.GONE
                }else {
                    adapterExplorer = TaskAdapter(noTasks, true)
                    dateTaskRecyclerView.adapter = adapterExplorer
                    noTasksAvailable.visibility = View.GONE
                    dateTaskRecyclerView.visibility = View.VISIBLE
                }

            }
            else ->{
                toDoListMainViewModel.decrementMonth()
                setDayAndMonthText()
                val dateGenerated = toDoListMainViewModel.currentMonthSelected
                adapter = DateAdapter(dateGenerated,tasks)
                dateExplorerRecyclerView.adapter = adapter
                allTimeText.text = getString(R.string.all_time)
                if (noTasks.isNullOrEmpty()){
                    noTasksAvailable.visibility = View.VISIBLE
                    dateTaskRecyclerView.visibility = View.GONE
                }else {
                    adapterExplorer = TaskAdapter(noTasks, true)
                    dateTaskRecyclerView.adapter = adapterExplorer
                    dateTaskRecyclerView.visibility = View.VISIBLE
                    noTasksAvailable.visibility = View.GONE
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        callbacks = null
        activity?.setActionBar(null)
    }


    //set The month and year text vale
    private fun setDayAndMonthText(){
        val monthAndYear = "${toDoListMainViewModel.getSelectedMonth} ${toDoListMainViewModel.getSelectedYear}"
        monthAndYearSelected.text = monthAndYear
    }

    //This recycler view is for the first recycler view (The horizontal one) Holder class
    //Display all dates with a green circle if the date has a task
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
    private inner class DateAdapter(var dateTime: LocalDate,val tasksDB: List<TasksDB>)
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
                val time = tasks.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                val areTheyEqual = monthDate.isEqual(time)
                if (areTheyEqual){
                    listOfDatesTasks += tasks
                    holder.dayNumber.background = getDrawable(context!!, R.drawable.task_requaired)
                    break
                }
                else{
                    holder.dayNumber.background = getDrawable(context!!, R.drawable.avalibale_tasks)
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
                    }

                }
                if (list.isNullOrEmpty()){
                    noTasksAvailable.visibility = View.VISIBLE
                    dateTaskRecyclerView.visibility = View.GONE
                }else {
                    noTasksAvailable.visibility = View.GONE
                    allTimeText.text = getString(R.string.all_day)
                    adapterExplorer = TaskAdapter(list, false)
                    dateTaskRecyclerView.adapter = adapterExplorer
                    dateTaskRecyclerView.visibility = View.VISIBLE
                }
            }
            //Make day bold if it is the same is localDate
            if (dateTime == LocalDate.now()){
                holder.apply {
                    this.dayName.setTypeface(null, Typeface.BOLD)
                    this.dayNumber.setTypeface(null, Typeface.BOLD)
                }
            }
            else{
                holder.apply {
                    this.dayName.setTypeface(null, Typeface.NORMAL)
                    this.dayNumber.setTypeface(null, Typeface.NORMAL)
                }
            }
            holder.bind(dateTime.dayOfWeek, position + 1)
        }

        override fun getItemCount(): Int = dateTime.lengthOfMonth()


    }

    //This recycler view is for the second recycler view (The vertical one) Task holder class
    //Display all the tasks *depends on the user choice
    private inner class TaskHolder(view: View)
        :RecyclerView.ViewHolder(view){
       val taskTitle: TextView = view.findViewById(R.id.to_do_list_title)
       val taskDesc: TextView = view.findViewById(R.id.to_do_list_description)
        val timeFrom: TextView = view.findViewById(R.id.time_from)
        val cardViewHolder: CardView = view.findViewById(R.id.cardView_holder)
        val timeRemainingCountdown:TextView = view.findViewById(R.id.time_remaining)
        val exactTime:TextView = view.findViewById(R.id.exact_time)
        val creationDate: TextView = view.findViewById(R.id.creation_date_text)

        fun bind(time: String, task: TasksDB, isSelected: Boolean){
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
            val format = SimpleDateFormat("yy/MM/dd")
            exactTime.text = format.format(task.date)
            val createdOn = "${getString(R.string.date_created)} ${format.format(task.creationDate)}"
            creationDate.text = createdOn
            val androidColors = resources.getIntArray(R.array.androidcolors)
            val randomAndroidColor = androidColors[Random().nextInt(androidColors.size)]
            cardViewHolder.setBackgroundColor(randomAndroidColor)

            //send due date
            val dueDate = task.date
            updateTimeFrequently(task.isDone,dueDate)

            //set Click listener to update the task
            cardViewHolder.setOnClickListener {
               callbacks?.passIDCallBack(id)
            }
            if (isSelected){
                exactTime.setTextColor(getColor(context!!,R.color.black))
            }
            else{
                exactTime.setTextColor(getColor(context!!,R.color.white))
            }


        }
        private fun updateTimeFrequently(isDone: Boolean,dueDate:Date){
            val timeHandler = Handler(Looper.getMainLooper())
            timeHandler.postDelayed(object : Runnable {
                @SuppressLint("SetTextI18n")
                override fun run() {
                    val late = Date().toInstant().isAfter(dueDate.toInstant())
                    if (!isDone){
                        var duration: Long = if (late) {
                            Date().time - dueDate.time
                        } else {
                        dueDate.time - Date().time
                        }
                        val seconds: Long = ((duration / 1000) % 60)
                        val minutes: Long = ((duration / (1000 * 60)) % 60)
                        val hours: Long = ((duration / (1000 * 60 * 60)) % 24)
                        val days: Long = ((duration / (1000 * 60 * 60 * 24)) % 365)
                        val stringStatus = if (late) {
                            "${activity?.getText(R.string.late_time)}"
                        } else {
                            "${activity?.getText(R.string.remaining_time)}"
                        }
                         val output = stringStatus +
                            "$days ${activity?.getText(R.string.days)} $hours ${activity?.getText(R.string.hours)} " +
                            "$minutes ${activity?.getText(R.string.minutes)} $seconds ${activity?.getText(R.string.seconds)}"
                        timeRemainingCountdown.text = output
                        if (late) {
                            timeRemainingCountdown.setTextColor(getColor(context!!,R.color.red))
                        } else {
                         timeRemainingCountdown.setTextColor(getColor(context!!,R.color.light_green))
                        }
                }
                else{ timeRemainingCountdown.visibility = View.GONE }
                    timeHandler.postDelayed(this, 1000)
                }
            }, 100)
        }

    }
    private inner class TaskAdapter(var tasksDB: List<TasksDB>, val isSelected :Boolean)
        :RecyclerView.Adapter<TaskHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
          val view = layoutInflater.inflate(R.layout.to_do_list_details, parent, false)
            return TaskHolder(view)
        }

        override fun onBindViewHolder(holder: TaskHolder, position: Int) {
            val calender = Calendar.getInstance()
            calender.time = tasksDB[position].date
            val getTime = calender.get(Calendar.HOUR)
            val convertStart = if (getTime == 0) 12 else getTime
            val getAmPm = if(calender.get(Calendar.AM_PM) == 0) "$convertStart am" else "$convertStart pm"
        holder.bind(getAmPm, tasksDB[position], isSelected)

        }

        override fun getItemCount(): Int = tasksDB.size

    }




    companion object{
        fun newInstance(): ToDoListMainFragment {
            return ToDoListMainFragment()
        }
    }
}