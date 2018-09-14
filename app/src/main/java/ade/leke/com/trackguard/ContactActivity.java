package ade.leke.com.trackguard;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;
import ade.leke.com.trackguard.model.ContactListModel;
import ade.leke.com.trackguard.model.CustomAdapter;

public class ContactActivity extends ActionBarActivity {

    ListView list;
    CustomAdapter adapter;
    public ContactActivity CustomListView = null;
    public ArrayList<ContactListModel> CustomListViewValuesArr = new ArrayList<ContactListModel>();
    FeedReaderDbHelper mDbHelper;// = new FeedReaderDbHelper(getBaseContext());
    Context context;
    int contact_count;
    ContactProfile profile;
    String selectContactMobile = "";
    String selcetContactName = "";
    CheckBox chkbxMG;

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
        setContentView(R.layout.activity_contact);
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

        SpannableString s = new SpannableString("Panic Contact");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);


        profile = new ContactProfile(getBaseContext());
        mDbHelper = new FeedReaderDbHelper(getBaseContext());
        CustomListView = this;

        context = this;
        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
        setListData();

        Resources res = getResources();
        list = (ListView) findViewById(R.id.list);  // List defined in XML ( See Below )

        /**************** Create Custom Adapter *********/
        adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
        list.setAdapter(adapter);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.

        contact_count = profile.contactCount();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact, menu);
        return true;
    }


    public void addContactManual() {


        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.contact_dialog);

        SpannableString s = new SpannableString("Add Contact");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        dialog.setTitle(s);

        dialog.setCancelable(true);
        // set the custom dialog components - text, image and button
        final EditText name = (EditText) dialog.findViewById(R.id.txtCName);
        final EditText phone = (EditText) dialog.findViewById(R.id.txtCPhone);

        chkbxMG = (CheckBox) dialog.findViewById(R.id.chkbxMG);
        Button saveContact = (Button) dialog.findViewById(R.id.butSaveContact);
        // if button is clicked, close the custom dialog
        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (!name.getText().toString().isEmpty()) {
                        if (!phone.getText().toString().isEmpty()) {
                            CustomListViewValuesArr.clear();

                            Contact contact = new Contact();
                            String firstname = name.getText().toString();
                            String surname = "None";
                            String[] names = name.getText().toString().split(" ");
                            if (names.length > 1) {

                                firstname = names[0];
                                surname = names[1];
                            }

                            contact.setFirstname(firstname);
                            contact.setLastname(surname);
                            contact.setPhoneNumber(phone.getText().toString());
                            contact.setContactId(0);
                            contact.setUid(UUID.randomUUID().toString());
                            if (chkbxMG.isChecked()) {
                                contact.setStatus("2");
                            } else {
                                contact.setStatus("1");
                            }
                            boolean state = profile.create(contact);
                            if (state) {
                                CustomListViewValuesArr.clear();
                                setListData();


                                Resources res = getResources();
                                list = (ListView) findViewById(R.id.list);  // List defined in XML ( See Below )

                                /**************** Create Custom Adapter *********/
                                adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
                                list.setAdapter(adapter);
                                Toast.makeText(context, "Panic Contact added successfully", Toast.LENGTH_LONG).show();

                                dialog.dismiss();
                            } else {
                                Toast.makeText(ContactActivity.this, "Mobile Number Field is empty", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ContactActivity.this, "Name field is empty", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ContactActivity.this, "Unable to create user", Toast.LENGTH_LONG).show();
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

    public void contactOptions() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.contact_options_dialog);

        SpannableString s = new SpannableString("Add Contact Options");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        dialog.setTitle(s);
        //dialog.getActionBar()..setStatusBarColor(getResources().getColor(R.color.ColorPrimaryDark));
        dialog.setCancelable(true);


        // set the custom dialog components - text, image and button
        final EditText name = (EditText) dialog.findViewById(R.id.txtCName);
        final EditText phone = (EditText) dialog.findViewById(R.id.txtCPhone);


        Button saveContact = (Button) dialog.findViewById(R.id.butFromContact);
        // if button is clicked, close the custom dialog
        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 100);

            }

        });

        Button cancel = (Button) dialog.findViewById(R.id.butManualContact);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addContactManual();

            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Uri contact = data.getData();
            ContentResolver cr = getContentResolver();

            Cursor c = managedQuery(contact, null, null, null, null);
            //      c.moveToFirst();

boolean ecaspe=true;
            while (c.moveToNext()) {
                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Contact contacts = new Contact();
                        String firstname = name;
                        String surname = "None";
                        String[] names = name.split(" ");
                        if (names.length == 2) {

                            firstname = names[0];
                            surname = names[1];
                        } else if (names.length > 2) {
                            firstname = names[0];
                            String temp = "";
                            for (int k = 1; k < names.length; k++) {

                                temp = temp + " " + names[k];
                            }
                            surname = temp;
                        }

                        contacts.setFirstname(firstname);
                        contacts.setLastname(surname);
                        contacts.setPhoneNumber(phone.replace("+234", "0").replaceAll(" ", ""));
                        contacts.setContactId(0);
                        contacts.setUid(UUID.randomUUID().toString());
                        contacts.setStatus("1'");
                        boolean state = false;
                        Contact contact1 = new ContactProfile(getBaseContext()).getByMobileNumber(phone.replace("+234", "0").replaceAll(" ", ""));
                        if(contact1==null){
                            state = profile.create(contacts);
                            CustomListViewValuesArr.clear();
                            setListData();


                            Resources res = getResources();
                            list = (ListView) findViewById(R.id.list);  // List defined in XML ( See Below )

                            /**************** Create Custom Adapter *********/
                            adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
                            list.setAdapter(adapter);
                            Toast.makeText(context, "Panic Contact added successfully", Toast.LENGTH_SHORT).show();
                            Toast.makeText(context,"Panic Contact added has Mobile Guard User",Toast.LENGTH_LONG).show();

                        }
                        if (state) {

ecaspe = false;
                        } else {
                            //Toast.makeText(ContactActivity.this, "Unable to create user", Toast.LENGTH_LONG).show();
                        }


                    }
                }

            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "No Contact Selected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.contact_adds) {

            if (profile.contactCount() != 4) {

                contactOptions();

            } else {
                Toast.makeText(this, "Panic Contact List Full", Toast.LENGTH_LONG).show();
            }

        } else if (id == android.R.id.home)

        {

            AppThemes t = new AppThemeProfile(getBaseContext()).get();
            if(t.getName().equalsIgnoreCase("Panic")) {
                Intent intentMain = new Intent(ContactActivity.this,
                        MainActivity.class);
                intentMain.putExtra("serviceState", true);
                ContactActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }else if(t.getName().equalsIgnoreCase("News")) {
                Intent intentMain = new Intent(ContactActivity.this,
                        Main2Activity.class);
                intentMain.putExtra("serviceState", true);
                ContactActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            } else if(t.getName().equalsIgnoreCase("Normal")) {
                Intent intentMain = new Intent(ContactActivity.this,
                        Main3Activity.class);
                intentMain.putExtra("serviceState", true);
                ContactActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }

        } else if (id == R.id.delete_contact) {
            if (selectContactMobile.isEmpty()) {
                Toast.makeText(this, "Select Contact", Toast.LENGTH_LONG).show();

            } else {
                boolean state = profile.delete(selectContactMobile);
                if (state) {
                    Toast.makeText(this,
                            "" + selcetContactName + " deleted successfully"
                            ,
                            Toast.LENGTH_LONG)
                            .show();

                    CustomListViewValuesArr.clear();
                    setListData();
                    Resources res = getResources();
                    list = (ListView) findViewById(R.id.list);  // List defined in XML ( See Below )

                    /**************** Create Custom Adapter *********/
                    adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
                    list.setAdapter(adapter);
                }
            }


        } else if (id == R.id.edit_contact) {
            if (selectContactMobile.isEmpty()) {
                Toast.makeText(this, "Select contact", Toast.LENGTH_LONG).show();
            } else {
                final Dialog dialog = new Dialog(ContactActivity.this);
                dialog.setContentView(R.layout.contact_dialog);
                SpannableString s = new SpannableString("Edit Contact");
                s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

                dialog.setTitle(s);
                chkbxMG = (CheckBox) dialog.findViewById(R.id.chkbxMG);
                final Contact contact = profile.getByMobileNumber(selectContactMobile);
                // set the custom dialog components - text, image and button
                final EditText name = (EditText) dialog.findViewById(R.id.txtCName);
                final EditText phone = (EditText) dialog.findViewById(R.id.txtCPhone);
                if (contact != null) {
                    name.setText(contact.getFirstname() + " " + contact.getLastname());
                    phone.setText(contact.getPhoneNumber());
                    if (contact.getStatus().equalsIgnoreCase("2")) {
                        chkbxMG.setChecked(true);
                    }
                }
                Button saveContact = (Button) dialog.findViewById(R.id.butSaveContact);
                saveContact.setText("Update");
                // if button is clicked, close the custom dialog
                saveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {

                            if (contact != null) {
                                if (!name.getText().toString().isEmpty()) {
                                    if (!phone.getText().toString().isEmpty()) {
                                        String[] names = name.getText().toString().split(" ");
                                        String firstname = name.getText().toString();
                                        String lastname = "None";
                                        if (names.length > 1) {

                                            firstname = names[0];
                                            lastname = names[1];
                                        }
                                        contact.setFirstname(firstname);
                                        contact.setLastname(lastname);
                                        if (chkbxMG.isChecked()) {
                                            contact.setStatus("2");
                                        } else {
                                            contact.setStatus("1");
                                        }
                                        boolean state = profile.update(contact);
                                        SpannableString s2 = new SpannableString(name.getText().toString());
                                        s2.setSpan(new TypefaceSpan(ContactActivity.this, "exo_medium.otf"), 0, s2.length(),
                                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
                                        ContactActivity.this.setTitle(s2);
                                        if (state) {

                                            Toast.makeText(ContactActivity.this, "Panic Contact updated successfully", Toast.LENGTH_LONG).show();

                                            CustomListViewValuesArr.clear();
                                            dialog.dismiss();
                                            setListData();


                                            Resources res = getResources();
                                            list = (ListView) findViewById(R.id.list);  // List defined in XML ( See Below )

                                            /**************** Create Custom Adapter *********/
                                            adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
                                            list.setAdapter(adapter);

                                        }
                                    } else {
                                        Toast.makeText(getBaseContext(), "Mobile Number field Empty", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Name field Empty", Toast.LENGTH_LONG).show();
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
        } else if (id == R.id.contact_request) {

            if (selectContactMobile.isEmpty()) {
                Toast.makeText(this, "Select Contact", Toast.LENGTH_LONG).show();
            } else {

                try {
                    sendSmsByManager(selectContactMobile);
                } catch (Exception e) {
                    Toast.makeText(this, "Select Contact", Toast.LENGTH_LONG).show();
                }
            }

        }else if (id == R.id.find_phone) {

            if (selectContactMobile.isEmpty()) {
                Toast.makeText(this, "Select Panic Contact", Toast.LENGTH_LONG).show();
            } else {

                try {
                    sendSmsByManager2(selectContactMobile);
                } catch (Exception e) {
                    Toast.makeText(this, "Select Contact", Toast.LENGTH_LONG).show();
                }
            }

        }



        return super.

                onOptionsItemSelected(item);
    }

    public void setListData() {

        try {

            List<Contact> contactList = profile.getAllContacts();
            if (!contactList.isEmpty()) {
                for (Contact c : contactList) {
                    final ContactListModel sched = new ContactListModel();

                    String lastname = c.getLastname();
                    if (lastname.equalsIgnoreCase("None")) {
                        lastname = "";
                    }
                    /******* Firstly take data in model object ******/
                    sched.setName(c.getFirstname() + " " + lastname);
                    sched.setImage("image");
                    sched.setMobileNumber(c.getPhoneNumber());

                    /******** Take Model Object in ArrayList **********/
                    CustomListViewValuesArr.add(sched);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }


    }

    /*****************
     * This function used by adapter
     ****************/
    public void onItemClick(int mPosition) {
        ContactListModel tempValues = (ContactListModel) CustomListViewValuesArr.get(mPosition);
        //ArrayList<ContactListModel> arrayList = cust
        //CustomListViewValuesArr.clear();

        ContactListModel data = tempValues;
        if(!tempValues.getImage().equalsIgnoreCase("B")) {
            CustomListViewValuesArr.clear();
            setListData();

            Resources res = getResources();
            list = (ListView) findViewById(R.id.list);  // List defined in XML ( See Below )

            /**************** Create Custom Adapter *********/
            adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
            list.setAdapter(adapter);

            tempValues.setImage("B");
            CustomListViewValuesArr.set(mPosition, tempValues);


            /**************** Create Custom Adapter *********/
            adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
            list.setAdapter(adapter);
       /*
        Intent intentMain = new Intent(ContactActivity.this,
                ContactDisplayActivity.class);
        intentMain.putExtra("id",tempValues.getMobileNumber());
        intentMain.putExtra("name",tempValues.getName());
        intentMain.putExtra("lat","0.0");
        intentMain.putExtra("lng","0.0");
        ContactActivity.this.startActivity(intentMain);*/


            selcetContactName = tempValues.getName();
            selectContactMobile = tempValues.getMobileNumber();

        }else{
            tempValues.setImage("G");
            CustomListViewValuesArr.set(mPosition, tempValues);
            Resources res = getResources();
            list = (ListView) findViewById(R.id.list);  // List defined in XML ( See Below )

            /**************** Create Custom Adapter *********/
            adapter = new CustomAdapter(CustomListView, CustomListViewValuesArr, res);
            list.setAdapter(adapter);
       /*
        Intent intentMain = new Intent(ContactActivity.this,
                ContactDisplayActivity.class);
        intentMain.putExtra("id",tempValues.getMobileNumber());
        intentMain.putExtra("name",tempValues.getName());
        intentMain.putExtra("lat","0.0");
        intentMain.putExtra("lng","0.0");
        ContactActivity.this.startActivity(intentMain);*/


            selcetContactName ="";
            selectContactMobile = "";
        }

    }

    public void sendSmsByManager(String phoneNumber) {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();


            smsManager.sendTextMessage(phoneNumber,
                    null,
                    "Mgloc:lr",
                    null,
                    null);
            Log.d("Send SMS", "Sent msg to " + phoneNumber);
            Toast.makeText(getBaseContext(), "Location Request Sent", Toast.LENGTH_LONG).show();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
    public void sendSmsByManager2(String phoneNumber) {
        try {
            // Get the default instance of the SmsManager
            SmsManager smsManager = SmsManager.getDefault();


            smsManager.sendTextMessage(phoneNumber,
                    null,
                    "MGact "+phoneNumber,
                    null,
                    null);
            Log.d("Send SMS", "Sent msg to " + phoneNumber);
            Toast.makeText(getBaseContext(), "Missing Phone Alert Sent", Toast.LENGTH_LONG).show();

        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }
}
