package nu.geeks.uio_kth.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nu.geeks.uio_kth.Objects.Person;
import nu.geeks.uio_kth.Objects.Transaction;
import nu.geeks.uio_kth.R;

/**
 * Created by Hannes on 2016-02-21.
 */
public class ProjectContentAdapter extends BaseExpandableListAdapter {



    private final String TAG = "CONTENT_ADAPTER";
    private Context context;
    private ArrayList<Person> persons; // header titles
    // Child data in format of header title, child title
    private ArrayList<Transaction> transactions;

    private HashMap<String, ArrayList<Transaction>> child;

    public ProjectContentAdapter(Context context, ArrayList<Person> persons, ArrayList<Transaction> transactions) {
        this.context = context;
        this.persons = persons;
        this.transactions = transactions;
        this.child = new HashMap<>();
        generateData();
    }


    //Method to generate a HashMap handled by the adapter when generating the expandable
    //list view
    private void generateData(){
        ArrayList<Transaction> temp = new ArrayList<>();

        for (Person p: this.persons) {
            for (Transaction t:this.transactions) {
                if (p.name.equals(t.person)){
                    //Log.e(TAG,t.object);
                    temp.add(t);
                }

            }

            this.child.put(p.name,(ArrayList<Transaction>)temp.clone());
            temp.clear();

        }

    }

    @Override
    public int getGroupCount() {
        return this.persons.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childCount=0;
        for (Transaction t:transactions) {
            if(t.person.equals(this.persons.get(groupPosition).name))
                childCount++;
        }
        return this.child.get(this.persons.get(groupPosition).name).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.persons.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.child.get(this.persons.get(groupPosition).name).get(
                childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    // The group view (the view with name and total sum) is generated here.
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        // Getting header title

        Typeface caviarDreamsBold = Typeface.createFromAsset(this.context.getAssets(), "CaviarDreams_Bold.ttf");
        Typeface caviarDreams = Typeface.createFromAsset(this.context.getAssets(), "CaviarDreams.ttf");

        Person headerTitle =  (Person) getGroup(groupPosition);


        // Inflating header layout and setting text
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView header_text = (TextView) convertView.findViewById(android.R.id.text1);
        TextView header_number = (TextView) convertView.findViewById(android.R.id.text2);

        header_text.setText(headerTitle.name);
        header_number.setText(Float.toString(headerTitle.amount));
        header_text.setTextColor(Color.WHITE);
        header_number.setTextColor(Color.WHITE);

        // If group is expanded then change the text into bold and change the
        // icon
        if (isExpanded) {
            header_text.setTypeface(caviarDreamsBold, Typeface.BOLD);
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.up, 0);
        } else {
            // If group is not expanded then change the text back into normal
            // and change the icon

            header_text.setTypeface(caviarDreams, Typeface.NORMAL);
            header_text.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.down, 0);
        }

        return convertView;
    }


    //The child view (the view with individual objects and their cost) is generated here.
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Typeface caviarDreams = Typeface.createFromAsset(this.context.getAssets(), "CaviarDreams.ttf");

        // Getting child text
        final Transaction childText = (Transaction) getChild(groupPosition, childPosition);

        // Inflating child layout and setting textviews
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_layout, parent, false);
        }

        TextView child_object = (TextView) convertView.findViewById(R.id.child_object);
        TextView child_cost = (TextView) convertView.findViewById(R.id.child_cost);

        child_object.setText(childText.object);
        child_cost.setText(Float.toString(childText.amount));

        child_object.setTextColor(Color.WHITE);
        child_cost.setTextColor(Color.WHITE);

        child_object.setTypeface(caviarDreams);


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
