package com.example.awadeshkumar.chatapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.awadeshkumar.chatapplication.Adapter.UsersListAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class UsersListActivity extends AppCompatActivity {
    Context mContext;
    ListView listView;
    List<ParseUser> mUsersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        listView = (ListView) findViewById(R.id.users_list_view);
        mContext = this;
        getParseUsers();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("user", mUsersList.get(position).getUsername());
                startActivity(intent);

            }
        });
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SpannableString s = new SpannableString("Select user");
        getSupportActionBar().setTitle(s);
    }

    private void configureUserNameInParse(final String userName) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("username", userName);
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                }
            }
        });
    }

    public void getParseUsers() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                query.findInBackground(new FindCallback<ParseUser>() {
                    @Override
                    public void done(List<ParseUser> usersList, com.parse.ParseException e) {
                        if (e == null) {
                            mUsersList = usersList;
                            UsersListAdapter usersListAdapter = new UsersListAdapter(usersList, mContext);
                            listView.setAdapter(usersListAdapter);
                        } else {
                            showToast("No users to load");
                        }
                    }
                });
            }
        });

    }


    private void openParseChatActivity(String name) {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtra("user", name);
        startActivity(i);
    }

    @SuppressLint("ShowToast")
    public void showToast(String text) {
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
