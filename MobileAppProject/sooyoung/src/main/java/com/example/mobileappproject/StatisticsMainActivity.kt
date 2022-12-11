package com.example.mobileappproject


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.*

import com.example.mobileappproject.databinding.ActivityMainBinding
import com.example.mobileappproject.databinding.ActivityStatisticsMainBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
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

    //private val timerList = mutableListOf<timerList>() //저장된 timerList
    /*private var selectPos = -1 // 선택된 list
    private var type = -1 // 선택된 timermode = 0 : pomodoro, 1 : timebox

    private var DBselected = 0
    private var titleChange = "None"
    private var DBid = (-1).toLong()*/

    //room
    lateinit var timerTodoAdapter: timerTodoListAdapter
    lateinit var todoViewModel: TodoViewModel

    //linechart
    /*
    private val TAG = this.javaClass.simpleName
    lateinit var lineChart: LineChart
    private val chartData = ArrayList<ChartData>()

     */

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityStatisticsMainBinding.inflate(layoutInflater)
        setContentView(binding.root)




        //room data access
      //  todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
       // todoViewModel.readAllData.observe(this) {
        //    timerTodoAdapter.update(it)
        //}

        //Toolbar setting
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.statisticsdrawer, R.string.menu_item_open, R.string.menu_item_clos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        drawerLayout = binding.statisticsdrawer
        navigationView = binding.statisticsnaView
        navigationView.menu.findItem(R.id.switch_menu).actionView.findViewById<SwitchCompat>(R.id.switchField).setOnCheckedChangeListener {
                _, isChecked ->
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
        navigationView.setNavigationItemSelectedListener(this) //navigation 리스너
        //navigation f

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

        //line chart
        /*var lineChart: LineChart = findViewById(R.id.lineChart)

        private val lineChartData = ArrayList<ChartData>()*/
        // 서버에서 데이터 가져오기 (서버에서 가져온 데이터로 가정하고 직접 추가)
        /*
        chartData.clear()
        addChartItem("1월", 7.9)
        addChartItem("2월", 8.2)
        addChartItem("3월", 8.3)
        addChartItem("4월", 8.5)
        addChartItem("5월", 7.3)

        // 그래프 그릴 자료 넘기기
        LineChart(chartData)

         */



        //bar chart
        var barChart: BarChart = findViewById(R.id.barChart)// barChart 생성

        //임시 데이터
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1.2f,3.0f))
        entries.add(BarEntry(2.2f,6.0f))
        entries.add(BarEntry(3.2f,5.0f))
        entries.add(BarEntry(4.2f,9.0f))
        entries.add(BarEntry(5.2f,8.0f))
        entries.add(BarEntry(6.2f,3.0f))
        entries.add(BarEntry(7.2f,5.0f))

        barChart.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) //그래프의 그림자
            setDrawGridBackground(false)//격자구조 넣을건지
            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                axisMaximum = 13f //12 위치에 선을 그리기 위해 13f로 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                granularity = 1f // 1시간 마다 선을 그리려고 설정.
                setDrawLabels(true) // 값 적는거 허용 (0, 50, 100)
                setDrawGridLines(true) //격자 라인 활용
                setDrawAxisLine(false) // 축 그리기 설정
                axisLineColor = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_secondary_variant) // 축 색깔 설정
                gridColor = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_on_secondary) // 축 아닌 격자 색깔 설정
                textColor = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_primary_dark) // 라벨 텍스트 컬러 설정
                textSize = 13f //라벨 텍스트 크기
            }

            // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            class MyXAxisFormatter : ValueFormatter() {
                private val days = arrayOf("SUN","MON","TUE","WED","THU","FRI","SAT")
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return days.getOrNull(value.toInt()-1) ?: value.toString()
                }
            }

            xAxis.run {
                position = XAxis.XAxisPosition.BOTTOM //X축을 아래에다가 둔다.
                granularity = 1f // 1 단위만큼 간격 두기
                setDrawAxisLine(true) // 축 그림
                setDrawGridLines(false) // 격자
                textColor = ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_primary_dark) //라벨 색상
                textSize = 12f // 텍스트 크기
                valueFormatter = MyXAxisFormatter() // X축 라벨값(밑에 표시되는 글자) 바꿔주기 위해 설정
            }
            axisRight.isEnabled = false // 오른쪽 Y축을 안보이게 해줌.
            setTouchEnabled(false) // 그래프 터치해도 아무 변화없게 막음
            animateY(1000) // 밑에서부터 올라오는 애니매이션 적용
            legend.isEnabled = false //차트 범례 설정
        }

        var set = BarDataSet(entries,"DataSet") // 데이터셋 초기화
        set.color = ContextCompat.getColor(applicationContext!!, com.google.android.material.R.color.design_default_color_primary_dark) // 바 그래프 색 설정

        val dataSet :ArrayList<IBarDataSet> = ArrayList()
        dataSet.add(set)
        var data = BarData(dataSet)
        data.barWidth = 0.3f //막대 너비 설정

        barChart.run {
            this.data = data//차트의 데이터를 data로 설정해줌.
            setFitBars(true)
            invalidate()
        }
        /*

         }
     */
    }

    private fun printPieChart(categoryList : MutableList<Int>) {
        //pie chart
        val pieChart: PieChart = findViewById(R.id.pieChart)// pieChart 생성

        Log.d("soo","categoryList.size == ${categoryList.size}")
        pieChart.setUsePercentValues(true)
        //임시 데이터
        val entriespie = ArrayList<PieEntry>()
        val colorsItems = ArrayList<Int>()

        if(categoryList.size == 0) {
            entriespie.add(PieEntry(1.toFloat(), "NoCategory"))

            colorsItems.add(Color.parseColor("#808080"))
        }
        else {
            var study = 0
            var exercise = 0
            var meeting = 0
            var plan = 0
            for (i in 0..categoryList.size - 1) {
                when (categoryList[i]) {
                    1 -> study += 1
                    2 -> exercise += 1
                    3 -> meeting += 1
                    4 -> plan += 1
                }
            }
            Log.d("soo", "1:2:3:4 = $study, $exercise, $meeting, $plan")

            if (study != 0)
                entriespie.add(PieEntry(study.toFloat(), "Study"))
            if (exercise != 0)
                entriespie.add(PieEntry(exercise.toFloat(), "exercise"))
            if (meeting != 0)
                entriespie.add(PieEntry(meeting.toFloat(), "meeting"))
            if (plan != 0)
                entriespie.add(PieEntry(plan.toFloat(), "plan"))


            colorsItems.add(Color.parseColor("#67d5b5"))
            colorsItems.add(Color.parseColor("#ee7785"))
            colorsItems.add(Color.parseColor("#aaabd3"))
            colorsItems.add(Color.parseColor("#ffda8e"))
        }

        /*for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
        colorsItems.add(ColorTemplate.getHoloBlue())*/

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
            centerText = "CATEGORY"
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
/*
        pieChart.run {
            this.data = data//차트의 데이터를 data로 설정해줌.
            invalidate()
        }*/
    }

    private fun setMonthView() {
        monthYear.text = monthYearFromDate(CalendarUtil.selectedDate)
        val daysInMonth = daysInMonthArray(CalendarUtil.selectedDate)

        val calendarAdapter = StatisticsCalendarAdapter(daysInMonth,
            this@StatisticsMainActivity,
            applicationContext,
            onPieChart = {printPieChart(it)})
        val layoutManager = GridLayoutManager(applicationContext, 7)
        calendar.layoutManager = layoutManager
        calendar.adapter = calendarAdapter

        // clickEvent 제거
        /*calendarAdapter.setOnItemClickListener(object :
            CalendarAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                Log.d("uin", "item click")

                val popupFragment = PopupWindowFragment(position, daysInMonth, this@StatisticsMainActivity, RESULT_OK, supportFragmentManager)
                popupFragment.show(supportFragmentManager, "custom Dialog")
            }
        }
        )*/
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
                Toast.makeText(this,"Calendar 실행", Toast.LENGTH_SHORT).show()
                val mainIntent: Intent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }
            R.id.menu_item2-> {
                Toast.makeText(this,"Timer 실행", Toast.LENGTH_SHORT).show()
                val timerIntent:Intent = Intent(this, TimerMainActivity::class.java)
                startActivity(timerIntent)
            }
            R.id.menu_item3-> {
                Toast.makeText(this,"Statistics 실행", Toast.LENGTH_SHORT).show()
                val statisticsIntent:Intent = Intent(this, StatisticsMainActivity::class.java)
                startActivity(statisticsIntent)
            }
            R.id.menu_item4-> Toast.makeText(this,"Settings 실행", Toast.LENGTH_SHORT).show()
        }
        return false
    }

}