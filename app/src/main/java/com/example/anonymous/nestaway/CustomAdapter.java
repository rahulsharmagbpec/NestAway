package com.example.anonymous.nestaway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by anonymous on 10/17/2015.
 */
public class CustomAdapter extends BaseAdapter
{
    Context context;
    ArrayList<House> list;
    private static LayoutInflater inflater=null;
    public CustomAdapter(MainActivity mainActivity, ArrayList<House> list)
    {
        this.list = list;
        context = mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.program_list, null);
        holder.title = (TextView) rowView.findViewById(R.id.title);
        holder.rent = (TextView) rowView.findViewById(R.id.rent);
        holder.image = (ImageView) rowView.findViewById(R.id.imageView);

        holder.title.setText(list.get(i).getTitle());
        holder.rent.setText(list.get(i).getRent() + "");
        Picasso.with(context).load(list.get(i).getImage()).into(holder.image);
        return rowView;
    }

    class Holder
    {
        TextView title;
        TextView rent;
        ImageView image;
    }
}
