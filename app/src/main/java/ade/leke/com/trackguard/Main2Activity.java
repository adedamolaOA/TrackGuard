package ade.leke.com.trackguard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.common.GPSTracker;
import ade.leke.com.trackguard.common.TimeDifference;
import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.News;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;
import ade.leke.com.trackguard.db.db.entities.profile.NewsProfile;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;
import ade.leke.com.trackguard.model.MyAdapter;
import ade.leke.com.trackguard.model.NewsDataObject;
import ade.leke.com.trackguard.model.NewsRecyclerViewAdapter;
import ade.leke.com.trackguard.services.LogMovementService;
import ade.leke.com.trackguard.services.MovementService;
import ade.leke.com.trackguard.services.NewsService;
import ade.leke.com.trackguard.services.PayService;
import ade.leke.com.trackguard.services.SimChangeService;

public class Main2Activity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener{
    //First We Declare Titles And Icons For Our Navigation Drawer List View
    //This Icons And Titles Are holded in an Array as you can see
    AppLocationService appLocationService;
    String TITLES[] = {"News",
            "Current Location",
            "Notification",
            "Panic Contacts",
            "Movement Log",
            "Profile",
            "Settings",
            "About Us"};
    int ICONS[] = {R.drawable.news,R.drawable.map_pin,R.drawable.mail_m,R.drawable.users,R.drawable.book,R.drawable.id_card,R.drawable.settings,R.drawable.grid};

    //Similarly we Create a String Resource for the name and email in the header view
    //And we also create a int resource for profile picture in the header view

    String NAME = " ";
    String EMAIL = " ";
    int PROFILE = R.drawable.contact_image;
    String profileImage="";
    private Toolbar toolbar;                              // Declaring the Toolbar Object

    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
    DrawerLayout Drawer;                                  // Declaring DrawerLayout

    ActionBarDrawerToggle mDrawerToggle;                  // Declaring Action Bar Drawer Toggle

    private RecyclerView mRecyclerViewNews;
    private RecyclerView.Adapter mAdapterNews;
    private RecyclerView.LayoutManager mLayoutManagerNews;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<News> newsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);

                                        mAdapterNews = new NewsRecyclerViewAdapter(Main2Activity.this,getDataSet());
                                        mRecyclerViewNews.setAdapter(mAdapterNews);
                                        mRecyclerViewNews.refreshDrawableState();
                                        swipeRefreshLayout.setRefreshing(false);

                                        //fetchMovies();
                                    }
                                }
        );
        SpannableString s = new SpannableString("Mobile Guard");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        getBaseContext().setTheme(R.style.AppTheme_normal);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        UserProfile userProfile = new UserProfile(Main2Activity.this);
        User user = userProfile.get();
        profileImage=user.getVersion();
        toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));
getSupportActionBar().setTitle(s);
        startPayService();
        startNewsService();
        startLogMovement();
        mRecyclerViewNews = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerViewNews.setHasFixedSize(true);
        mLayoutManagerNews = new LinearLayoutManager(this);
        mRecyclerViewNews.setLayoutManager(mLayoutManagerNews);
        mAdapterNews = new NewsRecyclerViewAdapter(this,getDataSet());
        mRecyclerViewNews.setAdapter(mAdapterNews);

        if(!getDataSet().isEmpty()) {
           }else{
            LinearLayout l = (LinearLayout)findViewById(R.id.news_layout);

            ImageView img = new ImageView(getBaseContext());
            TextView txt = new TextView(getBaseContext());
            s = new SpannableString("No Network Connection");
            s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

            txt.setText(s);
            txt.setFitsSystemWindows(true);
            //txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            txt.setTextColor(getResources().getColor(R.color.color_non_selected));
            txt.setGravity(Gravity.CENTER_HORIZONTAL);
            img.setImageResource(R.drawable.notconnected);
            img.setPadding(0,100,0,0);
            img.setFitsSystemWindows(true);
            l.removeView(mRecyclerViewNews);
            l.addView(img);
            l.addView(txt);
        }
        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);
        appLocationService = new AppLocationService(
                Main2Activity.this);


        NAME = user.getFirstname()+" "+user.getLastname();
        EMAIL = user.getEmail();

        mRecyclerView = (RecyclerView) findViewById(R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View

        mRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        mAdapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE,profileImage,this);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture

        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView

        final GestureDetector mGestureDetector = new GestureDetector(Main2Activity.this, new GestureDetector.SimpleOnGestureListener() {

            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

        });


        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());


                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    Drawer.closeDrawers();
                    //Toast.makeText(Main2Activity.this, "The Item Clicked is: " + recyclerView.getChildPosition(child), Toast.LENGTH_SHORT).show();


                    if (recyclerView.getChildPosition(child)==4) {
                        Intent intentMain = new Intent(Main2Activity.this,
                                ContactActivity.class);
                        Main2Activity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");

                        finish();
                        //return true;
                    } else if (recyclerView.getChildPosition(child)==3) {

                        Intent intentMain = new Intent(Main2Activity.this,
                                NotificationActivity.class);
                        Main2Activity.this.startActivity(intentMain);
                        finish();

                    } else if (recyclerView.getChildPosition(child)==2) {

                        Location gpsLocation = appLocationService
                                .getLocation(LocationManager.GPS_PROVIDER);
                        if (gpsLocation != null) {
                            System.out.println("================================================================was here");

                            double clatitude = gpsLocation.getLatitude();
                            double clongitude = gpsLocation.getLongitude();

                            Intent intentMain = new Intent(Main2Activity.this,
                                    MovementMapActivity.class);
                            intentMain.putExtra("date", "My Current Location");
                            intentMain.putExtra("map_state", false);
                            intentMain.putExtra("lat", clatitude + "");
                            intentMain.putExtra("lng", clongitude + "");
                            Main2Activity.this.startActivity(intentMain);
                            finish();
                        } else {

                            GPSTracker gps = new GPSTracker(Main2Activity.this);
                            if (gps.canGetLocation()) {


                                double latitude = gps.getLatitude();
                                double longitude = gps.getLongitude();
                                gps.stopUsingGPS();

                                //new LongRunningPanicNotification().execute();

                                Intent intentMain = new Intent(Main2Activity.this,
                                        MovementMapActivity.class);
                                intentMain.putExtra("date", "My Current Location");
                                intentMain.putExtra("map_state", false);
                                intentMain.putExtra("lat", latitude + "");
                                intentMain.putExtra("lng", longitude + "");
                                Main2Activity.this.startActivity(intentMain);
                                finish();

                            } else {
                                gpsLocation = appLocationService
                                        .getLocation(LocationManager.PASSIVE_PROVIDER);
                                if (gpsLocation != null) {
                                    System.out.println("==========================*************==========================was here");

                                    double cclatitude = gpsLocation.getLatitude();
                                    double cclongitude = gpsLocation.getLongitude();

                                    Intent intentMain = new Intent(Main2Activity.this,
                                            MovementMapActivity.class);
                                    intentMain.putExtra("date", "My Current Location");
                                    intentMain.putExtra("map_state", false);
                                    intentMain.putExtra("lat", cclatitude + "");
                                    intentMain.putExtra("lng", cclongitude + "");
                                    Main2Activity.this.startActivity(intentMain);
                                    finish();
                                } else {
                                    showSettingsAlert();
                                }

                            }


                        }
                    }
                    else if (recyclerView.getChildPosition(child)==5) {

                        Intent intentMain = new Intent(Main2Activity.this,
                                MovementMainActivity.class);
                        Main2Activity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();

                    } else if (recyclerView.getChildPosition(child)==7) {

                        Intent intentMain = new Intent(Main2Activity.this,
                                MgSettingsActivity.class);
                        Main2Activity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();

                    } else if (recyclerView.getChildPosition(child)==8) {


                        Intent intentMain = new Intent(Main2Activity.this,
                                MoreInformationActivity.class);
                        Main2Activity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();

                    }  else if (recyclerView.getChildPosition(child)==6) {


                        Intent intentMain = new Intent(Main2Activity.this,
                                ProfileActivity.class);
                        Main2Activity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();

                    }
                    return true;

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

            }
        });



        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager

        mRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager


        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);        // Drawer object Assigned to the view
        mDrawerToggle = new ActionBarDrawerToggle(this,Drawer,toolbar,R.string.openDrawer,R.string.closeDrawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened( As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }



        }; // Drawer Toggle Object Made
        Drawer.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
        mDrawerToggle.syncState();               // Finally we set the drawer toggle sync State
        // Finally we set the drawer toggle sync State


    }

    @Override
    public void onRefresh() {
       // fetchMovies();
fetch();
    }

    public void fetch(){
        swipeRefreshLayout.setRefreshing(true);

        mAdapterNews = new NewsRecyclerViewAdapter(this,getDataSet());
        mRecyclerViewNews.setAdapter(mAdapterNews);
        mRecyclerViewNews.refreshDrawableState();
        swipeRefreshLayout.setRefreshing(false);


    }

    private ArrayList<NewsDataObject> getDataSet() {
        ArrayList results = new ArrayList<NewsDataObject>();
            ArrayList<News> news = new NewsProfile(getBaseContext()).get();
        newsList = news;
        for(News n: news) {
            NewsDataObject obj = new NewsDataObject();
            obj.setNews(n.getNews());
            obj.setImage(n.getImage());
            obj.setSubject(n.getSubject());
            SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            try{

            obj.setTimeAgo(new TimeDifference(new Date(), s.parse(n.getDate())).getDifferenceString());
            results.add(obj);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return results;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            setTheme();
            return true;
        }else if (id == R.id.action_profile) {
            setTheme();
            return true;
        }else if (id == R.id.action_aboutus) {
            setTheme();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
    public void contactOptions() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.theme_dialog);

        SpannableString s = new SpannableString("Switch Mode");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster


        dialog.setTitle(s);
        dialog.setCancelable(true);


        // set the custom dialog components - text, image and button
        final RadioButton radioPanicMode = (RadioButton) dialog.findViewById(R.id.radioPanicMode);
        final RadioButton radioNewsMode = (RadioButton) dialog.findViewById(R.id.radioNewsMode);

        final RadioButton radioNormalMode = (RadioButton) dialog.findViewById(R.id.radioNormalMode);
        Button saveContact = (Button) dialog.findViewById(R.id.butSetTheme);
        // if button is clicked, close the custom dialog
        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(radioNormalMode.isChecked()){
                    //toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
                    AppThemes a = new AppThemeProfile(getBaseContext()).get();
                    a.setName("Normal");
                    boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                    if(state){Toast.makeText(getBaseContext(),"Normal Mode",Toast.LENGTH_LONG).show();
                        Intent intentMain = new Intent(Main2Activity.this,
                                Main3Activity.class);
                        intentMain.putExtra("serviceState", true);
                        Main2Activity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();
                        dialog.dismiss();}

                }else if(radioNewsMode.isChecked()){
                    AppThemes a = new AppThemeProfile(getBaseContext()).get();
                    a.setName("News");
                    boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                    if(state){Toast.makeText(getBaseContext(),"News Mode",Toast.LENGTH_LONG).show();dialog.dismiss();}
                }else if(radioPanicMode.isChecked()){
                    AppThemes a = new AppThemeProfile(getBaseContext()).get();
                    a.setName("Panic");
                    boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                    if(state){Toast.makeText(getBaseContext(),"Security Mode",Toast.LENGTH_LONG).show();
                        Intent intentMain = new Intent(Main2Activity.this,
                                MainActivity.class);
                        intentMain.putExtra("serviceState", true);
                        Main2Activity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();
                        dialog.dismiss();}
                }
            }

        });
/*
        Button cancel = (Button) dialog.findViewById(R.id.butManualContact);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addContactManual();

            }
        });*/
        dialog.show();
    }

    public void setTheme(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(Main2Activity.this);
        // builderSingle.setIcon(R.drawable.logo);
        SpannableString s = new SpannableString("Select Mode");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        builderSingle.setTitle("Select Mode");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                Main2Activity.this,
                android.R.layout.select_dialog_item);
                arrayAdapter.add("News Mode");
        arrayAdapter.add("Security Mode");
        arrayAdapter.add("Normal Mode");


        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final int loc = which;
                        if(loc==2){
                            //toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryDark));
                            AppThemes a = new AppThemeProfile(getBaseContext()).get();
                            a.setName("Normal");
                            boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                            if(state){Toast.makeText(getBaseContext(),"Normal Mode",Toast.LENGTH_LONG).show();
                                Intent intentMain = new Intent(Main2Activity.this,
                                        Main3Activity.class);
                                intentMain.putExtra("serviceState", true);
                                Main2Activity.this.startActivity(intentMain);
                                Log.i("Content ", " Menu layout ");
                                finish();
                                dialog.dismiss();}

                        }else if(loc==0){
                            AppThemes a = new AppThemeProfile(getBaseContext()).get();
                            a.setName("News");
                            boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                            if(state){Toast.makeText(getBaseContext(),"News Mode",Toast.LENGTH_LONG).show();dialog.dismiss();}
                        }else if(loc==1){
                            AppThemes a = new AppThemeProfile(getBaseContext()).get();
                            a.setName("Panic");
                            boolean state  = new AppThemeProfile(getBaseContext()).update(a);
                            if(state){Toast.makeText(getBaseContext(),"Security Mode",Toast.LENGTH_LONG).show();
                                Intent intentMain = new Intent(Main2Activity.this,
                                        MainActivity.class);
                                intentMain.putExtra("serviceState", true);
                                Main2Activity.this.startActivity(intentMain);
                                Log.i("Content ", " Menu layout ");
                                finish();
                                dialog.dismiss();}
                        }
                    }
                });
        builderSingle.show();

    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    int lm = 0;
    int mt = 0;
    int nt = 0;

    public void startPayService() {
        startService(
                new Intent(getBaseContext(), PayService.class));
    }
    public void startNewsService() {
        startService(
                new Intent(getBaseContext(), NewsService.class));
    }

    public void startLogMovement() {

        if (lm == 0) {
            lm = 1;

            if (new SettingsProfile(getBaseContext()).get().getGoDark().equalsIgnoreCase("0")) {
                startService(
                        new Intent(getBaseContext(), LogMovementService.class));
            }
            startService(
                    new Intent(getBaseContext(), SimChangeService.class));
        } else {
            lm = 0;
            stopService(new Intent(this, LogMovementService.class));
        }

    }

    public void startMovementTracker() {


        if (mt == 0) {
            Toast.makeText(this, "Movement tracking activated (1 Hour)", Toast.LENGTH_LONG).show();
            startService(

                    new Intent(this, MovementService.class));
            mt = 1;
        } else {
            Toast.makeText(this, "Movement tracking Stopped", Toast.LENGTH_LONG).show();
            stopService(new Intent(this, MovementService.class));
            mt = 0;
        }
    }


    public void onItemClick(int mPosition) {

        News n = newsList.get(mPosition);
        Intent intentMain = new Intent(Main2Activity.this,
                NewsDisplayActivity.class);
        intentMain.putExtra("serviceState", true);
        intentMain.putExtra("news",n.getNews());
        intentMain.putExtra("subject",n.getSubject());
        intentMain.putExtra("image",n.getImage());
        intentMain.putExtra("author",n.getAuthor());
        intentMain.putExtra("source",n.getClient());
        intentMain.putExtra("bulletin",n.getBulletin());

        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String timeAgo="";
        try{

            timeAgo = new TimeDifference(new Date(), s.parse(n.getDate())).getDifferenceString();

        }catch (Exception e){
            e.printStackTrace();
        }

        intentMain.putExtra("timeAgo",timeAgo);
        Main2Activity.this.startActivity(intentMain);
        Log.i("Content ", " Menu layout ");
        finish();

    }


}
