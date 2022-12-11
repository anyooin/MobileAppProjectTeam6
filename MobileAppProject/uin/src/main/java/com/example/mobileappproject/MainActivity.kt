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
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.CalendarUtil.Companion.today
import com.example.mobileappproject.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

var date = ""
var switchOffOn = 0
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var calendar: RecyclerView
    lateinit var selectMonthYear: TextView

    //navigation ADD
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var binding: ActivityMainBinding

    lateinit var searchView: androidx.appcompat.widget.SearchView
    lateinit var tabLayout: TabLayout
    lateinit var todoTab: TabLayout.Tab
    lateinit var diaryTab: TabLayout.Tab
    lateinit var selectDayInMonth: MutableList<LocalDate?> // 화면에 보여지는 월 리스트
    var selectPosition = 0 //처음에는 오늘날짜위치, 그다음부터는 선택날짜위치

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar setting
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.menu_item_open, R.string.menu_item_clos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        //navigation
        drawerLayout = binding.drawer
        navigationView = binding.naView
        navigationView.menu.findItem(R.id.switch_menu).actionView.findViewById<SwitchCompat>(R.id.switchField)
            .setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    switchOffOn = 1
                    setBackgroundFrame()

                } else {
                    switchOffOn = 0
                    setBackgroundFrame()
                }
            }
        setBackgroundFrame()
        navigationView.itemIconTintList = null
        navigationView.setNavigationItemSelectedListener(this)

        //init widgets
        calendar = findViewById(R.id.daysView)
        selectMonthYear = findViewById(R.id.monthYear)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CalendarUtil.selectedDate = LocalDate.now()
            selectDayInMonth = daysInMonthArray(LocalDate.now())
            selectPosition = FinddayOfWeek(1)
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

        // setting tabs & searchView
        searchView = binding.searchView
        tabLayout = binding.tabs
        todoTab = tabLayout.newTab()
        todoTab.text = "TodoList"
        diaryTab = tabLayout.newTab()
        diaryTab.text = "Diary"
        tabLayout.addTab(todoTab)
        tabLayout.addTab(diaryTab)

        date = (selectDayInMonth[selectPosition]).toString()

        tabLayout.selectTab(todoTab)
        println("오늘날짜 : $selectPosition ")
        supportFragmentManager.beginTransaction().replace(R.id.tabContent, ToDoTab(selectPosition, selectDayInMonth, this@MainActivity, RESULT_OK, searchView)).commit()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // called when tab selected
                val transaction = supportFragmentManager.beginTransaction()
                when (tab?.text) {
                    "TodoList" -> {
                        //dateInPopupGlobal.text = dayInMonth[position].toString()
                        println("오늘날짜 : $selectPosition, ")
                        transaction.replace(
                            R.id.tabContent,
                            ToDoTab(selectPosition, selectDayInMonth, this@MainActivity, RESULT_OK, searchView)
                        )
                    }
                    "Diary" -> {
                        //dateInPopupGlobal.text = dayInMonth[position].toString()
                        transaction.replace(
                            R.id.tabContent,
                            DairyTab(selectPosition, selectDayInMonth, this@MainActivity, RESULT_OK, searchView)
                        )
                    }
                }
                transaction.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // called when tab unselected
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // called when a tab is reselected
            }
        })
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setMonthView() {
        selectMonthYear.text = monthYearFromDate(CalendarUtil.selectedDate)

        //background frame
        if (switchOffOn == 1) {
            //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            println("Here in switch is checked =====================================")
            val date = selectMonthYear.text.toString().split(" ")
            println("date ==== ${date[0]}")

            drawerLayout.background = when (date[0]) {
                "December", "January", "February" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                "March", "April", "May" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                "June", "July", "August" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                "September", "October", "November" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                else -> {
                    null
                }
            }
        } else {
            binding.drawer.background = null
        }

        selectDayInMonth = daysInMonthArray(CalendarUtil.selectedDate)
        val calendarAdapter = CalendarAdapter(selectDayInMonth, this@MainActivity)
        println("CalendarAdapter size is ${calendarAdapter.itemCount}")
        val layoutManager = GridLayoutManager(applicationContext, 7)
        calendar.layoutManager = layoutManager
        calendar.adapter = calendarAdapter

        // clickEvent
        calendarAdapter.setOnItemClickListener(object :
            CalendarAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Log.d("uin", "item click")

                //************Tab layout*********
                selectPosition = position
                date = (selectDayInMonth[selectPosition]).toString()
                tabLayout.selectTab(todoTab)
                val transaction = supportFragmentManager.beginTransaction()

                transaction.replace(
                    R.id.tabContent,
                    ToDoTab(selectPosition, selectDayInMonth, this@MainActivity,RESULT_OK, searchView)
                )
                transaction.commit()

                println("선택날짜: ${selectPosition}")

                //val popupFragment = PopupWindowFragment(position, dayInMonth, this@MainActivity, RESULT_OK, supportFragmentManager)
                //popupFragment.show(supportFragmentManager, "custom Dialog")
            }
        }
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setBackgroundFrame()
    {
        if (switchOffOn == 1) {
            navigationView.menu.findItem(R.id.switch_menu).actionView.findViewById<SwitchCompat>(R.id.switchField).isChecked = true
            val date = CalendarUtil.today.toString().split("-")
            drawerLayout.background = when (date[1]) {
                "12", "01", "02" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                "03", "04", "05" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                "06", "07", "08" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                "09", "10", "11" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                else -> {
                    null
                }
            }
        } else {
            navigationView.menu.findItem(R.id.switch_menu).actionView.findViewById<SwitchCompat>(R.id.switchField).isChecked = false
            drawerLayout.background= null

        }
    }

    private fun daysInMonthArray(date: LocalDate): MutableList<LocalDate?> {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val yearMonth = YearMonth.from(date)
            val daysInMonth = yearMonth.lengthOfMonth()
            //val firstOfMonth = CalendarUtil.selectedDate.withDayOfMonth(1)
            val dayOfWeek = FinddayOfWeek(0)

            println("day in month == $daysInMonth")
            //println("firstOfMonth =- ${firstOfMonth}")
            println("dayOfWeek == ${dayOfWeek}")
            val daysInMonthArray: MutableList<LocalDate?> = mutableListOf()
//            val loopRange = if (dayOfWeek < 7 && daysInMonth + dayOfWeek > 35) 1..42
//            else if (dayOfWeek == 7 && daysInMonth + dayOfWeek > 35) 8..42
//            else 1..35
            val loopRange = if (dayOfWeek == 7 && daysInMonth + dayOfWeek > 35) 8..42
            else 1..42
            for (i in loopRange) {
                if(i <= dayOfWeek && dayOfWeek != 0) {
                    daysInMonthArray.add(LocalDate.of(CalendarUtil.selectedDate.year,
                        CalendarUtil.selectedDate.minusMonths(1).monthValue,
                        (CalendarUtil.selectedDate.minusMonths(1).lengthOfMonth()-dayOfWeek+i)))
                    //daysInMonthArray.add((daysInMonth - dayOfWeek + i).toString())
                    //daysInMonthArray.add(null)
                } else if (i > daysInMonth + dayOfWeek) {
                    daysInMonthArray.add(LocalDate.of(CalendarUtil.selectedDate.year,
                        CalendarUtil.selectedDate.plusMonths(1).monthValue,
                        (i - dayOfWeek - daysInMonth)))
                    //daysInMonthArray.add((i - daysInMonth + dayOfWeek).toString())
                    //daysInMonthArray.add(null)
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
    private fun FinddayOfWeek(isToday : Int): Int{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val firstOfMonth = CalendarUtil.selectedDate.withDayOfMonth(1)
            var dayOfWeek = firstOfMonth.dayOfWeek.value

            if (isToday == 0) {
                return dayOfWeek
            }
            else {
                if (dayOfWeek == 7){
                    dayOfWeek = 0
                }
                val today = LocalDate.now()
                println("계산날짜: ${today.dayOfMonth + dayOfWeek - 1}")
                return today.dayOfMonth + dayOfWeek -  1
            }
        }
        return 0
    }

    private fun monthYearFromDate(date: LocalDate): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")
            return date.format(formatter)
        }
        return "Error in monthYearFromDate Function"
    }

    // *****************************Menu items part*******************
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
    @SuppressLint("UseCompatLoadingForDrawables")
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
            R.id.menu_item4-> Toast.makeText(this,"Settings 실행", Toast.LENGTH_SHORT).show()
            R.id.switch_menu-> {
                Toast.makeText(this, "Switch ON/Off", Toast.LENGTH_SHORT).show()
            }
        }
        return false
    }
}