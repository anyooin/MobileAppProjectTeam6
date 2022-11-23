package com.example.mobileappproject
import android.content.Context
import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.mobileappproject.Todo
import com.example.mobileappproject.TodoDatabase

private const val DATABASE_NAME = "todo-database.db"
class TodoRepository private constructor(context: Context){

    private val database: TodoDatabase = Room.databaseBuilder(
        context.applicationContext,
        TodoDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val todoDao = database.todoDao()

    fun list(dateInPopup: String): LiveData<MutableList<Todo>> {
        println("Date in TodoRepository Class $dateInPopup")
        return  todoDao.list(dateInPopup)
    }

    fun getCurrentDay(day: String): LiveData<MutableList<Todo>> = todoDao.getCurrentDay(day)

    fun getTodo(id: Long): Todo = todoDao.selectOne(id)

    fun insert(dto: Todo) = todoDao.insert(dto)

    suspend fun update(dto: Todo) = todoDao.update(dto)

    fun delete(dto: Todo) = todoDao.delete(dto)

    companion object {
        private var INSTANCE: TodoRepository?=null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = TodoRepository(context)
            }
        }

        fun get(): TodoRepository {
            return INSTANCE ?:
            throw IllegalStateException("TodoRepository must be initialized")
        }
    }
}