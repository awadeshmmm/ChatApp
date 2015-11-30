package com.example.awadeshkumar.chatapplication;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.awadeshkumar.chatapplication.Utilities.Logger;
import com.example.awadeshkumar.chatapplication.Utilities.UncaughtExceptionHandler;
import com.example.awadeshkumar.chatapplication.helpers.SharedPreferenceManager;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Launching activity
 */

public class MainActivity extends AppCompatActivity {
    Toolbar mToolbar;
    AutoCompleteTextView userNameAutoCompleteTextView;
    EditText passwordEditText;
    Button userLoginButton;
    URL profile_pic = null;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    String id, name, email;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
                getApplicationContext()));
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(this, "LOGIN_DETAILS");
        String isLoggedIn = sharedPreferenceManager.getStringPreference("loggedIn");
        if (isLoggedIn != null && isLoggedIn.equals("true")) {
            Intent intent = new Intent(this, UsersListActivity.class);
            startActivity(intent);
            finish();

        }
        setContentView(R.layout.activity_main);
        userNameAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        userLoginButton = (Button) findViewById(R.id.sign_in_button);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        Runtime runtime = Runtime.getRuntime();
        Logger.appendInfoLog("===========================================================================\nApp started\nTotal memory allocated: " + memoryInfo.totalMem + "\nFree Memory: " + memoryInfo.availMem + "\n" + runtime.freeMemory() + "\n" + runtime.totalMemory() + "\n" + runtime.maxMemory(), "SPLASH");
        mContext = this;
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile, email, user_birthday, user_friends"));
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        SpannableString title = new SpannableString("Chat Application");
        getSupportActionBar().setTitle(title);
        // To login in Parse for existing users
        userLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = userNameAutoCompleteTextView.getText().toString();
                String passWord = passwordEditText.getText().toString();
                ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {
                            SharedPreferenceManager preferenceManager = new SharedPreferenceManager(mContext, "LOGIN_DETAILS");
                            preferenceManager.storeStringPreference("loggedIn", "true");

                            Toast.makeText(getApplicationContext(),
                                    "Successfully Logged in",
                                    Toast.LENGTH_LONG).show();
                            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                            installation.put("username", userName);
                            installation.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {

                                    }
                                }
                            });
                            // If user exist and authenticated, send user to UsersList.class
                            Intent intent = new Intent(mContext, UsersListActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "No such user exist, please signUp using facebook login",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
        // Handling login using Facebook credentials
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String accessToken = loginResult.getAccessToken()
                        .getToken();
                Log.i("accessToken", accessToken);

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,
                                                    GraphResponse response) {

                                Log.i("LoginActivity", response.toString());
                                try {
                                    id = object.getString("id");
                                    try {
                                        profile_pic = new URL(
                                                "https://graph.facebook.com/" + id + "/picture?type=large");
                                        Log.i("profile_pic",
                                                profile_pic + "");

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                    name = object.getString("name");
                                    email = object.getString("email");
                                    //   userSignUp(name, email, profile_pic);
                                    ParseUser user = new ParseUser();
                                    user.setUsername(name);
                                    user.setEmail(email);
                                    ParseGeoPoint point = new ParseGeoPoint(40.0, -30.0);
                                    user.put("location", point);
                                    user.setPassword(id);
                                    //signUp in parse
                                    user.signUpInBackground(new SignUpCallback() {
                                        @Override
                                        public void done(com.parse.ParseException e) {

                                            if (e == null) {
                                                /**
                                                 * If users login first time, send user to UsersListActivity.class
                                                 */

                                                Picasso.with(mContext).load(profile_pic.toString()).into(new Target() {
                                                    @Override
                                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                        ParseUser currentUser = ParseUser.getCurrentUser();
                                                        if (bitmap != null) {
                                                            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
                                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayBitmapStream);
                                                            byte[] byteArray = byteArrayBitmapStream.toByteArray();
                                                            ParseFile saveImageFile = new ParseFile("profilePicture.jpg", byteArray);
                                                            currentUser.put("profilePicture", saveImageFile);
                                                        }
                                                        currentUser.saveInBackground();

                                                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                                        installation.put("username", name);
                                                        installation.saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                if (e == null) {
                                                                    Toast.makeText(getApplicationContext(),
                                                                            "Successfully Registered",
                                                                            Toast.LENGTH_LONG).show();
                                                                    SharedPreferenceManager preferenceManager = new SharedPreferenceManager(mContext, "LOGIN_DETAILS");
                                                                    preferenceManager.storeStringPreference("user-details", "true");
                                                                    Intent intent = new Intent(mContext, UsersListActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onBitmapFailed(Drawable errorDrawable) {

                                                    }

                                                    @Override
                                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                    }
                                                });
                                            } else if (e.toString().contains("com.parse.ParseRequest$ParseRequestException: username " + name + " already taken")) {
                                                // If user already exist
                                                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                                if (!installation.containsKey(name)) {
                                                    installation.put("username", name);
                                                    installation.saveInBackground();
                                                }
                                                loginInParse(name, id);

                                            } else {
                                                Toast.makeText(mContext, "Some error has occured", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields",
                        "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void userSignUp(String name, String email, final URL profilePicUrl) {
        ParseUser user = new ParseUser();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword("12345");
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Picasso.with(mContext).load(profilePicUrl.toString()).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            ParseUser currentUser = ParseUser.getCurrentUser();
                            if (bitmap != null) {
                                ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayBitmapStream);
                                byte[] byteArray = byteArrayBitmapStream.toByteArray();
                                ParseFile saveImageFile = new ParseFile("profilePicture.jpg", byteArray);
                                currentUser.put("profilePicture", saveImageFile);
                            }
                            currentUser.saveInBackground();
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                    try {
                        Bitmap dp = Picasso.with(mContext).load(profilePicUrl.toString()).get();

                    } catch (IOException exception) {

                    }
                    Intent intent = new Intent(mContext, UsersListActivity.class);
                    startActivity(intent);
                } else {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    try {
                        Bitmap dp = Picasso.with(mContext).load(profilePicUrl.toString()).get();
                        if (dp != null) {
                            ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
                            dp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayBitmapStream);
                            byte[] byteArray = byteArrayBitmapStream.toByteArray();
                            ParseFile saveImageFile = new ParseFile("profilePicture.jpg", byteArray);
                            currentUser.put("profilePicture", saveImageFile);
                        }
                        currentUser.saveInBackground();
                    } catch (IOException exception) {

                    }
                    Intent intent = new Intent(mContext, UsersListActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    /**
     * Login in parse if user already exists
     *
     * @param userName
     * @param passWord
     */
    public void loginInParse(final String userName, String passWord) {
        ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
            @Override
            public void done(ParseUser user, com.parse.ParseException e) {
                if (user != null) {

                    Toast.makeText(getApplicationContext(),
                            "Successfully Logged in",
                            Toast.LENGTH_LONG).show();
                    SharedPreferenceManager preferenceManager = new SharedPreferenceManager(mContext, "LOGIN_DETAILS");
                    preferenceManager.storeStringPreference("loggedIn", "true");
                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    if (!installation.containsKey(userName)) {
                        installation.put("username", userName);
                        installation.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Intent intent = new Intent(mContext, UsersListActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(mContext, "Some error has occured !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Intent intent = new Intent(mContext, UsersListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {

                }
            }
        });
    }
}