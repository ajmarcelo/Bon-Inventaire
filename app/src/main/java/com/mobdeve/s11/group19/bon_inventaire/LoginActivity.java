package com.mobdeve.s11.group19.bon_inventaire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ImageButton ibConfirm;
    private TextView tvRegister;
    private ImageButton ibBack;
    private EditText etEmail;
    private EditText etPassword;
    private ProgressBar pbLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initFirebase();
        initConfiguration();
        initConfirm();
        initBack();
        initRegister();

    }

    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
    }

    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.etEmail = findViewById(R.id.et_login_email);
        this.etPassword = findViewById(R.id.et_login_password);
        this.pbLogin = findViewById(R.id.pb_login);
    }

    private void initConfirm() {
        this.ibConfirm = findViewById(R.id.ib_login_confirm);
        this.ibConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if(!checkField(email, password))
                    signIn(email,password);
            }
        });
    }

    private void signIn(String email, String password){
        this.pbLogin.setVisibility(View.VISIBLE);

        this.mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            successfulLogin();
                        } else {
                            failedLogin();
                        }
                    }
                });
    }

    private boolean checkField(String email, String password) {
        boolean hasError = false;

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

        return hasError;
    }

    private void successfulLogin() {
        this.pbLogin.setVisibility(View.GONE);
        Toast.makeText(this, "User Successfully Logged In", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void failedLogin() {
        this.pbLogin.setVisibility(View.GONE);
        Toast.makeText(this, "Email or Password is not found", Toast.LENGTH_SHORT).show();
    }

    private void initBack() {
        this.ibBack = findViewById(R.id.ib_login_back);
        this.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initRegister() {
        this.tvRegister = findViewById(R.id.tv_login_register);
        this.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);

                startActivity(intent);
                finish();
            }
        });
    }
}