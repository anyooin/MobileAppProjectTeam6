package com.example.mobileappproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.ActivityDiaryPageBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DiaryPageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var binding: ActivityDiaryPageBinding
    lateinit var diaryViewModel: DiaryViewModel
    lateinit var diaryAdapter: DiaryAdapter

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar setting
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.drawerNav, R.string.menu_item_open, R.string.menu_item_clos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        drawerLayout = binding.drawerNav
        navigationView = binding.naView
        navigationView.setNavigationItemSelectedListener(this)


        // set widgets in diary page
        val addButton = findViewById<FloatingActionButton>(R.id.addDiaryItem)
        val diaryItems = findViewById<RecyclerView>(R.id.diary_recycler_view)

        addButton.setOnClickListener {
            Log.d("qadridin", "add button in diary page clicked ")

            val intent = Intent(this, WritingDiaryPage::class.java).apply {
                putExtra("type", "ADD")
            }
            requestActivity.launch(intent)
        }

        diaryViewModel = ViewModelProvider(this)[DiaryViewModel::class.java]
        println("diary view model")

        diaryViewModel.diaryItemsList.observe(this) {
            diaryAdapter.update(it)
        }


        diaryAdapter = DiaryAdapter(this)
        diaryItems.layoutManager = LinearLayoutManager(this)
        diaryItems.adapter = diaryAdapter

        //should be implemented
        diaryAdapter.setItemClickListener(object: DiaryAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, itemId: Long) {
                println("Here in click card in diary page")
                Toast.makeText(this@DiaryPageActivity, "$itemId", Toast.LENGTH_SHORT).show()
                CoroutineScope(Dispatchers.IO).launch {
                    val diary = diaryViewModel.getOne(itemId)

                    val intent = Intent(this@DiaryPageActivity, WritingDiaryPage::class.java).apply {
                        putExtra("type", "EDIT")
                        putExtra("item", diary)
                    }
                    requestActivity.launch(intent)
                }
            }
        })
    }

    private val requestActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            val diary = it.data?.getSerializableExtra("diary") as Diary

            when(it.data?.getIntExtra("flag", -1)) {
                0 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        diaryViewModel.insert(diary)
                    }
                    Toast.makeText(this, "추가되었습니다.", Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        diaryViewModel.update(diary)
                    }
                    Toast.makeText(this ,"수정되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_item1-> {
                Toast.makeText(this,"Calendar 실행", Toast.LENGTH_SHORT).show()
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }
            R.id.menu_item2-> {
                Toast.makeText(this,"Timer 실행", Toast.LENGTH_SHORT).show()
                val timerIntent = Intent(this, TimerMainActivity::class.java)
                startActivity(timerIntent)}
            R.id.menu_item3-> Toast.makeText(this,"TodoList 실행", Toast.LENGTH_SHORT).show()
            R.id.menu_item4-> {
                Toast.makeText(this, "Diary 실행", Toast.LENGTH_SHORT).show()
                //val DiaryIntent = Intent(this, )
            }
            R.id.menu_item5-> Toast.makeText(this,"Statistics 실행", Toast.LENGTH_SHORT).show()
            R.id.menu_item6-> Toast.makeText(this,"Settings 실행", Toast.LENGTH_SHORT).show()
        }
        return false
    }
}