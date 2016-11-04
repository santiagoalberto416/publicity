package com.sparkcompass.tobaccodock.home.banner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.pacificfjord.pfapi.TCSMenu;
import com.sparkcompass.tobaccodock.home.MenuItemClickListener;

import java.util.HashMap;

/**
 * Created by mind-p6 on 2/10/15.
 */
public class BannerFragmentAdapter extends FragmentPagerAdapter {

    private TCSMenu menu;
    private FragmentManager fragmentManager;
    private HashMap<Integer, String> tags;
    private MenuItemClickListener menuItemClickListener;

    public void setMenu(TCSMenu menu) {
        boolean update = this.menu != null;
        this.menu = menu;

        if (update) {
            for (int i = 0; i < menu.menuItems.size(); i++) {
                String tag = tags.get(i);
                if (tag != null) {
                    Fragment fragment = fragmentManager.findFragmentByTag(tag);
                    if (fragment != null)
                        ((BannerItemFragment) fragment).updateMenuItem(menu.menuItems.get(i));
                }
            }
        }

        notifyDataSetChanged();
    }

    public BannerFragmentAdapter(FragmentManager fragmentManager, TCSMenu menu, MenuItemClickListener menuItemClickListener) {
        super(fragmentManager);
        this.fragmentManager = fragmentManager;
        tags = new HashMap<>();
        this.menuItemClickListener = menuItemClickListener;
        setMenu(menu);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment item = (Fragment) super.instantiateItem(container, position);
        String tag = item.getTag();

        if (tag != null) {
            tags.put(position, tag);
        }

        return item;
    }

    @Override
    public Fragment getItem(int position) {
        BannerItemFragment fragment = new BannerItemFragment();
        fragment.setMenuItem(menu.menuItems.get(position));
        fragment.setMenuItemClickListener(menuItemClickListener);
        return fragment;
    }

    @Override
    public int getCount() {
        return menu == null ? 0 : menu.menuItems.size();
    }
}
