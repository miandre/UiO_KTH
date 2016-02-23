package nu.geeks.uio_kth.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;

import nu.geeks.uio_kth.R;

/**
 * Created by hannespa on 16-02-23.
 */
public class ImportProjectView extends Activity {

    static final String TAG ="IMPORT_PROJECT";

    TextView project_name;

    String projectId;

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

        project_name = (TextView) findViewById(R.id.tv_imp_proj_name);
        project_name.setText(projectId);

    }
}
