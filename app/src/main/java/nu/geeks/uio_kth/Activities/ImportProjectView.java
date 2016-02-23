package nu.geeks.uio_kth.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import nu.geeks.uio_kth.Database.GetProjectCallback;
import nu.geeks.uio_kth.Database.ServerRequest;
import nu.geeks.uio_kth.Objects.DataProvider;
import nu.geeks.uio_kth.R;

/**
 * Created by hannespa on 16-02-23.
 */
public class ImportProjectView extends Activity {

    static final String TAG ="IMPORT_PROJECT";

    TextView project_name_text;
    DataProvider projectToAdd;
    String projectId,project_name;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_project);

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
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.fetchProjectDataInBackground(projectId, new GetProjectCallback() {
            @Override
            public void done(int projectPosition) {

            }

            @Override
            public void done(DataProvider projectFromServer) {
                projectToAdd = projectFromServer;
                Log.e(TAG, projectToAdd.getProjectName());
                project_name_text = (TextView) findViewById(R.id.tv_imp_proj_name);
                project_name_text.setText(projectToAdd.getProjectName());
            }
        });



    }
}
