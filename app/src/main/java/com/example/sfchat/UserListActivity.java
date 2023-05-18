package com.example.sfchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {
    private DatabaseReference userDBRef;
    private ChildEventListener userChildEventListener;
    private ArrayList<User> userArrayList;
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private FirebaseAuth auth;
    private RecyclerView.LayoutManager userLayoutManager;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Intent intent = getIntent();
        if(intent!=null) {
            userName = intent.getStringExtra("userName");
        }else{
            userName = "default user";
        }


        userArrayList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();

        recyclerViewBuild();
        userDBRefListener();
    }


    private void recyclerViewBuild() {
        userRecyclerView = findViewById(R.id.recyclerUsersView);
        userRecyclerView.setHasFixedSize(true);

        userLayoutManager = new LinearLayoutManager(this);
        userAdapter = new UserAdapter(userArrayList);
        userAdapter.setOnUserClickListener(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(int position) {
                goToChat(position);
            }
        });

        userRecyclerView.setLayoutManager(userLayoutManager);
        userRecyclerView.setAdapter(userAdapter);

        userRecyclerView.addItemDecoration(new DividerItemDecoration(userRecyclerView.getContext() ,DividerItemDecoration.VERTICAL ));
    }

    private void userDBRefListener() {
        userDBRef = FirebaseDatabase.getInstance().getReference().child("users");

        if (userChildEventListener == null) {
            userChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    User user = snapshot.getValue(User.class);
                  /*для удаления чата с самим собой(можно сделать избранным)*/
                   if(!user.getId().equals(auth.getCurrentUser().getUid())) {
                       user.setAvatarImageResource(R.drawable.userimage);
                       userArrayList.add(user);
                       userAdapter.notifyDataSetChanged();
                   }else{
                       user.setAvatarImageResource(R.drawable.favimage);
                       user.setName("favorites");
                       userArrayList.add(user);
                       userAdapter.notifyDataSetChanged();
                   }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            userDBRef.addChildEventListener(userChildEventListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.signOut) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(UserListActivity.this, SignInActivity.class));
                return true;}
            else{
                return super.onOptionsItemSelected(item);
        }

    }

    private void goToChat(int position) {
        Intent intent = new Intent(UserListActivity.this , ChatActivity.class);
        intent.putExtra("getterUserId" ,userArrayList.get(position).getId());
        intent.putExtra("getterUserName", userArrayList.get(position).getName());
        intent.putExtra("userName" , userName);
        startActivity(intent);



    }
}