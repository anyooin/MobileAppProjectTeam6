package com.example.mobileappproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mobileappproject.databinding.PopupWindowFragementBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TAB_LABEL_VISIBILITY_LABELED
import java.time.LocalDate

var date = ""
class PopupWindowFragment(private var position: Int, private var dayInMonth: MutableList<LocalDate?>,
                          private var mainActivity: Activity, private var RESULT_OK: Int,
                          private var supportFragmentManager: FragmentManager) : DialogFragment() {

    lateinit var binding: PopupWindowFragementBinding
   // lateinit var todoViewModel: TodoViewModel
   // lateinit var todoAdapter: TodoAdapter

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

        // set dialog frame size
        if (dialog != null) dialog!!.window!!.setLayout(
            350.toPx(requireContext()),
            520.toPx(requireContext())
        )

        cancel.setOnClickListener {
            Log.d("qadridin", "clicked cancel button in popup Window")
            super.dismiss()
        }

        dateInPopupGlobal.text = (dayInMonth[position]).toString()
        date = dateInPopupGlobal.text.toString()

        // setting tabs
        val tabLayout = binding.tabs
        val todoTab = tabLayout.newTab()
        todoTab.text = "TodoList"
        val diaryTab = tabLayout.newTab()
        diaryTab.text = "Diary"
        tabLayout.addTab(todoTab)
        tabLayout.addTab(diaryTab)

        tabLayout.selectTab(todoTab)
        childFragmentManager.beginTransaction().replace(R.id.tabContent, ToDoTab(position, dayInMonth, mainActivity, RESULT_OK)).commit()

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
            // called when tab selected
                val transaction = childFragmentManager.beginTransaction()
                when (tab?.text) {
                    "TodoList" -> {
                        dateInPopupGlobal.text = dayInMonth[position].toString()
                        transaction.replace(R.id.tabContent, ToDoTab(position, dayInMonth, mainActivity, RESULT_OK))
                    }
                    "Diary" -> {
                        dateInPopupGlobal.text = "Diary"
                        transaction.replace(R.id.tabContent, DairyTab(mainActivity, RESULT_OK))
                    }
                }
                transaction.commit()

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            // called when tab unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            // called when a tab is reselected
                val transaction = childFragmentManager.beginTransaction()
                when (tab?.text) {
                    "TodoList" -> {
                        dateInPopupGlobal.text = dayInMonth[position].toString()
                        transaction.replace(R.id.tabContent, ToDoTab(position, dayInMonth, mainActivity, RESULT_OK))
                    }
                    "Diary" -> {
                        dateInPopupGlobal.text = "Diary"
                        transaction.replace(R.id.tabContent, DairyTab(mainActivity, RESULT_OK))
                    }
                }
                transaction.commit()
            }
        })
    }

    private fun Int.toPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()
}