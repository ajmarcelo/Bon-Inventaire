package com.mobdeve.s11.group19.bon_inventaire;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemAllActivity extends AppCompatActivity {

    private RecyclerView rvAllItems;
    private LinearLayoutManager llmManager;
    private ItemAllAdapter itemAllAdapter;
    private ArrayList<Item> dataItem;
    private FloatingActionButton fabAllItemsAdd;
    private ImageButton ibCancel;
    private TextView tvAllItemsNoItems;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    /**
     * A launcher for a previously-prepared call to start the process of executing an ActivityResultContract
     */
    private ActivityResultLauncher allItemsAddActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        getDataFromDatabase();

                        rvAllItems.setVisibility(View.VISIBLE);
                        tvAllItemsNoItems.setVisibility(View.GONE);
                    }
                }
            }
    );

    /**
     * Initializes the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_items);
        tvAllItemsNoItems = findViewById(R.id.tv_all_items_no_items);
        initFirebase();
        initConfiguration();
        initRecyclerView();
        initAllItemsAdd();
        initCancel();
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
     * Initializes the recycler view of the activity.
     */
    private void initRecyclerView () {
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
                        dataItem =  snapshot.getValue(t);

                        if(dataItem == null){
                            dataItem = new ArrayList<Item>();
                            dataItem.add(new Item("", "Example Item", "Example Item", 1,"2022",0));
                        }

                        rvAllItems = findViewById(R.id.rv_all_items);

                        llmManager = new LinearLayoutManager(ItemAllActivity.this, LinearLayoutManager.VERTICAL, false);
                        rvAllItems.setLayoutManager(llmManager);


                        itemAllAdapter = new ItemAllAdapter(dataItem,ItemAllActivity.this);
                        rvAllItems.setAdapter(itemAllAdapter);

                        if(dataItem.get(0).getItemName().equals("")){
                            rvAllItems.setVisibility(View.GONE);
                            tvAllItemsNoItems.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DatabaseError: ", error.toString());
                    }
                });

    }


    /**
     * Initializes the intent for the next activity (adding an item).
     */
    private void initAllItemsAdd() {
        this.fabAllItemsAdd = findViewById(R.id.fab_all_items_add);
        this.fabAllItemsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemAllActivity.this, AddItemActivity.class);

                allItemsAddActivityResultLauncher.launch(intent);
            }
        });
    }

    /**
     * Initializes the intent for the next activity (navigating back).
     */
    private void initCancel() {
        this.ibCancel = findViewById(R.id.ib_all_items_back);
        this.ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemAllActivity.this, HomeActivity.class);

                startActivity(intent);

                finish();
            }
        });
    }

    /**
     * Called when an activity launched exits, giving the requestCode started it with.
     * @param requestCode was set when calling startActivityForResult() as part of the parameter
     * @param resultCode not used in this overridden method.
     * @param data not used in this overridden method.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2)
            getDataFromDatabase();
    }

    /**
     * Retrieves the data from the database and sets the retrieved data to the current data.
     */
    private void getDataFromDatabase(){
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
                        dataItem =  snapshot.getValue(t);

                        if(dataItem == null){
                            dataItem = new ArrayList<Item>();
                            dataItem.add(new Item("", "Example Item", "Example Item", 1,"2022",0));
                        }

                        itemAllAdapter.setData(dataItem);

                        if(dataItem.get(0).getItemName().equals("")){
                            rvAllItems.setVisibility(View.GONE);
                            tvAllItemsNoItems.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DatabaseError: ", error.toString());
                    }
                });
    }
}