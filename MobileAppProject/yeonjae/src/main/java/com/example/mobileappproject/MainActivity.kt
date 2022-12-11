package com.example.mobileappproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

var switchOffOn = 0
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var calendar: RecyclerView
    lateinit var monthYear: TextView

    //navigation ADD
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var binding: ActivityMainBinding

    @SuppressLint("UseSwitchCompatOrMaterialCode", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar setting


        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.menu_item_open, R.string.menu_item_clos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

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

        if (switchOffOn == 1) {
            //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            println("Here in switch is checked =====================================")
            val date = monthYear.text.toString().split(" ")
            println("date ==== ${date[0]}")
            binding.drawer.background = when (date[0]) {
                "December", "January", "February" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                "March", "April", "May" -> resources.getDrawable(R.drawable.spring1_removebg_preview)
                "June", "July", "August" -> resources.getDrawable(R.drawable.summer1_removebg_preview)
                "September", "October", "November" -> resources.getDrawable(R.drawable.autumn1)
                else -> {
                    null
                }
            }
        } else {
            binding.drawer.background = null
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

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setBackgroundFrame()
    {
        if (switchOffOn == 1) {
            navigationView.menu.findItem(R.id.switch_menu).actionView.findViewById<SwitchCompat>(R.id.switchField).isChecked = true
            val date = CalendarUtil.today.toString().split("-")
            drawerLayout.background = when (date[1]) {
                "12", "01", "02" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                "03", "04", "05" -> resources.getDrawable(R.drawable.spring1_removebg_preview)
                "06", "07", "08" -> resources.getDrawable(R.drawable.summer1_removebg_preview)
                "09", "10", "11" -> resources.getDrawable(R.drawable.autumn1)
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
            val firstOfMonth = CalendarUtil.selectedDate.withDayOfMonth(1)
            val dayOfWeek = firstOfMonth.dayOfWeek.value

            println("day in month == $daysInMonth")
            println("firstOfMonth =- ${firstOfMonth}")
            println("dayOfWeek == ${dayOfWeek}")
            val daysInMonthArray: MutableList<LocalDate?> = mutableListOf()
            val loopRange = if (dayOfWeek < 7 && daysInMonth + dayOfWeek > 35) 1..42 else if (dayOfWeek == 7 && daysInMonth + dayOfWeek > 35) 8..42
            else 1..35
            for (i in loopRange) {
                //SHOULD BE DEVELOPED LATER
                if(i <= dayOfWeek) {
                    daysInMonthArray.add(LocalDate.of(CalendarUtil.selectedDate.year,
                    CalendarUtil.selectedDate.minusMonths(1).monthValue,
                        (CalendarUtil.selectedDate.minusMonths(1).lengthOfMonth()-dayOfWeek+i)))
                //daysInMonthArray.add((daysInMonth - dayOfWeek + i).toString())

                    //daysInMonthArray.add(null)
                } else if (i > daysInMonth + dayOfWeek) {
                    //      daysInMonthArray.add((i - daysInMonth + dayOfWeek).toString())
                   // daysInMonthArray.add(null)
                    daysInMonthArray.add(LocalDate.of(CalendarUtil.selectedDate.year,
                    CalendarUtil.selectedDate.plusMonths(1).monthValue,
                        (i - dayOfWeek - daysInMonth)))
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
            R.id.menu_item4-> {
                Toast.makeText(this, "Settings 실행", Toast.LENGTH_SHORT).show()
                val settingIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingIntent)
            }
            R.id.switch_menu-> {
                Toast.makeText(this, "Switch ON/Off", Toast.LENGTH_SHORT).show()
            }
        }


        return false
    }
}