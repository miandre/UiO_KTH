package nu.geeks.uio_kth;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import nu.geeks.uio_kth.Database.ProjectDbHelper;

/**
 * Created by Hannes on 2016-02-19.
 */
public class ProjectContentView extends Activity {

    private int projectId;

    //Database stuff
    SQLiteDatabase sqLiteDatabase;
    ProjectDbHelper projectDbHelper;
    Cursor cursor;

    TextView projectName, totalExpenses;
    ArrayList<String> transactions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_project_content);

        //Get the extras that contain the id from the main screen.
        Bundle b = getIntent().getExtras();
        projectId = b.getInt("project_id");
        Log.e("got id ", projectId + "");

        transactions = readTransactions();

        Typeface caviarBold = Typeface.createFromAsset(getAssets(), "CaviarDreams_Bold.ttf");

        projectName = (TextView) findViewById(R.id.tvProjectName); //Lol naming convention.
        totalExpenses = (TextView) findViewById(R.id.tv_total_expences);

        projectDbHelper = new ProjectDbHelper(getApplicationContext());
        sqLiteDatabase = projectDbHelper.getReadableDatabase();
        cursor = projectDbHelper.getProjects(sqLiteDatabase);

        projectName.setTypeface(caviarBold);
        projectName.setText(getName(projectId));

    }

    private String getName(int projectId){
        cursor.moveToPosition(projectId);
        return cursor.getString(0);
    }



    private ArrayList<String> readTransactions(){
        //TODO - läs från databas, fyll upp transactions.

        ArrayList<String> data = new ArrayList<>();
        data.add("Mike bought beer - 100 kr");
        data.add("Hannes bought beer - 300 kr");
        data.add("Anton bought beer - 240 kr");
        data.add("Linnea bought beer - 1450 kr");
        return data;
    }


}
