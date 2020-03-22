package com.kweetz.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kweetz.database.model.Receipt

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM Receipt")
    fun getAllReceipts(): List<Receipt>

    @Query("SELECT * FROM Receipt where ID=:pos")
    fun getReceipt(vararg pos:Int):Receipt
    @Insert
    fun insertAll(vararg receipts: Receipt)

    @Query("SELECT COUNT(ID) FROM Receipt")
    fun getCount(): Int
    @Delete
    fun delete(receipt: Receipt)
}