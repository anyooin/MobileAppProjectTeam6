package com.example.mobileappproject
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.SoundPool
import android.os.Build.VERSION_CODES.N
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobileappproject.databinding.ActivityTimerListTodoPopupBinding
import com.example.mobileappproject.databinding.ActivityTimerMainBinding

import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*

var timerTodoSelectedDay = "ALL"
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

    private lateinit var currentSelectedTimer: TextView

    private val timerList = mutableListOf<timerList>() //저장된 timerList

    private var selectPos = -1 // 선택된 list
    private var type = -1 // 선택된 timermode = 0 : pomodoro, 1 : timebox
    private lateinit var RecordTimerMode: TextView
    private lateinit var RecordTime: TextView

    lateinit var binding:ActivityTimerMainBinding
    private var DBselected = 0
    private var titleChange = "None"
    private var DBid = (-1).toLong()

    //room
    lateinit var timerTodoAdapter: timerTodoListAdapter
    lateinit var todoViewModel: TodoViewModel

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
        currentSelectedTimer = binding.currentSelectedTimer

        //navigation s
        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            R.string.drawer_opened,
            R.string.drawer_closed
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        //supportActionBar?.setHomeAsUpIndicator(R.drawable.navi_menu) // 홈버튼 이미지 변경
        //supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게
        toggle.syncState()

        // 네비게이션 드로어 생성
        drawerLayout = binding.drawerLayout//findViewById(R.id.drawer_layout)

        // 네비게이션 드로어 내에있는 화면의 이벤트를 처리하기 위해 생성
        navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this) //navigation 리스너
        //navigation f

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

            binding.basicTimer.setBackgroundResource(R.drawable.timer_selected_button)
            binding.pomodoro.setBackgroundResource(R.drawable.timer_button)
            binding.timebox.setBackgroundResource(R.drawable.timer_button)

            if (selectPos != -1) {
                RecordTimerMode.text = "Mode = basicTimer"
                timerList[selectPos].timerMode = "Mode = basicTimer"
                if(timerList[selectPos].basictimeVar.size == 0) {
                    timerList[selectPos].timeRecord = "time = 00:00:00"
                    RecordTime.text = "time = 00:00:00"
                }
                else {
                    var timeP = timerList[selectPos].basictimeVar[timerList[selectPos].basictimeVar.size-1]
                    timerList[selectPos].timeRecord = "time = %02d:%02d:%02d".format(timeP/3600, timeP/60, timeP%60)
                    RecordTime.text = "time = %02d:%02d:%02d".format(timeP/3600, timeP/60, timeP%60)
                }
            }

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

            binding.basicTimer.setBackgroundResource(R.drawable.timer_button)
            binding.pomodoro.setBackgroundResource(R.drawable.timer_selected_button)
            binding.timebox.setBackgroundResource(R.drawable.timer_button)

            updateRemainTime(60 * 30 * 1000L)
            updateSeekBar(60 * 30 * 1000L, pomodoroSeekBar)
            if (selectPos != -1) {
                RecordTimerMode.text = "Mode = pomodoro"
                timerList[selectPos].timerMode = "Mode = pomodoro"
                RecordTime.text = "포모도로 %d회 성공".format(timerList[selectPos].pomodorotimeVar)
            }
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

            binding.basicTimer.setBackgroundResource(R.drawable.timer_button)
            binding.pomodoro.setBackgroundResource(R.drawable.timer_button)
            binding.timebox.setBackgroundResource(R.drawable.timer_selected_button)

            val hourToken = timeBoxremainHourTextView.text.split(":")[0].toInt()
            val minToken = timeBoxremainMinutesTextView.text.split(":")[0].toInt()
            val secToken = timeBoxremainSecondsTextView.text.split(":")[0].toInt()

            updateRemainTime((hourToken * 60 * 60 + minToken * 60 + secToken) * 1000L)
            updateSeekBar((hourToken * 60 * 60 + minToken * 60 + secToken) * 1000L, timeboxSeekBar)
            if (selectPos != -1) {
                RecordTimerMode.text = "Mode = timebox"
                timerList[selectPos].timerMode = "Mode = timebox"
                timerList[selectPos].timeRecord = "time = %02d:%02d:%02d".format(hourToken, minToken, secToken)
                RecordTime.text = "time = %02d:%02d:%02d".format(hourToken, minToken, secToken)
            }
        }

        //TIMER
        binding.basictimerstartBtn.setOnClickListener {
            binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
            binding.chronometer.start()
        }
        binding.basictimerstopBtn.setOnClickListener {
            pauseTime = binding.chronometer.base - SystemClock.elapsedRealtime()
            binding.chronometer.stop()
            if (selectPos != -1) {

                val recordPauseTime = SystemClock.elapsedRealtime() - binding.chronometer.base
                var h = (recordPauseTime / 3600000)
                var m = (recordPauseTime - h * 3600000) / 60000
                var s = (recordPauseTime - h * 3600000 - m * 60000) / 1000


                //BASICTIMER 기록되어 있는 시간 기록 받아와서 카운트 한 시간에 더하기
                if (isNotEmpty(timerList[selectPos].basictimeVar))//timerList[selectPos].timeRecord 얘 넣으면 안되서 저거 넣음
                {
                    val timeRecordStrData = timerList[selectPos].basictimeVar[timerList[selectPos].basictimeVar.size-1]

                    val recordedH = timeRecordStrData / 3600  //timeRecordStrArr[0].split(" ")[2]
                    val recordedM = timeRecordStrData / 60   //timeRecordStrArr[1]
                    val recordedS = timeRecordStrData % 60   //timeRecordStrArr[2]

                    h += recordedH
                    m += recordedM
                    s += recordedS
                }

                val textH = "%02d".format(h)
                val textM = "%02d".format(m)
                val textS = "%02d".format(s)

                RecordTime.text = "time = ${textH}:${textM}:${textS}"
                timerList[selectPos].timeRecord = "time = ${textH}:${textM}:${textS}"

                //database에 추가
                CoroutineScope(Dispatchers.IO).launch {
                    if (timerList[selectPos].timeConnectedID != (-1).toLong()) {
                        val todo = todoViewModel.getOne(timerList[selectPos].timeConnectedID)
                        todo.basicTimer = "%s".format(timerList[selectPos].timeRecord)
                        todoViewModel.update(todo)
                    }
                }
            }
        }
        binding.basictimerresetBtn.setOnClickListener {
            val h = timerList[selectPos].timeRecord.split(":")[0].split(" ")[2]
            val m = timerList[selectPos].timeRecord.split(":")[1]
            val s = timerList[selectPos].timeRecord.split(":")[2]
            timerList[selectPos].basictimeVar.add(h.toLong() * 3600 + m.toLong() * 60 + s.toLong())
            pauseTime = 0L
            binding.chronometer.base = SystemClock.elapsedRealtime()
            binding.chronometer.stop()
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
            if (selectPos != -1) {
                val h = timeBoxremainHourTextView.text
                val m = timeBoxremainMinutesTextView.text
                val s = timeBoxremainSecondsTextView.text
                RecordTime.text = "time = ${h}${m}${s}"
                timerList[selectPos].timeRecord = "time = ${h}${m}${s}"
                timerList[selectPos].timeboxtimeVar.add(
                    h.substring(0..1).toLong() * 3600 + m.substring(0..1)
                        .toLong() * 60 + s.toString().toLong()
                )
                //database에 추가
                if(timerList[selectPos].timeConnectedID != (-1).toLong()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val todo = todoViewModel.getOne(timerList[selectPos].timeConnectedID)
                        todo.timeBox = "%s".format(timerList[selectPos].timeRecord)
                        todoViewModel.update(todo)
                    }
                }
            }
        }
        binding.timeboxresetBtn.setOnClickListener {
            timeboxSeekBar.progress = 0
            updateRemainTime(10800 * 1000L)
            updateSeekBar(10800 * 1000L, timeboxSeekBar)

            stopCountDown()
        }

        //timer 추가함수
        binding.timerListAddButton.setOnClickListener {
            timerList.add(timerList("timer ${timerList.size}", "Mode = -", "time = 00:00:00"))
            (binding.recyclerView.adapter as timerListAdapter).notifyItemInserted(timerList.size)
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

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = timerListAdapter(timerList,
            onClickRemoveButton = { deleteTimerList(it) },
            onClickSelectItem = { selectTimerItem(it) },
            onTimerItem = { recordTimer(it) },
            onTimeRecord = { recordTime(it) },
            onConnectedDB = {connectTimerDB(it)})

        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )

        timerTodoAdapter = timerTodoListAdapter(this)
        binding.selectRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.selectRecyclerView.adapter = timerTodoAdapter

        var checkScreen = 0
        timerTodoAdapter.setItemClickListener(object: timerTodoListAdapter.ItemClickListener{
            override fun onClick(preView: View?, view: View, position: Int, itemId: Long) {
                preView?.setBackgroundColor(Color.parseColor("#ffffff"))

                if (preView != null) {
                    if((preView.equals(view) && (checkScreen%2 == 1))) {
                        view.setBackgroundColor(Color.parseColor("#ffffff"))
                        DBselected = 0
                        checkScreen = 0
                        DBid = -1
                    }
                    else{
                        view.setBackgroundColor(Color.parseColor("#D0A4ED"))
                        DBselected = 1
                        checkScreen = 1
                        DBid = itemId
                        titleChange = view.findViewById<TextView>(R.id.timertodoTitle).text.toString()
                    }
                }
                else {
                    view.setBackgroundColor(Color.parseColor("#D0A4ED"))
                    DBselected = 1
                    checkScreen = 1
                    DBid = itemId
                    titleChange = view.findViewById<TextView>(R.id.timertodoTitle).text.toString()
                }
            }
        })
    }

    private fun connectTimerDB(position: Int) {
        if(timerList[position].timeConnected) {
            val dig = timerListTodoPopup(this)
            //basic
            var popBasic = 0.toLong()
            if(timerList[position].basictimeVar.size != 0)
                popBasic = timerList[position].basictimeVar[timerList[position].basictimeVar.size-1]
            val popupBasic = "%02d".format(popBasic/3600)+":%02d".format(popBasic/60%60)+":%02d".format(popBasic%60)

            //timebox
            var popTB = 0.toLong()
            if(timerList[position].timeboxtimeVar.size != 0)
                popTB = timerList[position].timeboxtimeVar[timerList[position].timeboxtimeVar.size-1]
            val popupTB = "%02d".format(popTB/3600)+":%02d".format(popTB/60%60)+":%02d".format(popTB%60)

            dig.show(popupBasic, timerList[position].pomodorotimeVar, popupTB)
        }
        else if(DBselected == 1) // db랑 연결
        {
            timerList[position].timername = titleChange.split(" ")[2]
            timerList[position].timeConnected = true
            timerList[position].timeConnectedID = DBid

            CoroutineScope(Dispatchers.IO).launch {
                val basic = todoViewModel.getOne(DBid).basicTimer
                val pomodoro = todoViewModel.getOne(DBid).pomodoro
                val timebox = todoViewModel.getOne(DBid).timeBox
                if(basic != "0") {
                    val basicH = basic.split(" ")[2].split(":")[0].toLong()
                    val basicM = basic.split(" ")[2].split(":")[1].toLong()
                    val basicS = basic.split(" ")[2].split(":")[2].toLong()
                    timerList[position].basictimeVar.add(basicH*3600+basicM*60+basicS)
                }
                if(pomodoro != "0") {
                    val pomo = pomodoro.split(" ")[1][0]
                    timerList[position].pomodorotimeVar = pomo-'0'
                }
                if(timebox != "0") {
                    val timeH = timebox.split(" ")[2].split(":")[0].toLong()
                    val timeM = timebox.split(" ")[2].split(":")[1].toLong()
                    val timeS = timebox.split(" ")[2].split(":")[2].toLong()
                    timerList[position].timeboxtimeVar.add(timeH*3600+timeM*60+timeS)
                }
            }
            (binding.recyclerView.adapter as timerListAdapter).notifyItemChanged(position)
        }
    }

    // timerList 삭제함수
    private fun deleteTimerList(position : Int) {
        if(timerList.size == 0){
            return
        }
        timerList.removeAt(position)
        if(selectPos == position) {
            currentSelectedTimer.text = "Selected Timer = None"
            selectPos = -1
        }
    }
    //timerList 에서 선택하기
    private fun selectTimerItem(position: Int)
    {
        currentSelectedTimer.text = "Selected Timer = ${timerList[position].timername}"
        selectPos = position
    }
    // timer MODE 변수 받아오기
    private fun recordTimer(timerMode:TextView) {
        RecordTimerMode = timerMode
        if(type == -1)
            RecordTimerMode.text = "Mode = basicTimer"
        else if(type == 0)
            RecordTimerMode.text = "Mode = pomodoro"
        else if(type == 1)
            RecordTimerMode.text = "Mode = timebox"
    }
    // TIME 기록 변수 받아오기
    fun recordTime(timeRecord : TextView) {
        RecordTime = timeRecord
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
            if(selectPos != -1) {
                RecordTime.text = "포모도로 %d회 성공!".format(timerList[selectPos].pomodorotimeVar+1)
                timerList[selectPos].timeRecord = "포모도로 %d회 성공!".format(timerList[selectPos].pomodorotimeVar+1)
                timerList[selectPos].pomodorotimeVar = timerList[selectPos].pomodorotimeVar+1

                //database에 추가
                if(timerList[selectPos].timeConnectedID != -1.toLong()) {
                    CoroutineScope(Dispatchers.IO).launch{
                        val todo = todoViewModel.getOne(timerList[selectPos].timeConnectedID)
                        todo.pomodoro = "%s".format(timerList[selectPos].timeRecord)
                        todoViewModel.update(todo)
                    }
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
        val menuItem = menu?.findItem(R.id.menu_search)
        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }
        })
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
                val mainIntent: Intent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }
            R.id.menu_item2-> Toast.makeText(this,"Timer 실행", Toast.LENGTH_SHORT).show()
            R.id.menu_item3-> {
                Toast.makeText(this,"Statistics 실행", Toast.LENGTH_SHORT).show()
                val statisticsIntent:Intent = Intent(this, StatisticsMainActivity::class.java)
                startActivity(statisticsIntent)
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

    //BASICTIMER 리스트 비어있는지 확인용
    fun isNotEmpty(list: List<*>?): Boolean {
        return list != null && list.isNotEmpty()
    }

}