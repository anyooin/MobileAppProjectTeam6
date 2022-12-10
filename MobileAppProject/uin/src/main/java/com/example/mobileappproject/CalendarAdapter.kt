package com.example.mobileappproject

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.R
import com.example.mobileappproject.databinding.DaysCellBinding
import com.example.mobileappproject.CalendarUtil
import java.time.LocalDate


class CalendarAdapter(private val days: MutableList<LocalDate?>, private var activity: LifecycleOwner) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var selectPos = -1  //선택한 날짜 위치

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
        layoutParams.height = (parent.height * 0.17).toInt()

        return CalendarViewHolder(DaysCellBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CalendarViewHolder).binding

        // itemClickListener 연결
        if (listener != null){
            binding.cellRoot.setOnClickListener(View.OnClickListener {
                var beforePos = selectPos
                selectPos = position
                notifyItemChanged(beforePos)
                notifyItemChanged(selectPos)
                listener?.onItemClick(it, position)
            })
        }
        val day : LocalDate? = days[position]

        // 날짜표시 & 오늘날짜 바탕색
        if (day == null) {
            binding.cellDayText.text = ""
            binding.todoTitle1.text = ""
            binding.todoTitle2.text = ""
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.cellDayText.text = day.dayOfMonth.toString()
            }
            println("selectPos == $selectPos")
            if(selectPos == position) {  //선택한 날짜라면-->?
                binding.cellRoot.setBackgroundResource(R.drawable.day_cell_select)
                binding.cellDayText.setTextColor(Color.WHITE)
            }else if (day.equals(CalendarUtil.today)) { //오늘날짜라면
                binding.cellRoot.setBackgroundResource(R.drawable.day_cell_today)
                binding.cellDayText.setTextColor(Color.WHITE)
            }
            else {
                binding.cellRoot.setBackgroundResource(R.drawable.day_cell_normal)
                binding.cellDayText.setTextColor(Color.BLACK)

                // change weekend textColor
                if((position+1) % 7 == 0) {
                    binding.cellDayText.setTextColor(Color.BLUE)
                } else if (position == 0 || position % 7 == 0) {
                    binding.cellDayText.setTextColor(Color.RED)
                }
            }
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