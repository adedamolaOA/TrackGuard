package ade.leke.com.trackguard.db.db.entities.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.User;

/**
 * Created by SecureUser on 1/29/2016.
 */


/**
 * Created by SecureUser on 10/29/2015.
 */
public class AppThemeProfile {

    FeedReaderDbHelper mDbHelper;
    public AppThemeProfile(Context context){
        mDbHelper = new FeedReaderDbHelper(context);
    }

    public boolean create(AppThemes user){
        boolean state = false;

        AppThemes localUser = user;
        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TNAME, localUser.getName());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TBG, localUser.getBackground());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PC, localUser.getPrimaryColor());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PCD, localUser.getPrimaryColorDark());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TSTATUS, localUser.getStatus());

            long newRowId;
            newRowId = db.insert(
                    FeedReaderContract.FeedEntry.TABLE_NAME_THEME,
                    null,
                    values);
            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  state;
    }

    public AppThemes get(){
        AppThemes user = null;
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();


            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TNAME,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TBG,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PC,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PCD,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TSTATUS


            };

            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_THEME,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            while (c.moveToNext()) {
                user = new AppThemes();
                long itemId = c.getLong(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
                String name = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TNAME));
                String bg = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TBG));
                String pc = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PC));
                String pcd = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PCD));
                String status = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_TSTATUS));

                user.setName(name);
                user.setStatus(status);
                user.setBackground(bg);
                user.setPrimaryColor(pc);
                user.setPrimaryColorDark(pcd);


            }
            c.close();
            db.close();
        }catch (Exception e){

            e.printStackTrace();
        }

        return  user;
    }

    public boolean update(AppThemes user){
        boolean state = false;

        try {


            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry._ID,  user.getId());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TNAME,user.getName());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TBG, user.getBackground());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PC,user.getPrimaryColor());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PCD, user.getPrimaryColorDark());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TSTATUS, user.getStatus());

            long newRowId;
            newRowId = db.update(
                    FeedReaderContract.FeedEntry.TABLE_NAME_THEME,
                    values, null,null);


            state= true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return state;
    }


}
