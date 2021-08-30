package com.mobdeve.s11.group19.bon_inventaire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditListActivity extends AppCompatActivity {

    public static final String KEY_LIST = "KEY_LIST";
    public static final String KEY_DESCRIPTION = "KEY_DESCRIPTION";
    public static final String KEY_ID = "KEY_ID";

    private ImageButton ibSave;
    private ImageButton ibCancel;
    private EditText etName;
    private EditText etDescription;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        initFirebase();
        initConfiguration();
        initSave();
        initCancel();
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
    }

    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.etName = findViewById(R.id.et_edit_list_name);
        this.etDescription = findViewById(R.id.et_edit_list_description);

        Intent intent = getIntent();

        String name =  intent.getStringExtra(SettingsListActivity.KEY_LIST);
        String description = intent.getStringExtra(SettingsListActivity.KEY_DESCRIPTION);

        etName.setText(name);
        etDescription.setText(description);
    }

    private void initSave() {
        this.ibSave = findViewById(R.id.ib_edit_list_save);
        this.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                String name = etName.getText().toString();
                String description = etDescription.getText().toString();
                int id = intent.getIntExtra(SettingsListActivity.KEY_ID,0);

                if (!checkField(name)) {
                    //database
                    List list = new List(name,description,id);
                    retrieveList(list);
                }
                else
                    Toast.makeText(getApplicationContext(), "Name must not be empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkField(String name) {
        boolean hasError = false;

        if(name.isEmpty()) {
            this.etName.setError("Required Field");
            this.etName.requestFocus();
            hasError = true;
        }

        return hasError;
    }

    public void retrieveList(List list) {
        Toast.makeText(getApplicationContext(), "Adding item to the database...", Toast.LENGTH_SHORT).show();

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<List>> t = new GenericTypeIndicator<ArrayList<List>>() {};
                        ArrayList<List> allList = snapshot.getValue(t);

                        int index = findIndex(allList,list);

                        String oldList = allList.get(index).getListName();
                        allList.set(index, list);
                        String newList = allList.get(index).getListName();

                        storeItem(allList,oldList,newList);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int findIndex(ArrayList<List> allList, List list){
        int sentinel = 0;
        for(int i = 0; i < allList.size(); i++) {
            List tempList = allList.get(i);
            if(tempList.getListID() == list.getListID()){
                return i;
            }
        }
        return sentinel;
    }

    private void storeItem(ArrayList<List> allList, String oldList, String newName) {

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
                .setValue(allList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            retrieveItem(allList, oldList, newName);

                        } else {
                            Toast.makeText(getApplicationContext(), "Can't Edit to the database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void retrieveItem(ArrayList<List> allList, String oldList, String newList) {
        Toast.makeText(getApplicationContext(), "Editing item to the database...", Toast.LENGTH_SHORT).show();

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
                        ArrayList<Item> allItem = snapshot.getValue(t);

                        allItem = renameList(allItem,oldList,newList);
                        storeItem(allList, allItem);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private ArrayList<Item> renameList(ArrayList<Item> allItem, String oldList, String newList){
        ArrayList<Item> tempAllItem = allItem;

        for(int i = 0; i < allItem.size(); i++) {
            Item tempItem = allItem.get(i);
            if(tempItem.getItemList().equals(oldList)){
                tempItem.setItemList(newList);
                tempAllItem.set(i,tempItem);
            }
        }

        return tempAllItem;
    }

    private void storeItem(ArrayList<List> allList, ArrayList<Item> allItem) {

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .setValue(allItem)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(), "Successfully Added to the database", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditListActivity.this, ItemListActivity.class);

                            intent.putExtra(KEY_LIST, allList.get(1).getListName());
                            intent.putExtra(KEY_DESCRIPTION, allList.get(1).getListDescription());
                            intent.putExtra(KEY_ID, allList.get(1).getListID());

                            setResult(Activity.RESULT_OK, intent);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't Add to the database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initCancel() {
        this.ibCancel = findViewById(R.id.ib_edit_list_cancel);
        this.ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}