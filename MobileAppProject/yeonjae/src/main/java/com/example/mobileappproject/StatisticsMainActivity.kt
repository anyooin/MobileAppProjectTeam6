package com.example.mobileappproject


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.*
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

    private val timerList = mutableListOf<timerList>() //저장된 timerList
    private var selectPos = -1 // 선택된 list
    private var type = -1 // 선택된 timermode = 0 : pomodoro, 1 : timebox

    private var DBselected = 0
    private var titleChange = "None"
    private var DBid = (-1).toLong()

    //room
    lateinit var timerTodoAdapter: timerTodoListAdapter
    lateinit var todoViewModel: TodoViewModel

    private val statisticsList = mutableListOf<Todo>()

    var statistics_basic: Long = 0
    var statistics_pomodoro: Long = 1
    var statistics_timebox: Long = 2
    var statistics_nonDesignate: Long = 3
    var statistics_study: Long = 4
    var statistics_workout: Long = 5
    var statistics_meeting: Long = 6
    var statistics_promise: Long = 7
    var d_day_basic: Long = 11
    var d1_basic: Long = 22
    var d2_basic: Long = 33
    var d3_basic: Long = 44
    var d4_basic: Long = 55
    var d5_basic: Long = 66
    var d6_basic: Long = 77
    var d_day_pomodoro: Long = 1
    var d1_pomodoro: Long = 2
    var d2_pomodoro: Long = 3
    var d3_pomodoro: Long = 4
    var d4_pomodoro: Long = 5
    var d5_pomodoro: Long = 6
    var d6_pomodoro: Long = 7

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











        //val entry_chart: ArrayList<Map.Entry<*, *>> = ArrayList()

        //val entriesline = ArrayList<LineData>()
        //entriesline.add(LineData(statistics_study.toFloat(), "공부"))
        //LineDataSet lineDataSet1 = new LineDataSet(data1(), "Data Set1")

    }

    //bar chart
    private fun printBarChart(basicList: MutableList<String>) {
        //bar chart
        var barChart: BarChart = findViewById(R.id.barChart)// barChart 생성

        Log.d("soo","basicList.size == ${basicList.size}")

        // 최근 추가 항목(날짜) 부터 7일치
        val entries = ArrayList<BarEntry>()

        val value_str_to_float:Array<Float> = Array(7, { 0f })
        for(i in 0..basicList.size-1){
            if(i > 6) {//7
                break
            }
            if(basicList[i] == "0"){
                value_str_to_float[i] = 0f
            }
            else {
                value_str_to_float[i] = basicList[i].split(":")[0].toFloat() * 3600 +
                        basicList[i].split(":")[1].toFloat() * 60 +
                        basicList[i].split(":")[2].toFloat()
            }
        }

        var total_basic: Int = 0
        for(i in 0..basicList.size-1){
            Log.d("check_1","basicList.size == ${basicList.size}")

            if(basicList[i] == "0")
            {
                total_basic += 0
            }

            else{
                total_basic += (basicList[i].split(":")[0].toInt() * 3600 +
                        basicList[i].split(":")[1].toInt() * 60 +
                        basicList[i].split(":")[2].toInt())
            }
        }

        var h = total_basic / 3600
        var m = (total_basic - h * 3600) / 60
        var s = (total_basic - h * 3600 - m * 60)

        var h_string: String
        if(h < 10){
            h_string = "0" + (h).toString()
        }
        else{
            h_string = (h).toString()
        }

        var m_string: String
        if(m < 10){
            m_string = "0" + (m).toString()
        }
        else{
            m_string = (m).toString()
        }

        var s_string: String
        if(s < 10){
            s_string = "0" + (s).toString()
        }
        else{
            s_string = (s).toString()
        }


        var total_basic_string = h_string + ":" + m_string + ":" + s_string

        var basic_total = findViewById(R.id.BasicTotal) as TextView
        basic_total.setText(total_basic_string)

        //entries.add(BarEntry(1.2f,value_str_to_float[6]))
        //entries.add(BarEntry(2.2f,value_str_to_float[5]))
        //entries.add(BarEntry(3.2f,value_str_to_float[4]))
        //entries.add(BarEntry(4.2f,value_str_to_float[3]))
        //entries.add(BarEntry(5.2f,value_str_to_float[2]))
        //entries.add(BarEntry(6.2f,value_str_to_float[1]))
        //entries.add(BarEntry(7.2f,value_str_to_float[0]))


        for(idx in 0..basicList.size-1){
            if(idx > 6){//7
                break
            }
            val num = idx.toFloat() + (0.2).toFloat()
            entries.add(BarEntry(num, value_str_to_float[idx])) //확인 위해서 초 단위로 넣어둠, 변경 필요!
        }

        val size = basicList.size
        for(idx in size-1..size-7) {/*idx in size-1..size-7idx in size-1..6*/
            basicList.add("0")
        }

        //bar chart용 값들
        var d_day_total = findViewById(R.id.D_day_total) as TextView
        d_day_total.setText(basicList[6])

        var d1_total = findViewById(R.id.D1_total) as TextView
        d1_total.setText(basicList[5])

        var d2_total = findViewById(R.id.D2_total) as TextView
        d2_total.setText(basicList[4])

        var d3_total = findViewById(R.id.D3_total) as TextView
        d3_total.setText(basicList[3])

        var d4_total = findViewById(R.id.D4_total) as TextView
        d4_total.setText(basicList[2])

        var d5_total = findViewById(R.id.D5_total) as TextView
        d5_total.setText(basicList[1])

        var d6_total = findViewById(R.id.D6_total) as TextView
        d6_total.setText(basicList[0])

        barChart.run {
            description.isEnabled = false // 차트 옆에 별도로 표기되는 description을 안보이게 설정 (false)
            setMaxVisibleValueCount(7) // 최대 보이는 그래프 개수를 7개로 지정
            setPinchZoom(false) // 핀치줌(두손가락으로 줌인 줌 아웃하는것) 설정
            setDrawBarShadow(false) //그래프의 그림자
            setDrawGridBackground(false)//격자구조 넣을건지
            axisLeft.run { //왼쪽 축. 즉 Y방향 축을 뜻한다.
                //axisMaximum = 13f //12 위치에 선을 그리기 위해 13f로 맥시멈값 설정
                axisMinimum = 0f // 최소값 0
                //granularity = 1f // 1시간 마다 선을 그리려고 설정.
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
                private val days = arrayOf("D6","D5","D4","D3","D2","D1","D-day")
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
    }

    //line chart
    private fun printLineChart(pomodoroList: MutableList<String>) {

        var lineChart: LineChart = findViewById(R.id.lineChart)

        Log.d("soo","pomodoroList.size == ${pomodoroList.size}")
        //val lineInput = Array<Long>(3, {1})
        //entry 배열 생성
        var entriesLine: ArrayList<Entry> = ArrayList()

        if(pomodoroList.size == 0){
            entriesLine.add(Entry(1F, 0F))
            val dataset: LineDataSet = LineDataSet(entriesLine, "항목 없음")

            dataset.valueTextSize = 12f // 값 폰트 지정해서 사이즈 키우기
            dataset.setDrawFilled(false) // 그래프 밑부분 색칠

            // 그래프 data 생성 -> 최종 입력 데이터
            var dataLine: LineData = LineData(dataset)
            lineChart.data = dataLine

            lineChart.animateXY(10, 10)
       }

        else{
            /*
            var d6_pomo = 0
            var d5_pomo = 0
            var d4_pomo = 0
            var d3_pomo = 0
            var d2_pomo = 0
            var d1_pomo = 0
            var d_dat_pomo = 0
            for(i in 0 .. 6){

            }
             */
            // 최근 추가 항목(날짜) 부터 7일치
            //entry 배열 초기값
            //entriesLine.add(Entry(1F, pomodoroList[6].toFloat()))
            //entriesLine.add(Entry(2F, pomodoroList[5].toFloat()))
            //entriesLine.add(Entry(3F, pomodoroList[4].toFloat()))
            //entriesLine.add(Entry(4F, pomodoroList[3].toFloat()))
            //entriesLine.add(Entry(5F, pomodoroList[2].toFloat()))
            //entriesLine.add(Entry(6F, pomodoroList[1].toFloat()))
            //entriesLine.add(Entry(7F, pomodoroList[0].toFloat()))

            for(idx in 0..pomodoroList.size-1){
                if(idx > 7){
                    break
                }
                var num : Float = pomodoroList[idx].split("회")[0].toFloat()
                entriesLine.add(Entry(idx.toFloat(), num))
            }

            var total_pomodoro = 0
            for(idx in 0..pomodoroList.size-1){
                total_pomodoro += pomodoroList[idx].split("회")[0].toInt()
            }

            var pomodoro_total = findViewById(R.id.PomodoroTotal) as TextView
            pomodoro_total.setText(total_pomodoro.toString())



            // 그래프 구현을 위한 LineDataSet 생성
            var dataset: LineDataSet = LineDataSet(entriesLine, "포모도로 횟수")

            dataset.valueTextSize = 12f // 값 폰트 지정해서 사이즈 키우기
            dataset.setDrawFilled(false) // 그래프 밑부분 색칠

            // 그래프 data 생성 -> 최종 입력 데이터
            var dataLine: LineData = LineData(dataset)
            lineChart.data = dataLine

            lineChart.animateXY(10, 10)
        }


/*
        //entry 배열 초기값
        entriesLine.add(Entry(1F, d6_pomodoro.toFloat()))
        entriesLine.add(Entry(2F, d5_pomodoro.toFloat()))
        entriesLine.add(Entry(3F, d4_pomodoro.toFloat()))
        entriesLine.add(Entry(4F, d3_pomodoro.toFloat()))
        entriesLine.add(Entry(5F, d2_pomodoro.toFloat()))
        entriesLine.add(Entry(6F, d1_pomodoro.toFloat()))
        entriesLine.add(Entry(7F, d_day_pomodoro.toFloat()))
        // 그래프 구현을 위한 LineDataSet 생성
        var dataset: LineDataSet = LineDataSet(entriesLine, "포모도로 횟수")


 */
    }

    //pie chart
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
            var nonDesignate = 0
            var study = 0
            var exercise = 0
            var meeting = 0
            var plan = 0
            for (i in 0..categoryList.size - 1) {
                when (categoryList[i]) {
                    0 -> nonDesignate += 1
                    1 -> study += 1
                    2 -> exercise += 1
                    3 -> meeting += 1
                    4 -> plan += 1
                }
            }
            Log.d("soo", "0:1:2:3:4 = $nonDesignate, $study, $exercise, $meeting, $plan")


            if (nonDesignate != 0)
                entriespie.add(PieEntry(nonDesignate.toFloat(), "미지정"))
            if (study != 0)
                entriespie.add(PieEntry(study.toFloat(), "공부"))
            if (exercise != 0)
                entriespie.add(PieEntry(exercise.toFloat(), "운동"))
            if (meeting != 0)
                entriespie.add(PieEntry(meeting.toFloat(), "약속"))
            if (plan != 0)
                entriespie.add(PieEntry(plan.toFloat(), "회의"))


            colorsItems.add(Color.parseColor("#67d5b5"))
            colorsItems.add(Color.parseColor("#ee7785"))
            colorsItems.add(Color.parseColor("#aaabd3"))
            colorsItems.add(Color.parseColor("#ffda8e"))
            colorsItems.add(Color.parseColor("#3a746a"))

            //pie chart용 값들

            var nonDesignate_total = findViewById(R.id.nonDesignate_total) as TextView
            nonDesignate_total.setText(nonDesignate.toString())
            Log.d("check_nondesig", "time = $nonDesignate_total, ${statistics_nonDesignate.toString()}")

            var study_total = findViewById(R.id.Study_total) as TextView
            study_total.setText(study.toString())
            Log.d("check_study", "time = $study_total, ${statistics_study.toString()}")

            var workout_total = findViewById(R.id.Workout_total) as TextView
            workout_total.setText(exercise.toString())

            var meeting_total = findViewById(R.id.Meeting_total) as TextView
            meeting_total.setText(meeting.toString())
            Log.d("check_meeting", "time = $meeting_total, ${statistics_meeting.toString()}")

            var promise_total = findViewById(R.id.Promise_total) as TextView
            promise_total.setText(plan.toString())


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
    }

    //timebox 총 횟수 측정, 횟수로 측정함, 시간이면 변경 필요
    private fun printTotalTimeBox(timeBoxList: MutableList<String>){
        Log.d("soo","timeBoxList.size == ${timeBoxList.size}")

        var timebox_total = findViewById(R.id.TimeboxTotal) as TextView
        var total_timebox = 0

        if(timeBoxList.size == 0) {
            timebox_total.setText("0")
        }
        else {
            for (i in 0..timeBoxList.size - 1){
                total_timebox += timeBoxList[i].toInt()
            }
            timebox_total.setText(total_timebox.toString())
        }
    }
    /*
    var pieChart: PieChart = findViewById(R.id.pieChart)// pieChart 생성

    pieChart.setUsePercentValues(true)
    //임시 데이터
    val entriespie = ArrayList<PieEntry>()
    entriespie.add(PieEntry(statistics_nonDesignate.toFloat(), "미지정"))
    entriespie.add(PieEntry(statistics_study.toFloat(), "공부"))
    entriespie.add(PieEntry(statistics_workout.toFloat(), "운동"))
    entriespie.add(PieEntry(statistics_meeting.toFloat(), "회의"))
    entriespie.add(PieEntry(statistics_promise.toFloat(), "약속"))

    val colorsItems = ArrayList<Int>()
    for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
    for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)
    for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
    for (c in ColorTemplate.MATERIAL_COLORS) colorsItems.add(c)
    for (c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
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
        centerText = "카테고리%"
        setEntryLabelColor(Color.BLACK)
        animateY(1400, Easing.EaseInOutQuad)
        animate()

    }
/*
    pieChart.run {
        this.data = data//차트의 데이터를 data로 설정해줌.
        invalidate()
    }
*/


     */

    private fun setMonthView() {
        monthYear.text = monthYearFromDate(CalendarUtil.selectedDate)
        val daysInMonth = daysInMonthArray(CalendarUtil.selectedDate)

        val calendarAdapter = StatisticsCalendarAdapter(daysInMonth, this@StatisticsMainActivity, applicationContext,
            onPieChart = { printPieChart(it)}, onLineChart = { printLineChart(it)}, onBarChart = { printBarChart(it)}, onTotaltime = { printTotalTimeBox(it)})
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
/*
    private fun statisticsValues(statisticsArray: Array<Long>) {

        Log.d("yyy","categoryList.size == ${statisticsArray.size}")


        statistics_basic = statisticsArray[0]
        statistics_pomodoro = statisticsArray[1]
        statistics_timebox = statisticsArray[2]
        statistics_nonDesignate = statisticsArray[3]
        statistics_study = statisticsArray[4]
        statistics_workout = statisticsArray[5]
        statistics_meeting = statisticsArray[6]
        statistics_promise = statisticsArray[7]
        d_day_basic = statisticsArray[8]
        d1_basic = statisticsArray[9]
        d2_basic = statisticsArray[10]
        d3_basic = statisticsArray[11]
        d4_basic = statisticsArray[12]
        d5_basic = statisticsArray[13]
        d6_basic = statisticsArray[14]
        d_day_pomodoro = statisticsArray[15]
        d1_pomodoro = statisticsArray[16]
        d2_pomodoro = statisticsArray[17]
        d3_pomodoro = statisticsArray[18]
        d4_pomodoro = statisticsArray[19]
        d5_pomodoro = statisticsArray[20]
        d6_pomodoro = statisticsArray[21]

        Log.d("yeonjae3", "basic:pomodoro:timebox == $statistics_basic, $statistics_pomodoro, $statistics_timebox")

    }

 */
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