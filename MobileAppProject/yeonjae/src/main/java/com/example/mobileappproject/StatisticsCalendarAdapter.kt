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
//import android.graphics.Color
import android.util.Log
import com.example.mobileappproject.databinding.StatisticsDaysCellBinding


class StatisticsCalendarAdapter(private val days: MutableList<LocalDate?>, private var activity: LifecycleOwner, private var monthYear : String) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = mutableListOf<Todo>()

    class CalendarViewHolder(val binding: StatisticsDaysCellBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.statistics_days_cell, parent, false)
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()

        return CalendarViewHolder(StatisticsDaysCellBinding.bind(view))
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val binding = (holder as CalendarViewHolder).binding

        // 날짜표시 & 오늘날짜 바탕색
        val day : LocalDate? = days[position]
        if (day == null) {
            binding.cellDayText.text = ""
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.cellDayText.text = day.dayOfMonth.toString()

                //if(list[position].basicTimer)
                //binding.statistic_cell.setBackgroundColor(Color.parseColor("#110000F"))

                if(list.size != 0) {
                    for (num in 0..list.size-1) {
                        if (list[num].date == day.toString()) {

                            var basic = list[num].basicTimer
                            if(basic == "0")
                                break

                            val h = basic.split(":")[0].toInt()
                            val m = basic.split(":")[1].toInt()
                            val s = basic.split(":")[2].toInt()

                            /*<color name="less2H_blue">#110000FF</color>
                        <color name="less4H_blue">#440000FF</color>
                        <color name="less6H_blue">#770000FF</color>
                        <color name="less8H_blue">#AA0000FF</color>
                        <color name="more8H_blue">#FF0000FF</color>*/

                            val time = h * 3600 + m * 60 + s
                            Log.d("dd","time = $time , ${day.dayOfMonth.toString()}")
                            if (time in 1..3) {
                                binding.statisticCell.setBackgroundColor(R.color.less2H_blue)
                                Log.d("dd","1..3 setBackGround")
                            }
                            else if (time in 4..6) {
                                binding.statisticCell.setBackgroundColor(R.color.less4H_blue)
                                Log.d("dd","4..6 setBackGround")
                            }
                            else if (time in 7..9) {
                                binding.statisticCell.setBackgroundColor(Color.parseColor("#ffff0000"))
                                Log.d("dd","7..9 setBackGround")
                            }
                            else if (time in 10..12) {
                                binding.statisticCell.setBackgroundColor(R.color.less8H_blue)
                                Log.d("dd","10..12 setBackGround")
                            }
                            else {
                                binding.statisticCell.setBackgroundColor(R.color.more8H_blue)
                                Log.d("dd","13... setBackGround")
                            }
                        }
                    }
                }
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
    }

    override fun getItemCount(): Int = days.size

    fun update(newList: MutableList<Todo>) {
        this.list = newList
        notifyDataSetChanged()
    }
}