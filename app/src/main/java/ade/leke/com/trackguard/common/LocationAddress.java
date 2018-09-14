package ade.leke.com.trackguard.common;

/**
 * Created by SecureUser on 10/28/2015.
 */
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ade.leke.com.trackguard.db.db.entities.Panic;

public class LocationAddress {
    private static final String TAG = "LocationAddress";

    public static void getAddressFromLocation(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();

                        String[] addressArray = result.split("\n");
                        String address = "";
                        int k = 0;
                        for(String aType: addressArray){
                            if(k==0){
                                address = aType;
                            }
                            else if(k==(addressArray.length-3)){
                                if(!aType.equalsIgnoreCase("null")) {
                                    address = address + "\n " + aType;
                                }
                            }else if(k>=(addressArray.length-2)){
                                if(!aType.equalsIgnoreCase("null")) {
                                    address = address + "\n " + aType;
                                }
                            }
                            else{
                                address = address+", "+aType;
                            }
                            k++;
                        }
                        bundle.putString("address", address);
                        bundle.putDouble("lat", latitude);
                        bundle.putDouble("lng",longitude);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();

                        bundle.putString("address", "No Address Found");
                        bundle.putDouble("lat",latitude);
                        bundle.putDouble("lng",longitude);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    public static void getAddressFromLocationSIM(final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    message.what = 2;
                    Bundle bundle = new Bundle();
                    bundle.putString("address", "that can not be retrieved");
                    message.setData(bundle);

                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();

                        String[] addressArray = result.split("\n");
                        String address = "";
                        int k = 0;
                        for(String aType: addressArray){
                            if(k==0){
                                address = aType;
                            }
                            else if(k==(addressArray.length-3)){
                                if(!aType.equalsIgnoreCase("null")) {
                                    address = address + "\n " + aType;
                                }
                            }else if(k>=(addressArray.length-2)){
                                if(!aType.equalsIgnoreCase("null")) {
                                    address = address + "\n " + aType;
                                }
                            }
                            else{
                                address = address+", "+aType;
                            }
                            k++;
                        }
                        bundle.putString("address", address);
                        bundle.putDouble("lat", latitude);
                        bundle.putDouble("lng",longitude);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();

                        bundle.putString("address", "Latitude: "+latitude+", Longitude: "+longitude);
                        bundle.putDouble("lat",latitude);
                        bundle.putDouble("lng",longitude);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
    public static void getAddressFromLocation(final String date,final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    message.what = 2;
                    Bundle bundle = new Bundle();
                    bundle.putString("date",date);
                    message.setData(bundle);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();

                        String[] addressArray = result.split("\n");
                        String address = "";
                        int k = 0;
                        for(String aType: addressArray){
                            if(k==0){
                                address = aType;
                            }
                            else if(k==(addressArray.length-3)){
                                if(!aType.equalsIgnoreCase("null")) {
                                    address = address + "\n " + aType;
                                }
                            }else if(k>=(addressArray.length-2)){
                                if(!aType.equalsIgnoreCase("null")) {
                                    address = address + "\n " + aType;
                                }
                            }
                            else{
                                address = address+", "+aType;
                            }
                            k++;
                        }
                        bundle.putString("address", address);
                        bundle.putDouble("lat", latitude);
                        bundle.putDouble("lng",longitude);
                        bundle.putString("date", date);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();

                        bundle.putString("address", "No Address Found");
                        bundle.putString("date",date);
                        bundle.putDouble("lat",latitude);
                        bundle.putDouble("lng",longitude);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }

    public static void getAddressFromLocationPanic(final String uuid,final double latitude, final double longitude,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            latitude, longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i)).append("\n");
                        }
                        sb.append(address.getLocality()).append("\n");
                        sb.append(address.getPostalCode()).append("\n");
                        sb.append(address.getCountryName());
                        result = sb.toString();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Unable connect to Geocoder", e);
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    message.what = 2;
                    message.what = 1;
                    Bundle bundle = new Bundle();

                    bundle.putString("address", "Latitude:"+latitude+", Longtitude: "+longitude);
                    bundle.putString("uid", uuid);
                    bundle.putDouble("lat",latitude);
                    bundle.putDouble("lng", longitude);
                    message.setData(bundle);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (result != null) {
                        message.what = 1;
                        Bundle bundle = new Bundle();

                        String[] addressArray = result.split("\n");
                        String address = "";
                        int k = 0;
                        for(String aType: addressArray){
                            if(k==0){
                                address = aType;
                            }
                            else if(k==(addressArray.length-3)){
                                if(!aType.equalsIgnoreCase("null")) {
                                    address = address + "\n " + aType;
                                }
                            }else if(k>=(addressArray.length-2)){
                                if(!aType.equalsIgnoreCase("null")) {
                                    address = address + "\n " + aType;
                                }
                            }
                            else{
                                address = address+", "+aType;
                            }
                            k++;
                        }
                        bundle.putString("address", address);
                        bundle.putDouble("lat", latitude);
                        bundle.putDouble("lng",longitude);
                        bundle.putString("uid",uuid);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();

                        bundle.putString("address", "Latitude:"+latitude+", Longtitude: "+longitude);
                        bundle.putString("uid",uuid);
                        bundle.putDouble("lat",latitude);
                        bundle.putDouble("lng",longitude);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}
