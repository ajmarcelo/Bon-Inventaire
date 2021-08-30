package com.mobdeve.s11.group19.bon_inventaire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private ImageButton ibAddList;
    private ImageButton ibAddItem;
    private ImageButton ibSeeLists;
    private ImageButton ibSeeItems;
    private FloatingActionButton fabHomeSettings;
    private TextView tvHomeGreeting;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initFirebase();
        initConfiguration();
        initGreeting();
        initAddList();
        initAddItem();
        initSeeLists();
        initSeeItems();
        initHomeSettings();
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
    }

    private void initGreeting() {
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.name.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue().toString();
                        String title = name.toUpperCase().charAt(0) + name.substring(1,name.length());
                        tvHomeGreeting.setText("Bonjour, " + title + "! ");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
//                });
    }

    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.tvHomeGreeting = findViewById(R.id.tv_home_greeting);
    }

    private void initAddList() {
        this.ibAddList = findViewById(R.id.ib_home_add_list);
        this.ibAddList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddListActivity.class);

                startActivity(intent);
            }
        });
    }

    private void initAddItem() {
        this.ibAddItem = findViewById(R.id.ib_home_add_item);
        this.ibAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddItemActivity.class);

                startActivity(intent);
            }
        });
    }

    private void initSeeLists() {
        this.ibSeeLists = findViewById(R.id.ib_home_see_lists);
        this.ibSeeLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ListActivity.class);

                startActivity(intent);
            }
        });
    }

    private void initSeeItems() {
        this.ibSeeItems = findViewById(R.id.ib_home_see_items);
        this.ibSeeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ItemAllActivity.class);

                startActivity(intent);
            }
        });
    }

    private void initHomeSettings() {
        this.fabHomeSettings = findViewById(R.id.fab_home_settings);
        this.fabHomeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsAccountActivity.class);

                startActivity(intent);
            }
        });
    }
}