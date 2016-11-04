package com.sparkcompass.tobaccodock.home.banner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pacificfjord.pfapi.TCSMenuItem;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.home.MenuItemClickListener;
import com.sparkcompass.tobaccodock.utils.ImageUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mind-p6 on 8/29/15.
 */
public class BannerItemFragment extends Fragment {

    @Bind(R.id.banner_image)
    protected ImageView bannerImage;
    private TCSMenuItem menuItem;
    private MenuItemClickListener menuItemClickListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_banner_item, container, false);
        ButterKnife.bind(this, rootView);

        if (menuItem != null) {
            ImageUtils.replaceImage(menuItem.getIconUrl(), bannerImage, getActivity());
            bannerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (menuItemClickListener != null) {
                        menuItemClickListener.itemClickedWithAction(menuItem.getAction());
                    }
                }
            });
        }

        return rootView;
    }

    public void setMenuItem(TCSMenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public void updateMenuItem(TCSMenuItem menuItem) {
        if (this.menuItem.getIconUrl().compareTo(menuItem.getIconUrl()) != 0)
            ImageUtils.replaceImage(menuItem.getIconUrl(), bannerImage, getActivity());

        this.menuItem = menuItem;
    }

    public void setMenuItemClickListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }
}
