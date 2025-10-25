package com.dreamyducks.navcook.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(query: Query)

    @Delete
    suspend fun delete(query: Query)

    @androidx.room.Query("SELECT * FROM search_queries")
    fun getAllQueries() : Flow<List<Query>>
}

@Entity(tableName = "search_queries")
data class Query (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val query: String
)