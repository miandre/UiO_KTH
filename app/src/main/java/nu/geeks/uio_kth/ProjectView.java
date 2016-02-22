package nu.geeks.uio_kth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import nu.geeks.uio_kth.Adapters.ProjectDataAdapter;
import nu.geeks.uio_kth.Database.DataProvider;
import nu.geeks.uio_kth.Database.ProjectDbHelper;


/**
 * This is the main acitivity for the application, the starting view with all the projects.
 *
 * Created by Micke on 2016-02-17.
 */


public class ProjectView extends Activity implements View.OnClickListener {

    ListView listView;                      //The listview for the projects
    SQLiteDatabase sqLiteDatabase;          //The database with the projects (Note that there are two different databases)
    ProjectDbHelper projectDbHelper;        //Used to communicate with the database
    Cursor cursor;                          //Used as a pointer, points to a field in the database.
    ProjectDataAdapter projectDataAdapter;  //An adapter to handle communication with the list view and the content of the list.
    Button bNewProject;                     //The new-project-button
    TextView tv_current_projects;           //The static text at the top of the screen
    Typeface caviarBold;                    //Typsnittet som används.

    /**
     * Called by Android, instanziate all stuff.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);                 //Always call super.
        setContentView(R.layout.activity_project_view);     //the layout (the xml in res/layout)

        caviarBold = Typeface.createFromAsset(getAssets(),"CaviarDreams_Bold.ttf");

        //Connect the button and the text in the xml.
        bNewProject = (Button) findViewById(R.id.bNewProject);
        bNewProject.setOnClickListener(this);   //Set the onClickListener, which is implemented in thiss class.
        tv_current_projects = (TextView) findViewById(R.id.tv_current_projects);
        tv_current_projects.setTypeface(caviarBold);

        listView = (ListView) findViewById(R.id.project_view);

        projectDataAdapter = new ProjectDataAdapter(getApplicationContext(), R.layout.list_item);
        listView.setAdapter(projectDataAdapter);    //Connect listview to the adapter

        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startProjectContentView(position);
            }
        });

        projectDbHelper = new ProjectDbHelper(getApplicationContext());

        sqLiteDatabase = projectDbHelper.getReadableDatabase();
        cursor = projectDbHelper.getProjects(sqLiteDatabase);

        updateListview(projectDbHelper,sqLiteDatabase,cursor);



    }

    /**
     * Starts the next view, when you have pressed a project in the list.
     *
     *
     * @param position position in the list
     */
    private void startProjectContentView(int position){

        //Get the project ID.
        cursor.moveToPosition(position);
        String id = cursor.getString(2); //Id

        Intent intent = new Intent(this,ProjectContentView.class);
        intent.putExtra("project_id", position);
        startActivity(intent);
    }

    /**
     * reads from the database, updates the adapter and the listview.
     *
     * @param projectDbHelper
     * @param sqLiteDatabase
     * @param cursor
     */
    private void updateListview(ProjectDbHelper projectDbHelper, SQLiteDatabase sqLiteDatabase, Cursor cursor) {

        projectDataAdapter.clear();
        projectDataAdapter.notifyDataSetChanged();

        sqLiteDatabase = projectDbHelper.getReadableDatabase();
        cursor = projectDbHelper.getProjects(sqLiteDatabase);

        if (cursor.moveToFirst()) {
            do {
                String projectName, projectPassword, projectId, projectIcon;
                projectName = cursor.getString(0);
                projectPassword = cursor.getString(1);
                projectId = cursor.getString(2);
                projectIcon = cursor.getString(3);

                DataProvider dataProvider = new DataProvider(projectName, projectPassword, projectId, projectIcon);
                projectDataAdapter.add(dataProvider);

            } while (cursor.moveToNext());
        }

        projectDataAdapter.notifyDataSetChanged();
    }


    /**
     * Handles only the click of the new-project-button
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bNewProject:
                startActivity(new Intent(this, CreateProject.class));
                finish();
                break;
        }
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        projectDbHelper = new ProjectDbHelper(getApplicationContext());
        sqLiteDatabase = projectDbHelper.getReadableDatabase();
        cursor = projectDbHelper.getProjects(sqLiteDatabase);

        switch (item.getItemId()) {
            case R.id.delete_project:
                cursor.moveToPosition(info.position);
                Log.e("Delete Click", cursor.getString(0));
                projectDbHelper.deleteProject(cursor.getString(2), sqLiteDatabase);
                updateListview(projectDbHelper,sqLiteDatabase,cursor);
        }

        return super.onContextItemSelected(item);

    }
}

