package com.example.awadeshkumar.chatapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.awadeshkumar.chatapplication.Adapter.ChatArrayAdapter;
import com.example.awadeshkumar.chatapplication.Utilities.ChatMessage;
import com.example.awadeshkumar.chatapplication.helpers.PushReceiver;
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
    private ChatArrayAdapter adapter;
    private static String username;
    private EditText txtMessage;
    private ListView chatListView;
    private BroadcastReceiver pushReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            receiveMessage();
        }
    };


    @Override
    protected void onResume() {
        PushReceiver.isBackground = false;
        super.onResume();
    }

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
        txtMessage = (EditText) findViewById(R.id.chat_box);
        Button btnSend = (Button) findViewById(R.id.btnSend);
        chatListView = (ListView) findViewById(R.id.chatList);

        adapter = new ChatArrayAdapter(getApplicationContext(), R.layout.listitem_chat);

        chatListView.setAdapter(adapter);

        txtMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    adapter.add(new ChatMessage(false, txtMessage.getText().toString()));
                    txtMessage.setText("");
                    return true;
                }
                return false;
            }
        });

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
                        scrollMyListViewToBottom();
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
        } catch (JSONException ignored) {
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
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void receiveMessage() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        query.setLimit(-1);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> messages, ParseException e) {
                if (e == null) {
                    adapter.clear();
                    for (int i = messages.size() - 1; i >= 0; i--) {
                        if (messages.get(i).getString(USER_NAME_KEY).equals(ParseUser.getCurrentUser().getUsername())) {
                            addItemstoListView(messages.get(i).getString(USER_NAME_KEY)
                                    + ": " + messages.get(i).getString("message"), false);
                        } else if (messages.get(i).getString(USER_NAME_KEY).equals(username)) {
                            addItemstoListView(messages.get(i).getString(USER_NAME_KEY)
                                    + ": " + messages.get(i).getString("message"), true);
                        }
                    }
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

    public void addItemstoListView(String message, boolean left) {
        ChatMessage chatMessage = new ChatMessage(left, message);
        adapter.add(chatMessage);
        adapter.notifyDataSetChanged();
        chatListView.invalidate();
        scrollMyListViewToBottom();
    }

    private void scrollMyListViewToBottom() {
        chatListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatListView.setSelection(adapter.getCount() - 1);
            }
        });
    }

    @Override
    protected void onStop() {
        PushReceiver.isBackground = true;
        super.onStop();
    }
}
