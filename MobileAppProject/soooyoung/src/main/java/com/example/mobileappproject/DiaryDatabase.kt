package com.example.mobileappproject

import androidx.lifecycle.LiveData
import androidx.room.*
import java.io.Serializable

@Entity(tableName = "DiaryTable")
class Diary(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "image") val image: String //Should be consider
): Serializable {
}

//To do Database.kt
@Database(entities = [Diary::class], version = 1, exportSchema = false)
abstract class DiaryDatabase: RoomDatabase() {
    abstract fun DiaryDao(): DiaryDao
}

//To doDao
@Dao
interface DiaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dto: Diary)

    @Query("select * from DiaryTable")
    fun list(): LiveData<MutableList<Diary>>

    @Query("select * from DiaryTable where date = (:date)")
    fun getCurrentDay(date: String): LiveData<MutableList<Diary>>

    @Query("select * from DiaryTable where id = (:id)")
    fun selectOne(id: Long): Diary

    @Update
    suspend fun update(dto: Diary)

    @Delete
    fun delete(dto: Diary)
}