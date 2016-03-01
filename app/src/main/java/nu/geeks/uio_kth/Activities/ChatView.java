package nu.geeks.uio_kth.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import nu.geeks.uio_kth.R;

/**
 * Created by Micke on 2016-03-01.
 */
public class ChatView extends Activity implements View.OnClickListener{

    static final String TAG = "CHAT VIEW";

    private String projectId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        Bundle b = getIntent().getExtras();
        projectId = b.getString("project_id");
        Log.e(TAG, "Project ID: " + projectId);
    }

    @Override
    public void onClick(View v) {

    }
}
