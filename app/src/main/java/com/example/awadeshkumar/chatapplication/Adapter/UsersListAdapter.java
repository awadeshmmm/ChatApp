package com.example.awadeshkumar.chatapplication.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.awadeshkumar.chatapplication.R;
import com.example.awadeshkumar.chatapplication.views.RoundedImageView;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Awadesh Kumar on 11/24/2015.
 */
public class UsersListAdapter extends BaseAdapter {
    List<ParseUser> usersList;
    Context mContext;

    public UsersListAdapter(List<ParseUser> usersList, Context context) {
        this.usersList = usersList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public Object getItem(int i) {
        return usersList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        TextView userNameTextView;
        final RoundedImageView profileImageView;
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.row_users_list, parent, false);
            userNameTextView = (TextView) convertView.findViewById(R.id.username);
            profileImageView = (RoundedImageView) convertView.findViewById(R.id.profileImage);
            convertView.setTag(R.id.username, userNameTextView);
            convertView.setTag(R.id.profileImage, profileImageView);
        } else {
            userNameTextView = (TextView) convertView.getTag(R.id.username);
            profileImageView = (RoundedImageView) convertView.getTag(R.id.profileImage);
        }
        userNameTextView.setText(usersList.get(i).getString("username"));
        //  Picasso.with(mContext).load("https://graph.facebook.com/887740027999593/picture?type=large").into(profileImageView);
        ParseFile file = usersList.get(i).getParseFile("profilePicture");
        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        profileImageView.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
                        // data has the bytes for the resume
                    } else {
                        // something went wrong
                    }
                }
            });
        }

        return convertView;
    }
}
