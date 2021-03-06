package com.mobdeve.s11.group19.bon_inventaire;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsAccountActivity extends AppCompatActivity {

    private TextView tvEdit;
    private TextView tvDelete;
    private ImageButton ibLogout;
    private ImageButton ibBack;

    private Dialog dialog;
    private Button btnDeleteAccountContinue;
    private Button btnDeleteAccountCancel;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_account);
        this.tvDelete = findViewById(R.id.tv_settings_account_delete);

        initConfirmationDialogBox();
        initFirebase();
        initConfiguration();
        initEdit();
        initDelete();
        initLogout();
        initBack();
    }

    /**
     * Initializes the dialog box confirmation for deleting the user's account.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initConfirmationDialogBox() {
        dialog = new Dialog(SettingsAccountActivity.this);
        dialog.setContentView(R.layout.confirmation_delete_account);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.confirmation;
        btnDeleteAccountCancel = dialog.findViewById(R.id.btn_confirm_delete_account_cancel);
        btnDeleteAccountContinue = dialog.findViewById(R.id.btn_confirm_delete_account_continue);
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
     * Initializes the intent for the next activity (editing the current user's account information).
     */
    private void initEdit() {
        this.tvEdit = findViewById(R.id.tv_settings_account_edit);
        this.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsAccountActivity.this, AccountEditActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initializes the deletion of the current user's account.
     */
    private void initDelete() {
        //Triggers the dialog box for the confirmation for deleting the current user's account
        this.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        //Proceeds to deleting the account of the user in the database
        this.btnDeleteAccountContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsAccountActivity.this, MainActivity.class);

                mDatabase.getReference(Collections.users.name())
                        .child(mAuth.getCurrentUser().getUid()).removeValue().
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                startActivity(intent);
            }
        });

        //Cancels the deletion of the account of the user
        this.btnDeleteAccountCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Initializes the intent for the next activity (finishing the session of the current user).
     */
    private void initLogout() {
        this.ibLogout = findViewById(R.id.ib_logout);
        this.ibLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsAccountActivity.this, MainActivity.class);

                mAuth.signOut();
                Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Initializes the intent for the next activity (navigating back).
     */
    private void initBack() {
        this.ibBack = findViewById(R.id.ib_settings_account_back);
        this.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsAccountActivity.this, HomeActivity.class);

                startActivity(intent);
            }
        });
    }
}