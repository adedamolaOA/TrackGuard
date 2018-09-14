package ade.leke.com.trackguard.db.db.entities.profile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.News;
import ade.leke.com.trackguard.db.db.entities.Settings;

/**
 * Created by SecureUser on 1/29/2016.
 */
public class NewsProfile {

    FeedReaderDbHelper mDbHelper;
    public NewsProfile(Context context){
        mDbHelper = new FeedReaderDbHelper(context);
    }
    public boolean create(News settings){
        boolean state = false;

        try {

            SQLiteDatabase db = mDbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NUID, settings.getUuid());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NCLIENT, settings.getClient());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NSUBJECT, settings.getSubject());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NEWS,settings.getNews());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NIMAGE,settings.getImage());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NDATE,settings.getDate());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NBulletin,settings.getBulletin());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAuthor,settings.getAuthor());
            values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NSTATUS,settings.getStatus());


            long newRowId;
            newRowId = db.insert(
                    FeedReaderContract.FeedEntry.TABLE_NAME_NEWS,
                    null,
                    values);
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
                    FeedReaderContract.FeedEntry.TABLE_NAME_NEWS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NUID + "='" + uid + "'",
                    null);
            state = true;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return state;
    }

    public ArrayList<News> get(){

        ArrayList<News> s = new ArrayList<>();
        try{
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NUID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NCLIENT,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NSUBJECT,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NEWS,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NIMAGE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NDATE,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NBulletin,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NAuthor,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SSTATUS

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry._ID + " DESC";


            final Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME_NEWS,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
            if(c!=null){
                while(c.moveToNext()) {
                    //c.moveToNext();
                    long itemId = c.getLong(
                            c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));

                    String uid = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NUID));
                    String client = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NCLIENT));
                    String subject = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NSUBJECT));
                    String news = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NEWS));
                    String image = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NIMAGE));
                    String date = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NDATE));
                    String bulletin = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NBulletin));
                    String author = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAuthor));
                    String status = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NSTATUS));

                    News n = new News();
                    n.setStatus(status);
                    n.setId(itemId);
                    n.setClient(client);
                    n.setSubject(subject);
                    n.setNews(news);
                    n.setImage(image);
                    n.setDate(date);
                    n.setAuthor(author);
                    n.setBulletin(bulletin);
                    n.setUuid(uid);
                    s.add(n);
                }

            }

            c.close();
            db.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return s;
    }

}
