package com.sparkcompass.tobaccodock.utils;

import android.content.Context;
import android.widget.ImageView;

import com.sparkcompass.tobaccodock.R;
import com.squareup.picasso.Picasso;

/**
 * Created by mind-p6 on 8/29/15.
 */
public class ImageUtils {


    public static void replaceImage(String url, ImageView imageView, Context context) {
        int drawableResource = R.drawable.ecos_logo;
        if (url.startsWith("file://")) {
            url = url.replace("file://", "").replace(".png", "").replace("-", "_").toLowerCase();
            int id = context.getResources().getIdentifier(url, "drawable", context.getPackageName());
            if (id > 0)
                imageView.setImageResource(id);
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            Picasso.with(context).load(url).into(imageView);
        }
    }

    public static void replaceNotificationImage(String url, ImageView imageView, Context context){
        int drawableResource = R.drawable.notification_icon;
        if (url.startsWith("file://")) {
            url = url.replace("file://", "").replace(".png", "").replace("-", "_").toLowerCase();
            int id = context.getResources().getIdentifier(url, "drawable", context.getPackageName());
            if (id > 0)
                imageView.setImageResource(id);
            else
                imageView.setImageResource(drawableResource);
        } else if (url.startsWith("http://") || url.startsWith("https://")) {
            Picasso.with(context).load(url).into(imageView);
        } else {
            imageView.setImageResource(drawableResource);
        }
    }
}
