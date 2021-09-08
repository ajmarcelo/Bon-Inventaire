package com.mobdeve.s11.group19.bon_inventaire;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

public class SettingsItemInListActivity extends AppCompatActivity {

    private TextView tvEdit;
    private TextView tvDelete;
    private ImageButton ibBack;

    private Dialog dialog;
    private Button btnDeleteItemInListContinue;
    private Button btnDeleteItemInListCancel;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    /**
     * For initializing activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_item);
        this.tvDelete = findViewById(R.id.tv_settings_item_delete);

        initConfirmationDialogBox();
        initFirebase();
        initConfiguration();
        initEdit();
        initDelete();
        initBack();
    }

    /**
     * Initializes the dialog box confirmation for deleting an item.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initConfirmationDialogBox() {
        dialog = new Dialog(SettingsItemInListActivity.this);
        dialog.setContentView(R.layout.confirmation_delete_item);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.confirmation;
        btnDeleteItemInListCancel = dialog.findViewById(R.id.btn_confirm_delete_item_cancel);
        btnDeleteItemInListContinue = dialog.findViewById(R.id.btn_confirm_delete_item_continue);
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
     * Initializes the intent for the next activity (editing the item information).
     */
    private void initEdit() {
        this.tvEdit = findViewById(R.id.tv_settings_item_edit);
        this.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsItemInListActivity.this, EditItemInListActivity.class);
                Intent info = getIntent();

                intent.putExtra(Keys.KEY_NAME.name(), info.getStringExtra(Keys.KEY_NAME.name()));
                intent.putExtra(Keys.KEY_LIST.name(), info.getStringExtra(Keys.KEY_LIST.name()));
                intent.putExtra(Keys.KEY_NOTE.name(), info.getStringExtra(Keys.KEY_NOTE.name()));
                intent.putExtra(Keys.KEY_NUM_STOCKS.name(), info.getIntExtra(Keys.KEY_NUM_STOCKS.name(),0));
                intent.putExtra(Keys.KEY_EXPIRE_DATE.name(), info.getStringExtra(Keys.KEY_EXPIRE_DATE.name()));
                intent.putExtra(Keys.KEY_ITEM_ID.name(), info.getIntExtra(Keys.KEY_ITEM_ID.name(),0));

                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Initializes the deletion of a list.
     */
    private void initDelete() {
        //Triggers the dialog box for the confirmation for deleting the item
        this.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        //Proceeds to deleting the item in the database
        this.btnDeleteItemInListContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();

                String name = intent.getStringExtra(Keys.KEY_NAME.name());
                String list = intent.getStringExtra(Keys.KEY_LIST.name());
                String note = intent.getStringExtra(Keys.KEY_NOTE.name());
                int numStocks = intent.getIntExtra(Keys.KEY_NUM_STOCKS.name(),0);
                String expireDate = intent.getStringExtra(Keys.KEY_EXPIRE_DATE.name());
                int id = intent.getIntExtra(Keys.KEY_ITEM_ID.name(),0);

                Item item = new Item(name,list, note, numStocks,expireDate, id);
                retrieveItem(item);
            }
        });

        //Cancels the deletion of the item
        this.btnDeleteItemInListCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Retrieves the items of the current user.
     * @param item The item to be deleted
     */
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

    /**
     * Looks for the index of the list to be deleted.
     * @param allItem   The arraylist of items to be iterated into
     * @param item      The item to be found in the list
     * @return          Returns the index if the item is in the list and zero (sentinel) if not
     */
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

    /**
     * Stores the updated items of the user after an item is deleted
     * @param allItem   The user's updated list of items
     * @param item      The item to be deleted
     */
    private void storeItem(ArrayList<Item> allItem, Item item) {

        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .setValue(allItem)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully Deleted from the database", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();

                            intent.putExtra(Keys.KEY_NAME.name(), item.getItemName());
                            intent.putExtra(Keys.KEY_LIST.name(), item.getItemList());
                            intent.putExtra(Keys.KEY_NUM_STOCKS.name(), item.getItemNumStocks());
                            intent.putExtra(Keys.KEY_EXPIRE_DATE.name(), item.getItemExpireDate());
                            intent.putExtra(Keys.KEY_NOTE.name(), item.getItemNote());
                            intent.putExtra(Keys.KEY_ITEM_ID.name(), item.getItemID());

                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't delete to the database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Initializes the intent for the next activity (navigating back).
     */
    private void initBack() {
        this.ibBack = findViewById(R.id.ib_settings_item_back);
        this.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Dismisses the dialog box.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }
}