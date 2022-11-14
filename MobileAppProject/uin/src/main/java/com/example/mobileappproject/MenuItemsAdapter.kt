package com.example.mobileappproject

import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.ActivityMainBinding
import com.example.mobileappproject.databinding.MemosItemRecyclerviewBinding
import com.example.mobileappproject.databinding.MenuItemBinding

class MenuItemsAdapter(val data: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class MyViewHolder(val binding: MenuItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        MyViewHolder(MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("kkang", "onBindViewHolder : $position")
        val binding = (holder as MyViewHolder).binding
        //print view data
        binding.itemData.text = data[position]
        //add view event
        binding.itemRoot.setOnClickListener {
            Log.d("kkang", "item root click: $position")
        }
    }

}

// To do list 의 adapter 에 menu item 에 들어갔던 layout 을 그대로 씀
class TodoItemAdapter(val data: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class TodoViewHolder(val binding: MemosItemRecyclerviewBinding): RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        TodoItemAdapter.TodoViewHolder(
            MemosItemRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("kkang", "onBindViewHolder : $position")
        val binding = (holder as TodoItemAdapter.TodoViewHolder).binding
        //print view data
        binding.todoListItemData.text = data[position]

        // popup window에 있는 to do list title 누르면 전제 내용 보기
        binding.todolistItem.setOnClickListener {
            Log.d("kkang", "item root click: $position")
        }
    }

}