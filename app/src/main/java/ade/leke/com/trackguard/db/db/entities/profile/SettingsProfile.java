package ade.leke.com.trackguard.db.db.entities.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.Settings;

/**
 * Created by SecureUser on 11/1/2015.
 */
public class SettingsProfile {

    FeedReaderDbHelper mDbHelper;
    public SettingsProfile(Context context){
        mDbHelper = new FeedReaderDbHelper(context);
    }
    public boolean create(Settings settings){
        boolean state = false;

        try {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUID, settings.getUid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PINTERVAL, settings.getpInterval());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MINTERVAL, settings.getmInterval());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MGS,settings.getMGS());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SMS,settings.getSMS());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_GDARK,settings.getGoDark());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NUMBER_OF_SMS,settings.getNumberOfSMS());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SSTATUS,settings.getSettingsStatus());


            long newRowId;
            newRowId = db.insert(
                    FeedReaderContract.FeedEntry.TABLE_NAME_SETTINGS,
                    null,
                    values);
            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public boolean update(Settings settings){
        boolean state = false;

        try {


            SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry._ID, settings.getSid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SUID, settings.getUid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PINTERVAL, settings.getpInterval());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MINTERVAL,settings.getmInterval());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MGS,settings.getMGS());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SMS,settings.getSMS());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_GDARK,settings.getGoDark());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NUMBER_OF_SMS,settings.getNumberOfSMS());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SSTATUS,settings.getSettingsStatus());

// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.update(
                    FeedReaderContract.FeedEntry.TABLE_NAME_SETTINGS,
                    values, FeedReaderContract.FeedEntry.COLUMN_NAME_SUID +" = '"+ settings.getUid()+"'" ,null);

            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public boolean delete(String uid) {
        boolean state = false;
        try {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();


// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.delete(
                    FeedReaderContract.FeedEntry.TABLE_NAME_PANIC,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PUID + "='" + uid + "'",
                    null);
            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public Settings get(){

        Settings s = null;
        try{
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SUID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PINTERVAL,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MINTERVAL,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MGS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SMS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_GDARK,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NUMBER_OF_SMS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SSTATUS

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry._ID + " DESC";


            final Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_SETTINGS,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            if(c!=null){
                c.moveToNext();
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SUID));
                String pInterval = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PINTERVAL));
                String mInterval = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MINTERVAL));
                String mgs = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MGS));
                String sms = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SMS));
                String goDark = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_GDARK));
                String numberOfSMS = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NUMBER_OF_SMS));
                String statuss = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SSTATUS));

                s = new Settings();
                s.setUid(uid);
                s.setGoDark(goDark);
                s.setMGS(mgs);
                s.setmInterval(mInterval);
                s.setSettingsStatus(statuss);
                s.setNumberOfSMS(numberOfSMS);
                s.setpInterval(pInterval);
                s.setSid(itemId);
                s.setSMS(sms);


            }

            c.close();
            db.close();

        }catch (Exception e){
e.printStackTrace();
        }
        return s;
    }
}
