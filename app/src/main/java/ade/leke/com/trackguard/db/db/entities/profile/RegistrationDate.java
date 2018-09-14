package ade.leke.com.trackguard.db.db.entities.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.Reg;

/**
 * Created by SecureUser on 10/29/2015.
 */
public class RegistrationDate {
    FeedReaderDbHelper mDbHelper;
    public RegistrationDate(Context context){
        mDbHelper = new FeedReaderDbHelper(context);
    }

    public boolean create(Reg reg){
        boolean state = false;


        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PAY_DATE, format.format(new Date()));
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATE_DATE, format.format(new Date()));
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_IS_UPDATE, reg.getIsUpdated());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_OWING, reg.getOwing());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PAY_STATUS, reg.getPayService());

            long newRowId;
            newRowId = db.insert(
                    FeedReaderContract.FeedEntry.TABLE_NAME_PAY,
                    null,
                    values);
            state = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return  state;
    }

    public Reg get(){
        Reg reg = null;
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();


            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PAY_DATE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATE_DATE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_IS_UPDATE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_OWING,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PAY_STATUS


            };

            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_PAY,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            while (c.moveToNext()) {
                reg = new Reg();
                long itemId = c.getLong(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PAY_DATE));
                String isUpdated = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_IS_UPDATE));
                String owing = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_OWING));
                String pay = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PAY_STATUS));
                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                reg.setRegId(itemId);
                reg.setRegDate(format.parse(date));
                reg.setUpdateDate(format.parse(date));
                reg.setIsUpdated(isUpdated);
                reg.setOwing(owing);
                reg.setPayService(pay);


            }
        }catch (Exception e){

            e.printStackTrace();
        }

        return  reg;
    }

    public boolean update(Reg reg){
        boolean state = false;

        try {


            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            //values.put(FeedReaderContract.FeedEntry._ID,  reg.getRegId());
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATE_DATE,format.format(reg.getUpdateDate()));
          ;


            long newRowId;
            newRowId = db.update(
                    FeedReaderContract.FeedEntry.TABLE_NAME_PAY,
                    values,FeedReaderContract.FeedEntry._ID+"="+reg.getRegId() ,null);


            state= true;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return state;
    }


}
