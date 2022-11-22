package com.example.mobileappproject
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.mobileappproject.Todo
import com.example.mobileappproject.databinding.ActivityTodoPageBinding
import com.example.mobileappproject.databinding.ActivityWritingDiaryPageBinding
import com.example.mobileappproject.databinding.PopupWindowFragementBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class WritingDiaryPage : AppCompatActivity() {
    private var diary: Diary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityWritingDiaryPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("type")

        if (type.equals("ADD")) {
            binding.btnSave.text = "추가하기"
            binding.diaryPageDeleteBt.visibility = View.INVISIBLE
        } else {
            diary = intent.getSerializableExtra("item") as Diary?
            binding.btnSave.text = "수정하기"
            //제목 + 내용정보
            binding.etTodoTitle.setText(diary!!.title)
            binding.etTodoContent.setText(diary!!.content)

            //set backBtn and deleteBtn
            binding.diaryPageDeleteBt.visibility = View.VISIBLE
            binding.diaryPageDeleteBt.setOnClickListener {
                Toast.makeText(this, "삭제", Toast.LENGTH_SHORT).show()
                DiaryViewModel().delete(this.diary!!)
                finish()
            }
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTodoTitle.text.toString()
            val content = binding.etTodoContent.text.toString()
            val date = CalendarUtil.today.toString()
            //should be developed
            val image_src = "Noting"

            if (type.equals("ADD")) {  //추가하기
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val diary = Diary(0, title, content, date, image_src)
                    val intent = Intent().apply {
                        putExtra("diary", diary)
                        putExtra("flag", 0)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } else { // 수정하기
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val diary = Diary(diary!!.id, title, content, date, image_src)
                    val intent = Intent().apply {
                        putExtra("diary", diary)
                        putExtra("flag", 1)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }
}