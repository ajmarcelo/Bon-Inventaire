package com.mobdeve.s11.group19.bon_inventaire;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListViewHolder> {

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

                intent.putExtra(Keys.KEY_NAME.name(), dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemName());
                intent.putExtra(Keys.KEY_LIST.name(), dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemList());
                intent.putExtra(Keys.KEY_NUM_STOCKS.name(), dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemNumStocks());
                intent.putExtra(Keys.KEY_EXPIRE_DATE.name(), dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemExpireDate());
                intent.putExtra(Keys.KEY_NOTE.name(), dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemNote());
                intent.putExtra(Keys.KEY_ITEM_ID.name(), dataItem.get(itemListViewHolder.getBindingAdapterPosition()).getItemID());

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
