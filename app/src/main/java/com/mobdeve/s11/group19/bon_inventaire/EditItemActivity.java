package com.mobdeve.s11.group19.bon_inventaire;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class EditItemActivity extends AppCompatActivity {

    public static final String CHANNEL_NAME = "Bon_Inventaire";
    public static final String CHANNEL_ID = "BI_Notify";
    public static final long MILISECOND_IN_24HRS = 86400000;

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

    private String initialName;
    private String initialList;
    private String initialNote;
    private int initialNumStocks;

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

        this.initialName = intent.getStringExtra(Keys.KEY_NAME.name());
        this.initialList = intent.getStringExtra(Keys.KEY_LIST.name());
        this.initialNote = intent.getStringExtra(Keys.KEY_NOTE.name());
        this.initialNumStocks = intent.getIntExtra(Keys.KEY_NUM_STOCKS.name(),0);
        String expireDate = intent.getStringExtra(Keys.KEY_EXPIRE_DATE.name());
        int id = intent.getIntExtra(Keys.KEY_ITEM_ID.name(),0);

        this.etName.setText(initialName);
        this.etList.setText(initialList);
        this.etNote.setText(initialNote);
        this.etNumStocks.setText(Integer.toString(initialNumStocks));
        this.etExpireDate.setText(expireDate);
        etExpireDate.setFocusable(false);

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

        if(expireDate.isEmpty())
            etExpireDate.setHint("No Date");
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
                if(!name.isEmpty())
                    name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
                String list = etList.getText().toString();
                String numStocks = etNumStocks.getText().toString();
                String expireDate = etExpireDate.getText().toString();
                String note = etNote.getText().toString();
                int id = intent.getIntExtra(Keys.KEY_ITEM_ID.name(),0);

                if (!checkField(name,list, Integer.parseInt(numStocks),note)) {
                    if(list.isEmpty())
                        list = "Unlisted";
                    Item item = new Item(name,list, note, Integer.parseInt(numStocks),expireDate, id);
//                    retrieveItem(item);
                    updateItems(item);
                }
                else
                    Toast.makeText(getApplicationContext(), "Updating Item Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkField(String name, String list, int numStocks, String note) {
        boolean hasError = false;

        if(name.equals(initialName) && (numStocks == initialNumStocks) && list.equals(initialList) && note.equals(initialNote)) {
            Toast.makeText(getApplicationContext(), "No Changes Has Been Made", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

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

                        getUserName(item.getItemName(), item.getItemID(), item.getItemNumStocks());

                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DatabaseError: ", error.toString());
                    }
                });
    }

    private void initCancel() {
        this.ibCancel = findViewById(R.id.ib_edit_item_cancel);
        this.ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUserName(String itemName, int itemId, int numStocks) {
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.name.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue().toString();
                        checkDecrease("Bonjour, " + name + "! " + itemName + " is out of stock.", numStocks, itemId);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNotifChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.enableLights(true);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private void checkDecrease (String body, int newNumStocks, int itemId) {
        String oldNumStocks = Integer.toString(this.initialNumStocks);

        if (newNumStocks < Integer.parseInt(oldNumStocks)) {
            if (newNumStocks == 0) {
//                Intent intent = new Intent(this, HomeActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, 0);
//                initNotifStock("Out of Stock!", body, pendingIntent);
                initNotifStockRepeat(body, itemId);
            }
            else if (newNumStocks == 1) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 1000, intent, 0);
                initNotifStock("Low on stock!", etName.getText().toString() + " is low on stock.", pendingIntent);
            }
        }
        else {
            cancelNotifStockRepeat(body, itemId);
        }
    }

    private void cancelNotifStockRepeat (String body, int itemId) {
        createNotifChannel();

        String reqCodeRepeat = Integer.toString(itemId) + "999";

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intentRedirect = new Intent(this, HomeActivity.class);
        intentRedirect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingRedirect = PendingIntent.getActivity(this, Integer.parseInt(reqCodeRepeat), intentRedirect, 0);

        Intent intent = new Intent(EditItemActivity.this, NotificationAlarm.class);
        intent.putExtra(Keys.KEY_TITLE.name(), "Out of stock!");
        intent.putExtra(Keys.KEY_MSG.name(), body);
        intent.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
        intent.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect);

        PendingIntent pendInt0d = PendingIntent.getBroadcast(EditItemActivity.this,
                Integer.parseInt(reqCodeRepeat), intent, 0);

        alarmManager.cancel(pendInt0d);
    }

    private void initNotifStockRepeat (String body, int itemId) {
        createNotifChannel();

        String reqCodeRepeat = Integer.toString(itemId) + "999";

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intentRedirect = new Intent(this, HomeActivity.class);
        intentRedirect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingRedirect = PendingIntent.getActivity(this, Integer.parseInt(reqCodeRepeat), intentRedirect, 0);

        Intent intent = new Intent(EditItemActivity.this, NotificationAlarm.class);
        intent.putExtra(Keys.KEY_TITLE.name(), "Out of stock!");
        intent.putExtra(Keys.KEY_MSG.name(), body);
        intent.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
        intent.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect);

        PendingIntent pendInt0d = PendingIntent.getBroadcast(EditItemActivity.this,
                Integer.parseInt(reqCodeRepeat), intent, 0);

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1000 * 30,
                1000 * 30, pendInt0d);

//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1000 * 5,
//                MILISECOND_IN_24HRS * 3, pendInt0d);
    }

    private void initNotifStock (String title, String body, PendingIntent pendingIntent) {
        createNotifChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setAutoCancel(true)
                .setSmallIcon(R.drawable.app_name_logo)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body);

//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(new Random().nextInt(), builder.build());

        //TODO
        Toast.makeText(EditItemActivity.this, "Stock notification done", Toast.LENGTH_SHORT).show();
    }
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