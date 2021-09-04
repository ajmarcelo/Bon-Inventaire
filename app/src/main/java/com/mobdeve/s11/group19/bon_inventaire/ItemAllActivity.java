package com.mobdeve.s11.group19.bon_inventaire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

    private ActivityResultLauncher allItemsAddActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();

                        String name = intent.getStringExtra(Keys.KEY_NAME.name());
                        String list = intent.getStringExtra(Keys.KEY_LIST.name());
                        String note = intent.getStringExtra(Keys.KEY_NOTE.name());
                        int numStocks = intent.getIntExtra(Keys.KEY_NUM_STOCKS.name(),0);
                        String expireDate = intent.getStringExtra(Keys.KEY_EXPIRE_DATE.name());
                        int id = intent.getIntExtra(Keys.KEY_ITEM_ID.name(),0);

                        if((dataItem.get(0).getItemName()).equals(""))
                            dataItem.remove(0);

                        rvAllItems.setVisibility(View.VISIBLE);
                        tvAllItemsNoItems.setVisibility(View.GONE);

                        dataItem.add(0 , new Item(name, list, note, numStocks, expireDate, id));
                        itemAllAdapter.notifyItemChanged(0);
                        itemAllAdapter.notifyItemRangeChanged(0, itemAllAdapter.getItemCount());
                    }
                }
            }
    );

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

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
    }

    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initRecyclerView () {
        Toast.makeText(getApplicationContext(), "Retrieving items from the database...", Toast.LENGTH_SHORT).show();

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


                        itemAllAdapter = new ItemAllAdapter(dataItem);
                        rvAllItems.setAdapter(itemAllAdapter);

                        if(dataItem.get(0).getItemName().equals("") && dataItem.size() == 1){
                            rvAllItems.setVisibility(View.GONE);
                            tvAllItemsNoItems.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });

    }



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

//    private int findIndex(ArrayList<Item> allItem, Item item){
//        int sentinel = 0;
//        for(int i = 0; i < allItem.size(); i++) {
//            Item tempItem = allItem.get(i);
//            if(tempItem.getItemID() == item.getItemID()){
//                return i;
//            }
//        }
//        return sentinel;
//    }
//
//    private void getDataFromDatabase(){
//        mDatabase.getReference(Collections.users.name())
//                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
//                        dataItem =  snapshot.getValue(t);
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private void loadData() {
//
//        Intent intent = getIntent();
//
//        String eName = intent.getStringExtra(ItemViewActivity.KEY_NAME);
//        String eList = intent.getStringExtra(ItemViewActivity.KEY_LIST);
//        String eNote = intent.getStringExtra(ItemViewActivity.KEY_NOTE);
//        int eNumStocks = intent.getIntExtra(ItemViewActivity.KEY_NUM_STOCKS,0);
//        String eExpireDate = intent.getStringExtra(ItemViewActivity.KEY_EXPIRE_DATE);
//        int eId = intent.getIntExtra(ItemViewActivity.KEY_ID,0);
//
//
//        String dName = intent.getStringExtra(SettingsItemActivity.KEY_NAME);
//        String dList = intent.getStringExtra(SettingsItemActivity.KEY_LIST);
//        String dNote = intent.getStringExtra(SettingsItemActivity.KEY_NOTE);
//        int dNumStocks = intent.getIntExtra(SettingsItemActivity.KEY_NUM_STOCKS,0);
//        String dExpireDate = intent.getStringExtra(SettingsItemActivity.KEY_EXPIRE_DATE);
//        int dId = intent.getIntExtra(SettingsItemActivity.KEY_ID,0);
//
//
//        if(eName != null && dataItem != null){
//            getDataFromDatabase();
//            Item item = new Item(eName, eList, eNote, eNumStocks, eExpireDate, eId);
//
//            int index = findIndex(dataItem,item);
//
//            dataItem.set(index,item);
//            itemAllAdapter.notifyItemChanged(index);
//            itemAllAdapter.notifyItemRangeChanged(0, itemAllAdapter.getItemCount());
//        }
//        else if(dName != null && dataItem != null){
//            getDataFromDatabase();
//            Item item = new Item(dName, dList, dNote, dNumStocks, dExpireDate, dId);
//
//            dataItem.remove(item);
//            itemAllAdapter.notifyItemChanged(0);
//            itemAllAdapter.notifyItemRangeChanged(0, itemAllAdapter.getItemCount());
//        }
//        else if(dataItem != null){
//            itemAllAdapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        this.loadData();
//    }
}