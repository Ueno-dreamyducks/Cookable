package com.dreamyducks.navcook.data.database.searchQueries

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Query::class], version = 1, exportSchema = false)
abstract class SearchQueriesDatabase : RoomDatabase() {
    abstract fun searchDao(): SearchDao

    companion object {
        @Volatile
        private var Instance: SearchQueriesDatabase? = null

        fun getDatabase(context: Context) : SearchQueriesDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SearchQueriesDatabase::class.java, "searchQuery_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}