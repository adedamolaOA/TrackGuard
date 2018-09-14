package ade.leke.com.trackguard.db.db.entities.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.Notification;

/**
 * Created by SecureUser on 10/31/2015.
 */
public class NotificationProfile {
    FeedReaderDbHelper mDbHelper;

    public NotificationProfile(Context context) {
        mDbHelper = new FeedReaderDbHelper(context);
    }

    public boolean create(Notification notification) {
        boolean state = false;

        try {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();

            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LUID, notification.getUid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CONTACT_NUMBER, notification.getNotificationContact());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LREQUEST, notification.getNotificationRequest());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MESSAGE, notification.getNotificationMessage());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_RESPONSE, notification.getNotificationResponse());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LDATE, notification.getNotificationDate());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LSTATUS, notification.getNotificationStatus());

            long newRowId;
            newRowId = db.insert(
                    FeedReaderContract.FeedEntry.TABLE_NAME_NOTIFICATION_LOCAL,
                    null,
                    values);
            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public ArrayList<Notification> getAllNotification() {
        ArrayList<Notification> contactList = new ArrayList<>();
        try {
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
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LDATE + " DESC";

            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_NOTIFICATION_LOCAL,  // The table to query
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

                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LUID));
                String contact = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_CONTACT_NUMBER));
                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LDATE));
                String message = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MESSAGE));
                String request = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LREQUEST));
                String response = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_RESPONSE));
                String status = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LSTATUS));

                Notification notification = new Notification();
                notification.setUid(uid);
                notification.setNid(itemId);
                notification.setNotificationContact(contact);
                notification.setNotificationDate(date);
                notification.setNotificationMessage(message);
                notification.setNotificationRequest(request);
                notification.setNotificationResponse(response);
                notification.setNotificationStatus(status);
                contactList.add(notification);


            }
            c.close();
            db.close();
        } catch (Exception e) {

            e.printStackTrace();
        }

        return contactList;
    }

    public boolean newNotification() {
        ArrayList<Notification> contactList = new ArrayList<>();
        try {
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
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LDATE + " DESC";

            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_NOTIFICATION_LOCAL,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LSTATUS +"='NP' OR "+FeedReaderContract.FeedEntry.COLUMN_NAME_LSTATUS +"='NL' OR "+FeedReaderContract.FeedEntry.COLUMN_NAME_LSTATUS +"='NR'",                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            if (c.getCount() > 0) {
                db.close();
                return true;
            } else {
                db.close();
                return false;
            }



        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }


    }

    public ArrayList<Notification> getMovementDate(String startDate, String endDate) {
        ArrayList<Notification> contactList = new ArrayList<>();
        try {
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


            Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_NOTIFICATION_LOCAL,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry.COLUMN_NAME_LDATE + " BETWEEN ? AND ?",                                // The columns for the WHERE clause
                    new String[]{
                            startDate, endDate},  //+ " 23:59:59"      + " 00:00:00"                      // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            while (c.moveToNext()) {
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LUID));
                String contact = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_CONTACT_NUMBER));
                String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LDATE));
                String message = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_MESSAGE));
                String request = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LREQUEST));
                String response = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_RESPONSE));
                String status = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LSTATUS));

                Notification notification = new Notification();
                notification.setUid(uid);
                notification.setNid(itemId);
                notification.setNotificationContact(contact);
                notification.setNotificationDate(date);
                notification.setNotificationMessage(message);
                notification.setNotificationRequest(request);
                notification.setNotificationResponse(response);
                notification.setNotificationStatus(status);
                contactList.add(notification);


            }
            c.close();
            db.close();
        } catch (Exception e) {

            e.printStackTrace();
        }

        return contactList;
    }


    public boolean update(Notification notification) {
        boolean state = false;

        try {


            SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry._ID, notification.getNid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LUID, notification.getUid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_CONTACT_NUMBER, notification.getNotificationContact());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LREQUEST, notification.getNotificationRequest());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MESSAGE, notification.getNotificationMessage());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_RESPONSE, notification.getNotificationResponse());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LDATE, notification.getNotificationDate());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LSTATUS, notification.getNotificationStatus());

// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.update(
                    FeedReaderContract.FeedEntry.TABLE_NAME_NOTIFICATION_LOCAL,
                    values, FeedReaderContract.FeedEntry._ID + " = " + notification.getNid() + "", null);

            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public boolean delete(long nid) {
        boolean state = false;
        try {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();


// Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.delete(
                    FeedReaderContract.FeedEntry.TABLE_NAME_NOTIFICATION_LOCAL,
                    FeedReaderContract.FeedEntry._ID + "=" + nid + "",
                    null);
            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public Contact getByMobileNumber(String mobileNumber) {

        Contact contact = null;
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

            final Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber + " = '" + mobileNumber + "'",                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            if (c != null) {
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

            c.close();
            db.close();

        } catch (Exception e) {

        }
        return contact;
    }

    public Notification getById(long id) {

        Notification notification = null;
        try {
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
                    FeedReaderContract.FeedEntry._ID + " = " + id + "",                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            if (c != null) {
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
        } catch (Exception e) {

        }
        return notification;
    }

    public int contactCount() {
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
