package com.sparkcompass.tobaccodock.notifications;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sparkcompass.tobaccodock.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mind-p6 on 8/31/15.
 */
public class NotificationViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.notification_icon_image)
    public ImageView notificationIcon;
    @Bind(R.id.notification_title)
    public TextView notificationTitle;
    @Bind(R.id.notification_description)
    public TextView notificationDescription;
    @Bind(R.id.see_notification_button)
    public ImageView seeNotification;
    @Bind(R.id.discard_notification)
    public ImageView discardNotification;
    @Bind(R.id.background)
    public RelativeLayout backgroundContainer;
    @Bind(R.id.notification_date)
    public TextView notificationDate;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
