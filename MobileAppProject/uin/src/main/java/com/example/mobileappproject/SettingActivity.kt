package com.example.mobileappproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SwitchCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mobileappproject.databinding.ActivitySettingBinding
import com.google.android.material.navigation.NavigationView

class SettingActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    //navigation
    private lateinit var navigationView: NavigationView
    private lateinit var drawerLayout: DrawerLayout
    //navigation

    lateinit var binding: ActivitySettingBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 네비게이션 드로어 생성
        drawerLayout = binding.drawerLayout//findViewById(R.id.drawer_layout)

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

    // 드로어 내 아이템 클릭 이벤트 처리하는 함수
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_item1-> {
                Toast.makeText(this,"Calendar 실행", Toast.LENGTH_SHORT).show()
                val MainIntent = Intent(this, MainActivity::class.java)
                startActivity(MainIntent)
            }
            R.id.menu_item2-> {
                Toast.makeText(this,"Timer 실행", Toast.LENGTH_SHORT).show()
                val timerIntent = Intent(this, TimerMainActivity::class.java)
                startActivity(timerIntent)
            }
            R.id.menu_item3-> {
                Toast.makeText(this,"Statistics 실행", Toast.LENGTH_SHORT).show()
                val StatisticsIntent = Intent(this, StatisticsMainActivity::class.java)
                startActivity(StatisticsIntent)
            }
            R.id.menu_item4-> Toast.makeText(this,"Settings 실행", Toast.LENGTH_SHORT).show()
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }
}