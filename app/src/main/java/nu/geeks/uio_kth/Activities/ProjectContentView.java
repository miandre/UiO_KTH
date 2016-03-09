package nu.geeks.uio_kth.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nu.geeks.uio_kth.Adapters.ProjectContentAdapter;
import nu.geeks.uio_kth.Database.GetChatCallback;
import nu.geeks.uio_kth.Database.GetProjectCallback;
import nu.geeks.uio_kth.Database.GetTransactionCallback;
import nu.geeks.uio_kth.Database.ProjectDbHelper;
import nu.geeks.uio_kth.Database.ServerRequest;
import nu.geeks.uio_kth.Database.TransactionsDbHelper;
import nu.geeks.uio_kth.Objects.Algorithms;
import nu.geeks.uio_kth.Objects.ChatMessage;
import nu.geeks.uio_kth.Objects.DataProvider;
import nu.geeks.uio_kth.Objects.Payment;
import nu.geeks.uio_kth.Objects.Person;
import nu.geeks.uio_kth.Objects.User;
import nu.geeks.uio_kth.R;
import nu.geeks.uio_kth.Objects.Transaction;
import nu.geeks.uio_kth.Views.PopupViews;

/**
 * Created by Hannes on 2016-02-19.
 */
public class ProjectContentView extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

    //TODO Show current status (+/- amount) for each person in the list view.
    //Perhaps add a value to the persons object?


    private int projectPosition;
    private String projectId;
    private String projectNameString;

    //Database stuff
    TransactionsDbHelper transactionsDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursorProject, cursorContent;

    TextView projectName, totalExpenses, tv_by_who, tv_what, tv_how_much;


    ArrayList<Transaction> transactions; //This holds all the transactions.
    ArrayList<Person> persons; //This holds one instance of all people in this project, and their total sum.

    Button add_transaction, bShare, calculate, bBack, bChat;
    Typeface caviarBold;

    static final String TAG = "ContentView";
    private final String LOCAL_STORAGE = "LOCALE_STORAGE";
    static final String DEFAULT_USER = "default_user";
    static final String CHAT_LENGTH = "chat_length";



    SharedPreferences localStorage,chatLengthStorage;
    SharedPreferences.Editor spEditor,chatEditor;

    ProjectContentAdapter projectContentAdapter;
    ExpandableListView expandableListView;

    int chatLength;

    CountDownTimer timer;

    final Animation animation = new AlphaAnimation(1.0f, 0.5f);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localStorage = this.getSharedPreferences(LOCAL_STORAGE, 0);
        spEditor = localStorage.edit();
        setContentView(R.layout.activity_project_content);




        Log.e(TAG, Integer.toString(localStorage.getInt("Micke", 0)));

        caviarBold = Typeface.createFromAsset(getAssets(), "CaviarDreams_Bold.ttf");

        //Get the extras that contain the id (actually cursor position) from the main screen.
        Bundle b = getIntent().getExtras();
        projectPosition = b.getInt("project_id");

        projectNameString = getName(projectPosition);

        projectName = (TextView) findViewById(R.id.tvProjectName); //Lol naming convention.
        totalExpenses = (TextView) findViewById(R.id.tv_total_expences);


        // font

        totalExpenses.setTypeface(caviarBold);
        totalExpenses.setTextColor(Color.WHITE);

        projectName.setTypeface(caviarBold);
        projectName.setText(projectNameString);
        projectName.setTextColor(Color.WHITE);


        add_transaction = (Button) findViewById(R.id.bt_add_trans);
        add_transaction.setOnClickListener(this);

        bShare = (Button) findViewById(R.id.bShare2);
        bShare.setOnClickListener(this);

        calculate = (Button) findViewById(R.id.bt_calculate);
        calculate.setOnClickListener(this);



        bChat = (Button) findViewById(R.id.bChat);
        bChat.setOnClickListener(this);

        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in



        chatLengthStorage = this.getSharedPreferences(projectId+"_chat", 0);
        chatEditor = chatLengthStorage.edit();

        chatLength = chatLengthStorage.getInt(CHAT_LENGTH,0);

        getOnlineData();

        timer = new CountDownTimer(10000, 3000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateOnlineData();

            }

            @Override
            public void onFinish() {
                start();
            }
        }.start();

    }

    @Override
    protected void onResume() {
        timer.start();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        timer.start();
        super.onRestart();
    }

    //Retreive project content from online database
    private void getOnlineData() {

        //Create service request
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchProjectContentInBackground(projectId, new GetTransactionCallback() {
            @Override
            public void done(ArrayList<Transaction> onlineTransactions) {
                //reference online data to local transaction list
                transactions = onlineTransactions;

                //Update lists of persons
                fillPersonList();
                //Create/set the list view
                setListView();
            }
        });
    }

    private void updateOnlineData() {

        //Create service request
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchProjectContentInBackground(projectId, new GetTransactionCallback() {
            @Override
            public void done(ArrayList<Transaction> onlineTransactions) {
                //reference online data to local transaction list
                if (onlineTransactions.size() > transactions.size()) {
                    transactions = onlineTransactions;
                    //Update lists of persons
                    fillPersonList();
                    //Create/set the list view
                    setListView();
                }
                checkForChatMessage(new ChatMessage("", "", projectId));
            }
        });
    }





    // Create the expandable listview showing persons and transactions.
    private void setListView() {

        expandableListView = (ExpandableListView) findViewById(R.id.simple_expandable_listview);

       projectContentAdapter = new ProjectContentAdapter(this,persons,transactions);

        expandableListView.setAdapter(projectContentAdapter);

        update();

    }

    //Add all persons ho have mad a transaction to the list of persons.

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

    //Get the name and the ID of the current project
    private String getName(int projectPosition){

        ProjectDbHelper dbHelper = new ProjectDbHelper(getApplicationContext());
        sqLiteDatabase = dbHelper.getReadableDatabase();
        cursorProject = dbHelper.getProjects(sqLiteDatabase);
        cursorProject.moveToPosition(projectPosition);
        String name = cursorProject.getString(0);
        projectId = cursorProject.getString(2);
        return name;
    }



    // read the expenses from the local database (not used atm)
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
                //Updating the list view after adding transaction.
                setListView();
            }

            @Override
            public void done(DataProvider projectToAdd) {
                Log.e(TAG, "Callback 2");
            }
        });

    }

    // recount total expenses (all people), make sure listview is up to date
    // called when adding expenses and when initializing the activity
    private void update(){

        float total = 0;
        for(Person p : persons){
            total += p.amount;
        }
        totalExpenses.setText("Total expences: " + total + " kr.");
        projectContentAdapter.notifyDataSetChanged();
    }

    //Getting the list of current members to generate outocomplete-list
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
            case R.id.bt_calculate:
                openCalculatePopup();
                break;
            case R.id.bChat:
                viewChat();
                bChat.clearAnimation();
                break;
        }
    }

    private void checkForChatMessage(ChatMessage msg) {
        //Create service request

        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.updateChatInBackground(msg, new GetChatCallback() {
            @Override
            public void done(ArrayList<ChatMessage> newChatContent) {
             if (newChatContent.size()>chatLengthStorage.getInt(CHAT_LENGTH,0)){
                 bChat.startAnimation(animation);

                }

            }
        });



    }

    private void openCalculatePopup() {
        //Calculate the debts
        Algorithms algorithms = new Algorithms();
        ArrayList<Payment> payments = algorithms.calculatePayments(persons);

        //Create a string of all payments.
        String pay = "";
        for(Payment p : payments)
        {
            pay += p.from + " should pay " + p.to + " " + p.amount + "\n\n";
        }
        PopupViews.PersonalExpensesView(this, caviarBold, pay);
    }

    //Wanted to do this as a static method in PopupViews, but since the addbutton calls another
    private void openAddTransactionPopup() {
        // make popup view
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.add_transaction, null);
        final AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setView(dialogLayout);

        tv_by_who = (TextView) dialogLayout.findViewById(R.id.tv_by_who);
        tv_what = (TextView) dialogLayout.findViewById(R.id.tv_what);
        tv_how_much = (TextView) dialogLayout.findViewById(R.id.tv_how_much);

        tv_by_who.setTypeface(caviarBold);
        tv_what.setTypeface(caviarBold);
        tv_how_much.setTypeface(caviarBold);



        //Initialize buttons end edittexts.
        Button ok = (Button) dialogLayout.findViewById(R.id.bt_ok_add_trans);
        Button cancel = (Button) dialogLayout.findViewById(R.id.bt_cancel_trans);
        String[] names = getNameList();
        final EditText add_trans_object, add_trans_amount;
        final AutoCompleteTextView add_trans_by_who;
        final ArrayAdapter<String> personAutoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);

        add_trans_by_who = (AutoCompleteTextView) dialogLayout.findViewById(R.id.et_by_who);
        add_trans_by_who.setTypeface(caviarBold);
        add_trans_by_who.setText(localStorage.getString(DEFAULT_USER, ""));

        add_trans_object = (EditText) dialogLayout.findViewById(R.id.et_object);
        add_trans_object.setTypeface(caviarBold);

        add_trans_amount = (EditText) dialogLayout.findViewById(R.id.et_amount);
        add_trans_amount.setTypeface(caviarBold);



        // auto completion
        //Remove hint when object is focused
        add_trans_by_who.setAdapter(personAutoAdapter);
        add_trans_by_who.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_trans_by_who.setHint("");
                add_trans_by_who.showDropDown();
            }
        });
        add_trans_by_who.setThreshold(1);

        //Remove hint when object is focused
        add_trans_object.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    add_trans_object.setHint("");
                } else {

                    add_trans_object.setHint("What did you buy?");
                }
            }
        });

        //Remove hint when object is focused
        add_trans_amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    add_trans_amount.setHint("");
                }else{
                    add_trans_amount.setHint("What did it cost?");
                }
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get text fields
                String amount = add_trans_amount.getText().toString();
                String object = add_trans_object.getText().toString();
                String byWho = add_trans_by_who.getText().toString();

                spEditor.putString(DEFAULT_USER,User.addName(byWho));
                spEditor.commit();

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

    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

    public void viewProjects(){

        startActivity(new Intent(this, ProjectView.class));
        finish();
    }

    public void viewChat(){
        Intent intent = new Intent(this,ChatView.class);
        intent.putExtra("project_id",projectId);
        intent.putExtra("project_names", getNameList());
        startActivity(intent);
        //finish();
    }

}

/*
*
* package nu.geeks.uio_kth.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Map;

import nu.geeks.uio_kth.Database.GetProjectCallback;
import nu.geeks.uio_kth.Database.GetTransactionCallback;
import nu.geeks.uio_kth.Database.ProjectDbHelper;
import nu.geeks.uio_kth.Database.ServerRequest;
import nu.geeks.uio_kth.Database.TransactionsDbHelper;
import nu.geeks.uio_kth.Objects.Algorithms;
import nu.geeks.uio_kth.Objects.DataProvider;
import nu.geeks.uio_kth.Objects.Payment;
import nu.geeks.uio_kth.Objects.Person;
import nu.geeks.uio_kth.Objects.User;
import nu.geeks.uio_kth.R;
import nu.geeks.uio_kth.Objects.Transaction;
import nu.geeks.uio_kth.Views.PopupViews;


public class ProjectContentView extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private int projectPosition;
    private String projectId;
    private String projectNameString;
*/
    /*
    //Database stuff
    TransactionsDbHelper transactionsDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursorProject, cursorContent;

    TextView projectName, totalExpenses, tv_by_who, tv_what, tv_how_much;


    ArrayList<Transaction> transactions; //This holds all the transactions.
    ArrayList<Person> persons; //This holds one instance of all people in this project, and their total sum.
    ArrayAdapter<Person> personArrayAdapter; //This is the array adapter for the list view.


    ListView listView;
    Button add_transaction, bShare, calculate, bBack, bChat;
    Typeface caviarBold;

    static final String TAG = "ContentView";
    private final String LOCAL_STORAGE = "LOCALE_STORAGE";
    static final String DEFAULT_USER = "default_user";


    SharedPreferences localStorage;
    SharedPreferences.Editor spEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localStorage = this.getSharedPreferences(LOCAL_STORAGE,0);
        spEditor = localStorage.edit();
        setContentView(R.layout.activity_project_content);


        Log.e(TAG, Integer.toString(localStorage.getInt("Micke", 0)));

        caviarBold = Typeface.createFromAsset(getAssets(), "CaviarDreams_Bold.ttf");

        //Get the extras that contain the id (actually cursor position) from the main screen.
        Bundle b = getIntent().getExtras();
        projectPosition = b.getInt("project_id");

        projectNameString = getName(projectPosition);

        projectName = (TextView) findViewById(R.id.tvProjectName); //Lol naming convention.
        totalExpenses = (TextView) findViewById(R.id.tv_total_expences);


        // font

        totalExpenses.setTypeface(caviarBold);
        totalExpenses.setTextColor(Color.WHITE);

        projectName.setTypeface(caviarBold);
        projectName.setText(projectNameString);
        projectName.setTextColor(Color.WHITE);


        add_transaction = (Button) findViewById(R.id.bt_add_trans);
        add_transaction.setOnClickListener(this);

        bShare = (Button) findViewById(R.id.bShare2);
        bShare.setOnClickListener(this);

        calculate = (Button) findViewById(R.id.bt_calculate);
        calculate.setOnClickListener(this);

        //    bBack = (Button) findViewById(R.id.bBack);
        //    bBack.setOnClickListener(this);

        bChat = (Button) findViewById(R.id.bChat);
        bChat.setOnClickListener(this);

        getOnlineData();

        //readTransactions();
        //fillPersonList();
        //setListView();

    }

    //Retreive projet content from online service
    private void getOnlineData() {

        //Create service request
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchProjectContentInBackground(projectId, new GetTransactionCallback() {
            @Override
            public void done(ArrayList<Transaction> onlineTransactions) {
                //reference online data to lokal transaction list
                transactions = onlineTransactions;

                //Update lists of persons
                fillPersonList();
                //Create/set the list view
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
            case R.id.bt_calculate:
                openCalculatePopup();
                break;
            //   case R.id.bBack:
            //       viewProjects();
            //       break;
            case R.id.bChat:
                viewChat();
                break;
        }
    }

    private void openCalculatePopup() {
        //Calculate the debts
        Algorithms algorithms = new Algorithms();
        ArrayList<Payment> payments = algorithms.calculatePayments(persons);

        //Create a string of all payments.
        String pay = "";
        for(Payment p : payments)
        {
            pay += p.from + " should pay " + p.to + " " + p.amount + "\n\n";
        }
        PopupViews.PersonalExpensesView(this, caviarBold, pay);
    }

    //Wanted to do this as a static method in PopupViews, but since the addbutton calls another
    private void openAddTransactionPopup() {
        // make popup view
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.add_transaction, null);
        final AlertDialog builder = new AlertDialog.Builder(this).create();
        builder.setView(dialogLayout);

        tv_by_who = (TextView) dialogLayout.findViewById(R.id.tv_by_who);
        tv_what = (TextView) dialogLayout.findViewById(R.id.tv_what);
        tv_how_much = (TextView) dialogLayout.findViewById(R.id.tv_how_much);

        tv_by_who.setTypeface(caviarBold);
        tv_what.setTypeface(caviarBold);
        tv_how_much.setTypeface(caviarBold);



        //Initialize buttons end edittexts.
        Button ok = (Button) dialogLayout.findViewById(R.id.bt_ok_add_trans);
        Button cancel = (Button) dialogLayout.findViewById(R.id.bt_cancel_trans);
        String[] names = getNameList();
        final EditText add_trans_object, add_trans_amount;
        final AutoCompleteTextView add_trans_by_who;
        final ArrayAdapter<String> personAutoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);

        add_trans_by_who = (AutoCompleteTextView) dialogLayout.findViewById(R.id.et_by_who);
        add_trans_by_who.setTypeface(caviarBold);
        add_trans_by_who.setText(localStorage.getString(DEFAULT_USER, ""));

        add_trans_object = (EditText) dialogLayout.findViewById(R.id.et_object);
        add_trans_object.setTypeface(caviarBold);

        add_trans_amount = (EditText) dialogLayout.findViewById(R.id.et_amount);
        add_trans_amount.setTypeface(caviarBold);



        // auto completion
        //Remove hint when object is focused
        add_trans_by_who.setAdapter(personAutoAdapter);
        add_trans_by_who.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_trans_by_who.setHint("");
                add_trans_by_who.showDropDown();
            }
        });
        add_trans_by_who.setThreshold(1);

        //Remove hint when object is focused
        add_trans_object.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    add_trans_object.setHint("");
                } else {

                    add_trans_object.setHint("What did you buy?");
                }
            }
        });

        //Remove hint when object is focused
        add_trans_amount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    add_trans_amount.setHint("");
                }else{
                    add_trans_amount.setHint("What did it cost?");
                }
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get text fields
                String amount = add_trans_amount.getText().toString();
                String object = add_trans_object.getText().toString();
                String byWho = add_trans_by_who.getText().toString();

                spEditor.putString(DEFAULT_USER,User.addName(byWho));
                spEditor.commit();

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

    public void viewChat(){
        Intent intent = new Intent(this,ChatView.class);
        intent.putExtra("project_id",projectId);
        intent.putExtra("project_names", getNameList());
        startActivity(intent);
        //finish();
    }

}
*
* */