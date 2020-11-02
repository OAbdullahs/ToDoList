package com.abdullahalomair.todolist

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*


private const val TAG = "ToDoListMainFragment"
class ToDoListMainFragment: Fragment(){
    private var callbacks: CallBacks? = null
    private lateinit var currentMonth: TextView
    private lateinit var currentTime: TextView
    private val currentCalendar = Calendar.getInstance()
    private val currentMonthName = SimpleDateFormat("MMMM").format(currentCalendar.time)
    private lateinit var dateExplorerRecyclerView: RecyclerView
    private lateinit var monthAndYearSelected: TextView
    private lateinit var toDoListMainViewModel: ToDoListMainViewModel
    private lateinit var adapter: DateAdapter
    private lateinit var nextMonthButton: ImageView
    private lateinit var backMonthButton: ImageView


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
        currentMonth = view.findViewById(R.id.current_month)
        currentMonth.text = currentMonthName
        currentTime = view.findViewById(R.id.current_time) as TextView
        monthAndYearSelected = view.findViewById(R.id.calender_month_year)
        nextMonthButton = view.findViewById(R.id.next_month_button)
        backMonthButton = view.findViewById(R.id.back_month_button)


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
        itemDecoration.setDrawable(ContextCompat.getDrawable(context!!,R.drawable.item_divider)!!)
        dateExplorerRecyclerView.addItemDecoration(itemDecoration)

        //get Year and month selected
        setDayAndMonthText()
        //Update time every minute
        updateTimeFrequently()

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
            '+' ->{
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
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateHolder {
            val view = layoutInflater.inflate(R.layout.horizontal_date_explorer, parent, false)
            return DateHolder(view)
        }

        override fun onBindViewHolder(holder: DateHolder, position: Int) {
            val monthDate = LocalDate.of(dateTime.year, dateTime.monthValue, position + 1)
            dateTime = monthDate
            if (dateTime == LocalDate.now()){
                Log.i(TAG,"----------" + dateTime.toString())
                holder.apply {
                    this.dayName.setTypeface(null, Typeface.BOLD)
                    this.dayNumber.setTypeface(null, Typeface.BOLD)
                }
            }
            holder.bind(dateTime.dayOfWeek, position + 1)
        }

        override fun getItemCount(): Int = dateTime.lengthOfMonth()
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

    companion object{
        fun newInstance(): ToDoListMainFragment {
            return ToDoListMainFragment()
        }
    }
}