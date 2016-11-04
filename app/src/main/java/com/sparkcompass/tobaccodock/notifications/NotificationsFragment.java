package com.sparkcompass.tobaccodock.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pacificfjord.pfapi.TCSAppAction;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSMenu;
import com.pacificfjord.pfapi.TCSMenuItem;
import com.pacificfjord.pfapi.TCSSuccessDelegate;
import com.pacificfjord.pfapi.utilites.TCSTwitter;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.Constants;
import com.sparkcompass.tobaccodock.common.TCSFragment;
import com.sparkcompass.tobaccodock.common.views.WrapContentLinearLayoutManager;
import com.sparkcompass.tobaccodock.home.MenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import twitter4j.Status;

public class NotificationsFragment extends TCSFragment implements NotificationsAdapter.RemovedNotificationListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.notifications_recycler_view)
    protected RecyclerView notificationsRecyclerView;
    private NotificationsAdapter adapter;
    private List<Object> notificationsList;
    private List<Status> statuses;
    private MenuItemClickListener menuItemClickListener;
    private List<Object> removedNotifications;
    @Bind(R.id.no_notifications)
    protected TextView noNotifications;
    private boolean mMessageReceiverRegistered = false;
    @Bind(R.id.progress_container)
    protected RelativeLayout progressContainer;
    @Bind(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    private boolean isRefreshing;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String action = intent.getAction();

            if (action.equals(TCSAppInstance.kTCSAppActionsUpdated)) {
                reloadNotifications();
            }

            if (action.equals(TCSAppInstance.kTCSAppInstanceActive)) {
                syncNotifications();
            }
        }
    };

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, rootView);
        syncNotifications();
        swipeRefreshLayout.setOnRefreshListener(this);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (notificationsList.get(viewHolder.getAdapterPosition()) instanceof TCSAppAction) {
                    TCSAppAction action = (TCSAppAction) notificationsList.get(viewHolder.getAdapterPosition());
                    TCSAppInstance.getInstance().removeAppAction(action);
                    reloadNotifications();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(notificationsRecyclerView);

        if (!mMessageReceiverRegistered) {
            LocalBroadcastManager.getInstance(getContext())
                    .registerReceiver(mMessageReceiver,
                            new IntentFilter(TCSAppInstance.kTCSAppActionsUpdated));
            LocalBroadcastManager.getInstance(getContext())
                    .registerReceiver(mMessageReceiver,
                            new IntentFilter(TCSAppInstance.kTCSAppInstanceActive));
            mMessageReceiverRegistered = true;
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        if (mMessageReceiverRegistered) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        }
        super.onDestroy();
    }

    private void syncNotifications() {
        notificationsRecyclerView.setHasFixedSize(false);
        WrapContentLinearLayoutManager llm =
                new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,
                        false);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        notificationsRecyclerView.setLayoutManager(llm);
        adapter = new NotificationsAdapter(menuItemClickListener, getContext());
        adapter.setRemovedNotificationListener(this);
        notificationsRecyclerView.setAdapter(adapter);

        if (!isRefreshing)
            progressContainer.setVisibility(View.VISIBLE);
        TCSAppInstance.getInstance().getNotifyActionsWithCompletion(new TCSSuccessDelegate() {
            @Override
            public void done(boolean success) {
                reloadNotifications();
                if (isRefreshing) {
                    isRefreshing = false;
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    progressContainer.setVisibility(View.GONE);
                }
            }
        });

        fetchTwitterTimeLine();
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

        adapter.setNotificationsList(notificationsList);
        adapter.notifyDataSetChanged();

        if (notificationsList.size() > 0) {
            notificationsRecyclerView.setVisibility(View.VISIBLE);
            noNotifications.setVisibility(View.GONE);
        } else {
            notificationsRecyclerView.setVisibility(View.GONE);
            noNotifications.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void notificationsRemoved(TCSAppAction action) {
        if (removedNotifications == null) {
            removedNotifications = new ArrayList<>();
        }

        removedNotifications.add(action);
    }

    public void setMenuItemListener(MenuItemClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {

    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        syncNotifications();
    }
}
