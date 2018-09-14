package ade.leke.com.trackguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SecureUser on 9/18/2015.
 */
public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "m_store_zims.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_Contact_ID + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LASTNAME + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber + TEXT_TYPE +COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS + TEXT_TYPE+

                    " )";

    private static final String SQL_CREATE_ENTRIES_MOVEMENT =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME_Movement + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UID + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LAT + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LNG + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE + TEXT_TYPE +COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS + TEXT_TYPE +
                    " )";
    private static final String SQL_CREATE_ENTRIES_PANIC =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME_PANIC + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PUID + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PLAT + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PLNG + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PADDRESS + TEXT_TYPE +COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE + TEXT_TYPE +COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PSTATUS + TEXT_TYPE +
                    " )";

    private static final String SQL_CREATE_USER =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME_USER + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UFIRSTNAME + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ULASTNAME + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UMOBILE_NUMBER + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UEMAIL + TEXT_TYPE +COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS + TEXT_TYPE +COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_VERSION + TEXT_TYPE +COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PASSCODE + TEXT_TYPE +COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SIM + TEXT_TYPE +
                    " )";
    private static final String SQL_CREATE_DATE =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME_PAY + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATE_DATE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_IS_UPDATE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_OWING + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PAY_STATUS + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PAY_DATE + TEXT_TYPE +
                    " )";


    public static final String SQL_CREATE_NOTIFICATION =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME_NOTIFICATION_LOCAL + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LUID + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_CONTACT_NUMBER + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LREQUEST + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_RESPONSE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LDATE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LSTATUS + TEXT_TYPE +
                    " )";

    public static final String SQL_CREATE_SETTING =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME_SETTINGS + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SUID + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PINTERVAL + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MINTERVAL + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MGS + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SMS + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_GDARK + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NUMBER_OF_SMS + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SSTATUS + TEXT_TYPE +
                    " )";
    public static final String SQL_CREATE_NEWS =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME_NEWS + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NUID + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NCLIENT + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NSUBJECT + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NEWS + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NIMAGE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NDATE + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NBulletin + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NAuthor + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NSTATUS + TEXT_TYPE +
                    " )";

    public static final String SQL_CREATE_THEME =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME_THEME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TNAME + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TBG + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PC + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PCD + TEXT_TYPE + COMMA_SEP +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS + TEXT_TYPE +
                    " )";


    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES_USER =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME_USER;

    private static final String SQL_DELETE_ENTRIES_PAY =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME_PAY;


    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_DATE);
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_USER);
        db.execSQL(SQL_CREATE_NOTIFICATION);
        db.execSQL(SQL_CREATE_ENTRIES_MOVEMENT);
        db.execSQL(SQL_CREATE_ENTRIES_PANIC);
        db.execSQL(SQL_CREATE_SETTING);
        db.execSQL(SQL_CREATE_THEME);
        db.execSQL(SQL_CREATE_NEWS);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_DELETE_ENTRIES_USER);
        db.execSQL(SQL_DELETE_ENTRIES_PAY);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
