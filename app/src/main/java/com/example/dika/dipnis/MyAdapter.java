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
 * Created by Laza on 23-Sep-17.
 */

class ListItem {
    public String id, vrstaIzvodjacRazlog, datumVreme, kratakOpis, lokacija;
    public Bitmap icon;

    public ListItem(String i, String vir, String dv, String ko, String l, Bitmap ic) {
        this.id = i;
        this.vrstaIzvodjacRazlog = vir;
        this.datumVreme = dv;
        this.kratakOpis = ko;
        this.lokacija = l;
        this.icon = ic;
    }
}

public class MyAdapter extends ArrayAdapter<ListItem> {

    ArrayList<ListItem> itemsList = new ArrayList<>();

    public MyAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<ListItem> objects) {
        super(context, resource, objects);
        itemsList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.ei_list_item_layout, null);
        TextView tvVrstaIzvodjacRazlog = (TextView) v.findViewById(R.id.EIListItemTvVrstaIzvodjacRazlog);
        TextView tvDatumVreme = (TextView) v.findViewById(R.id.EIListItemTvDatumVreme);
        TextView tvKratakOpis = (TextView) v.findViewById(R.id.EIListItemTvKratakOpis);
        TextView tvLokacija = (TextView) v.findViewById(R.id.EIListItemTvLokacija);
        TextView tvId = (TextView) v.findViewById(R.id.EIListItemTvId);
        ImageView ivIcon = (ImageView) v.findViewById(R.id.EIListItemIvIcon);
        tvVrstaIzvodjacRazlog.setText(itemsList.get(position).vrstaIzvodjacRazlog);
        tvDatumVreme.setText(itemsList.get(position).datumVreme);
        tvKratakOpis.setText(itemsList.get(position).kratakOpis);
        tvLokacija.setText(itemsList.get(position).lokacija);
        tvId.setText(itemsList.get(position).id);
        ivIcon.setImageBitmap(itemsList.get(position).icon);
        return v;

    }
}
