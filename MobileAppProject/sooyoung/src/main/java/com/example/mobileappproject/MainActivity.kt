package com.example.mobileappproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mobileappproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var initTime = 0L
    var pauseTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        //TimerMode
        binding.basicTimer.setOnClickListener {
            binding.basicTimerScreen.visibility = View.VISIBLE
            binding.pomodoroScreen.visibility = View.INVISIBLE
            binding.timeboxScreen.visibility = View.INVISIBLE
        }
        binding.pomodoro.setOnClickListener {
            binding.basicTimerScreen.visibility = View.INVISIBLE
            binding.pomodoroScreen.visibility = View.VISIBLE
            binding.timeboxScreen.visibility = View.INVISIBLE
        }
        binding.timebox.setOnClickListener {
            binding.basicTimerScreen.visibility = View.INVISIBLE
            binding.pomodoroScreen.visibility = View.INVISIBLE
            binding.timeboxScreen.visibility = View.VISIBLE
        }


        //TIMER
        binding.startBtn.setOnClickListener {
            binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
            binding.chronometer.start()
        }

        binding.stopBtn.setOnClickListener {
            binding.chronometer.base - SystemClock.elapsedRealtime()
            binding.chronometer.stop()
        }

        binding.resetBtn.setOnClickListener {
            pauseTime = 0L
            binding.chronometer.base = SystemClock.elapsedRealtime()
            binding.chronometer.stop()
        }

        // LIST
        val datas = mutableListOf<String>()
        for(i in 1..5) {
            datas.add("timer $i")
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = recyclerAdapter(datas)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

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