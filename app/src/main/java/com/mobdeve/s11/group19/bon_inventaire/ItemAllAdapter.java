package com.mobdeve.s11.group19.bon_inventaire;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ItemAllAdapter extends RecyclerView.Adapter<ItemAllViewHolder> {

    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_LIST = "KEY_LIST";
    public static final String KEY_NUM_STOCKS = "KEY_NUM_STOCKS";
    public static final String KEY_EXPIRE_DATE = "KEY_EXPIRE_DATE";
    public static final String KEY_NOTE = "KEY_NOTE";
    public static final String KEY_ID = "KEY_ID";

    private ArrayList<Item> dataItem;

    public ItemAllAdapter(ArrayList<Item> dataItem) {
        this.dataItem = dataItem;
    }

    @NonNull
    @NotNull
    @Override
    public ItemAllViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.all_item_list, parent, false);

        ItemAllViewHolder itemViewHolder = new ItemAllViewHolder(itemView);

        itemViewHolder.getClAllItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ItemViewActivity.class);

                intent.putExtra(KEY_NAME, dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemName());
                intent.putExtra(KEY_LIST, dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemList());
                intent.putExtra(KEY_NUM_STOCKS, dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemNumStocks());
                intent.putExtra(KEY_EXPIRE_DATE, dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemExpireDate());
                intent.putExtra(KEY_NOTE, dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemNote());

                v.getContext().startActivity(intent);

            }
        });

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemAllViewHolder holder, int position) {
        holder.setTvAllItemName(dataItem.get(position).getItemName());
        holder.setTvAllItemList(dataItem.get(position).getItemList());
        holder.setTvAllItemDate(dataItem.get(position).getItemExpireDate());
        holder.setTvAllItemStocks(dataItem.get(position).getItemNumStocks() + " QTY");
    }

    @Override
    public int getItemCount() {
        return this.dataItem.size();
    }
}
