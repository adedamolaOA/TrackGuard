package ade.leke.com.trackguard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import ade.leke.com.trackguard.common.SoftKeyboard;
import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Reg;
import ade.leke.com.trackguard.db.db.entities.Settings;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.RegistrationDate;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;

public class RegistrationActivity extends ActionBarActivity {

    UserProfile userProfile;
    EditText txtFirstname ;
    EditText txtOthernames;
    EditText txtMobileNumber;
    EditText txtEmail;
    EditText txtPassCode;
    CheckBox termsAndConditions;
    SoftKeyboard softKeyboard;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        SpannableString s = new SpannableString("Mobile Guard");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        userProfile = new UserProfile(getBaseContext());
        txtFirstname = (EditText) findViewById(R.id.txtRegFirstname);
        txtOthernames = (EditText) findViewById(R.id.txtRegOthernames);
        txtMobileNumber = (EditText) findViewById(R.id.txtRegMobile);
        txtEmail = (EditText) findViewById(R.id.txtRegEmail);
        txtPassCode = (EditText) findViewById(R.id.editTextAccessCode);

        termsAndConditions = (CheckBox) findViewById(R.id.chkbxTerms);

        TextView regMsg = (TextView) findViewById(R.id.lblRegMsg);
        //Sign Up for an account with Mobile Guard to able you enjoy the features.
        SpannableString ss = new SpannableString("Sign Up for an account with Mobile Guard to able you enjoy the features.");
        ss.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, ss.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        regMsg.setText(ss);
        Button regBut = (Button) findViewById(R.id.butRegister);
        regBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Toast.makeText(RegistrationActivity.this,termsAndConditions.isChecked()+"",Toast.LENGTH_LONG).show();
               if(termsAndConditions.isChecked()){
                   if(!txtFirstname.getText().toString().isEmpty()&&!txtOthernames.getText().toString().isEmpty()&&!txtEmail.getText().toString().isEmpty()
                   &&!txtMobileNumber.getText().toString().isEmpty()&&!txtPassCode.getText().toString().isEmpty()){
                       if(txtEmail.getText().toString().contains("@")
                               &&(txtEmail.getText().toString().contains(".com")||txtEmail.getText().toString().contains(".co")||
                               txtEmail.getText().toString().contains(".co.uk")||txtEmail.getText().toString().contains(".gov.ng"))) {
                           User user = new User();

                           TelephonyManager telephoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                           String sim = telephoneMgr.getSimSerialNumber();//getLine1Number();
                           user.setSIM(sim);
                           user.setStatus("1");
                           user.setLastname(txtOthernames.getText().toString());
                           user.setMobileNumber(txtMobileNumber.getText().toString());
                           user.setEmail(txtEmail.getText().toString());
                           user.setFirstname(txtFirstname.getText().toString());
                           user.setId(0);
                           user.setVersion("1");
                           user.setPassCode(txtPassCode.getText().toString());

                           boolean state = userProfile.create(user);
                           if (state) {
                               Settings s = new Settings();
                               s.setmInterval("600000");//5min --> 300000,10min --> 600000 milliseconds,15min --> 900000 milliseconds,20min -->1200000 milliseconds , 30min -->1800000 milliseconds, 1hr --> 3600000 milliseconds
                               s.setNumberOfSMS("10");
                               s.setpInterval("600000");
                               s.setSettingsStatus("1");
                               s.setUid(UUID.randomUUID().toString());
                               s.setGoDark("0");
                               s.setMGS("1");
                               s.setSid(0);
                               s.setSMS("1");

                               state = new SettingsProfile(RegistrationActivity.this).create(s);
                               if (state) {

                                   Reg r = new Reg();
                                   r.setRegId(0);
                                   r.setRegDate(new Date());
                                   r.setIsUpdated("0");
                                   r.setUpdateDate(new Date());
                                   r.setOwing("0");
                                   r.setPayService("1");
                                   state = new RegistrationDate(getBaseContext()).create(r);
                                   if(state) {
                                       AppThemes app = new AppThemes();
                                       app.setPrimaryColorDark("#35478C");
                                       app.setPrimaryColor("#4E7AC7");
                                       app.setBackground("#FFFFFF");
                                       app.setName("News");
                                       app.setId(0);
                                       app.setStatus("1");
                                       //sendSmsByManager();
                                       state = new AppThemeProfile(getBaseContext()).create(app);
                                       if(state) {
                                           AppThemes t = new AppThemeProfile(getBaseContext()).get();
                                           Intent intentMain = new Intent(RegistrationActivity.this,
                                                   MainActivity.class);
                                           intentMain.putExtra("serviceState", true);
                                           RegistrationActivity.this.startActivity(intentMain);
                                           Log.i("Content ", " Menu layout ");
                                           finish();
                                           /*if(t.getName().equalsIgnoreCase("Panic")) {
                                               Intent intentMain = new Intent(RegistrationActivity.this,
                                                       MainActivity.class);
                                               intentMain.putExtra("serviceState", true);
                                               RegistrationActivity.this.startActivity(intentMain);
                                               Log.i("Content ", " Menu layout ");
            startActivity(new Intent(getBaseContext(),DasboardNavActivity.class));                                    finish();
                                           }else if(t.getName().equalsIgnoreCase("News")) {
                                               Intent intentMain = new Intent(RegistrationActivity.this,
                                                       Main2Activity.class);
                                               intentMain.putExtra("serviceState", true);
                                               RegistrationActivity.this.startActivity(intentMain);
                                               Log.i("Content ", " Menu layout ");
                                               finish();
                                           } else if(t.getName().equalsIgnoreCase("Normal")) {
                                               Intent intentMain = new Intent(RegistrationActivity.this,
                                                       Main3Activity.class);
                                               intentMain.putExtra("serviceState", true);
                                               RegistrationActivity.this.startActivity(intentMain);
                                               Log.i("Content ", " Menu layout ");
                                               finish();
                                           }*/
                                           Toast.makeText(RegistrationActivity.this, "Sign up completed", Toast.LENGTH_LONG).show();
                                       }
                                       }
                               }
                           } else {
                               Toast.makeText(RegistrationActivity.this, "Unable to complete sign up", Toast.LENGTH_LONG).show();
                           }
                       }else {
                           Toast.makeText(RegistrationActivity.this, "Invaid Email Address", Toast.LENGTH_LONG).show();
                       }
                   }else {
                       Toast.makeText(RegistrationActivity.this, "One or more Empty fields", Toast.LENGTH_LONG).show();
                   }
               }else {
                   Toast.makeText(RegistrationActivity.this, "Terms and Condition not agreed", Toast.LENGTH_LONG).show();
               }


            }
        });

        Button terms = (Button) findViewById(R.id.butTermsAndConditions);

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(RegistrationActivity.this);
                dialog.setContentView(R.layout.terms_and_conditions_layout);

                SpannableString s = new SpannableString("TERMS AND CONDITIONS");
                s.setSpan(new TypefaceSpan(RegistrationActivity.this, "exo_medium.otf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

                dialog.setTitle(s);
                dialog.setCancelable(true);



                Button saveContact = (Button) dialog.findViewById(R.id.butIAgree);
                // if button is clicked, close the custom dialog
                saveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        termsAndConditions.setChecked(true);
                        dialog.dismiss();

                    }

                });

                Button cancel = (Button) dialog.findViewById(R.id.butIDisagree);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });
                dialog.show();
            }
        });


    }

    /*public void sendSmsByManager() {
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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
