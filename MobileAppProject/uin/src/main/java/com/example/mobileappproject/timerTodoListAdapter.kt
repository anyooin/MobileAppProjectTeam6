package com.example.mobileappproject

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.ActivityTimerListTodoPopupBinding
import com.example.mobileappproject.timerTodoListAdapter.TimerListViewHolder
import kotlinx.coroutines.NonDisposableHandle.parent

class timerTodoListAdapter(
    val context: Context,
    val onShowDB: (Array<String>) -> Unit,
    val onSelectTimer : (Long) -> Unit,
    val onClick : (Array<String>) -> Unit
) : RecyclerView.Adapter<TimerListViewHolder>(){

    private var list = mutableListOf<Todo>()
    var preView: View? = null
    private var preId : String = "-1"

    inner class TimerListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.timertodoTitle)
        var startTime = itemView.findViewById<TextView>(R.id.timertodoTime)
        var content = itemView.findViewById<TextView>(R.id.timertodoContent)
        var category = itemView.findViewById<TextView>(R.id.timertodoCategoty)

        fun onBind(TodoT:String, TodoS:String, TodoE:String,TodoC:String, TodoD: String) {
            title.text = "${TodoT}              "
            if(TodoS == "시작시간" || TodoE == "종료시간"){
                startTime.text = "하루종일"
            }else {
                startTime.text = "${TodoS} ~ ${TodoE}"
            }
            content.text = "${TodoC}"
            category.text = "${TodoD}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_timer_list_todo_popup, parent, false)
        return TimerListViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: TimerListViewHolder, position: Int) {

        if(!list[position].isTimer){ //isTimer == false
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            holder.onBind("-","-","-","-","-")
        }
        else if(timerTodoSelectedDay.equals(list[position].date) || timerTodoSelectedDay.equals("ALL")) {
            // 선택된 date || date == None
            val to = list[position]
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams =
                RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            //val items: Array<String> = resources.getStringArray(R.array.category_list) 왜 오류나지
            val items = arrayOf("지정안함", "공부", "운동", "회의", "약속")

            items[to.categoryNum]
            holder.onBind(to.title, to.startTime, to.endTime, to.content, items[to.categoryNum])
        }
        else{
            // 선택된 date가 아닌 경우
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            holder.onBind("-","-","-","-","-")
        }

        holder.itemView.findViewById<Button>(R.id.timertodoBtn).setOnClickListener {
            val to = list[position]
            val timeArray = arrayOf(to.basicTimer, to.pomodoro, to.timeBox)
            onShowDB(timeArray)
        }

        holder.itemView.findViewById<Button>(R.id.timerselectBtn).setOnClickListener {
            val to = list[position]
            val timeArray = arrayOf(to.id.toString(), to.basicTimer, to.pomodoro, to.timeBox)


            if(preId == to.id.toString()) {
                //it.setBackgroundColor(Color.parseColor("#000000"))
                it.setBackgroundResource(R.drawable.day_cell_today)
                timeArray[0] = "-1"
                preId = "-1"
            }
            else {
                //preView?.setBackgroundColor(Color.parseColor("#000000"))

                preView?.setBackgroundResource(R.drawable.day_cell_today)
                //it.setBackgroundColor(Color.parseColor("#D0A4ED"))
                it.setBackgroundResource(R.drawable.day_cell_select)
                preId = to.id.toString()
            }
            onClick(timeArray)
            onSelectTimer(list[position].id)
            preView = it
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