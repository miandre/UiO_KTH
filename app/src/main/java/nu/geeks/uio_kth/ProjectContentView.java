package nu.geeks.uio_kth;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import nu.geeks.uio_kth.Database.ProjectDbHelper;
import nu.geeks.uio_kth.Database.TransactionsDbHelper;

/**
 * Created by Hannes on 2016-02-19.
 */
public class ProjectContentView extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private int projectPosition;
    private String projectId;
    private String projectNameString;

    //Database stuff
    TransactionsDbHelper transactionsDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursorProject, cursorContent;

    TextView projectName, totalExpenses;


    ArrayList<Transaction> transactions; //This holds all the transactions.
    ArrayList<Person> persons; //This holds one instance of all people in this project, and their total sum.
    ArrayAdapter<Person> personArrayAdapter; //This is the array adapter for the list view.


    ListView listView;
    Button add_transaction, bShare;
    Typeface caviarBold;


    static final String TAG = "ContentView";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project_content);

        //Get the extras that contain the id from the main screen.
        Bundle b = getIntent().getExtras();
        projectPosition = b.getInt("project_id");

        projectNameString = getName(projectPosition);

        caviarBold = Typeface.createFromAsset(getAssets(), "CaviarDreams_Bold.ttf");

        projectName = (TextView) findViewById(R.id.tvProjectName); //Lol naming convention.
        totalExpenses = (TextView) findViewById(R.id.tv_total_expences);
        totalExpenses.setTypeface(caviarBold);
        totalExpenses.setTextColor(Color.WHITE);

        projectName.setTypeface(caviarBold);
        projectName.setText(projectNameString);
        projectName.setTextColor(Color.WHITE);

        readTransactions();
        fillPersonList();

        setListView();


        add_transaction = (Button) findViewById(R.id.bt_add_trans);
        add_transaction.setOnClickListener(this);

        bShare = (Button) findViewById(R.id.bShare2);
        bShare.setOnClickListener(this);

    }

    private void setListView() {
        listView = (ListView) findViewById(R.id.lv_persons);

        personArrayAdapter = new ArrayAdapter<Person>(this, android.R.layout.simple_list_item_2, android.R.id.text1, persons){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                TextView person = (TextView) v.findViewById(android.R.id.text1);
                TextView amount = (TextView) v.findViewById(android.R.id.text2);
                person.setTypeface(caviarBold);
                amount.setTypeface(caviarBold);
                person.setTextColor(Color.WHITE);
                amount.setTextColor(Color.WHITE);
                person.setText(persons.get(position).name);
                amount.setText(persons.get(position).amount + " kr");
                return v;
            }
        };
        listView.setAdapter(personArrayAdapter);
        update();


        listView.setOnItemClickListener(this);
    }

    private void fillPersonList(){

        persons = new ArrayList<>();
        for(Transaction t : transactions) {
            boolean personInArray = false;
            for (Person p : persons) {
                if (p.isSame(t.person)) {
                    p.amount += t.amount;
                    personInArray = true;
                }
            }

            if (!personInArray) {
                persons.add(new Person(t.person, t.amount));
            }
        }

    }



    private String getName(int projectPosition){


        ProjectDbHelper dbHelper = new ProjectDbHelper(getApplicationContext());
        sqLiteDatabase = dbHelper.getReadableDatabase();
        cursorProject = dbHelper.getProjects(sqLiteDatabase);
        cursorProject.moveToPosition(projectPosition);
        String name = cursorProject.getString(0);
        projectId = cursorProject.getString(2);
        return name;
    }





    private void readTransactions(){

        transactionsDbHelper = new TransactionsDbHelper(this);
        sqLiteDatabase = transactionsDbHelper.getReadableDatabase();
        cursorContent = transactionsDbHelper.getInformation(sqLiteDatabase);
        transactions = new ArrayList<>();

        if (cursorContent.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(cursorContent.getString(0), cursorContent.getString(1), cursorContent.getString(2), cursorContent.getString(3));
                //If this transaction belongs to this project.
                if(transaction.projectId.equals(projectId)) {
                    transactions.add(transaction);
                    Log.e(TAG, "Added person: " + transaction.person);
                }else{
                    Log.e(TAG, "Not in this project: " + transaction.projectId + "!=" + projectId);
                }

            } while (cursorContent.moveToNext());
        }

    }

    public void addTransaction(String amount, String object, String by){
        Transaction transaction = new Transaction(projectId, by, amount, object);
        transactionsDbHelper = new TransactionsDbHelper(this);
        sqLiteDatabase = transactionsDbHelper.getWritableDatabase();
        transactionsDbHelper.addInformation(projectId, by, amount, object, sqLiteDatabase);
        transactions.add(transaction);

        boolean isInPersons = false;
        for(Person p : persons){
            if(p.isSame(by)){
                p.amount += transaction.amount;
                isInPersons = true;
            }
        }

        if(!isInPersons) persons.add(new Person(transaction.person, transaction.amount));
        update();
    }


    private void update(){

        float total = 0;
        for(Person p : persons){
            total += p.amount;
        }
        totalExpenses.setText("Total expences: " + total + " kr.");
        personArrayAdapter.notifyDataSetChanged();
    }

    public String[] getNameList(){
        String[] names = new String[persons.size()];
        int i = 0;
        for(Person p : persons){
            names[i]= p.name;
            i++;
        }
        return names;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_add_trans:
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.add_transaction, null);
                final AlertDialog builder = new AlertDialog.Builder(this).create();
                builder.setView(dialogLayout);

                //Initialize buttons end edittexts.
                Button ok = (Button) dialogLayout.findViewById(R.id.bt_ok_add_trans);
                Button cancel = (Button) dialogLayout.findViewById(R.id.bt_cancel_trans);
                String[] names = getNameList();
                final EditText add_trans_object, add_trans_amount;
                final AutoCompleteTextView add_trans_by_who;
                final ArrayAdapter<String> personAutoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);

                add_trans_by_who = (AutoCompleteTextView) dialogLayout.findViewById(R.id.et_by_who);
                add_trans_object = (EditText) dialogLayout.findViewById(R.id.et_object);
                add_trans_amount = (EditText) dialogLayout.findViewById(R.id.et_amount);
                add_trans_by_who.setAdapter(personAutoAdapter);
                add_trans_by_who.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        add_trans_by_who.showDropDown();
                    }
                });
                add_trans_by_who.setThreshold(1);


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String amount = add_trans_amount.getText().toString();
                        String object = add_trans_object.getText().toString();
                        String byWho = add_trans_by_who.getText().toString();
                        if (amount.equals("") || object.equals("") || byWho.equals("")){
                            Toast.makeText(getApplicationContext(), "You have to add something", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "okButton failed");
                        }else{
                            Log.e(TAG, "okButton OK");
                            addTransaction(amount, object, byWho);
                            builder.dismiss();
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });

                builder.show();
                break;

            case R.id.bShare2:
                openShareView();
                break;

        }
    }

    public void openShareView(){

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.share_view, null);
        final AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setView(dialogLayout);

        Button ok = (Button) dialogLayout.findViewById(R.id.bDoneShare);
        TextView text = (TextView)dialogLayout.findViewById(R.id.tv_share_text);
        text.setTypeface(caviarBold);
        text.setTextColor(Color.WHITE);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.show();

    }

    @Override
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String name = persons.get(position).name;
        String msg = name + "'s all transactions:\n";
        for(Transaction t : transactions){
            if(t.projectId.equals(projectId)){
                if(t.person.equals(name)){
                    msg += t.object + " for " + t.amount + "\n";
                }
            }
        }

        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.single_person_transactions, null);
        final AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setView(dialogLayout);

        Button ok = (Button) dialogLayout.findViewById(R.id.tv_single_person_ok);
        TextView text = (TextView)dialogLayout.findViewById(R.id.tv_single_person_transactions);
        text.setTypeface(caviarBold);
        text.setTextColor(Color.WHITE);
        text.setText(msg);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.show();
    }
}
