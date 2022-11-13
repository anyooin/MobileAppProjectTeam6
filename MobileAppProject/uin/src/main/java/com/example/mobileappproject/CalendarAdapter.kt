package com.example.mobileappproject

import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatViewInflater
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.MainActivity
import com.example.mobileappproject.databinding.DaysCellBinding
import com.example.mobileappproject.databinding.PopupWindowFragementBinding
import kotlinx.coroutines.NonDisposableHandle.parent
import java.time.LocalDate
import kotlin.contracts.contract

//class CalendarViewHolder(val binding: DaysCellBinding):
//    RecyclerView.ViewHolder(binding.root)

class CalendarAdapter(private val days: MutableList<LocalDate?>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class CalendarViewHolder(val binding: DaysCellBinding):
        RecyclerView.ViewHolder(binding.root)

    interface OnItemClickListener{ //인터페이스
        fun onItemClick(view: View, position: Int)
    }
    var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

//    interface OnItemListener{
//        fun onItemClick(position: Int, dayText: String)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.days_cell, parent, false)
        val layoutParams: ViewGroup.LayoutParams = view.layoutParams
        layoutParams.height = (parent.height * 0.166666666).toInt()

        return CalendarViewHolder(DaysCellBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as CalendarViewHolder).binding

        //holder.bind(data!![position])
        if (listener != null){
            binding.cellRoot.setOnClickListener(View.OnClickListener {
                listener?.onItemClick(it, position)
            })
        }
//        // Cell 누를 때 popup window 띄어야함
//        binding.cellRoot.setOnClickListener {
//            Log.d("onCalendarBindView", "$position day was clicked!")
//
//            val popupFragment = popupWindowFragement(position)
//            // 여기 에 Fragment manager 이나 activity 접속 안돼서 연결 안됐서요.
//
//            //popupFragment.show((activity as FragmentActivity).supportFragmentManager, "custom Dialog")
//        }

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
            binding.cellDayText.setTextColor(Color.RED)
        } else if (position == 0 || position % 7 == 0) {
            binding.cellDayText.setTextColor(Color.RED)
        }
    }

    override fun getItemCount(): Int = days.size

}