package com.coremobile.coreyhealth.progressdb;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.sqlcipher.database.SQLiteDatabase;

import com.coremobile.coreyhealth.progressdb.DatabaseHelper.Tables;

import java.util.ArrayList;
import java.util.List;

/**
 * @author iRESLab
 */
public class ProgressInfoDBControl extends ContentProvider {
    private Context context;
    private DatabaseHelper dbhelper;
    private SQLiteDatabase db;

    public static final String TABLE_NAME = Tables.ProgressbarTable.TableName;
    public static final String[] allcols = new String[]{
            DatabaseHelper.Tables.ProgressbarTable.id, Tables.ProgressbarTable.objectId,
            Tables.ProgressbarTable.status, Tables.ProgressbarTable.displaytext,
            Tables.ProgressbarTable.color};

    public ProgressInfoDBControl(Context context) {
        this.context = context;

        dbhelper = new DatabaseHelper(context);

    }


    public ProgressInfoDBControl open() throws SQLException {

        try {
            db = dbhelper.getReadableDatabase("corey123");
        } catch (Exception e) {
            e.getMessage();
        }

        return this;
    }

    public void close() {
        if (db != null) {
            db.close();
        }

    }

    public ContentValues createContentValues(ProgressObjectModel datadoctor) {

        ContentValues values = new ContentValues();
        values.put(Tables.ProgressbarTable.displaytext,
                datadoctor.getStageName());
        values.put(Tables.ProgressbarTable.color,
                datadoctor.getColor());
        values.put(Tables.ProgressbarTable.objectId,
                datadoctor.getObjectId());
        values.put(Tables.ProgressbarTable.status,
                datadoctor.getStatus());

        return values;

    }

    private ProgressObjectModel cursortoProgressBarObjects(Cursor cursor) {
        ProgressObjectModel result = new ProgressObjectModel();
        result.setId(cursor.getInt(cursor
                .getColumnIndex(Tables.ProgressbarTable.id)));
        result.setObjectId(cursor.getString(cursor
                .getColumnIndex(Tables.ProgressbarTable.objectId)));
        result.setColor(cursor.getString(cursor
                .getColumnIndex(Tables.ProgressbarTable.color)));
        result.setStageName(cursor.getString(cursor
                .getColumnIndex(Tables.ProgressbarTable.displaytext)));
        result.setStatus(cursor.getString(cursor
                .getColumnIndex(Tables.ProgressbarTable.status)));

        return result;

    }

    public boolean insertProgressBarObject(ProgressObjectModel doctordata) {

        try {
            this.open();
            if (db.insert(TABLE_NAME, null, createContentValues(doctordata)) != -1) {
                //  Log.d("data", "inserted");
                Cursor cursour = db.rawQuery("select * from " + TABLE_NAME,
                        null);
                cursour.moveToFirst();

                cursour.close();
                this.close();
                //  Log.e("ProgressBarObject", "inserted");
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {

            e.getMessage();
        } finally {
            this.close();
        }
        return false;

    }

    public boolean deleteSingleProgressBarObjects(ProgressObjectModel bean) {

        try {
            this.open();
            if (db.delete(TABLE_NAME, "ObjectID =?", new String[]{bean.getObjectId()}) != -1) {
                this.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.getMessage();
        } finally {
            this.close();
        }

        return false;

    }

    public boolean deleteallProgressBarObjects() {

        try {
            this.open();
            if (db.delete(TABLE_NAME, null, null) != -1) {
                this.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.getMessage();
        } finally {
            this.close();
        }

        return false;

    }

    public List<ProgressObjectModel> getProgressBarObjects(String objectId) {
        List<ProgressObjectModel> listdata = new ArrayList<ProgressObjectModel>();
        String where = Tables.ProgressbarTable.objectId + "=?";
        String sortOrder =
                Tables.ProgressbarTable.objectId + " DESC";
        String[] projection = new String[]{
                DatabaseHelper.Tables.ProgressbarTable.id, Tables.ProgressbarTable.objectId,
                Tables.ProgressbarTable.status, Tables.ProgressbarTable.displaytext,
                Tables.ProgressbarTable.color};
        SQLiteDatabase db_progress = null;
        try {
            db_progress = dbhelper.getReadableDatabase("corey123");
//            Cursor cursor = db.query(TABLE_NAME, projection, where, new String[]{objectId}, null, null, sortOrder);
            Cursor cursor = db_progress.rawQuery("SELECT * FROM ProgressbarTable WHERE ObjectID = ?", new String[]{objectId});
            while (cursor.moveToNext()) {
                listdata.add(cursortoProgressBarObjects(cursor));
            }
            cursor.close();
        } catch (SQLException e) {
            e.getMessage();
        } finally {
            if (db_progress != null)
                db_progress.close();
        }

        return listdata;

    }

    public ProgressObjectModel getSingleProgressBarObject(int id) {
        ProgressObjectModel listdata = new ProgressObjectModel();
        try {
            this.open();
            Cursor cursor = db.query(TABLE_NAME, allcols,
                    Tables.ProgressbarTable.id + " = " + id, null, null, null,
                    null);
            cursor.moveToFirst();
            listdata = cursortoProgressBarObjects(cursor);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.getMessage();
        } finally {
            this.close();
        }
        return listdata;

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        net.sqlcipher.database.SQLiteDatabase.loadLibs(context);
        DatabaseHelper dbHelper;
        dbHelper = new DatabaseHelper(context);
        try {

            db = dbHelper.getWritableDatabase("corey123");
        } catch (Exception e) {
            e.getMessage();
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
