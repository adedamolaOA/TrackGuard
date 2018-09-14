package ade.leke.com.trackguard;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import ade.leke.com.trackguard.common.GPSTracker;
import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.model.AgencyAdapter;
import ade.leke.com.trackguard.model.AgencyListModel;
import ade.leke.com.trackguard.model.Mail;

public class ReportViolationActivity extends ActionBarActivity {

    ListView list;
    AgencyAdapter adapter;
    public  ReportViolationActivity CustomListView = null;
    public ArrayList<AgencyListModel> CustomListViewValuesArr = new ArrayList<AgencyListModel>();
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static int RESULT_LOAD_IMAGE = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    String galleryPath;
    private Uri fileUri; // file url to store image/video

    private ImageView imgPreview;
    private VideoView videoPreview;
    private Button btnCapturePicture, btnRecordVideo;
    TextView comment;
    // directory name to store captured images and videos
    private static final String IMAGE_DIRECTORY_NAME = "Violation";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_violation);
        SpannableString s = new SpannableString("Report Incidents");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        comment = (TextView) findViewById(R.id.lblComment);
        imgPreview = (ImageView) findViewById(R.id.imgCaptured);



        Button report = (Button) findViewById(R.id.butReport);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*StrictMode.ThreadPolicy policy = new
                        StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);*/
                Mail m = new Mail("npf.app@gmail.com", "millinik1987");
                GPSTracker gps = new GPSTracker(ReportViolationActivity.this);
                if(gps.canGetLocation()){
                    String[] toArr = {"adeleke.adedamola@gmail.com","npf.app@gmail.com"};
                    m.setTo(toArr);
                    m.setFrom("npf.app@gmail.com");
                    m.setSubject("Incident Report @ Latitude: "+gps.getLatitude()+", Longitude: "+gps.getLongitude());
                    m.setBody(comment.getText().toString());

                    try {
                        if(fileUri!=null) {
                            m.addAttachment(fileUri.getPath());
                        }else{
                            m.addAttachment(galleryPath);
                        }

                        if(m.send()) {
                            Toast.makeText(ReportViolationActivity.this, "Incident Report was sent successfully.", Toast.LENGTH_LONG).show();
                            //finish();
                        } else {
                            Toast.makeText(ReportViolationActivity.this, "Unable to Report Incident", Toast.LENGTH_LONG).show();
                        }
                    } catch(Exception e) {
                        //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
                        Log.e("MailApp", "Could not send email", e);
                    }
                }else{
                    gps.showSettingsAlert();
                }

            }
        });


        /*CustomListView = this;
        setList();

        Resources res =getResources();
        list= (ListView)findViewById( R.id.agency_listView );  // List defined in XML ( See Below )

        *************** Create Custom Adapter ********
        adapter=new AgencyAdapter( CustomListView, CustomListViewValuesArr,res );
        list.setAdapter(adapter);*/



    }

    public void setList(){


            final AgencyListModel sched = new AgencyListModel();

            /******* Firstly take data in model object ******/
            sched.setName("Nigerian Police Force");
            sched.setImage("police");
            sched.setDescription("Report Incidents to the nigerian police force for processing");

            /******** Take Model Object in ArrayList **********/
            CustomListViewValuesArr.add(sched);

        final AgencyListModel nema = new AgencyListModel();

        /******* Firstly take data in model object ******/
        nema.setName("National Emergency Management Agency");
        nema.setImage("nema");
        nema.setDescription("Report Incidents to the NEMA for processing");

        /******** Take Model Object in ArrayList **********/
        CustomListViewValuesArr.add(nema);

        final AgencyListModel fire_service = new AgencyListModel();

        /******* Firstly take data in model object ******/
        fire_service.setName("Nigerian Fire Service");
        fire_service.setImage("fire_service");
        fire_service.setDescription("Report Incidents to the nigerian fire service for processing");

        /******** Take Model Object in ArrayList **********/
        CustomListViewValuesArr.add(fire_service);

        final AgencyListModel frsc = new AgencyListModel();

        /******* Firstly take data in model object ******/
        frsc.setName("Federal Road Safety Corps");
        frsc.setImage("frsc");
        frsc.setDescription("Report Incidents to the nigerian Federal Road Safety Corps for processing");

        /******** Take Model Object in ArrayList **********/
        CustomListViewValuesArr.add(frsc);

    }

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }
    /**
     * Recording video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // preview the recorded video
                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            galleryPath = picturePath;

            imgPreview.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }
    }

    /**
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            // hide video preview
            //videoPreview.setVisibility(View.GONE);

            imgPreview.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            imgPreview.setImageBitmap(bitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Previewing recorded video
     */
    private void previewVideo() {
        try {
            // hide image preview
            imgPreview.setVisibility(View.GONE);

            videoPreview.setVisibility(View.VISIBLE);
            videoPreview.setVideoPath(fileUri.getPath());
            // start playing
            videoPreview.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report_violation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_note) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.note_layout);

            SpannableString s = new SpannableString("Add Comment");
            s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

            dialog.setTitle(s);

            // set the custom dialog components - text, image and button
            final EditText name = (EditText) dialog.findViewById(R.id.txtComment);


            Button saveComment = (Button) dialog.findViewById(R.id.butSaveComment);
            // if button is clicked, close the custom dialog
            saveComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    comment.setText(name.getText());
                    //comment.refreshDrawableState();
                    Toast.makeText(getBaseContext(),"Comment Added",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

            });

            Button cancel = (Button) dialog.findViewById(R.id.butNoteCancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();

                }
            });
            dialog.show();
            return true;
        }else if (id == R.id.gallery) {
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
            return true;
        }else if (id == R.id.camera) {
            captureImage();
            return true;
        }else if(id == android.R.id.home){

            Intent intentMain = new Intent(ReportViolationActivity.this,
                    MainActivity.class);
            ReportViolationActivity.this.startActivity(intentMain);
            Log.i("Content ", " Menu layout ");
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    /*****************  This function used by adapter ****************/
    public void onItemClick(int mPosition)
    {
        AgencyListModel tempValues = ( AgencyListModel ) CustomListViewValuesArr.get(mPosition);




        Toast.makeText(CustomListView,
                "" + tempValues.getName() + " deleted successfully"
                ,
                Toast.LENGTH_LONG)
                .show();
    }
}
