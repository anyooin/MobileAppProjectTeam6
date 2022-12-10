package com.example.mobileappproject

import com.example.mobileappproject.R
import com.example.mobileappproject.Todo
import com.example.mobileappproject.timerTodoSelectedDay
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.ActivityTimerListTodoPopupBinding

class timerTodoListAdapter(val context: Context) :
    RecyclerView.Adapter<timerTodoListAdapter.TimerListViewHolder>() {

    private var list = mutableListOf<Todo>()
    var preView: View? = null

    inner class TimerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.timertodoTitle)
        var startTime = itemView.findViewById<TextView>(R.id.timertodoTime)
        var content = itemView.findViewById<TextView>(R.id.timertodoContent)
        var date = itemView.findViewById<TextView>(R.id.timertodoDate)

        fun onBind(TodoT: String, TodoS: String, TodoE: String, TodoC: String, TodoD: String) {
            title.text = "title = ${TodoT}              "
            startTime.text = "time = ${TodoS} ~ ${TodoE}"
            content.text = "context = ${TodoC}"
            date.text = "date = ${TodoD}"

            itemView.setOnClickListener {
                itemClickListener.onClick(preView, it, layoutPosition, list[layoutPosition].id)
                preView = it
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_timer_list_todo_popup, parent, false)
        return TimerListViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TimerListViewHolder, position: Int) {
        if (!list[position].isTimer) { //isTimer == false
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            holder.onBind("-", "-", "-", "-", "-")
        } else if (timerTodoSelectedDay.equals(list[position].date) || timerTodoSelectedDay.equals("ALL")) {
            // 선택된 date || date == None
            var to = list[position]
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            holder.onBind(to.title, to.startTime, to.endTime, to.content, to.date)
        } else {
            // 선택된 date가 아닌 경우
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            holder.onBind("-", "-", "-", "-", "-")
        }
    }

    fun update(newList: MutableList<Todo>) {
        this.list = newList
        notifyDataSetChanged()
    }

    fun updating() {
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onClick(preView: View?, view: View, position: Int, itemId: Long)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }
}