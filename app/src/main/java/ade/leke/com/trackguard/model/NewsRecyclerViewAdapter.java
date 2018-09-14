package ade.leke.com.trackguard.model;

/**
 * Created by SecureUser on 1/29/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

        import java.util.ArrayList;

import ade.leke.com.trackguard.Main2Activity;
import ade.leke.com.trackguard.MainActivity;
import ade.leke.com.trackguard.MovementMainActivity;
import ade.leke.com.trackguard.NewsDisplayActivity;
import ade.leke.com.trackguard.R;

public class NewsRecyclerViewAdapter extends RecyclerView
        .Adapter<NewsRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<NewsDataObject> mDataset;
    private static MyClickListener myClickListener;
    private static Activity activity;


    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        TextView timeAgo;
        ImageView imageNews;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);

            timeAgo = (TextView) itemView.findViewById(R.id.lblTimeAgo);
            imageNews = (ImageView) itemView.findViewById(R.id.imageNews);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Main2Activity sct = (Main2Activity)activity;

            /****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****/

            sct.onItemClick(this.getPosition());



        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {

        this.myClickListener = myClickListener;
    }

    public NewsRecyclerViewAdapter(Activity a,ArrayList<NewsDataObject> myDataset) {
        mDataset = myDataset;
        activity = a;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.label.setText(mDataset.get(position).getSubject());
        holder.dateTime.setText(mDataset.get(position).getNews().substring(0, 30));
        holder.timeAgo.setText(mDataset.get(position).getTimeAgo());
        byte[] newsImg =null;
        try {
            newsImg = Base64.decode(mDataset.get(position).getImage(),Base64.DEFAULT);
            holder.imageNews.setImageBitmap(BitmapFactory.decodeByteArray(newsImg,0,newsImg.length));

        }catch (Exception e){
e.printStackTrace();
        }
       }

    public void addItem(NewsDataObject dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }


    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener{
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {


                    }
    }

}