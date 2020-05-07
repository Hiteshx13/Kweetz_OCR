package com.kweetz.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kweetz.R
import com.kweetz.database.dao.ReceiptDao
import com.kweetz.database.model.Receipt


@Database(entities = arrayOf(Receipt::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    companion object {

        open fun getDatabase(context: Context): AppDatabase {
            var database: AppDatabase? = null
            var db_name = context.getString(R.string.app_name).toString() + ".db"
            if (database == null) {
                synchronized(AppDatabase::class) {
                    if (database == null) {
                        database = Room.databaseBuilder(
                                context,
                                AppDatabase::class.java, db_name
                        ).allowMainThreadQueries().addCallback(object : RoomDatabase.Callback() {

                        }).build()
                    }
                }
            }
            return database!!
        }
    }

    /* Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,applicationContext.getString(R.string.app_name
            )
            ).allowMainThreadQueries().build()*/
    abstract fun productsDao(): ReceiptDao
    /* @Database(entities = arrayOf(Product::class), version = 1)
     abstract class AppDatabase : RoomDatabase() {

     }*/
}