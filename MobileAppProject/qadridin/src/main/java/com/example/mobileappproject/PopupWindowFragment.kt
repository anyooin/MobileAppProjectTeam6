package com.example.mobileappproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView

import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.PopupWindowFragementBinding

class PopupWindowFragment(private var position: Int) : DialogFragment() {

    lateinit var binding: PopupWindowFragementBinding
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

        val cancel = view.findViewById<Button>(R.id.cancelBt)
        val next = view.findViewById<Button>(R.id.nextBt)
        val dateInPopup = view.findViewById<TextView>(R.id.dateInPopup)
        val addButton = view.findViewById<ImageView>(R.id.addPopItem)
        val toDoListContext = view.findViewById<RecyclerView>(R.id.toDoListContext)


        //add some memos by default for test
        // by start should access excel and read data with key date and add to memos
        val memos = mutableListOf<String>()
        memos.add("STUDY")
        memos.add("Workout")
        memos.add("Payment")
        setPopWindowAttr(memos, toDoListContext, dateInPopup)

        cancel.setOnClickListener {
            Log.d("qadridin", "clicked cancel button in popup Window")
            super.dismiss()
        }
        next.setOnClickListener {
            Log.d("qadridin", "clicked next button in popup Window")
            position += 1

            //Should to access excel file and read from table to do titles
            // to add to memos
            // var dataFromTable = Table(date or position)

            // For test
            val memos1 = mutableListOf<String>()
            memos1.add("Jogging")
            memos1.add("Swimming")
            memos1.add("Club")
            memos1.add("Sleep")

            setPopWindowAttr(memos1, toDoListContext, dateInPopup)
        }

        addButton.setOnClickListener {
            Log.d("qadridin", "clicked add button in popup Window")

            //Move to do list maker page
        }
    }

    fun setPopWindowAttr(memos: MutableList<String>, toDoListContext: RecyclerView, dateInPopup: TextView)  {
        dateInPopup.text = (position - 1).toString()

        // Set layout and to do list titles to show in popup Window
        toDoListContext.layoutManager = LinearLayoutManager(layoutInflater.context)
        toDoListContext.adapter = TodoItemAdapter(memos)
    }
}