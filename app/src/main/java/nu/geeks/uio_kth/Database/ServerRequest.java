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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import nu.geeks.uio_kth.Objects.ChatMessage;
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


    //Method called to store project data. Opens e progress dialog and calls an Asynchronus background task.

    public void storeProjectDataInBackground(DataProvider dataProvider, GetProjectCallback projectCallback) {
        progressDialog.show();
        new storeProjectDataAsyncTask(dataProvider, projectCallback).execute();

    }


    //Method called to store transaction data. Opens e progress dialog and calls an Asynchronus background task.
    public void storeTransactionDataInBackground(Transaction transaction, GetProjectCallback projectCallback) {
        progressDialog.show();

        new storeTransactionDataAsyncTask(transaction, projectCallback).execute();

    }
    public void updateChatInBackground(ChatMessage chatMessage, GetChatCallback chatCallback) {
        progressDialog.show();

        new chatUpdateAsyncTask(chatMessage, chatCallback).execute();

    }

    //Method called to fetch project data. Opens e progress dialog and calls an Asynchronus background task.
    public void fetchProjectDataInBackground(String project_id, GetProjectCallback projectCallback){
        progressDialog.show();
        new fetchProjectDataAsyncTask(project_id, projectCallback).execute();

    }

    //Method called to fetch project data. Opens e progress dialog and calls an Asynchronus background task.
    public void fetchProjectContentInBackground(String project_id, GetTransactionCallback transactionCallback){
        progressDialog.show();
        new fetchProjectContentAsyncTask(project_id, transactionCallback).execute();

    }




    //Inner class that performs asynchronus backround tasks.
    public class storeProjectDataAsyncTask extends AsyncTask<Void, Void, Void> {
        DataProvider dataProvider;
        GetProjectCallback projectCallback;

        //constructor
        public storeProjectDataAsyncTask(DataProvider dataProvider, GetProjectCallback projectCallback) {
            this.projectCallback = projectCallback;
            this.dataProvider = dataProvider;
        }


        //Method to use for everything that should be performed in the background
        @Override
        protected Void doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            //Add data to send as an http post
            dataToSend.add(new BasicNameValuePair("project_id",dataProvider.getProjectId()));
            dataToSend.add(new BasicNameValuePair("name", dataProvider.getProjectName()));
            dataToSend.add(new BasicNameValuePair("password", dataProvider.getProjectPassword()));
            dataToSend.add(new BasicNameValuePair("icon", dataProvider.getProjectIcon()));

            //Debug
            //  Log.e(TAG, "ID: " + dataProvider.getProjectId() + "\nName: " + dataProvider.getProjectName()
            //        + "\nPassword: " + dataProvider.getProjectPassword() + "\n Icon: " + dataProvider.getProjectIcon());


            //Create the http request and set timeout-time
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);

            //Create a post with the  adress to the *.php file as argument
            HttpPost post = new HttpPost(SERVER_ADDRESS + "StoreProject.php");

            //Post the http-request (must be try-catch)
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
        //This methot is called after Backgroundtask is finished.
        @Override
        protected void onPostExecute(Void aVoid) {
            //Close the progress dialog
            progressDialog.dismiss();
            //Call the callback task to finish the asynch task
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

            //Debug
            Log.e(TAG, "Store Transaction: \nID: "+transaction.projectId+ "\nName: "+transaction.person
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
        //this task is automatically called after doInBackground() is finished
        @Override
        protected void onPostExecute(Void aVoid) {
            //Close the progress dialog
            progressDialog.dismiss();
            //Call the callback task to finish the asynch task
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

            //The data tu send must be in the form of a List of <NameValuePairs>, hence the list even
            //if only one argument is posted
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

                //Create JSON object to store handle recieved data
                JSONObject jsonObject = new JSONObject(result);

                //Chack if server did not return error
                if (jsonObject.has("fail")){
                    Log.e(TAG,"jsonFail");
                    projectToAdd = null;
                } else {

                    //Set variable values by extracting data from JSON object, based on keys defined in php-file
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

            //Return dhe recieved data as an object
            return projectToAdd;
        }

        //finsh the background task and pass the stored data in an object to the callback function
        //this task is automatically called after doInBackground() is finished
        @Override
        protected void onPostExecute(DataProvider projectToAdd) {
            progressDialog.dismiss();
            projectCallback.done(projectToAdd);
            super.onPostExecute(projectToAdd);
        }

    }


    //Bakground task to fetch project content
    public class fetchProjectContentAsyncTask extends AsyncTask<Void, Void, ArrayList<Transaction>> {
        String project_id;
        GetTransactionCallback transactionCallback;


        //constructor
        public fetchProjectContentAsyncTask(String project_id, GetTransactionCallback transactionCallback) {
            this.transactionCallback = transactionCallback;
            this.project_id = project_id;

        }

        //Background task that runs on separate thread
        @Override
        protected ArrayList<Transaction> doInBackground(Void... params) {

            //The data tu send must be in the form of a List of <NameValuePairs>, hence the list even
            //if only one argument is posted
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            //Add the data to send
            dataToSend.add(new BasicNameValuePair("project_id", project_id));
            Log.e("sending project ID: ", project_id);

            //Create http parameters to send
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            //Create http client
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchProjectContent.php");

            ArrayList<Transaction> transactions=new ArrayList<>();

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                Log.e("jsonAnswer: ", result);

                //Create JSON array to handle recieved data
                JSONArray jsonArray = new JSONArray(result);
                //JSON object to use as temp variable when extracting data from JSON array
                JSONObject jsonObject;

                //Chack if server did not return error
                if (jsonArray.getJSONObject(0).has("fail")){
                    Log.e(TAG,"jsonFail");
                    transactions.clear();

                } else {
                    //Iterate through JSON array to extract and save all transactions
                    for(int i = 0; i<jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        //Set variable values by extracting data from JSON object, based on keys defined in php-file
                        String person = jsonObject.getString("person");
                        String project_id = jsonObject.getString("project_id");
                        String amount = jsonObject.getString("amount");
                        String object = jsonObject.getString("object");

                        transactions.add(new Transaction(project_id, person, amount, object));

                    /* Debug Text
                    Log.e(TAG, "Transaction in list:\nID: "+transactions.get(i).projectId+ "\nName: "+transactions.get(i).person
                            +"\nAmount: "+transactions.get(i).amount+"\n Object: "+transactions.get(i).object);
                    */
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ServerRequest: ", "try failed");
                transactions.clear();
            }

            //Return the recieved data as a list of transactions
            return transactions;
        }

        //finsh the background task and pass the stored data in a list to the callback function
        //this task is automatically called after doInBackground() is finished
        @Override
        protected void onPostExecute(ArrayList<Transaction> transactions) {
            progressDialog.dismiss();
            transactionCallback.done(transactions);
            super.onPostExecute(transactions);
        }

    }

    public class chatUpdateAsyncTask extends AsyncTask<Void, Void, ArrayList<ChatMessage>> {
        ChatMessage chatMessage;
        GetChatCallback chatCallback;


        //constructor
        public chatUpdateAsyncTask(ChatMessage chatMessage, GetChatCallback chatCallback) {
            this.chatCallback = chatCallback;
            this.chatMessage = chatMessage;

        }

        //Background task that runs on separate thread
        @Override
        protected ArrayList<ChatMessage> doInBackground(Void... params) {

            //The data tu send must be in the form of a List of <NameValuePairs>, hence the list even
            //if only one argument is posted
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            //Add the data to send
            dataToSend.add(new BasicNameValuePair("name", chatMessage.name));
            dataToSend.add(new BasicNameValuePair("message", chatMessage.message));
            dataToSend.add(new BasicNameValuePair("project_id", chatMessage.project_id));
            Log.e("sending project ID: ", chatMessage.project_id);

            //Create http parameters to send
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            //Create http client
            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "UpdateChat.php");

            ArrayList<ChatMessage> chatContent=new ArrayList<>();

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                Log.e("jsonAnswer: ", result);

                //Create JSON array to handle recieved data
                JSONArray jsonArray = new JSONArray(result);
                //JSON object to use as temp variable when extracting data from JSON array
                JSONObject jsonObject;

                //Chack if server did not return error
                if (jsonArray.getJSONObject(0).has("fail")){
                    Log.e(TAG,"jsonFail");
                    chatContent.clear();

                } else {
                    //Iterate through JSON array to extract and save all transactions
                    for(int i = 0; i<jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        //Set variable values by extracting data from JSON object, based on keys defined in php-file
                        String name = jsonObject.getString("name");
                        String project_id = jsonObject.getString("project_id");
                        String message = jsonObject.getString("message");


                        chatContent.add(new ChatMessage(name,message,project_id));

                     //Debug Text
                    Log.e(TAG, "Chat :\nID: "+chatContent.get(i).project_id+ "\nName: "+chatContent.get(i).name
                            +"\nMessage: "+chatContent.get(i).message);

                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ServerRequest: ", "try failed");
                chatContent.clear();
            }

            //Return the recieved data as a list of transactions
            return chatContent;
        }

        //finsh the background task and pass the stored data in a list to the callback function
        //this task is automatically called after doInBackground() is finished
        @Override
        protected void onPostExecute(ArrayList<ChatMessage> chatContent) {
            progressDialog.dismiss();
            chatCallback.done(chatContent);
            super.onPostExecute(chatContent);
        }

    }





}

