package com.example.mobileappproject

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.mobileappproject.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var calendar: RecyclerView
    lateinit var monthYear: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar setting
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.menu_item_open, R.string.menu_item_clos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        // Menu items
        val data = mutableListOf("Calendar", "Timer", "Todo List", "Statistics", "Settings")

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = MenuItemsAdapter(data)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

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

    private fun setMonthView() {
        monthYear.text = monthYearFromDate(CalendarUtil.selectedDate)
        val daysInMonth = daysInMonthArray(CalendarUtil.selectedDate)
        //
      //  val todo = TodoViewModel().getCurrentDay(day.toString())
        // println(todo.date)
        // println(todo)
        //todo.observe(this) { findViewById<TextView>().todoTitle1.text = it[0].title.toString() }

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

}