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
    @ColumnInfo(name = "startDate") val startDate: String,
    @ColumnInfo(name = "endDate") val endDate: String,
    @ColumnInfo(name = "startTime") val startTime: String,
    @ColumnInfo(name = "endTime") val endTime: String,
    @ColumnInfo(name = "date") val date: String,  //생성한 날짜 -> 선택한날짜?
    @ColumnInfo(name = "isChecked") var isChecked: Boolean,
    @ColumnInfo(name = "isTimer") var isTimer: Boolean,
    @ColumnInfo(name = "categoryNum") var categoryNum: Int
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

    @Query("select * from todoTable")
    fun readAllData() : LiveData<MutableList<Todo>>

    @Query("select * from todoTable where date = (:date)")
    fun list(date: String): LiveData<MutableList<Todo>>

    @Query("select * from todoTable where date = (:date)")
    fun getCurrentDay(date: String): LiveData<MutableList<Todo>>
    /*
    @Query("SELECT * FROM todoTable WHERE date BETWEEN date(:from) AND date(:to)")
    fun fetchUserBetweenDate(from: String?, to: String?): LiveData<List<User?>?>?
    */

    @Query("select * from todoTable where id = (:id)")
    fun selectOne(id: Long): Todo

    @Update
    suspend fun update(dto: Todo)

    @Delete
    fun delete(dto: Todo)
}