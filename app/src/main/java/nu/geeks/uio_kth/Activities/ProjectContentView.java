package nu.geeks.uio_kth.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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

import nu.geeks.uio_kth.Database.GetProjectCallback;
import nu.geeks.uio_kth.Database.GetTransactionCallback;
import nu.geeks.uio_kth.Database.ProjectDbHelper;
import nu.geeks.uio_kth.Database.ServerRequest;
import nu.geeks.uio_kth.Database.TransactionsDbHelper;
import nu.geeks.uio_kth.Objects.DataProvider;
import nu.geeks.uio_kth.Objects.Person;
import nu.geeks.uio_kth.R;
import nu.geeks.uio_kth.Objects.Transaction;
import nu.geeks.uio_kth.Views.PopupViews;

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

        //Get the extras that contain the id (actually cursor position) from the main screen.
        Bundle b = getIntent().getExtras();
        projectPosition = b.getInt("project_id");

        projectNameString = getName(projectPosition);

        projectName = (TextView) findViewById(R.id.tvProjectName); //Lol naming convention.
        totalExpenses = (TextView) findViewById(R.id.tv_total_expences);

        // font
        caviarBold = Typeface.createFromAsset(getAssets(), "CaviarDreams_Bold.ttf");
        totalExpenses.setTypeface(caviarBold);
        totalExpenses.setTextColor(Color.WHITE);

        projectName.setTypeface(caviarBold);
        projectName.setText(projectNameString);
        projectName.setTextColor(Color.WHITE);


        add_transaction = (Button) findViewById(R.id.bt_add_trans);
        add_transaction.setOnClickListener(this);

        bShare = (Button) findViewById(R.id.bShare2);
        bShare.setOnClickListener(this);

        getOnlineData();
        //readTransactions();
       // fillPersonList();

        //setListView();





    }

    private void getOnlineData() {
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchProjectContentInBackground(projectId, new GetTransactionCallback() {
            @Override
            public void done(ArrayList<Transaction> onlineTransactions) {
                transactions=onlineTransactions;
                fillPersonList();
                setListView();
            }
        });
    }

    // expenses list (totals)
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





    // read the expenses
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

    public void addTransaction(Transaction transaction){

        transactionsDbHelper = new TransactionsDbHelper(this);
        sqLiteDatabase = transactionsDbHelper.getWritableDatabase();
        transactionsDbHelper.addInformation(transaction, sqLiteDatabase);
        transactions.add(transaction);

        boolean isInPersons = false;
        for(Person p : persons){
            if(p.isSame(transaction.person)){
                p.amount += transaction.amount;
                isInPersons = true;
            }
        }

        if(!isInPersons) persons.add(new Person(transaction.person, transaction.amount));
        update();
        transactionsDbHelper.close();
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.storeTransactionDataInBackground(transaction, new GetProjectCallback() {
            @Override
            public void done(int projectPosition) {

            }

            @Override
            public void done(DataProvider projectToAdd) {

            }
        });
        update();
    }

    // recount total expenses (all people), make sure listview is up to date
    // called when adding expenses and when initializing the activity
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

                openAddTransactionPopup();


                break;

            case R.id.bShare2:
                openShareView();
                break;

        }
    }

    //Wanted to do this as a static method in PopupViews, but since the addbutton calls another
    private void openAddTransactionPopup() {
        // make popup view
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

        // auto completion
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
                // get text fields
                String amount = add_trans_amount.getText().toString();
                String object = add_trans_object.getText().toString();
                String byWho = add_trans_by_who.getText().toString();

                // verify not empty
                if (amount.equals("") || object.equals("") || byWho.equals("")){
                    Toast.makeText(getApplicationContext(), "You have to add something", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "okButton failed");
                }else{
                    Log.e(TAG, "okButton OK");
                    // add expense
                    Transaction transaction = new Transaction(projectId, byWho, amount, object);
                    addTransaction(transaction);
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
    }

    public void openShareView(){
        PopupViews.ShareView(this, caviarBold, projectId);
    }

    @Override
    // list for one persons expenses
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



        // open as popup
        PopupViews.PersonalExpensesView(this, caviarBold, msg);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        viewProjects();
    }

    public void viewProjects(){

        startActivity(new Intent(this, ProjectView.class));
        finish();
    }
}
