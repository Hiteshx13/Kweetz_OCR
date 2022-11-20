package com.kweetz.database.dao;

import android.database.Cursor;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.kweetz.database.model.Receipt;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ReceiptDao_Impl implements ReceiptDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Receipt> __insertionAdapterOfReceipt;

  private final EntityDeletionOrUpdateAdapter<Receipt> __deletionAdapterOfReceipt;

  public ReceiptDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReceipt = new EntityInsertionAdapter<Receipt>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `Receipt` (`ID`,`ReceiptNo`,`ReceiptDate`,`ReceiptIssuer`,`ReceiptTotal`,`ReceiptDescription`,`ReceiptFullText`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
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
  public void insertAll(final Receipt... receipts) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfReceipt.insert(receipts);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Receipt receipt) {
    __db.assertNotSuspendingTransaction();
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
    final String _sql = "SELECT `Receipt`.`ID` AS `ID`, `Receipt`.`ReceiptNo` AS `ReceiptNo`, `Receipt`.`ReceiptDate` AS `ReceiptDate`, `Receipt`.`ReceiptIssuer` AS `ReceiptIssuer`, `Receipt`.`ReceiptTotal` AS `ReceiptTotal`, `Receipt`.`ReceiptDescription` AS `ReceiptDescription`, `Receipt`.`ReceiptFullText` AS `ReceiptFullText` FROM Receipt";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfID = CursorUtil.getColumnIndexOrThrow(_cursor, "ID");
      final int _cursorIndexOfReceiptNo = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptNo");
      final int _cursorIndexOfReceiptDate = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptDate");
      final int _cursorIndexOfReceiptIssuer = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptIssuer");
      final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptTotal");
      final int _cursorIndexOfReceiptDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptDescription");
      final int _cursorIndexOfReceiptFullText = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptFullText");
      final List<Receipt> _result = new ArrayList<Receipt>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Receipt _item;
        _item = new Receipt();
        final int _tmpID;
        _tmpID = _cursor.getInt(_cursorIndexOfID);
        _item.setID(_tmpID);
        final String _tmpReceiptNo;
        if (_cursor.isNull(_cursorIndexOfReceiptNo)) {
          _tmpReceiptNo = null;
        } else {
          _tmpReceiptNo = _cursor.getString(_cursorIndexOfReceiptNo);
        }
        _item.setReceiptNo(_tmpReceiptNo);
        final String _tmpReceiptDate;
        if (_cursor.isNull(_cursorIndexOfReceiptDate)) {
          _tmpReceiptDate = null;
        } else {
          _tmpReceiptDate = _cursor.getString(_cursorIndexOfReceiptDate);
        }
        _item.setReceiptDate(_tmpReceiptDate);
        final String _tmpReceiptIssuer;
        if (_cursor.isNull(_cursorIndexOfReceiptIssuer)) {
          _tmpReceiptIssuer = null;
        } else {
          _tmpReceiptIssuer = _cursor.getString(_cursorIndexOfReceiptIssuer);
        }
        _item.setReceiptIssuer(_tmpReceiptIssuer);
        final String _tmpReceiptTotal;
        if (_cursor.isNull(_cursorIndexOfReceiptTotal)) {
          _tmpReceiptTotal = null;
        } else {
          _tmpReceiptTotal = _cursor.getString(_cursorIndexOfReceiptTotal);
        }
        _item.setReceiptTotal(_tmpReceiptTotal);
        final String _tmpReceiptDescription;
        if (_cursor.isNull(_cursorIndexOfReceiptDescription)) {
          _tmpReceiptDescription = null;
        } else {
          _tmpReceiptDescription = _cursor.getString(_cursorIndexOfReceiptDescription);
        }
        _item.setReceiptDescription(_tmpReceiptDescription);
        final String _tmpReceiptFullText;
        if (_cursor.isNull(_cursorIndexOfReceiptFullText)) {
          _tmpReceiptFullText = null;
        } else {
          _tmpReceiptFullText = _cursor.getString(_cursorIndexOfReceiptFullText);
        }
        _item.setReceiptFullText(_tmpReceiptFullText);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Receipt getReceipt(final int... pos) {
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
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfID = CursorUtil.getColumnIndexOrThrow(_cursor, "ID");
      final int _cursorIndexOfReceiptNo = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptNo");
      final int _cursorIndexOfReceiptDate = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptDate");
      final int _cursorIndexOfReceiptIssuer = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptIssuer");
      final int _cursorIndexOfReceiptTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptTotal");
      final int _cursorIndexOfReceiptDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptDescription");
      final int _cursorIndexOfReceiptFullText = CursorUtil.getColumnIndexOrThrow(_cursor, "ReceiptFullText");
      final Receipt _result;
      if(_cursor.moveToFirst()) {
        _result = new Receipt();
        final int _tmpID;
        _tmpID = _cursor.getInt(_cursorIndexOfID);
        _result.setID(_tmpID);
        final String _tmpReceiptNo;
        if (_cursor.isNull(_cursorIndexOfReceiptNo)) {
          _tmpReceiptNo = null;
        } else {
          _tmpReceiptNo = _cursor.getString(_cursorIndexOfReceiptNo);
        }
        _result.setReceiptNo(_tmpReceiptNo);
        final String _tmpReceiptDate;
        if (_cursor.isNull(_cursorIndexOfReceiptDate)) {
          _tmpReceiptDate = null;
        } else {
          _tmpReceiptDate = _cursor.getString(_cursorIndexOfReceiptDate);
        }
        _result.setReceiptDate(_tmpReceiptDate);
        final String _tmpReceiptIssuer;
        if (_cursor.isNull(_cursorIndexOfReceiptIssuer)) {
          _tmpReceiptIssuer = null;
        } else {
          _tmpReceiptIssuer = _cursor.getString(_cursorIndexOfReceiptIssuer);
        }
        _result.setReceiptIssuer(_tmpReceiptIssuer);
        final String _tmpReceiptTotal;
        if (_cursor.isNull(_cursorIndexOfReceiptTotal)) {
          _tmpReceiptTotal = null;
        } else {
          _tmpReceiptTotal = _cursor.getString(_cursorIndexOfReceiptTotal);
        }
        _result.setReceiptTotal(_tmpReceiptTotal);
        final String _tmpReceiptDescription;
        if (_cursor.isNull(_cursorIndexOfReceiptDescription)) {
          _tmpReceiptDescription = null;
        } else {
          _tmpReceiptDescription = _cursor.getString(_cursorIndexOfReceiptDescription);
        }
        _result.setReceiptDescription(_tmpReceiptDescription);
        final String _tmpReceiptFullText;
        if (_cursor.isNull(_cursorIndexOfReceiptFullText)) {
          _tmpReceiptFullText = null;
        } else {
          _tmpReceiptFullText = _cursor.getString(_cursorIndexOfReceiptFullText);
        }
        _result.setReceiptFullText(_tmpReceiptFullText);
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
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
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

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
