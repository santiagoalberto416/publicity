package com.sparkcompass.tobaccodock.events;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSScheduleItem;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.squareup.picasso.Picasso;

import net.danlew.android.joda.DateUtils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aaron Vega on 3/23/15.
 */
public class ScheduleItemsAdapter
        extends RecyclerView.Adapter<ScheduleItemsAdapter.ScheduleItemViewHolder> implements View.OnClickListener {

    private Context context;
    private List<TCSScheduleItem> scheduleItems;
    protected ItemClickListener itemClickListener;

    public ScheduleItemsAdapter(Context context) {
        this.context = context;
    }

    public ScheduleItemsAdapter(Context context, List<TCSScheduleItem> scheduleItems) {
        this.context = context;
        this.scheduleItems = scheduleItems;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setScheduleItems(List<TCSScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    @Override
    public ScheduleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item_row, parent, false);

        itemView.setOnClickListener(this);

        return new ScheduleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ScheduleItemViewHolder holder, int position) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd yyyy, h:mm a");
        TCSScheduleItem item = scheduleItems.get(position);
        holder.itemTitle.setText(item.getName());
        holder.itemTitle.setTypeface(null, Typeface.BOLD);
        String dateStr = formatter.print(item.getStartDate());
        if (DateUtils.isToday(item.getStartDate())) {
            dateStr = "Today";
        }

        if(item.getFacility() != null && item.getFacility().getName() != null){
            holder.itemLocation.setText(item.getFacility().getName());
        } else {
            holder.itemLocation.setText("Undefined location");
        }

        holder.itemDate.setText(dateStr);
        holder.itemDuration.setText(item.getDuration());
        if (item.getUrl() != null && !item.getUrl().isEmpty()) {
            Picasso.with(context).load(item.getUrl()).into(holder.itemImage);
//            ImageLoader.getInstance().displayImage(item.getUrl(), holder.itemImage);
        }
    }

    @Override
    public int getItemCount() {
        return scheduleItems != null ? scheduleItems.size() : 0;
    }

    private TCSScheduleItem getItem(int position) {
        return scheduleItems.get(position);
    }

    @Override
    public void onClick(View view) {

    }

    public class ScheduleItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.item_title)
        protected TextView itemTitle;
        @Bind(R.id.item_location)
        protected TextView itemLocation;
        @Bind(R.id.item_date)
        protected TextView itemDate;
        @Bind(R.id.item_duration)
        protected TextView itemDuration;
        @Bind(R.id.item_image)
        protected ImageView itemImage;

        public ScheduleItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            TCSSkin appSkin = TCSAppInstance.getInstance().getSelectedSkin();
            itemView.setBackgroundColor(Color.parseColor(appSkin.getBgColor()));
            itemView.getBackground().setAlpha(Integer.parseInt(appSkin.getOpacity()));
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.itemClicked(view, getItem(getPosition()));
            }
        }
    }

    public interface ItemClickListener {
        void itemClicked(View view, TCSScheduleItem item);
    }
}
