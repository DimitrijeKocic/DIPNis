package com.example.dika.dipnis;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Laza on 24-Sep-17.
 */

class ListItemP {
    public String id, tipProblema, opis, lokacija;
    public Bitmap img;

    public ListItemP(String i, String tp, String o, String l, Bitmap img) {
        this.id = i;
        this.tipProblema = tp;
        this.opis = o;
        this.lokacija = l;
        this.img = img;
    }
}

public class MyAdapterP extends ArrayAdapter<ListItemP> {

    ArrayList<ListItemP> problemsList = new ArrayList<>();

    public MyAdapterP(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<ListItemP> objects) {
        super(context, resource, objects);
        problemsList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.p_list_item_layout, null);
        TextView tvTipProblema = (TextView) v.findViewById(R.id.PListItemTvTipProblema);
        TextView tvOpis = (TextView) v.findViewById(R.id.PListItemTvOpis);
        TextView tvLokacija = (TextView) v.findViewById(R.id.PListItemTvLokacija);
        TextView tvId = (TextView) v.findViewById(R.id.PListItemTvId);
        ImageView ivImg = (ImageView) v.findViewById(R.id.PListItemIvImage);
        tvTipProblema.setText(problemsList.get(position).tipProblema);
        tvOpis.setText(problemsList.get(position).opis);
        tvLokacija.setText(problemsList.get(position).lokacija);
        tvId.setText(problemsList.get(position).id);
        ivImg.setImageBitmap(problemsList.get(position).img);
        return v;

    }
}
