package com.mobdeve.s11.group19.bon_inventaire;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListViewHolder> {

    private ArrayList<List> dataList;

    public ListAdapter(ArrayList<List> dataList) {
        this.dataList = dataList;
    }

    /**
     * creates a new ViewHolder object whenever the RecyclerView needs for showing all items.
     * This is the moment when the row layout is inflated, passed to the ViewHolder
     * object and each child view can be found and stored.
     * @param parent the ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType the view type of the new View.
     */
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.all_list_list, parent, false);

        ListViewHolder listViewHolder = new ListViewHolder(itemView);

        listViewHolder.getClAllList().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ItemListActivity.class);

                intent.putExtra(Keys.KEY_LIST.name(), dataList.get(listViewHolder.getBindingAdapterPosition()).getListName());
                intent.putExtra(Keys.KEY_DESCRIPTION.name(), dataList.get(listViewHolder.getBindingAdapterPosition()).getListDescription());
                intent.putExtra(Keys.KEY_LIST_ID.name(), dataList.get(listViewHolder.getBindingAdapterPosition()).getListID());

                v.getContext().startActivity(intent);
            }
        });

        return listViewHolder;
    }

    /**
     * This method is used to update the contents of the itemView to reflect the item at the given position.
     * @param holder the ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position the position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull ListViewHolder holder, int position) {
        holder.setTvAllListName(dataList.get(position).getListName());
    }

    /**
     * It returns The number of items currently available in adapter.
     * @return Returns an integer of the adapter's current number of items
     */
    @Override
    public int getItemCount() {
        return this.dataList.size();
    }
}
