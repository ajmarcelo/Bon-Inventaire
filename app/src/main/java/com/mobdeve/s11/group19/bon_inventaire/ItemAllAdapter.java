package com.mobdeve.s11.group19.bon_inventaire;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class ItemAllAdapter extends RecyclerView.Adapter<ItemAllViewHolder> {

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

                intent.putExtra(Keys.KEY_NAME.name(), dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemName());
                intent.putExtra(Keys.KEY_LIST.name(), dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemList());
                intent.putExtra(Keys.KEY_NUM_STOCKS.name(), dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemNumStocks());
                intent.putExtra(Keys.KEY_EXPIRE_DATE.name(), dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemExpireDate());
                intent.putExtra(Keys.KEY_NOTE.name(), dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemNote());
                intent.putExtra(Keys.KEY_ITEM_ID.name(), dataItem.get(itemViewHolder.getBindingAdapterPosition()).getItemID());

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
