package com.mobdeve.s11.group19.bon_inventaire;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ItemViewInListActivity extends AppCompatActivity {

    private TextView tvName;
    private TextView tvList;
    private TextView tvNumStocks;
    private TextView tvExpireDate;
    private TextView tvNote;
    private FloatingActionButton fabEdit;
    private ImageButton ibBack;

    /**
     * Initializes the activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        initConfiguration();
        initValues();
        initEditItem();
        initBack();
    }

    /**
     * Set the flags of the windows, as per the WindowsManager.LayoutParams flags.
     */
    private void initConfiguration() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * Loads the data for onResume()
     */
    private void loadData() {
        Intent intent = getIntent();

        String name =  intent.getStringExtra(Keys.KEY_NAME.name());
        String list = intent.getStringExtra(Keys.KEY_LIST.name());
        int numStocks = intent.getIntExtra(Keys.KEY_NUM_STOCKS.name(),0);
        String expireDate = intent.getStringExtra(Keys.KEY_EXPIRE_DATE.name());
        String note = intent.getStringExtra(Keys.KEY_NOTE.name());
        int id = intent.getIntExtra(Keys.KEY_ITEM_ID.name(),0);

        tvName.setText(name);
        tvList.setText(list);
        tvNumStocks.setText(Integer.toString(numStocks));
        tvExpireDate.setText(expireDate);
        tvNote.setText(String.valueOf(note));
    }

    /**
     * When the activity enters the Resumed state, it comes to the foreground, and then the system invokes the onResume() callback.
     * Then calls loadData()
     */
    @Override
    protected void onResume() {
        super.onResume();

        this.loadData();
    }

    /**
     * Initializes the values to be shown in the activity
     */
    private void initValues(){
        this.tvName = findViewById(R.id.tv_item_view_name);
        this.tvList = findViewById(R.id.tv_item_view_list);
        this.tvNumStocks = findViewById(R.id.tv_item_view_num_stocks);
        this.tvExpireDate = findViewById(R.id.tv_item_view_expire_date);
        this.tvNote = findViewById(R.id.tv_item_view_note);

        Intent intent = getIntent();

        String name;
        String list;
        String note;
        int numStocks;
        String expireDate;

        name = intent.getStringExtra(Keys.KEY_NAME.name());
        list = intent.getStringExtra(Keys.KEY_LIST.name());
        note = intent.getStringExtra(Keys.KEY_NOTE.name());
        numStocks = intent.getIntExtra(Keys.KEY_NUM_STOCKS.name(),0);
        expireDate = intent.getStringExtra(Keys.KEY_EXPIRE_DATE.name());

        this.tvName.setText(name);
        this.tvList.setText(list);
        this.tvNumStocks.setText(Integer.toString(numStocks));
        this.tvExpireDate.setText(expireDate);
        this.tvNote.setText(String.valueOf(note));
    }

    /**
     * Initializes the intent for the next activity (editing an item)
     */
    private void initEditItem() {
        this.fabEdit = findViewById(R.id.fab_item_view_edit);
        this.fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemViewInListActivity.this, SettingsItemInListActivity.class);
                Intent info = getIntent();

                intent.putExtra(Keys.KEY_NAME.name(), info.getStringExtra(Keys.KEY_NAME.name()));
                intent.putExtra(Keys.KEY_LIST.name(), info.getStringExtra(Keys.KEY_LIST.name()));
                intent.putExtra(Keys.KEY_NOTE.name(), info.getStringExtra(Keys.KEY_NOTE.name()));
                intent.putExtra(Keys.KEY_NUM_STOCKS.name(), info.getIntExtra(Keys.KEY_NUM_STOCKS.name(),0));
                intent.putExtra(Keys.KEY_EXPIRE_DATE.name(), info.getStringExtra(Keys.KEY_EXPIRE_DATE.name()));
                intent.putExtra(Keys.KEY_ITEM_ID.name(), info.getIntExtra(Keys.KEY_ITEM_ID.name(),0));

                v.getContext().startActivity(intent);

                finish();
            }
        });
    }

    /**
     * Initializes the intent for the next activity (navigating back)
     */
    private void initBack() {
        this.ibBack = findViewById(R.id.ib_item_view_back);
        this.ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                Intent info = getIntent();

                intent.putExtra(Keys.KEY_NAME.name(), info.getStringExtra(Keys.KEY_NAME.name()));
                intent.putExtra(Keys.KEY_LIST.name(), info.getStringExtra(Keys.KEY_LIST.name()));
                intent.putExtra(Keys.KEY_NOTE.name(), info.getStringExtra(Keys.KEY_NOTE.name()));
                intent.putExtra(Keys.KEY_NUM_STOCKS.name(), info.getIntExtra(Keys.KEY_NUM_STOCKS.name(),0));
                intent.putExtra(Keys.KEY_EXPIRE_DATE.name(), info.getStringExtra(Keys.KEY_EXPIRE_DATE.name()));
                intent.putExtra(Keys.KEY_ITEM_ID.name(), info.getIntExtra(Keys.KEY_ITEM_ID.name(),0));

//                Toast.makeText(getApplicationContext(), info.getStringExtra(EditItemActivity.KEY_NAME), Toast.LENGTH_SHORT).show();


                finish();
            }
        });
    }
}