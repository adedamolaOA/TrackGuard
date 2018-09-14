package ade.leke.com.trackguard.db.db.entities.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.Movement;

/**
 * Created by SecureUser on 10/30/2015.
 */
public class MovementProfile {

    FeedReaderDbHelper mDbHelper;
    public MovementProfile(Context context){
        mDbHelper = new FeedReaderDbHelper(context);
    }
    public boolean create(Movement movement){
        boolean state = false;

        try {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();

            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LAT, movement.getLat());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LNG, movement.getLng());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS,movement.getAddress());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE,movement.getMDate());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UID,movement.getUid());

            long newRowId;
            newRowId = db.insert(
                    FeedReaderContract.FeedEntry.TABLE_NAME_Movement,
                    null,
                    values);
            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public ArrayList<Movement> getAllMovement(){
        ArrayList<Movement> contactList = new ArrayList<>();
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LAT,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LNG,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DATE + " DESC";

            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_Movement,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            while (c.moveToNext()) {
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String lat = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LAT));
                String lng = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LNG));

                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE));
                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_UID));
                String address = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS));

                Movement movement = new Movement();
                movement.setAddress(address);
                movement.setLat(lat);
                movement.setLng(lng);
                movement.setMDate(date);
                movement.setMid(itemId);
                movement.setUid(uid);
                contactList.add(movement);


            }
        }catch (Exception e){

            e.printStackTrace();
        }

        return contactList;
    }


    public ArrayList<Movement> getMovementDate(String startDate,String endDate){
        ArrayList<Movement> contactList = new ArrayList<>();
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LAT,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LNG,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DATE + " ASC";


            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_Movement,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE + " BETWEEN ? AND ?",                                // The columns for the WHERE clause
                    new String[] {
                            startDate, endDate},  //+ " 23:59:59"      + " 00:00:00"                      // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            while (c.moveToNext()) {
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String lat = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LAT));
                String lng = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LNG));

                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE));
                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_UID));
                String address = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS));

                Movement movement = new Movement();
                movement.setAddress(address);
                movement.setLat(lat);
                movement.setLng(lng);
                movement.setMDate(date);
                movement.setMid(itemId);
                movement.setUid(uid);
                contactList.add(movement);


            }
        }catch (Exception e){

            e.printStackTrace();
        }

        return contactList;
    }

    public ArrayList<Movement> getMovementDateDESC(String startDate,String endDate){
        ArrayList<Movement> contactList = new ArrayList<>();
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LAT,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LNG,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DATE + " DESC";


            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_Movement,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE + " BETWEEN ? AND ?",                                // The columns for the WHERE clause
                    new String[] {
                            startDate, endDate},  //+ " 23:59:59"      + " 00:00:00"                      // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            while (c.moveToNext()) {
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String lat = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LAT));
                String lng = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LNG));

                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE));
                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_UID));
                String address = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS));

                Movement movement = new Movement();
                movement.setAddress(address);
                movement.setLat(lat);
                movement.setLng(lng);
                movement.setMDate(date);
                movement.setMid(itemId);
                movement.setUid(uid);
                contactList.add(movement);


            }
        }catch (Exception e){

            e.printStackTrace();
        }

        return contactList;
    }
    public  Movement getMovementById(long id){
        Movement movement=null;
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LAT,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LNG,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry.COLUMN_NAME_DATE + " DESC";


            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_Movement,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry._ID + " ="+id,                                // The columns for the WHERE clause
                    null,  //+ " 23:59:59"      + " 00:00:00"                      // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            while (c.moveToNext()) {
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String lat = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LAT));
                String lng = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LNG));

                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE));
                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_UID));
                String address = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS));

                movement = new Movement();
                movement.setAddress(address);
                movement.setLat(lat);
                movement.setLng(lng);
                movement.setMDate(date);
                movement.setMid(itemId);
                movement.setUid(uid);



            }
        }catch (Exception e){

            e.printStackTrace();
        }

        return movement;
    }

    public boolean update(Movement movement){
        boolean state = false;

        try {


            SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LAT, movement.getLat());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LNG, movement.getLng());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_ADDRESS,movement.getAddress());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE,movement.getMDate());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UID,movement.getUid());

// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.update(
                    FeedReaderContract.FeedEntry.TABLE_NAME_Movement,
                    values, FeedReaderContract.FeedEntry._ID +" = "+ movement.getMid()+"" ,null);

            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public boolean delete(String startDate,String endDate) {
        boolean state = false;
        try {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();


// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.delete(
                    FeedReaderContract.FeedEntry.TABLE_NAME_Movement,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MDATE + " BETWEEN ? AND ?",                                // The columns for the WHERE clause
                    new String[] {
                            startDate, endDate});
            state = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public boolean delete(long uid) {
        boolean state = false;
        try {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();


// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.delete(
                    FeedReaderContract.FeedEntry.TABLE_NAME_Movement,
                    FeedReaderContract.FeedEntry._ID + "=" + uid + "",
                    null);
            state = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public Contact getByMobileNumber(String mobileNumber){

        Contact contact = null;
        try{
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

            final Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber +" = '"+mobileNumber+"'",                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            if(c!=null){
                c.moveToNext();
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String firstname = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME));
                String phone = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber));

                String lastname = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LASTNAME));
                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_Contact_ID));
                String status = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS));
                contact = new Contact();
                contact.setFirstname(firstname);
                contact.setPhoneNumber(phone);
                contact.setUid(uid);
                contact.setContactId(itemId);
                contact.setLastname(lastname);
                contact.setStatus(status);
            }

        }catch (Exception e){

        }
        return contact;
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
