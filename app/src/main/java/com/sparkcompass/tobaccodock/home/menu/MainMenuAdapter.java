package com.sparkcompass.tobaccodock.home.menu;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSMenu;
import com.pacificfjord.pfapi.TCSMenuItem;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.views.TCSMenuItemView;
import com.sparkcompass.tobaccodock.home.MenuItemClickListener;
import com.sparkcompass.tobaccodock.utils.ImageUtils;

/**
 * Created by mind-p6 on 8/29/15.
 */
public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuViewHolder> implements View.OnClickListener {

    private TCSMenu menu;
    private Context context;
    private MenuItemClickListener menuItemClickListener;

    public MainMenuAdapter(Context context, TCSMenu menu, MenuItemClickListener menuItemClickListener) {
        this.menu = menu;
        this.context = context;
        this.menuItemClickListener = menuItemClickListener;
    }

    public void setItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }

    @Override
    public MainMenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tcs_menu_item, viewGroup, false);

        return new MainMenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MainMenuViewHolder mainMenuViewHolder, int i) {
        TCSMenuItem menuItem = menu.menuItems.get(i);
        ImageUtils.replaceImage(menuItem.getIconUrl(), mainMenuViewHolder.menuItemImage, context);

        ((TCSMenuItemView) mainMenuViewHolder.itemView).setAction(menu.menuItems.get(i).getAction());
        mainMenuViewHolder.itemView.setOnClickListener(this);

        TCSSkin appSkin = TCSAppInstance.getInstance().getSelectedSkin();
        if (appSkin.getMenu().containsKey(TCSSkin.BG_COLOR) && !appSkin.getMenu().get(TCSSkin.BG_COLOR).isEmpty())
            mainMenuViewHolder.itemView.setBackgroundColor(Color.parseColor(appSkin.getMenu().get(TCSSkin.BG_COLOR)));

        mainMenuViewHolder.menuItemTitle.setText(menuItem.getName());
    }

    @Override
    public int getItemCount() {
        return menu == null ? 0 : menu.menuItems.size();
    }

    @Override
    public void onClick(View v) {
        if (menuItemClickListener != null) {
            menuItemClickListener.itemClickedWithAction(((TCSMenuItemView) v).getAction());
        }
    }

    private TCSMenuItem getItem(int position) {
        return menu.menuItems.get(position);
    }
}
