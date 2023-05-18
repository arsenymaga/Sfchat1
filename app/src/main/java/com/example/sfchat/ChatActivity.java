package com.example.sfchat;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    private ListView messageListView;
    private AwesomeMessageAdapter adapter;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private ImageButton imageButton;
    private Button sendMessageButton;
    private EditText messageEditText;
    private String userName;
    private FirebaseDatabase database;
    private DatabaseReference messagesDBRef;
    private ChildEventListener messagesChildEventListener;

    private String getterUserName;

    private DatabaseReference usersDBRef;
    private ChildEventListener usersChildEventListener;

    private static final int RC_IMAGE = 123;

    private FirebaseStorage storage;

    private StorageReference imageStorageRef;

    private String getterUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mediaPlayer = MediaPlayer.create(this, R.raw.pop_up);

        database = FirebaseDatabase.getInstance();
        messagesDBRef = database.getReference().child("messages");
        usersDBRef = database.getReference().child("users");

        progressBar = findViewById(R.id.progressBar);
        imageButton = findViewById(R.id.imageButton);
        sendMessageButton = findViewById(R.id.sendMessage);
        messageEditText = findViewById(R.id.messageEditText);





        storage = FirebaseStorage.getInstance();
        imageStorageRef = storage.getReference().child("chat_images");

        auth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("userName");
            getterUserId = intent.getStringExtra("getterUserId");
            getterUserName = intent.getStringExtra("getterUserName");
        } else {
            userName = "Anonymous";
        }
        setTitle(getterUserName);


        messageListView = findViewById(R.id.listView);
        List<AwesomeMessage> awesomeMessages = new ArrayList<>();
        adapter = new AwesomeMessageAdapter(this, R.layout.useritem, awesomeMessages);
        messageListView.setAdapter(adapter);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    sendMessageButton.setEnabled(true);
                } else {
                    sendMessageButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});


        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AwesomeMessage message = new AwesomeMessage();
                message.setText(messageEditText.getText().toString());
                message.setName(userName);
                message.setSender(auth.getCurrentUser().getUid());
                message.setGetter(getterUserId);
                mediaPlayer.start();
                message.setImageUrl(null);
                messagesDBRef.push().setValue(message);
                messageEditText.setText("");
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.setType("image/*");
                intent1.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent1, "Choose an image"), RC_IMAGE);

            }
        });

        messagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                AwesomeMessage message = snapshot.getValue(AwesomeMessage.class);

                if (message.getSender().equals(auth.getCurrentUser().getUid())
                        && message.getGetter().equals(getterUserId)) {

                    message.setMine(true);
                    adapter.add(message);
                } else if (message.getGetter().equals(auth.getCurrentUser().getUid()) &&
                        message.getSender().equals(getterUserId)) {
                    message.setMine(false);
                    adapter.add(message);
                }


            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }

        ;
        messagesDBRef.addChildEventListener(messagesChildEventListener);

        usersChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    userName = user.getName();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String
                    previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        }

        ;

        usersDBRef.addChildEventListener(usersChildEventListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ChatActivity.this, SignInActivity.class));
            return true;
        }
        else{

            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            StorageReference imageRef = imageStorageRef.child(selectedImageUri.getLastPathSegment());
            UploadTask upload = imageRef.putFile(selectedImageUri);
//            upload = imageRef.putFile(selectedImageUri);

            Task<Uri> urlTask = upload.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        AwesomeMessage message = new AwesomeMessage();
                        message.setImageUrl(downloadUri.toString());
                        message.setName(userName);
                        message.setSender(auth.getCurrentUser().getUid());
                        message.setGetter(getterUserId);
                        messagesDBRef.push().setValue(message);
                    } else {
                        // Handle failures
                    }
                }
            });
        }

    }
}