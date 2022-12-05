package com.example.mobileappproject

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mobileappproject.databinding.ActivitySettingsBinding
import com.google.android.material.navigation.NavigationView

var switchOffOn = 0
class SettingsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toggle: ActionBarDrawerToggle
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    lateinit var binding: ActivitySettingsBinding


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Toolbar setting
        setSupportActionBar(binding.toolbar)
        toggle = ActionBarDrawerToggle(this, binding.drawerSetting, R.string.menu_item_open, R.string.menu_item_clos)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toggle.syncState()

        drawerLayout = binding.drawerSetting
        navigationView = binding.naView
        navigationView.itemIconTintList = null
        navigationView.setNavigationItemSelectedListener(this)

        if (switchOffOn == 1) {
            //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            val date = CalendarUtil.today.toString().split("-")
            binding.drawerSetting.background = when (date[1]) {
                "12", "01", "02" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                "03", "04", "05" -> resources.getDrawable(R.drawable.spring1_removebg_preview)
                "06", "07", "08" -> resources.getDrawable(R.drawable.summer1_removebg_preview)
                "09", "10", "11" -> resources.getDrawable(R.drawable.autumn1)
                else -> {
                    null
                }
            }
            binding.switchField.isChecked = true
            switchOffOn = 1

        } else {
            //    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.drawerSetting.background = null
            binding.switchField.isChecked = false
            switchOffOn = 0
        }


        binding.switchField.setOnCheckedChangeListener { _, isChecked ->
            //background
            if (binding.switchField.isChecked) {
             //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                val date = CalendarUtil.today.toString().split("-")
                    binding.drawerSetting.background = when (date[1]) {
                        "12", "01", "02" -> resources.getDrawable(R.drawable.winter1_removebg_preview)
                        "03", "04", "05" -> resources.getDrawable(R.drawable.spring1_removebg_preview)
                        "06", "07", "08" -> resources.getDrawable(R.drawable.summer1_removebg_preview)
                        "09", "10", "11" -> resources.getDrawable(R.drawable.autumn1)
                        else -> {
                            null
                        }
                    }

                switchOffOn = 1
                binding.switchField.isChecked = true

            } else {
            //    AppCompatDelegate.setDefaultNightMode(App
                //    CompatDelegate.MODE_NIGHT_NO)
                binding.drawerSetting.background= null
                binding.switchField.isChecked = false
                switchOffOn = 0
            }
        }


    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_item1-> {
                Toast.makeText(this,"Calendar 실행", Toast.LENGTH_SHORT).show()
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }
            R.id.menu_item2-> {
                Toast.makeText(this,"Timer 실행", Toast.LENGTH_SHORT).show()
                val timerIntent = Intent(this, TimerMainActivity::class.java)
                startActivity(timerIntent)
            }
            R.id.menu_item3-> {
                Toast.makeText(this,"Statistics 실행", Toast.LENGTH_SHORT).show()
                val statisticsIntent = Intent(this, StatisticsMainActivity::class.java)
                startActivity(statisticsIntent)
            }
            R.id.menu_item4-> {
                Toast.makeText(this, "Settings 실행", Toast.LENGTH_SHORT).show()
                val settingIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingIntent)
            }
        }
        return false
    }

}

