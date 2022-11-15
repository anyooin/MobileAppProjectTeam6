package com.example.mobileappproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

//todo list의 추가와 수정을 담당
class EditTodoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_todo)
    }
}