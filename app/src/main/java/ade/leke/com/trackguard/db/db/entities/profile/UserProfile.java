package ade.leke.com.trackguard.db.db.entities.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.User;

/**
 * Created by SecureUser on 10/29/2015.
 */
public class UserProfile {

    FeedReaderDbHelper mDbHelper;
    public UserProfile(Context context){
        mDbHelper = new FeedReaderDbHelper(context);
    }

    public boolean create(User user){
        boolean state = false;

        User localUser = user;
        try {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UFIRSTNAME, localUser.getFirstname());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ULASTNAME, localUser.getLastname());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UMOBILE_NUMBER, localUser.getMobileNumber());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UEMAIL, localUser.getEmail());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_USTATUS, localUser.getStatus());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SIM, localUser.getSim());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_VERSION, localUser.getVersion());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSCODE, localUser.getPassCode());

            long newRowId;
            newRowId = db.insert(
                    FeedReaderContract.FeedEntry.TABLE_NAME_USER,
                    null,
                    values);
            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  state;
    }

    public User get(){
        User user = null;
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();


            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UFIRSTNAME,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ULASTNAME,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UMOBILE_NUMBER,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UEMAIL,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_USTATUS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SIM,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_VERSION,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PASSCODE


            };

            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_USER,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            while (c.moveToNext()) {
                user = new User();
                long itemId = c.getLong(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
                String firstname = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_UFIRSTNAME));
                String lastname = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_ULASTNAME));
                String mobileNumber = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_UMOBILE_NUMBER));
                String email = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_UEMAIL));
                String status = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_USTATUS));
                String sim = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SIM));
                String version  = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_VERSION));
                String passcode  = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSCODE));

                user.setEmail(email);
                user.setFirstname(firstname);
                user.setId(itemId);
                user.setLastname(lastname);
                user.setMobileNumber(mobileNumber);
                user.setStatus(status);
                user.setSIM(sim);
                user.setVersion(version);
                user.setPassCode(passcode);


            }
            c.close();
            db.close();
        }catch (Exception e){

            e.printStackTrace();
        }

        return  user;
    }

    public boolean update(User user){
        boolean state = false;

        try {


            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry._ID,  user.getId());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UFIRSTNAME,user.getFirstname());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ULASTNAME, user.getLastname());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UMOBILE_NUMBER,user.getMobileNumber());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UEMAIL, user.getEmail());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_USTATUS, user.getStatus());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SIM, user.getSim());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_VERSION, user.getVersion());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PASSCODE, user.getPassCode());

            long newRowId;
            newRowId = db.update(
                    FeedReaderContract.FeedEntry.TABLE_NAME_USER,
                    values, null,null);


            state= true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return state;
    }

    public boolean getRegistration(){
        boolean state;

        SQLiteDatabase db = mDbHelper.getReadableDatabase();


        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_UFIRSTNAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_ULASTNAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_UMOBILE_NUMBER,
                FeedReaderContract.FeedEntry.COLUMN_NAME_UEMAIL,
                FeedReaderContract.FeedEntry.COLUMN_NAME_USTATUS,
                FeedReaderContract.FeedEntry.COLUMN_NAME_SIM,
                FeedReaderContract.FeedEntry.COLUMN_NAME_VERSION,
                FeedReaderContract.FeedEntry.COLUMN_NAME_PASSCODE

        };




        try {
            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_USER,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    null                                 // The sort order
            );

            if (c != null) {
                if(c.getCount()!=0) {
                    state = true;
                }else{
                    state = false;
                }
            } else {
                state = false;
            }
            c.close();
            db.close();
        }catch (Exception e){

            state = false;
        }

        return state;
    }

}
