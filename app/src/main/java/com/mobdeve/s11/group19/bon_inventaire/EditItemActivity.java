package com.mobdeve.s11.group19.bon_inventaire;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
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
import java.util.Calendar;
import java.util.HashMap;

public class EditItemActivity extends AppCompatActivity {

    private ImageButton ibSave;
    private ImageButton ibCancel;
    private EditText etName;
    private AutoCompleteTextView etList;
    private EditText etNumStocks;
    private EditText etExpireDate;
    private EditText etNote;
    private ArrayList<List> userLists;
    private String[] dropdown;
    private ProgressBar pbEditItem;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

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
        this.userLists = new ArrayList<List>();
        this.etName = findViewById(R.id.et_edit_item_name);
        this.etNumStocks = findViewById(R.id.et_edit_item_num_stocks);
        this.etExpireDate = findViewById(R.id.et_edit_item_expire_date);
        this.etNote = findViewById(R.id.et_edit_item_note);
        this.pbEditItem = findViewById(R.id.pb_edit_item);
        this.etList = (AutoCompleteTextView) findViewById(R.id.et_edit_item_list);
        Intent intent = getIntent();

        String name = intent.getStringExtra(Keys.KEY_NAME.name());
        String list = intent.getStringExtra(Keys.KEY_LIST.name());
        String note = intent.getStringExtra(Keys.KEY_NOTE.name());
        int numStocks = intent.getIntExtra(Keys.KEY_NUM_STOCKS.name(),0);
        String expireDate = intent.getStringExtra(Keys.KEY_EXPIRE_DATE.name());
        int id = intent.getIntExtra(Keys.KEY_ITEM_ID.name(),0);

        this.etName.setText(name);
        this.etList.setText(list);
        this.etNote.setText(note);
        this.etNumStocks.setText(Integer.toString(numStocks));
        this.etExpireDate.setText(expireDate);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etExpireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditItemActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePick, int year, int month, int day) {
                        month = month + 1;
                        String date = month+"/"+day+"/"+year;
                        etExpireDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        //List Dropdown
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getUid()).child(Collections.lists.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<List>> t = new GenericTypeIndicator<ArrayList<List>>() {};
                        userLists =  snapshot.getValue(t);

                        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(EditItemActivity.this, R.layout.dropdown_item, dropdownList(userLists));
                        etList.setThreshold(1);
                        etList.setAdapter(adapterList);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


    }

    private String[] dropdownList (ArrayList<List> userLists) {
        int n = userLists.size();
        dropdown = new String[userLists.size()];
        for(int i = 0; i < n; i++) {
            dropdown[i] = userLists.get(i).getListName();
        }
        return dropdown;
    }

    private void initSave() {
        this.ibSave = findViewById(R.id.ib_edit_item_save);
        this.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                String name = etName.getText().toString();
                String list = etList.getText().toString();
                String numStocks = etNumStocks.getText().toString();
                String expireDate = etExpireDate.getText().toString();
                String note = etNote.getText().toString();
                int id = intent.getIntExtra(Keys.KEY_ITEM_ID.name(),0);

                if (!checkField(name,Integer.parseInt(numStocks))) {
                    Item item = new Item(name,list, note, Integer.parseInt(numStocks),expireDate, id);
//                    retrieveItem(item);
                    updateItems(item);
                }
                else
                    Toast.makeText(getApplicationContext(), "Updating Item Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkField(String name, int numStocks) {
        boolean hasError = false;

        if(name.isEmpty()) {
            this.etName.setError("Required Field");
            this.etName.requestFocus();
            hasError = true;
        }

        if(Integer.toString(numStocks).isEmpty()) {
            this.etNumStocks.setError("Required Field");
            this.etNumStocks.requestFocus();
            hasError = true;
        }
        else if(numStocks < 0) {
            this.etNumStocks.setError("Minimum is 0.");
            this.etNumStocks.requestFocus();
            hasError = true;
        }
        else if(numStocks > 1000000) {
            this.etNumStocks.setError("Limit exceeded!\nMaximum is 1,000,000.");
            this.etNumStocks.requestFocus();
            hasError = true;
        }
        return hasError;
    }

    public void updateItems(Item item){

        HashMap editedItem = new HashMap();
        editedItem.put("itemExpireDate", item.getItemExpireDate());
        editedItem.put("itemList", item.getItemList());
        editedItem.put("itemName", item.getItemName());
        editedItem.put("itemNote", item.getItemNote());
        editedItem.put("itemNumStocks", item.getItemNumStocks());

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid())
                .child(Collections.items.name())
                .orderByChild("itemID")
                .equalTo(item.getItemID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Log.d("Item Parent: ", child.getKey());
                            mDatabase.getReference(Collections.users.name())
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child(Collections.items.name())
                                    .child(child.getKey())
                                    .updateChildren(editedItem);
                        }
                        Intent intent = new Intent();

                        intent.putExtra(Keys.KEY_NAME.name(), item.getItemName());
                        intent.putExtra(Keys.KEY_LIST.name(), item.getItemList());
                        intent.putExtra(Keys.KEY_NUM_STOCKS.name(), item.getItemNumStocks());
                        intent.putExtra(Keys.KEY_EXPIRE_DATE.name(), item.getItemExpireDate());
                        intent.putExtra(Keys.KEY_NOTE.name(), item.getItemNote());
                        intent.putExtra(Keys.KEY_ITEM_ID.name(), item.getItemID());

                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DatabaseError: ", error.toString());
                    }
                });
    }

//    public void retrieveItem(Item item) {
//        Toast.makeText(getApplicationContext(), "Adding item to the database...", Toast.LENGTH_SHORT).show();
//        this.pbEditItem.setVisibility(View.VISIBLE);
//
//        mDatabase.getReference(Collections.users.name())
//                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
//                        ArrayList<Item> allItem = snapshot.getValue(t);
//
//                        int index = findIndex(allItem,item);
//                        allItem.set(index, item);
//                        storeItem(allItem, index);
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
//
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
//    private void storeItem(ArrayList<Item> allItem, int index) {
//
//        mDatabase.getReference(Collections.users.name())
//                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
//                .setValue(allItem)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()) {
//                            Toast.makeText(getApplicationContext(), "Successfully Added to the database", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent();
//
//                            intent.putExtra(KEY_NAME, allItem.get(index).getItemName());
//                            intent.putExtra(KEY_LIST, allItem.get(index).getItemList());
//                            intent.putExtra(KEY_NUM_STOCKS, allItem.get(index).getItemNumStocks());
//                            intent.putExtra(KEY_EXPIRE_DATE, allItem.get(index).getItemExpireDate().toString());
//                            intent.putExtra(KEY_NOTE, allItem.get(index).getItemNote());
//                            intent.putExtra(KEY_ID, allItem.get(index).getItemID());
//
//                            setResult(Activity.RESULT_OK, intent);
//                            finish();
//                        } else {
//                            Toast.makeText(getApplicationContext(), "Can't Add to the database", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void initCancel() {
        this.ibCancel = findViewById(R.id.ib_edit_item_cancel);
        this.ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}