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
    fun readAllData() : LiveData<MutableList<Todo>> = todoDao.readAllData()

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

private const val DATABASE_Diary = "diary-database.db"
class DiaryRepository private constructor(context: Context){

    private val database: DiaryDatabase = Room.databaseBuilder(
        context.applicationContext,
        DiaryDatabase::class.java,
        DATABASE_Diary
    ).build()

    private val DiaryDao = database.DiaryDao()

    fun list(): LiveData<MutableList<Diary>> {
        return  DiaryDao.list()
    }

    fun getCurrentDay(day: String): LiveData<MutableList<Diary>> = DiaryDao.getCurrentDay(day)

    fun getTodo(id: Long): Diary = DiaryDao.selectOne(id)

    fun insert(dto: Diary) = DiaryDao.insert(dto)

    suspend fun update(dto: Diary) = DiaryDao.update(dto)

    fun delete(dto: Diary) = DiaryDao.delete(dto)

    companion object {
        private var INSTANCE: DiaryRepository?=null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = DiaryRepository(context)
            }
        }

        fun get(): DiaryRepository {
            return INSTANCE ?:
            throw IllegalStateException("DiaryRepository must be initialized")
        }
    }
}