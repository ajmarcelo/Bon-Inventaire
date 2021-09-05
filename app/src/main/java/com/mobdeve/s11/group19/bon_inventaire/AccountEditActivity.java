package com.mobdeve.s11.group19.bon_inventaire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountEditActivity extends AppCompatActivity {

    private ImageButton ibConfirm;
    private ImageButton ibCancel;
    private EditText etName;
    private EditText etNewPassword;
    private EditText etConfirmNewPassword;
    private EditText etCurrentPassword;
    private String initialName;
    private ProgressBar pbAccountEdit;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private AuthCredential authCredential;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        initFirebase();
        initCurrentUserInfo();
        initConfiguration();
        initConfirm();
        initCancel();

    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
        this.mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void initCurrentUserInfo() {
        this.mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.name.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue().toString();
                        etName.setText(name);
                        initialName = name;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.etName = findViewById(R.id.et_account_edit_name);
        this.etNewPassword = findViewById(R.id.et_account_edit_password);
        this.etConfirmNewPassword = findViewById(R.id.et_account_edit_confirm_password);
        this.etCurrentPassword = findViewById(R.id.et_account_edit_current_password);
        this.pbAccountEdit = findViewById(R.id.pb_account_edit);
    }

    private void initConfirm() {
        this.ibConfirm = findViewById(R.id.ib_account_edit_confirm);
        this.ibConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();
                String confirmNewPassword = etConfirmNewPassword.getText().toString().trim();
                String currentPassword = etCurrentPassword.getText().toString().trim();

                if(!checkField(name, newPassword,confirmNewPassword,currentPassword))
                    updateUser(name,newPassword,currentPassword);
            }
        });
    }

    private boolean checkField(String name, String newPassword, String confirmNewPassword, String currentPassword) {
        boolean hasError = false;

        if(newPassword.isEmpty() && confirmNewPassword.isEmpty() && name.equals(initialName)) {
            Toast.makeText(AccountEditActivity.this, "No Changes Has Been Made", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        else {
            if(name.isEmpty()) {
                this.etName.setError("Name cannot be empty");
                this.etName.requestFocus();
                hasError = true;
            }

            if(!newPassword.isEmpty() || !confirmNewPassword.isEmpty()) {
                if(!newPassword.isEmpty() && (newPassword.length() < 6)) {
                    this.etNewPassword.setError("Must be at least 6 characters");
                    this.etNewPassword.requestFocus();
                }
                if(!confirmNewPassword.equals(newPassword)) {
                    this.etConfirmNewPassword.setError("New Password does not match");
                    this.etConfirmNewPassword.requestFocus();
                    hasError = true;
                }
            }

            if(currentPassword.isEmpty()) {
                this.etCurrentPassword.setError("Required Field");
                this.etCurrentPassword.requestFocus();
                hasError = true;
            }
        }

        return hasError;
    }

    private void updateUser(String newName, String newPassword, String currentPassword) {
        this.pbAccountEdit.setVisibility(View.VISIBLE);

        this.authCredential = EmailAuthProvider.getCredential(this.mUser.getEmail(), currentPassword);
        this.mUser.reauthenticate(this.authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void void1) {
                        if(!newName.equals(initialName)) {
                            mDatabase.getReference(Collections.users.name())
                                    .child(mAuth.getCurrentUser().getUid()).child(Collections.name.name())
                                    .setValue(newName)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(AccountEditActivity.this, "Updated User Name", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        if(!newPassword.isEmpty() && !newPassword.equals(currentPassword)) {
                            mUser.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void void1) {
                                        Toast.makeText(AccountEditActivity.this, "Updated User Password", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                        if(newName.equals(initialName) && newPassword.equals(currentPassword)) {
                            Toast.makeText(AccountEditActivity.this, "No User Information To Be Updated", Toast.LENGTH_SHORT).show();
                            pbAccountEdit.setVisibility(View.GONE);
                            etCurrentPassword.setText("");
                            etNewPassword.setText("");
                            etConfirmNewPassword.setText("");
                        }
                        else {
                            Toast.makeText(AccountEditActivity.this, "Updated Account Information", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AccountEditActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AccountEditActivity.this, "You entered an incorrect password",Toast.LENGTH_SHORT).show();
                        pbAccountEdit.setVisibility(View.GONE);
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