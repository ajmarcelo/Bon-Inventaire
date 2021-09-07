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

    private ImageButton ibSave;
    private ImageButton ibCancel;
    private EditText etName;
    private EditText etDescription;
    private ProgressBar pbEditList;

    private String initialName;
    private String initialDescription;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    /**
     * Initializes the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        this.etName = findViewById(R.id.et_edit_list_name);
        this.etDescription = findViewById(R.id.et_edit_list_description);
        this.pbEditList = findViewById(R.id.pb_edit_list);
        Intent intent = getIntent();

        this.initialName =  intent.getStringExtra(Keys.KEY_LIST.name());
        this.initialDescription = intent.getStringExtra(Keys.KEY_DESCRIPTION.name());

        etName.setText(initialName);
        etDescription.setText(initialDescription);

        initFirebase();
        initConfiguration();
        initSave();
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
     * Initializes the editing of a list.
     */
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
                    List list = new List(name,description,id);
                    checkList(list);
                }
            }
        });
    }

    /**
     * Checks if the input for each field is valid and if an information has changed.
     * @param newName
     * @param newDesc
     * @return
     */
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

    /**
     * Checks if the updated list name has the same name with one of the existing lists of the current user.
     * @param list
     */
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

    /**
     * Updates the information of the list.
     * @param list
     */
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

    /**
     * Updates the list name of the items under the currently updated list.
     * @param list
     */
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

                        intent.putExtra(Keys.KEY_LIST.name(), list.getListName());
                        intent.putExtra(Keys.KEY_DESCRIPTION.name(), list.getListDescription());
                        intent.putExtra(Keys.KEY_ID.name(), list.getListID());

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

    /**
     * Initializes the cancellation of the update or activity.
     */
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