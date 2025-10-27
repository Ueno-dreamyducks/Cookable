package com.dreamyducks.navcook.data.database.recentRecipes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dreamyducks.navcook.network.Recipe

@Database(entities = [RecentRecipe::class], version = 2, exportSchema = false)
abstract class RecentRecipesDatabase : RoomDatabase() {
    abstract fun recentRecipeDao() : RecentRecipeDao

    companion object {
        @Volatile
        private var Instance: RecentRecipesDatabase? = null

        fun getDatabase(context: Context) : RecentRecipesDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, RecentRecipesDatabase::class.java, "history_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it}
            }
        }
    }
}