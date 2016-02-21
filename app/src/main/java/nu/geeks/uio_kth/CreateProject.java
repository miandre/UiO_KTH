package nu.geeks.uio_kth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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
import nu.geeks.uio_kth.Database.ProjectDbHelper;

public class CreateProject extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    Button bCreate,bView,bShare;
    EditText etPassword, etProjectName;
    TextView tv_create_project,tv_create_name,tv_create_password, tv_set_icon;
    Context context;
    ProjectDbHelper projectDbHelper;
    SQLiteDatabase sqLiteDatabase;
    Spinner iconSpinner;
    int spinnerIndex=0;
    List<String> spinnerArray = new ArrayList<String>();
    Typeface caviarBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

       caviarBold = Typeface.createFromAsset(getAssets(),"CaviarDreams_Bold.ttf");


        etPassword = (EditText) findViewById(R.id.etPassword);
        etProjectName = (EditText) findViewById(R.id.etProjectName);

        tv_create_project = (TextView) findViewById(R.id.tv_create_project);
        tv_create_name = (TextView) findViewById(R.id.tv_create_name);
        tv_create_password = (TextView) findViewById(R.id.tv_create_password);
        tv_set_icon = (TextView) findViewById(R.id.tv_set_icon);

        tv_set_icon.setTypeface(caviarBold);
        tv_create_project.setTypeface(caviarBold);
        tv_create_name.setTypeface(caviarBold);
        tv_create_password.setTypeface(caviarBold);

        bCreate = (Button) findViewById(R.id.bCreate);
        bCreate.setOnClickListener(this);

        bShare = (Button) findViewById(R.id.bShare);
        bShare.setOnClickListener(this);

        bView= (Button) findViewById(R.id.bCancel);
        bView.setOnClickListener(this);

        //Add A-Z and a-z to array.
        for(int i = 65; i < 91; i++){
            spinnerArray.add("" + (char) i);
        }
        for(int i = 98; i < 123; i++){
            spinnerArray.add("" + (char) i);
        }


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
            case R.id.bCreate:
                addProject(v);
                viewProjects();
                break;
            case R.id.bCancel:
            viewProjects();
                break;
            case R.id.bShare:
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


    public void addProject(View view){

        context = this;
        String projectName = etProjectName.getText().toString();
        String projectPassword = etPassword.getText().toString();
        String projectIcon = spinnerArray.get(spinnerIndex);
        projectDbHelper = new ProjectDbHelper(context);
        sqLiteDatabase = projectDbHelper.getWritableDatabase();

        projectDbHelper.addProjectData(projectName,projectPassword, projectIcon,sqLiteDatabase);

        Toast.makeText(getBaseContext(),"Project Saved",Toast.LENGTH_SHORT).show();
        projectDbHelper.close();
    }

    public void viewProjects(){

        startActivity(new Intent(this, ProjectView.class));
        finish();
    }

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
