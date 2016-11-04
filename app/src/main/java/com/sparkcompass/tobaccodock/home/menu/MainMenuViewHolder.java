package com.sparkcompass.tobaccodock.home.menu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.pacificfjord.pfapi.views.TCSTextView;
import com.sparkcompass.tobaccodock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mind-p6 on 8/29/15.
 */
public class MainMenuViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.menu_item_image)
    public ImageView menuItemImage;
    @Bind(R.id.menu_item_title)
    public TCSTextView menuItemTitle;
    public View itemView;

    public MainMenuViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
    }
}
