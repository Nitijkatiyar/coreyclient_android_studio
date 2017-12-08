package com.coremobile.coreyhealth;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteQueryBuilder;
import net.sqlcipher.database.SQLiteStatement;

import java.util.ArrayList;

/*
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteStatement;
import android.database.sqlite.SQLiteDatabase;
*/

public class DatabaseProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri
            .parse("content://com.coremobile.coreyhealth.databaseprovider/messages");
    public static final Uri CONTENT_URI_phonecalls = Uri
            .parse("content://com.coremobile.coreyhealth.databaseprovider/phonecalls");


    // The underlying database 
    public static SQLiteDatabase coreyhealthDB;
    // private static final String TAG = "Corey_DatabaseProvider";
    private static final String DATABASE_NAME = "corey";
    // database version history
    private static final int DATABASE_VERSION = AppConfig.DatabaseVersion;

    // following are field names for Messages Table
    private static final String MESSAGE_TABLE = "messages";


    public static final String KEY_ID = "_id";
    public static String KEY_MSGID = "MSGID";
    public static String KEY_APPLICATION_ID = "APPLICATION_ID";
    public static String KEY_APPLICATION = "APPLICATION";
    public static String KEY_APPLICATION_TITLE = "APPTITLE";
    public static String KEY_APP_GROUP_ID = "APP_GROUP_ID";
    public static String KEY_APP_SERVER_ID = "APP_SERVER_ID"; // server side id
    // for the entry
    // (linked in
    // profile id)
    public static String KEY_OBJECT_ID = "OBJECT_ID";
    public static String KEY_OBJECT_NAME = "OBJECTNAME";
    public static String KEY_OBJECT_DISPLAYTEXT = "OBJECTDISPLAYTEXT";
    public static String KEY_FIELD_NAME = "FIELDNAME";
    public static String KEY_FIELD_DISPLAYTEXT = "FIELDDISPLAYTEXT";
    public static String KEY_FIELD_VALUE = "FIELDVALUE";
    public static String KEY_READ_FIELD = "READ";
    public static String KEY_MESSAGE_TYPE = "TYPE";
    public static String KEY_APP_COMMONLOGO = "COMMON_LOGO";
    public static String KEY_APP_STATUS = "STATUS";
    public static String KEY_ISEDITABLE = "ISEDITABLE";
    public static String KEY_FIELD_ID = "FIELD_ID";
    public static String KEY_HASSTATUS = "HAS_STATUS";
    public static String KEY_ADDFILED = "ADD_FIELD";
    public static String KEY_ISNEW = "IS_NEW";
    public static String KEY_OPENURL = "OPEN_URL";
    public static String KEY_CONVERTTIME = "CONVERT_TIME";
    public static String KEY_TIME = "NOTE_UPDATE_TIME";
    public static String KEY_DISPLAY_ROW = "DISPLAY_ROW";
    public static String KEY_DISPLAY_COLOUMN = "DISPLAY_COL";
    public static String KEY_MSG_CONTEXT = "CONTEXT";
    public static String KEY_ISPARENT_CONTEXT = "ISPARENT";
    public static String KEY_ISLOADING_CONTEXT = "ISLOADING";
    public static String KEY_PARENT_ID_CONTEXT = "PARENT_ID";
    public static String KEY_DISP_QUADRANT_CONTEXT = "DISP_Quadrant";
    public static String KEY_DISPLAY_ICON = "Display_ICON";
    public static String KEY_FUTURE_USE_4_CONTEXT = "FUTURE_USE_4";
    public static String KEY_LISTVALUE = "LISTVALUE";
    public static String KEY_EDITABLE_LISTVALUE = "EDITABLE_LISTVALUE";
    public static String KEY_ISADDFIELD = "ISADDFIELD";
    public static String KEY_ADDTYPE = "ADDTYPE";
    public static String KEY_OBJECT_DISPLAY_SUBSCRIPT = "OBJECT_DISPLAY_SUBSCRIPT";
    public static String KEY_OBJECT_GraphType = "OBJECT_GRAPH_TYPE";
    public static String KEY_OBJECT_GraphId = "OBJECT_GRAPH_ID";
    public static String KEY_OBJECT_NeedTimeValue = "OBJECT_NEED_TIME_VALUE";
    public static String KEY_FIELD_MULTISELECT = "FIELDMULTISELECT";

    // column index
    public static final int MSGID_COLUMN = 1;
    public static final int APPLICATION_ID_COLUMN = 2;
    public static final int APPLICATION_COLUMN = 3;
    public static final int APP_GROUP_ID_COLUMN = 4;
    public static final int APP_SERVER_ID_COLUMN = 5;
    public static final int OBJECT_ID_COLUMN = 6;
    public static final int OBJECT_NAME_COLUMN = 7;
    public static final int APPLICATION_TITLE_COLUMN = 8;
    public static final int OBJECT_DISPLAYTEXT_COLUMN = 9;
    public static final int FIELD_NAME_COLUMN = 10;
    public static final int FIELD_DISPLAY_TEXT_COLUMN = 11;
    public static final int FIELD_VALUE_COLUMN = 12;
    public static final int READ_COLUMN = 13;
    public static final int MESSAGE_TYPE_COLUMN = 14;
    public static final int MESSAGE_TYPE_LOGO = 15;
    public static final int MESSAGE_TYPE_STATUS = 16;
    public static final int MESSAGE_ISEDITABLE = 17;
    public static final int FIELD_ID_COLOUMN = 18;
    public static final int KEY_HASSTATUS_COLOUMN = 19;
    public static final int KEY_ADDFILED_COLOUMN = 20;
    public static final int KEY_ISNEW_COLOUMN = 21;
    public static final int KEY_OPENURL_COLOUMN = 22;
    public static final int KEY_CONVERTTIME_COLOUMN = 23;
    public static final int KEY_TIME_COLOUMN = 24;
    public static final int KEY_DISPLAY_ROW_COLOUMN = 25;
    public static final int KEY_DISPLAY_COL_COLOUMN = 26;
    public static final int KEY_CONTEXT_COLOUMN = 27;
    public static final int KEY_ISPARENT_COLOUMN = 28;
    public static final int KEY_PARENT_ID_COLOUMN = 29;
    public static final int KEY_DISP_QUADRANT_COLOUMN = 30;
    public static final int KEY_DISPLAY_ICON_COLOUMN = 31;
    public static final int KEY_FUTURE_USE_4_COLOUMN = 32;
    public static final int KEY_LISTVALUE_COLOUMN = 33;
    public static final int KEY_EDITABLE_LISTVALUE_COLOUMN = 34;
    public static final int KEY_ISADDFIELD_COLOUMN = 35;
    public static final int KEY_ADDTYPE_COLOUMN = 36;
    public static final int KEY_OBJECT_DISPLAY_SUBSCRIPT_COLUMN = 37;
    public static final int OBJECT_GRAPHTYPE_COLUMN = 38;
    public static final int OBJECT_GRAPHID_COLUMN = 39;
    public static final int OBJECT_NEEDTIMEVALUE_COLUMN = 40;
    public static final int FIELD_MULTISELECT_VALUE = 41;
    public static final int KEY_ISLOADING_COLOUMN = 42;
    // message type constants
    public static final String MSG_TYPE_MESSAGE = "message";

    public static final String MSG_TYPE_INSTANT_GRATIFICATION = "instant_gratification";
    public static final String MSG_TYPE_PHONE_CALL = "retrievecallerinfo";
    public static final String MSG_TYPE_MARKET_TRACKER = "market_tracker";
    public static final String MSG_TYPE_MARKET_COREFY = "corefy";
    public static final String MSG_TYPE_LOCATION = "location";
    public static final String MSG_TYPE_TODO = "todo";

    // Create the constants used to differentiate between the different URI
    // requests.
    private static final int MESSAGES = 1;
    private static final int MESSAGE_ID = 2;
    // for phone call
    private static final int PHONE_CALLS = 3;
    private static final int PHONE_CALLS_ID = 4;


    // Column Names
    public static String KEY_CALL_ID = "id";
    public static String KEY_DATE = "date";
    public static String KEY_PHONENUMBER = "phonenumber";
    public static String KEY_FIRSTNAME = "firstname";
    public static String KEY_LASTNAME = "lastname";
    public static String KEY_CALLTIME = "calltime";
    public static String KEY_COMPANY = "company";

    private static final UriMatcher uriMatcher;

    // For phone support
    private static final String DB_TABLE_phonecalls = "phonecalls";
    public static final String[] COLS_phonecalls = new String[]{"id",
            "phonenumber", "firstname", "lastname", "calltime", "company"
    };

    // Allocate the UriMatcher object, where a URI ending in earthquakes
    // will correspond to a request for all earthquakes, and earthquakes
    // with a trailing /[rowID] will represent a single earthquake row.
    static {
        // MyApplication application = (MyApplication) getApplication();
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.coremobile.coreyhealth.databaseprovider", "messages",
                MESSAGES);
        //What is the below one - need to investigate
        uriMatcher.addURI("com.coremobile.provider.coreyhealth", "messages/#",
                MESSAGE_ID);
        // for phone call
        uriMatcher.addURI("com.coremobile.coreyhealth.databaseprovider",
                "phonecalls", PHONE_CALLS);
        uriMatcher.addURI("com.coremobile.coreyhealth.databaseprovider",
                "phonecalls/#", PHONE_CALLS_ID);
    }


    private static class DatabaseHelper extends SQLiteOpenHelper {

        // this is for device id table. it will only be once.
        // ie when the application boots up and he is prompted to login,
        // before he logins itself we will add his device id tho this table.
        // after that it will not be used again.
        // private static String DEVICEID_TABLE_NAME = "DEVICEID";

        /*
         * private static String CREATE_USER_TABLE = "create table " +
         * USER_INFO_TABLE_NAME + "(" + USERNAME_FIELD + " text not null, " +
         * PASSWORD_FIELD + " text not null," + DEVICEID_FIELD + " text null, "
         * + KEY_MSGID + " int null );";
         */
        private static String CREATE_MSGS_TABLE = "create table "
                + MESSAGE_TABLE + "(" + KEY_ID
                + " integer primary key autoincrement, " + KEY_MSGID
                + " integer, " + KEY_APPLICATION_ID + " integer, "
                + KEY_APPLICATION + " text, " + KEY_APP_GROUP_ID + " integer, "
                + KEY_APP_SERVER_ID + " text, " + KEY_OBJECT_ID + " integer, "
                + KEY_OBJECT_NAME + " text not null, " + KEY_APPLICATION_TITLE
                + " text , " + KEY_OBJECT_DISPLAYTEXT + " text, "
                + KEY_FIELD_NAME + " text not null, " + KEY_FIELD_DISPLAYTEXT
                + " text, "
                + KEY_FIELD_VALUE + " text not null,"
                + KEY_READ_FIELD + " integer not null, " + KEY_MESSAGE_TYPE
                + " text , " + KEY_APP_COMMONLOGO + " text , " + KEY_APP_STATUS
                + " text , " + KEY_ISEDITABLE + "  text ," + KEY_FIELD_ID
                + " text , " + KEY_HASSTATUS + " text , " + KEY_ADDFILED
                + " text ," + KEY_ISNEW + " text ," + KEY_OPENURL + " text ,"
                + KEY_CONVERTTIME + " text ," + KEY_TIME + " text , "
                + KEY_DISPLAY_ROW + " text , " + KEY_DISPLAY_COLOUMN
                + " text , " + KEY_MSG_CONTEXT + " text ,"
                + KEY_ISPARENT_CONTEXT + " text , " + KEY_PARENT_ID_CONTEXT
                + " text, " + KEY_DISP_QUADRANT_CONTEXT + " text ,"
                + KEY_DISPLAY_ICON + " text , "
                + KEY_FUTURE_USE_4_CONTEXT + " text , "
                + KEY_LISTVALUE + " text , "
                + KEY_EDITABLE_LISTVALUE + " text , "
                + KEY_ISADDFIELD + " text , "
                + KEY_ADDTYPE + " text , "
                + KEY_OBJECT_DISPLAY_SUBSCRIPT + " text , "
                + KEY_OBJECT_GraphType + " text, "
                + KEY_OBJECT_GraphId + " text, " + KEY_OBJECT_NeedTimeValue + " text, "
                + KEY_FIELD_MULTISELECT + " text, " + KEY_ISLOADING_CONTEXT + " text);";


        private static final String DB_CREATE_phonecalls = "CREATE TABLE "
                + DB_TABLE_phonecalls
                + "(id INTEGER PRIMARY KEY autoincrement, phonenumber TEXT NOT NULL,firstname TEXT,lastname TEXT,calltime BIGINT NOT NULL,company TEXT);";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            //  	Context context = getContext();
            //     SQLiteDatabase.loadLibs(context);


            // db.execSQL(CREATE_USER_TABLE);
            db.execSQL(CREATE_MSGS_TABLE);
            Log.d("TableColumns", "" + CREATE_MSGS_TABLE);
            // For phone support
            db.execSQL(DB_CREATE_phonecalls);


        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            System.out.println("DatabaseProvider.onUpgrade " + oldVersion
                    + ", " + newVersion);
            if (newVersion > oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
                db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_phonecalls);
                onCreate(db);
            }
        }

    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        //   int i=0;
        coreyhealthDB.beginTransaction();
        try {
            // standard SQL insert statement, that can be reused

            SQLiteStatement insert = coreyhealthDB
                    .compileStatement("insert into "
                            + MESSAGE_TABLE
                            + "("
                            + KEY_MSGID
                            + ","
                            + KEY_APPLICATION_ID
                            + ","
                            + KEY_APPLICATION
                            + ","
                            + KEY_APP_GROUP_ID
                            + ","
                            + KEY_APP_SERVER_ID
                            + ","
                            + KEY_OBJECT_ID
                            + ","
                            + KEY_OBJECT_NAME
                            + ","
                            + KEY_APPLICATION_TITLE
                            + ","
                            + KEY_OBJECT_DISPLAYTEXT
                            + ","
                            + KEY_FIELD_NAME
                            + ","
                            + KEY_FIELD_DISPLAYTEXT
                            + ","
                            + KEY_FIELD_VALUE
                            + ","
                            + KEY_READ_FIELD
                            + ","
                            + KEY_MESSAGE_TYPE
                            + ","
                            + KEY_APP_COMMONLOGO
                            + ","
                            + KEY_APP_STATUS
                            + ","
                            + KEY_ISEDITABLE
                            + ","
                            + KEY_FIELD_ID
                            + ","
                            + KEY_HASSTATUS
                            + ","
                            + KEY_ADDFILED
                            + ","
                            + KEY_ISNEW
                            + ","
                            + KEY_OPENURL
                            + ","
                            + KEY_CONVERTTIME
                            + ","
                            + KEY_TIME
                            + ","
                            + KEY_DISPLAY_ROW
                            + ","
                            + KEY_DISPLAY_COLOUMN
                            + ","
                            + KEY_MSG_CONTEXT
                            + ","
                            + KEY_ISPARENT_CONTEXT
                            + ","
                            + KEY_PARENT_ID_CONTEXT
                            + ","
                            + KEY_DISP_QUADRANT_CONTEXT
                            + ","
                            + KEY_DISPLAY_ICON
                            + ","
                            + KEY_FUTURE_USE_4_CONTEXT
                            + ","
                            + KEY_LISTVALUE
                            + ","
                            + KEY_EDITABLE_LISTVALUE
                            + ","
                            + KEY_ISADDFIELD
                            + ","
                            + KEY_ADDTYPE
                            + ","
                            + KEY_OBJECT_DISPLAY_SUBSCRIPT
                            + ","
                            + KEY_OBJECT_GraphType
                            + ","
                            + KEY_OBJECT_GraphId
                            + ","
                            + KEY_OBJECT_NeedTimeValue
                            + ","
                            + KEY_FIELD_MULTISELECT
                            + ","
                            + KEY_ISLOADING_CONTEXT
                            + ")"
                            + " values "
                            + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            for (ContentValues value : values) {
                bind(insert, MSGID_COLUMN, value.getAsString(KEY_MSGID));
                bind(insert, APPLICATION_ID_COLUMN,
                        value.getAsString(KEY_APPLICATION_ID));
                bind(insert, APPLICATION_COLUMN,
                        value.getAsString(KEY_APPLICATION));
                bind(insert, APP_GROUP_ID_COLUMN,
                        value.getAsString(KEY_APP_GROUP_ID));
                bind(insert, APP_SERVER_ID_COLUMN,
                        value.getAsString(KEY_APP_SERVER_ID));
                bind(insert, OBJECT_ID_COLUMN, value.getAsString(KEY_OBJECT_ID));
                bind(insert, OBJECT_NAME_COLUMN,
                        value.getAsString(KEY_OBJECT_NAME));
                bind(insert, APPLICATION_TITLE_COLUMN,
                        value.getAsString(KEY_APPLICATION_TITLE));
                bind(insert, OBJECT_DISPLAYTEXT_COLUMN,
                        value.getAsString(KEY_OBJECT_DISPLAYTEXT));
                bind(insert, FIELD_NAME_COLUMN,
                        value.getAsString(KEY_FIELD_NAME));
                bind(insert, FIELD_DISPLAY_TEXT_COLUMN,
                        value.getAsString(KEY_FIELD_DISPLAYTEXT));
                bind(insert, FIELD_VALUE_COLUMN,
                        value.getAsString(KEY_FIELD_VALUE));
                bind(insert, READ_COLUMN, value.getAsString(KEY_READ_FIELD));
                bind(insert, MESSAGE_TYPE_COLUMN,
                        value.getAsString(KEY_MESSAGE_TYPE));
                bind(insert, MESSAGE_TYPE_LOGO,
                        value.getAsString(KEY_APP_COMMONLOGO));
                bind(insert, MESSAGE_TYPE_STATUS,
                        value.getAsString(KEY_APP_STATUS));
                bind(insert, MESSAGE_ISEDITABLE,
                        value.getAsString(KEY_ISEDITABLE));
                bind(insert, FIELD_ID_COLOUMN, value.getAsString(KEY_FIELD_ID));
                bind(insert, KEY_HASSTATUS_COLOUMN,
                        value.getAsString(KEY_HASSTATUS));
                bind(insert, KEY_ADDFILED_COLOUMN,
                        value.getAsString(KEY_ADDFILED));
                bind(insert, KEY_ISNEW_COLOUMN, value.getAsString(KEY_ISNEW));
                bind(insert, KEY_OPENURL_COLOUMN,
                        value.getAsString(KEY_OPENURL));
                bind(insert, KEY_CONVERTTIME_COLOUMN,
                        value.getAsString(KEY_CONVERTTIME));
                bind(insert, KEY_TIME_COLOUMN, value.getAsString(KEY_TIME));
                bind(insert, KEY_DISPLAY_ROW_COLOUMN,
                        value.getAsString(KEY_DISPLAY_ROW));
                bind(insert, KEY_DISPLAY_COL_COLOUMN,
                        value.getAsString(KEY_DISPLAY_COLOUMN));
                bind(insert, KEY_CONTEXT_COLOUMN,
                        value.getAsString(KEY_MSG_CONTEXT));
                bind(insert, KEY_ISPARENT_COLOUMN,
                        value.getAsString(KEY_ISPARENT_CONTEXT));
                bind(insert, KEY_ISLOADING_COLOUMN,
                        value.getAsString(KEY_ISLOADING_CONTEXT));
                bind(insert, KEY_PARENT_ID_COLOUMN,
                        value.getAsString(KEY_PARENT_ID_CONTEXT));
                bind(insert, KEY_DISP_QUADRANT_COLOUMN,
                        value.getAsString(KEY_DISP_QUADRANT_CONTEXT));
                bind(insert, KEY_DISPLAY_ICON_COLOUMN,
                        value.getAsString(KEY_DISPLAY_ICON));
                bind(insert, KEY_FUTURE_USE_4_COLOUMN,
                        value.getAsString(KEY_FUTURE_USE_4_CONTEXT));
                bind(insert, KEY_LISTVALUE_COLOUMN,
                        value.getAsString(KEY_LISTVALUE));
                bind(insert, KEY_EDITABLE_LISTVALUE_COLOUMN,
                        value.getAsString(KEY_EDITABLE_LISTVALUE));
                bind(insert, KEY_ISADDFIELD_COLOUMN,
                        value.getAsString(KEY_ISADDFIELD));
                bind(insert, KEY_ADDTYPE_COLOUMN,
                        value.getAsString(KEY_ADDTYPE));
                bind(insert, KEY_OBJECT_DISPLAY_SUBSCRIPT_COLUMN,
                        value.getAsString(KEY_OBJECT_DISPLAY_SUBSCRIPT));
                bind(insert, OBJECT_GRAPHTYPE_COLUMN, value.getAsString(KEY_OBJECT_GraphType));
                bind(insert, OBJECT_GRAPHID_COLUMN, value.getAsString(KEY_OBJECT_GraphId));
                bind(insert, OBJECT_NEEDTIMEVALUE_COLUMN, value.getAsString(KEY_OBJECT_NeedTimeValue));
                bind(insert, FIELD_MULTISELECT_VALUE,
                        value.getAsString(KEY_FIELD_MULTISELECT));


                insert.execute();
                //       i++;
                //   Log.d("Databaseprovider","Inserted statement ="+i);
            }
            coreyhealthDB.setTransactionSuccessful();
            numInserted = values.length;
        } catch (Exception e) {
            e.getMessage();
        } finally {
            coreyhealthDB.endTransaction();
        }
        return numInserted;
    }

    private void bind(SQLiteStatement stmt, int index, String val) {
        if (val != null) {
            stmt.bindString(index, val);
        } else {
            stmt.bindNull(index);
        }
    }

    @Override
    public ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        return super.applyBatch(operations);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MESSAGES:
                try {
                    count = coreyhealthDB.delete(MESSAGE_TABLE, selection, selectionArgs);
                } catch (SQLiteException e) {
                    e.getMessage();
                }
                //     coreyhealthDB.execSQL("VACUUM");
                break;
            case MESSAGE_ID:
                String segment = uri.getPathSegments().get(1);
                count = coreyhealthDB.delete(MESSAGE_TABLE, KEY_MSGID
                        + "="
                        + segment
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                        + ')' : ""), selectionArgs);
                break;
            case PHONE_CALLS:
                count = coreyhealthDB.delete(DB_TABLE_phonecalls, selection,
                        selectionArgs);
                //  coreyhealthDB.execSQL("VACUUM");
                break;
            case PHONE_CALLS_ID:
                String id = uri.getPathSegments().get(1);
                count = coreyhealthDB.delete(DB_TABLE_phonecalls, "id ="
                        + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                        + ')' : ""), selectionArgs);
                break;


            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public void vacuumDb() {
        coreyhealthDB.execSQL("VACUUM");
    }

    public void deletedb(Context ctxt) {/*
        coreyhealthDB.close();
    	 coreyhealthDB.remove();
    	 coreyhealthDB.
    	String path = "/data/data/com.coremobile.coreyhealth/files/";
        SQLiteDatabase.deleteDatabase(new File(path));
    	deleteDatabase(coreyhealthDB); */
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MESSAGES:
                return "vnd.android.cursor.dir/vnd.coremobile.corey";
            case MESSAGE_ID:
                return "vnd.android.cursor.item/vnd.coremobile.corey";
            case PHONE_CALLS:
                return "vnd.android.cursor.dir/vnd.coremobile.corey";
            case PHONE_CALLS_ID:
                return "vnd.android.cursor.item/vnd.coremobile.corey";
            default:
                throw new IllegalArgumentException("Unsupported URI dhhdjdjdkjdk: "
                        + uri);
        }
    }

    @Override
    public Uri insert(Uri _uri, ContentValues values) {
        // Log.i("TAG","insert uri: " + _uri);
        // Insert the new row, will return the row number if
        // successful.

        switch (uriMatcher.match(_uri)) {

            case PHONE_CALLS:
                // /////////////////
                long rowIDpc = coreyhealthDB
                        .insert(DB_TABLE_phonecalls, null, values);
                // Return a URI to the newly inserted row on success.
                if (rowIDpc > 0) {
                    Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowIDpc);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return uri;
                }
                break;


            default:
                long rowID = coreyhealthDB.insert(MESSAGE_TABLE, null, values);
                // Return a URI to the newly inserted row on success.
                if (rowID > 0) {
                    Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return uri;
                }
        }

        throw new SQLException("Failed to insert row into " + _uri);

    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        SQLiteDatabase.loadLibs(context);
        DatabaseHelper dbHelper;
        dbHelper = new DatabaseHelper(context);
        //   coreyhealthDB = dbHelper.getWritableDatabase();
        this.deletedb(getContext());
        SQLiteDatabase.loadLibs(getContext(), getContext().getFilesDir());
        try {
            coreyhealthDB = dbHelper.getWritableDatabase("corey123");
            return (coreyhealthDB == null) ? false : true;
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            Log.d("databaseprovider", " database creation exception");
        /*	Toast.makeText(this.getContext(),
                    "Database encryption error occured. Please uninstall your existing app and install again", Toast.LENGTH_LONG).show();
		*/
            MyApplication.INSTANCE.DB_EXEPTION = true;
            //	Utils.signout();
        }
        return (coreyhealthDB == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (uriMatcher.match(uri)) {
            case PHONE_CALLS:

                qb.setTables(DB_TABLE_phonecalls);
                // Apply the query to the underlying database.
                // Log.i(TAG,"selection: " + selection);
                Cursor c1 = qb.query(coreyhealthDB, projection, selection,
                        selectionArgs, null, null, sortOrder);
                // Log.i(TAG,"cursor count: " + c.getCount());
                // Register the contexts ContentResolver to be notified if
                // the cursor result set changes.
                c1.setNotificationUri(getContext().getContentResolver(), uri);
                // Return a cursor to the query result.
                return c1;


            default:
                qb.setTables(MESSAGE_TABLE);
                // Apply the query to the underlying database.
                // Log.i(TAG,"selection: " + selection);
                Cursor c2 = qb.query(coreyhealthDB, projection, selection,
                        selectionArgs, null, null, sortOrder);
                // Log.i(TAG,"cursor count: " + c.getCount());
                // Register the contexts ContentResolver to be notified if
                // the cursor result set changes.
                if (getContext() != null)
                    c2.setNotificationUri(getContext().getContentResolver(), uri);
                // Return a cursor to the query result.
//            if(c2 !=null )c2.close();
                return c2;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
            case MESSAGES:
                count = coreyhealthDB.update(MESSAGE_TABLE, values, selection,
                        selectionArgs);

                break;


            case MESSAGE_ID:
                String segment = uri.getPathSegments().get(1);

                count = coreyhealthDB.update(MESSAGE_TABLE, values, KEY_MSGID
                        + "="
                        + segment
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                        + ')' : ""), selectionArgs);
                break;
            case PHONE_CALLS:
                count = coreyhealthDB.update(DB_TABLE_phonecalls, values, selection,
                        selectionArgs);
                break;
            case PHONE_CALLS_ID:
                String id = uri.getPathSegments().get(1);
                count = coreyhealthDB.update(DB_TABLE_phonecalls, values, "id="
                        + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection
                        + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


}
