package com.example.awadeshkumar.chatapplication.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.awadeshkumar.chatapplication.R;
import com.example.awadeshkumar.chatapplication.Utilities.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView messageTextView;
    private List<ChatMessage> messages = new ArrayList<ChatMessage>();
    private LinearLayout wrapper;

    @Override
    public void add(ChatMessage object) {
        messages.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public int getCount() {
        return this.messages.size();
    }

    public ChatMessage getItem(int index) {
        return this.messages.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listitem_chat, parent, false);
        }

        wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

        ChatMessage chatMessage = getItem(position);

        messageTextView = (TextView) row.findViewById(R.id.chat_item);

        messageTextView.setText(chatMessage.message);

        messageTextView.setBackgroundResource(chatMessage.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
        wrapper.setGravity(chatMessage.left ? Gravity.LEFT : Gravity.RIGHT);

        return row;
    }

}
