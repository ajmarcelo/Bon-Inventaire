package com.mobdeve.s11.group19.bon_inventaire;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AccountEditActivity extends AppCompatActivity {

    private ImageButton ibConfirm;
    private ImageButton ibCancel;
    private EditText etName;
    private EditText etPassword;
    private EditText etConfirmPassword;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        initFirebase();
        initConfiguration();
        initConfirm();
        initCancel();

    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
        this.mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.etName = findViewById(R.id.et_account_edit_name);
        this.etPassword = findViewById(R.id.et_account_edit_password);
        this.etConfirmPassword = findViewById(R.id.et_account_edit_confirm_password);
    }


    private void initConfirm() {
        this.ibConfirm = findViewById(R.id.ib_account_edit_confirm);
        this.ibConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                if(!checkField(name,password,confirmPassword))
                    updateUser(name,password);
            }
        });
    }

    private boolean checkField(String name, String password, String confirmPassword) {
        boolean hasError = false;

        if(name.isEmpty()) {
            this.etName.setError("Required Field");
            this.etName.requestFocus();
            hasError = true;
        }

        if(password.isEmpty()) {
            this.etPassword.setError("Required Field");
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

    private void updateUser(String name, String password) {
//        this.pbRegister.setVisibility(View.VISIBLE);

        this.mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.name.name())
                .setValue(name)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            mUser.updatePassword(password)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(AccountEditActivity.this, "User Information Successfully Updated", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    private void initCancel() {
        this.ibCancel = findViewById(R.id.ib_account_edit_cancel);
        this.ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}