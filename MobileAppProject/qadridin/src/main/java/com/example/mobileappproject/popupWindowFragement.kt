package com.example.mobileappproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileappproject.databinding.PopupWindowFragementBinding

class popupWindowFragement(private val position: Int) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        println("Here in popupWindow")
        val binding = PopupWindowFragementBinding.inflate(inflater, container, false)

    /*    binding.popWindow.visibility = View.VISIBLE
        binding.cancelBt.setOnClickListener {
            Log.d("cancel", "clicked cancel button in popUpWIndow")
        }
        binding.dateInPopup.text = (position+1).toString()
        binding.nextBt.setOnClickListener {
            Log.d("next button", "clicked next button in popup win")
        }
        val memos = mutableListOf<String>()
        memos.add("Study")
        memos.add("Workout")
        memos.add("Payment")

        val layoutManager = LinearLayoutManager(activity)
        binding.toDoListContext.layoutManager = layoutManager
        val adapter= PopupWindowAdapter(memos)
        binding.toDoListContext.adapter = adapter
     */
        return binding.root
    }


    //popUp window items
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancel = view.findViewById<Button>(R.id.cancelBt)
        val next = view.findViewById<Button>(R.id.nextBt)
        val dateInPopup = view.findViewById<TextView>(R.id.dateInPopup)
        val addButton = view.findViewById<ImageView>(R.id.addPopItem)
        val binding = PopupWindowFragementBinding.inflate(layoutInflater)

        //add some memos by default for test
        val memos = mutableListOf<String>()
        memos.add("Study")
        memos.add("Workout")
        memos.add("Payment")

       // binding.toDoListContext.layoutManager = LinearLayoutManager()
        // Set adapter for memos context
        binding.toDoListContext.adapter = TodoItemAdapter(memos)

        cancel.setOnClickListener {
            Log.d("qadridin", "clicked cancel button in popupwindow")
        }
        next.setOnClickListener {
            Log.d("qadridin", "clicked next button in popupwindow")
        }
        dateInPopup.text = (position + 1).toString()

        addButton.setOnClickListener {
            Log.d("qadridin", "clicked add button in popupwindow")

        }
    }


}