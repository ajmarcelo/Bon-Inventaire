package com.mobdeve.s11.group19.bon_inventaire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
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
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_LIST = "KEY_LIST";
    public static final String KEY_NUM_STOCKS = "KEY_NUM_STOCKS";
    public static final String KEY_EXPIRE_DATE = "KEY_EXPIRE_DATE";
    public static final String KEY_NOTE = "KEY_NOTE";
    public static final String KEY_ID = "KEY_ID";

    private ImageButton ibSave;
    private ImageButton ibCancel;
    private EditText etName;
    private EditText etList;
    private EditText etNumStocks;
    private EditText etExpireDate;
    private EditText etNote;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

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

        this.etName = findViewById(R.id.et_add_item_name);
        this.etList = findViewById(R.id.et_add_item_list);
        this.etNumStocks = findViewById(R.id.et_add_item_num_stocks);
        this.etExpireDate = findViewById(R.id.et_add_item_expire_date);
        this.etNote = findViewById(R.id.et_add_item_note);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        etExpireDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddItemActivity.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
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

        Intent intent = getIntent();
        String list =  intent.getStringExtra(ItemListActivity.KEY_LIST);

        this.etList.setText(list);
    }

    private void initSave() {
        this.ibSave = findViewById(R.id.ib_add_item_save);
        this.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String list = etList.getText().toString();
                String numStocks = etNumStocks.getText().toString();
                //int numStocks = Integer.parseInt(etNumStocks.getText().toString());
                String expireDate = etExpireDate.getText().toString();
                String note = etNote.getText().toString();
                int id = 0;

                if (!checkField(name,numStocks)) {
                    //database
                    Item item = new Item(name,list, note, Integer.parseInt(numStocks),expireDate, id);
                    retrieveItem(item);
                }
                else
                    Toast.makeText(getApplicationContext(), "Adding Item Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkField(String name, String numStocks) {
        boolean hasError = false;

        if(name.isEmpty()) {
            this.etName.setError("Required Field");
            this.etName.requestFocus();
            hasError = true;
        }

        if(numStocks.isEmpty()) {
            this.etNumStocks.setError("Required Field");
            this.etNumStocks.requestFocus();
            hasError = true;
        }
        else if(Integer.parseInt(numStocks) < 0) {
            this.etNumStocks.setError("Minimum is 0.");
            this.etNumStocks.requestFocus();
            hasError = true;
        }
        else if(Integer.parseInt(numStocks) > 1000000) {
            this.etNumStocks.setError("Limit exceeded!\nMaximum is 1,000,000.");
            this.etNumStocks.requestFocus();
            hasError = true;
        }
        return hasError;
    }

    public void retrieveItem(Item item) {
        Toast.makeText(getApplicationContext(), "Adding item to the database...", Toast.LENGTH_SHORT).show();

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
                        ArrayList<Item> allItem = snapshot.getValue(t);

                        if(allItem == null){
                            allItem = new ArrayList<Item>();
                            allItem.add(0,item);
                            storeItem(allItem);
                        }
                        else if(!isSameItem(allItem,item)){
                            item.setItemID(allItem.size());
                            allItem.add(0,item);
                            storeItem(allItem);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Item already created", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isSameItem(ArrayList<Item> allItem, Item item){
        for(int i = 0; i < allItem.size(); i++) {
            Item tempItem = allItem.get(i);
            if(tempItem.getItemName().equals(item.getItemName())){
                if(tempItem.getItemList().equals(item.getItemList())){
                    Toast.makeText(getApplicationContext(), "Item is on another List", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        }
        return false;
    }

    private void storeItem(ArrayList<Item> allItem) {

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .setValue(allItem)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully Added to the database", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();

                            intent.putExtra(KEY_NAME, allItem.get(0).getItemName());
                            intent.putExtra(KEY_LIST, allItem.get(0).getItemList());
                            intent.putExtra(KEY_NUM_STOCKS, allItem.get(0).getItemNumStocks());
                            intent.putExtra(KEY_EXPIRE_DATE, allItem.get(0).getItemExpireDate().toString());
                            intent.putExtra(KEY_NOTE, allItem.get(0).getItemNote());
                            intent.putExtra(KEY_ID, allItem.get(0).getItemID());

                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't Add to the database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initCancel() {
        this.ibCancel = findViewById(R.id.ib_add_item_cancel);
        this.ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}