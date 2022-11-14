package com.example.mobileappproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.MemosItemRecyclerviewBinding

/*
class PopupWindowHolder(val binding: MemosItemRecyclerviewBinding): RecyclerView.ViewHolder(binding.root)

class PopupWindowAdapter(val datas: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    override fun getItemCount(): Int{
        return datas.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = PopupWindowHolder(
        MemosItemRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding=(holder as PopupWindowHolder).binding
        binding.todoListItemData.text= datas[position]
    }
}

 */