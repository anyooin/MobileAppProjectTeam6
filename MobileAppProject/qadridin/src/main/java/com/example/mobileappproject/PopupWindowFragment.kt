package com.example.mobileappproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AsyncPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.PopupWindowFragementBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

var date = ""
class PopupWindowFragment(private var position: Int, private var dayInMonth: MutableList<LocalDate?>,
                          private var mainActivity: Activity, private var RESULT_OK: Int,
                          private var supportFragmentManager: FragmentManager) : DialogFragment() {

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

        return binding.root
    }

    //popUp window items
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("On view Created ")
        val cancel = view.findViewById<Button>(R.id.cancelBt)
        val dateInPopupGlobal = view.findViewById<TextView>(R.id.dateInPopup)
        val addButton = view.findViewById<ImageView>(R.id.addTodo)
        val toDoListContext = view.findViewById<RecyclerView>(R.id.toDoListContext)

        if (dialog != null) dialog!!.window!!.setLayout(
            300.toPx(requireContext()),
            450.toPx(requireContext())
        )

        cancel.setOnClickListener {
            Log.d("qadridin", "clicked cancel button in popup Window")
            super.dismiss()
        }

        addButton.setOnClickListener {
            Log.d("qadridin", "clicked add button in popup Window")

            //Move to do list maker page
            val intent = Intent(mainActivity, TodoPageActivity::class.java).apply {
                putExtra("type", "ADD")
            }
            requestActivity.launch(intent)
        }

        setPopWindowAttr(toDoListContext, dateInPopupGlobal)
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



    private fun setPopWindowAttr(toDoListContext: RecyclerView, dateInPopupGlobal: TextView)  {
        dateInPopupGlobal.text = (dayInMonth[position]).toString()
        date = dateInPopupGlobal.text.toString()
        println("setPopWinAttr before todoViewModel")
        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        println("setPopWinAttr after todoViewModel")


        todoViewModel.todoList.observe(this) {
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

    private fun Int.toPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

}