package com.example.mobileappproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.FragmentDairyTabBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate


class DairyTab(private var position: Int, private var dayInMonth: MutableList<LocalDate?>, private val mainActivity: Activity, private val RESULT_OK: Int, private val searchView: androidx.appcompat.widget.SearchView): Fragment() {


    lateinit var binding: FragmentDairyTabBinding
    lateinit var diaryViewModel: DiaryViewModel
    lateinit var diaryAdapter: DiaryAdapter
    lateinit var diaryItem: RecyclerView
    lateinit var filteredList: MutableList<Diary>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDairyTabBinding.inflate(inflater, container, false)

        val addButton = binding.addDiaryItem
        diaryItem = binding.DiaryListContext

        addButton.setOnClickListener {
            Log.d("qadridin", "add button in diary page clicked ")
            val intent = Intent(mainActivity, WritingDiaryPageActivity::class.java).apply {
                putExtra("type", "ADD")
            }
            requestActivity.launch(intent)
            setDiaryItems(diaryItem)
        }
        //
        setDiaryItems(diaryItem)
        return binding.root
    }

    fun setDiaryItems(diaryItems: RecyclerView)
    {
        diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        println("diary view model")

        diaryViewModel.diaryInCurrentDayList.observe(viewLifecycleOwner) {
            diaryAdapter.update(it)
        }

        diaryAdapter = DiaryAdapter(mainActivity)
        diaryItems.layoutManager = LinearLayoutManager(mainActivity)
        diaryItems.adapter = diaryAdapter

        //should be implemented
        diaryAdapter.setItemClickListener(object: DiaryAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, itemId: Long) {
                println("Here in click card in diary page")
                Toast.makeText(mainActivity, "$itemId", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val diary = diaryViewModel.getOne(itemId)
                    val intent = Intent(mainActivity, WritingDiaryPageActivity::class.java).apply {
                        putExtra("type", "EDIT")
                        putExtra("item", diary)
                    }
                    requestActivity.launch(intent)
                }
                setDiaryItems(diaryItems)
            }
        })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(msg: String): Boolean {
                filter(msg)
                return false
            }

            override fun onQueryTextSubmit(msg: String): Boolean {
                // filter(msg)
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
        diaryViewModel.diaryInCurrentDayList.observe(viewLifecycleOwner) {
            for (item in it) {
                if ((item.title.toLowerCase().contains(text.toLowerCase())) || (item.content.toLowerCase().contains(text.toLowerCase()))){
                    filteredList.add(item)
                }
            }
            if (filteredList.isEmpty()) {
                Toast.makeText(mainActivity, "No Such Todo Found In This Date..", Toast.LENGTH_SHORT).show()
            }
            else {
                diaryAdapter.update(filteredList)
                diaryItem.layoutManager = LinearLayoutManager(mainActivity)
                diaryItem.adapter = diaryAdapter
            }
        }
    }

    private val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val diary = it.data?.getSerializableExtra("diary") as Diary

            when(it.data?.getIntExtra("flag", -1)) {
                0 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        diaryViewModel.insert(diary)
                    }
                    Toast.makeText(mainActivity, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        diaryViewModel.update(diary)
                    }
                    Toast.makeText(mainActivity ,"수정되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}