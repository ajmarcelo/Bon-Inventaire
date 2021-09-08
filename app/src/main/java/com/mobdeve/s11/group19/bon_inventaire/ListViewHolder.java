package com.mobdeve.s11.group19.bon_inventaire;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewHolder extends RecyclerView.ViewHolder {

    private ConstraintLayout clAllList;
    private TextView tvAllListName;

    public ListViewHolder(@NonNull @org.jetbrains.annotations.NotNull View itemView) {
        super(itemView);

        this.clAllList = itemView.findViewById(R.id.cl_all_list);
        this.tvAllListName = itemView.findViewById(R.id.tv_all_list_name);
    }

    /**
     * Returns the constraint layout under all_list_list.xml
     * @return Returns a constraint layout
     */
    public ConstraintLayout getClAllList() {
        return this.clAllList;
    }

    /**
     * Sets the text of item name
     * @param tvAllListName The String containing the name of an item
     */
    public void setTvAllListName (String tvAllListName) {
        this.tvAllListName.setText(tvAllListName);
    }
}

