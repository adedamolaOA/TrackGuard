package ade.leke.com.trackguard.db.db.entities.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.Notification;
import ade.leke.com.trackguard.db.db.entities.Panic;

/**
 * Created by SecureUser on 11/1/2015.
 */
public class PanicProfile {
    FeedReaderDbHelper mDbHelper;
    public PanicProfile(Context context){
        mDbHelper = new FeedReaderDbHelper(context);
    }
    public boolean create(Panic panic){
        boolean state = false;

        try {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();

            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PUID, panic.getUid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PLAT, panic.getLat());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PLNG,panic.getLng());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PADDRESS,panic.getAddress());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE,panic.getDate());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PSTATUS,panic.getStatus());


            long newRowId;
            newRowId = db.insert(
                    FeedReaderContract.FeedEntry.TABLE_NAME_PANIC,
                    null,
                    values);
            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }


    public ArrayList<Panic> getPanic(String startDate,String endDate){
        ArrayList<Panic> contactList = new ArrayList<>();
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PUID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PLAT,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PLNG,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PADDRESS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PSTATUS

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE + " DESC";


            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_PANIC,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE + " BETWEEN ? AND ?",                                // The columns for the WHERE clause
                    new String[] {
                            startDate, endDate},  //+ " 23:59:59"      + " 00:00:00"                      // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            while (c.moveToNext()) {
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PUID));
                String lat = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PLAT));
                String lng = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PLNG));
                String address = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PADDRESS));
                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE));
                String status = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PSTATUS));

                Panic p = new Panic();
                p.setDate(date);
                p.setAddress(address);
                p.setUid(uid);
                p.setLng(lng);
                p.setLat(lat);
                p.setPid(itemId);
                p.setStatus(status);
                contactList.add(p);


            }
            c.close();
            db.close();
        }catch (Exception e){

            e.printStackTrace();
        }

        return contactList;
    }


    public boolean update(Panic panic){
        boolean state = false;

        try {


            SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry._ID, panic.getPid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PUID, panic.getUid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PLAT, panic.getLat());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PLNG,panic.getLng());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PADDRESS,panic.getAddress());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE,panic.getDate());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PSTATUS,panic.getStatus());

// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.update(
                    FeedReaderContract.FeedEntry.TABLE_NAME_PANIC,
                    values, FeedReaderContract.FeedEntry.COLUMN_NAME_PUID +" = '"+ panic.getUid()+"'" ,null);

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

    public Panic getByStaus(String status){

        Panic p = null;
        try{
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PUID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PLAT,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PLNG,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PADDRESS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PSTATUS

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE + " DESC";


            final Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_PANIC,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PSTATUS +" = '"+status+"'",                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            if(c!=null){
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PUID));
                String lat = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PLAT));
                String lng = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PLNG));
                String address = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PADDRESS));
                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE));
                String statuss = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PSTATUS));

                p = new Panic();
                p.setDate(date);
                p.setAddress(address);
                p.setUid(uid);
                p.setLng(lng);
                p.setLat(lat);
                p.setPid(itemId);
                p.setStatus(statuss);

            }

            c.close();
            db.close();

        }catch (Exception e){

        }
        return p;
    }


    public List<Panic> getByPUID(String pUid){

        ArrayList<Panic> panics = new ArrayList<>();
        try{
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PUID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PLAT,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PLNG,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PADDRESS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PSTATUS

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE + " DESC";


            final Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_PANIC,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry.COLUMN_NAME_PUID +" = '"+pUid+"'",                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            while (c.moveToNext()){

                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PUID));
                String lat = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PLAT));
                String lng = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PLNG));
                String address = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PADDRESS));
                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PDATE));
                String statuss = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_PSTATUS));

                Panic p = new Panic();
                p.setDate(date);
                p.setAddress(address);
                p.setUid(uid);
                p.setLng(lng);
                p.setLat(lat);
                p.setPid(itemId);
                p.setStatus(statuss);
                panics.add(p);

            }

            c.close();
            db.close();

        }catch (Exception e){
e.printStackTrace();
        }
        return panics;
    }
    public Notification getById(long id){

        Notification notification = null;
        try{
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LUID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_CONTACT_NUMBER,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LREQUEST,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MESSAGE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_RESPONSE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LDATE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LSTATUS
            };


// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DATE + " DESC";

            final Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_NOTIFICATION_LOCAL,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry._ID +" = "+id+"",                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            if(c!=null){
                c.moveToNext();
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LUID));
                String contact = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_CONTACT_NUMBER));
                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LDATE));
                String message = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MESSAGE));
                String request = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LREQUEST));
                String response = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_RESPONSE));
                String status = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LSTATUS));

                notification = new Notification();
                notification.setUid(uid);
                notification.setNid(itemId);
                notification.setNotificationContact(contact);
                notification.setNotificationDate(date);
                notification.setNotificationMessage(message);
                notification.setNotificationRequest(request);
                notification.setNotificationResponse(response);
                notification.setNotificationStatus(status);
            }

            c.close();
            db.close();
        }catch (Exception e){

        }
        return notification;
    }

    public int contactCount(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_Contact_ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_LASTNAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber,
                FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS

        };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME + " DESC";

        Cursor c = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        return c.getCount();
    }
}
