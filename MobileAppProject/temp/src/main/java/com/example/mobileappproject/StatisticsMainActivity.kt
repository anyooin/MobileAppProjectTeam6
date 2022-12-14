package com.example.mobileappproject
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.XAxis
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarEntry
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.*

import com.example.mobileappproject.databinding.ActivityMainBinding
import com.example.mobileappproject.databinding.ActivityStatisticsMainBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomsheet.BottomSheetDialog

import com.google.android.material.navigation.NavigationView
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
class StatisticsMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var calendar: RecyclerView
    lateinit var monthYear: TextView

    //navigation ADD
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStatisticsMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Toolbar setting
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.statisticsdrawer, R.string.menu_item_open, R.string.menu_item_clos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        // Menu items
        /*
        val data = mutableListOf("Calendar", "Timer", "Todo List", "Statistics", "Settings")

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = MenuItemsAdapter(data)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )*/
        drawerLayout = binding.statisticsdrawer
        navigationView = binding.statisticsnaView
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

        //bar chart
        var barChart: BarChart = findViewById(R.id.barChart)// barChart ??????

        //?????? ?????????
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1.2f,3.0f))
        entries.add(BarEntry(2.2f,6.0f))
        entries.add(BarEntry(3.2f,5.0f))
        entries.add(BarEntry(4.2f,9.0f))
        entries.add(BarEntry(5.2f,8.0f))
        entries.add(BarEntry(6.2f,3.0f))
        entries.add(BarEntry(7.2f,5.0f))

        barChart.run {
            description.isEnabled = false // ?????? ?????? ????????? ???????????? description??? ???????????? ?????? (false)
            setMaxVisibleValueCount(7) // ?????? ????????? ????????? ????????? 7?????? ??????
            setPinchZoom(false) // ?????????(?????????????????? ?????? ??? ???????????????) ??????
            setDrawBarShadow(false) //???????????? ?????????
            setDrawGridBackground(false)//???????????? ????????????
            axisLeft.run { //?????? ???. ??? Y?????? ?????? ?????????.
                axisMaximum = 13f //12 ????????? ?????? ????????? ?????? 13f??? ???????????? ??????
                axisMinimum = 0f // ????????? 0
                granularity = 1f // 1?????? ?????? ?????? ???????????? ??????.
                setDrawLabels(true) // ??? ????????? ?????? (0, 50, 100)
                setDrawGridLines(true) //?????? ?????? ??????
                setDrawAxisLine(false) // ??? ????????? ??????
                axisLineColor = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_secondary_variant) // ??? ?????? ??????
                gridColor = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_on_secondary) // ??? ?????? ?????? ?????? ??????
                textColor = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_primary_dark) // ?????? ????????? ?????? ??????
                textSize = 13f //?????? ????????? ??????
            }

            // X??? ?????????(?????? ???????????? ??????) ???????????? ?????? ??????
            class MyXAxisFormatter : ValueFormatter() {
                private val days = arrayOf("SUN","MON","TUE","WED","THU","FRI","SAT")
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return days.getOrNull(value.toInt()-1) ?: value.toString()
                }
            }

            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X?????? ??????????????? ??????.
                granularity = 1f // 1 ???????????? ?????? ??????
                setDrawAxisLine(true) // ??? ??????
                setDrawGridLines(false) // ??????
                textColor = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_primary_dark) //?????? ??????
                textSize = 12f // ????????? ??????
                valueFormatter = MyXAxisFormatter() // X??? ?????????(?????? ???????????? ??????) ???????????? ?????? ??????
            }
            axisRight.isEnabled = false // ????????? Y?????? ???????????? ??????.
            setTouchEnabled(false) // ????????? ???????????? ?????? ???????????? ??????
            animateY(1000) // ??????????????? ???????????? ??????????????? ??????
            legend.isEnabled = false //?????? ?????? ??????
        }

        var set = BarDataSet(entries,"DataSet") // ???????????? ?????????
        set.color = ContextCompat.getColor(applicationContext!!, com.google.android.material.R.color.design_default_color_primary_dark) // ??? ????????? ??? ??????

        val dataSet :ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        var data = BarData(dataSet)
        data.barWidth = 0.3f //?????? ?????? ??????

        barChart.run {
            this.data = data//????????? ???????????? data??? ????????????.
            setFitBars(true)
            invalidate()
        }
        /*

         }
     */

        //pie chart
        var pieChart: PieChart = findViewById(R.id.pieChart)// pieChart ??????

        pieChart.setUsePercentValues(true)
        //?????? ?????????
        val entriespie = ArrayList<PieEntry>()
        entriespie.add(PieEntry(508f, "Study"))
        entriespie.add(PieEntry(600f, "Todo"))
        entriespie.add(PieEntry(750f, "work out"))

        val colorsItems = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
        colorsItems.add(ColorTemplate.getHoloBlue())

        val pieDataSet = PieDataSet(entriespie, "")
            pieDataSet.apply {
                colors = colorsItems
                valueTextColor = Color.BLACK
                valueTextSize = 16f
            }


        val pieData = PieData(pieDataSet)
            pieChart.apply{
                this.data = pieData
                description.isEnabled = false
                isRotationEnabled = false
                centerText = "This is Center"
                setEntryLabelColor(Color.BLACK)
                animateY(1400, Easing.EaseInOutQuad)
                animate()

            }
/*
        pieChart.run {
            this.data = data//????????? ???????????? data??? ????????????.
            invalidate()
        }
*/


    }

    private fun setMonthView() {
        monthYear.text = monthYearFromDate(CalendarUtil.selectedDate)
        val daysInMonth = daysInMonthArray(CalendarUtil.selectedDate)
        //
        //  val todo = TodoViewModel().getCurrentDay(day.toString())
        // println(todo.date)
        // println(todo)
        //todo.observe(this) { findViewById<TextView>().todoTitle1.text = it[0].title.toString() }

        val calendarAdapter = CalendarAdapter(daysInMonth, this@StatisticsMainActivity)
        println("CalendarAdapter size is ${calendarAdapter.itemCount}")
        val layoutManager = GridLayoutManager(applicationContext, 7)
        calendar.layoutManager = layoutManager
        calendar.adapter = calendarAdapter

        // clickEvent
        calendarAdapter.setOnItemClickListener(object :
            CalendarAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Log.d("uin", "item click")

                val popupFragment = PopupWindowFragment(position, daysInMonth, this@StatisticsMainActivity, RESULT_OK, supportFragmentManager)
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
            R.id.menu_item1-> {
                Toast.makeText(this,"Calendar ??????", Toast.LENGTH_SHORT).show()
                val mainIntent: Intent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }
            R.id.menu_item2-> {
                Toast.makeText(this,"Timer ??????", Toast.LENGTH_SHORT).show()
                val timerIntent:Intent = Intent(this, TimerMainActivity::class.java)
                startActivity(timerIntent)
            }
            R.id.menu_item3-> {
                Toast.makeText(this,"Statistics ??????", Toast.LENGTH_SHORT).show()
                val statisticsIntent:Intent = Intent(this, StatisticsMainActivity::class.java)
                startActivity(statisticsIntent)
            }
            R.id.menu_item4-> Toast.makeText(this,"Settings ??????", Toast.LENGTH_SHORT).show()
        }
        return false
    }

}