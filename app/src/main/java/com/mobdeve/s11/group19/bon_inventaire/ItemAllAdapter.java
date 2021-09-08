package com.mobdeve.s11.group19.bon_inventaire;

import android.app.Activity;
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
    private Activity activity;

    public ItemAllAdapter(ArrayList<Item> dataItem, Activity activity) {
        this.dataItem = dataItem;
        this.activity = activity;
    }

    /**
     * Creates a new ViewHolder object whenever the RecyclerView needs for showing all items.
     * This is the moment when the row layout is inflated, passed to the ViewHolder
     * object and each child view can be found and stored.
     * @param parent the ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType the view type of the new View.
     */
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

                activity.startActivityForResult(intent,2);

            }
        });

        return itemViewHolder;
    }

    /**
     * This method is used to update the contents of the itemView to reflect the item at the given position.
     * @param holder the ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position the position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemAllViewHolder holder, int position) {
        holder.setTvAllItemName(dataItem.get(position).getItemName());
        holder.setTvAllItemList(dataItem.get(position).getItemList());
        holder.setTvAllItemDate(dataItem.get(position).getItemExpireDate());
        holder.setTvAllItemStocks(dataItem.get(position).getItemNumStocks() + " QTY");
    }

    /**
     * It returns The number of items currently available in adapter.
     * @return  Returns an integer of the adapter's current number of items
     */
    @Override
    public int getItemCount() {
        return this.dataItem.size();
    }

    /**
     * It sets the data to be used by the adapter
     * @param data  The data to update the view with
     */
    public void setData(ArrayList<Item> data){
        this.dataItem.clear();
        this.dataItem.addAll(data);
        notifyDataSetChanged();
    }
}
