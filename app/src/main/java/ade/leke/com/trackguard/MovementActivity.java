package ade.leke.com.trackguard;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

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
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Movement;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.MovementProfile;
import ade.leke.com.trackguard.model.MovementAdapter;
import ade.leke.com.trackguard.model.MovementListModel;

public class MovementActivity extends ActionBarActivity {

    ListView list;
    MovementAdapter adapter;
    public MovementActivity CustomListView = null;
    public ArrayList<MovementListModel> CustomListViewValuesArr = new ArrayList<MovementListModel>();
    FeedReaderDbHelper mDbHelper;// = new FeedReaderDbHelper(getBaseContext());
    Context context;
    int contact_count;
    String d;
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
        setContentView(R.layout.activity_movement);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        d = getIntent().getStringExtra("date");
        SpannableString s = new SpannableString(d);
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);


        mDbHelper = new FeedReaderDbHelper(getBaseContext());
        CustomListView = this;

        context = this;
        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/

        loadMovementData(d);

        Resources res = getResources();
        list = (ListView) findViewById(R.id.list_movement);  // List defined in XML ( See Below )

        /**************** Create Custom Adapter *********/
        adapter = new MovementAdapter(CustomListView, CustomListViewValuesArr, res);
        list.setAdapter(adapter);
        registerForContextMenu(list);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //new LongRunningGetMovement().execute();


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Movement Log Menu");
        menu.add(0, v.getId(), 0, "View Movement In Map");
        menu.add(0, v.getId(), 0, "Delete Movement");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "View Movement In Map") {
            onItemClick(list.getSelectedItemPosition());
        } else if (item.getTitle() == "Delete Movement") {
            onItemClickDelete(list.getSelectedItemPosition());
        } else {
            return false;
        }
        return true;
    }

    List<Movement> movements;

    public void loadMovementData(String date) {
        movements = new MovementProfile(getBaseContext()).getMovementDateDESC(date+" 00:00:00",date+" 23:59:59");
        for (Movement m : movements) {
            MovementListModel sched = new MovementListModel();
            sched.setDate(m.getMDate());
            String result = m.getLat() + " " + m.getLng();
            sched.setAddress(m.getAddress());
            CustomListViewValuesArr.add(sched);
        }
    }

    public void setListData(String data) {
        //Toast.makeText(MovementActivity.this,data,Toast.LENGTH_LONG).show();
        try {
            JSONArray jObj = new JSONArray(data);
            //JSONArray jArr = jObj.getJSONArray(0);
            //Toast.makeText(this,jObj.length()+" ------ "+jObj.length(),Toast.LENGTH_LONG).show();
            for (int i = 0; i < jObj.length(); i++) {

                JSONObject obj = jObj.getJSONObject(i);
                MovementListModel sched = new MovementListModel();
                sched.setDate(obj.getString("date"));
                double lat = Double.parseDouble(obj.getString("lat"));
                double lng = Double.parseDouble(obj.getString("lng"));
                String result = lat + " " + lng;
                sched.setAddress(result);
                CustomListViewValuesArr.add(sched);

            }
            Resources res = getResources();
            adapter = new MovementAdapter(CustomListView, CustomListViewValuesArr, res);
            list.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home)

        {

            Intent intentMain = new Intent(MovementActivity.this,
                    MovementMainActivity.class);
            intentMain.putExtra("serviceState", true);
            MovementActivity.this.startActivity(intentMain);
            Log.i("Content ", " Menu layout ");
            finish();

        } else if (id == R.id.refresh)

        {
            CustomListViewValuesArr.clear();
            loadMovementData(d);

            Resources res = getResources();
            list = (ListView) findViewById(R.id.list_movement);  // List defined in XML ( See Below )

            /**************** Create Custom Adapter *********/
            adapter = new MovementAdapter(CustomListView, CustomListViewValuesArr, res);
            list.setAdapter(adapter);


        }else if (id == R.id.delete)

        {
            boolean delete = new MovementProfile(getBaseContext()).delete(d+" 00:00:00",d+" 23:59:59");
            if (delete) {

                Toast.makeText(getBaseContext(), "Movement Records for  " + d + " deleted", Toast.LENGTH_LONG).show();
                Intent intentMain = new Intent(MovementActivity.this,
                        MovementMainActivity.class);
                MovementActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();

            } else {
                Toast.makeText(getBaseContext(), "failed", Toast.LENGTH_LONG).show();
            }


        }
        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    public void onItemClick(int mPosition) {
        MovementListModel tempValues = (MovementListModel) CustomListViewValuesArr.get(mPosition);
        Movement movement = movements.get(mPosition);
        Intent intentMain = new Intent(MovementActivity.this,
                MovementMapActivity.class);
        intentMain.putExtra("date", tempValues.getDate());


        intentMain.putExtra("map_state", true);
        intentMain.putExtra("id", movement.getMid());

        intentMain.putExtra("lat", movement.getLat());
        intentMain.putExtra("lng", movement.getLng());
        MovementActivity.this.startActivity(intentMain);
        finish();


    }

    public void onItemClickDelete(int mPosition) {
        MovementListModel tempValues = (MovementListModel) CustomListViewValuesArr.get(mPosition);
        Movement movement = movements.get(mPosition);
        boolean delete = new MovementProfile(getBaseContext()).delete(movement.getMid());
        if (delete) {
            Toast.makeText(MovementActivity.this, "Movement deleted", Toast.LENGTH_LONG).show();
            CustomListViewValuesArr.clear();
            loadMovementData(d);

            Resources res = getResources();
            list = (ListView) findViewById(R.id.list_movement);  // List defined in XML ( See Below )

            /**************** Create Custom Adapter *********/
            adapter = new MovementAdapter(CustomListView, CustomListViewValuesArr, res);
            list.setAdapter(adapter);

        } else {
            Toast.makeText(MovementActivity.this, "Unable to deleted movement", Toast.LENGTH_LONG).show();
        }


    }

    private class LongRunningGetMovement extends AsyncTask<Void, Void, String> {
        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];

                n = in.read(b);

                if (n > 0) out.append(new String(b, 0, n));

            }

            return out.toString();

        }

        @Override

        protected String doInBackground(Void... params) {
            String uuid = Settings.Secure.getString(getBaseContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = new HttpGet("http://10.0.2.2:8080/MobileGuardRestful/webresources/logmovement/1/" + uuid);
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
            if (results != null) {

                setListData(results);


            }


        }

    }


}
