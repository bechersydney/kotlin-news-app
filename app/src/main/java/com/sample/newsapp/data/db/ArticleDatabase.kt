package com.sample.newsapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sample.newsapp.data.db.models.Article

@Database(
    entities = [Article::class],
    version = 1
)
// annotate for converters
@TypeConverters(Converters::class)
// abstract: requirement for Room
abstract class ArticleDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao

    companion object {
        // thread will automatically sees if instance is changed(always updated instance return)
        @Volatile
        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        // this operator invoke will run after creating instance
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        // use named parameter always
        private fun createDatabase(context: Context): ArticleDatabase =
            Room.databaseBuilder(
                context = context.applicationContext,
                klass = ArticleDatabase::class.java,
                name = "Article_db.db"
            ).build()
    }
}