package nu.geeks.uio_kth.Activities;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nu.geeks.uio_kth.Database.GetChatCallback;
import nu.geeks.uio_kth.Database.GetTransactionCallback;
import nu.geeks.uio_kth.Database.ServerRequest;
import nu.geeks.uio_kth.Objects.ChatMessage;
import nu.geeks.uio_kth.Objects.Transaction;
import nu.geeks.uio_kth.R;

/**
 * Created by Micke on 2016-03-01.
 */
public class ChatView extends Activity implements View.OnClickListener{

    static final String TAG = "CHAT VIEW";

    private String projectId;
    ArrayList<ChatMessage> chatContent;
    ArrayAdapter<ChatMessage> chatMessageAdapter;
    String[] nameList;
    ListView listView;

    Typeface caviar;

    Button bSend;
    EditText etMessage;
    AutoCompleteTextView etName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        Bundle b = getIntent().getExtras();
        projectId = b.getString("project_id");
        nameList = b.getStringArray("project_names");
        Log.e(TAG, "Project ID: " + projectId);
        listView = (ListView) findViewById(R.id.lv_chat);
        caviar = Typeface.createFromAsset(getAssets(), "CaviarDreams_Bold.ttf");
        getChatContent();

        bSend = (Button) findViewById(R.id.bt_send_msg);
        etMessage = (EditText) findViewById(R.id.etMessage);
        etName = (AutoCompleteTextView) findViewById(R.id.etName);

        bSend.setOnClickListener(this);


        setOnFocusListeners();


    }

    private void fillListView() {


        chatMessageAdapter = new ArrayAdapter<ChatMessage>(this,android.R.layout.simple_list_item_2, android.R.id.text1, chatContent){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                TextView name = (TextView) findViewById(android.R.id.text2);
                TextView msg = (TextView) findViewById(android.R.id.text1);
                name.setTypeface(caviar);
                msg.setTypeface(caviar);
                name.setText(chatContent.get(position).name);
                msg.setText(chatContent.get(position).message);
                name.setTextColor(Color.WHITE);
                msg.setTextColor(Color.WHITE);
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
                } else {
                    etMessage.setHint("Enter message here!");
                }
            }
        });

        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    etName.setHint("");
                }else{

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

        if(etName.getText().toString().equals("") && etMessage.getText().toString().equals("")){
            Toast.makeText(this, "You have to fill in both name and message", Toast.LENGTH_LONG);
            return null;
        }else{
            ChatMessage msg = new ChatMessage(etName.getText().toString(), etMessage.getText().toString(), projectId);
            etMessage.setText("");
            return msg;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_send_msg:
                ChatMessage msg = createChatMessage();
                if(msg != null){
                    addChatMessage(msg);
                }
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
                fillListView();
            }
        });

        chatMessageAdapter.notifyDataSetChanged();

    }

    private void getChatContent() {
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.updateChatInBackground(new ChatMessage("", "", projectId), new GetChatCallback() {
            @Override
            public void done(ArrayList<ChatMessage> newChatContent) {
                chatContent = newChatContent;
                fillListView();
            }
        });
    }


}
