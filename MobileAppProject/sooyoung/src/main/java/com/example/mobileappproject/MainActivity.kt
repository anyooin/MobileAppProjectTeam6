package com.example.mobileappproject

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileappproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    var initTime = 0L
    var pauseTime = 0L

    //private lateinit var binding: ActivityMainBinding

    private lateinit var remainHourTextView: TextView
    private lateinit var remainMinutesTextView: TextView
    private lateinit var remainSecondsTextView: TextView
    private lateinit var seekBar: SeekBar
    private lateinit var currentSelectedTimer: TextView

    private val datas = mutableListOf<String>()

    private var selectPos = -1 // 선택된 list
    private lateinit var RecordTimerMode: TextView
    private lateinit var RecordTime: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSounds()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        remainHourTextView = binding.remainHourTextView
        remainMinutesTextView = binding.remainMinutesTextView
        remainSecondsTextView = binding.remainSecondsTextView
        seekBar = binding.seekBar
        currentSelectedTimer = binding.currentSelectedTimer

        setSupportActionBar(binding.toolbar)

        //seekbar event handler
        binding.seekBar.setOnSeekBarChangeListener(this)

        //TimerMode
        binding.basicTimer.setOnClickListener {
            binding.basicTimerScreen.visibility = View.VISIBLE
            binding.basictimerBtn.visibility = View.VISIBLE
            binding.pomodoroScreen.visibility = View.INVISIBLE
            binding.pomodoroBtn.visibility = View.INVISIBLE
            binding.timeboxScreen.visibility = View.INVISIBLE
            binding.timeboxBtn.visibility = View.INVISIBLE
            binding.remainHourTextView.visibility = View.INVISIBLE
            binding.remainMinutesTextView.visibility = View.INVISIBLE
            binding.remainSecondsTextView.visibility = View.INVISIBLE
            binding.seekBar.visibility = View.INVISIBLE
            binding.chronometer.visibility = View.VISIBLE
            if(selectPos != -1) {
                RecordTimerMode.text = "Mode = basicTimer"
            }
        }
        binding.pomodoro.setOnClickListener {

            binding.basicTimerScreen.visibility = View.INVISIBLE
            binding.basictimerBtn.visibility = View.INVISIBLE
            binding.pomodoroScreen.visibility = View.VISIBLE
            binding.pomodoroBtn.visibility = View.VISIBLE
            binding.timeboxScreen.visibility = View.INVISIBLE
            binding.timeboxBtn.visibility = View.INVISIBLE
            binding.remainHourTextView.visibility = View.VISIBLE
            binding.remainMinutesTextView.visibility = View.VISIBLE
            binding.remainSecondsTextView.visibility = View.VISIBLE
            binding.seekBar.visibility = View.VISIBLE
            binding.chronometer.visibility = View.INVISIBLE
            updateRemainTime(60*30*1000L)
            updateSeekBar(60*30*1000L)
            if(selectPos != -1) {
                RecordTimerMode.text = "Mode = pomodoro"
            }
        }
        binding.timebox.setOnClickListener {
            binding.basicTimerScreen.visibility = View.INVISIBLE
            binding.basictimerBtn.visibility = View.INVISIBLE
            binding.pomodoroScreen.visibility = View.INVISIBLE
            binding.pomodoroBtn.visibility = View.INVISIBLE
            binding.timeboxScreen.visibility = View.VISIBLE
            binding.timeboxBtn.visibility = View.VISIBLE
            binding.remainHourTextView.visibility = View.VISIBLE
            binding.remainMinutesTextView.visibility = View.VISIBLE
            binding.remainSecondsTextView.visibility = View.VISIBLE
            binding.seekBar.visibility = View.VISIBLE
            binding.chronometer.visibility = View.INVISIBLE
            updateRemainTime(3*60*60*1000L)
            updateSeekBar(3*60*60*1000L)
            if(selectPos != -1) {
                RecordTimerMode.text = "Mode = timebox"
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
            if(selectPos != -1) {
                var recordPauseTime = SystemClock.elapsedRealtime() - binding.chronometer.base
                var h =(recordPauseTime/3600000)
                var textH = "%02d:".format(h)
                var m = (recordPauseTime-h*3600000)/60000
                var textM = "%02d:".format(m)
                var s = (recordPauseTime-h*3600000-m*60000)/1000
                var textS = "%02d".format(s)

                RecordTime.text = "time = ${textH}${textM}${textS}"
            }

        }
        binding.basictimerresetBtn.setOnClickListener {
            pauseTime = 0L
            binding.chronometer.base = SystemClock.elapsedRealtime()
            binding.chronometer.stop()
        }


        //POMODORO
        binding.pomodorostartBtn.setOnClickListener {
            if(seekBar.progress != 0) {
                startCountDown()
            }
        }
        binding.pomodorostopBtn.setOnClickListener {
            stopCountDown()
            if(selectPos != -1) {
                RecordTime.text = "time = ${remainHourTextView.text}${remainMinutesTextView.text}" +
                        "${remainSecondsTextView.text}"
            }
        }
        binding.pomodororesetBtn.setOnClickListener {
            seekBar.progress = 0
            updateRemainTime(0)
            updateSeekBar(0)
        }

        //TIMEBOX
        binding.timeboxstartBtn.setOnClickListener {
            if(seekBar.progress != 0) {
                startCountDown()
            }
        }
        binding.timeboxstopBtn.setOnClickListener {
            stopCountDown()

            if(selectPos != -1) {
                RecordTime.text = "time = ${remainHourTextView.text}${remainMinutesTextView.text}" +
                        "${remainSecondsTextView.text}"
            }
        }
        binding.timeboxresetBtn.setOnClickListener {
            seekBar.progress = 0
            updateRemainTime(0)
            updateSeekBar(0)
        }
        // Timer LIST
        for(i in 0..2) {
            datas.add("timer $i")
        }

        binding.timerListAddButton.setOnClickListener {
            datas.add("timer ${datas.size}")
            (binding.recyclerView.adapter as timerListAdapter).notifyDataSetChanged()
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = timerListAdapter(datas,
            onClickRemoveButton= {deleteTimerList(it)},
            onClickSelectItem = {selectTimerItem(it)},
            onTimerItem = {recordTimer(it)},
            onTimeRecord = {recordTime(it)})

        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

    }
    // timerList 삭제함수
    fun deleteTimerList(position : Int) {
        if(datas.size == 0){
            return
        }
        datas.removeAt(position)
        if(selectPos == position) {
            currentSelectedTimer.text = "Selected Timer = None"
            selectPos = -1
        }
    }
    // timerList 선택함수
    fun selectTimerItem(position: Int)
    {
        currentSelectedTimer.text = "Selected Timer = ${datas[position]}"
        selectPos = position
    }
    // timerList 기록함수
    fun recordTimer(timerMode:TextView)
    {
        RecordTimerMode = timerMode
        //timerMode.text = "선택된 timer"
    }
    fun recordTime(timeRecord : TextView) {
        RecordTime = timeRecord
        //timeRecord.text = "기록된 시간"
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
        super.onDestroy()
        // sound파일들이 메모리에서 제거된다.
        soundPool.release()
    }


    //Seekbar event listener
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        // seekbar 값 변경될 때마다 호출
        if(p2) {
            updateRemainTime(seekBar.progress*1000L)
        }
    }
    override fun onStartTrackingTouch(p0: SeekBar?) {
        // seekbar 첫 눌림에 호출
        stopCountDown()
    }
    override fun onStopTrackingTouch(p0: SeekBar?) {
        // seekbar 드래그 떼면 호출
        if(seekBar.progress == 0) { // 0분이면 시작안함
            stopCountDown()
        }
        else { //  0분이 아닌경우
            startCountDown()
        }
    }


    private fun stopCountDown() {
        currentCountDownTimer?.cancel() // timer 멈추기
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun startCountDown(){
        currentCountDownTimer = createCountDownTimer(seekBar.progress*1000L)
        currentCountDownTimer?.start()  // timer 시작하기

        tickingSoundId?.let {soundId->
            soundPool.play(soundId,1F,1F,0,-1,1F)
        }
    }

    private fun createCountDownTimer(initialMills:Long) =
        // 1초 마다 호출되도록 함
        object : CountDownTimer(initialMills, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                //countDownInterval 마다 호출됨
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                //timer가 종료되면 호출
                completeCountDown()
            }

        }

    private fun completeCountDown(){
        updateRemainTime(0)
        updateSeekBar(0)

        // 끝난 경우
        // 끝난 벨소리 재생함
        soundPool.autoPause()
        bellSoundId?.let {soundId->
            soundPool.play(soundId, 1F,1F,0,0,1F)
        }
    }

    // 기본적으로 함수마다 초의 단위를 통일하는게 좋음. 개발할 때 가독성이 좋음
    private fun updateRemainTime(remainMillis: Long){
        // 총 남은 초
        val remainSeconds = remainMillis/1000

        // 시만 보여줌, 분만 보여줌, 초만 보여줌
        remainHourTextView.text = "%02d:".format(remainSeconds/60/60)
        remainMinutesTextView.text = "%02d:".format(remainSeconds/60%60)
        remainSecondsTextView.text= "%02d".format(remainSeconds%60)

    }

    private fun initSounds() {
        // mp4 파일을 로드함
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun updateSeekBar(remainMillis: Long) {
        // 밀리 세컨드를 분(정수)으로 바꿔서 보여줌
        seekBar.progress = (remainMillis / 1000).toInt()
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
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


}
