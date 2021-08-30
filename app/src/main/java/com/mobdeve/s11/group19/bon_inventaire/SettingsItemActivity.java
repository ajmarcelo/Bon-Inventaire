package com.mobdeve.s11.group19.bon_inventaire;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class SettingsItemActivity extends AppCompatActivity {

    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_LIST = "KEY_LIST";
    public static final String KEY_NUM_STOCKS = "KEY_NUM_STOCKS";
    public static final String KEY_EXPIRE_DATE = "KEY_EXPIRE_DATE";
    public static final String KEY_NOTE = "KEY_NOTE";
    public static final String KEY_ID = "KEY_ID";

    private TextView tvEdit;
    private TextView tvDelete;
    private ImageButton ibBack;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_item);

        initFirebase();
        initConfiguration();
        initEdit();
        initDelete();
        initBack();
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
        this.tvEdit = findViewById(R.id.tv_settings_item_edit);
        this.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsItemActivity.this, EditItemActivity.class);
                Intent info = getIntent();

                intent.putExtra(KEY_NAME, info.getStringExtra(ItemViewActivity.KEY_NAME));
                intent.putExtra(KEY_LIST, info.getStringExtra(ItemViewActivity.KEY_LIST));
                intent.putExtra(KEY_NOTE, info.getStringExtra(ItemViewActivity.KEY_NOTE));
                intent.putExtra(KEY_NUM_STOCKS, info.getIntExtra(ItemViewActivity.KEY_NUM_STOCKS,0));
                intent.putExtra(KEY_EXPIRE_DATE, info.getStringExtra(ItemViewActivity.KEY_EXPIRE_DATE));
                intent.putExtra(KEY_ID, info.getIntExtra(ItemViewActivity.KEY_ID,0));

                startActivity(intent);
                finish();
            }
        });
    }

    private void initDelete() {
        this.tvDelete = findViewById(R.id.tv_settings_item_delete);
        this.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                String name =  intent.getStringExtra(ItemViewActivity.KEY_NAME);
                String list = intent.getStringExtra(ItemViewActivity.KEY_LIST);
                String note = intent.getStringExtra(ItemViewActivity.KEY_NOTE);
                int numStocks = intent.getIntExtra(ItemViewActivity.KEY_NUM_STOCKS,0);
                String expireDate = intent.getStringExtra(ItemViewActivity.KEY_EXPIRE_DATE);
                int id = intent.getIntExtra(ItemViewActivity.KEY_ID,0);

                Item item = new Item(name,list, note, numStocks,expireDate, id);
                retrieveItem(item);
            }
        });
    }

    public void retrieveItem(Item item) {
        Toast.makeText(getApplicationContext(), "Deleting item to the database...", Toast.LENGTH_SHORT).show();

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
                        ArrayList<Item> allItem = snapshot.getValue(t);

                        int index = findIndex(allItem,item);
                        allItem.remove(index);

                        storeItem(allItem,item);

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int findIndex(ArrayList<Item> allItem, Item item){
        int sentinel = 0;
        for(int i = 0; i < allItem.size(); i++) {
            Item tempItem = allItem.get(i);
            if(tempItem.getItemID() == item.getItemID()){
                return i;
            }
        }
        return sentinel;
    }

    private void storeItem(ArrayList<Item> allItem, Item item) {

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .setValue(allItem)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully Deleted from the database", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SettingsItemActivity.this,ItemAllActivity.class);

                            intent.putExtra(KEY_NAME, item.getItemName());
                            intent.putExtra(KEY_LIST, item.getItemList());
                            intent.putExtra(KEY_NUM_STOCKS, item.getItemNumStocks());
                            intent.putExtra(KEY_EXPIRE_DATE, item.getItemExpireDate().toString());
                            intent.putExtra(KEY_NOTE, item.getItemNote());
                            intent.putExtra(KEY_ID, item.getItemID());

                            setResult(Activity.RESULT_OK, intent);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't delete to the database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initBack() {
        this.ibBack = findViewById(R.id.ib_settings_item_back);
        this.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}