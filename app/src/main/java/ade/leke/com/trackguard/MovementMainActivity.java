package ade.leke.com.trackguard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Movement;
import ade.leke.com.trackguard.db.db.entities.Reg;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.MovementProfile;
import ade.leke.com.trackguard.db.db.entities.profile.RegistrationDate;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;
import ade.leke.com.trackguard.model.MovementAdapter;
import ade.leke.com.trackguard.model.MovementListModel;
import ade.leke.com.trackguard.model.MovementMainAdapter;
import ade.leke.com.trackguard.model.MovementMainListModel;

public class MovementMainActivity extends ActionBarActivity {

    ListView list;
    MovementMainAdapter adapter;
    public MovementMainActivity CustomListView = null;
    public ArrayList<MovementMainListModel> CustomListViewValuesArr = new ArrayList<MovementMainListModel>();
    FeedReaderDbHelper mDbHelper;// = new FeedReaderDbHelper(getBaseContext());
    Context context;
    int contact_count;
    ActionBar actionBar;

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
        setContentView(R.layout.activity_movement_main);
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
        SpannableString s = new SpannableString("My Movement");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
        actionBar = getSupportActionBar();
        actionBar.setTitle(s);

        mDbHelper = new FeedReaderDbHelper(getBaseContext());
        CustomListView = this;

        context = this;
        /******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/

        loadMovementData();

        Resources res = getResources();
        list = (ListView) findViewById(R.id.list_movement_main);  // List defined in XML ( See Below )

        /**************** Create Custom Adapter *********/
        adapter = new MovementMainAdapter(CustomListView, CustomListViewValuesArr, res);
        list.setAdapter(adapter);
        list.setLongClickable(true);
        registerForContextMenu(list);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //new LongRunningGetMovement().execute();

    }

    List<Movement> movements;

    //ArrayList<>
    public void loadMovementData() {
        movements = new MovementProfile(getBaseContext()).getAllMovement();
        Calendar cal = Calendar.getInstance();

        int months = cal.get(cal.MONTH);
        months = months + 1;
        String month;
        if (months < 10) {
            month = "0" + months;
        } else {
            month = months + "";
        }
        int year = cal.get(cal.YEAR);

        MovementProfile movementProfile = new MovementProfile(getBaseContext());
        for (int i = 32; i > 1; i--) {


            List<Movement> movement;
            if (i < 10) {
                movement = movementProfile.getMovementDate(month + "/0" + i + "/" + year + " 00:00:00", month + "/0" + i + "/" + year + " 23:59:59");
            } else {
                movement = movementProfile.getMovementDate(month + "/" + i + "/" + year + " 00:00:00", month + "/" + i + "/" + year + " 23:59:59");

            }
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++ " + month + "/" + i + "/" + year + " 00:00:00" + " ----------- " + movement.size());
            if (movement.size() != 0) {
                MovementMainListModel sched = new MovementMainListModel();
                sched.setTitle(i + "/" + month + "/" + year);

                if (i == 1 || i == 21 || i == 31) {
                    sched.setDescription("Movement Log for " + i + "st of " + getMonthName(months) + " " + year);
                } else if (i == 2 || i == 23) {
                    sched.setDescription("Movement Log for " + i + "nd of " + getMonthName(months) + " " + year);
                } else if (i == 3) {
                    sched.setDescription("Movement Log for " + i + "rd of " + getMonthName(months) + " " + year);
                } else if ((i >= 4 && i <= 20) || (i >= 24 && i <= 30)) {
                    sched.setDescription("Movement Log for " + i + "th of " + getMonthName(months) + " " + year);
                }
                CustomListViewValuesArr.add(sched);
                // break;
            }
        }

    }

    boolean isArchiveYear = false;
    boolean isArchiveMonth;
    int archiveYear = 2015;


    public void loadMovementArchiveMonthData(int yea) {
        Reg reg = new RegistrationDate(getBaseContext()).get();
        //int year = reg.getRegDate().getYear();
        //int currentYear = new Date().getYear();
        //int range = currentYear-year;

        int[] months = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        for (int month : months) {
            movements = new MovementProfile(getBaseContext()).getMovementDate(month + "/01/" + yea + " 00:00:00", month + "/31/" + yea + " 23:59:59");
            if (!movements.isEmpty()) {

                MovementMainListModel sched = new MovementMainListModel();
                sched.setTitle(getMonthName(month) + "");
                sched.setDescription("Movement Log for the month of " + getMonthName(month)+" in the year "+archiveYear);

                CustomListViewValuesArr.add(sched);
            }
        }

        isArchiveMonth = true;
        isArchiveYear = false;
        // break;


    }

    public void loadMovementArchiveData() {
        Reg reg = new RegistrationDate(getBaseContext()).get();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String formatedDate = format.format(reg.getRegDate());
        int year = Integer.parseInt(formatedDate.split(" ")[0].split("/")[2]);
        int currentYear = Integer.parseInt(format.format(new Date()).split(" ")[0].split("/")[2]);;
        int range = currentYear - year;
        ArrayList<Integer> years = new ArrayList<>();
        if (range != 0) {
            for (int i = 0; i < (range + 1); i++) {

                int y = year + i;
                years.add(y);
            }
        } else {
            years.add(year);
        }
        for (int yea : years) {
            movements = new MovementProfile(getBaseContext()).getMovementDate("01/01/" + yea + " 00:00:00", "12/31/" + yea + " 23:59:59");
            if (!movements.isEmpty()) {
                MovementProfile movementProfile = new MovementProfile(getBaseContext());

                MovementMainListModel sched = new MovementMainListModel();
                sched.setTitle(yea + "");
                sched.setDescription("Movement Log for the year " + yea);

                CustomListViewValuesArr.add(sched);
            }
        }
        isArchiveYear = true;
        // break;


    }

    public void loadMovementMonthArchiveData(String month, String year) {
        movements = new MovementProfile(getBaseContext()).getAllMovement();
        Calendar cal = Calendar.getInstance();

        int j = Integer.parseInt(month);
        MovementProfile movementProfile = new MovementProfile(getBaseContext());


        if (j<10) {
            month = "0" + j;
        } else {
            month = j + "";
        }
        for (int i = 31; i > 0; i--) {


            List<Movement> movement;
            if (i < 10) {
                movement = movementProfile.getMovementDate(month + "/0" + i + "/" + year + " 00:00:00", month + "/0" + i + "/" + year + " 23:59:59");
            } else {
                movement = movementProfile.getMovementDate(month + "/" + i + "/" + year + " 00:00:00", month + "/" + i + "/" + year + " 23:59:59");

            }
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++ " + month + "/" + i + "/" + year + " 00:00:00" + " ----------- " + movement.size());
            if (movement.size() != 0) {
                MovementMainListModel sched = new MovementMainListModel();
                sched.setTitle(i + "/" + month + "/" + year);

                if (i == 1 || i == 21 || i == 31) {
                    sched.setDescription("Movement Log for " + i + "st of " + getMonthName(j) + " " + year);
                } else if (i == 2 || i == 23) {
                    sched.setDescription("Movement Log for " + i + "nd of " + getMonthName(j) + " " + year);
                } else if (i == 3) {
                    sched.setDescription("Movement Log for " + i + "rd of " + getMonthName(j) + " " + year);
                } else if ((i >= 4 && i <= 20) || (i >= 24 && i <= 30)) {
                    sched.setDescription("Movement Log for " + i + "th of " + getMonthName(j) + " " + year);
                }
                CustomListViewValuesArr.add(sched);
                // break;
            }
        }
        isArchiveMonth=false;
        isArchiveYear=false;


    }

    public void loadMovementArchiveData(String year) {
        movements = new MovementProfile(getBaseContext()).getAllMovement();
        Calendar cal = Calendar.getInstance();

        String month = "";
        MovementProfile movementProfile = new MovementProfile(getBaseContext());
        for (int j = 13; j > 1; j--) {

            if (10 < j) {
                month = "0" + j;
            } else {
                month = j + "";
            }
            for (int i = 32; i > 1; i--) {


                List<Movement> movement;
                if (i < 10) {
                    movement = movementProfile.getMovementDate(month + "/0" + i + "/" + year + " 00:00:00", month + "/0" + i + "/" + year + " 23:59:59");
                } else {
                    movement = movementProfile.getMovementDate(month + "/" + i + "/" + year + " 00:00:00", month + "/" + i + "/" + year + " 23:59:59");

                }
                System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++ " + month + "/" + i + "/" + year + " 00:00:00" + " ----------- " + movement.size());
                if (movement.size() != 0) {
                    MovementMainListModel sched = new MovementMainListModel();
                    sched.setTitle(i + "/" + month + "/" + year);

                    if (i == 1 || i == 21 || i == 31) {
                        sched.setDescription("Movement Log for " + i + "st of " + getMonthName(j) + " " + year);
                    } else if (i == 2 || i == 23) {
                        sched.setDescription("Movement Log for " + i + "nd of " + getMonthName(j) + " " + year);
                    } else if (i == 3) {
                        sched.setDescription("Movement Log for " + i + "rd of " + getMonthName(j) + " " + year);
                    } else if ((i >= 4 && i <= 20) || (i >= 24 && i <= 30)) {
                        sched.setDescription("Movement Log for " + i + "th of " + getMonthName(j) + " " + year);
                    }
                    CustomListViewValuesArr.add(sched);
                    // break;
                }
            }

        }
    }

    public String getMonthName(int position) {
        String[] monthStrings = {"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthStrings[position];

    }

    public int getMonthValue(String month) {
        String[] monthStrings = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        int m=0;

        for(String mon:monthStrings){
            m++;
            if(mon.equalsIgnoreCase(month)){
                break;
            }

        }
        return m;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movement_main, menu);
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
                Intent intentMain = new Intent(MovementMainActivity.this,
                        MainActivity.class);
                intentMain.putExtra("serviceState", true);
                MovementMainActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }else if(t.getName().equalsIgnoreCase("News")) {
                Intent intentMain = new Intent(MovementMainActivity.this,
                        Main2Activity.class);
                intentMain.putExtra("serviceState", true);
                MovementMainActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            } else if(t.getName().equalsIgnoreCase("Normal")) {
                Intent intentMain = new Intent(MovementMainActivity.this,
                        Main3Activity.class);
                intentMain.putExtra("serviceState", true);
                MovementMainActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }

        } else if (id == R.id.archive)

        {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.passcode_dialog);

            SpannableString s = new SpannableString("Access Authorization");
            s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
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
                    if(name.getText().toString().equalsIgnoreCase(user.getPassCode())){
                        SpannableString s = new SpannableString("Movement Archive");
                        s.setSpan(new TypefaceSpan(MovementMainActivity.this, "exo_medium.otf"), 0, s.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
                        actionBar = getSupportActionBar();
                        actionBar.setTitle(s);
                        CustomListViewValuesArr.clear();
                        loadMovementArchiveData();

                        Resources res = getResources();
                        list = (ListView) findViewById(R.id.list_movement_main);  // List defined in XML ( See Below )

                        /**************** Create Custom Adapter *********/
                        adapter = new MovementMainAdapter(CustomListView, CustomListViewValuesArr, res);
                        list.setAdapter(adapter);
                        dialog.dismiss();
                    }else{
                        Toast.makeText(MovementMainActivity.this,"Incorrect Access Code",Toast.LENGTH_LONG).show();
                    }


                }

            });

            dialog.show();



        }


        return super.onOptionsItemSelected(item);
    }

    public void onItemClick(int mPosition) {
        MovementMainListModel tempValues = (MovementMainListModel) CustomListViewValuesArr.get(mPosition);

        if (isArchiveYear) {
            archiveYear = Integer.parseInt(tempValues.getTitle());


            CustomListViewValuesArr.clear();
            loadMovementArchiveMonthData(Integer.parseInt(tempValues.getTitle()));
            //loadMovementArchiveData();

            Resources res = getResources();
            list = (ListView) findViewById(R.id.list_movement_main);  // List defined in XML ( See Below )

            /**************** Create Custom Adapter *********/
            adapter = new MovementMainAdapter(CustomListView, CustomListViewValuesArr, res);
            list.refreshDrawableState();

            list.setAdapter(adapter);
            list.refreshDrawableState();
        } else if (isArchiveMonth) {

            SpannableString s = new SpannableString(tempValues.getTitle()+" "+archiveYear);
            s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
            actionBar = getSupportActionBar();
            actionBar.setTitle(s);

            CustomListViewValuesArr.clear();
            loadMovementMonthArchiveData(getMonthValue(tempValues.getTitle()) + "", archiveYear + "");



            Resources res = getResources();
            list = (ListView) findViewById(R.id.list_movement_main);  // List defined in XML ( See Below )

            /**************** Create Custom Adapter *********/
            adapter = new MovementMainAdapter(CustomListView, CustomListViewValuesArr, res);
            list.setAdapter(adapter);
        } else {
            Intent intentMain = new Intent(MovementMainActivity.this,
                    MovementActivity.class);
            String[] dateArray = tempValues.getTitle().split("/");
            String day = dateArray[0];
            String month = dateArray[1];
            if (Integer.parseInt(day) < 10) {
                day = "0" + day;
            }
            if (Integer.parseInt(month) < 10) {
                month = "" + month;
            }
            intentMain.putExtra("date", month + "/" + day + "/" + dateArray[2]);
            MovementMainActivity.this.startActivity(intentMain);
            finish();
        }


    }
}
