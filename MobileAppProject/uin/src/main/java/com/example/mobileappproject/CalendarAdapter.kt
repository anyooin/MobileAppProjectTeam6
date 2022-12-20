package com.example.mobileappproject

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.util.Log.w
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.core.view.get
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.R
import com.example.mobileappproject.databinding.DaysCellBinding
import com.example.mobileappproject.CalendarUtil
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate


class CalendarAdapter(private val days: MutableList<LocalDate?>, private var activity: LifecycleOwner, private var navigationView: NavigationView) :
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

    @SuppressLint("SetTextI18n")
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
        val dayDel = days[15].toString().split("-")[1] == day.toString().split("-")[1]

        // 날짜표시 & 오늘날짜 바탕색
        if (day == null) {
            binding.cellDayText.text = ""
            binding.todoCount.text = ""
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                binding.cellDayText.text = day.dayOfMonth.toString()
                //if(!dayDel) {
                //    binding.cellDayText.setTextColor(Color.LTGRAY)
                    //binding.cellDayText.setTypeface(null, Typeface.NORMAL)
                    //binding.cellRoot.setBackgroundColor(Color.LTGRAY)
                //}
            }
            println("selectPos == $selectPos")
            if(selectPos == position) {  //선택한 날짜라면-->?
                binding.cellRoot.setBackgroundResource(R.drawable.day_cell_select)
                binding.cellDayText.setTextColor(Color.WHITE)
                binding.todoCount.setTextColor(Color.WHITE)
            }else if (day.equals(CalendarUtil.today)) { //오늘날짜라면
                binding.cellRoot.setBackgroundResource(R.drawable.day_cell_today)
                //binding.cellDayText.setBackgroundResource(R.drawable.day_cell_select)
                binding.cellDayText.setTextColor(Color.WHITE)
                binding.todoCount.setTextColor(Color.WHITE)
            }
            else {
                binding.cellRoot.setBackgroundResource(0)
                binding.cellDayText.setTextColor(Color.BLACK)
                binding.todoCount.setTextColor(Color.parseColor("#5282FF"))

                // change weekend textColor
                if((position+1) % 7 == 0  && dayDel) {
                    binding.cellDayText.setTextColor(Color.BLUE)
                } else if (position == 0 || position % 7 == 0  && dayDel) {
                    binding.cellDayText.setTextColor(Color.RED)
                }

                if(!dayDel) {
                    binding.cellDayText.setTextColor(Color.LTGRAY)
                    binding.todoCount.setTextColor(Color.LTGRAY)
                }
            }
        }



        val navViewTotalTodo = navigationView.getHeaderView(0).findViewById<TextView>(R.id.total_todo_text)
        val navViewDone = navigationView.getHeaderView(0).findViewById<TextView>(R.id.num_of_todo_done_text)
        val navViewRemained = navigationView.getHeaderView(0).findViewById<TextView>(R.id.num_of_todo_remained_text)
        val navViewTotalDiary = navigationView.getHeaderView(0).findViewById<TextView>(R.id.total_diary_text)

        //set to do list title for day
        val todo = TodoViewModel().getCurrentDay(day.toString())
        todo.observe(activity) {
            var nonCheck = 0
            for(i in it) {
                if(i.isChecked == false){
                    nonCheck += 1
                }
            }
            if (nonCheck > 0) {
                binding.todoCount.text = nonCheck.toString()
            }
            else {
                binding.todoCount.text = ""
            }
        }


        TodoViewModel().readAllData.observe(activity) {
            var checked = 0
            var nonChecked = 0
            var total = 0
            for (i in it){
                if(!i.isChecked) {
                    nonChecked += 1
                }
                else if (i.isChecked) {
                    checked += 1
                }
            }
            total = checked + nonChecked
            navViewTotalTodo.text = total.toString() + " 개"
            navViewRemained.text = nonChecked.toString() + " 개"
            navViewDone.text = checked.toString() + " 개"
        }

        DiaryViewModel().diaryItemsList.observe(activity) {
            navViewTotalDiary.text = it.size.toString() + " 개"
        }

    }

    override fun getItemCount(): Int = days.size

}