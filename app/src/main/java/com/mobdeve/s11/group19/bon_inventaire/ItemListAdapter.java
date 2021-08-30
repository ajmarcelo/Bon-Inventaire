package com.mobdeve.s11.group19.bon_inventaire;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListViewHolder> {

    public static final String KEY_NAME = "KEY_NAME";
    public static final String KEY_LIST = "KEY_LIST";
    public static final String KEY_NUM_STOCKS = "KEY_NUM_STOCKS";
    public static final String KEY_EXPIRE_DATE = "KEY_EXPIRE_DATE";
    public static final String KEY_NOTE = "KEY_NOTE";
    public static final String KEY_ID = "KEY_ID";

    private ArrayList<Item> dataItem;

    public ItemListAdapter(ArrayList<Item> dataItem) {
        this.dataItem = dataItem;
    }

    @NonNull
    @NotNull
    @Override
    public ItemListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.per_item_list, parent, false);

        ItemListViewHolder itemListViewHolder = new ItemListViewHolder(itemView);

        itemListViewHolder.getClListItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ItemViewInListActivity.class);

                intent.putExtra(KEY_NAME, dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemName());
                intent.putExtra(KEY_LIST, dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemList());
                intent.putExtra(KEY_NUM_STOCKS, dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemNumStocks());
                intent.putExtra(KEY_EXPIRE_DATE, dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemExpireDate());
                intent.putExtra(KEY_NOTE, dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemNote());

                v.getContext().startActivity(intent);

            }
        });

        return itemListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemListViewHolder holder, int position) {
        holder.setTvListItemName(dataItem.get(position).getItemName());
        holder.setTvListItemDate(dataItem.get(position).getItemExpireDate());
        holder.setTvListItemStocks(dataItem.get(position).getItemNumStocks() + " QTY");
    }

    @Override
    public int getItemCount() {
        return this.dataItem.size();
    }
}
