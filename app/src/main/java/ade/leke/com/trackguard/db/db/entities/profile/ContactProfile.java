package ade.leke.com.trackguard.db.db.entities.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.Contact;

/**
 * Created by SecureUser on 10/29/2015.
 */
public class ContactProfile {

    FeedReaderDbHelper mDbHelper;
    public ContactProfile(Context context){
        mDbHelper = new FeedReaderDbHelper(context);
    }
    public boolean create(Contact contact){
        boolean state = false;

        try {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();

            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_Contact_ID, contact.getUid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, contact.getFirstname());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LASTNAME,contact.getLastname());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber,contact.getPhoneNumber());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS,contact.getStatus());

            long newRowId;
            newRowId = db.insert(
                    FeedReaderContract.FeedEntry.TABLE_NAME,
                    null,
                    values);
            state = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public ArrayList<Contact> getAllContacts(){
        ArrayList<Contact> contactList = new ArrayList<>();
        try {
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

            while (c.moveToNext()) {
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String firstname = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME));
                String phone = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber));

                String lastname = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LASTNAME));
                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_Contact_ID));
                String status = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS));

                Contact contact = new Contact();
                contact.setLastname(lastname);
                contact.setStatus(status);
                contact.setContactId(itemId);
                contact.setFirstname(firstname);
                contact.setPhoneNumber(phone);
                contact.setUid(uid);
                contactList.add(contact);


            }
        }catch (Exception e){

            e.printStackTrace();
        }

        return contactList;
    }

    public boolean update(Contact contact){
        boolean state = false;

        try {


            SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry._ID,  contact.getContactId());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_Contact_ID, contact.getUid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, contact.getFirstname());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LASTNAME, contact.getLastname());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber, contact.getPhoneNumber());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_STATUS, contact.getStatus());

// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.update(
                    FeedReaderContract.FeedEntry.TABLE_NAME,
                    values, null,null);

            state = true;

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
                    FeedReaderContract.FeedEntry.TABLE_NAME,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber + "='" + uid + "'",
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
