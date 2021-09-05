package com.mobdeve.s11.group19.bon_inventaire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
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

public class ListActivity extends AppCompatActivity {

    private RecyclerView rvAllLists;
    private LinearLayoutManager llmManager;
    private ListAdapter listAdapter;
    protected ArrayList<List> dataList;
    private FloatingActionButton fabAllListAdd;
    private ImageButton ibCancel;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;


    private ActivityResultLauncher allListAddActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();

                        String list =  intent.getStringExtra(Keys.KEY_LIST.name());
                        String desc = intent.getStringExtra(Keys.KEY_DESCRIPTION.name());
                        int id = intent.getIntExtra(Keys.KEY_LIST_ID.name(),0);

                        dataList.add(1 , new List(list,desc,id));
                        listAdapter.notifyItemChanged(0);
                        listAdapter.notifyItemRangeChanged(0, listAdapter.getItemCount());
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_lists);

        initFirebase();
        initConfiguration();
        initRecyclerView();
        initAllListAdd();
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

    public void initRecyclerView() {
//        Toast.makeText(getApplicationContext(), "Retrieving list from the database...", Toast.LENGTH_SHORT).show();

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<List>> t = new GenericTypeIndicator<ArrayList<List>>() {};
                        dataList =  snapshot.getValue(t);

                        rvAllLists = findViewById(R.id.rv_all_lists);

                        llmManager = new LinearLayoutManager(ListActivity.this, LinearLayoutManager.VERTICAL, false);
                        rvAllLists.setLayoutManager(llmManager);

                        listAdapter = new ListAdapter(dataList);
                        rvAllLists.setAdapter(listAdapter);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initAllListAdd() {
        this.fabAllListAdd = findViewById(R.id.fab_all_lists_add);
        this.fabAllListAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, AddListActivity.class);

                allListAddActivityResultLauncher.launch(intent);
            }
        });
    }

    private void initCancel() {
        this.ibCancel = findViewById(R.id.ib_all_lists_back);
        this.ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, HomeActivity.class);

                startActivity(intent);
                finish();
            }
        });
    }

//    private int findIndex(ArrayList<List> allList, List list){
//        int sentinel = 0;
//        for(int i = 0; i < allList.size(); i++) {
//            List tempList = allList.get(i);
//            if(tempList.getListID() == list.getListID()){
//                return i;
//            }
//        }
//        return sentinel;
//    }

//    private void getDataFromDatabase(){
//        mDatabase.getReference(Collections.users.name())
//                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        GenericTypeIndicator<ArrayList<List>> t = new GenericTypeIndicator<ArrayList<List>>() {};
//                        dataList =  snapshot.getValue(t);
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

//    private void loadData() {
//
//        Intent intent = getIntent();
//
//
//        String eList = intent.getStringExtra(Keys.KEY_LIST.name());
//        String eDescription = intent.getStringExtra(Keys.KEY_DESCRIPTION.name());
//        int eId = intent.getIntExtra(Keys.KEY_LIST_ID.name(),0);
//
//
//        String dList = intent.getStringExtra(Keys.KEY_LIST.name());
//        String dDescription = intent.getStringExtra(Keys.KEY_DESCRIPTION.name());
//        int dId = intent.getIntExtra(Keys.KEY_LIST_ID.name(),0);
//
//
//        if(eList != null && dataList != null){
//            getDataFromDatabase();
//            List list = new List(eList, eDescription, eId);
//
//            int index = findIndex(dataList,list);
//
//            dataList.set(index,list);
//            listAdapter.notifyItemChanged(index);
//            listAdapter.notifyItemRangeChanged(0, listAdapter.getItemCount());
//        }
//        else if(dList != null && dataList != null){
//            getDataFromDatabase();
//            List list = new List(eList, dDescription, dId);
//
//            dataList.remove(list);
//            listAdapter.notifyItemChanged(0);
//            listAdapter.notifyItemRangeChanged(0, listAdapter.getItemCount());
//        }
//        else if(dataList != null){
//            listAdapter.notifyDataSetChanged();
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