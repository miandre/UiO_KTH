package nu.geeks.uio_kth;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import nu.geeks.uio_kth.Database.ProjectDbHelper;
import nu.geeks.uio_kth.Database.TransactionsDbHelper;

/**
 * Created by Hannes on 2016-02-19.
 */
public class ProjectContentView extends Activity implements View.OnClickListener{

    private int projectPosition;
    private String projectId;

    //Database stuff
    TransactionsDbHelper transactionsDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursorProject, cursorContent;

    TextView projectName, totalExpenses;
    ArrayList<Transaction> transactions;

    Button add_transaction, ok_trans, cancel_trans;

    static final String TAG = "ContentView";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project_content);

        //Get the extras that contain the id from the main screen.
        Bundle b = getIntent().getExtras();
        projectPosition = b.getInt("project_id");
        Log.e("got id ", projectPosition + "");

        readTransactions();

        Typeface caviarBold = Typeface.createFromAsset(getAssets(), "CaviarDreams_Bold.ttf");

        projectName = (TextView) findViewById(R.id.tvProjectName); //Lol naming convention.
        totalExpenses = (TextView) findViewById(R.id.tv_total_expences);

        projectName.setTypeface(caviarBold);
        projectName.setText(getName(projectPosition));

        add_transaction = (Button) findViewById(R.id.bt_add_trans);
        add_transaction.setOnClickListener(this);




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
                transactions.add(transaction);
            } while (cursorContent.moveToNext());
        }

    }

    public void addTransaction(String amount, String object, String by){
        Transaction transaction = new Transaction(projectId, by, amount, object);
        transactionsDbHelper = new TransactionsDbHelper(this);
        sqLiteDatabase = transactionsDbHelper.getWritableDatabase();
        transactionsDbHelper.addInformation(projectId, by,amount,object,sqLiteDatabase);
        transactions.add(transaction);
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
                final EditText add_trans_object, add_trans_amount, add_trans_by_who;
                add_trans_by_who = (EditText) dialogLayout.findViewById(R.id.et_by_who);
                add_trans_object = (EditText) dialogLayout.findViewById(R.id.et_object);
                add_trans_amount = (EditText) dialogLayout.findViewById(R.id.et_amount);

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

                builder.show();
                break;

        }
    }

}
