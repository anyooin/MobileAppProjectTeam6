package com.example.mobileappproject

class timerList(val name :String, val mode: String, val record:String) {
    var timername: String = name
    var timerMode: String = mode
    var timeRecord: String = record
    var timeConnected : Boolean = false //DB랑 연동되었는지 확인
    var timeConnectedID : Long = (-1).toLong()
    var basictimeVar = mutableListOf<Long>() //basic time 변수 저장 list
    var pomodorotimeVar : Int = 0 //pomodoro time 성공 횟수 저장
    var timeboxtimeVar = mutableListOf<Long>() //timebox time 변수 저장 list
}