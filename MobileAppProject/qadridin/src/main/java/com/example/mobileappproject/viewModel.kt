package com.example.mobileappproject

import android.R
import android.graphics.ColorSpace
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mobileappproject.PopupWindowFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Class as Class1


class TodoViewModel: ViewModel() {
    public val todoList: LiveData<MutableList<Todo>>
    private val todoRepository: TodoRepository = TodoRepository.get()
    init {
        println("date in to do ViewModel $date")
        todoList = todoRepository.list(date)
    }

    fun getOne(id: Long) = todoRepository.getTodo(id)

    fun insert(dto: Todo) = viewModelScope.launch(Dispatchers.IO) {
        todoRepository.insert(dto)
    }

    fun update(dto: Todo) = viewModelScope.launch(Dispatchers.IO) {
        todoRepository.update(dto)
    }

    fun delete(dto: Todo) = viewModelScope.launch(Dispatchers.IO) {
        todoRepository.delete(dto)
    }

}

