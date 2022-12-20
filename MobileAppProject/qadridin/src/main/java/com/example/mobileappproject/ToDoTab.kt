package com.example.mobileappproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mobileappproject.databinding.FragmentToDoTabBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Files.find
import java.time.LocalDate
import java.util.*
import java.util.Locale.filter

class ToDoTab(private var position: Int, private var dayInMonth: MutableList<LocalDate?>,
              private var mainActivity: Activity, private var RESULT_OK: Int, private var searchView: androidx.appcompat.widget.SearchView
) : Fragment() {

    lateinit var binding: FragmentToDoTabBinding
    lateinit var todoViewModel: TodoViewModel
    lateinit var todoAdapter: TodoAdapter
    lateinit var toDoListContext: RecyclerView
    lateinit var filteredList: MutableList<Todo>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentToDoTabBinding.inflate(inflater, container, false)

        val addButton = binding.addTodo
        toDoListContext = binding.toDoListContext

        addButton.setOnClickListener {
            Log.d("qadridin", "clicked add button in popup Window")
            //Move to do list maker page
            val intent = Intent(mainActivity, TodoPageActivity::class.java).apply {
                putExtra("type", "ADD")
            }
            requestActivity.launch(intent)
            setPopWindowAttr(toDoListContext)
        }

        setPopWindowAttr(toDoListContext)
        return binding.root
    }


    private val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val todo = it.data?.getSerializableExtra("todo") as Todo

            when(it.data?.getIntExtra("flag", -1)) {
                0 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        todoViewModel.insert(todo)
                    }
                    Toast.makeText(mainActivity, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        todoViewModel.update(todo)
                    }
                    Toast.makeText(mainActivity, "수정되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setPopWindowAttr(toDoListContext: RecyclerView)  {

        println("setPopWinAttr before todoViewModel")
        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        println("setPopWinAttr after todoViewModel")


        todoViewModel.todoList.observe(viewLifecycleOwner) {
            todoAdapter.update(it)
        }


        todoAdapter = TodoAdapter(mainActivity, dayInMonth[position].toString(), resources.getStringArray(R.array.category_list))
        toDoListContext.layoutManager = LinearLayoutManager(mainActivity)
        toDoListContext.adapter = todoAdapter


        todoAdapter.setItemCheckBoxClickListener(object: TodoAdapter.ItemCheckBoxClickListener {
            override fun onClick(view: View, position: Int, itemId: Long) {
                CoroutineScope(Dispatchers.IO).launch {
                    val todo = todoViewModel.getOne(itemId)
                    todo.isChecked = !todo.isChecked
                  //  filteredList = mutableListOf()
                    todoViewModel.update(todo)
                }
                setPopWindowAttr(toDoListContext)
            }
        })

        todoAdapter.setItemClickListener(object: TodoAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, itemId: Long) {
                //Toast.makeText(mainActivity, "$itemId", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val todo = todoViewModel.getOne(itemId)

                    val intent = Intent(mainActivity, TodoPageActivity::class.java).apply {
                        putExtra("type", "EDIT")
                        putExtra("item", todo)
                    }
                    requestActivity.launch(intent)
                }

                setPopWindowAttr(toDoListContext)
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(msg: String): Boolean {
                    filter(msg)
                    return false
            }

            override fun onQueryTextSubmit(msg: String): Boolean {
                searchView.setQuery("", false)
                searchView.clearFocus()
                searchView.onActionViewCollapsed()
                filter(msg)
                return false
            }

        })

    }


    private fun filter(text: String)
    {
        filteredList = mutableListOf()
        todoViewModel.todoList.observe(viewLifecycleOwner) {
            for (item in it) {
                if ((item.title.toLowerCase().contains(text.toLowerCase())) || (item.content.toLowerCase().contains(text.toLowerCase()))){
                  //  todoViewModel.update(item)
                    filteredList.add(item)
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(mainActivity, "No Such Todo Found In This Date..", Toast.LENGTH_SHORT).show()
            }
            else {
                 todoAdapter.update(filteredList)
               //  toDoListContext.layoutManager = LinearLayoutManager(mainActivity)
                // toDoListContext.adapter = todoAdapter
            }
        }

    }
}

