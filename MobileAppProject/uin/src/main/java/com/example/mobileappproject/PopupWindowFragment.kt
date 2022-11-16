package com.example.mobileappproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.PopupWindowFragementBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class PopupWindowFragment(private var position: Int, private var dayInMonth: MutableList<LocalDate?>,
                          private var mainActivity: Activity, private var RESULT_OK: Int) : DialogFragment() {

    lateinit var binding: PopupWindowFragementBinding
    lateinit var todoViewModel: TodoViewModel
    lateinit var todoAdapter: TodoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("Here in popupWindow")
        binding = PopupWindowFragementBinding.inflate(inflater, container, false)

        //View Model on Main Create
        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]


        return binding.root
    }

    //popUp window items
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancel = view.findViewById<Button>(R.id.cancelBt)
        val next = view.findViewById<Button>(R.id.nextBt)
        val dateInPopup = view.findViewById<TextView>(R.id.dateInPopup)
        val addButton = view.findViewById<ImageView>(R.id.addTodo)
        val toDoListContext = view.findViewById<RecyclerView>(R.id.toDoListContext)


        setPopWindowAttr(toDoListContext, dateInPopup)

        cancel.setOnClickListener {
            Log.d("qadridin", "clicked cancel button in popup Window")
            super.dismiss()
        }
        next.setOnClickListener {
            Log.d("qadridin", "clicked next button in popup Window")
            position += 1
            setPopWindowAttr(toDoListContext, dateInPopup)
        }

        addButton.setOnClickListener {
            Log.d("qadridin", "clicked add button in popup Window")

            //Move to do list maker page
            val intent = Intent(mainActivity, TodoPageActivity::class.java).apply {
                putExtra("type", "ADD")
            }
            requestActivity.launch(intent)
        }

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



    private fun setPopWindowAttr(toDoListContext: RecyclerView, dateInPopup: TextView)  {
        dateInPopup.text = (dayInMonth[position]).toString()

        todoViewModel.todoList.observe(this) {
            todoAdapter.update(it)
        }

        todoAdapter = TodoAdapter(mainActivity, dayInMonth[position].toString())
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