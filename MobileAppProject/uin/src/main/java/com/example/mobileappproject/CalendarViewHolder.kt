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
class CalendarAdapter(private val days: MutableList<String>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var selectedDate: LocalDate =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now()
    } else {
        TODO("VERSION.SDK_INT < O")
    }

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
        binding.cellDayText.text = days[position]
        binding.cellRoot.setOnClickListener {
            Log.d("onCalendarBindView", "$position day was clicked!")
        }

        val day : String = days.get(position)

        if (day == null) {
            binding.cellDayText.setText("")
        } else {
            if (day.equals(selectedDate)) {
                binding.cellDayText.setBackgroundColor(Color.LTGRAY)
            }
        }

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