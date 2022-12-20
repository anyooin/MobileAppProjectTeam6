package com.example.mobileappproject

import android.app.Application
import com.example.mobileappproject.TodoRepository

class ApplicationClass: Application() {

    override fun onCreate() {
        super.onCreate()

        TodoRepository.initialize(this)
        DiaryRepository.initialize(this)
    }
}