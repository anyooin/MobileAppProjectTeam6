package com.example.mobileappproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var calendar: RecyclerView
    lateinit var monthYear: TextView

    //navigation ADD
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar setting
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.menu_item_open, R.string.menu_item_clos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        drawerLayout = binding.drawer
        navigationView = binding.naView
        navigationView.itemIconTintList = null
        navigationView.setNavigationItemSelectedListener(this)
        //navigation f

        //init widgets
        calendar = findViewById(R.id.daysView)
        monthYear = findViewById(R.id.monthYear)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CalendarUtil.selectedDate = LocalDate.now()
        }
        setMonthView()

        // prev month & year
        binding.minusYear.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusYears(1)
            }
            setMonthView()
        }
        binding.minusMonth.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusMonths(1)
            }
            setMonthView()
        }

        // next month & year
        binding.plusMonth.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusMonths(1)
            }
            setMonthView()
        }
        binding.plusYear.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusYears(1)
            }
            setMonthView()
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setMonthView() {
        monthYear.text = monthYearFromDate(CalendarUtil.selectedDate)

        //background frame
        val date = monthYear.text.toString().split(" ")
        if (switchOffOn == 1) {
            println(" ${monthYear.text}========================${date[1]}")
            drawerLayout.background = when (date[0]) {
                "December", "January", "February" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                "March", "April", "May" -> resources.getDrawable(R.drawable.spring1_removebg_preview)
                "June", "July", "August" -> resources.getDrawable(R.drawable.summer1_removebg_preview)
                "September", "October", "November" -> resources.getDrawable(R.drawable.autumn1)
                else -> {
                    null
                }
            }
        } else {
            drawerLayout.background = null
        }

        val daysInMonth = daysInMonthArray(CalendarUtil.selectedDate)
        val calendarAdapter = CalendarAdapter(daysInMonth, this@MainActivity)
        println("CalendarAdapter size is ${calendarAdapter.itemCount}")
        val layoutManager = GridLayoutManager(applicationContext, 7)
        calendar.layoutManager = layoutManager
        calendar.adapter = calendarAdapter

        // clickEvent
        calendarAdapter.setOnItemClickListener(object :
            CalendarAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Log.d("uin", "item click")

                val popupFragment = PopupWindowFragment(position, daysInMonth, this@MainActivity, RESULT_OK, supportFragmentManager)
                popupFragment.show(supportFragmentManager, "custom Dialog")
            }
        }
        )
    }

    private fun daysInMonthArray(date: LocalDate): MutableList<LocalDate?> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val yearMonth = YearMonth.from(date)
            val daysInMonth = yearMonth.lengthOfMonth()
            val firstOfMonth = CalendarUtil.selectedDate.withDayOfMonth(1)
            val dayOfWeek = firstOfMonth.dayOfWeek.value

            println("day in month == $daysInMonth")
            val daysInMonthArray: MutableList<LocalDate?> = mutableListOf()
            for (i in 1..42) {
                //SHOULD BE DEVELOPED LATER
                if(i <= dayOfWeek) {
                    //  daysInMonthArray.add((daysInMonth - dayOfWeek + i).toString())
                    daysInMonthArray.add(null)
                } else if (i > daysInMonth + dayOfWeek) {
                    //      daysInMonthArray.add((i - daysInMonth + dayOfWeek).toString())
                    daysInMonthArray.add(null)
                }
                else {
                    daysInMonthArray.add(LocalDate.of(CalendarUtil.selectedDate.year,
                        CalendarUtil.selectedDate.monthValue, i - dayOfWeek))
                }
            }
            println(daysInMonthArray)
            return daysInMonthArray
        }
        return ArrayList()
    }


    private fun monthYearFromDate(date: LocalDate): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
            return date.format(formatter)
        }
        return "Error in monthYearFromDate Function"
    }

    // Menu items part
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val menuItem = menu?.findItem(R.id.menu_search)
        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean = true

            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("kkang", "searched word: $query")
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_item1-> Toast.makeText(this,"Calendar 실행", Toast.LENGTH_SHORT).show()
            R.id.menu_item2-> {
                Toast.makeText(this,"Timer 실행", Toast.LENGTH_SHORT).show()
                val timerIntent = Intent(this, TimerMainActivity::class.java)
                startActivity(timerIntent)
            }
            R.id.menu_item3-> {
                Toast.makeText(this,"Statistics 실행", Toast.LENGTH_SHORT).show()
                val statisticsIntent = Intent(this, StatisticsMainActivity::class.java)
                startActivity(statisticsIntent)
            }
            R.id.menu_item4-> {
                Toast.makeText(this, "Settings 실행", Toast.LENGTH_SHORT).show()
                val settingIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingIntent)
            }
        }
        return false
    }
}