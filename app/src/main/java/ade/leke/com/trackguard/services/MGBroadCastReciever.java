package ade.leke.com.trackguard.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ade.leke.com.trackguard.db.db.entities.profile.SettingsProfile;

/**
 * Created by SecureUser on 10/28/2015.
 */
public class MGBroadCastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                //Intent y = new Intent(context,MainActivity.class);
                //context.startActivity(y);
                try{
                ade.leke.com.trackguard.db.db.entities.Settings settings = new SettingsProfile(context).get();
                if(settings.getMGS().equalsIgnoreCase("1")) {
                    context.startService(new Intent(context, PayService.class));

                    context.startService(new Intent(context, LogMovementService.class));

                    context.startService(new Intent(context, SimChangeService.class));


                    context.startService(new Intent(context, NewsService.class));
                }
               }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

