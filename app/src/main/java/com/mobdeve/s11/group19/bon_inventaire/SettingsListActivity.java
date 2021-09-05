package com.mobdeve.s11.group19.bon_inventaire;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsListActivity extends AppCompatActivity {

    private TextView tvEdit;
    private TextView tvDelete;
    private ImageButton ibBack;

    private Dialog dialog;
    private Button btnDeleteListContinue;
    private Button btnDeleteListCancel;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_list);
        this.tvDelete = findViewById(R.id.tv_settings_list_delete);

        initConfirmationDialogBox();
        initFirebase();
        initConfiguration();
        initEdit();
        initDelete();
        initBack();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initConfirmationDialogBox() {
        dialog = new Dialog(SettingsListActivity.this);
        dialog.setContentView(R.layout.confirmation_delete_list);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.confirmation;
        btnDeleteListCancel = dialog.findViewById(R.id.btn_confirm_delete_list_cancel);
        btnDeleteListContinue = dialog.findViewById(R.id.btn_confirm_delete_list_continue);
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance();
    }

    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initEdit() {
        this.tvEdit = findViewById(R.id.tv_settings_list_edit);
        this.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsListActivity.this, EditListActivity.class);

                Intent info = getIntent();
                Toast.makeText(getApplicationContext(), info.getStringExtra(Keys.KEY_LIST.name()), Toast.LENGTH_SHORT).show();


                intent.putExtra(Keys.KEY_LIST.name(), info.getStringExtra(Keys.KEY_LIST.name()));
                intent.putExtra(Keys.KEY_DESCRIPTION.name(), info.getStringExtra(Keys.KEY_DESCRIPTION.name()));
                intent.putExtra(Keys.KEY_LIST_ID.name(), info.getIntExtra(Keys.KEY_LIST_ID.name(),0));

                startActivity(intent);
                finish();
            }
        });
    }

    private void initDelete() {
        this.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        this.btnDeleteListContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                String name = intent.getStringExtra(Keys.KEY_LIST.name());
                String description = intent.getStringExtra(Keys.KEY_DESCRIPTION.name());
                int id = intent.getIntExtra(Keys.KEY_LIST_ID.name(),0);

                if (name.length() > 0) {
                    //database
                    List list = new List(name,description,id);
//                    retrieveList(list);
                    deleteList(list);
                }
                else
                    Toast.makeText(getApplicationContext(), "List cannot be deleted...", Toast.LENGTH_SHORT).show();
            }
        });

        this.btnDeleteListCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void deleteList(List list){

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
                                    .removeValue();
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
        editedList.put("itemList", "Unlisted");

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
                        Intent intent = new Intent(SettingsListActivity.this, ListActivity.class);

                        intent.putExtra(Keys.KEY_LIST.name(), list.getListName());
                        intent.putExtra(Keys.KEY_DESCRIPTION.name(), list.getListDescription());
                        intent.putExtra(Keys.KEY_LIST_ID.name(), list.getListID());

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
//
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
//                        String listName = allList.get(index).getListName();
//                        allList.remove(index);
//
//                        storeItem(allList,listName, list);
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
//    private void storeItem(ArrayList<List> allList, String listName, List list) {
//
//        mDatabase.getReference(Collections.users.name())
//                .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
//                .setValue(allList)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//                            retrieveItem(allList, listName, list);
//
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Can't Edit to the database", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }
//
//    public void retrieveItem(ArrayList<List> allList, String listName, List list) {
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
//                            allItem = moveList(allItem,listName);
//
//                        storeItem(allList, allItem, list);
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
//    private ArrayList<Item> moveList(ArrayList<Item> allItem, String listName){
//        ArrayList<Item> tempAllItem = allItem;
//
//        for(int i = 0; i < allItem.size(); i++) {
//            Item tempItem = allItem.get(i);
//            if(tempItem.getItemList().equals(listName)){
//                tempItem.setItemList("Unlisted");
//                tempAllItem.set(i,tempItem);
//            }
//        }
//
//        return tempAllItem;
//    }
//
//    private void storeItem(ArrayList<List> allList, ArrayList<Item> allItem, List list) {
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
//                            Intent intent = new Intent(SettingsListActivity.this, ListActivity.class);
//
//                            intent.putExtra(Keys.KEY_LIST.name(), list.getListName());
//                            intent.putExtra(Keys.KEY_DESCRIPTION.name(), list.getListDescription());
//                            intent.putExtra(Keys.KEY_LIST_ID.name(), list.getListID());
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

    private void initBack() {
        this.ibBack = findViewById(R.id.ib_settings_list_back);
        this.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsListActivity.this, ListActivity.class);

                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }
}