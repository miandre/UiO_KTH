package nu.geeks.uio_kth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import nu.geeks.uio_kth.Database.DataProvider;
import nu.geeks.uio_kth.Database.ProjectDbHelper;
import nu.geeks.uio_kth.Database.TransactionsDbHelper;

/**
 * Created by Hannes on 2016-02-19.
 */
public class ProjectContentView extends Activity implements View.OnClickListener{

    private int projectId;

    //Database stuff
    TransactionsDbHelper transactionsDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursorProject, cursorContent;

    TextView projectName, totalExpenses;
    ArrayList<Transaction> transactions;

    Button add_transaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project_content);

        //Get the extras that contain the id from the main screen.
        Bundle b = getIntent().getExtras();
        projectId = b.getInt("project_id");
        Log.e("got id ", projectId + "");

        readTransactions();

        Typeface caviarBold = Typeface.createFromAsset(getAssets(), "CaviarDreams_Bold.ttf");

        projectName = (TextView) findViewById(R.id.tvProjectName); //Lol naming convention.
        totalExpenses = (TextView) findViewById(R.id.tv_total_expences);

        projectName.setTypeface(caviarBold);
        projectName.setText(getName(projectId));

        add_transaction = (Button) findViewById(R.id.bt_add_trans);
        add_transaction.setOnClickListener(this);

    }


    private String getName(int projectId){

        ProjectDbHelper dbHelper = new ProjectDbHelper(getApplicationContext());
        sqLiteDatabase = dbHelper.getReadableDatabase();
        cursorProject = dbHelper.getProjects(sqLiteDatabase);
        cursorProject.moveToPosition(projectId);
        return cursorProject.getString(0);
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


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_add_trans:
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.add_transaction, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(dialogLayout);
                builder.show();
        }
    }
}
