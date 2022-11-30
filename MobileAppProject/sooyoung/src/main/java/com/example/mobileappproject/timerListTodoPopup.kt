package com.example.mobileappproject

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import com.example.mobileappproject.databinding.ActivityTimerListTodoPopupBinding
import com.example.mobileappproject.databinding.ActivityTimerPopupBinding

class timerListTodoPopup(private val context: AppCompatActivity ) {

    private lateinit var binding: ActivityTimerPopupBinding
    private val dig = Dialog(context)

    fun show(basic: String, pomodoro: Int, timeBox: String) {
        binding = ActivityTimerPopupBinding.inflate(context.layoutInflater)

        dig.requestWindowFeature(Window.FEATURE_NO_TITLE) // 타이틀 바 제거
        dig.setContentView(binding.root) // 다이얼로그에 사용할 xml 파일을 불러옴
        dig.setCancelable(false)

        //부모 액티비티에서 전달받은 텍스트 세팅
        binding.timertodoPopupBasic.text = "basic = %s".format(basic)
        binding.timertodoPopupPomodoro.text = "pomodoro = %s회 성공".format(pomodoro)
        binding.timertodoPopupTimebox.text = "timeBox = %s".format(timeBox)

        binding.timerPopupOk.setOnClickListener {
            dig.dismiss()
        }
        dig.show()
    }
}