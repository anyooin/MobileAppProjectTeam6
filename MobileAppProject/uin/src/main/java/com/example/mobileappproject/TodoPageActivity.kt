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
import com.example.mobileappproject.databinding.ActivityTodoPageBinding
import com.example.mobileappproject.databinding.PopupWindowFragementBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class TodoPageActivity : AppCompatActivity() {
    lateinit var binding: com.example.mobileappproject.databinding.ActivityTodoPageBinding
    private var todo: Todo?=null

   // @SuppressLint("SimpleDateFormat")
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
       binding.todolistBackBt.setOnClickListener {
           finish()
       }
       val items = resources.getStringArray(R.array.category_list)
       val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,items)

       binding.category.adapter = categoryAdapter
//       binding.category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//           override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//               when(position) {
//                   1 ->
//               }
//           }
//       }


        val type = intent.getStringExtra("type")

        if (type.equals("ADD")) {
            binding.btnSave.text = "추가하기"
            binding.todolistDeleteBt.visibility = View.INVISIBLE
        } else {
            todo = intent.getSerializableExtra("item") as Todo?
            binding.btnSave.text = "수정하기"
            //제목 + 내용정보
            binding.etTodoTitle.setText(todo!!.title)
            binding.etTodoContent.setText(todo!!.content)
            //날짜 + 시간정보
            binding.startDate.text = todo!!.startDate
            binding.endDate.setText(todo!!.endDate)
            binding.startTime.setText(todo!!.startTime)
            binding.endTime.setText(todo!!.endTime)
            //그외 세팅정보
            binding.isTimer.setChecked(todo!!.isTimer)
            binding.category.setSelection(todo!!.categoryNum)

            //set backBtn and deleteBtn
            binding.todolistDeleteBt.visibility = View.VISIBLE
            binding.todolistDeleteBt.setOnClickListener {
                Toast.makeText(this, "삭제", Toast.LENGTH_SHORT).show()
                TodoViewModel().delete(this.todo!!)
                finish()
            }
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTodoTitle.text.toString()
            val content = binding.etTodoContent.text.toString()
            val startDate = binding.startDate.text.toString()
            val endDate = binding.endDate.text.toString()
            val startTime = binding.startTime.text.toString()
            val endTime = binding.endTime.text.toString()
            val isTimer = binding.isTimer.isChecked()
            val category = binding.category.selectedItemPosition

            if (type.equals("ADD")) {  //추가하기
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val todo = Todo(0, title, content, startDate, endDate, startTime, endTime, date,false, isTimer, category)
                    val intent = Intent().apply {
                        putExtra("todo", todo)
                        putExtra("flag", 0)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } else { // 수정하기
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    val todo = Todo(todo!!.id, title, content, startDate, endDate, startTime, endTime, date, todo!!.isChecked, isTimer, category)
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
                    binding.startDate.text = String.format("%d-%d-%d", year, month+1, DayOfMonth)
                }
                else if (id == "endDate") {
                    binding.endDate.text = String.format("%d-%d-%d", year, month+1, DayOfMonth)
                }
            }
        }, year, month, day)

        mDatePicker.show()
    }
}