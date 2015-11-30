package com.example.awadeshkumar.chatapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Awadesh
 * To have chat with the selected user
 */
public class ChatActivity extends AppCompatActivity {
    public static final String USER_NAME_KEY = "username";
    private ArrayAdapter<String> adapter;
    private static String username;
    private EditText txtMessage;
    private Button btnSend;
    private ListView chatListView;
    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receiveMessage();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        username = intent.getStringExtra("user");
        setupUI();
        receiveMessage();
        registerReceiver(pushReceiver, new IntentFilter("MyAction"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString s = new SpannableString(username);
        getSupportActionBar().setTitle(s);

    }

    public void setupUI() {
        txtMessage = (EditText) findViewById(R.id.etMensaje);
        btnSend = (Button) findViewById(R.id.btnSend);
        chatListView = (ListView) findViewById(R.id.chatList);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        chatListView.setAdapter(adapter);
        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String data = txtMessage.getText().toString();
                ParseObject message = new ParseObject("Messages");
                message.put(USER_NAME_KEY, ParseUser.getCurrentUser().getUsername());
                message.put("message", data);
                message.saveInBackground(new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        receiveMessage();
                    }
                });
                createPushNotifications(data);
                txtMessage.setText("");
            }
        });
    }

    public void createPushNotifications(String message) {
        JSONObject object = new JSONObject();
        try {
            object.put("alert", message);
            object.put("title", "Chat");
            object.put("action", "MyAction");

            ParseQuery query = ParseInstallation.getQuery();
            query.whereEqualTo(USER_NAME_KEY, username);

            ParsePush pushNotification = new ParsePush();
            pushNotification.setQuery(query);
            pushNotification.setData(object);
            pushNotification.sendInBackground(new SendCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        receiveMessage();
                    }
                }
            });
        } catch (JSONException e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        finish();
        return super.onOptionsItemSelected(item);
    }

    private void receiveMessage() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        query.setLimit(5);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> messages, ParseException e) {
                if (e == null) {
                    adapter.clear();
                    StringBuilder builder = new StringBuilder();
                    for (int i = messages.size() - 1; i >= 0; i--) {
                        if (messages.get(i).getString(USER_NAME_KEY).equals(username) || messages.get(i).getString(USER_NAME_KEY).equals(ParseUser.getCurrentUser().getUsername())) {

                            builder.append(messages.get(i).getString(USER_NAME_KEY)
                                    + ": " + messages.get(i).getString("message") + "\n");
                        }
                    }
                    addItemstoListView(builder.toString());

                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void addItemstoListView(String message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        chatListView.invalidate();
    }


}
