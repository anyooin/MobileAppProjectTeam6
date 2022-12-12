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


class StatisticsCalendarAdapter(private val days: MutableList<LocalDate?>, private var activity: LifecycleOwner, val context: Context,
/*val onStatisticsValues: (Array<Long>) -> Unit*/
val onPieChart : (MutableList<Int>)->Unit,
val onLineChart : (MutableList<String>)->Unit,
val onBarChart : (MutableList<String>)->Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class StatisticsCalendarViewHolder(val binding: StatisticsDaysCellBinding):
        RecyclerView.ViewHolder(binding.root)
    private var list = mutableListOf<Todo>()
    var pieList = mutableListOf<Int>()
    var lineList = mutableListOf<String>()
    var barList = mutableListOf<String>()



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

                //basic timer cell에 출력


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
/*
        var totalBasic: Long = 0
        var totalPomodoro: Long = 0
        var totalTimebox: Long = 0
        var totalNonDesignate: Long = 0
        var totalStudy: Long = 0
        var totalWorkout: Long = 0
        var totalMeeting: Long = 0
        var totalPromise: Long = 0
        var valuesArray = Array<Long>(22, {0})



        if(list.size!= 0) {
            for (num in 0..list.size-1) {

                //날짜별 계산
                if(list[num].date == day.toString()){
                    var count = 8 // array index
                    for(i in num downTo num-6) {
                        var week_basic = list[i].basicTimer
                        var week_h = week_basic.split(":")[0].toLong()
                        var week_m = week_basic.split(":")[0].toLong()
                        var week_s = week_basic.split(":")[0].toLong()

                        val week_time = week_h * 3600 + week_m * 60 + week_s

                        valuesArray[count] = week_time //index 8~14 까지 basic timer값 7일치
                        valuesArray[count+7] = list[i].pomodoro.toLong()// index 8~14 까지 pomodoro timer값 7일치
                        count++
                    }


                }

                //총 시간 계산
                var basic = list[num].basicTimer

                val h = basic.split(":")[0].toLong()
                val m = basic.split(":")[1].toLong()
                val s = basic.split(":")[2].toLong()

                val time = h * 3600 + m * 60 + s

                totalBasic += time
                totalPomodoro += list[num].pomodoro.toLong()
                totalTimebox += list[num].timeBox.toLong()
                when(list[num].categoryNum) {
                    1 -> totalStudy += time // 공부
                    2 -> totalWorkout += time // 운동
                    3 -> totalMeeting += time // 회의
                    4 -> totalPromise += time // 약속
                    else -> totalNonDesignate += time //지정안함
                }
            }
        }

        valuesArray[0] = totalBasic
        valuesArray[1] = totalPomodoro
        valuesArray[2] = totalTimebox
        valuesArray[3] = totalNonDesignate
        valuesArray[4] = totalStudy
        valuesArray[5] = totalWorkout
        valuesArray[6] = totalMeeting
        valuesArray[7] = totalPromise


        Log.d("adapter_yeon", "b:p:t = $totalBasic, $totalPomodoro, $totalTimebox")
        onStatisticsValues(valuesArray)

        */


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




        val setCellColors = TodoViewModel().getCurrentDay(day.toString())
        setCellColors.observe(activity) { it ->
            // when there is to do in current day
            //println("======================> ${it.size}")
            if(position == itemCount-1){
                onPieChart(pieList)
                onLineChart(lineList)
                onBarChart(barList)
            }

            if (it.size == 0)
            {
                //binding.cellRoot.setBackgroundColor(Color.GREEN)
            }

            // all to do datas in current cell
            // you can't get db data out of this block So work here
            else if (it.size > 0) {
                for (i in 0 until it.size) {
                    if(it[i].isTimer) {
                        categoryNum.add(it[i].categoryNum)
                        basicTimer.add(it[i].basicTimer)
                        pomodoro.add(it[i].pomodoro)
                        timeBox.add(it[i].timeBox)
                    }
                    //pieList add
                    //if(it[i].categoryNum != 0) {
                    //}
                    pieList.add(it[i].categoryNum)
                    //lineList add
                    lineList.add(it[i].pomodoro)
                    //barList add
                    barList.add(it[i].basicTimer)
                }
                Log.d("soo", "barListsize == ${barList.size}")

                        /*
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


                         */

                // calculate
                var startEndTImeInTodo = 0
                // total time of basic timers in one day
                for(i in 0 until startTime.size)
                {
                    if (startTime[i].toString() != "시작시간" && endTime[i].toString() != "종료시간") {
                        val (hs, ms) = startTime[i].split(":").map { it1 -> it1.toInt() }
                        val (he, me) = endTime[i].split(":").map { it2 -> it2.toInt() }
                        // convert hours to min and add
                        startEndTImeInTodo += (he * 60 + me) - (hs * 60 + ms)
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

                //show one day total basic time
                var h = basicTime/3600
                var m = (basicTime - h)/60
                var s = basicTime - h - m
                binding.StatisticsBasicTime.text = "%02d:%02d:%02d".format(h, m, s)


                // choose color depend on time that was set in to Do
                // or depend on timer type time

                /*
                if(startEndTImeInTodo < 120) {
                    binding.cellRoot.setBackgroundColor(Color.parseColor("#110000FF"))
                }
                else if (startEndTImeInTodo in 121..239)
                    binding.cellRoot.setBackgroundColor(Color.parseColor("#440000FF"))
                else if (startEndTImeInTodo in 241..359)
                    binding.cellRoot.setBackgroundColor(Color.parseColor("#770000FF"))
                else if(startEndTImeInTodo in 360..479)
                    binding.cellRoot.setBackgroundColor(Color.parseColor("#AA0000FF"))
                else if(startEndTImeInTodo > 480) {
                    println("in more 8 hours")
                    binding.cellRoot.setBackgroundColor(Color.parseColor("#FF0000FF"))
                }
                */
                if(basicTime in 1..3) {
                    binding.cellRoot.setBackgroundColor(Color.parseColor("#110000FF"))
                }
                else if (basicTime in 4..6)
                    binding.cellRoot.setBackgroundColor(Color.parseColor("#440000FF"))
                else if (basicTime in 7..9)
                    binding.cellRoot.setBackgroundColor(Color.parseColor("#770000FF"))
                else if(basicTime in 10..12)
                    binding.cellRoot.setBackgroundColor(Color.parseColor("#AA0000FF"))
                else if(basicTime > 12) {
                    println("in more 8 hours")
                    binding.cellRoot.setBackgroundColor(Color.parseColor("#FF0000FF"))
                }
            }


        }

    }

    override fun getItemCount(): Int = days.size


}