package com.example.mobileappproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class timerTodoListAdapter(val context: Context) : RecyclerView.Adapter<timerTodoListAdapter.TimerListViewHolder>(){

    private var list = mutableListOf<Todo>()

    inner class TimerListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.timertodoTitle)
        var startTime = itemView.findViewById<TextView>(R.id.timertodoTime)
        var content = itemView.findViewById<TextView>(R.id.timertodoContent)
        var date = itemView.findViewById<TextView>(R.id.timertodoDate)

        fun onBind(TodoT:String, TodoS:String, TodoE:String,TodoC:String, TodoD: String) {
            title.text = "title = ${TodoT}              "
            startTime.text = "time = ${TodoS} ~ ${TodoE}"
            content.text = "context = ${TodoC}"
            date.text = "date = ${TodoD}"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_timer_list_todo_popup, parent, false)
        return TimerListViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TimerListViewHolder, position: Int) {
        if(timerTodoSelectedDay.equals(list[position].date) || timerTodoSelectedDay.equals("None")) {
            var to = list[position]
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            holder.onBind(to.title, to.startTime, to.endTime, to.content, to.date)
        }
        else{
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            holder.onBind("-","-","-","-","-")
        }
    }

    fun update(newList: MutableList<Todo>) {
        this.list = newList
        notifyDataSetChanged()
    }
    fun updating(){
        notifyDataSetChanged()
    }
}