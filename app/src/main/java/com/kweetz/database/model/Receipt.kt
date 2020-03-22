package com.kweetz.database.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Receipt")
data class Receipt(

        @PrimaryKey(autoGenerate = true) var ID: Int = 0,
        @ColumnInfo(name = "ReceiptNo") var receiptNo: String = "",
        @ColumnInfo(name = "ReceiptDate") var receiptDate: String = "",
        @ColumnInfo(name = "ReceiptIssuer") var receiptIssuer: String = "",
        @ColumnInfo(name = "ReceiptTotal") var receiptTotal: String = "",
        @ColumnInfo(name = "ReceiptDescription") var receiptDescription: String = "",
        @ColumnInfo(name = "ReceiptFullText") var receiptFullText: String = ""
) :Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(ID)
        parcel.writeString(receiptNo)
        parcel.writeString(receiptDate)
        parcel.writeString(receiptIssuer)
        parcel.writeString(receiptTotal)
        parcel.writeString(receiptDescription)
        parcel.writeString(receiptFullText)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Receipt> {
        override fun createFromParcel(parcel: Parcel): Receipt {
            return Receipt(parcel)
        }

        override fun newArray(size: Int): Array<Receipt?> {
            return arrayOfNulls(size)
        }
    }
}