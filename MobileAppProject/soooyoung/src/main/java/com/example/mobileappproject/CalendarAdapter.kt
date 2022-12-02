package com.example.mobileappproject

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.R
import com.example.mobileappproject.databinding.DaysCellBinding
import com.example.mobileappproject.CalendarUtil
import java.time.LocalDate


class CalendarAdapter(private val days: MutableList<LocalDate?>, private var activity: LifecycleOwner) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

        if (day == null) {
            binding.cellDayText.text = ""
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.cellDayText.text = day.dayOfMonth.toString()
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