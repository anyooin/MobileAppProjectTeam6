package com.example.mobileappproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
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
}