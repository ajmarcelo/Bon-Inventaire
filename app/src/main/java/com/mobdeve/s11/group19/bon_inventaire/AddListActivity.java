package com.mobdeve.s11.group19.bon_inventaire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddListActivity extends AppCompatActivity {

    private ImageButton ibSave;
    private ImageButton ibCancel;
    private EditText etName;
    private EditText etDescription;
    private ProgressBar pbAddList;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    /**
     * Initializes the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        initFirebase();
        initConfiguration();

        this.etName = findViewById(R.id.et_add_list_name);
        this.etDescription = findViewById(R.id.et_add_list_description);
        this.pbAddList = findViewById(R.id.pb_add_list);

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
     * Initializes the adding of a list.
     */
    private void initSave() {
        this.ibSave = findViewById(R.id.ib_add_list_save);
        this.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                if(!name.isEmpty())
                    name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
                String description = etDescription.getText().toString();
                int id = 0;

                if (!checkField(name)) {
                    List list = new List(name,description,id);
                    ArrayList<List> allList = new ArrayList<List>();
                    allList.add(list);
                    retrieveList(list);
                }
                else
                    Toast.makeText(getApplicationContext(), "Adding List Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks if the input for the list name field is valid.
     * @param name  The list name inputted by the user
     * @return      Returns true if there is an error in the input field. Otherwise, it returns false
     */
    private boolean checkField(String name) {
        boolean hasError = false;

        if(name.isEmpty()) {
            this.etName.setError("Required Field");
            this.etName.requestFocus();
            hasError = true;
        }

        return hasError;
    }

    /**
     * Retrieves the lists of the current user.
     * @param list The list to be added
     */
    public void retrieveList(List list) {
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<List>> t = new GenericTypeIndicator<ArrayList<List>>() {};
                        ArrayList<List> allList = snapshot.getValue(t);

                        if(!isSameList(allList,list)){
                            pbAddList.setVisibility(View.VISIBLE);
                            list.setListID(allList.size());
                            allList.add(1,list);
                            storeList(allList);
                        } else {
                            etName.setError("List with same name already exist");
                            etName.requestFocus();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve lists", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Checks if the inputted list by the user already exists.
     * @param allList   The arraylist of current lists
     * @param list      The list the user inputted in the field
     * @return
     */
    private boolean isSameList(ArrayList<List> allList, List list){
        for(int i = 0; i < allList.size(); i++) {
            List tempList = allList.get(i);
            if(tempList.getListName().equals(list.getListName())){
                return true;
            }
        }
        return false;
    }

    /**
     * Stores the lists to the database.
     * @param allList   The arraylist of current lists
     */
    private void storeList(ArrayList<List> allList) {
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
                .setValue(allList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "List Successfully Added", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();

                            intent.putExtra(Keys.KEY_LIST.name(), allList.get(1).getListName());
                            intent.putExtra(Keys.KEY_DESCRIPTION.name(), allList.get(1).getListDescription());
                            intent.putExtra(Keys.KEY_LIST_ID.name(), allList.get(1).getListID());

                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "List NOT Added", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Initializes the cancellation of the activity (adding of list).
     */
    private void initCancel() {
        this.ibCancel = findViewById(R.id.ib_add_list_cancel);
        this.ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}