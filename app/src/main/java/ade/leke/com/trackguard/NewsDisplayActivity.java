package ade.leke.com.trackguard;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.model.MovementMainAdapter;
import ade.leke.com.trackguard.model.MovementMainListModel;

public class NewsDisplayActivity extends ActionBarActivity {

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
        setContentView(R.layout.activity_news_display);

        Intent i = getIntent();
        String subject = i.getStringExtra("subject");
        String news = i.getStringExtra("news");
        String image = i.getStringExtra("image");
        String author = i.getStringExtra("author");
        String source = i.getStringExtra("source");
        String bulletin = i.getStringExtra("bulletin");
        String timeAgo = i.getStringExtra("timeAgo");
        SpannableString s = new SpannableString("News Display");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView lblSubject = (TextView)findViewById(R.id.lblSubject);
        TextView lblNews = (TextView)findViewById(R.id.lblNews);
        TextView lblAuthor = (TextView)findViewById(R.id.lblSourceAuthor);
        TextView lblTimeAgo = (TextView)findViewById(R.id.lblTimeAgo);
        TextView lblBulletin = (TextView)findViewById(R.id.lblBulletin);
        ImageView imageNews = (ImageView)findViewById(R.id.imageNews);
        lblSubject.setText(subject);
        lblNews.setText(news);
        lblTimeAgo.setText(timeAgo);
        lblBulletin.setText(Html.fromHtml(bulletin));
        lblAuthor.setText(source + " | " + author);
        byte[] img = Base64.decode(image,Base64.DEFAULT);
        imageNews.setImageBitmap(BitmapFactory.decodeByteArray(img,0,img.length));




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_news_display, menu);
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
            return true;
        }else if (id == android.R.id.home)

        {
            AppThemes t = new AppThemeProfile(getBaseContext()).get();
            if(t.getName().equalsIgnoreCase("Panic")) {
                Intent intentMain = new Intent(NewsDisplayActivity.this,
                        MainActivity.class);
                intentMain.putExtra("serviceState", true);
                NewsDisplayActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }else if(t.getName().equalsIgnoreCase("News")) {
                Intent intentMain = new Intent(NewsDisplayActivity.this,
                        Main2Activity.class);
                intentMain.putExtra("serviceState", true);
                NewsDisplayActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            } else if(t.getName().equalsIgnoreCase("Normal")) {
                Intent intentMain = new Intent(NewsDisplayActivity.this,
                        Main3Activity.class);
                intentMain.putExtra("serviceState", true);
                NewsDisplayActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }
        }


        return super.onOptionsItemSelected(item);
    }
}
