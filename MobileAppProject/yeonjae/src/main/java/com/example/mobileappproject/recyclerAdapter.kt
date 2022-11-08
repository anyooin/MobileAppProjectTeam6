package com.example.mobileappproject

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.ActivityMainBinding
import com.example.mobileappproject.databinding.ItemMainBinding

class recyclerAdapter(val datas: MutableList<String>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class MyViewHolder(val binding: ItemMainBinding) :RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int =datas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MyViewHolder(ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolder).binding
        binding.itemData.text = datas[position]
        binding.itemRoot.setOnClickListener {
            Log.d("lee","item root click : $position")
        }
    }
}