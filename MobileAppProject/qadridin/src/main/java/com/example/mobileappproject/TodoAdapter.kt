package com.example.mobileappproject

import android.content.Context
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(val context: Context, val currentDay: String): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var list = mutableListOf<Todo>()

    inner class TodoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<TextView>(R.id.todoListItem_data)
        var checkbox = itemView.findViewById<CheckBox>(R.id.checkDone)
        var timestamp = itemView.findViewById<TextView>(R.id.todoListItem_timeStamp)

        fun onBind(data: Todo) {
            title.text = data.title
            checkbox.isChecked = data.isChecked
            timestamp.text = data.timestamp

            if (data.isChecked) {
                title.paintFlags = title.paintFlags or STRIKE_THRU_TEXT_FLAG
            } else {
                title.paintFlags = title.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            }

            checkbox.setOnClickListener {
                itemCheckBoxClickListener.onClick(it, layoutPosition, list[layoutPosition].id)
            }

            itemView.setOnClickListener {
                itemClickListner.onClick(it, layoutPosition, list[layoutPosition].id)
            }

        }
    }

    interface ItemClickListener {
        fun onClick(view: View,  position: Int, itemId: Long)
    }

    private lateinit var itemClickListner: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.memos_item_recyclerview, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(newList: MutableList<Todo>) {
        this.list = newList
        notifyDataSetChanged()
    }

    interface ItemCheckBoxClickListener {
        fun onClick(view: View, position: Int, itemId: Long)
    }

    private lateinit var itemCheckBoxClickListener: ItemCheckBoxClickListener

    fun setItemCheckBoxClickListener(itemCheckBoxClickListener: ItemCheckBoxClickListener) {
        this.itemCheckBoxClickListener = itemCheckBoxClickListener
    }
}