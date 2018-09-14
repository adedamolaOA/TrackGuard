package ade.leke.com.trackguard.model;

/**
 * Created by SecureUser on 11/2/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ade.leke.com.trackguard.common.AppLocationService;
import ade.leke.com.trackguard.PanicLocationDirectionMap;
import ade.leke.com.trackguard.R;

/**
 * Created by SecureUser on 9/18/2015.
 */
public class PanicAdapter extends BaseAdapter implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    PanicListModel tempValues=null;
    AppLocationService appLocationService;
    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public PanicAdapter(Activity a, ArrayList d,Resources resLocal) {

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

        public TextView date;
        public TextView address;


    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.panic_list_layout, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            Typeface mgFont = Typeface.createFromAsset(res.getAssets(),"fonts/exo_medium.otf");
            holder = new ViewHolder();
            holder.date = (TextView) vi.findViewById(R.id.lblPanicMovementDate);
            holder.address=(TextView)vi.findViewById(R.id.lblPanicMovementAddress);

            holder.date.setTypeface(mgFont);
            holder.address.setTypeface(mgFont);
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.address.setText("No Panic Information");

        }
        else
        {
            tempValues=null;
            tempValues = ( PanicListModel ) data.get( position );

            holder.date.setText( tempValues.getDate() );
            holder.address.setText(tempValues.getAddress());

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


            PanicLocationDirectionMap sct = (PanicLocationDirectionMap)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(mPosition);
        }
    }
}
