package com.example.sfchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText repeatPasswordEditText;
    private TextView loginTextView;
    private Button loginButton;
    private ImageView icon;
    private FirebaseDatabase database;
    private DatabaseReference usersDBRef;
    private boolean loginModeExit = false;

    private static final String TAG = "SignInActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();
        icon = findViewById(R.id.imageViewIcon);
        nameEditText = findViewById(R.id.editTextTextName);
        emailEditText = findViewById(R.id.editTextTextEmail);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        repeatPasswordEditText = findViewById(R.id.editTextTextRepeatPassword);
        loginTextView = findViewById(R.id.Login);
        loginButton = findViewById(R.id.button);
        icon.animate().alpha(1).setDuration(2000);


        database = FirebaseDatabase.getInstance();
        usersDBRef = database.getReference("users");


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginSignUpUser(emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString().trim());
            }
        });

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, UserListActivity.class));
        }


    }

    public void loginSignUpUser(String email, String password) {


        if (loginModeExit) {

            if (passwordEditText.getText().toString().trim().length() < 6) {
                Toast.makeText(this, "Password is to short. Please write 6 symbols", Toast.LENGTH_LONG).show();
            } else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
//                                    createUser(user);
                                    Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                    intent.putExtra("userName",nameEditText.getText().toString());
                                    startActivity(intent);
//                            updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                                }
                            }
                        });
            }
        } else {
            if (!passwordEditText.getText().toString().trim().
                    equals(repeatPasswordEditText.getText().toString().trim())) {
                Toast.makeText(this, "Password mismatch", Toast.LENGTH_SHORT).show();
            } else if (passwordEditText.getText().toString().trim().length() < 6) {
                Toast.makeText(this, "Password is to short. Please write 6 symbols", Toast.LENGTH_LONG).show();
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    createUser(user);
                                    Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                    intent.putExtra("userName", nameEditText.getText().toString().trim());
                                    startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    private void createUser(FirebaseUser firebaseUser) {
        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(nameEditText.getText().toString().trim());
        usersDBRef.push().setValue(user);


    }


    public void loginMode(View v) {
        if (loginModeExit) {
            loginModeExit = false;
            loginButton.setText("Sign Up");
            loginTextView.setText("Tap to Log in");
            nameEditText.setVisibility(View.VISIBLE);
            repeatPasswordEditText.setVisibility(View.VISIBLE);
        } else {
            loginModeExit = true;
            loginButton.setText("Log in");
            loginTextView.setText("Tap to Sign up");
            repeatPasswordEditText.setVisibility(View.GONE);

        }
    }


}