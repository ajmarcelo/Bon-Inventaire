package com.mobdeve.s11.group19.bon_inventaire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditListActivity extends AppCompatActivity {

    public static final String KEY_LIST = "KEY_LIST";
    public static final String KEY_DESCRIPTION = "KEY_DESCRIPTION";
    public static final String KEY_ID = "KEY_ID";

    private ImageButton ibSave;
    private ImageButton ibCancel;
    private EditText etName;
    private EditText etDescription;
    private ProgressBar pbEditList;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    private String initialName;
    private String initialDescription;

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
        this.pbEditList = findViewById(R.id.pb_edit_list);
        Intent intent = getIntent();

        this.initialName =  intent.getStringExtra(Keys.KEY_LIST.name());
        this.initialDescription = intent.getStringExtra(Keys.KEY_DESCRIPTION.name());

        etName.setText(initialName);
        etDescription.setText(initialDescription);
    }

    private void initSave() {
        this.ibSave = findViewById(R.id.ib_edit_list_save);
        this.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                String name = etName.getText().toString().trim();
                if(!name.isEmpty())
                    name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
                String description = etDescription.getText().toString().trim();
                int id = intent.getIntExtra(Keys.KEY_LIST_ID.name(),0);

                if (!checkField(name, description)) {
                    //database
                    List list = new List(name,description,id);
//                    retrieveList(list);
                    checkList(list);
                }
//                else
//                    Toast.makeText(getApplicationContext(), "Name must not be empty.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkField(String newName, String newDesc) {
        boolean hasError = false;

        if(newName.equals(initialName) && newDesc.equals(initialDescription)) {
            Toast.makeText(EditListActivity.this, "No Changes Has Been Made", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if(newName.isEmpty()) {
            this.etName.setError("Required Field");
            this.etName.requestFocus();
            hasError = true;
        }

        return hasError;
    }

    public void checkList(List list){

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid())
                .child(Collections.lists.name())
                .orderByChild("listName")
                .equalTo(list.getListName())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean notSameList = true;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Log.d("List Parent: ", child.getKey());
                            notSameList = false;
                        }
                        if(notSameList || !(list.getListDescription()).equals(initialDescription)){
                            pbEditList.setVisibility(View.VISIBLE);
                            updateList(list);
                        }
                        else if(!notSameList) {
                            etName.setError("List with same name already exist");
                            etName.requestFocus();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DatabaseError: ", error.toString());
                    }
                });
    }

    public void updateList(List list){

        HashMap editedList = new HashMap();
        editedList.put("listDescription", list.getListDescription());
        editedList.put("listName", list.getListName());

        mDatabase.getReference(Collections.users.name())
            .child(mAuth.getCurrentUser().getUid())
            .child(Collections.lists.name())
            .orderByChild("listID")
            .equalTo(list.getListID())
            .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Log.d("List Parent: ", child.getKey());
                    mDatabase.getReference(Collections.users.name())
                            .child(mAuth.getCurrentUser().getUid())
                            .child(Collections.lists.name())
                            .child(child.getKey())
                            .updateChildren(editedList);
                }
                updateItems(list);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DatabaseError: ", error.toString());
            }
        });
    }

    public void updateItems(List list){

        Intent intent = getIntent();

        String oldList =  intent.getStringExtra(Keys.KEY_LIST.name());

        HashMap editedList = new HashMap();
        editedList.put("itemList", list.getListName());

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid())
                .child(Collections.items.name())
                .orderByChild("itemList")
                .equalTo(oldList)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Log.d("Item Parent: ", child.getKey());
                            mDatabase.getReference(Collections.users.name())
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(Collections.items.name())
                                    .child(child.getKey())
                                    .updateChildren(editedList);
                        }
                        Intent intent = new Intent(EditListActivity.this, ItemListActivity.class);

                        intent.putExtra(KEY_LIST, list.getListName());
                        intent.putExtra(KEY_DESCRIPTION, list.getListDescription());
                        intent.putExtra(KEY_ID, list.getListID());

                        setResult(Activity.RESULT_OK, intent);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DatabaseError: ", error.toString());
                    }
                });
    }


//    public void retrieveList(List list) {
//        Toast.makeText(getApplicationContext(), "Adding item to the database...", Toast.LENGTH_SHORT).show();
//        pbEditList.setVisibility(View.VISIBLE);
//        mDatabase.getReference(Collections.users.name())
//                .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        GenericTypeIndicator<ArrayList<List>> t = new GenericTypeIndicator<ArrayList<List>>() {};
//                        ArrayList<List> allList = snapshot.getValue(t);
//
//                        int index = findIndex(allList,list);
//
//                        String oldList = allList.get(index).getListName();
//                        allList.set(index, list);
//                        String newList = allList.get(index).getListName();
//
//                        storeItem(allList,oldList,newList,index);
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
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
//
//    private void storeItem(ArrayList<List> allList, String oldList, String newName, int index) {
//
//        mDatabase.getReference(Collections.users.name())
//                .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
//                .setValue(allList)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//                            retrieveItem(allList, oldList, newName, index);
//
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Can't Edit to the database", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//    public void retrieveItem(ArrayList<List> allList, String oldList, String newList, int index) {
//        Toast.makeText(getApplicationContext(), "Editing item to the database...", Toast.LENGTH_SHORT).show();
//
//        mDatabase.getReference(Collections.users.name())
//                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
//                        ArrayList<Item> allItem = snapshot.getValue(t);
//
//                        if(!(allItem == null || allItem.isEmpty()))
//                            allItem = renameList(allItem,oldList,newList);
//
//                        storeItem(allList, allItem, index);
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private ArrayList<Item> renameList(ArrayList<Item> allItem, String oldList, String newList){
//        ArrayList<Item> tempAllItem = allItem;
//
//        for(int i = 0; i < allItem.size(); i++) {
//            Item tempItem = allItem.get(i);
//            if(tempItem.getItemList().equals(oldList)){
//                tempItem.setItemList(newList);
//                tempAllItem.set(i,tempItem);
//            }
//        }
//
//        return tempAllItem;
//    }
//
//    private void storeItem(ArrayList<List> allList, ArrayList<Item> allItem, int index) {
//
//        mDatabase.getReference(Collections.users.name())
//                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
//                .setValue(allItem)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//
//                            Toast.makeText(getApplicationContext(), "Successfully Added to the database", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(EditListActivity.this, ItemListActivity.class);
//
//                            intent.putExtra(KEY_LIST, allList.get(index).getListName());
//                            intent.putExtra(KEY_DESCRIPTION, allList.get(index).getListDescription());
//                            intent.putExtra(KEY_ID, allList.get(index).getListID());
//
//                            setResult(Activity.RESULT_OK, intent);
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Can't Add to the database", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

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