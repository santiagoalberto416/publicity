package com.sparkcompass.tobaccodock.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pacificfjord.pfapi.TCSAppAction;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSMenu;
import com.pacificfjord.pfapi.TCSMenuItem;
import com.pacificfjord.pfapi.TCSSuccessDelegate;
import com.pacificfjord.pfapi.beacons.GimbalManager;
import com.pacificfjord.pfapi.utilites.TCSTwitter;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.beacons.BeaconsActivity;
import com.sparkcompass.tobaccodock.common.AppPageEnum;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.TCSFragment;
import com.sparkcompass.tobaccodock.common.WebViewActivity;
import com.sparkcompass.tobaccodock.home.banner.BannerFragmentAdapter;
import com.sparkcompass.tobaccodock.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.Status;


public class HomeFragment extends TCSFragment implements GimbalManager.BeaconPopCountDelegate {

    @Bind(R.id.banner_menu_pager)
    protected ViewPager bannerMenuPager;
    @Bind(R.id.button_1)
    protected ImageButton buttonOne;
    @Bind(R.id.button_2)
    protected ImageButton buttonTwo;
    @Bind(R.id.button_3)
    protected ImageButton buttonThree;
    @Bind(R.id.button_4)
    protected ImageButton buttonFour;
    @Bind(R.id.label_1)
    protected TextView labelOne;
    @Bind(R.id.label_2)
    protected TextView labelTwo;
    @Bind(R.id.label_3)
    protected TextView labelThree;
    @Bind(R.id.label_4)
    protected TextView labelFour;
    @Bind(R.id.container_1)
    protected LinearLayout containerOne;
    @Bind(R.id.container_2)
    protected LinearLayout containerTwo;
    @Bind(R.id.container_3)
    protected LinearLayout containerThree;
    @Bind(R.id.container_4)
    protected LinearLayout containerFour;
    @Bind(R.id.notifications_counter)
    protected TextView notificationsCounter;
    @Bind(R.id.toolbar_container)
    protected LinearLayout toolbarContainer;
    @Bind(R.id.notifications_container)
    protected RelativeLayout notificationsContainer;
    @Bind(R.id.beacons_info)
    protected RelativeLayout beaconsInfo;
    @Bind(R.id.messages_container)
    protected RelativeLayout messages_container;
    @Bind(R.id.explore)
    protected TextView whatsAroundMe;
    @Bind(R.id.beacon_counter)
    protected TextView beaconsCounter;
    @Bind(R.id.beacon_image)
    protected ImageView beaconImage;
    @Bind(R.id.notifications_image)
    protected ImageView notificationsImage;

    private List<Object> notificationsList;
    private MenuItemClickListener menuItemClickListener;
    private List<Status> statuses;
    private boolean mMessageReceiverRegistered = false;
    private List<Object> removedNotifications;
    private static ChangeFragmentListener changeFragmentListener;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String action = intent.getAction();

            if (action.equals(TCSAppInstance.kTCSAppActionsUpdated)) {
                reloadNotifications();
            }

            if (action.equals(TCSAppInstance.kTCSAppInstanceActive)) {
                setupBannerItems();
                setupToolbarButtons();
                syncNotifications();
            }
        }
    };

    public static HomeFragment newInstance(ChangeFragmentListener listener) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        changeFragmentListener = listener;

        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);
        customizeViews(TCSAppInstance.getInstance().getSelectedSkin());
        setupBannerItems();
        setupToolbarButtons();
        syncNotifications();
        GimbalManager.getInstance().setBeaconPopCountDelegate(this);
        beaconsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GimbalManager.getInstance().getBeaconCount() > 0) {
                    Intent i = new Intent(getContext(), BeaconsActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                } else {
                    Intent i = new Intent(getContext(), WebViewActivity.class);
                    i.putExtra(Constants.WEB_URL, Constants.HELP_URL);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                }
            }
        });

        messages_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragmentListener.changeFragment(AppPageEnum.CONTACT.NOTIFICATIONS);
            }
        });

        if (!mMessageReceiverRegistered) {
            LocalBroadcastManager.getInstance(getActivity())
                    .registerReceiver(mMessageReceiver,
                            new IntentFilter(TCSAppInstance.kTCSAppActionsUpdated));
            LocalBroadcastManager.getInstance(getActivity())
                    .registerReceiver(mMessageReceiver,
                            new IntentFilter(TCSAppInstance.kTCSAppInstanceActive));
            mMessageReceiverRegistered = true;
        }

        toolbarContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        beaconImage.setColorFilter(Color.argb(200, 255, 255, 255));
        beaconsCounter.setText("0");
        beaconsCounter.setVisibility(View.GONE);
        whatsAroundMe.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onDestroy() {
        if (mMessageReceiverRegistered) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        }
        super.onDestroy();
    }

    private void setupBannerItems() {
        BannerFragmentAdapter bannerFragmentAdapter = new BannerFragmentAdapter(getChildFragmentManager(),
                TCSAppInstance.getInstance().menuWithName(Constants.BANNER_MENU), menuItemClickListener);
        bannerMenuPager.setAdapter(bannerFragmentAdapter);
        bannerMenuPager.setOffscreenPageLimit(3);
        bannerMenuPager.setPageMargin(10);
    }

    private void setupToolbarButtons() {
        TCSMenu toolbarMenu = TCSAppInstance.getInstance().menuWithName(Constants.TOOLBAR_MENU);
        if (toolbarMenu != null) {
            int count = Math.min(4, toolbarMenu.menuItems.size());
            for (int i = 0; i < count; i++) {
                final TCSMenuItem menuItem = toolbarMenu.menuItems.get(i);
                switch (i) {
                    case 0:
                        ImageUtils.replaceImage(menuItem.getIconUrl(), buttonOne, getActivity());
                        containerOne.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (menuItemClickListener != null) {
                                    menuItemClickListener.itemClickedWithAction(menuItem.getAction());
                                }
                            }
                        });
                        labelOne.setText(menuItem.getName());
                        break;
                    case 1:
                        ImageUtils.replaceImage(menuItem.getIconUrl(), buttonTwo, getActivity());
                        containerTwo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (menuItemClickListener != null) {
                                    menuItemClickListener.itemClickedWithAction(menuItem.getAction());
                                }
                            }
                        });
                        labelTwo.setText(menuItem.getName());
                        break;
                    case 2:
                        ImageUtils.replaceImage(menuItem.getIconUrl(), buttonThree, getActivity());
                        containerThree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (menuItemClickListener != null) {
                                    menuItemClickListener.itemClickedWithAction(menuItem.getAction());
                                }
                            }
                        });
                        labelThree.setText(menuItem.getName());
                        break;
                    case 3:
                        ImageUtils.replaceImage(menuItem.getIconUrl(), buttonFour, getActivity());
                        containerFour.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (menuItemClickListener != null) {
                                    menuItemClickListener.itemClickedWithAction(menuItem.getAction());
                                }
                            }
                        });
                        labelFour.setText(menuItem.getName());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void syncNotifications() {
        TCSAppInstance.getInstance().getNotifyActionsWithCompletion(new TCSSuccessDelegate() {
            @Override
            public void done(boolean success) {
                reloadNotifications();
            }
        });

        fetchTwitterTimeLine();

    }

    private void fetchTwitterTimeLine() {
        TCSMenu menu = TCSAppInstance.getInstance().menuWithName(Constants.TWITTER);
        if (menu != null) {
            TCSMenuItem item = menu.menuItems.get(0);
            String twitterHandle = item.getName();
            TCSTwitter.getUserTimeline(twitterHandle, new TCSTwitter.UserTimelineCallback() {
                @Override
                public void done(boolean success, List<Status> ls) {
                    statuses = ls;
                    reloadNotifications();
                }
            });
        }
    }

    private void reloadNotifications() {
        notificationsList = new ArrayList<>();
        for (TCSAppAction action : TCSAppInstance.getInstance().getAppActionList()) {
            if (removedNotifications != null && removedNotifications.contains(action)) {
                continue;
            }
            notificationsList.add(action);
        }

        if (statuses != null) {
            for (Status status : statuses) {
                notificationsList.add(status);
            }
        }

        if (notificationsList.size() > 0) {
            notificationsCounter.setVisibility(View.VISIBLE);
            notificationsCounter.setText(Integer.toString(notificationsList.size()));
            notificationsImage.setColorFilter(Color.argb(255, 255, 255, 255));
        } else {
            notificationsCounter.setVisibility(View.GONE);
            notificationsCounter.setText("0");
            notificationsImage.setColorFilter(Color.argb(235, 255, 255, 255));
        }


    }

    public void setMenuItemListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {

        toolbarContainer.setBackgroundColor(Color.parseColor(skinTemplate.getColor()));


        if (skinTemplate.getMenu().containsKey(TCSSkin.BG_COLOR) &&
                !skinTemplate.getMenu().get(TCSSkin.BG_COLOR).isEmpty()) {
            notificationsContainer.setBackgroundColor(Color.parseColor("#000000"));
        } else {
            notificationsContainer.setBackgroundColor(Color.parseColor("#000000"));
        }
    }

    @Override
    public void beaconPopCountUpdated() {
        if (GimbalManager.getInstance().getBeaconCount() > 0) {
            beaconImage.setColorFilter(Color.argb(255, 255, 255, 255));
            beaconsCounter.setText(Integer.toString(GimbalManager.getInstance().getBeaconCount()));
            beaconsCounter.setVisibility(View.VISIBLE);
            whatsAroundMe.setVisibility(View.VISIBLE);
        } else {
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        beaconImage.setColorFilter(Color.argb(200, 255, 255, 255));
                        beaconsCounter.setText("0");
                        beaconsCounter.setVisibility(View.GONE);
                        whatsAroundMe.setVisibility(View.GONE);
                    }
                });
            }

        }
    }


}
