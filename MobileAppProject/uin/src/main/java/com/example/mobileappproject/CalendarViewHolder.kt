package com.example.mobileappproject

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.DaysCellBinding
import java.time.LocalDate

class CalendarViewHolder(val binding: DaysCellBinding):
    RecyclerView.ViewHolder(binding.root)
class CalendarAdapter(private val days: MutableList<LocalDate?>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.days_cell, parent, false)
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()

        return CalendarViewHolder(DaysCellBinding.bind(view))
      /*  CalendarViewHolder(
            DaysCellBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemListener
        )
       */
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CalendarViewHolder).binding

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

        binding.cellRoot.setOnClickListener {
            Log.d("onCalendarBindView", "$position day was clicked!")
        }

        // change weekend textColor
        if((position+1) % 7 == 0) {
            binding.cellDayText.setTextColor(Color.BLUE)
        } else if (position == 0 || position % 7 == 0) {
            binding.cellDayText.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int = days.size

    interface OnItemListener{
        fun onItemClick(position: Int, dayText: String)
    }

}