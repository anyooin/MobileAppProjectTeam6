package com.example.mobileappproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mobileappproject.databinding.FragmentToDoTabBinding
import com.example.mobileappproject.databinding.PopupWindowFragementBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Files.find
import java.time.LocalDate

class ToDoTab(private var position: Int, private var dayInMonth: MutableList<LocalDate?>,
              private var mainActivity: Activity, private var RESULT_OK: Int
) : Fragment() {

    lateinit var binding: FragmentToDoTabBinding
    lateinit var todoViewModel: TodoViewModel
    lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentToDoTabBinding.inflate(inflater, container, false)

        val addButton = binding.addTodo
        val toDoListContext = binding.toDoListContext

        addButton.setOnClickListener {
            Log.d("qadridin", "clicked add button in popup Window")

            //Move to do list maker page
            val intent = Intent(mainActivity, TodoPageActivity::class.java).apply {
                putExtra("type", "ADD")
            }
            requestActivity.launch(intent)
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
                    todoViewModel.update(todo)
                }
            }
        })

        todoAdapter.setItemClickListener(object: TodoAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, itemId: Long) {
                Toast.makeText(mainActivity, "$itemId", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val todo = todoViewModel.getOne(itemId)

                    val intent = Intent(mainActivity, TodoPageActivity::class.java).apply {
                        putExtra("type", "EDIT")
                        putExtra("item", todo)
                    }
                    requestActivity.launch(intent)
                }
            }
        })
    }

}
