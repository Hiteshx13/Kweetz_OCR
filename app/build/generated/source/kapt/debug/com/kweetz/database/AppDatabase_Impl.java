package com.kweetz.database;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import com.kweetz.database.dao.ReceiptDao;
import com.kweetz.database.dao.ReceiptDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile ReceiptDao _receiptDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `Receipt` (`ID` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `ReceiptNo` TEXT NOT NULL, `ReceiptDate` TEXT NOT NULL, `ReceiptIssuer` TEXT NOT NULL, `ReceiptTotal` TEXT NOT NULL, `ReceiptDescription` TEXT NOT NULL, `ReceiptFullText` TEXT NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd7c483b874425a7d4cdcdb8b8f5d02a5')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `Receipt`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsReceipt = new HashMap<String, TableInfo.Column>(7);
        _columnsReceipt.put("ID", new TableInfo.Column("ID", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipt.put("ReceiptNo", new TableInfo.Column("ReceiptNo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipt.put("ReceiptDate", new TableInfo.Column("ReceiptDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipt.put("ReceiptIssuer", new TableInfo.Column("ReceiptIssuer", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipt.put("ReceiptTotal", new TableInfo.Column("ReceiptTotal", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipt.put("ReceiptDescription", new TableInfo.Column("ReceiptDescription", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReceipt.put("ReceiptFullText", new TableInfo.Column("ReceiptFullText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReceipt = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReceipt = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReceipt = new TableInfo("Receipt", _columnsReceipt, _foreignKeysReceipt, _indicesReceipt);
        final TableInfo _existingReceipt = TableInfo.read(_db, "Receipt");
        if (! _infoReceipt.equals(_existingReceipt)) {
          return new RoomOpenHelper.ValidationResult(false, "Receipt(com.kweetz.database.model.Receipt).\n"
                  + " Expected:\n" + _infoReceipt + "\n"
                  + " Found:\n" + _existingReceipt);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "d7c483b874425a7d4cdcdb8b8f5d02a5", "292cc04ae1184ae84fc953d3f0ca2c22");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "Receipt");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `Receipt`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ReceiptDao.class, ReceiptDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public ReceiptDao productsDao() {
    if (_receiptDao != null) {
      return _receiptDao;
    } else {
      synchronized(this) {
        if(_receiptDao == null) {
          _receiptDao = new ReceiptDao_Impl(this);
        }
        return _receiptDao;
      }
    }
  }
}
