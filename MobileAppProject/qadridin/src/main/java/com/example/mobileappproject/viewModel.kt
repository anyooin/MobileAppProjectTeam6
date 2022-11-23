package com.example.mobileappproject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TodoViewModel: ViewModel() {
    val todoList: LiveData<MutableList<Todo>>
    private val todoRepository: TodoRepository = TodoRepository.get()
    init {
        todoList = todoRepository.list(date)
    }

    fun getCurrentDay(days: String) = todoRepository.getCurrentDay(days)

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


class DiaryViewModel: ViewModel() {
    val diaryItemsList: LiveData<MutableList<Diary>>
    val diaryRepository: DiaryRepository = DiaryRepository.get()
    init {
        diaryItemsList = diaryRepository.list()
    }

    fun getCurrentDay(days: String) = diaryRepository.getCurrentDay(days)

    fun getOne(id: Long) = diaryRepository.getTodo(id)

    fun insert(dto: Diary) = viewModelScope.launch(Dispatchers.IO) {
        diaryRepository.insert(dto)
    }

    fun update(dto: Diary) = viewModelScope.launch(Dispatchers.IO) {
        diaryRepository.update(dto)
    }

    fun delete(dto: Diary) = viewModelScope.launch(Dispatchers.IO) {
        diaryRepository.delete(dto)
    }

}

