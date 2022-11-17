package com.example.mobileappproject

import androidx.lifecycle.LiveData
import androidx.room.*
import java.io.Serializable

//To do Table
//to do.kt
@Entity(tableName = "todoTable")
class Todo(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "timestamp") val timestamp: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "isChecked") var isChecked: Boolean
): Serializable {
}

//To do Database.kt
@Database(entities = [Todo::class], version = 1, exportSchema = false)
abstract class TodoDatabase: RoomDatabase() {
    abstract fun todoDao(): TodoDao
}

//To doDao
@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dto: Todo)

    @Query("select * from todoTable where date = (:date)")
    fun list(date: String): LiveData<MutableList<Todo>>

    @Query("select * from todoTable where id = (:id)")
    fun selectOne(id: Long): Todo

    @Update
    suspend fun update(dto: Todo)

    @Delete
    fun delete(dto: Todo)
}