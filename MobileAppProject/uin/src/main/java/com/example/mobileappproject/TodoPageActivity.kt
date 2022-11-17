package com.example.mobileappproject

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import com.example.mobileappproject.databinding.ActivityTodoPageBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class TodoPageActivity : AppCompatActivity() {
    lateinit var binding: ActivityTodoPageBinding
    private var todo: Todo?=null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startTime.setOnClickListener{
            setupTimePicker("startTime")
            Log.d("uin", "startTime")
        }
        binding.endTime.setOnClickListener{
            setupTimePicker("endTime")
            Log.d("uin", "endTime")
        }
        binding.startDate.setOnClickListener{
            setupDatePicker("startDate")
            Log.d("uin", "startDate")
        }
        binding.endDate.setOnClickListener{
            setupDatePicker("endDate")
            Log.d("uin", "endDate")
        }

        val type = intent.getStringExtra("type")

        if (type.equals("ADD")) {
            binding.btnSave.text = "추가하기"
        } else {
            todo = intent.getSerializableExtra("item") as Todo?  //
            binding.etTodoTitle.setText(todo!!.title)
            binding.etTodoContent.setText(todo!!.content)
            binding.btnSave.text = "수정하기"
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTodoTitle.text.toString()
            val content = binding.etTodoContent.text.toString()
            // 데이터베이스 작업을 쉽게하기위해 timestamp2 만들기
            val timestamp = binding.startDate.text.toString() + " " +
                binding.startTime.text.toString() + "~" + binding.endDate.text.toString() + " " +
                    binding.endTime.text.toString()
            //val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm").format(System.currentTimeMillis())

            if (type.equals("ADD")) {
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val todo = Todo(0, title, content, timestamp,false)
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
                    val todo = Todo(todo!!.id, title, content, timestamp, todo!!.isChecked)

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

    private fun setupTimePicker(id: String) {
        val mTimePicker: TimePickerDialog
        val mCurrentTime = Calendar.getInstance()
        val hour = mCurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mCurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                if (id == "startTime") {
                    binding.startTime.text = String.format("%d:%d", hourOfDay, minute)
                }
                else if (id == "endTime") {
                    binding.endTime.text = String.format("%d:%d", hourOfDay, minute)
                }
            }
        }, hour, minute, false)

        mTimePicker.show()

        /*
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            timeString = "${hourOfDay}시 ${minute}분"
            result.text = "날짜/시간 : "+dateString + " / " + timeString
        }
        TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()

        val cal = Calendar.getInstance()
        mTimePicker = TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                if (id == "startTime") {
                    binding.startTime.text = String.format("%d : %d", hourOfDay, minute)
                }
                else if (id == "endTime") {
                    binding.endTime.text = String.format("%d : %d", hourOfDay, minute)
                }
            }
            TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE),true).show()
        }
        */
    }

    private fun setupDatePicker(id: String) {
        val mDatePicker: DatePickerDialog
        val mCurrentDate = Calendar.getInstance()
        val year = mCurrentDate.get(Calendar.YEAR)
        val month = mCurrentDate.get(Calendar.MONTH)
        val day = mCurrentDate.get(Calendar.DAY_OF_MONTH)

        mDatePicker = DatePickerDialog(this, object : DatePickerDialog.OnDateSetListener{

            override fun onDateSet(view: DatePicker?, year: Int, month: Int, DayOfMonth: Int) {
                if (id == "startDate") {
                    binding.startDate.text = String.format("%d:%d:%d", year, month, DayOfMonth)
                }
                else if (id == "endDate") {
                    binding.endDate.text = String.format("%d:%d:%d", year, month, DayOfMonth)
                }
            }
        }, year, month, day)

        mDatePicker.show()
    }
}