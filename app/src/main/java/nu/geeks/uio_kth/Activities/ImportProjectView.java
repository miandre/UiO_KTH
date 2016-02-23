package nu.geeks.uio_kth.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nu.geeks.uio_kth.Database.GetProjectCallback;
import nu.geeks.uio_kth.Database.ProjectDbHelper;
import nu.geeks.uio_kth.Database.ServerRequest;
import nu.geeks.uio_kth.Objects.DataProvider;
import nu.geeks.uio_kth.R;

/**
 * Created by hannespa on 16-02-23.
 */
public class ImportProjectView extends Activity implements View.OnClickListener {

    static final String TAG ="IMPORT_PROJECT";

    TextView project_name,project_add,project_id,project_icon;
    DataProvider projectToAdd;
    String projectId;
    Button bt_add;
    Button bt_not_add;
    Typeface caviarBold,tower,icon;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_project);

        // link button with view and set listener
        bt_add = (Button) findViewById(R.id.bt_add);
        bt_add.setOnClickListener(this);


        // link button with view and set listener
        bt_not_add= (Button) findViewById(R.id.bt_not_add);
        bt_not_add.setOnClickListener(this);

        project_name = (TextView) findViewById(R.id.tv_imp_proj_name);
        project_add = (TextView) findViewById(R.id.tv_imp_proj_add);
        project_id = (TextView) findViewById(R.id.tv_imp_proj_id);
        project_icon = (TextView) findViewById(R.id.tv_imp_proj_icon);


        caviarBold = Typeface.createFromAsset(getAssets(),"CaviarDreams_Bold.ttf");
        tower =  Typeface.createFromAsset(getAssets(),"HTOWERT.TTF");
        icon =  Typeface.createFromAsset(getAssets(),"icons3.ttf");

        project_name.setTypeface(caviarBold);
        project_add.setTypeface(caviarBold);
        project_id.setTypeface(tower);
        project_icon.setTypeface(icon);



        //Get the exact URL
        //string structure: https://u.io/?id=xxxxxxx
        String msg = getIntent().getDataString();
        if(msg.contains("?id=")){
            int pos = msg.indexOf("=");
            projectId = msg.substring(pos+1);
            Log.e(TAG, projectId);
        }else{
            Log.e(TAG, "UNPARSABLE URL");
            finish();
        }

        //Create a server request to fetch project data from online database
        //based on project ID
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchProjectDataInBackground(projectId, new GetProjectCallback() {

           //Empty callback method (overloaded), required by interface.
            @Override
            public void done(int projectPosition) {

            }

            //This callback method contains the project data received from the database
            @Override
            public void done(DataProvider projectFromServer) {

                //Copy reference to global variable
                projectToAdd = projectFromServer;
                //Display name of imported project

                project_name.setText(projectToAdd.getProjectName());
                project_id.setText(projectToAdd.getProjectId());
                project_icon.setText(projectToAdd.getProjectIcon());
            }
        });



    }

    //Add a new project based on the data retrieved from the server request
        private void addImportedProject(){

        ProjectDbHelper projectDbHelper = new ProjectDbHelper(this);
        SQLiteDatabase sqLiteDatabase = projectDbHelper.getWritableDatabase();
        projectDbHelper.addProjectData(projectToAdd, sqLiteDatabase);

    }


    public void viewProjects(){

        startActivity(new Intent(this, ProjectView.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.bt_add:

                addImportedProject();
                viewProjects();
                finish();

                break;

            case R.id.bt_not_add:
                viewProjects();
                finish();
                break;

        }
    }

}
