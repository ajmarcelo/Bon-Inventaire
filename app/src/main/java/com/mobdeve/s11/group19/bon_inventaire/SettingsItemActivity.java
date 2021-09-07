package com.mobdeve.s11.group19.bon_inventaire;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class SettingsItemActivity extends AppCompatActivity {

    public static final String CHANNEL_NAME = "Bon_Inventaire";
    public static final String CHANNEL_ID = "BI_Notify";

    private TextView tvEdit;
    private TextView tvDelete;
    private ImageButton ibBack;

    private Dialog dialog;
    private Button btnDeleteItemContinue;
    private Button btnDeleteItemCancel;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

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
     * Initializes the dialog box confirmation for deleting the user's account.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initConfirmationDialogBox() {
        dialog = new Dialog(SettingsItemActivity.this);
        dialog.setContentView(R.layout.confirmation_delete_item);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.confirmation;
        btnDeleteItemCancel = dialog.findViewById(R.id.btn_confirm_delete_item_cancel);
        btnDeleteItemContinue = dialog.findViewById(R.id.btn_confirm_delete_item_continue);
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
     * Initializes the intent for the next activity (editing the current user's item information).
     */
    private void initEdit() {
        this.tvEdit = findViewById(R.id.tv_settings_item_edit);
        this.tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsItemActivity.this, EditItemActivity.class);
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
     * Initializes the deletion of the current user's account.
     */
    private void initDelete() {
        //Triggers the dialog box for the confirmation for deleting the current user's item
        this.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        //Proceeds to deleting the item in the database
        this.btnDeleteItemContinue.setOnClickListener(new View.OnClickListener() {
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
                getUserName(id, name);
            }
        });

        //Cancels the deletion of the item of the user
        this.btnDeleteItemCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * Creates a notification channel for the notifications
     */
    private void createNotifChannel () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableVibration(true);
            channel.enableLights(true);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    /**
     * Gets the name of the current user for the following notification-related functions
     */
    private void getUserName(int itemId, String itemName) {
        mDatabase.getReference(Collections.users.name())
                .child(mAuth.getCurrentUser().getUid()).child(Collections.name.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.getValue().toString();
                        cancelNotifStockRepeat("Bonjour, " + name + "! " + itemName + "is out of stock.", itemId);
                        cancelNotifExp("Bonjour, " + name + "! " + itemName, itemId);
                        cancelNotifExpOthers("Bonjour, " + name + "! " + itemName, itemId);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Can't retrieve data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Removes the repeating notification set for an item
     * @param body      The body of the notification to be displayed
     * @param itemId    The ID of the current item to be deleted
     */
    private void cancelNotifStockRepeat (String body, int itemId) {
        createNotifChannel();

        String reqCodeRepeat = Integer.toString(itemId) + "999";

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(SettingsItemActivity.this, NotificationAlarm.class);
        intent.putExtra(Keys.KEY_TITLE.name(), "Out of stock!");
        intent.putExtra(Keys.KEY_MSG.name(), body);
        intent.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);

        PendingIntent pendInt0d = PendingIntent.getBroadcast(SettingsItemActivity.this,
                Integer.parseInt(reqCodeRepeat), intent, 0);

        alarmManager.cancel(pendInt0d);
    }

    /**
     * Removes the expiration notification set for an item on its exact expiration date
     * @param body      The body of the notification to be displayed
     * @param itemId    The ID of the current item to be deleted
     */
    private void cancelNotifExp (String body, int itemId) {
        String expiredMsg = " has expired";
        String expiredTitle = "Item expired";
        String reqCode0d = Integer.toString(itemId) + "0";

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intentRedirect0d = new Intent(this, HomeActivity.class);
        intentRedirect0d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingRedirect0d = PendingIntent.getActivity(this, Integer.parseInt(reqCode0d), intentRedirect0d, 0);

        Intent intent0d = new Intent(SettingsItemActivity.this, NotificationAlarm.class);
        intent0d.putExtra(Keys.KEY_TITLE.name(), expiredTitle);
        intent0d.putExtra(Keys.KEY_MSG.name(), body + expiredMsg);
        intent0d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
        intent0d.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect0d);

        PendingIntent pendInt0d = PendingIntent.getBroadcast(SettingsItemActivity.this,
                Integer.parseInt(reqCode0d), intent0d, 0);

        alarmManager.cancel(pendInt0d);
    }

    /**
     * Removes the three (3) expiration notifications set for an item before its exact expiration date
     * @param body      The body of the notification to be displayed
     * @param itemId    The ID of the current item to be deleted
     */
    private void cancelNotifExpOthers (String body, int itemId) {
        String expireSoonMsg = " will expire in ";
        String expireSoonTitle = "Item expiring soon!";
        String reqCode1d = Integer.toString(itemId) + "1";
        String reqCode3d = Integer.toString(itemId) + "3";
        String reqCode7d = Integer.toString(itemId) + "7";

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intentRedirect1d = new Intent(this, HomeActivity.class);
        intentRedirect1d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingRedirect1d = PendingIntent.getActivity(this, Integer.parseInt(reqCode1d), intentRedirect1d, 0);

        Intent intent1d = new Intent(SettingsItemActivity.this, NotificationAlarm.class);
        intent1d.putExtra(Keys.KEY_TITLE.name(), expireSoonTitle);
        intent1d.putExtra(Keys.KEY_MSG.name(), body + expireSoonMsg + "one (1) day");
        intent1d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
        intent1d.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect1d);

        PendingIntent pendInt1d = PendingIntent.getBroadcast(SettingsItemActivity.this,
                Integer.parseInt(reqCode1d), intent1d, 0);

        alarmManager.cancel(pendInt1d);

        Intent intentRedirect3d = new Intent(this, HomeActivity.class);
        intentRedirect3d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingRedirect3d = PendingIntent.getActivity(this, Integer.parseInt(reqCode3d), intentRedirect3d, 0);

        Intent intent3d = new Intent(SettingsItemActivity.this, NotificationAlarm.class);
        intent3d.putExtra(Keys.KEY_TITLE.name(), expireSoonTitle);
        intent3d.putExtra(Keys.KEY_MSG.name(), body + expireSoonMsg + "three (3) days");
        intent3d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
        intent3d.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect3d);

        PendingIntent pendInt3d = PendingIntent.getBroadcast(SettingsItemActivity.this,
                Integer.parseInt(reqCode3d), intent3d, 0);

        alarmManager.cancel(pendInt3d);

        Intent intentRedirect7d = new Intent(this, HomeActivity.class);
        intentRedirect7d.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingRedirect7d = PendingIntent.getActivity(this, Integer.parseInt(reqCode7d), intentRedirect7d, 0);

        Intent intent7d = new Intent(SettingsItemActivity.this, NotificationAlarm.class);
        intent7d.putExtra(Keys.KEY_TITLE.name(), expireSoonTitle);
        intent7d.putExtra(Keys.KEY_MSG.name(), body + expireSoonMsg + "seven (7) days");
        intent7d.putExtra(Keys.KEY_CHANNEL_ID.name(), CHANNEL_ID);
        intent7d.putExtra(Keys.KEY_REDIRECT_INTENT.name(), pendingRedirect7d);

        PendingIntent pendInt7d = PendingIntent.getBroadcast(SettingsItemActivity.this,
                Integer.parseInt(reqCode7d), intent7d, 0);

        alarmManager.cancel(pendInt7d);
    }

    //TODO
    /**
     * Retrieves the items of the current user.
     * @param item
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
    //TODO
    /**
     * Storing the items from the deleted list to the "Unlisted" list.
     * @param allItem
     * @param item
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