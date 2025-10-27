package com.dreamyducks.navcook.data.database.recentRecipes

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.dreamyducks.navcook.network.Recipe
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentRecipeDao {
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insert(recipe: RecentRecipe)

    @Query("SELECT * FROM recipes_history")
    fun getAllRecentRecipesStream() : Flow<List<RecentRecipe>>
}

@Entity(tableName = "recipes_history")
data class RecentRecipe(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,
    val title: String = "",
    val thumbnail: String = "",
)


fun Recipe.toReduced(): RecentRecipe {
    return RecentRecipe(
        id = this.id,
        title = this.title,
        thumbnail = this.thumbnail
    )
}