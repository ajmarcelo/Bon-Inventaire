package com.mobdeve.s11.group19.bon_inventaire;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ItemListViewHolder extends RecyclerView.ViewHolder{

    private ConstraintLayout clListItem;
    private TextView tvListItemName;
    private TextView tvListItemStocks;
    private TextView tvListItemDate;

    public ItemListViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
        super(itemView);

        this.clListItem = itemView.findViewById(R.id.cl_list_item);
        this.tvListItemName = itemView.findViewById(R.id.tv_per_item_name);
        this.tvListItemStocks = itemView.findViewById(R.id.tv_per_item_stocks);
        this.tvListItemDate = itemView.findViewById(R.id.tv_per_item_date);
    }

    /**
     * Returns the constraint layout under all_item_list.xml
     * @return  Returns a constraint layout
     */
    public ConstraintLayout getClListItem() { return this.clListItem; }

    /**
     * Sets the text of item name
     * @param tvListItemName The String containing the name of an item
     */
    public void setTvListItemName (String tvListItemName) {
        this.tvListItemName.setText(tvListItemName);
    }

    /**
     * Sets the text of the item's number of stocked
     * @param tvListItemStocks The String containing the number of stocks of an item
     */
    public void setTvListItemStocks (String tvListItemStocks) {
        this.tvListItemStocks.setText(tvListItemStocks);
    }

    /**
     * Sets the text of the item's expiry date
     * @param tvListItemDate The String containing the expiration date of an item
     */
    public void setTvListItemDate (String tvListItemDate) {
        this.tvListItemDate.setText(tvListItemDate);
    }
}
