package nu.geeks.uio_kth.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import nu.geeks.uio_kth.Database.DataProvider;
import nu.geeks.uio_kth.R;

/**
 * Created by Micke on 2016-02-17.
 */
public class ProjectDataAdapter extends ArrayAdapter {

    List list = new ArrayList();


    public ProjectDataAdapter(Context context, int resource) {
        super(context, resource);
    }

    static class LayoutHandler{
        TextView PROJECT_NAME,PROJECT_PASSWORD,PROJECT_ID;

    }

    @Override
    public void clear() {
        super.clear();
        list.clear();
    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutHandler layoutHandler;
        Typeface caviarDreams = Typeface.createFromAsset(this.getContext().getAssets(),"CaviarDreams.ttf");
        Typeface tower = Typeface.createFromAsset(this.getContext().getAssets(),"HTOWERT.TTF");
        if(row == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.list_item,parent,false);
            layoutHandler = new LayoutHandler();
            layoutHandler.PROJECT_NAME = (TextView) row.findViewById(R.id.tv_project_name);
            //layoutHandler.PROJECT_PASSWORD = (TextView) row.findViewById(R.id.tv_project_password);
            layoutHandler.PROJECT_ID = (TextView) row.findViewById(R.id.tv_project_id);
            layoutHandler.PROJECT_NAME.setTypeface(caviarDreams);
            layoutHandler.PROJECT_ID.setTypeface((tower));


            row.setTag(layoutHandler);
        } else{
            layoutHandler = (LayoutHandler) row.getTag();

        }
        DataProvider dataProvider = (DataProvider) this.getItem(position);
        layoutHandler.PROJECT_NAME.setText(dataProvider.getProjectName());
        //layoutHandler.PROJECT_PASSWORD.setText(dataProvider.getProjectPassword());
        layoutHandler.PROJECT_ID.setText(dataProvider.getProjectId());

        return row;
    }


}
