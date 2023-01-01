package com.example.news.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import com.example.news.models.Article
import retrofit2.Converter

@Database(
    entities = [Article::class],
    version = 1
)

@TypeConverter(Converter :: class)
abstract class ArticleDatabase :RoomDatabase(){
    abstract fun getArticleDao(): ArticleDao

    companion object{
        @Volatile

        private var instance: ArticleDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance?: createDatabase(context).also{ instance = it}
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ArticleDatabase::class.java,
                "article_db.db"
            ).build()
        }
    }