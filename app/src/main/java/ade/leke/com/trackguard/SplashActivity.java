package ade.leke.com.trackguard;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;

public class SplashActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Panic);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //getSupportActionBar().hide();

        Typeface mgFont = Typeface.createFromAsset(getAssets(),"fonts/exo_medium.otf");
        final TextView text = (TextView) findViewById(R.id.lblLoadingProcess);
        text.setTypeface(mgFont);
        text.setText("Your Personal Panic Alert System");
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    //text.setText("Loading Settings");
                    //Thread.sleep(5000L);
                    //text.setText("Loading Mobile Guard Services");
                    //Thread.sleep(5000L);
                    text.setText("Checking Registration Status");
                    Thread.sleep(5000L);
                }catch (Exception e){
e.printStackTrace();
                }
                UserProfile x = new UserProfile(getBaseContext());
                if (x.getRegistration()) {
                    AppThemes t = new AppThemeProfile(getBaseContext()).get();
                    if(t.getName().equalsIgnoreCase("Panic")) {
                        Intent intentMain = new Intent(SplashActivity.this,
                                MainActivity.class);
                        intentMain.putExtra("serviceState", true);
                        SplashActivity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();
                    }else if(t.getName().equalsIgnoreCase("News")) {
                        Intent intentMain = new Intent(SplashActivity.this,
                                Main2Activity.class);
                        intentMain.putExtra("serviceState", true);
                        SplashActivity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();
                    } else if(t.getName().equalsIgnoreCase("Normal")) {
                        Intent intentMain = new Intent(SplashActivity.this,
                                Main3Activity.class);
                        intentMain.putExtra("serviceState", true);
                        SplashActivity.this.startActivity(intentMain);
                        Log.i("Content ", " Menu layout ");
                        finish();
                    }
                }else{

                    Intent intentMain = new Intent(SplashActivity.this,
                            RegistrationActivity.class);
                    //intentMain.putExtra("loc", "" + latitude + ":" + longitude);
                    SplashActivity.this.startActivity(intentMain);
                    SplashActivity.this.finish();
                }
            }
        }, 2500);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
