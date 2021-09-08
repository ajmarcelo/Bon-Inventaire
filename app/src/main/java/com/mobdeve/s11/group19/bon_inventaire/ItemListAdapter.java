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

public class ItemListAdapter extends RecyclerView.Adapter<ItemListViewHolder> {

    private ArrayList<Item> dataItem;
    private Activity activity;

    public ItemListAdapter(ArrayList<Item> dataItem, Activity activity) {
        this.dataItem = dataItem;
        this.activity = activity;
    }

    /**
     * creates a new ViewHolder object whenever the RecyclerView needs for showing items in current list.
     * This is the moment when the row layout is inflated, passed to the ViewHolder
     * object and each child view can be found and stored.
     * @param parent the ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType the view type of the new View.
     */
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

                activity.startActivityForResult(intent,1);
            }
        });

        return itemListViewHolder;
    }

    /**
     * This method is used to update the contents of the itemView to reflect the item at the given position.
     * @param holder the ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position the position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemListViewHolder holder, int position) {
        holder.setTvListItemName(dataItem.get(position).getItemName());
        holder.setTvListItemDate(dataItem.get(position).getItemExpireDate());
        holder.setTvListItemStocks(dataItem.get(position).getItemNumStocks() + " QTY");
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
     * @param data The data to update the view with
     */
    public void setData(ArrayList<Item> data){
        this.dataItem.clear();
        this.dataItem.addAll(data);
        notifyDataSetChanged();
    }
}
