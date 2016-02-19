package nu.geeks.uio_kth;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
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
 * Created by Micke on 2016-02-17.
 */


public class ProjectView extends Activity implements View.OnClickListener {

    ListView listView;
    SQLiteDatabase sqLiteDatabase;
    ProjectDbHelper projectDbHelper;
    Cursor cursor;
    ProjectDataAdapter projectDataAdapter;
    Button bNewProject;
    TextView tv_current_projects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_view);

        Typeface tower = Typeface.createFromAsset(getAssets(),"HTOWERT.TTF");
        Typeface caviarBold = Typeface.createFromAsset(getAssets(),"CaviarDreams_Bold.ttf");

        bNewProject = (Button) findViewById(R.id.bNewProject);
        bNewProject.setOnClickListener(this);
        tv_current_projects = (TextView) findViewById(R.id.tv_current_projects);
        tv_current_projects.setTypeface(caviarBold);

        listView = (ListView) findViewById(R.id.project_view);
        projectDataAdapter = new ProjectDataAdapter(getApplicationContext(), R.layout.list_item);
        listView.setAdapter(projectDataAdapter);

        registerForContextMenu(listView);


        projectDbHelper = new ProjectDbHelper(getApplicationContext());
        sqLiteDatabase = projectDbHelper.getReadableDatabase();
        cursor = projectDbHelper.getProjects(sqLiteDatabase);
        updateListview(projectDbHelper,sqLiteDatabase,cursor);



    }

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


    @Override
    public void onClick(View v) {
        Cursor cursor;
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

