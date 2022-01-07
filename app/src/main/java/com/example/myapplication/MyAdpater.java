package com.example.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyAdpater extends BaseAdapter {

    Context context;
    List<MyCity> mCountries;
    private LayoutInflater mLayoutInflater;
    private List<MyCity> arraylist;
    Callback callback;
    Typeface type_normal;
    public MyAdpater(Context mContext, List<MyCity> mCountries) {
        this.context=mContext;
        this.mCountries=mCountries;
        //   mLayoutInflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<MyCity>();
        this.arraylist.addAll(mCountries);
        //type_normal= Typeface.createFromAsset(mContext.getAssets(), "font/Montserrat-Regular.ttf");
    }

    @Override
    public int getCount() {
        return mCountries.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_country_drop, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mNameView = (TextView) convertView.findViewById(R.id.intl_phone_edit__country__item_name);
            viewHolder.mDialCode = (TextView) convertView.findViewById(R.id.intl_phone_edit__country__item_dialcode);
            viewHolder.parent_rwoclick=(LinearLayout)convertView.findViewById(R.id.parent_rwoclick);
            viewHolder.mImageView=(ImageView)convertView.findViewById(R.id.flagimage) ;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
       // viewHolder.mImageView.setImageResource(mCountries.get(position).getImageflag());
        viewHolder.mNameView.setText(mCountries.get(position).getName());
        viewHolder.mDialCode.setText(mCountries.get(position).getState());
       // viewHolder.mDialCode.setText(String.format("+%s", mCountries.get(position).getDialCode()));
        /*viewHolder.mNameView.setTypeface(type_normal);
        viewHolder.mDialCode.setTypeface(type_normal);*/

        viewHolder.parent_rwoclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callback!=null)
                {
                    int flad_id=getFlagResource(mCountries.get(position));
                    String state=mCountries.get(position).getState();
                    String city=mCountries.get(position).getName();
                    String lat=mCountries.get(position).getLat();
                    String lon=mCountries.get(position).getLon();

                    callback.clickaction(flad_id,state,city,lat,lon);
                }
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        public ImageView mImageView;
        public TextView mNameView;
        public TextView mDialCode;
        LinearLayout parent_rwoclick;
    }
    public  void onCallBackReturn(Callback callback)
    {
        this.callback=callback;
    }

    public interface Callback{
        void clickaction(int position, String dialcode, String countryname,String lat, String lon);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mCountries.clear();
        if (charText.length() == 0) {
            mCountries.addAll(arraylist);
        }
        else
        {
            for (MyCity wp : arraylist)
            {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    mCountries.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }


    private int getFlagResource(MyCity country) {
        return context.getResources().getIdentifier("country_" +
                country.getState().toLowerCase(), "drawable", context.getPackageName());
        // System.out.println("countrycode"+getContext().getResources().getDialCode());
    }
}

