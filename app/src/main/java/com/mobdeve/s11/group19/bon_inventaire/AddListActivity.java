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

public class AddListActivity extends AppCompatActivity {

    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_DESCRIPTION = "KEY_DESCRIPTION";
    public static final String KEY_ID = "KEY_ID";

    private ImageButton ibSave;
    private ImageButton ibCancel;
    private EditText etName;
    private EditText etDescription;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        initFirebase();
        initConfiguration();
        initSave();
        initCancel();
    }

    private void initFirebase() {
        this.mAuth = FirebaseAuth.getInstance();
        this.mdatabase = FirebaseDatabase.getInstance();
    }

    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.etName = findViewById(R.id.et_add_list_name);
        this.etDescription = findViewById(R.id.et_add_list_description);
    }

    private void initSave() {
        this.ibSave = findViewById(R.id.ib_add_list_save);
        this.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
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
        Toast.makeText(getApplicationContext(), "Adding list to the database...", Toast.LENGTH_SHORT).show();

        mdatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<List>> t = new GenericTypeIndicator<ArrayList<List>>() {};
                        ArrayList<List> allList = snapshot.getValue(t);

                        if(!isSameList(allList,list)){
                            list.setListID(allList.size());
                            allList.add(1,list);
                            storeList(allList);
                        } else {
                            Toast.makeText(getApplicationContext(), "List already created", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isSameList(ArrayList<List> allList, List list){
        for(int i = 0; i < allList.size(); i++) {
            List tempList = allList.get(i);
            if(tempList.getListName().equals(list.getListName())){
                return true;
            }
        }
        return false;
    }

    private void storeList(ArrayList<List> allList) {

        mdatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.lists.name())
                .setValue(allList)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully Added to the database", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();

                            intent.putExtra(KEY_NAME, allList.get(1).getListName());
                            intent.putExtra(KEY_DESCRIPTION, allList.get(1).getListDescription());
                            intent.putExtra(KEY_ID, allList.get(1).getListID());

                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't Add to the database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

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