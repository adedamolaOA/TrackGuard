package ade.leke.com.trackguard;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;
import ade.leke.com.trackguard.model.ContactListModel;

public class ContactDisplayActivity extends ActionBarActivity {

    private GoogleMap mMap;
    String ids = "";
    String name = "";
    double lat = 0;//9.072264;
    double lng = 0; //7.491302;
    ContactProfile profile;

    public  ContactDisplayActivity CustomListView = null;
    public ArrayList<ContactListModel> CustomListViewValuesArr = new ArrayList<ContactListModel>();
    FeedReaderDbHelper mDbHelper;
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
        setContentView(R.layout.activity_contact_display);
        Intent intent = getIntent();
        ids = intent.getStringExtra("id");
        name = intent.getStringExtra("name");
        lat  = Double.parseDouble(intent.getStringExtra("lat"));
        lng  = Double.parseDouble(intent.getStringExtra("lng"));

        profile = new ContactProfile(getBaseContext());
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
        SpannableString s = new SpannableString(name);
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        mDbHelper = new FeedReaderDbHelper(getBaseContext());
        CustomListView = this;


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setUpMapIfNeeded();
        new LongRunningGetIO().execute();

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMapIfNeeded(String locationData) {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap(locationData);
            }
        }
    }

    private void setUpMap(String locationData) {

        //Toast.makeText(this,datas,Toast.LENGTH_LONG).show();
        try {

            JSONArray jObj = new JSONArray(locationData);
            JSONObject objs = jObj.getJSONObject(0);

            String latss = objs.getString("lat");
            String lngss = objs.getString("lng");
            String dates = objs.getString("date");
            double latitudes = Double.parseDouble(latss);
            double longtuides = Double.parseDouble(lngss);


            //JSONArray jArr = jObj.getJSONArray(0);
            //Toast.makeText(this,jObj.length()+" ------ "+jObj.length(),Toast.LENGTH_LONG).show();
            for (int i=1; i < jObj.length(); i++) {

                JSONObject obj = jObj.getJSONObject(i);

                String lats = obj.getString("lat");
                String lngs = obj.getString("lng");
                String date = obj.getString("date");
                double latitude = Double.parseDouble(lats);
                double longtuide = Double.parseDouble(lngs);

                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longtuide)).title(name +" "+date));

            }

            if(jObj.length()==0){
                Toast.makeText(this,"No location available",Toast.LENGTH_LONG).show();
            }

             CameraPosition cp = new CameraPosition.Builder()
                    .target(new LatLng(latitudes, longtuides))
                    .zoom(7)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

            mMap.addMarker(new MarkerOptions().position(new LatLng(latitudes, longtuides)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));




        }catch (Exception e){
            Toast.makeText(this,"No location available",Toast.LENGTH_LONG).show();
        }

    }
    private void setUpMap() {

        CameraPosition cp = new CameraPosition.Builder()
                .target(new LatLng(lat, lng))
                .zoom(17)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));

        //mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete_contact) {

            boolean state = profile.delete(ids);
            if(state) {
                Toast.makeText(this,
                        "" + name + " deleted successfully"
                        ,
                        Toast.LENGTH_LONG)
                        .show();

                Intent intentMain = new Intent(ContactDisplayActivity.this,
                        ContactActivity.class);

                ContactDisplayActivity.this.startActivity(intentMain);
            }
        }else if(id==android.R.id.home)

        {

            Intent intentMain = new Intent(ContactDisplayActivity.this,
                    ContactActivity.class);
            ContactDisplayActivity.this.startActivity(intentMain);
            Log.i("Content ", " Menu layout ");
            finish();

        }else if(id==R.id.track_contact) {

            sendSmsByManager();
        }else
         if(id==R.id.edit_contact)
        {
            final Dialog dialog = new Dialog(ContactDisplayActivity.this);
            dialog.setContentView(R.layout.contact_dialog);
            SpannableString s = new SpannableString("Edit Contact");
            s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

            dialog.setTitle(s);
            final Contact contact = profile.getByMobileNumber(ids);
            // set the custom dialog components - text, image and button
            final EditText name = (EditText) dialog.findViewById(R.id.txtCName);
            final EditText phone = (EditText) dialog.findViewById(R.id.txtCPhone);
            if(contact !=null) {
                name.setText(contact.getFirstname()+" "+contact.getLastname());
                phone.setText(contact.getPhoneNumber());
            }
            Button saveContact = (Button) dialog.findViewById(R.id.butSaveContact);
            saveContact.setText("Update");
            // if button is clicked, close the custom dialog
            saveContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        if(contact!=null) {
                            String[] names = name.getText().toString().split(" ");
                            String firstname = name.getText().toString();
                            String lastname = "None";
                            if(names.length>1){

                                firstname = names[0];
                                lastname = names[1];
                            }
                            contact.setFirstname(firstname);
                            contact.setLastname(lastname);
                            boolean state = profile.update(contact);
                            SpannableString s2 = new SpannableString(name.getText().toString());
                            s2.setSpan(new TypefaceSpan(ContactDisplayActivity.this, "exo_medium.otf"), 0, s2.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
                            ContactDisplayActivity.this.setTitle(s2);
                            if(state) {

                                Toast.makeText(ContactDisplayActivity.this, "Emergency Contact updated successfully", Toast.LENGTH_LONG).show();

                                dialog.dismiss();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }

            });

            Button cancel = (Button) dialog.findViewById(R.id.butContactCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                }
            });


            dialog.show();


        }
        return super.onOptionsItemSelected(item);
    }

    public void sendSmsByManager() {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();

                smsManager.sendTextMessage(ids,
                        null,
                        "MGloc:" + 21342, //requesting location from user
                        null,
                        null);
                Log.d("Send SMS", "Sent msg to " + ids);

            Toast.makeText(getApplicationContext(), "Request Sent to "+name+" for location",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),"Request Failed",
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
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
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            String uuid = Settings.Secure.getString(getBaseContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            HttpGet httpGet = new HttpGet("http://10.0.2.2:8080/MobileGuardRestful/webresources/com.eds.restful.logmovement/1/"+uuid +"/"+ ids);
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


                setUpMapIfNeeded(results);


            }



        }

    }
}
