package ade.leke.com.trackguard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.Settings;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.MovementProfile;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;
import ade.leke.com.trackguard.services.LogMovementService;
import ade.leke.com.trackguard.services.PayService;
import ade.leke.com.trackguard.services.SimChangeService;

public class MgSettingsActivity extends ActionBarActivity {

    ArrayAdapter<CharSequence> adapter;
    ArrayAdapter<CharSequence> movementAdapter;
    Spinner panicInterval;
    Spinner movementInterval;
    Switch mgsSwitch;
    Switch smsSwitch;
    Switch goDarkSwitch;
    Switch goSIMswitch;
    Switch turnOffPhoneAlert;

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
        setContentView(R.layout.activity_mg_settings);
        SpannableString s = new SpannableString("Settings");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        mgsSwitch = (Switch) findViewById(R.id.mgsSwitch);
        //smsSwitch = (Switch) findViewById(R.id.smsSwitch);
        goDarkSwitch = (Switch) findViewById(R.id.goDarkSwitch);
        goSIMswitch = (Switch) findViewById(R.id.goSIMswitch);
        panicInterval = (Spinner) findViewById(R.id.panic_spinner);
        turnOffPhoneAlert = (Switch) findViewById(R.id.switchTurnOff);
        TextView lblPAI = (TextView)findViewById(R.id.lblPAI);
        TextView lblMLI = (TextView)findViewById(R.id.lblMLI);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.panic_interval, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        panicInterval.setAdapter(adapter);

        movementInterval = (Spinner) findViewById(R.id.movement_spinner);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.movement_interval, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        movementInterval.setAdapter(adapter);
        Typeface mgFont = Typeface.createFromAsset(getAssets(),"fonts/exo_medium.otf");
        goDarkSwitch.setTypeface(mgFont);
        mgsSwitch.setTypeface(mgFont);
        //smsSwitch.setTypeface(mgFont);
        goSIMswitch.setTypeface(mgFont);
        turnOffPhoneAlert.setTypeface(mgFont);
        lblMLI.setTypeface(mgFont);
        lblPAI.setTypeface(mgFont);

        Settings settings = new SettingsProfile(getBaseContext()).get();
        if(settings.getGoDark().equalsIgnoreCase("1")) {
            goDarkSwitch.setChecked(true);
        }else{
            goDarkSwitch.setChecked(false);
        }

        if(settings.getMGS().equalsIgnoreCase("1")){
            mgsSwitch.setChecked(true);
        }else{
            mgsSwitch.setChecked(false);
        }

        if(settings.getSMS().equalsIgnoreCase("1")){
            turnOffPhoneAlert.setChecked(false);
        }else{
            turnOffPhoneAlert.setChecked(true);
        }


        TelephonyManager telephoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
try {
    User user = new UserProfile(getBaseContext()).get();
    if (user.getSim().equalsIgnoreCase(telephoneMgr.getSimSerialNumber())) {
        goSIMswitch.setChecked(true);
    } else {
        goSIMswitch.setChecked(false);
    }
}catch (Exception e){
    e.printStackTrace();
}
        String selectedInterval = settings.getpInterval();
        String interval = "600000";
        if (selectedInterval.equalsIgnoreCase("300000")) {
            panicInterval.setSelection(0);

        } else if (selectedInterval.equalsIgnoreCase("600000")) {
            panicInterval.setSelection(1);

        } else if (selectedInterval.equalsIgnoreCase("900000")) {
            panicInterval.setSelection(2);
        } else if (selectedInterval.equalsIgnoreCase("1200000")) {
            panicInterval.setSelection(3);

        } else if (selectedInterval.equalsIgnoreCase("1800000")) {
            panicInterval.setSelection(4);

        }else
        if (selectedInterval.equalsIgnoreCase("3600000")) {
            panicInterval.setSelection(5);

        }

        selectedInterval = settings.getmInterval();

        if (selectedInterval.equalsIgnoreCase("300000")) {
            movementInterval.setSelection(0);

        } else if (selectedInterval.equalsIgnoreCase("600000")) {
            movementInterval.setSelection(1);

        } else if (selectedInterval.equalsIgnoreCase("900000")) {
            movementInterval.setSelection(2);

        } else if (selectedInterval.equalsIgnoreCase("1200000")) {
            movementInterval.setSelection(3);

        } else if (selectedInterval.equalsIgnoreCase("1800000")) {
            movementInterval.setSelection(4);

        }else
        if (selectedInterval.equalsIgnoreCase("3600000")) {
            movementInterval.setSelection(5);

        }
        ////5min --> 300000,10min --> 600000 milliseconds,15min --> 900000 milliseconds,20min -->1200000 milliseconds , 30min -->1800000 milliseconds, 1hr --> 3600000 milliseconds
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
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);



        mgsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mgsSwitch.isChecked()) {
                    AlertDialog.Builder builderInner = new AlertDialog.Builder(
                            MgSettingsActivity.this);

                    SpannableString s = new SpannableString("Mobile Guard Services");
                    s.setSpan(new TypefaceSpan(MgSettingsActivity.this, "exo_medium.otf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builderInner.setTitle(s);
                    s = new SpannableString("You are about to turn ON the Mobile Guard Service, switching On this service will enable all functions available on the current version of Mobile Guard.\nService Charges will be removed from Your Mobile mobile each month \nDo you wish to continue?");
                    s.setSpan(new TypefaceSpan(MgSettingsActivity.this, "exo_medium.otf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builderInner.setMessage(s);
                    builderInner.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    Settings settings = new SettingsProfile(MgSettingsActivity.this).get();
                                    settings.setMGS("1");
                                    boolean update = new SettingsProfile(MgSettingsActivity.this).update(settings);
                                    if (update) {
                                        startService(
                                                new Intent(getBaseContext(), PayService.class));
                                        startService(
                                                new Intent(getBaseContext(), LogMovementService.class));

                                        startService(
                                                new Intent(getBaseContext(), SimChangeService.class));
                                        //sendSmsByManager();
                                        Toast.makeText(MgSettingsActivity.this, "Mobile Guard Service ON", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }

                                }
                            });
                    builderInner.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                }
                            });
                    builderInner.show();


                } else {

                    AlertDialog.Builder builderInner = new AlertDialog.Builder(
                            MgSettingsActivity.this);

                    SpannableString s = new SpannableString("Mobile Guard Services");
                    s.setSpan(new TypefaceSpan(MgSettingsActivity.this, "exo_medium.otf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builderInner.setTitle(s);
                    s = new SpannableString("You are about to turn off the Mobile Guard Service, turning off this service will disable some of the functions of Mobile Guard.\nDo you wish to continue?");
                    s.setSpan(new TypefaceSpan(MgSettingsActivity.this, "exo_medium.otf"), 0, s.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    builderInner.setMessage(s);
                    builderInner.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    Settings settings = new SettingsProfile(MgSettingsActivity.this).get();
                                    settings.setMGS("0");
                                    boolean update = new SettingsProfile(MgSettingsActivity.this).update(settings);
                                    if (update) {
                                        stopService(
                                                new Intent(getBaseContext(), PayService.class));
                                        stopService(
                                                new Intent(getBaseContext(), LogMovementService.class));

                                        stopService(
                                                new Intent(getBaseContext(), SimChangeService.class));
                                        Toast.makeText(MgSettingsActivity.this, "Mobile Guard Service OFF", Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }

                                }
                            });
                    builderInner.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    dialog.dismiss();
                                }
                            });
                    builderInner.show();

                }

            }
        });

        turnOffPhoneAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Dialog dialog = new Dialog(MgSettingsActivity.this);
                dialog.setContentView(R.layout.passcode_dialog);

                SpannableString s = new SpannableString("Access Authorization");
                s.setSpan(new TypefaceSpan(MgSettingsActivity.this, "exo_medium.otf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

                dialog.setTitle(s);
                dialog.setCancelable(true);


                // set the custom dialog components - text, image and button
                final EditText name = (EditText) dialog.findViewById(R.id.txtDialogAccessCode);



                Button saveContact = (Button) dialog.findViewById(R.id.butAuthorize);
                // if button is clicked, close the custom dialog
                saveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User user = new UserProfile(getBaseContext()).get();
                        if(name.getText().toString().equalsIgnoreCase(user.getPassCode())) {

                            if (turnOffPhoneAlert.isChecked()) {
                                Settings settings = new SettingsProfile(MgSettingsActivity.this).get();
                                settings.setSMS("0");
                                boolean update = new SettingsProfile(MgSettingsActivity.this).update(settings);
                                if (update) {
                                    Toast.makeText(MgSettingsActivity.this, "Turn Off Phone Alert is On", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Settings settings = new SettingsProfile(MgSettingsActivity.this).get();
                                settings.setSMS("1");
                                boolean update = new SettingsProfile(MgSettingsActivity.this).update(settings);
                                if (update) {
                                    Toast.makeText(MgSettingsActivity.this, "Turn Off Phone Alert is OFF", Toast.LENGTH_LONG).show();
                                }
                            }
                        }else{
                            Toast.makeText(MgSettingsActivity.this,"Incorrect Access Code",Toast.LENGTH_LONG).show();
                        }

                    }

                });

                dialog.show();



            }
        });
        goSIMswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                final Dialog dialog = new Dialog(MgSettingsActivity.this);
                dialog.setContentView(R.layout.passcode_dialog);

                SpannableString s = new SpannableString("Access Authorization");
                s.setSpan(new TypefaceSpan(MgSettingsActivity.this, "exo_medium.otf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

                dialog.setTitle(s);
                dialog.setCancelable(true);


                // set the custom dialog components - text, image and button
                final EditText name = (EditText) dialog.findViewById(R.id.txtDialogAccessCode);



                Button saveContact = (Button) dialog.findViewById(R.id.butAuthorize);
                // if button is clicked, close the custom dialog
                saveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        User user = new UserProfile(getBaseContext()).get();
                        if (name.getText().toString().equalsIgnoreCase(user.getPassCode())) {
                            if (goSIMswitch.isChecked()) {
                                User users = new UserProfile(getBaseContext()).get();
                                TelephonyManager telephoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                                users.setSIM(telephoneMgr.getSimSerialNumber());
                                boolean update = new UserProfile(getBaseContext()).update(users);
                                if (update) {
                                    Toast.makeText(MgSettingsActivity.this, "SIM State Updated", Toast.LENGTH_LONG).show();
                                    stopService(new Intent(MgSettingsActivity.this, SimChangeService.class));
                                    dialog.dismiss();
                                }
                            } else {
                                //Settings settings = new SettingsProfile(MgSettingsActivity.this).get();
                                //settings.setSMS("0");
                                //boolean update = new SettingsProfile(MgSettingsActivity.this).update(settings);
                                //goSIMswitch.setChecked(true);

                            }
                        }else{
                            Toast.makeText(MgSettingsActivity.this,"Incorrect Access Code",Toast.LENGTH_LONG).show();
                            goSIMswitch.setChecked(false);
                            dialog.dismiss();
                        }

                        }

                    }

                    );

                    dialog.show();


                }
            });

        goDarkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (goDarkSwitch.isChecked()) {
                    Settings settings = new SettingsProfile(MgSettingsActivity.this).get();
                    settings.setGoDark("1");
                    boolean update = new SettingsProfile(MgSettingsActivity.this).update(settings);
                    if (update) {
                        stopService(new Intent(getBaseContext(), LogMovementService.class));
                        Toast.makeText(MgSettingsActivity.this, "Movement Tracking OFF", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Settings settings = new SettingsProfile(MgSettingsActivity.this).get();
                    settings.setGoDark("0");
                    boolean update = new SettingsProfile(MgSettingsActivity.this).update(settings);
                    if (update) {
                        startService(new Intent(getBaseContext(), LogMovementService.class));
                        Toast.makeText(MgSettingsActivity.this, "Movement Tracking ON", Toast.LENGTH_LONG).show();
                    }

                }


            }
        });


        panicInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedInterval = panicInterval.getSelectedItem().toString();
                String interval = "600000";
                if (selectedInterval.equalsIgnoreCase("5 Minutes")) {
                    interval = "300000";
                } else if (selectedInterval.equalsIgnoreCase("10 Minutes")) {
                    interval = "600000";
                } else if (selectedInterval.equalsIgnoreCase("15 Minutes")) {
                    interval = "900000";
                } else if (selectedInterval.equalsIgnoreCase("20 Minutes")) {
                    interval = "1200000";
                } else if (selectedInterval.equalsIgnoreCase("30 Minutes")) {
                    interval = "1800000";
                }else
                if (selectedInterval.equalsIgnoreCase("1 Hour")) {
                    interval = "3600000";
                }
                Settings settings = new SettingsProfile(MgSettingsActivity.this).get();
                settings.setpInterval(interval);
                boolean update = new SettingsProfile(MgSettingsActivity.this).update(settings);
                if (update) {
                    Toast.makeText(MgSettingsActivity.this, "Panic Interval Updated", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        movementInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedInterval = movementInterval.getSelectedItem().toString();
                String interval = "600000";
                if (selectedInterval.equalsIgnoreCase("5 Minutes")) {
                    interval = "300000";
                } else if (selectedInterval.equalsIgnoreCase("10 Minutes")) {
                    interval = "600000";
                } else if (selectedInterval.equalsIgnoreCase("15 Minutes")) {
                    interval = "900000";
                } else if (selectedInterval.equalsIgnoreCase("20 Minutes")) {
                    interval = "1200000";
                } else if (selectedInterval.equalsIgnoreCase("30 Minutes")) {
                    interval = "1800000";
                }
                if (selectedInterval.equalsIgnoreCase("1 Hour")) {
                    interval = "3600000";
                }
                Settings settings = new SettingsProfile(MgSettingsActivity.this).get();
                settings.setmInterval(interval);
                boolean update = new SettingsProfile(MgSettingsActivity.this).update(settings);
                if (update) {
                    Toast.makeText(MgSettingsActivity.this, "Movement Interval Updated", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

   /* public void sendSmsByManager() {
        try {
            String shortCode ="32811";
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(shortCode,
                    null,
                    "mgloc reg",
                    null,
                    null);

            Log.d("Send SMS", "Sent Location");


        } catch (Exception ex) {
            Log.d("Mobile Guard", "Sending Failed");
        }
    }*/
    public void sendSmsByManager(String shortCode) {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();


            smsManager.sendTextMessage(shortCode,
                    null,
                    "mgloc",
                    null,
                    null);
            Log.d("Send SMS", "Sent msg to " + shortCode);


        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mg_settings, menu);
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
        } else if (id == android.R.id.home)

        {
            AppThemes t = new AppThemeProfile(getBaseContext()).get();
            if(t.getName().equalsIgnoreCase("Panic")) {
                Intent intentMain = new Intent(MgSettingsActivity.this,
                        MainActivity.class);
                intentMain.putExtra("serviceState", true);
                MgSettingsActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }else if(t.getName().equalsIgnoreCase("News")) {
                Intent intentMain = new Intent(MgSettingsActivity.this,
                        Main2Activity.class);
                intentMain.putExtra("serviceState", true);
                MgSettingsActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            } else if(t.getName().equalsIgnoreCase("Normal")) {
                Intent intentMain = new Intent(MgSettingsActivity.this,
                        Main3Activity.class);
                intentMain.putExtra("serviceState", true);
                MgSettingsActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
