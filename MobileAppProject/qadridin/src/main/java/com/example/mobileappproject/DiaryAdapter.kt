package com.example.mobileappproject

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DiaryAdapter(val context: Context): RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    private var list = mutableListOf<Diary>()

    inner class DiaryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<TextView>(R.id.diary_item_title)
        var desc = itemView.findViewById<TextView>(R.id.diary_item_desc)
        var image = itemView.findViewById<ImageView>(R.id.diary_item_image)


        @SuppressLint("SetTextI18n")
        fun onBind(data: Diary) {
            println("here in onBind")
            title.text = data.title
            desc.text = data.content
//            image.setImageResource(data.source)

            itemView.setOnClickListener {
                itemClickListner.onClick(it, layoutPosition, list[layoutPosition].id)
            }
        }
    }

    interface ItemClickListener {
        fun onClick(view: View, position: Int, itemId: Long)
    }

    private lateinit var itemClickListner: ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.diary_items, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(newList: MutableList<Diary>) {
        this.list = newList
        notifyDataSetChanged()
    }

}