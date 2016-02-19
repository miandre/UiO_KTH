package nu.geeks.uio_kth.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Micke on 2016-02-17.
 */
public class ProjectDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PROJECTS.DB";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_QUERY = "CREATE TABLE "+ProjectProperties.NewProjectData.TABLE_NAME+
            " ("+ProjectProperties.NewProjectData.PROJECT_NAME+" TEXT,"+ ProjectProperties.NewProjectData.PROJECT_PASSWORD+
            " TEXT,"+ProjectProperties.NewProjectData.PROJECT_ID+" TEXT,"+ ProjectProperties.NewProjectData.PROJECT_ICON+" TEXT);";

    public ProjectDbHelper(Context context){

        super(context, DATABASE_NAME,null,DATABASE_VERSION);
        Log.e("DATABASE OPERATIONS", "Database created/opened");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_QUERY);
        Log.e("DATABASE OPERATIONS", "Table created");
    }

    public void addProjectData(String projectName, String projectPassword, String projectIcon, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();

        contentValues.put(ProjectProperties.NewProjectData.PROJECT_NAME,projectName);
        contentValues.put(ProjectProperties.NewProjectData.PROJECT_PASSWORD,projectPassword);
        contentValues.put(ProjectProperties.NewProjectData.PROJECT_ID,generateProjectId());
        contentValues.put(ProjectProperties.NewProjectData.PROJECT_ICON,projectIcon);
        db.insert(ProjectProperties.NewProjectData.TABLE_NAME, null, contentValues);

        Log.e("DATABASE OPERATIONS", "New Project Added");
    }

    public Cursor getProjects(SQLiteDatabase db){

        String[] projections = {ProjectProperties.NewProjectData.PROJECT_NAME, ProjectProperties.NewProjectData.PROJECT_PASSWORD, ProjectProperties.NewProjectData.PROJECT_ID, ProjectProperties.NewProjectData.PROJECT_ICON};

        return db.query(ProjectProperties.NewProjectData.TABLE_NAME,projections,null,null,null,null,null);

    }


    public void deleteProject(String project_id, SQLiteDatabase sqLiteDatabase){

        String selection = ProjectProperties.NewProjectData.PROJECT_ID+" LIKE ?";
        String[] selection_args = {project_id};
        sqLiteDatabase.delete(ProjectProperties.NewProjectData.TABLE_NAME,selection,selection_args);
    }

private String generateProjectId(){

    int length =(int)(Math.random()*20%5)+5;
    StringBuilder ret = new StringBuilder();

    for(int i = 0; i<length; i++){
        char appender =(char)((Math.random()*System.currentTimeMillis()%57)+65);
        if (appender>96||appender<91)
        ret.append(appender);
        else i--;
    }
    Log.e("Generated ID",ret.toString());
    return ret.toString();
}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
