package nu.geeks.uio_kth.Adapters;


import android.content.Context;
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
            "icons.ttf");

    public SpinnerAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
    }

    //Set font on the closed version of the spinner.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(icons);
        return view;
    }

    //Set font on the open version of the spinner.
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(icons);
        return view;
    }
}
