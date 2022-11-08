package com.example.mobileappproject

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        findViewById(R.id.txt_remainMinutes)
    }
    private val seekBar: SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    var initTime = 0L
    var pauseTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //<<pomodoro 하면서 추가
        setContentView(R.layout.activity_main)

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
            binding.txtRemainMinutes.visibility = View.INVISIBLE
            binding.txtRemainSeconds.visibility = View.INVISIBLE
            binding.seekBar.visibility = View.INVISIBLE
            binding.chronometer.visibility = View.VISIBLE
        }
        binding.pomodoro.setOnClickListener {
            bindViews()
            binding.basicTimerScreen.visibility = View.INVISIBLE
            binding.basictimerBtn.visibility = View.INVISIBLE
            binding.pomodoroScreen.visibility = View.VISIBLE
            binding.pomodoroBtn.visibility = View.VISIBLE
            binding.timeboxScreen.visibility = View.INVISIBLE
            binding.timeboxBtn.visibility = View.INVISIBLE
            binding.txtRemainMinutes.visibility = View.VISIBLE
            binding.txtRemainSeconds.visibility = View.VISIBLE
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
            binding.txtRemainMinutes.visibility = View.INVISIBLE
            binding.txtRemainSeconds.visibility = View.INVISIBLE
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
    //POMODORO
    private fun bindViews(){
        seekBar.setOnSeekBarChangeListener(
            object :SeekBar.OnSeekBarChangeListener{
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    //long 타입?
                    remainMinutesTextView.text="%02d".format(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    TODO("Not yet implemented")
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    TODO("Not yet implemented")
                }
            }
        )
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