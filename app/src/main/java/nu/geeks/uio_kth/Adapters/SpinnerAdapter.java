package nu.geeks.uio_kth.Adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hannes on 2016-02-19.
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    Typeface icons = Typeface.createFromAsset(getContext().getAssets(),
            "icons3.ttf");

    public SpinnerAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    //Set font on the closed version of the spinner.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(icons);
        //view.setTextSize(80);
        //view.setTextColor(Color.BLACK);
        //view.setBackgroundColor(Color.argb(1,0,0,0));
        return view;
    }

    //Set font on the open version of the spinner.
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(icons);
        view.setTextSize(80);
        view.setTextColor(Color.BLACK);
        return view;
    }
}
