package com.mobdeve.s11.group19.bon_inventaire;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    /**
     * Initializes the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.tvHomeGreeting = findViewById(R.id.tv_home_greeting);
        initFirebase();
        initConfiguration();
        initGreeting();
        initAddList();
        initAddItem();
        initSeeLists();
        initSeeItems();
        initHomeSettings();
    }

    /**
     * Retrieve an instance of the database using getInstance().
     */
    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
    }

    /**
     * Initializes the name of the user to be used for the greeting message in this activity.
     */
    private void initGreeting() {
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.name.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue().toString();
                        String[] splitName = name.split(" ");
                        String firstName = splitName[0].trim();
                        String title = firstName.substring(0,1).toUpperCase() + firstName.substring(1).toLowerCase();
                        tvHomeGreeting.setText("Bonjour, " + title + "! ");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DatabaseError: ", error.toString());
                    }
                });
    }

    /**
     * Set the flags of the window, as per the WindowManager.LayoutParams flags.
     */
    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Initializes the intent for the next activity (adding a list).
     */
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

    /**
     * Initializes the intent for the next activity (adding an item).
     */
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

    /**
     * Initializes the intent for the next activity (viewing all lists).
     */
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

    /**
     * Initializes the intent for the next activity (viewing all items).
     */
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

    /**
     * Initializes the intent for the next activity (viewing the account settings).
     */
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