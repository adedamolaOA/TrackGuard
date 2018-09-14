package ade.leke.com.trackguard.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ade.leke.com.trackguard.MovementActivity;
import ade.leke.com.trackguard.R;

/**
 * Created by SecureUser on 10/28/2015.
 */
public class MovementAdapter  extends BaseAdapter implements View.OnClickListener {
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    MovementListModel tempValues=null;
    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public MovementAdapter(Activity a, ArrayList d,Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView date;
        public TextView address;
        public TextView status;
        public ImageView image;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.movement_list_layout, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            Typeface mgFont = Typeface.createFromAsset(res.getAssets(),"fonts/exo_medium.otf");
            holder = new ViewHolder();
            holder.date = (TextView) vi.findViewById(R.id.lblMovementDate);
            holder.address =(TextView)vi.findViewById(R.id.lblMovementAddress);

            holder.status = (TextView)vi.findViewById(R.id.lblStaus);
            holder.image = (ImageView)vi.findViewById(R.id.imgMovementStatus);
            holder.date.setTypeface(mgFont);
            holder.address.setTypeface(mgFont);


            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.address.setText("No Movement Data Found");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( MovementListModel ) data.get( position );

            /************  Set Model values in Holder elements ***********/


            holder.date.setText(tempValues.getDate());
            holder.address.setText(tempValues.getAddress());
            if(tempValues.getAddress().equalsIgnoreCase("No Address Found")){
                holder.image.setImageResource(res.getIdentifier(
                        "ade.leke.com.trackguard:drawable/loc_new_red"
                        ,null,null));
                holder.status.setBackgroundColor(Color.rgb(255, 84, 84));
            }else{
                holder.image.setImageResource(res.getIdentifier(
                        "ade.leke.com.trackguard:drawable/loc_new_greens"
                        ,null,null));
                holder.status.setBackgroundColor(Color.rgb(139,195,74));
            }

            vi.setOnClickListener(new OnItemClickListener(position));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener{
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


            MovementActivity sct = (MovementActivity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            //tvAddress.setText(locationAddress);
        }
    }
}
