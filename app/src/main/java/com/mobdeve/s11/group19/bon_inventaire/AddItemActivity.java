package com.mobdeve.s11.group19.bon_inventaire;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddItemActivity extends AppCompatActivity {

    public static final String CHANNEL_NAME = "Bon_Inventaire";
    public static final String CHANNEL_ID = "BI_Notify";
    public static final long MILISECOND_IN_24HRS = 86400000;

    private ImageButton ibSave;
    private ImageButton ibCancel;
    private EditText etName;
    AutoCompleteTextView etList;
    private ArrayList<List> userLists;
    private String[] dropdown;
    private EditText etNumStocks;
    private EditText etExpireDate;
    private EditText etNote;
    private ProgressBar pbAddItem;
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
        this.userLists = new ArrayList<List>();
        this.etName = findViewById(R.id.et_add_item_name);
        this.etNumStocks = findViewById(R.id.et_add_item_num_stocks);
        this.etExpireDate = findViewById(R.id.et_add_item_expire_date);
        this.etNote = findViewById(R.id.et_add_item_note);
        this.pbAddItem = findViewById(R.id.pb_add_item);
        this.etList = (AutoCompleteTextView) findViewById(R.id.et_add_item_list);

        //Date Picker
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
        String list =  intent.getStringExtra(Keys.KEY_LIST.name());

        this.etList.setText(list);

        //List Dropdown
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getUid()).child(Collections.lists.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<List>> t = new GenericTypeIndicator<ArrayList<List>>() {};
                        userLists =  snapshot.getValue(t);
                        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(AddItemActivity.this, R.layout.dropdown_item, dropdownList(userLists));
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
        String initDate = etExpireDate.getText().toString();

        this.ibSave = findViewById(R.id.ib_add_item_save);
        this.ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                if(!name.isEmpty())
                    name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
                String list = etList.getText().toString();
                String numStocks = etNumStocks.getText().toString();
                String expireDate = etExpireDate.getText().toString();
                String note = etNote.getText().toString();
                int id = 0;

                if (!checkField(name, list, numStocks)) {
                    //database
                    if(list.isEmpty())
                        list = "Unlisted";
                    Item item = new Item(name, list, note, Integer.parseInt(numStocks),expireDate, id);
//                    retrieveItem(item);
                    retrieveItem(item, initDate);
                }
                else
                    Toast.makeText(getApplicationContext(), "Adding Item Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkField(String name, String list, String numStocks) {
        boolean hasError = false;

        if(name.isEmpty()) {
            this.etName.setError("Required Field");
            this.etName.requestFocus();
            hasError = true;
        }
        if(!list.isEmpty())
            if(!ArrayUtils.contains(dropdown, list)) {
                this.etList.setError("List Not Created");
                this.etList.requestFocus();
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

//    public void retrieveItem(Item item) {
    public void retrieveItem(Item item, String initDate) {
        Toast.makeText(getApplicationContext(), "Adding item to the database...", Toast.LENGTH_SHORT).show();
        pbAddItem.setVisibility(View.VISIBLE);
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        GenericTypeIndicator<ArrayList<Item>> t = new GenericTypeIndicator<ArrayList<Item>>() {};
                        ArrayList<Item> allItem = snapshot.getValue(t);

                        if(allItem == null) {
                            allItem = new ArrayList<Item>();
                            allItem.add(0,item);
//                            storeItem(allItem);
                            storeItem(allItem, initDate);
                        } else {
                            item.setItemID(allItem.size());
                            allItem.add(0,item);
//                            storeItem(allItem);
                            storeItem(allItem, initDate);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private boolean isSameItem(ArrayList<Item> allItem, Item item){
//        for(int i = 0; i < allItem.size(); i++) {
//            Item tempItem = allItem.get(i);
//            if(tempItem.getItemName().equals(item.getItemName())){
//                if(tempItem.getItemList().equals(item.getItemList())){
//                    Toast.makeText(getApplicationContext(), "Item is on another List", Toast.LENGTH_SHORT).show();
//                }
//                return true;
//            }
//        }
//        return false;
//    }

//    private void storeItem(ArrayList<Item> allItem) {
    private void storeItem(ArrayList<Item> allItem, String initDate) {
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.items.name())
                .setValue(allItem)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully Added to the database", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();

                            intent.putExtra(Keys.KEY_NAME.name(), allItem.get(0).getItemName());
                            intent.putExtra(Keys.KEY_LIST.name(), allItem.get(0).getItemList());
                            intent.putExtra(Keys.KEY_NUM_STOCKS.name(), allItem.get(0).getItemNumStocks());
                            intent.putExtra(Keys.KEY_EXPIRE_DATE.name(), allItem.get(0).getItemExpireDate());
                            intent.putExtra(Keys.KEY_NOTE.name(), allItem.get(0).getItemNote());
                            intent.putExtra(Keys.KEY_ITEM_ID.name(), allItem.get(0).getItemID());

                            checkDate(allItem.get(0).getItemID(), allItem.get(0).getItemExpireDate().toString(),
                                initDate);

                            Toast.makeText(AddItemActivity.this, "DONE", Toast.LENGTH_SHORT).show();

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

    private void createNotifChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.enableLights(true);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    private long getExpiryDateInMs (long expDate, long dateNow) {
        long betweenMS = expDate - dateNow;
        long expAlarm = dateNow + betweenMS + 10000;
        //TODO
//        Toast.makeText(AddItemActivity.this, "BET: " + betweenMS, Toast.LENGTH_SHORT).show();
        return expAlarm;
    }

    private void checkDate (int itemId, String expDate, String oldDate) {
        String oldExpDate = oldDate;
        String newExpDate = expDate;

        if (!(oldExpDate.equals(newExpDate)))
            initNotifExp("Item expiring soon!", etName.getText().toString() + " will expire in ", itemId, newExpDate);
    }

    private void initNotifExp (String title, String body, int itemId, String expDate) {
        createNotifChannel();

        Date expDateInput = null;
        try {
            expDateInput = new SimpleDateFormat("MM/dd/yyyy").parse(expDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long timeNow = System.currentTimeMillis();
        long expDateMS = expDateInput.getTime();
        long temp = expDateMS;
        while (temp > timeNow) {
            temp -= MILISECOND_IN_24HRS;
        }
        long timeInMS = timeNow - temp;
        expDateMS += timeInMS;

//        Toast.makeText(AddItemActivity.this, "NOW: " + timeNow, Toast.LENGTH_SHORT).show();

        long expDate1Day = expDateMS - MILISECOND_IN_24HRS;
        long timeAlarm = getExpiryDateInMs(expDate1Day, timeNow);

//        Toast.makeText(AddItemActivity.this, "1Day: " + expDate1Day, Toast.LENGTH_SHORT).show();
//        Toast.makeText(AddItemActivity.this, "ALARM: " + timeAlarm, Toast.LENGTH_SHORT).show();

        String reqCode1d = Integer.toString(itemId) + "1";
        String reqCode3d = Integer.toString(itemId) + "3";
        String reqCode7d = Integer.toString(itemId) + "7";

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent1d = new Intent(AddItemActivity.this, NotificationAlarm.class);
        intent1d.putExtra(Keys.KEY_TITLE.name(), title);
        intent1d.putExtra(Keys.KEY_MSG.name(), body + "one (1) day");
        intent1d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);

        PendingIntent pendInt1d = PendingIntent.getBroadcast(AddItemActivity.this,
                Integer.parseInt(reqCode1d), intent1d, 0);

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendInt1d);

        if (expDateMS - (MILISECOND_IN_24HRS * 3) > timeNow) {
            long expDate3Days = expDateMS - (MILISECOND_IN_24HRS * 3);
            timeAlarm = getExpiryDateInMs(expDate3Days, timeNow);

            Intent intent3d = new Intent(AddItemActivity.this, NotificationAlarm.class);
            intent3d.putExtra(Keys.KEY_TITLE.name(), title);
            intent3d.putExtra(Keys.KEY_MSG.name(), body + "three (3) days");
            intent3d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);

            PendingIntent pendInt3d = PendingIntent.getBroadcast(AddItemActivity.this,
                    Integer.parseInt(reqCode3d), intent3d, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendInt3d);
        }

        if (expDateMS - (MILISECOND_IN_24HRS * 7) > timeNow) {
            long expDate7Days = expDateMS - (MILISECOND_IN_24HRS * 7);
            timeAlarm = getExpiryDateInMs(expDate7Days, timeNow);

            Intent intent7d = new Intent(AddItemActivity.this, NotificationAlarm.class);
            intent7d.putExtra(Keys.KEY_TITLE.name(), title);
            intent7d.putExtra(Keys.KEY_MSG.name(), body + "seven (7) days");
            intent7d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);

            PendingIntent pendInt7d = PendingIntent.getBroadcast(AddItemActivity.this,
                    Integer.parseInt(reqCode7d), intent7d, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendInt7d);
        }
    }
}