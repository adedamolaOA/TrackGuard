package ade.leke.com.trackguard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;

public class MoreInformationActivity extends ActionBarActivity {

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
        setContentView(R.layout.activity_more_information);
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);


        SpannableString s = new SpannableString("About Us");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        Typeface mgFont = Typeface.createFromAsset(getAssets(),"fonts/exo_medium.otf");
        TextView lblAboutUsTxt1 = (TextView) findViewById(R.id.lblAboutUsTxt1);
        TextView lblAboutUsTxt2 = (TextView) findViewById(R.id.lblAboutUsTxt2);
       // TextView lblAboutUsTitle = (TextView) findViewById(R.id.lblAboutUsTitle);
        //lblAboutUsTitle.setTypeface(mgFont);
        lblAboutUsTxt1.setTypeface(mgFont);
        lblAboutUsTxt2.setTypeface(mgFont);
        Button terms  = (Button) findViewById(R.id.butTerms);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MoreInformationActivity.this);
                dialog.setContentView(R.layout.terms_and_conditions_layout);

                dialog.setCancelable(true);
                SpannableString s = new SpannableString("TERMS AND CONDITIONS");
                s.setSpan(new TypefaceSpan(MoreInformationActivity.this, "exo_medium.otf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

                dialog.setTitle(s);
                dialog.setCancelable(true);




                Button saveContact = (Button) dialog.findViewById(R.id.butIAgree);
                saveContact.setText("OK");
                //saveContact.setVisibility(View.INVISIBLE);
                // if button is clicked, close the custom dialog
                saveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        dialog.dismiss();

                    }

                });

                Button cancel = (Button) dialog.findViewById(R.id.butIDisagree);
                cancel.setVisibility(View.INVISIBLE);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });
                dialog.show();
            }
        });
        //Button contactUs = (Button) findViewById(R.id.butContactUss);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_information, menu);
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
                Intent intentMain = new Intent(MoreInformationActivity.this,
                        MainActivity.class);
                intentMain.putExtra("serviceState", true);
                MoreInformationActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }else if(t.getName().equalsIgnoreCase("News")) {
                Intent intentMain = new Intent(MoreInformationActivity.this,
                        Main2Activity.class);
                intentMain.putExtra("serviceState", true);
                MoreInformationActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            } else if(t.getName().equalsIgnoreCase("Normal")) {
                Intent intentMain = new Intent(MoreInformationActivity.this,
                        Main3Activity.class);
                intentMain.putExtra("serviceState", true);
                MoreInformationActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }
        }


        return super.onOptionsItemSelected(item);
    }
}
