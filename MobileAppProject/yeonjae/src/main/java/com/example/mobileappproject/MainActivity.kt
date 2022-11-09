package com.example.mobileappproject

import android.annotation.SuppressLint
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.Menu
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileappproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById(R.id.remainMinutesTextView)
    }
    private val remainSecondsTextView: TextView by lazy {
        findViewById(R.id.remainSecondsTextView)
    }
    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    private val soundPool = SoundPool.Builder().build()

    private var currentCountDownTimer: CountDownTimer? = null
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    var initTime = 0L
    var pauseTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //<<pomodoro 하면서 추가
        setContentView(R.layout.activity_main)
        bindViews()
        initSounds()
        //pomodoro 하면서 추가>>

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

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

    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    // 사용자가 seek바를 변경한 경우 시간 변경.
                    if (fromUser){
                        updateRemainTime(progress*60*1000L)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // 스크롤을 통해 다시 타이머를 시작하는 경우
                    // 현재 카운트 다운을 멈춘다.
                    stopCountDown()

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar?:return
                    // TrackingTouch가 멈췄을 때 카운트 다운을 시작한다.
                    if (seekBar.progress ==0){
                        stopCountDown()
                    }else{
                        startCountDown()
                    }
                }

            }
        )
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }

    private fun startCountDown(){
        currentCountDownTimer = createCountDownTimer(seekBar.progress*60*1000L)
        currentCountDownTimer?.start()

        tickingSoundId?.let {soundId->
            soundPool.play(soundId,1F,1F,0,-1,1F)
        }
    }

    private fun createCountDownTimer(initialMills:Long) =
        // 1초 마다 호출되도록 함
        object : CountDownTimer(initialMills, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
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
