package com.example.mobileappproject
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileappproject.databinding.ActivityTimerMainBinding

import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

var timerTodoSelectedDay = "ALL"
var timerModeRecord = arrayOf("00:00:00","0회 성공","03:00:00")

class TimerMainActivity: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener , SeekBar.OnSeekBarChangeListener{
    //navigation
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    //navigation

    private lateinit var toggle: ActionBarDrawerToggle
    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    private var successPomo = 0
    private var pauseTime = 0L

    //pomodoro variable
    private lateinit var remainHourTextView: TextView
    private lateinit var remainMinutesTextView: TextView
    private lateinit var remainSecondsTextView: TextView
    private lateinit var pomodoroSeekBar: SeekBar

    //timebox variable
    private lateinit var timeBoxremainHourTextView: TextView
    private lateinit var timeBoxremainMinutesTextView: TextView
    private lateinit var timeBoxremainSecondsTextView: TextView
    private lateinit var timeboxSeekBar: SeekBar

    private var selectPos = -1 // 선택된 list
    private var type = -1 // 선택된 timermode = 0 : pomodoro, 1 : timebox
    private var todoID:Long = (-1).toLong() // datebase id

    lateinit var binding:ActivityTimerMainBinding

    //room
    lateinit var timerTodoAdapter: timerTodoListAdapter
    lateinit var todoViewModel: TodoViewModel

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerMainBinding.inflate(layoutInflater)
        initSounds()

        setContentView(binding.root)

        //room data access
        todoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]
        todoViewModel.readAllData.observe(this) {
            timerTodoAdapter.update(it)
        }

        remainHourTextView = binding.remainHourTextView
        remainMinutesTextView = binding.remainMinutesTextView
        remainSecondsTextView = binding.remainSecondsTextView
        timeBoxremainHourTextView = binding.timeBoxremainHourTextView
        timeBoxremainMinutesTextView = binding.timeBoxremainMinutesTextView
        timeBoxremainSecondsTextView = binding.timeBoxremainSecondsTextView

        pomodoroSeekBar = binding.pomodoroSeekBar
        timeboxSeekBar = binding.timeboxSeekBar

        //navigation s
        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.drawer_opened,
            R.string.drawer_closed
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toggle.syncState()

        // 네비게이션 드로어 생성
        drawerLayout = binding.drawerLayout//findViewById(R.id.drawer_layout)

        // 네비게이션 드로어 내에있는 화면의 이벤트를 처리하기 위해 생성
        navigationView = binding.navView
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

        //background frame
        val date = CalendarUtil.today.toString().split("-")
        if (switchOffOn == 1) {
            binding.drawerLayout.background = when (date[1]) {
                "12", "01", "02" -> resources.getDrawable(R.drawable.frame_winter)
                "03", "04", "05" -> resources.getDrawable(R.drawable.frame_spring)
                "06", "07", "08" -> resources.getDrawable(R.drawable.frame_summer)
                "09", "10", "11" -> resources.getDrawable(R.drawable.frame_autumn)
                else -> {
                    null
                }
            }
        } else {
            binding.drawerLayout.background = null
        }

        //seekbar event handler
        binding.pomodoroSeekBar.setOnSeekBarChangeListener(this)
        binding.timeboxSeekBar.setOnSeekBarChangeListener(this)

        //TimerMode
        binding.basicTimer.setOnClickListener {

            type = -1
            binding.basictimerBtn.visibility = View.VISIBLE
            binding.pomodoroBtn.visibility = View.INVISIBLE
            binding.timeboxBtn.visibility = View.INVISIBLE
            binding.remainHourTextView.visibility = View.INVISIBLE
            binding.remainMinutesTextView.visibility = View.INVISIBLE
            binding.remainSecondsTextView.visibility = View.INVISIBLE
            binding.timeBoxremainHourTextView.visibility = View.INVISIBLE
            binding.timeBoxremainMinutesTextView.visibility = View.INVISIBLE
            binding.timeBoxremainSecondsTextView.visibility = View.INVISIBLE
            binding.pomodoroSeekBar.visibility = View.INVISIBLE
            binding.timeboxSeekBar.visibility = View.INVISIBLE
            binding.chronometer.visibility = View.VISIBLE

            binding.basicTimer.setBackgroundResource(R.drawable.day_cell_select)
            binding.pomodoro.setBackgroundResource(R.drawable.day_cell_today)
            binding.timebox.setBackgroundResource(R.drawable.day_cell_today)

            binding.recordH.text = "%s:".format(timerModeRecord[0].split(":")[0])
            binding.recordM.text = "%s:".format(timerModeRecord[0].split(":")[1])
            binding.recordS.text = "%s".format(timerModeRecord[0].split(":")[2])
        }

        binding.pomodoro.setOnClickListener {
            type = 0
            binding.basictimerBtn.visibility = View.INVISIBLE
            binding.pomodoroBtn.visibility = View.VISIBLE
            binding.timeboxBtn.visibility = View.INVISIBLE
            binding.remainHourTextView.visibility = View.VISIBLE
            binding.remainMinutesTextView.visibility = View.VISIBLE
            binding.remainSecondsTextView.visibility = View.VISIBLE
            binding.timeBoxremainHourTextView.visibility = View.INVISIBLE
            binding.timeBoxremainMinutesTextView.visibility = View.INVISIBLE
            binding.timeBoxremainSecondsTextView.visibility = View.INVISIBLE
            binding.pomodoroSeekBar.visibility = View.INVISIBLE
            binding.timeboxSeekBar.visibility = View.INVISIBLE
            binding.chronometer.visibility = View.INVISIBLE

            binding.basicTimer.setBackgroundResource(R.drawable.day_cell_today)
            binding.pomodoro.setBackgroundResource(R.drawable.day_cell_select)
            binding.timebox.setBackgroundResource(R.drawable.day_cell_today)

            updateRemainTime(60 * 30 * 1000L)
            updateSeekBar(60 * 30 * 1000L, pomodoroSeekBar)

            binding.recordH.text = timerModeRecord[1]
            binding.recordM.text = ""
            binding.recordS.text = ""

        }
        binding.timebox.setOnClickListener {
            type = 1
            binding.basictimerBtn.visibility = View.INVISIBLE
            binding.pomodoroBtn.visibility = View.INVISIBLE
            binding.timeboxBtn.visibility = View.VISIBLE
            binding.remainHourTextView.visibility = View.INVISIBLE
            binding.remainMinutesTextView.visibility = View.INVISIBLE
            binding.remainSecondsTextView.visibility = View.INVISIBLE
            binding.timeBoxremainHourTextView.visibility = View.VISIBLE
            binding.timeBoxremainMinutesTextView.visibility = View.VISIBLE
            binding.timeBoxremainSecondsTextView.visibility = View.VISIBLE
            binding.pomodoroSeekBar.visibility = View.INVISIBLE
            binding.timeboxSeekBar.visibility = View.VISIBLE
            binding.chronometer.visibility = View.INVISIBLE

            binding.basicTimer.setBackgroundResource(R.drawable.day_cell_today)
            binding.pomodoro.setBackgroundResource(R.drawable.day_cell_today)
            binding.timebox.setBackgroundResource(R.drawable.day_cell_select)

            val hourToken = timeBoxremainHourTextView.text.split(":")[0].toInt()
            val minToken = timeBoxremainMinutesTextView.text.split(":")[0].toInt()
            val secToken = timeBoxremainSecondsTextView.text.split(":")[0].toInt()

            updateRemainTime((hourToken * 60 * 60 + minToken * 60 + secToken) * 1000L)
            updateSeekBar((hourToken * 60 * 60 + minToken * 60 + secToken) * 1000L, timeboxSeekBar)

            binding.recordH.text = "%s:".format(timerModeRecord[2].split(":")[0])
            binding.recordM.text = "%s:".format(timerModeRecord[2].split(":")[1])
            binding.recordS.text = "%s".format(timerModeRecord[2].split(":")[2])
        }

        //Basic TIMER
        binding.basictimerstartBtn.setOnClickListener {
            binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
            binding.chronometer.start()
        }
        binding.basictimerstopBtn.setOnClickListener {
            pauseTime = binding.chronometer.base - SystemClock.elapsedRealtime()
            binding.chronometer.stop()

            val recordPauseTime = SystemClock.elapsedRealtime() - binding.chronometer.base
            var h = (recordPauseTime / 3600000)
            var m = (recordPauseTime - h * 3600000) / 60000
            var s = (recordPauseTime - h * 3600000 - m * 60000) / 1000

            binding.recordH.text = "%02d:".format(h + timerModeRecord[0].split(":")[0].toLong())
            binding.recordM.text = "%02d:".format(m + timerModeRecord[0].split(":")[1].toLong())
            binding.recordS.text = "%02d".format(s + timerModeRecord[0].split(":")[2].toLong())

            CoroutineScope(Dispatchers.IO).launch {
                if (todoID != (-1).toLong()) {
                    val todo = todoViewModel.getOne(todoID)
                    val time = "%s%s%s".format(binding.recordH.text,binding.recordM.text,binding.recordS.text)
                    todo.basicTimer = "%s".format(time)
                    todoViewModel.update(todo)
                }
            }
        }

        binding.basictimerresetBtn.setOnClickListener {

            pauseTime = 0L
            binding.chronometer.base = SystemClock.elapsedRealtime()
            binding.chronometer.stop()
            timerModeRecord[0] = "%s%s%s".format(binding.recordH.text,binding.recordM.text,binding.recordS.text)
        }

        //POMODORO
        binding.pomodorostartBtn.setOnClickListener {
            startCountDown(pomodoroSeekBar)
        }
        binding.pomodororesetBtn.setOnClickListener {
            pomodoroSeekBar.progress = 5
            updateRemainTime(5 * 1000L)
            updateSeekBar(5 * 1000L, pomodoroSeekBar)
            stopCountDown()
            /* pomodoro 30분
            pomodoroSeekBar.progress = 1800
            updateRemainTime(60 * 30 * 1000L)
            updateSeekBar(60 * 30 * 1000L, pomodoroSeekBar)
            stopCountDown()*/
        }

        //TIMEBOX
        binding.timeboxstartBtn.setOnClickListener {
            if (timeboxSeekBar.progress != 0) {
                startCountDown(timeboxSeekBar)
            }
        }
        binding.timeboxstopBtn.setOnClickListener {
            stopCountDown()

            val h = timeBoxremainHourTextView.text
            val m = timeBoxremainMinutesTextView.text
            val s = timeBoxremainSecondsTextView.text

            binding.recordH.text = "%02d:".format(h.substring(0..1).toLong())
            binding.recordM.text = "%02d:".format(m.substring(0..1).toLong())
            binding.recordS.text = "%02d".format(s.toString().toLong())
            timerModeRecord[2] = "%02d:%02d:%02d".format(h.substring(0..1).toLong(), m.substring(0..1).toLong(),s.toString().toLong())

            if(todoID != (-1).toLong()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val todo = todoViewModel.getOne(todoID)
                    val time = "%s%s%s".format(binding.recordH.text,binding.recordM.text,binding.recordS.text)
                    todo.timeBox = "%s".format(time)
                    todoViewModel.update(todo)
                }
            }

        }
        binding.timeboxresetBtn.setOnClickListener {
            timeboxSeekBar.progress = 0
            updateRemainTime(10800 * 1000L)
            updateSeekBar(10800 * 1000L, timeboxSeekBar)

            binding.recordH.text = "03:"
            binding.recordM.text = "00:"
            binding.recordS.text = "00"
            timerModeRecord[2] = "%02d:%02d:%02d".format(3,0,0)

            stopCountDown()
        }

        binding.initRecordBtn.setOnClickListener {
            if(type == 0) {
                binding.recordH.text = "0회 성공"
                binding.recordM.text = ""
                binding.recordS.text = ""
                timerModeRecord[1] = "0회 성공"

                if(todoID != (-1).toLong()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val todo = todoViewModel.getOne(todoID)
                        val time = "%s%s%s".format(binding.recordH.text,binding.recordM.text,binding.recordS.text)
                        todo.pomodoro = "%s".format(time)
                        todoViewModel.update(todo)
                    }
                }
            }
            else if(type == -1) {
                binding.recordH.text = "00:"
                binding.recordM.text = "00:"
                binding.recordS.text = "00"
                timerModeRecord[0] = "00:00:00"

                if(todoID != (-1).toLong()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val todo = todoViewModel.getOne(todoID)
                        val time = "%s%s%s".format(binding.recordH.text,binding.recordM.text,binding.recordS.text)
                        todo.basicTimer = "%s".format(time)
                        todoViewModel.update(todo)
                    }
                }
            }
            else if(type == 1) {
                binding.recordH.text = "03:"
                binding.recordM.text = "00:"
                binding.recordS.text = "00"
                timerModeRecord[2] = "03:00:00"

                if(todoID != (-1).toLong()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val todo = todoViewModel.getOne(todoID)
                        val time = "%s%s%s".format(binding.recordH.text,binding.recordM.text,binding.recordS.text)
                        todo.timeBox = "%s".format(time)
                        todoViewModel.update(todo)
                    }
                }
            }
        }

        //day selected button click
        binding.CurrentSelectedDate.text = timerTodoSelectedDay
        binding.dateSelectButton.setOnClickListener {
            setupDatePicker("CurrentSelectedDate")
        }
        binding.selectNoneday.setOnClickListener {
            timerTodoSelectedDay = "ALL"
            binding.CurrentSelectedDate.text = String.format("%s", "ALL")
            timerTodoAdapter.updating()
        }

        timerTodoAdapter = timerTodoListAdapter(this,
            onShowDB = { showTimerDB(it) },
            onSelectTimer = {selectTimerDB(it)},
            onClick = {clickTimer(it)})
        binding.selectRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.selectRecyclerView.adapter = timerTodoAdapter
        binding.selectRecyclerView.addItemDecoration(DividerItemDecoration(this,
            LinearLayoutManager.VERTICAL))

        /*var checkScreen = 0
        timerTodoAdapter.setItemClickListener(object: timerTodoListAdapter.ItemClickListener{
            override fun onClick(preView: View?, view: View, timeArray:Array<String>) {
                preView?.setBackgroundResource(R.drawable.day_cell_today)
                if (preView != null) {
                    if((preView.equals(view) && (checkScreen%2 == 1))) {
                        //view.setBackgroundColor(Color.parseColor("#000000"))
                        view.setBackgroundResource(R.drawable.day_cell_today)
                        checkScreen = 0
                        todoID = -1
                    }
                    else{
                        //view.setBackgroundColor(Color.parseColor("#D0A4ED"))
                        view.setBackgroundResource(R.drawable.day_cell_select)
                        checkScreen = 1
                    }
                }
                else {
                    //view.setBackgroundColor(Color.parseColor("#D0A4ED"))
                    view.setBackgroundResource(R.drawable.day_cell_select)
                    checkScreen = 1
                }

                Log.d("soo","todoID = $todoID")
                settingRecord(todoID, type, timeArray)
            }
        })*/
    }

    private fun clickTimer(timeArray : Array<String>) {
        todoID = timeArray[0].toLong()
        Log.d("soo","todoID = $todoID")
        settingRecord(todoID, type, timeArray)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun setBackgroundFrame()
    {
        if (switchOffOn == 1) {
            navigationView.menu.findItem(R.id.switch_menu).actionView.findViewById<SwitchCompat>(R.id.switchField).isChecked = true
            val date = CalendarUtil.today.toString().split("-")
            drawerLayout.background = when (date[1]) {
                "12", "01", "02" -> resources.getDrawable(R.drawable.frame_winter)
                "03", "04", "05" -> resources.getDrawable(R.drawable.frame_spring)
                "06", "07", "08" -> resources.getDrawable(R.drawable.frame_summer)
                "09", "10", "11" -> resources.getDrawable(R.drawable.frame_autumn)
                else -> {
                    null
                }
            }
        } else {
            navigationView.menu.findItem(R.id.switch_menu).actionView.findViewById<SwitchCompat>(R.id.switchField).isChecked = false
            drawerLayout.background= null
        }
    }

    private fun settingRecord(id : Long, type : Int, timearray : Array<String>) {
        if (type == -1) { // basic
            if (id == (-1).toLong() || timearray[1] == "0") {
                binding.recordH.text = "00:"
                binding.recordM.text = "00:"
                binding.recordS.text = "00"
            } else {
                binding.recordH.text = "%s:".format(timearray[1].split(":")[0])
                binding.recordM.text = "%s:".format(timearray[1].split(":")[1])
                binding.recordS.text = "%s".format(timearray[1].split(":")[2])
            }
        } else if (type == 0) { // pomo
            if (id == (-1).toLong() || timearray[2] == "0") {
                binding.recordH.text = "0회 성공"
                binding.recordM.text = ""
                binding.recordS.text = ""
            } else {
                binding.recordH.text = timearray[2]
                binding.recordM.text = ""
                binding.recordS.text = ""
            }
        } else if (type == 1) {// timebox
            if (id == (-1).toLong() || timearray[3] == "0") {
                binding.recordH.text = "03:"
                binding.recordM.text = "00:"
                binding.recordS.text = "00"
            } else {
                binding.recordH.text = "%s:".format(timearray[3].split(":")[0])
                binding.recordM.text = "%s:".format(timearray[3].split(":")[1])
                binding.recordS.text = "%s".format(timearray[3].split(":")[2])
            }
        }

        if(id == (-1).toLong()) {
            timerModeRecord[0] = "00:00:00"
            timerModeRecord[1] = "0회 성공"
            timerModeRecord[2] = "03:00:00"
        }
        else {
            if(timearray[1] == "0"){ timerModeRecord[0] = "00:00:00"}
            else {timerModeRecord[0] = timearray[1]}

            if(timearray[2] == "0"){ timerModeRecord[1] = "0회 성공"}
            else {timerModeRecord[1] = timearray[2]}

            if(timearray[3] == "0"){ timerModeRecord[2] = "03:00:00"}
            else {timerModeRecord[2] = timearray[3]}
        }
    }

    private fun showTimerDB(timeArray: Array<String>) { //DB정보출력
        val dig = timerListTodoPopup(this)
        var basic = timeArray[0]
        var pomo = timeArray[1]
        var timebox = timeArray[2]

        if(basic == "0") basic = "00:00:00"
        if(pomo == "0") pomo = "0회 성공"
        if(timebox == "0") timebox = "00:00:00"

        dig.show(basic, pomo, timebox)
    }

    private fun selectTimerDB(id : Long) {
        todoID = id
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }
    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }
    override fun onDestroy() {
        super.onDestroy() // sound 파일들이 메모리에서 제거된다.
        soundPool.release()
    }

    //Seekbar event listener, seekbar 값 변경될 때마다 호출
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if(p2) {
            if(type == 1)
                updateRemainTime(timeboxSeekBar.progress*1000L)
        }
    }
    override fun onStartTrackingTouch(p0: SeekBar?) {
        stopCountDown() // seekbar 첫 눌림에 호출
    }
    override fun onStopTrackingTouch(p0: SeekBar?) {
        // seekbar 드래그 떼면 호출
        if(p0?.progress == 0) { // 0분이면 시작안함
            stopCountDown()
        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel() // timer 멈추기
        currentCountDownTimer = null
        soundPool.autoPause()
    }
    private fun startCountDown(seekBar: SeekBar){
        currentCountDownTimer = createCountDownTimer(seekBar.progress*1000L, seekBar)
        currentCountDownTimer?.start()  // timer 시작하기

        tickingSoundId?.let {soundId->
            soundPool.play(soundId,1F,1F,0,-1,1F)
        }
    }

    private fun createCountDownTimer(initialMills:Long, seekBar: SeekBar) =
        // 1초 마다 호출되도록 함
        object : CountDownTimer(initialMills, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                //countDownInterval 마다 호출됨
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished, seekBar)
            }
            override fun onFinish() {
                //timer가 종료되면 호출
                completeCountDown(seekBar)
            }
        }

    private fun completeCountDown(seekBar: SeekBar){
        updateRemainTime(0)
        updateSeekBar(0, seekBar)

        if(type == 0) //pomodoro 인 경우
        {
            successPomo = binding.recordH.text.split("회")[0].toInt()+1
            binding.recordH.text = "%d회 성공".format(successPomo)
            binding.recordM.text = ""
            binding.recordS.text = ""
            timerModeRecord[1] = "%d회 성공".format(successPomo)

            if(todoID != -1.toLong()) {
                CoroutineScope(Dispatchers.IO).launch{
                    val todo = todoViewModel.getOne(todoID)
                    val time = "%s%s%s".format(binding.recordH.text,binding.recordM.text,binding.recordS.text)
                    todo.pomodoro = "%s".format(time)
                    todoViewModel.update(todo)
                }
            }
        }
        // 끝난 경우-끝난 벨소리 재생함
        soundPool.autoPause()
        bellSoundId?.let {soundId->
            soundPool.play(soundId, 1F,1F,0,0,1F)
        }
    }

    // 기본적으로 함수마다 초의 단위를 통일하는게 좋음. 개발할 때 가독성이 좋음
    private fun updateRemainTime(remainMillis: Long){
        // 총 남은 초
        val remainSeconds = remainMillis/1000

        if(type == 0)//pomodoro인 경우
        {
            remainHourTextView.text = "%02d:".format(remainSeconds/60/60)
            remainMinutesTextView.text = "%02d:".format(remainSeconds/60%60)
            remainSecondsTextView.text= "%02d".format(remainSeconds%60)
        }
        if (type == 1)// timeBox인경우
        {
            timeBoxremainHourTextView.text = "%02d:".format(remainSeconds/60/60)
            timeBoxremainMinutesTextView.text = "%02d:".format(remainSeconds/60%60)
            timeBoxremainSecondsTextView.text= "%02d".format(remainSeconds%60)
        }
    }

    private fun initSounds() {
        // mp4 파일을 로드함
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun updateSeekBar(remainMillis: Long, seekBar: SeekBar) {
        // 밀리 세컨드를 분(정수)으로 바꿔서 보여줌
        seekBar.progress = (remainMillis / 1000).toInt()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    // 툴바 메뉴 버튼이 클릭 됐을 때 실행하는 함수
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //toggle 버튼 선택시 navigation 창 열림
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }

        // 클릭한 툴바 메뉴 아이템 id 마다 다르게 실행하도록 설정
        when(item.itemId){
            android.R.id.home->{
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // 드로어 내 아이템 클릭 이벤트 처리하는 함수
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_item1-> {
                Toast.makeText(this,"Calendar 실행", Toast.LENGTH_SHORT).show()
                val MainIntent = Intent(this, MainActivity::class.java)
                startActivity(MainIntent)
            }
            R.id.menu_item2-> Toast.makeText(this,"Timer 실행", Toast.LENGTH_SHORT).show()
            R.id.menu_item3-> {
                Toast.makeText(this,"Statistics 실행", Toast.LENGTH_SHORT).show()
                val StatisticsIntent = Intent(this, StatisticsMainActivity::class.java)
                startActivity(StatisticsIntent)
            }
            R.id.menu_item4-> Toast.makeText(this,"Settings 실행", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    //날짜 선택 함수
    private fun setupDatePicker(id: String) {
        val mDatePicker: DatePickerDialog
        val mCurrentDate = Calendar.getInstance()
        val year = mCurrentDate.get(Calendar.YEAR)
        val month = mCurrentDate.get(Calendar.MONTH)
        val day = mCurrentDate.get(Calendar.DAY_OF_MONTH)

        mDatePicker = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener{

            override fun onDateSet(view: DatePicker?, year: Int, month: Int, DayOfMonth: Int) {
                if (id == "CurrentSelectedDate") {
                    binding.CurrentSelectedDate.text = String.format("%d-%d-%02d", year, month+1, DayOfMonth)
                    timerTodoSelectedDay = binding.CurrentSelectedDate.text.toString()

                    timerTodoAdapter.updating()
                }
            }
        }, year, month, day)

        mDatePicker.show()
    }
}