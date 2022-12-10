package com.example.mobileappproject

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.R
import com.example.mobileappproject.databinding.DaysCellBinding
import com.example.mobileappproject.CalendarUtil
import java.time.LocalDate
import android.content.Context
import android.graphics.Typeface
//import android.graphics.Color
import android.util.Log
import com.example.mobileappproject.databinding.StatisticsDaysCellBinding


class StatisticsCalendarAdapter(private val days: MutableList<LocalDate?>, private var activity: LifecycleOwner, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class StatisticsCalendarViewHolder(val binding: StatisticsDaysCellBinding):
        RecyclerView.ViewHolder(binding.root)
    private var list = mutableListOf<Todo>()



    //timertodolistadapter 참고
    /*inner class TimerListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.timertodoTitle)
        var startTime = itemView.findViewById<TextView>(R.id.timertodoTime)
        var content = itemView.findViewById<TextView>(R.id.timertodoContent)
        var date = itemView.findViewById<TextView>(R.id.timertodoDate)
        var isTimer = itemView.findViewById<TextView>(R.id.isTimer)
        var categoryNum = itemView.findViewById<TextView>(R.id.todoListItem_category)//이거 id 확실하지 않음
        var basicTimer = itemView.findViewById<TextView>(R.id.basicTimer)
        var pomodoro = itemView.findViewById<TextView>(R.id.pomodoro)
        var timeBox = itemView.findViewById<TextView>(R.id.timebox)

        fun onBind(TodoTitle:String, TodoStart:String, TodoEnd:String,TodoContent:String, TodoDate: String,
        TodoIsTimer:Boolean, TodoCategoryNum:Int, TodoBasic:String, TodoPomodoro:String, TodoTimeBox:String) {
            title.text = "title = ${TodoTitle}              "
            startTime.text = "time = ${TodoStart} ~ ${TodoEnd}"
            content.text = "context = ${TodoContent}"
            date.text = "date = ${TodoDate}"
            isTimer.text = "isTime = ${TodoIsTimer}"
            categoryNum.text = "categoryNum = ${TodoCategoryNum}"
            basicTimer.text = "basicTimer = ${TodoBasic}"
            pomodoro.text = "pomodoro = ${TodoPomodoro}"
            timeBox.text = "timeBox = ${TodoTimeBox}"
        }
    }*/


    // cell clickListener interface
    interface OnItemClickListener{
        fun onItemClick(view: View, position: Int)
    }
    var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.statistics_days_cell, parent, false)
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()

        return StatisticsCalendarViewHolder(StatisticsDaysCellBinding.bind(view))
    }

    //override fun getItemCount_todo(): Int = list.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as StatisticsCalendarViewHolder).binding

        // itemClickListener 연결
        if (listener != null){
            binding.cellRoot.setOnClickListener(View.OnClickListener {
                listener?.onItemClick(it, position)
            })
        }


        //타이머 시간들 받아오기
        /* var basictime = 0
         var pomodorotime = 0
         var timeboxtime = 0

         if(list[position].basicTimer.size != 0){
             basictime = list[position].basictime[list[position].basictime.size-1]
         }
 */

        // 날짜표시 & 오늘날짜 바탕색
        val day : LocalDate? = days[position]
        val dayDel = days[15].toString().split("-")[1] == day.toString().split("-")[1]

        if (day == null) {
            binding.cellDayText.text = ""
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if(!dayDel) {
                    binding.cellDayText.setTextColor(Color.BLACK)
                    binding.cellDayText.setTypeface(null, Typeface.NORMAL)
                    binding.cellRoot.setBackgroundColor(Color.LTGRAY)
                }
                binding.cellDayText.text = day.dayOfMonth.toString()
            }
            if (day.equals(CalendarUtil.today)) {
                binding.cellDayText.setBackgroundColor(Color.LTGRAY)
            }

        }

        // change weekend textColor
        if((position+1) % 7 == 0 && dayDel) {
            binding.cellDayText.setTextColor(Color.BLUE)
        } else if (position == 0 || position % 7 == 0 && dayDel) {
            binding.cellDayText.setTextColor(Color.RED)
        }



        //set to do list title for day

        val todo = TodoViewModel().getCurrentDay(day.toString())
        todo.observe(activity) {
            if (it.size > 0) {
                binding.todoTitle1.text = it[0].title
                if (it.size > 1) {
                    binding.todoTitle2.text = it[1].title
                }
            }
        }


        // set Colors to cells
        var title: MutableList<String> = mutableListOf()
        var startTime = mutableListOf<String>()
        var endTime = mutableListOf<String>()
        var content = mutableListOf<String>()
        var date = mutableListOf<String>()
        var isTimer = mutableListOf<Boolean>()
        var categoryNum = mutableListOf<Int>()
        var basicTimer = mutableListOf<String>()
        var pomodoro = mutableListOf<String>()
        var timeBox = mutableListOf<String>()
        var t = ""
        val setCellColors = TodoViewModel().getCurrentDay(day.toString())
        setCellColors.observe(activity) { it ->
            // when there is to do in current day
            println("======================> ${it.size}")
            if (it.size == 0)
            {
                binding.cellRoot.setBackgroundColor(Color.GREEN)
            }

            // all to do datas in current cell
            // you can't get db data out of this block So work here
            else if (it.size > 0) {
                for (i in 0 until it.size) {
                    title.add(it[i].title)
                    startTime.add(it[i].startTime)
                    endTime.add(it[i].endTime)
                    content.add(it[i].content)
                    date.add(it[i].date)
                    isTimer.add(it[i].isTimer)
                    categoryNum.add(it[i].categoryNum)
                    basicTimer.add(it[i].basicTimer)
                    pomodoro.add(it[i].pomodoro)
                    timeBox.add(it[i].timeBox)
                }
                // all to do list that was add in one day
                println("title === ${title.toString()}")
                println("startTime == $startTime")
                println("endTime == $endTime")
                println("content == $content")
                println("date == $date")
                println("isTime == $isTimer")
                println("CategoryNum = $categoryNum")
                println("basicTime == $basicTimer")
                println("pomodoro == $pomodoro")
                println("timebox == $timeBox")


                // calculate
                var startEndTImeInTodo = 0
                // total time of basic timers in one day
                for(i in 0 until startTime.size)
                {
                    if (startTime[i].toString() != "시작시간" && endTime[i].toString() != "종료시간") {
                        val (hs, ms) = startTime[i].split(":").map { it1 -> it1.toInt() }
                        val (he, me) = endTime[i].split(":").map { it2 -> it2.toInt() }
                        // convert hours to min and add
                        startEndTImeInTodo += (he * 60 + me) - (hs * 60 - ms)
                    }
                }

                var basicTime = 0
                // if 0 than will raise error while split basicTimer variable to do in more easy way when not timer than set to 00:00:00 type
                for (i in 0 until basicTimer.size){
                    if (basicTimer[i] != "0"){
                        var (h, m, s) = basicTimer[i].split(":").map { it3->it3.toInt() }
                        //converting to second
                        basicTime += h * 3600 + m * 60 + s
                    }
                }

                // choose color depend on time that was set in to Do
                // or depend on timer type time

                if(startEndTImeInTodo < 120) {
                    binding.cellRoot.setBackgroundColor(R.color.less2H_blue)
                }
                else if (startEndTImeInTodo in 121..239)
                    binding.cellRoot.setBackgroundColor(R.color.less4H_blue)
                else if (startEndTImeInTodo in 241..359)
                    binding.cellRoot.setBackgroundColor(R.color.less6H_blue)
                else if(startEndTImeInTodo in 360..479)
                    binding.cellRoot.setBackgroundColor(R.color.less8H_blue)

            }


        }

    }

    override fun getItemCount(): Int = days.size

}