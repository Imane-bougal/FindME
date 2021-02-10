package com.imaneb.findme.ui.message;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.imaneb.findme.MapFriendActivity;
import com.imaneb.findme.R;
import com.imaneb.findme.adapter.MessageRecyclerAdapter;
import com.imaneb.findme.data.model.Message;
import com.imaneb.findme.data.model.User;
import com.imaneb.findme.data.repository.AuthRepository;
import com.imaneb.findme.utils.Constants;
import com.imaneb.findme.viewModels.ViewModelProviderFactory;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class MessageActivity extends DaggerAppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MessageActivity_Tag";
    private MessageViewModel messageViewModel;
    private EditText messageInput;
    private ImageView messageSendBtn;
    private ImageView locatebtn;
    private RecyclerView recyclerView;
    private MessageRecyclerAdapter messageRecyclerAdapter = new MessageRecyclerAdapter();

    @Inject
    ViewModelProviderFactory providerFactory;
    @Inject
    AuthRepository authRepository;
    @Inject
    RequestManager requestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        messageViewModel = new ViewModelProvider(getViewModelStore(),providerFactory).get(MessageViewModel.class);
        getUserInfo();
        intToolbar();
        intView();
        observeFriendInfo();
        observeMessageList();
        observeNewMessage();
    }

    private void observeFriendInfo() {
        messageViewModel.getFriendInfo().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                getSupportActionBar().setTitle(user.getDisplayName());
                if(user.isOnline()){
                    getSupportActionBar().setSubtitle("Active now");
                }

                messageRecyclerAdapter.setUserInfo(authRepository.getCurrentUid(),user.getImage(),requestManager);
            }
        });
    }

    private void observeNewMessage() {
        messageViewModel.getNewMessage().observe(this, new Observer<Message>() {
            @Override
            public void onChanged(Message message) {
                Log.d(TAG, "onChanged: "+message.getMessage());
                messageRecyclerAdapter.setNewMessageAdd(message);
                recyclerView.smoothScrollToPosition(messageRecyclerAdapter.getItemCount()-1);
            }
        });
    }

    private void observeMessageList() {
        messageViewModel.getAllMessage().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                Log.d(TAG, "onChanged: "+messages.size());
                messageRecyclerAdapter.setMessageList(messages);
                recyclerView.setAdapter(messageRecyclerAdapter);
                if (messageRecyclerAdapter.getItemCount()>1){
                    recyclerView.smoothScrollToPosition(messageRecyclerAdapter.getItemCount()-1);
                }
            }
        });
    }

    private void intView() {
        messageInput = findViewById(R.id.message_input);
        messageSendBtn = findViewById(R.id.message_send_btn);
        locatebtn = findViewById(R.id.locate_btn);
        recyclerView = findViewById(R.id.recyclerView);
        messageSendBtn.setOnClickListener(this);
        locatebtn.setOnClickListener(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    private void getUserInfo() {
        Intent intent = getIntent();
        if (intent.hasExtra("key_uid")) {
            String uid = intent.getStringExtra("key_uid");
            messageViewModel.setProfileUid(uid);
            getIntent().removeExtra("key_uid");
        }
    }
    private void intToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTextAppearance);
        toolbar.setSubtitleTextAppearance(this, R.style.ToolbarSubtitleTextAppearance);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id == android.R.id.home ) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.message_send_btn) {
            sendMessage();
        }
        if (id == R.id.locate_btn) {
            locateFriend();
        }
    }

    private void locateFriend() {
        String imei = messageViewModel.LocateFriend();
        Intent i = new Intent(MessageActivity.this, MapFriendActivity.class);
        i.putExtra("Fimei", imei);
        startActivity(i);

    }

    private void sendMessage() {
        String inputMessage = messageInput.getText().toString();
        if (!inputMessage.isEmpty()) {
            Message message = new Message(Constants.TEXT_TYPE, inputMessage, "default");
            messageViewModel.sendMessage(message);
            messageInput.setText("");
        }

    }
}
