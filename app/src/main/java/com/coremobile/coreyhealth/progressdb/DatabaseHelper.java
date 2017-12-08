package com.coremobile.coreyhealth.progressdb;

import android.content.Context;

import com.coremobile.coreyhealth.AppConfig;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * @author iRESLab
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = "Database Action";
    public static final String DATABASE_NAME = "coreyhealth";
    public static int DATABASE_VERSION = AppConfig.DatabaseVersion;
    public static final String ENABLE_CASCADE = "PRAGMA foreign_keys = ON;";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_PROGRESSBAR_TABLE = "CREATE TABLE "
                + Tables.ProgressbarTable.TableName + "("
                + Tables.ProgressbarTable.id
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Tables.ProgressbarTable.color + " TEXT,"
                + Tables.ProgressbarTable.displaytext + " TEXT,"
                + Tables.ProgressbarTable.objectId + " TEXT,"
                + Tables.ProgressbarTable.status + " TEXT" + ")";
        db.execSQL(CREATE_PROGRESSBAR_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Tables.ProgressbarTable.TableName);
        DatabaseHelper.DATABASE_VERSION = newVersion;
        onCreate(db);
    }

    public static class Tables {
        public class ProgressbarTable {
            public static final String TableName = "ProgressbarTable";
            public static final String objectId = "ObjectID";
            public static final String displaytext = "StageDisplayText";
            public static final String status = "StageStatus";
            public static final String id = "id";
            public static final String color = "StageColor";

        }

    }
}
