package com.mobdeve.s11.group19.bon_inventaire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageButton ibRegister;
    private ImageButton ibLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initConfiguration();
        initRegister();
        initLogin();
    }

    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initRegister() {
        this.ibRegister = findViewById(R.id.ib_main_register);
        this.ibRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

                startActivity(intent);
            }
        });
    }

    private void initLogin() {
        this.ibLogin = findViewById(R.id.ib_main_login);
        this.ibLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);

                startActivity(intent);
            }
        });
    }
}