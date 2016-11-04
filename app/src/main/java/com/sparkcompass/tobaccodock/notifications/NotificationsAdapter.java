package com.sparkcompass.tobaccodock.notifications;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pacificfjord.pfapi.TCSAppAction;
import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.utilites.TCSNotificationService;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.home.MenuItemClickListener;
import com.sparkcompass.tobaccodock.utils.ImageUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import twitter4j.Status;

/**
 * Created by mind-p6 on 8/31/15.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationViewHolder> {

    private List<Object> notificationList;
    private MenuItemClickListener listener;
    private Context context;
    private RemovedNotificationListener removedNotificationListener;


    public void setRemovedNotificationListener(RemovedNotificationListener removedNotificationListener) {
        this.removedNotificationListener = removedNotificationListener;
    }

    public NotificationsAdapter(MenuItemClickListener listener, Context context) {
        this.context = context;
        this.listener = listener;
    }

    public void setNotificationsList(List<Object> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false);

        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        if (notificationList == null) {
            return;
        }

        if (notificationList.get(position) != null) {
            if (notificationList.get(position) instanceof Status) {
                Status item = (Status) notificationList.get(position);
                holder.notificationTitle.setVisibility(View.GONE);
                holder.notificationDescription.setText(item.getText());
                holder.notificationIcon.setImageResource(R.drawable.icon_twitter);
            } else if (notificationList.get(position) instanceof TCSAppAction) {
                TCSAppAction item = (TCSAppAction) notificationList.get(position);
                setSkinForView(holder);

                holder.notificationTitle.setVisibility(View.VISIBLE);
                holder.notificationTitle.setText(
                        item.getParamValueForKey(TCSNotificationService.KEYNOTIFICATIONTITLE));
                holder.notificationDescription.setText(
                        item.getParamValueForKey(TCSNotificationService.KEYNOTIFICATIONMESSAGE));

                Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
                currentCalender.setTime(item.getCreationTime());
                SimpleDateFormat dayFormat = new SimpleDateFormat("MMM dd HH:mm", Locale.US);
                String weekDay = dayFormat.format(currentCalender.getTime());
                holder.notificationDate.setText(weekDay);

                ImageUtils.replaceNotificationImage(item.getParamValueForKey("NotificationIconUrl"),
                        holder.notificationIcon, context);

                if (item.getActionType().equals(TCSAppAction.TCSAppActionType.ATYPE_MESSAGE))
                    holder.seeNotification.setVisibility(View.INVISIBLE);
                else
                    holder.seeNotification.setVisibility(View.VISIBLE);

                final int idx = position;
                holder.seeNotification.setColorFilter(Color.parseColor(TCSAppInstance.getInstance().getSelectedSkin().getColor()));
                holder.seeNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (notificationList.get(idx) instanceof TCSAppAction) {
                            TCSAppAction action = (TCSAppAction) notificationList.get(idx);
                            if (listener != null)
                                listener.itemClickedWithAction(action);
                        }
                    }
                });

                holder.discardNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (notificationList.get(idx) instanceof TCSAppAction) {
                            TCSAppAction action = (TCSAppAction) notificationList.get(idx);
                            TCSAppInstance.getInstance().removeAppAction(action);
                            if(removedNotificationListener != null) {
                                removedNotificationListener.notificationsRemoved(action);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return notificationList != null ? notificationList.size() : 0;
    }

    private void setSkinForView(NotificationViewHolder holder) {
        TCSSkin appSkin = TCSAppInstance.getInstance().getSelectedSkin();
        if (appSkin.getNotification().containsKey(TCSSkin.BG_COLOR) && !appSkin.getNotification().get(TCSSkin.BG_COLOR).isEmpty()) {
            holder.backgroundContainer.setBackgroundColor(Color.parseColor(appSkin.getNotification().get(TCSSkin.BG_COLOR)));
        } else {
            holder.backgroundContainer.setBackgroundColor(Color.parseColor(appSkin.getPrimaryColor()));
        }

        if (appSkin.getNotification().containsKey(TCSSkin.TEXT_COLOR) && !appSkin.getNotification().get(TCSSkin.TEXT_COLOR).isEmpty()) {
            holder.notificationTitle
                    .setTextColor(Color.parseColor(appSkin.getNotification().get(TCSSkin.TEXT_COLOR)));
            holder.notificationDescription.setTextColor(Color.parseColor(appSkin.getNotification().get(TCSSkin.TEXT_COLOR)));
//            holder.seeNotification.setColorFilter(Color.parseColor(appSkin.getNotification().get(TCSSkin.TEXT_COLOR)));
//            holder.discardNotification.setColorFilter(Color.parseColor(appSkin.getNotification().get(TCSSkin.TEXT_COLOR)));
        }

        holder.notificationIcon.setColorFilter(Color.parseColor(appSkin.getColor()));
    }

    public interface RemovedNotificationListener {
        void notificationsRemoved(TCSAppAction action);
    }
}
