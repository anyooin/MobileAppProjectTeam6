package com.example.mobileappproject

class timerList(val name :String, val mode: String, val record:String) {
    var timername: String = name
    var timerMode: String = mode
    var timeRecord: String = record
    var basictimeVar = mutableListOf<Long>() //basic time 변수 저장 list
    var pomodorotimeVar = mutableListOf<String>() //pomodoro time 변수 저장 list
    var timeboxtimeVar = mutableListOf<Long>() //timebox time 변수 저장 list
}