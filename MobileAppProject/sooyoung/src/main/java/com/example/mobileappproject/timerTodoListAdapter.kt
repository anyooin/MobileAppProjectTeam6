package com.example.mobileappproject

import android.app.Activity
import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.ActivityTimerListTodoPopupBinding
import com.example.mobileappproject.databinding.ActivityTimerMainBinding

class timerTodoListAdapter(val context: Context) : RecyclerView.Adapter<timerTodoListAdapter.TimerListViewHolder>(){

    private var list = mutableListOf<Todo>()

    //class TimerListViewHolder(val binding : ActivityTimerListTodoPopupBinding) : RecyclerView.ViewHolder(binding.root)

    inner class TimerListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.timertodoTitle)
        var startTime = itemView.findViewById<TextView>(R.id.timertodoTime)
        var content = itemView.findViewById<TextView>(R.id.timertodoContent)
        var date = itemView.findViewById<TextView>(R.id.timertodoDate)

        fun onBind(data: Todo) {
            title.text = "title = ${data.title}              "
            startTime.text = "time = ${data.startTime} ~ ${data.endTime}"
            content.text = "context = ${data.content}"
            date.text = "date = ${data.date}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerListViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_timer_list_todo_popup, parent, false)
        return TimerListViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TimerListViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    fun update(newList: MutableList<Todo>) {
        this.list = newList
        notifyDataSetChanged()
    }
}