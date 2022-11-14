package com.example.mobileappproject

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.ActivityMainBinding
import com.example.mobileappproject.databinding.ItemMainBinding
import kotlinx.coroutines.selects.select

class timerListAdapter(val datas: MutableList<String>,
                       val onClickRemoveButton: (position : Int) -> Unit,
                       val onClickSelectItem: (position : Int) -> Unit,
                       val onTimerItem: (timerMode: TextView) -> Unit,
                       val onTimeRecord: (timeRecord: TextView)->Unit)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class MyViewHolder(val binding: ItemMainBinding) :RecyclerView.ViewHolder(binding.root)
    override fun getItemCount(): Int =datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MyViewHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        binding.itemData.text = datas[position]
        binding.itemData.setOnClickListener {
            onClickSelectItem.invoke(position)
            onTimerItem(binding.timerMode)
            onTimeRecord(binding.timerRecord)
        }
        binding.timerListRemoveBtn.setOnClickListener {
            onClickRemoveButton.invoke(position)
            notifyItemRemoved(position)
            notifyDataSetChanged()
        }
    }
}