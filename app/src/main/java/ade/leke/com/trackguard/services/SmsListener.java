package ade.leke.com.trackguard.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.HandlerThread;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ade.leke.com.trackguard.LocationDirectionMap;
import ade.leke.com.trackguard.NotificationActivity;
import ade.leke.com.trackguard.PanicLocationDirectionMap;
import ade.leke.com.trackguard.R;
import ade.leke.com.trackguard.db.FeedReaderContract;
import ade.leke.com.trackguard.db.FeedReaderDbHelper;
import ade.leke.com.trackguard.db.NotificationDbReader;
import ade.leke.com.trackguard.db.db.entities.Contact;
import ade.leke.com.trackguard.db.db.entities.Settings;
import ade.leke.com.trackguard.db.db.entities.User;
import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;
import ade.leke.com.trackguard.db.db.entities.profile.NotificationProfile;
import ade.leke.com.trackguard.db.db.entities.Panic;
import ade.leke.com.trackguard.db.db.entities.profile.PanicProfile;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;
import ade.leke.com.trackguard.db.db.entities.profile.UserProfile;

/**
 * Created by SecureUser on 10/1/2015.
 */
public class SmsListener extends BroadcastReceiver {

    private SharedPreferences preferences;

    NotificationDbReader nDbHepler;

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub

        mDbHelper = new FeedReaderDbHelper(context);
        nDbHepler = new NotificationDbReader(context);

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        System.out.println(msg_from + " ==================");
                        System.out.println(msg_from.replace("+234", "0") + "==========" + msgBody.split(":").length);
                        if (new ContactProfile(context).getByMobileNumber(msg_from.replace("+234", "0")) != null && msgBody.split(":").length != 0) {

                            System.out.println(msgBody);
                            if (msgBody.split(":")[0].equalsIgnoreCase("MGloc")) {

                                String[] hj = {msg_from};
                                Contact contact = new ContactProfile(context).getByMobileNumber(msg_from.replace("+234", "0"));

                                String request = msgBody.split(":")[1];

                                ade.leke.com.trackguard.db.db.entities.Notification notification = new ade.leke.com.trackguard.db.db.entities.Notification();
                                notification.setNotificationResponse("None");
                                notification.setNotificationStatus("NR");
                                notification.setUid(UUID.randomUUID().toString());
                                notification.setNid(0);
                                notification.setNotificationRequest("LR");
                                notification.setNotificationContact(contact.getPhoneNumber());
                                SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                                notification.setNotificationDate(format.format(new Date()));
                                notification.setNotificationMessage(contact.getFirstname() + " " + contact.getLastname() + " is requesting your location");

                                boolean state = new NotificationProfile(context).create(notification);


                                if (state) {
                                    NotificationManager notif = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    Notification notify = new Notification(R.mipmap.ic_logo_t, "Location Request", System.currentTimeMillis());

                                    Intent intentMain = new Intent(context,
                                            NotificationActivity.class);


                                    PendingIntent pending = PendingIntent.getActivity(context.getApplicationContext(), 0, intentMain, 0);

                                    notify.setLatestEventInfo(context.getApplicationContext(), contact.getFirstname() + " " + contact.getLastname(), "Is Requesting for your location", pending);

                                    notif.notify(0, notify);
                                    getAbortBroadcast();
                                }
                            } else if (msgBody.split(":")[0].equalsIgnoreCase("Mgps")) {

                                String[] hj = {msg_from};
                                // How you want the results sorted in the resulting Cursor
                                Contact contact = new ContactProfile(context).getByMobileNumber(msg_from.replace("+234", "0"));

                                if (contact != null) {

                                    ade.leke.com.trackguard.db.db.entities.Notification notification = new ade.leke.com.trackguard.db.db.entities.Notification();
                                    notification.setNotificationResponse(msgBody.split(":")[1] + ":" + msgBody.split(":")[2]);
                                    notification.setNotificationStatus("NL");
                                    notification.setUid(UUID.randomUUID().toString());
                                    notification.setNid(0);
                                    notification.setNotificationRequest("GR");
                                    notification.setNotificationContact(contact.getPhoneNumber());
                                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                                    notification.setNotificationDate(format.format(new Date()));
                                    notification.setNotificationMessage(contact.getFirstname() + " " + contact.getLastname() + " sent current location to you");

                                    boolean state = new NotificationProfile(context).create(notification);

                                    if (state) {
                                        Intent intentMain = new Intent(context,
                                                LocationDirectionMap.class);
                                        intentMain.putExtra("id", contact.getContactId());
                                        intentMain.putExtra("name", contact.getFirstname() + " " + contact.getLastname());
                                        intentMain.putExtra("phoneNumber", contact.getPhoneNumber());
                                        intentMain.putExtra("lat", msgBody.split(":")[1]);
                                        intentMain.putExtra("lng", msgBody.split(":")[2]);
                                        NotificationManager notif = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                        Notification notify = new Notification(R.mipmap.ic_logo_t, "New Location to View", System.currentTimeMillis());

                                        PendingIntent pending = PendingIntent.getActivity(context.getApplicationContext(), 0, intentMain, 0);

                                        notify.setLatestEventInfo(context.getApplicationContext(), contact.getFirstname() + " " + contact.getLastname(), "Says View My Current Location", pending);

                                        notif.notify(0, notify);
                                        getAbortBroadcast();
                                    }
                                }
                            } else if (msgBody.split(":")[0].equalsIgnoreCase("MGpanic") && msgBody.split(":").length > 2) {

                                String[] hj = {msg_from};
                                Contact contact = new ContactProfile(context).getByMobileNumber(msg_from.replace("+234", "0"));
// How you want the results sorted in the resulting Cursor


                                if (contact != null) {

                                    ade.leke.com.trackguard.db.db.entities.Notification notification = new ade.leke.com.trackguard.db.db.entities.Notification();
                                    notification.setNotificationResponse(msgBody.split(":")[2] + ":" + msgBody.split(":")[3]);
                                    notification.setNotificationStatus("NP");
                                    notification.setUid(msgBody.split(":")[1]);
                                    notification.setNid(0);
                                    notification.setNotificationRequest("GP");
                                    notification.setNotificationContact(contact.getPhoneNumber());
                                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                                    notification.setNotificationDate(format.format(new Date()));
                                    notification.setNotificationMessage(contact.getFirstname() + " " + contact.getLastname() + " is sending you a panic alert");

                                    boolean state = new NotificationProfile(context).create(notification);

                                    if (state) {
                                        Panic panic = new Panic();
                                        panic.setPid(0);
                                        panic.setStatus("1");
                                        panic.setLat(msgBody.split(":")[2]);
                                        panic.setLng(msgBody.split(":")[3]);
                                        panic.setAddress(contact.getPhoneNumber());
                                        panic.setUid(msgBody.split(":")[1]);
                                        SimpleDateFormat format2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                                        panic.setDate(format2.format(new Date()));

                                        boolean statePanic = new PanicProfile(context).create(panic);
                                        if (statePanic) {
                                            Intent intentMain = new Intent(context, PanicLocationDirectionMap.class);
                                            intentMain.putExtra("id", contact.getContactId());
                                            intentMain.putExtra("name", contact.getFirstname() + " " + contact.getLastname());
                                            intentMain.putExtra("phoneNumber", contact.getPhoneNumber());
                                            intentMain.putExtra("lat", panic.getLat());
                                            intentMain.putExtra("lng", panic.getLng());
                                            intentMain.putExtra("uid", panic.getUid());
                                            NotificationManager notif = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                            Notification notify = new Notification(R.mipmap.ic_logo_t, "Panic Alert from " + contact.getFirstname(), System.currentTimeMillis());

                                            PendingIntent pending = PendingIntent.getActivity(context.getApplicationContext(), 0, intentMain, 0);

                                            notify.setLatestEventInfo(context.getApplicationContext(), contact.getFirstname(), "is sending you a panic alert", pending);

                                            notif.notify(0, notify);
                                            getAbortBroadcast();
                                        }
                                    }
                                }
                            } else if (msgBody.split(" ")[0].equalsIgnoreCase("MGAct")) {
                                String acode = msgBody.split(" ")[1];
                                User user = new UserProfile(context).get();
                                List<Contact> contacts = new ContactProfile(context).getAllContacts();
                                boolean known = false;
                                for (Contact contact : contacts) {
                                    if (contact.getPhoneNumber().equalsIgnoreCase(msg_from.replace("+234", "0"))) {
                                        known = true;
                                    }
                                }
                                if (user.getMobileNumber().contains(acode) && known) {


                                    final Context cc = context;
                                    //HandlerThread thread = new HandlerThread("MGAlert",1);
                                    Settings settings = new SettingsProfile(context).get();
                                    settings.setSMS("0");
                                    boolean stat = new SettingsProfile(context).update(settings);
                                    Thread thread1 = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            AudioManager audioManager = (AudioManager) cc.getSystemService(Context.AUDIO_SERVICE);

                                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                                            Ringtone r = RingtoneManager.getRingtone(cc, notification);
                                            //r.play();
                                            int i = 0;

                                            while (i <= 30) {

                                                i++;
                                                r.play();
                                                Settings settings = new SettingsProfile(context).get();
                                                if(settings.getSMS().equalsIgnoreCase("1")){
                                                    i=29;
                                                    r.stop();
                                                }else{
                                                    audioManager.setRingerMode(AudioManager.MODE_RINGTONE);
                                                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 5);
                                                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 10);
                                                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 20);
                                                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 30);//Volume(AudioManager.ADJUST_RAISE, 100);
                                                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));//Volume(AudioManager.ADJUST_RAISE, 100);

                                                }
                                                if (i > 29) {
                                                    r.stop();
                                                }

                                                try {
                                                    Thread.sleep(15000L);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                    });
                                    thread1.start();
                                }
                            }
                        }


                    }
                } catch (Exception e) {
//                            Log.d("Exception caught",e.getMessage());
                }
            }
        }
    }

    FeedReaderDbHelper mDbHelper;

    public ArrayList<String> setContactListData(String number) {

        ArrayList<String> numbers = new ArrayList<>();
        try {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
            String[] projection = {
                    FeedReaderContract.FeedEntry._ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_Contact_ID,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NAME,
                    FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber

            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    FeedReaderContract.FeedEntry.COLUMN_NAME_NAME + " DESC";

            final Cursor c = db.query(
                    FeedReaderContract.FeedEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber + " = '" + number + "'",                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            if (c != null) {
                c.moveToNext();
                long itemId = c.getLong(
                        c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));


                String phone = c.getString(c.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_Mobile_NUmber));
                numbers.add(phone);


            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        return numbers;

    }

}
