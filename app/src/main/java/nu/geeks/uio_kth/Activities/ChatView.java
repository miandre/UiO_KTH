package nu.geeks.uio_kth.Activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import nu.geeks.uio_kth.Database.GetChatCallback;
import nu.geeks.uio_kth.Database.GetTransactionCallback;
import nu.geeks.uio_kth.Database.ServerRequest;
import nu.geeks.uio_kth.Objects.ChatMessage;
import nu.geeks.uio_kth.Objects.Transaction;
import nu.geeks.uio_kth.Objects.User;
import nu.geeks.uio_kth.R;

/**
 * Created by Micke on 2016-03-01.
 */
public class ChatView extends Activity implements View.OnClickListener{

    static final String TAG = "CHAT VIEW";
    final String HINT = "Enter message here!";

    private String projectId;
    ArrayList<ChatMessage> chatContent;
    ArrayAdapter<ChatMessage> chatMessageAdapter;
    String[] nameList;
    ListView listView;

    boolean listViewInitialized;

    Typeface caviar;

    Button bSend;
    EditText etMessage;
    AutoCompleteTextView etName;

    CountDownTimer timer;

    private final String LOCAL_STORAGE = "LOCALE_STORAGE";
    static final String DEFAULT_USER = "default_user";


    SharedPreferences localStorage;
    SharedPreferences.Editor spEditor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        localStorage = this.getSharedPreferences(LOCAL_STORAGE,0);
        spEditor = localStorage.edit();

        Bundle b = getIntent().getExtras();
        projectId = b.getString("project_id");
        nameList = b.getStringArray("project_names");
        Log.e(TAG, "Project ID: " + projectId);
        listView = (ListView) findViewById(R.id.lv_chat);
        caviar = Typeface.createFromAsset(getAssets(), "CaviarDreams_Bold.ttf");
        listViewInitialized = false;

        addChatMessage(new ChatMessage("", "", projectId)); //Adding empty to update list



        bSend = (Button) findViewById(R.id.bt_send_msg);
        etMessage = (EditText) findViewById(R.id.etMessage);
        etName = (AutoCompleteTextView) findViewById(R.id.etName);

        bSend.setOnClickListener(this);
        bSend.setBackgroundResource(R.drawable.refresh);


        etName.setText(localStorage.getString(DEFAULT_USER,""));

        setOnFocusListeners();

        timer = new CountDownTimer(10000, 3000) {
            @Override
            public void onTick(long millisUntilFinished) {
                addChatMessage(new ChatMessage("", "", projectId));
            }

            @Override
            public void onFinish() {
                start();
            }
        }.start();

    }

    private void createListView(){
        listView = (ListView) findViewById(R.id.lv_chat);
        chatMessageAdapter = new ArrayAdapter<ChatMessage>(this,R.layout.list_chat, R.id.tvChatMessage, chatContent){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                TextView name = (TextView) v.findViewById(R.id.tvChatName);
                TextView msg = (TextView) v.findViewById(R.id.tvChatMessage);
                name.setTypeface(caviar);
                msg.setTypeface(caviar);
                name.setText(chatContent.get(position).name);
                msg.setText(chatContent.get(position).message);

                return v;
            }
        };
        listView.setAdapter(chatMessageAdapter);
        chatMessageAdapter.notifyDataSetChanged();
    }


    private void setOnFocusListeners() {
        etMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etMessage.setHint("");
                    bSend.setBackgroundResource(R.drawable.check);
                } else {
                    etMessage.setHint(HINT);
                }

            }
        });




        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    etName.setHint("");
                }else{
                    etName.setHint("Please enter a name");
                }
            }
        });

        ArrayAdapter<String> personAutoAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,nameList);
        etName.setAdapter(personAutoAdapter);
        etName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setHint("");
                etName.showDropDown();
            }
        });
        etName.setThreshold(1);
    }



    private ChatMessage createChatMessage(){

        spEditor.putString(DEFAULT_USER,User.addName(etName.getText().toString()));
        spEditor.commit();
        if(etName.getText().toString().equals("") || etMessage.getText().toString().equals("")){
            return null;
        }else{

            SimpleDateFormat sdt = new SimpleDateFormat("LLL d - HH:mm", Locale.getDefault());

            Log.e(TAG, "Date: " + sdt.format(new Date(System.currentTimeMillis())));
            ChatMessage msg = new ChatMessage(etName.getText().toString() + "   " +
                    sdt.format(new Date(System.currentTimeMillis()))
                    , etMessage.getText().toString(), projectId);
            etMessage.setText("");
            return msg;
        }
    }

    @Override
    protected void onPause() {
        timer.cancel();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_send_msg:
                ChatMessage msg = createChatMessage();
                if(msg != null){
                    addChatMessage(msg);
                }
                    addChatMessage(new ChatMessage("","",projectId));
                chatMessageAdapter.notifyDataSetChanged();
                break;
        }

    }

    private void addChatMessage(ChatMessage msg) {
        //Create service request

        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.updateChatInBackground(msg, new GetChatCallback() {
            @Override
            public void done(ArrayList<ChatMessage> newChatContent) {
                chatContent = newChatContent;


                    createListView();
                    chatMessageAdapter.notifyDataSetChanged();
                    listView.setSelection(chatContent.size()-1);

            }
        });



    }



}