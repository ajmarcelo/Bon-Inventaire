package com.mobdeve.s11.group19.bon_inventaire;

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

public class ItemListActivity extends AppCompatActivity {

    private RecyclerView rvListItems;
    private LinearLayoutManager llmManager;
    private ItemListAdapter itemListAdapter;
    private ArrayList<Item> dataItem;
    private FloatingActionButton fabListItemsAdd;
    private FloatingActionButton fabListItemsSettings;
    private ImageButton ibBack;

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvListItemsNoItems;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    /**
     * A launcher for a previously-prepared call to start the process of executing an ActivityResultContract
     */
    private ActivityResultLauncher allItemsAddActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        getDataFromDatabase();

                        rvListItems.setVisibility(View.VISIBLE);
                        tvListItemsNoItems.setVisibility(View.GONE);
                    }
                }
            }
    );

        /**
         * For initializing activity.
         * @param savedInstanceState
         */
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list_items);
        initFirebase();
        initConfiguration();
        this.tvTitle = findViewById(R.id.tv_list_items_title);
        this.tvDescription = findViewById(R.id.tv_list_items_description);
        this.tvListItemsNoItems = findViewById(R.id.tv_list_items_no_items);
        this.fabListItemsSettings = findViewById(R.id.fab_list_items_settings);
        
        Intent intent = getIntent();

        String list =  intent.getStringExtra(Keys.KEY_LIST.name());
        String description = intent.getStringExtra(Keys.KEY_DESCRIPTION.name());

        tvTitle.setText(list);
        tvDescription.setText(description);

        if(list.equals("Unlisted"))
            fabListItemsSettings.setVisibility(View.GONE);

        initRecyclerView();
        initListItemsAdd();
        initListItemsEdit();
        initBack();
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
     *  Initializes the recycler view of the activity
     */
    private void initRecyclerView () {
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
                        dataItem =  snapshot.getValue(t);

                        Intent intent = getIntent();
                        String list =  intent.getStringExtra(Keys.KEY_LIST.name());

                        if(!(dataItem == null || dataItem.isEmpty()))
                            filterList(dataItem);

                        if(dataItem == null || dataItem.isEmpty()){
                            dataItem = new ArrayList<Item>();
                            dataItem.add(new Item("", list, "Example Item", 1,"2022",0));
                        }

                        rvListItems = findViewById(R.id.rv_list_items);

                        llmManager = new LinearLayoutManager(ItemListActivity.this, LinearLayoutManager.VERTICAL, false);
                        rvListItems.setLayoutManager(llmManager);

                        itemListAdapter = new ItemListAdapter(dataItem,ItemListActivity.this);
                        rvListItems.setAdapter(itemListAdapter);

                        if(dataItem.get(0).getItemName().equals("")){
                            rvListItems.setVisibility(View.GONE);
                            tvListItemsNoItems.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DatabaseError: ", error.toString());
                    }
                });
    }

    /**
     * Filters the data of items based on current list
     * @param dataItem The list of items of the current user
     */
    private void filterList (ArrayList<Item> dataItem) {
        Intent intent = getIntent();

        String list =  intent.getStringExtra(Keys.KEY_LIST.name());
        ArrayList<Item> tempDataItem = new ArrayList<Item>();

        for(int i = 0; i < dataItem.size(); i++) {
            Item tempItem = dataItem.get(i);
            if(tempItem.getItemList().equals(list)) {
                tempDataItem.add(tempItem);
            }
        }
        this.dataItem.clear();
        this.dataItem.addAll(tempDataItem);
    }

    /**
     * Initializes the intent for the next activity (adding an item)
     */
    private void initListItemsAdd() {
        this.fabListItemsAdd = findViewById(R.id.fab_list_items_add);
        this.fabListItemsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemListActivity.this, AddItemActivity.class);

                Intent info = getIntent();

                intent.putExtra(Keys.KEY_LIST.name(), info.getStringExtra(Keys.KEY_LIST.name()));

                allItemsAddActivityResultLauncher.launch(intent);
            }
        });
    }

    /**
     * Initializes the intent for the next activity (settings for the list)
     */
    private void initListItemsEdit() {
        this.fabListItemsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemListActivity.this, SettingsListActivity.class);

                Intent info = getIntent();

                intent.putExtra(Keys.KEY_LIST.name(), info.getStringExtra(Keys.KEY_LIST.name()));
                intent.putExtra(Keys.KEY_DESCRIPTION.name(), info.getStringExtra(Keys.KEY_DESCRIPTION.name()));
                intent.putExtra(Keys.KEY_LIST_ID.name(), info.getIntExtra(Keys.KEY_LIST_ID.name(),0));

                startActivity(intent);
            }
        });
    }

    /**
     * Initializes the intent for the next activity (navigating back)
     */
    private void initBack() {
        this.ibBack = findViewById(R.id.ib_list_items_back);
        this.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemListActivity.this, ListActivity.class);

                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Called when an activity launched exits, giving the requestCode started it with and the resultCode it returned.
     * @param requestCode was set when calling startActivityForResult() as part of the parameter
     * @param resultCode not used in this overridden method.
     * @param data not used in this overridden method.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
            getDataFromDatabase();
    }

    /**
     * Retrieves the data from the database and sets the retrieve data to the current data
     */
    private void getDataFromDatabase(){
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
                        dataItem =  snapshot.getValue(t);

                        Intent intent = getIntent();
                        String list =  intent.getStringExtra(Keys.KEY_LIST.name());

                        if(!(dataItem == null || dataItem.isEmpty()))
                            filterList(dataItem);

                        if(dataItem == null || dataItem.isEmpty()){
                            dataItem = new ArrayList<Item>();
                            dataItem.add(new Item("", list, "Example Item", 1,"2022",0));
                        }

                        itemListAdapter.setData(dataItem);

                        if(dataItem.get(0).getItemName().equals("")){
                            rvListItems.setVisibility(View.GONE);
                            tvListItemsNoItems.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DatabaseError: ", error.toString());
                    }
                });
    }
}