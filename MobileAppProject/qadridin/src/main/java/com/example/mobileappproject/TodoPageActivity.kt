package com.example.mobileappproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobileappproject.databinding.ActivityTodoPageBinding
import java.text.SimpleDateFormat
import java.time.LocalDate

class TodoPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityTodoPageBinding
    private var todo: Todo?=null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val type = intent.getStringExtra("type")

        if (type.equals("ADD")) {
            binding.btnSave.text = "추가하기"
        } else {
            todo = intent.getSerializableExtra("item") as Todo?
            binding.etTodoTitle.setText(todo!!.title)
            binding.etTodoContent.setText(todo!!.content)
            binding.btnSave.text = "수정하기"
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTodoTitle.text.toString()
            val content = binding.etTodoContent.text.toString()
          //  val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           //     LocalDate.now()
           // } else {
           //
           // }

            if (type.equals("ADD")) {
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val todo = Todo(0, title, content,  false)
                    val intent = Intent().apply {
                        putExtra("todo", todo)
                        putExtra("flag", 0)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } else {
                // 수정
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val todo = Todo(todo!!.id, title, content, todo!!.isChecked)

                    val intent = Intent().apply {
                        putExtra("todo", todo)
                        putExtra("flag", 1)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }
}