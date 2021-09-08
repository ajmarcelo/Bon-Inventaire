package com.mobdeve.s11.group19.bon_inventaire;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ItemAllViewHolder extends RecyclerView.ViewHolder {

    private ConstraintLayout clAllItem;
    private TextView tvAllItemName;
    private TextView tvAllItemList;
    private TextView tvAllItemStocks;
    private TextView tvAllItemDate;

    public ItemAllViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
        super(itemView);

        this.clAllItem = itemView.findViewById(R.id.cl_all_item);
        this.tvAllItemName = itemView.findViewById(R.id.tv_all_item_name);
        this.tvAllItemList = itemView.findViewById(R.id.tv_all_item_list);
        this.tvAllItemStocks = itemView.findViewById(R.id.tv_all_item_stocks);
        this.tvAllItemDate = itemView.findViewById(R.id.tv_all_item_date);
    }

    /**
     * Returns the constraint layout under all_item_list.xml
     * @return  Returns a constraint layout
     */
    public ConstraintLayout getClAllItem() { return this.clAllItem; }

    /**
     * Sets the text of item name
     * @param tvAllItemName The String containing the name of an item
     */
    public void setTvAllItemName (String tvAllItemName) {
        this.tvAllItemName.setText(tvAllItemName);
    }

    /**
     * Sets the text of item list
     * @param tvAllItemList The String containing the name of the list where a certain item belongs
     */
    public void setTvAllItemList (String tvAllItemList) {
        this.tvAllItemList.setText(tvAllItemList);
    }

    /**
     * Sets the text of the item's number of stocked
     * @param tvAllItemStocks The String containing the number of stocks of an item
     */
    public void setTvAllItemStocks (String tvAllItemStocks) {
        this.tvAllItemStocks.setText(tvAllItemStocks);
    }

    /**
     * Sets the text of the item's expiry date
     * @param tvAllItemDate The String containing the expiration date of an item
     */
    public void setTvAllItemDate (String tvAllItemDate) {
        this.tvAllItemDate.setText(tvAllItemDate);
    }
}

