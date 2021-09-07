package com.mobdeve.s11.group19.bon_inventaire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton ibConfirm;
    private TextView tvLogin;
    private ImageButton ibBack;
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private ProgressBar pbRegister;

    private ArrayList<List> dataList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    /**
     * For initializing activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.etName = findViewById(R.id.et_register_name);
        this.etEmail = findViewById(R.id.et_register_email);
        this.etPassword = findViewById(R.id.et_register_password);
        this.etConfirmPassword = findViewById(R.id.et_register_confirm_password);
        this.pbRegister = findViewById(R.id.pb_register);

        this.dataList = new ArrayList<List>();
        dataList.add(new List("Unlisted", "Default List",0));

        initFirebase();
        initConfiguration();
        initConfirm();
        initBack();
        initLogin();
    }

    /**
     * Retrieve an instance of the database using getInstance().
     */
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
    }

    /**
     * Set the flags of the window, as per the WindowManager.LayoutParams flags.
     */
    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Initializes confirmation of registration
     */
    private void initConfirm() {
        this.ibConfirm = findViewById(R.id.ib_register_confirm);
        this.ibConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                if(!name.isEmpty())
                    name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if(!checkField(name, email, password, confirmPassword)) {

                    User user = new User(email, password);
                    storeUser(user,name);

                }
            }
        });
    }

    /**
     * Checks if the input for each field is valid.
     * @param name
     * @param email
     * @param password
     * @param confirmPassword
     * @return
     */
    private boolean checkField(String name, String email, String password, String  confirmPassword) {
        boolean hasError = false;

        if(name.isEmpty()) {
            this.etName.setError("Required Field");
            this.etName.requestFocus();
            hasError = true;
        }
        if(email.isEmpty()) {
            this.etEmail.setError("Required Field");
            this.etEmail.requestFocus();
            hasError = true;
        }
        if(password.isEmpty()) {
            this.etPassword.setError("Required Field");
            this.etPassword.requestFocus();
            hasError = true;
        }
        else if(password.length() < 6) {
            this.etPassword.setError("Must be at least 6 characters");
            this.etPassword.requestFocus();
            hasError = true;
        }

        if(confirmPassword.isEmpty()) {
            this.etConfirmPassword.setError("Required Field");
            this.etConfirmPassword.requestFocus();
            hasError = true;
        }

        else if(!confirmPassword.equals(password)) {
            this.etConfirmPassword.setError("Password does not match");
            this.etConfirmPassword.requestFocus();
            hasError = true;
        }

        return hasError;
    }

    /**
     * Stores the registered user to both realtime database and authentication
     * of firebase
     */
    private void storeUser(User user, String name) {
        this.pbRegister.setVisibility(View.VISIBLE);

        this.mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDatabase.getReference(Collections.users.name())
                                    .child(mAuth.getCurrentUser().getUid()).child(Collections.name.name())
                                    .setValue(name)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                mDatabase.getReference(Collections.users.name())
                                                        .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
                                                        .setValue(dataList)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()) {
                                                                    successfulRegistration();
                                                                } else {
                                                                    failedRegistration();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                failedRegistration();
                                            }
                                        }
                                    });
                        } else {
                            failedRegistration();
                        }
                    }
                });
    }

    /**
     * called when registration is successful
     */
    private void successfulRegistration() {
        this.pbRegister.setVisibility(View.GONE);
        Toast.makeText(this, "User Successfully Registered", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * called when registration failed
     */
    private void failedRegistration() {
        this.pbRegister.setVisibility(View.GONE);
        Toast.makeText(this, "User Registration Failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Initializes the intent for the next activity (navigating back)
     */
    private void initBack() {
        this.ibBack = findViewById(R.id.ib_register_back);
        this.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Initializes the intent for the next activity (logging in)
     */
    private void initLogin() {
        this.tvLogin = findViewById(R.id.tv_register_login);
        this.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                startActivity(intent);
                finish();
            }
        });
    }
}