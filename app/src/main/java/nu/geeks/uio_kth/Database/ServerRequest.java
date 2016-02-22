package nu.geeks.uio_kth.Database;

/**
 * Created by Micke on 2016-01-24.
 * This class handles server http requests as asynchronus background tasks.
 *
 */


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;


public class ServerRequest {

    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://iou.16mb.com/";
    static final String TAG = "ServerRequest";

    public ServerRequest(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing Server Request");
        progressDialog.setMessage("Please wait...");
    }



    public void storeProjectDataInBackground(DataProvider dataProvider, GetProjectCallback projectCallback) {
        progressDialog.show();
        Log.e(TAG, "storeProjectDataInBackground");
        new storeProjectDataAsyncTask(dataProvider, projectCallback).execute();

    }

/*
    public void fetchUserDataInBackground(DataProvider dataProvider, GetProjectCallback projectCallback){
        progressDialog.show();
        new fetchUserDataAsyncTask(dataProvider, projectCallback).execute();

    }
*/



    public class storeProjectDataAsyncTask extends AsyncTask<Void, Void, Void> {
        DataProvider dataProvider;
        GetProjectCallback projectCallback;

        public storeProjectDataAsyncTask(DataProvider dataProvider, GetProjectCallback projectCallback) {
            this.projectCallback = projectCallback;
            this.dataProvider = dataProvider;
        }

        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            //Add data to send as an http post
            dataToSend.add(new BasicNameValuePair("project_id",dataProvider.getProjectId()));
            dataToSend.add(new BasicNameValuePair("name", dataProvider.getProjectName()));
            dataToSend.add(new BasicNameValuePair("password", dataProvider.getProjectPassword()));
            dataToSend.add(new BasicNameValuePair("icon", dataProvider.getProjectIcon()));

            Log.e(TAG, "ID: "+dataProvider.getProjectId()+ "\nName: "+dataProvider.getProjectName()
            +"\nPassword: "+dataProvider.getProjectPassword()+"\n Icon: "+dataProvider.getProjectIcon());

            //Create the http request and set timeout-time
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);

            HttpPost post = new HttpPost(SERVER_ADDRESS + "StoreProject.php");

            //Post the http-request
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Exception caught");
            }
            return null;
        }

        //Close "wait" dialog
        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            projectCallback.done(0);
            super.onPostExecute(aVoid);
        }
    }

/*    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallback;

        public fetchUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.userCallback = userCallback;
            this.user = user;

        }

        @Override
        protected User doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));
            Log.d("sending username: ", user.username);
            Log.d("sending password: ", user.password);

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData.php");

            User returnedUser = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                Log.d("jsonAnswer: ", result);

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.has("fail")){
                    //if (false) {
                    returnedUser = null;
                } else {
                    String name = jsonObject.getString("name");
                    int age = jsonObject.getInt("age");
                    returnedUser = new User(name, user.username, user.password, age);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ServerRequest: ", "try failed");
            }


            return returnedUser;
        }

        @Override
        protected void onPostExecute(DataProvider dataProvider) {
            progressDialog.dismiss();
            userCallback.done(0);
            super.onPostExecute(data);
        }

    }*/
}

