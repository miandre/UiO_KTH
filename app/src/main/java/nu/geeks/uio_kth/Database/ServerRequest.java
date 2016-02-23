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

import nu.geeks.uio_kth.Objects.DataProvider;
import nu.geeks.uio_kth.Objects.Transaction;


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
        new storeProjectDataAsyncTask(dataProvider, projectCallback).execute();

    }

    public void storeTransactionDataInBackground(Transaction transaction, GetProjectCallback projectCallback) {
        progressDialog.show();

        new storeTransactionDataAsyncTask(transaction, projectCallback).execute();

    }


    public void fetchProjectDataInBackground(String project_id, GetProjectCallback projectCallback){
        progressDialog.show();
        new fetchProjectDataAsyncTask(project_id, projectCallback).execute();

    }




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

    public class storeTransactionDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Transaction transaction;
        GetProjectCallback projectCallback;

        public storeTransactionDataAsyncTask(Transaction transaction, GetProjectCallback projectCallback) {
            this.projectCallback = projectCallback;
            this.transaction = transaction;
        }

        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            //Add data to send as an http post
            dataToSend.add(new BasicNameValuePair("project_id",transaction.projectId));
            dataToSend.add(new BasicNameValuePair("person", transaction.person));
            dataToSend.add(new BasicNameValuePair("amount",String.valueOf(transaction.amount)));
            dataToSend.add(new BasicNameValuePair("object", transaction.object));

            Log.e(TAG, "ID: "+transaction.projectId+ "\nName: "+transaction.person
                    +"\nAmount: "+String.valueOf(transaction.amount)+"\n Object: "+transaction.object);

            //Create the http request and set timeout-time
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);

            HttpPost post = new HttpPost(SERVER_ADDRESS + "StoreTransaction.php");

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


    public class fetchProjectDataAsyncTask extends AsyncTask<Void, Void, DataProvider> {
        String project_id;
        GetProjectCallback projectCallback;

        public fetchProjectDataAsyncTask(String project_id, GetProjectCallback projectCallback) {
            this.projectCallback = projectCallback;
            this.project_id = project_id;

        }

        @Override
        protected DataProvider doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("project_id", project_id));
            Log.e("sending project ID: ", project_id);


            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchProjectData.php");

            DataProvider projectToAdd = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                Log.e("jsonAnswer: ", result);

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.has("fail")){
                    Log.e(TAG,"jsonFail");
                    projectToAdd = null;
                } else {
                    String project_name = jsonObject.getString("name");
                    String project_id = jsonObject.getString("project_id");
                    String project_password = jsonObject.getString("password");
                    String project_icon = jsonObject.getString("icon");
                    //String project_currency = jsonObject.getString("currency");
                    projectToAdd = new DataProvider(project_name,project_password,project_id,project_icon);
                    Log.e(TAG,"Created projectToAdd");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ServerRequest: ", "try failed");
            }


            return projectToAdd;
        }

        @Override
        protected void onPostExecute(DataProvider projectToAdd) {
            progressDialog.dismiss();
            projectCallback.done(projectToAdd);
            super.onPostExecute(projectToAdd);
        }

    }
}

