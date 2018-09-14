package ade.leke.com.trackguard.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import ade.leke.com.trackguard.NotificationActivity;
import ade.leke.com.trackguard.R;
import ade.leke.com.trackguard.db.db.entities.News;
import ade.leke.com.trackguard.db.db.entities.profile.NewsProfile;
import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;
import ade.leke.com.trackguard.handler.DataHandler;

/**
 * Created by SecureUser on 1/29/2016.
 */
public class NewsService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
new LongRunningGetIO().execute();
            }
        });

        t.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if ((intent != null)){
            onStart(intent, startId);
            return START_STICKY;

        }
        // Do your other onStartCommand stuff..
        return START_STICKY;

    }

    private class LongRunningGetIO extends AsyncTask<Void, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n>0) {
                byte[] b = new byte[4096];

                n =  in.read(b);

                if (n>0) out.append(new String(b, 0, n));

            }

            return out.toString();

        }

        @Override
        protected void onPreExecute() {
        }




        @Override

        protected String doInBackground(Void... params) {




            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            int intial = 0;
            int max = 0;
            ArrayList news = new NewsProfile(getBaseContext()).get();

intial = news.size();
                max = news.size()+100;

            HttpGet httpGet = new HttpGet("http://41.87.71.62:5885/MobileGuardWebService/webresources/com.ade.leke.entity.news/"+intial+"/"+max);
            String text = null;
            try {

                HttpResponse response = httpClient.execute(httpGet, localContext);

                HttpEntity entity = response.getEntity();

                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                return e.getLocalizedMessage();

            }







            return text;

        }

        protected void onPostExecute(String results) {
            if (results!=null) {

                try {
                    System.out.println("==================================================" + results);
                    JSONArray obj = new JSONArray(results);
                    System.out.println("============================"+obj.length());
                    //JSONArray obj = oj.getJSONArray("");
                   // NotificationManager notif = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                   // Notification notify = new Notification(R.mipmap.ic_logo_t, "Location Request", System.currentTimeMillis());

                    Intent intentMain = new Intent(getBaseContext(),
                            NotificationActivity.class);


                   // PendingIntent pending = PendingIntent.getActivity(getBaseContext().getApplicationContext(), 0, intentMain, 0);
StringBuilder b = new StringBuilder();

                    for(int i=0;i<obj.length();i++){
                        JSONObject o = obj.getJSONObject(i);
                        String client = o.getString("client");
                        String date = o.getString("date");
                        int id = o.getInt("id");
                        String image = o.getString("image");
                        String bulletin = o.getString("image2");
                        String author = o.getString("image3");
                        String news = o.getString("news");
                        String status = o.getString("status");
                        String subject = o.getString("subject");
                        String uuid = o.getString("uuid");
                        System.out.println("============================" + uuid);

                        News n = new News();
                        n.setClient(client);
                        n.setNews(news);
                        n.setUuid(uuid);
                        n.setDate(date);
                        n.setSubject(subject);
                        n.setId(id);
                        n.setBulletin(bulletin);
                        n.setAuthor(author);
                        n.setImage(image);
                        n.setStatus(status);

                        int h = 0;
                        h=n.getNews().length();
                        if(h>20){
                            h=20;
                        }
                        b.append(n.getNews().substring(0,h));
                        b.append("\n");
                        boolean state = new NewsProfile(getBaseContext()).create(n);


                    }
                   // notify.setLatestEventInfo(getBaseContext().getApplicationContext(), "Mobile Guard News", b.toString() , pending);

                   // notif.notify(0, notify);


                }catch (Exception e){
e.printStackTrace();
                }



            }

            try {

                Thread.sleep(300000);//15 Min : 900000
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            new LongRunningGetIO().execute();

        }

    }

}
