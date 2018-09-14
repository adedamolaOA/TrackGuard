package ade.leke.com.trackguard;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import ade.leke.com.trackguard.common.Mail;
import ade.leke.com.trackguard.common.TypefaceSpan;
import ade.leke.com.trackguard.db.db.entities.AppThemes;
import ade.leke.com.trackguard.db.db.entities.Movement;
import ade.leke.com.trackguard.db.db.entities.Reg;
import ade.leke.com.trackguard.db.db.entities.Settings;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.AppThemeProfile;
import ade.leke.com.trackguard.db.db.entities.profile.RegistrationDate;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;

public class ProfileActivity extends ActionBarActivity {

    EditText txtFirstname;
    EditText txtOthernames;
    EditText txtMobileNumber;
    EditText txtEmail;
    EditText txtPassCode;
    User user;
    String verificationKey;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static int RESULT_LOAD_IMAGE = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String IMAGE_DIRECTORY_NAME = "SecureMeSnap";
    String galleryPath;
    private Uri fileUri; // file url to store image/video

    private ImageView imgPreview;
    private VideoView videoPreview;
    private Button btnCapturePicture, btnRecordVideo;

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
        setContentView(R.layout.activity_profile);
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

        imgPreview = (ImageView) findViewById(R.id.imagePreview);
btnCapturePicture = (Button)findViewById(R.id.butGallery);
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        SpannableString s = new SpannableString("Profile");
        s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(s);
        verificationKey = UUID.randomUUID().toString();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        user = new UserProfile(getBaseContext()).get();
        txtFirstname = (EditText) findViewById(R.id.txtProfileFirstname);
        txtOthernames = (EditText) findViewById(R.id.txtProfileOthernames);
        txtMobileNumber = (EditText) findViewById(R.id.txtProfileMobile);
        txtEmail = (EditText) findViewById(R.id.txtProfileEmail);

        txtEmail.setText(user.getEmail());
        txtFirstname.setText(user.getFirstname());
        txtMobileNumber.setText(user.getMobileNumber());
        txtOthernames.setText(user.getLastname());
        if(!user.getVersion().equalsIgnoreCase("1")) {
            Bitmap b = BitmapFactory.decodeFile(user.getVersion());
            Bitmap scaledBitmap = scaleDown(b, 200, true);

            imgPreview.setImageBitmap(scaledBitmap);
        }else{
            imgPreview.setImageResource(R.drawable.contact_image);
        }
        Button passCode = (Button) findViewById(R.id.butChangeAccessCode);
        passCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.pass_code_change_layout);

                SpannableString s = new SpannableString("Change Access Code");
                s.setSpan(new TypefaceSpan(ProfileActivity.this, "exo_medium.otf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

                dialog.setTitle(s);
                dialog.setCancelable(true);


                // set the custom dialog components - text, image and button
                final EditText cpass = (EditText) dialog.findViewById(R.id.txtCCurrentPassword);
                final EditText npass = (EditText) dialog.findViewById(R.id.txtCCNewPassword);
                final EditText rcpass = (EditText) dialog.findViewById(R.id.txtCCCNewPassword);


                Button saveContact = (Button) dialog.findViewById(R.id.butChangeAccessCodes);
                // if button is clicked, close the custom dialog
                saveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        User user = new UserProfile(getBaseContext()).get();
                        if (cpass.getText().toString().equalsIgnoreCase(user.getPassCode())) {

                            if (npass.getText().toString().equalsIgnoreCase(rcpass.getText().toString())) {
                                user.setPassCode(npass.getText().toString());
                                boolean state = new UserProfile(getBaseContext()).update(user);
                                if (state) {
                                    Toast.makeText(ProfileActivity.this, "Access Code Updated", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            }
                        }

                    }

                });


                dialog.show();
            }
        });

        Button passCodeEmail = (Button) findViewById(R.id.butForgot);
        passCodeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });

        Button update = (Button) findViewById(R.id.butUpdateProfile);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!txtFirstname.getText().toString().isEmpty() && !txtOthernames.getText().toString().isEmpty() && !txtEmail.getText().toString().isEmpty()
                        && !txtMobileNumber.getText().toString().isEmpty()) {
                    if (txtEmail.getText().toString().contains("@")
                            && (txtEmail.getText().toString().contains(".com") || txtEmail.getText().toString().contains(".co") ||
                            txtEmail.getText().toString().contains(".co.uk") || txtEmail.getText().toString().contains(".gov.ng"))) {


                        user.setLastname(txtOthernames.getText().toString());
                        user.setMobileNumber(txtMobileNumber.getText().toString());
                        user.setEmail(txtEmail.getText().toString());
                        user.setFirstname(txtFirstname.getText().toString());


                        boolean state = new UserProfile(getBaseContext()).update(user);
                        if (state) {
                            Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Unable to complete sign up", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Invaid Email Address", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "One or more Empty fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void sendEmail() {

        Mail m = new Mail("wiitech.mobile.guard@gmail.com", "millinik");

        User user = new UserProfile(ProfileActivity.this).get();
        String[] toArr = {user.getEmail(), "wiitech.mobile.guard@gmail.com"};//
        m.setTo(toArr);
        m.setFrom("wiitech.mobile.guard@gmail.com");
        m.setSubject("Verification Key");
        StringBuilder panicMsg = new StringBuilder();
        panicMsg.append("You have requested to change you access code on the Mobile Guard System\n");
        panicMsg.append("Please Copy and Paste the Verification Key Below into the verification key field on the Mobile Guard application\n");
        panicMsg.append("Verification Key: " + verificationKey + "\n");
        m.setBody(panicMsg.toString());

        try {

            if (m.send()) {
                Toast.makeText(ProfileActivity.this, "Verification Key Sent to Email", Toast.LENGTH_LONG).show();

                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.email_passcode_layout);

                SpannableString s = new SpannableString("Verification Key");
                s.setSpan(new TypefaceSpan(this, "exo_medium.otf"), 0, s.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

                dialog.setTitle(s);
                dialog.setCancelable(true);


                // set the custom dialog components - text, image and button
                final EditText name = (EditText) dialog.findViewById(R.id.txtVerificationText);


                Button saveContact = (Button) dialog.findViewById(R.id.butEmailVerify);
                // if button is clicked, close the custom dialog
                saveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (name.getText().toString().equalsIgnoreCase(verificationKey)) {
                            dialog.dismiss();
                            final Dialog dialog2 = new Dialog(ProfileActivity.this);
                            dialog2.setContentView(R.layout.reset_passcode_layout);

                            SpannableString s = new SpannableString("Change Access Code");
                            s.setSpan(new TypefaceSpan(ProfileActivity.this, "exo_medium.otf"), 0, s.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster

                            dialog2.setTitle(s);
                            dialog2.setCancelable(true);


                            // set the custom dialog components - text, image and button
                            final EditText newPass = (EditText) dialog2.findViewById(R.id.txtCCuNewPassword);
                            final EditText cNewPass = (EditText) dialog2.findViewById(R.id.txtCCCuNewPasscode);


                            Button saveContact = (Button) dialog2.findViewById(R.id.butCCCAccessCodes);
                            // if button is clicked, close the custom dialog
                            saveContact.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    User user = new UserProfile(getBaseContext()).get();
                                    if (newPass.getText().toString().equalsIgnoreCase(cNewPass.getText().toString())) {


                                            user.setPassCode(cNewPass.getText().toString());
                                            boolean state = new UserProfile(getBaseContext()).update(user);
                                            if (state) {
                                                Toast.makeText(ProfileActivity.this, "Access Code Updated", Toast.LENGTH_LONG).show();
                                                dialog2.dismiss();
                                            }

                                    }

                                }

                            });


                            dialog.show();
                        }

                    }

                });

                Button cancel = (Button) dialog.findViewById(R.id.butResentVerficationKey);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Mail m = new Mail("wiitech.mobile.guard@gmail.com", "millinik");

                        User user = new UserProfile(ProfileActivity.this).get();
                        String[] toArr = {user.getEmail(), "wiitech.mobile.guard@gmail.com"};//
                        m.setTo(toArr);
                        m.setFrom("wiitech.mobile.guard@gmail.com");
                        m.setSubject("Verification Key");
                        StringBuilder panicMsg = new StringBuilder();
                        panicMsg.append("You have requested to change you access code on the Mobile Guard System\n");
                        panicMsg.append("Please Copy and Paste the Verification Key Below into the verification key field on the Mobile Guard application\n");
                        panicMsg.append("Verification Key: " + verificationKey + "\n");
                        m.setBody(panicMsg.toString());
                        try {
                            if (m.send()) {
                                Toast.makeText(ProfileActivity.this, "Verification Key sent to email address", Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception w) {
                            w.printStackTrace();
                        }

                    }
                });
                dialog.show();

                Log.i("Content ", " Menu layout ");
            } else {
                Toast.makeText(ProfileActivity.this, "failed", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            //Toast.makeText(MailApp.this, "There was a problem sending the email.", Toast.LENGTH_LONG).show();
            Log.e("MailApp", "Could not send email", e);
        }
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

            //snapped = BitmapFactory.decodeFile(picturePath);
            Bitmap b = BitmapFactory.decodeFile(picturePath);
            Bitmap scaledBitmap = scaleDown(b, 200, true);

            imgPreview.setImageBitmap(scaledBitmap);
            User user = new UserProfile(getBaseContext()).get();
            user.setVersion(picturePath);
            boolean state = new UserProfile(getBaseContext()).update(user);

        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
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

            //snapped = bitmap;
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
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home)

        {
            AppThemes t = new AppThemeProfile(getBaseContext()).get();
            if(t.getName().equalsIgnoreCase("Panic")) {
                Intent intentMain = new Intent(ProfileActivity.this,
                        MainActivity.class);
                intentMain.putExtra("serviceState", true);
                ProfileActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }else if(t.getName().equalsIgnoreCase("News")) {
                Intent intentMain = new Intent(ProfileActivity.this,
                        Main2Activity.class);
                intentMain.putExtra("serviceState", true);
                ProfileActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            } else if(t.getName().equalsIgnoreCase("Normal")) {
                Intent intentMain = new Intent(ProfileActivity.this,
                        Main3Activity.class);
                intentMain.putExtra("serviceState", true);
                ProfileActivity.this.startActivity(intentMain);
                Log.i("Content ", " Menu layout ");
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
