package ade.leke.com.trackguard;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.NotificationDbReader;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Notification;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.NotificationProfile;
import ade.leke.com.trackguard.model.ContactListModel;
import ade.leke.com.trackguard.model.NotificationAdapter;
import ade.leke.com.trackguard.model.NotificationListModel;

public class NotificationActivity extends ActionBarActivity {

    ListView list;
    NotificationAdapter adapter;
    NotificationDbReader nDbHepler;
    public  NotificationActivity CustomListView = null;
    public ArrayList<NotificationListModel> CustomListViewValuesArr = new ArrayList<NotificationListModel>();
    FeedReaderDbHelper mDbHelper;// = new FeedReaderDbHelper(getBaseContext());
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppThemes t = new AppThemeProfile(getBaseContext()).get();
        if(t.getName().equalsIgnoreCase("Panic")) {

            setTheme(R.style.AppTheme_Panic);
        }else if(t.getName().equalsIgnoreCase("News")) {

            setTheme(R.style.AppTheme);
        } else if(t.getName().equalsIgnoreCase("Normal")) {

            setTheme(R.style.AppTheme_normal);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        if(t.getName().equalsIgnoreCase("Panic")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary_S));

            setTheme(R.style.AppTheme_Panic);
        }else if(t.getName().equalsIgnoreCase("News")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));

            setTheme(R.style.AppTheme);
        } else if(t.getName().equalsIgnoreCase("Normal")) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary_N));

            setTheme(R.style.AppTheme_normal);
        }
        SpannableString s = new SpannableString("Notification");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        mDbHelper = new FeedReaderDbHelper(getBaseContext());

        nDbHepler = new NotificationDbReader(getBaseContext());
        CustomListView = this;

        context = this;
        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
        setListData();

        Resources res =getResources();
        list= (ListView)findViewById( R.id.listNotification );  // List defined in XML ( See Below )

        /**************** Create Custom Adapter *********/
        adapter=new NotificationAdapter( CustomListView, CustomListViewValuesArr,res );
        list.setAdapter(adapter);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //new LongRunningGetNotification().execute();
    }

    public ContactListModel getContact(String ids){

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_Contact_ID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
                FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber

        };

        ContactListModel contact = null;
        String[] hj = {ids};
// How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_NAME + " DESC";

        final Cursor c = db.query(
                FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber +" = '"+ids+"'",                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        if(c!=null){
            contact = new ContactListModel();
            c.moveToNext();
            contact.setName(c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME)));
            contact.setMobileNumber(c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber)));
            contact.setImage("");
        }

        return contact;

    }
    public void setListData()
    {

        try {
            List<Notification> notifications = new NotificationProfile(this).getAllNotification();
            for(Notification notification:notifications) {
                NotificationListModel sched = new NotificationListModel();
                if(notification.getNotificationRequest().equalsIgnoreCase("LR")){

                    sched.setSubject("Location Request");
                }else if(notification.getNotificationRequest().equalsIgnoreCase("GP")){

                    sched.setSubject("Panic Alert         ");
                }else if(notification.getNotificationRequest().equalsIgnoreCase("GR")){

                    sched.setSubject("Current Location");
                }
                //sched.setSubject(notification.getNotificationContact());
                sched.setMessage(notification.getNotificationMessage());
                sched.setPhoneId(notification.getNid() + "");
                sched.setViewStatus(notification.getNotificationStatus());//L//C//P
                sched.setDate(notification.getNotificationDate());

                /******** Take Model Object in ArrayList **********/
                CustomListViewValuesArr.add(sched);
            }
        }catch (Exception e){

            e.printStackTrace();
        }


    }

    public void setListData2(String datas){
        try {
            //Toast.makeText(this,datas,Toast.LENGTH_LONG).show();
            JSONArray jObj = new JSONArray(datas);
            //JSONArray jArr = jObj.getJSONArray(0);
            //Toast.makeText(this,jObj.length()+" ------ "+jObj.length(),Toast.LENGTH_LONG).show();
            for (int i=0; i < jObj.length(); i++) {

                JSONObject obj = jObj.getJSONObject(i);
                NotificationListModel sched = new NotificationListModel();
                sched.setDate(obj.getString("dateOfNotification"));
                String[] msg = obj.getString("message").split(",");

                if(obj.getString("request").equalsIgnoreCase("Add")) {
                    sched.setSubject("Request to join your contact");
                    sched.setMessage("A Contact would like you to keep an eye on them.");
                }else
                    if(obj.getString("request").equalsIgnoreCase("View")) {
                        sched.setSubject("Location Viewed");
                        sched.setMessage(msg[0]+" "+msg[1]+" viewed your current Location");

                }else
                    if(obj.getString("request").equalsIgnoreCase("Panic")) {
                        sched.setSubject("Panic Alert");
                        sched.setMessage(msg[0]+" "+msg[1]+" sent you a panic alert");
                        sched.setViewStatus("P");

                    }
                sched.setPhoneId(obj.getString("phoneId"));
                CustomListViewValuesArr.add(sched);

            }
            Resources res =getResources();
            adapter=new NotificationAdapter( CustomListView, CustomListViewValuesArr,res );
            list.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id==android.R.id.home)

        {
            AppThemes t = new AppThemeProfile(getBaseContext()).get();
            if(t.getName().equalsIgnoreCase("Panic")) {
                Intent intentMain = new Intent(NotificationActivity.this,
                        MainActivity.class);
                intentMain.putExtra("serviceState", true);
                NotificationActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }else if(t.getName().equalsIgnoreCase("News")) {
                Intent intentMain = new Intent(NotificationActivity.this,
                        Main2Activity.class);
                intentMain.putExtra("serviceState", true);
                NotificationActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            } else if(t.getName().equalsIgnoreCase("Normal")) {
                Intent intentMain = new Intent(NotificationActivity.this,
                        Main3Activity.class);
                intentMain.putExtra("serviceState", true);
                NotificationActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }
        }


        return super.onOptionsItemSelected(item);
    }

    public void onItemClick(int mPosition)
    {
        NotificationListModel tempValues = ( NotificationListModel ) CustomListViewValuesArr.get(mPosition);

        Notification n = new NotificationProfile(context).getById(Long.parseLong(tempValues.getPhoneId()));
        if(n.getNotificationStatus().equalsIgnoreCase("NL")){
            n.setNotificationStatus("L");
        }else if(n.getNotificationStatus().equalsIgnoreCase("NP")){
            n.setNotificationStatus("P");
        }else if(n.getNotificationStatus().equalsIgnoreCase("NR")){
            n.setNotificationStatus("R");
        }

        boolean state = new NotificationProfile(context).update(n);
        if(state) {
            //Toast.makeText(NotificationActivity.this,tempValues.getMessage(),Toast.LENGTH_LONG).show();
            Intent intentMain = new Intent(NotificationActivity.this,
                    ViewNotificationActivity.class);
            intentMain.putExtra("subject", tempValues.getSubject());
            intentMain.putExtra("message", tempValues.getMessage());
            intentMain.putExtra("date", tempValues.getDate());
            intentMain.putExtra("type",n.getNotificationStatus());
            intentMain.putExtra("uid",n.getUid());
            intentMain.putExtra("nid",n.getNid());
            if(!n.getNotificationResponse().equalsIgnoreCase("None")){
                String[] location = n.getNotificationResponse().split(":");
                intentMain.putExtra("lat",location[0]);
                intentMain.putExtra("lng",location[1]);
            }else{
                intentMain.putExtra("lat","None");
                intentMain.putExtra("lng","None");
            }
            intentMain.putExtra("phoneNumber",n.getNotificationContact());

            NotificationActivity.this.startActivity(intentMain);
            finish();

        }

    }

    private class LongRunningGetNotification extends AsyncTask<Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];

                n =  in.read(b);

                if (n>0) out.append(new String(b, 0, n));

            }

            return out.toString();

        }

        @Override

        protected String doInBackground(Void... params) {
            String uuid = Settings.Secure.getString(getBaseContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://10.0.2.2:8080/MobileGuardRestful/webresources/com.eds.restful.notification/1/"+uuid);
            String text = null;
            try {

                HttpResponse response = httpClient.execute(httpGet, localContext);

                HttpEntity entity = response.getEntity();

                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();

            }

            return text;

        }

        protected void onPostExecute(String results) {
            if (results!=null) {

                setListData2(results);




            }



        }

    }

}
