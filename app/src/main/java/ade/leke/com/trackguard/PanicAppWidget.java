package ade.leke.com.trackguard;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import ade.leke.com.trackguard.db.db.entities.profile.ContactProfile;

/**
 * Implementation of App Widget functionality.
 */
public class PanicAppWidget extends AppWidgetProvider {



    LocationManager locationManager ;
    String provider;
    String msg;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

     void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
         if(new ContactProfile(context).contactCount()!=0) {

             double latitude = 0D;
             double longitude = 0D;


             CharSequence widgetText = context.getString(R.string.appwidget_text);
             // Construct the RemoteViews object
             Intent intent = new Intent(context, PanicAlert.class);
             msg = "latitude: " + latitude + ", longitude: " + longitude;
             intent.putExtra("loc", msg);
             PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
             RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.panic_app_widget);
             views.setOnClickPendingIntent(R.id.butPanicAlertW, pendingIntent);
             appWidgetManager.updateAppWidget(appWidgetId, views);
             //views.setTextViewText(R.id.appwidget_text, widgetText);
         }else{
             Toast.makeText(context, "No Panic Contact", Toast.LENGTH_LONG).show();
             Intent intent = new Intent(context, ContactActivity.class);

             PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
             RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.panic_app_widget);
             views.setOnClickPendingIntent(R.id.butPanicAlertW, pendingIntent);
             appWidgetManager.updateAppWidget(appWidgetId, views);
         }
        // Instruct the widget manager to update the widget

    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            //Toast.makeText(getBaseContext(), loc.getLongitude()+" -- "+loc.getLatitude(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
}

