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

    /**
     * Initializes the activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        initFirebase();
        initConfiguration();

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
     * Gets the name of the lists from the current user's lists retrieved from the database.
     * @param userList
     * @return
     */
    private String[] dropdownList (ArrayList<List> userList) {
        int n = userList.size();
        dropdown = new String[userList.size()];
        for(int i = 0; i < n; i++) {
            dropdown[i] = userList.get(i).getListName();
        }
        return dropdown;
    }

    /**
     * Initializes the adding of an item.
     */
    private void initSave() {
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
                    retrieveItem(item);
                }
                else
                    Toast.makeText(getApplicationContext(), "Adding Item Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Checks if the input for each field is valid.
     * @param name
     * @param list
     * @param numStocks
     * @return
     */
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

    /**
     * Retrieves the items of the user from the database
     * @param item
     */
    public void retrieveItem(Item item) {
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
                            storeItem(allItem);
                        } else {
                            item.setItemID(allItem.size());
                            allItem.add(0,item);
                            storeItem(allItem);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Stores the updated items to the database.
     * @param allItem
     */
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

                            intent.putExtra(Keys.KEY_NAME.name(), allItem.get(0).getItemName());
                            intent.putExtra(Keys.KEY_LIST.name(), allItem.get(0).getItemList());
                            intent.putExtra(Keys.KEY_NUM_STOCKS.name(), allItem.get(0).getItemNumStocks());
                            intent.putExtra(Keys.KEY_EXPIRE_DATE.name(), allItem.get(0).getItemExpireDate());
                            intent.putExtra(Keys.KEY_NOTE.name(), allItem.get(0).getItemNote());
                            intent.putExtra(Keys.KEY_ITEM_ID.name(), allItem.get(0).getItemID());

                            getUserName(allItem.get(0).getItemID(), allItem.get(0).getItemExpireDate().toString(),
                                    allItem.get(0).getItemName(), allItem.get(0).getItemNumStocks());

                            //TODO
                            Toast.makeText(AddItemActivity.this, "DONE", Toast.LENGTH_SHORT).show();

                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Can't Add to the database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Initializes the cancellation of the update or activity.
     */
    private void initCancel() {
        this.ibCancel = findViewById(R.id.ib_add_item_cancel);
        this.ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Gets the name of the user to be used for the greeting message in the notification.
     * @param itemId
     * @param expDate
     * @param itemName
     * @param numStocks
     */
    private void getUserName(int itemId, String expDate, String itemName, int numStocks) {
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.name.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue().toString();
                        initNotifStockRepeat("Bonjour, " + name + "! " + itemName + " is out of stock.",
                                numStocks, itemId);
                        initNotifExp("Bonjour, " + name + "! " + itemName, itemId, expDate);
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

    private long getExpiryDateInMs (long expDate, long dateNow) {
        long betweenMS = expDate - dateNow;
        long expAlarm = dateNow + betweenMS + 4000;
        //TODO
//        Toast.makeText(AddItemActivity.this, "BET: " + betweenMS, Toast.LENGTH_SHORT).show();
        return expAlarm;
    }

    private void initNotifStockRepeat (String body, int numStocks, int itemId) {
        if (numStocks == 0) {
            String reqCodeRepeat = Integer.toString(itemId) + "999";
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent intentRedirect = new Intent(this, HomeActivity.class);
            intentRedirect.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingRedirect = PendingIntent.getActivity(this, Integer.parseInt(reqCodeRepeat), intentRedirect, 0);

            Intent intent = new Intent(AddItemActivity.this, NotificationAlarm.class);
            intent.putExtra(Keys.KEY_TITLE.name(), "Out of stock!");
            intent.putExtra(Keys.KEY_MSG.name(), body);
            intent.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
            intent.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect);

            PendingIntent pendInt0d = PendingIntent.getBroadcast(AddItemActivity.this,
                    Integer.parseInt(reqCodeRepeat), intent, 0);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1000 * 30,
            1000 * 30, pendInt0d);

//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, 1000 * 5,
//                MILISECOND_IN_24HRS * 3, pendInt0d);
        }
    }

    private void initNotifExp (String body, int itemId, String expDate) {
        String expiredMsg = " has expired";
        String expireSoonMsg = " will expire in ";

        String expireSoonTitle = "Item expiring soon!";
        String expiredTitle = "Item expired";

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
        long timeAlarm;
//        Toast.makeText(AddItemActivity.this, "NOW: " + timeNow, Toast.LENGTH_SHORT).show();
//        Toast.makeText(AddItemActivity.this, "1Day: " + expDate1Day, Toast.LENGTH_SHORT).show();
//        Toast.makeText(AddItemActivity.this, "ALARM: " + timeAlarm, Toast.LENGTH_SHORT).show();
        String reqCode0d = Integer.toString(itemId) + "0";
        String reqCode1d = Integer.toString(itemId) + "1";
        String reqCode3d = Integer.toString(itemId) + "3";
        String reqCode7d = Integer.toString(itemId) + "7";

//        Log.d("EXPMS", "Expiry date: " + expDateMS);
//        Log.d("EXPMS", "Minus 7 days: " + (expDateMS - (MILISECOND_IN_24HRS * 7)));
//        Log.d("EXPMS", "Time now: " + timeNow)

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (expDateMS == timeNow) {
            Intent intentRedirect0d = new Intent(this, HomeActivity.class);
            intentRedirect0d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingRedirect0d = PendingIntent.getActivity(this, Integer.parseInt(reqCode0d), intentRedirect0d, 0);

            Intent intent0d = new Intent(AddItemActivity.this, NotificationAlarm.class);
            intent0d.putExtra(Keys.KEY_TITLE.name(), expiredTitle);
            intent0d.putExtra(Keys.KEY_MSG.name(), body + expiredMsg);
            intent0d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
            intent0d.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect0d);

            PendingIntent pendInt0d = PendingIntent.getBroadcast(AddItemActivity.this,
                    Integer.parseInt(reqCode0d), intent0d, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, timeNow, pendInt0d);
        }

        if (expDateMS - MILISECOND_IN_24HRS >= timeNow) {
            long expDate1Day = expDateMS - MILISECOND_IN_24HRS;
            timeAlarm = getExpiryDateInMs(expDate1Day, timeNow);

            Intent intentRedirect1d = new Intent(this, HomeActivity.class);
            intentRedirect1d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingRedirect1d = PendingIntent.getActivity(this, Integer.parseInt(reqCode1d), intentRedirect1d, 0);

            Intent intent1d = new Intent(AddItemActivity.this, NotificationAlarm.class);
            intent1d.putExtra(Keys.KEY_TITLE.name(), expireSoonTitle);
            intent1d.putExtra(Keys.KEY_MSG.name(), body + expireSoonMsg + "one (1) day");
            intent1d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
            intent1d.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect1d);

            PendingIntent pendInt1d = PendingIntent.getBroadcast(AddItemActivity.this,
                    Integer.parseInt(reqCode1d), intent1d, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendInt1d);
        }

        if (expDateMS - (MILISECOND_IN_24HRS * 3) >= timeNow) {
            long expDate3Days = expDateMS - (MILISECOND_IN_24HRS * 3);
            timeAlarm = getExpiryDateInMs(expDate3Days, timeNow);

            Intent intentRedirect3d = new Intent(this, HomeActivity.class);
            intentRedirect3d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingRedirect3d = PendingIntent.getActivity(this, Integer.parseInt(reqCode3d), intentRedirect3d, 0);

            Intent intent3d = new Intent(AddItemActivity.this, NotificationAlarm.class);
            intent3d.putExtra(Keys.KEY_TITLE.name(), expireSoonTitle);
            intent3d.putExtra(Keys.KEY_MSG.name(), body + expireSoonMsg + "three (3) days");
            intent3d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
            intent3d.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect3d);

            PendingIntent pendInt3d = PendingIntent.getBroadcast(AddItemActivity.this,
                    Integer.parseInt(reqCode3d), intent3d, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendInt3d);
        }

        if (expDateMS - (MILISECOND_IN_24HRS * 7) >= timeNow) {
            long expDate7Days = expDateMS - (MILISECOND_IN_24HRS * 7);
            timeAlarm = getExpiryDateInMs(expDate7Days, timeNow);

            Intent intentRedirect7d = new Intent(this, HomeActivity.class);
            intentRedirect7d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingRedirect7d = PendingIntent.getActivity(this, Integer.parseInt(reqCode7d), intentRedirect7d, 0);

            Intent intent7d = new Intent(AddItemActivity.this, NotificationAlarm.class);
            intent7d.putExtra(Keys.KEY_TITLE.name(), expireSoonTitle);
            intent7d.putExtra(Keys.KEY_MSG.name(), body + expireSoonMsg + "seven (7) days");
            intent7d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
            intent7d.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect7d);

            PendingIntent pendInt7d = PendingIntent.getBroadcast(AddItemActivity.this,
                    Integer.parseInt(reqCode7d), intent7d, 0);

            alarmManager.set(AlarmManager.RTC_WAKEUP, timeAlarm, pendInt7d);
        }
    }
}