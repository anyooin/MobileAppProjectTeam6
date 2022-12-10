package com.example.mobileappproject

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
//import android.graphics.Color
import android.util.Log


class StatisticsCalendarAdapter(private val days: MutableList<LocalDate?>, private var activity: LifecycleOwner, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

    class CalendarViewHolder(val binding: DaysCellBinding):
        RecyclerView.ViewHolder(binding.root)

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
        val view: View = inflater.inflate(R.layout.days_cell, parent, false)
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()

        return CalendarViewHolder(DaysCellBinding.bind(view))
    }

    //override fun getItemCount_todo(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CalendarViewHolder).binding

        // itemClickListener 연결
        if (listener != null){
            binding.cellRoot.setOnClickListener(View.OnClickListener {
                listener?.onItemClick(it, position)
            })
        }

        // 날짜표시 & 오늘날짜 바탕색
        val day : LocalDate? = days[position]

        //타이머 시간들 받아오기
        /* var basictime = 0
         var pomodorotime = 0
         var timeboxtime = 0

         if(list[position].basicTimer.size != 0){
             basictime = list[position].basictime[list[position].basictime.size-1]
         }
 */



        if (day == null) {
            binding.cellDayText.text = ""

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.cellDayText.text = day.dayOfMonth.toString()

                //if(list[position].basicTimer)
                //less 2H
                binding.cellRoot.setBackgroundColor(Color.parseColor("#110000F"))
            }
            if (day.equals(CalendarUtil.today)) {
                binding.cellDayText.setBackgroundColor(Color.LTGRAY)
            }
        }

        // change weekend textColor
        if((position+1) % 7 == 0) {
            binding.cellDayText.setTextColor(Color.BLUE)
        } else if (position == 0 || position % 7 == 0) {
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
    }

    override fun getItemCount(): Int = days.size

}