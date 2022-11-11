package com.example.mobileappproject

import android.annotation.SuppressLint
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

    lateinit private var remainMinutesTextView: TextView
    lateinit private var remainSecondsTextView: TextView
    lateinit private var seekBar: SeekBar
//    private val remainMinutesTextView: TextView by lazy {
//        binding.remainMinutesTextView
//    }
//    private val remainSecondsTextView: TextView by lazy {
//        binding.remainSecondsTextView
//    }
//    private val seekBar: SeekBar by lazy {
//        binding.seekBar
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //<<pomodoro 하면서 추가
        //setContentView(R.layout.activity_main)
        initSounds()
        //pomodoro 하면서 추가>>

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        remainMinutesTextView = binding.remainMinutesTextView
        remainSecondsTextView = binding.remainSecondsTextView
        seekBar = binding.seekBar

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
            binding.remainMinutesTextView.visibility = View.INVISIBLE
            binding.remainSecondsTextView.visibility = View.INVISIBLE
            binding.seekBar.visibility = View.INVISIBLE
            binding.chronometer.visibility = View.VISIBLE
        }
        binding.pomodoro.setOnClickListener {

            binding.basicTimerScreen.visibility = View.INVISIBLE
            binding.basictimerBtn.visibility = View.INVISIBLE
            binding.pomodoroScreen.visibility = View.VISIBLE
            binding.pomodoroBtn.visibility = View.VISIBLE
            binding.timeboxScreen.visibility = View.INVISIBLE
            binding.timeboxBtn.visibility = View.INVISIBLE
            binding.remainMinutesTextView.visibility = View.VISIBLE
            binding.remainSecondsTextView.visibility = View.VISIBLE
            binding.seekBar.visibility = View.VISIBLE
            binding.chronometer.visibility = View.INVISIBLE

        }
        binding.timebox.setOnClickListener {
            binding.basicTimerScreen.visibility = View.INVISIBLE
            binding.basictimerBtn.visibility = View.INVISIBLE
            binding.pomodoroScreen.visibility = View.INVISIBLE
            binding.pomodoroBtn.visibility = View.INVISIBLE
            binding.timeboxScreen.visibility = View.VISIBLE
            binding.timeboxBtn.visibility = View.VISIBLE
            binding.remainMinutesTextView.visibility = View.INVISIBLE
            binding.remainSecondsTextView.visibility = View.INVISIBLE
            binding.seekBar.visibility = View.INVISIBLE
            binding.chronometer.visibility = View.INVISIBLE
        }


        //TIMER
        binding.basictimerstartBtn.setOnClickListener {
            binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
            binding.chronometer.start()
        }
        binding.basictimerstopBtn.setOnClickListener {
            binding.chronometer.base - SystemClock.elapsedRealtime()
            binding.chronometer.stop()
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
        }
        binding.pomodororesetBtn.setOnClickListener {
            seekBar.progress = 0
            updateRemainTime(0)
            updateSeekBar(0)
        }

        //TIMEBOX

        // LIST
        val datas = mutableListOf<String>()
        for(i in 1..5) {
            datas.add("timer $i")
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = recyclerAdapter(datas)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

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
            Log.d("Lee", "seekBar changed -- true progress : ${seekBar.progress}")
            updateRemainTime(seekBar.progress*60*1000L)
        }
        Log.d("Lee", "seekBar changed")
    }
    override fun onStartTrackingTouch(p0: SeekBar?) {
        // seekbar 첫 눌림에 호출
        stopCountDown()
        Log.d("Lee", "seekBar pressed")
    }
    override fun onStopTrackingTouch(p0: SeekBar?) {
        // seekbar 드래그 떼면 호출
        seekBar?:return
        Log.d("Lee", "seekBar exited. seekBar.progress = ${seekBar.progress}")
        if(seekBar.progress == 0) { // 0분이면 시작안함
            stopCountDown()
        }
        else { //  0분이 아닌경우
            startCountDown()
        }
    }


    private fun stopCountDown() {
        Log.d("Lee", "timer stop")
        currentCountDownTimer?.cancel() // timer 멈추기
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun startCountDown(){
        Log.d("Lee", "timer start")
        currentCountDownTimer = createCountDownTimer(seekBar.progress*60*1000L)
        currentCountDownTimer?.start()  // timer 시작하기


        tickingSoundId?.let {soundId->
            soundPool.play(soundId,1F,1F,0,-1,1F)
        }
    }

    private fun createCountDownTimer(initialMills:Long) =
        // 1초 마다 호출되도록 함
        object : CountDownTimer(initialMills, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                Log.d("Lee", "called timer")
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

        // 분만 보여줌, 초만 보여줌
        remainMinutesTextView.text = "%02d:".format(remainSeconds/60)
        remainSecondsTextView.text= "%02d".format(remainSeconds%60)

    }

    private fun initSounds() {
        // mp4 파일을 로드함
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)
    }

    private fun updateSeekBar(remainMillis: Long) {
        // 밀리 세컨드를 분(정수)으로 바꿔서 보여줌
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
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

