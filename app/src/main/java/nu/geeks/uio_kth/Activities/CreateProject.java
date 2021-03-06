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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nu.geeks.uio_kth.Adapters.SpinnerAdapter;
import nu.geeks.uio_kth.Objects.DataProvider;
import nu.geeks.uio_kth.Database.GetProjectCallback;
import nu.geeks.uio_kth.Database.ProjectDbHelper;
import nu.geeks.uio_kth.Database.ServerRequest;
import nu.geeks.uio_kth.R;

/**
 * The create-project-view.
 *
 */

public class CreateProject extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    Button bCreate,bView,bShare;
    EditText etProjectName;
    TextView tv_create_project,tv_create_name, tv_set_icon;
    ProjectDbHelper projectDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Spinner iconSpinner;
    int spinnerIndex=0;
    List<String> spinnerArray = new ArrayList<String>();
    Typeface caviarBold;
    static final String TAG = "CreateProject";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

       caviarBold = Typeface.createFromAsset(getAssets(),"CaviarDreams_Bold.ttf");


        // text removed on focus
        etProjectName = (EditText) findViewById(R.id.etProjectName);
        etProjectName.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                if (hasFocus)
                etProjectName.setHint("");
                else etProjectName.setHint("Project Name");
            }
        });

        // linking with view
        tv_create_project = (TextView) findViewById(R.id.tv_create_project);
        tv_create_name = (TextView) findViewById(R.id.tv_create_name);
        tv_set_icon = (TextView) findViewById(R.id.tv_set_icon);

        // fonts
        tv_set_icon.setTypeface(caviarBold);
        tv_create_project.setTypeface(caviarBold);
        tv_create_name.setTypeface(caviarBold);

        // link button with view and set listener
        bCreate = (Button) findViewById(R.id.bt_done);
        bCreate.setOnClickListener(this);


        // link button with view and set listener
        bView= (Button) findViewById(R.id.bCancel);
        bView.setOnClickListener(this);



        //Add A-Z and a-z to array.
        // icons
        for(int i = 65; i < 91; i++){
            spinnerArray.add("" + (char) i);
        }
        for(int i = 98; i < 123; i++){
            spinnerArray.add("" + (char) i);
        }

        // spinner list of icons
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(
                getApplicationContext(),
                R.layout.project_icon_spinner_item,
                spinnerArray
        );

        iconSpinner = (Spinner) findViewById(R.id.iconSpinner);
        iconSpinner.setAdapter(spinnerAdapter);
        iconSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_done:
                addProject();
                break;
            case R.id.bCancel:
            viewProjects();
                break;

        }

    }



    public void addProject(){
        DataProvider dataProvider = new DataProvider();

        dataProvider.setProjectName(etProjectName.getText().toString());
        dataProvider.setProjectPassword(".");
        dataProvider.setProjectIcon(spinnerArray.get(spinnerIndex));
        projectDbHelper = new ProjectDbHelper(this);
        sqLiteDatabase = projectDbHelper.getWritableDatabase();

        projectDbHelper.addProjectData(dataProvider, sqLiteDatabase);
        Cursor cursor = projectDbHelper.getProjects(sqLiteDatabase);
        cursor.moveToLast();
        final int pos = cursor.getPosition();
        Log.e(TAG, "Cursor pos: " + pos);

                Toast.makeText(getBaseContext(),"Project Saved",Toast.LENGTH_SHORT).show();
        projectDbHelper.close();
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.storeProjectDataInBackground(dataProvider, new GetProjectCallback() {
            @Override
            public void done(int projectPosition) {
                projectPosition=pos;
                Intent intent = new Intent(CreateProject.this,ProjectContentView.class);
                intent.putExtra("project_id", projectPosition);
                startActivity(intent);
                finish();
            }

            @Override
            public void done(DataProvider projectToAdd) {

            }
        });

    }

    public void viewProjects(){

        startActivity(new Intent(this, ProjectView.class));
        finish();
    }

    // icon selection
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerIndex=position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        viewProjects();
    }
}
