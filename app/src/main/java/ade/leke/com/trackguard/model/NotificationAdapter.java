package ade.leke.com.trackguard.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ade.leke.com.trackguard.NotificationActivity;
import ade.leke.com.trackguard.R;
import ade.leke.com.trackguard.common.TypefaceSpan;


/**
 * Created by SecureUser on 9/18/2015.
 */
public class NotificationAdapter extends BaseAdapter implements View.OnClickListener {

    /***********
     * Declare Used Variables
     *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater = null;
    public Resources res;
    NotificationListModel tempValues = null;
    int i = 0;

    /*************
     * CustomAdapter Constructor
     *****************/
    public NotificationAdapter(Activity a, ArrayList d, Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data = d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /********
     * What is the size of Passed Arraylist Size
     ************/
    public int getCount() {

        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /*********
     * Create a holder Class to contain inflated xml file elements
     *********/
    public static class ViewHolder {

        public TextView subject;
        public TextView message;
        public TextView status;
        public TextView date;


    }

    /******
     * Depends upon data size called for each row , Create each ListView row
     *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.notification_layout, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            Typeface mgFont = Typeface.createFromAsset(res.getAssets(),"fonts/exo_medium.otf");
            holder = new ViewHolder();
            holder.subject = (TextView) vi.findViewById(R.id.lblNotificationSubject);
            holder.message = (TextView) vi.findViewById(R.id.lblNoficationMessage);
            holder.status = (TextView) vi.findViewById(R.id.lblStaus);
            holder.date = (TextView) vi.findViewById(R.id.lblNotifyDate);
            holder.message.setTypeface(mgFont);

            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        if (data.size() <= 0) {
            holder.subject.setText("");
            holder.message.setText("No Notification");
            holder.status.setBackgroundColor(Color.RED);

        } else {
            /***** Get each Model object from Arraylist ********/
            tempValues = null;
            tempValues = (NotificationListModel) data.get(position);

            /************  Set Model values in Holder elements ***********/

            SpannableString s = new SpannableString(tempValues.getSubject());
            s.setSpan(new TypefaceSpan(vi.getContext(), "exo_medium.otf"), 0, s.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
            holder.subject.setText(s);
            SpannableString s3 = new SpannableString(tempValues.getMessage());
            s3.setSpan(new TypefaceSpan(vi.getContext(), "exo_medium.otf"), 0, s3.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
            holder.message.setText(s3);
            SpannableString s2 = new SpannableString(tempValues.getDate());
            s2.setSpan(new TypefaceSpan(vi.getContext(), "exo_medium.otf"), 0, s2.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//space_age exo_light lobster
            holder.date.setText(s2);
            if (tempValues.getViewStatus().equalsIgnoreCase("R")) {
                holder.status.setBackgroundColor(Color.rgb(83, 89, 177));
                holder.subject.setTextColor(Color.rgb(83, 89, 177));
            }else if (tempValues.getViewStatus().equalsIgnoreCase("P")) {
                holder.status.setBackgroundColor(Color.rgb(183,44,44));
                holder.subject.setTextColor(Color.rgb(183, 44, 44));
            }else if (tempValues.getViewStatus().equalsIgnoreCase("L")) {
                holder.status.setBackgroundColor(Color.rgb(3,169,244));
                holder.subject.setTextColor(Color.rgb(3, 169, 244));
            } else {
                holder.status.setBackgroundColor(Color.rgb(139,195,74));
                holder.subject.setTextColor(Color.rgb(139,195,74));
            }
            /*holder.image.setImageResource(
                    res.getIdentifier(
                            "com.androidexample.customlistview:drawable/contact_image.png"
                            ,null,null));//+tempValues.getImage()

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener(position));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /*********
     * Called when Item click in ListView
     ************/
    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


            NotificationActivity sct = (NotificationActivity) activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }
}

