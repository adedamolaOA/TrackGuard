package ade.leke.com.trackguard.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.ContactActivity;
import ade.leke.com.trackguard.R;

/**
 * Created by SecureUser on 9/18/2015.
 */
public class CustomAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    ContactListModel tempValues=null;
    AppLocationService appLocationService;
    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public CustomAdapter(Activity a, ArrayList d,Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;
        appLocationService = new AppLocationService(
                a);


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

        public TextView name;
        public TextView mobileNumber;
        public LinearLayout textWide;
        public ImageView image;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.contact_list, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            Typeface mgFont = Typeface.createFromAsset(res.getAssets(),"fonts/exo_medium.otf");
            holder = new ViewHolder();
            holder.name = (TextView) vi.findViewById(R.id.lblName);
            holder.mobileNumber=(TextView)vi.findViewById(R.id.lblMobileNumber);
            holder.image=(ImageView)vi.findViewById(R.id.image);
            holder.name.setTypeface(mgFont);
            holder.mobileNumber.setTypeface(mgFont);

            holder.textWide = (LinearLayout)vi.findViewById(R.id.listDisplayLayout);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.name.setText("No Data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            tempValues=null;
            tempValues = ( ContactListModel ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.name.setText( tempValues.getName() );
            holder.mobileNumber.setText(tempValues.getMobileNumber());
            if(tempValues.getImage().equalsIgnoreCase("B")){
                //Color color = new Color();
                holder.textWide.setBackgroundColor(res.getColor(R.color.selected_green));

            }else{
                holder.textWide.setBackgroundColor(res.getColor(R.color.color_selected));
            }
            /*holder.image.setImageResource(
                    res.getIdentifier(
                            "com.androidexample.customlistview:drawable/contact_image.png"
                            ,null,null));//+tempValues.getImage()

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener( position ));
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


            ContactActivity sct = (ContactActivity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }
}
