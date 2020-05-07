package com.kweetz.database.dao;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.kweetz.database.model.Receipt;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public final class ReceiptDao_Impl implements ReceiptDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter __insertionAdapterOfReceipt;

  private final EntityDeletionOrUpdateAdapter __deletionAdapterOfReceipt;

  public ReceiptDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReceipt = new EntityInsertionAdapter<Receipt>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Receipt`(`ID`,`ReceiptNo`,`ReceiptDate`,`ReceiptIssuer`,`ReceiptTotal`,`ReceiptDescription`,`ReceiptFullText`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Receipt value) {
        stmt.bindLong(1, value.getID());
        if (value.getReceiptNo() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getReceiptNo());
        }
        if (value.getReceiptDate() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getReceiptDate());
        }
        if (value.getReceiptIssuer() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getReceiptIssuer());
        }
        if (value.getReceiptTotal() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getReceiptTotal());
        }
        if (value.getReceiptDescription() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getReceiptDescription());
        }
        if (value.getReceiptFullText() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getReceiptFullText());
        }
      }
    };
    this.__deletionAdapterOfReceipt = new EntityDeletionOrUpdateAdapter<Receipt>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `Receipt` WHERE `ID` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Receipt value) {
        stmt.bindLong(1, value.getID());
      }
    };
  }

  @Override
  public void insertAll(Receipt... receipts) {
    __db.beginTransaction();
    try {
      __insertionAdapterOfReceipt.insert(receipts);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(Receipt receipt) {
    __db.beginTransaction();
    try {
      __deletionAdapterOfReceipt.handle(receipt);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Receipt> getAllReceipts() {
    final String _sql = "SELECT * FROM Receipt";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfID = _cursor.getColumnIndexOrThrow("ID");
      final int _cursorIndexOfReceiptNo = _cursor.getColumnIndexOrThrow("ReceiptNo");
      final int _cursorIndexOfReceiptDate = _cursor.getColumnIndexOrThrow("ReceiptDate");
      final int _cursorIndexOfReceiptIssuer = _cursor.getColumnIndexOrThrow("ReceiptIssuer");
      final int _cursorIndexOfReceiptTotal = _cursor.getColumnIndexOrThrow("ReceiptTotal");
      final int _cursorIndexOfReceiptDescription = _cursor.getColumnIndexOrThrow("ReceiptDescription");
      final int _cursorIndexOfReceiptFullText = _cursor.getColumnIndexOrThrow("ReceiptFullText");
      final List<Receipt> _result = new ArrayList<Receipt>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Receipt _item;
        final int _tmpID;
        _tmpID = _cursor.getInt(_cursorIndexOfID);
        final String _tmpReceiptNo;
        _tmpReceiptNo = _cursor.getString(_cursorIndexOfReceiptNo);
        final String _tmpReceiptDate;
        _tmpReceiptDate = _cursor.getString(_cursorIndexOfReceiptDate);
        final String _tmpReceiptIssuer;
        _tmpReceiptIssuer = _cursor.getString(_cursorIndexOfReceiptIssuer);
        final String _tmpReceiptTotal;
        _tmpReceiptTotal = _cursor.getString(_cursorIndexOfReceiptTotal);
        final String _tmpReceiptDescription;
        _tmpReceiptDescription = _cursor.getString(_cursorIndexOfReceiptDescription);
        final String _tmpReceiptFullText;
        _tmpReceiptFullText = _cursor.getString(_cursorIndexOfReceiptFullText);
        _item = new Receipt(_tmpID,_tmpReceiptNo,_tmpReceiptDate,_tmpReceiptIssuer,_tmpReceiptTotal,_tmpReceiptDescription,_tmpReceiptFullText);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Receipt getReceipt(int... pos) {
    StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM Receipt where ID=");
    final int _inputSize = pos.length;
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int _item : pos) {
      _statement.bindLong(_argIndex, _item);
      _argIndex ++;
    }
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _cursorIndexOfID = _cursor.getColumnIndexOrThrow("ID");
      final int _cursorIndexOfReceiptNo = _cursor.getColumnIndexOrThrow("ReceiptNo");
      final int _cursorIndexOfReceiptDate = _cursor.getColumnIndexOrThrow("ReceiptDate");
      final int _cursorIndexOfReceiptIssuer = _cursor.getColumnIndexOrThrow("ReceiptIssuer");
      final int _cursorIndexOfReceiptTotal = _cursor.getColumnIndexOrThrow("ReceiptTotal");
      final int _cursorIndexOfReceiptDescription = _cursor.getColumnIndexOrThrow("ReceiptDescription");
      final int _cursorIndexOfReceiptFullText = _cursor.getColumnIndexOrThrow("ReceiptFullText");
      final Receipt _result;
      if(_cursor.moveToFirst()) {
        final int _tmpID;
        _tmpID = _cursor.getInt(_cursorIndexOfID);
        final String _tmpReceiptNo;
        _tmpReceiptNo = _cursor.getString(_cursorIndexOfReceiptNo);
        final String _tmpReceiptDate;
        _tmpReceiptDate = _cursor.getString(_cursorIndexOfReceiptDate);
        final String _tmpReceiptIssuer;
        _tmpReceiptIssuer = _cursor.getString(_cursorIndexOfReceiptIssuer);
        final String _tmpReceiptTotal;
        _tmpReceiptTotal = _cursor.getString(_cursorIndexOfReceiptTotal);
        final String _tmpReceiptDescription;
        _tmpReceiptDescription = _cursor.getString(_cursorIndexOfReceiptDescription);
        final String _tmpReceiptFullText;
        _tmpReceiptFullText = _cursor.getString(_cursorIndexOfReceiptFullText);
        _result = new Receipt(_tmpID,_tmpReceiptNo,_tmpReceiptDate,_tmpReceiptIssuer,_tmpReceiptTotal,_tmpReceiptDescription,_tmpReceiptFullText);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public int getCount() {
    final String _sql = "SELECT COUNT(ID) FROM Receipt";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final Cursor _cursor = __db.query(_statement);
    try {
      final int _result;
      if(_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
