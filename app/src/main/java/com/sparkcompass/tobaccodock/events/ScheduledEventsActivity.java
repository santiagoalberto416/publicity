package com.sparkcompass.tobaccodock.events;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSScheduleItem;
import com.pacificfjord.pfapi.TCSScheduledEventsAPI;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.sparkcompass.tobaccodock.common.TCSActivity;


import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aaron Vega on 3/23/15.
 */
public class ScheduledEventsActivity extends TCSActivity implements ScheduleItemsAdapter.ItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView scheduledEventsView;
    private ScheduleItemsAdapter adapter;
    private List<TCSScheduleItem> items;
    @Bind(R.id.header_title)
    protected TextView titleView;
    @Bind(R.id.progress_container)
    protected RelativeLayout progressContainer;
    @Bind(R.id.missing_content)
    protected RelativeLayout missingContent;
    @Bind(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.sad_face)
    protected ImageView sadFace;

    private boolean isRefreshing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_scheduled_events);
        ButterKnife.bind(this);

        customizeViews(TCSAppInstance.getInstance().getSelectedSkin());

        scheduledEventsView = (RecyclerView) findViewById(R.id.scheduled_events);
        scheduledEventsView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        scheduledEventsView.setLayoutManager(llm);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new ScheduleItemsAdapter(this);
        adapter.setItemClickListener(this);
        scheduledEventsView.setAdapter(adapter);

        scheduledEventsView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        ImageView backButton = (ImageView) findViewById(R.id.back_button);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        syncScheduleItems();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        super.onBackPressed();
    }

    private List<TCSScheduleItem> getItemsByMonth(DateTime onDate) {
        List<TCSScheduleItem> filteredItems = new ArrayList<TCSScheduleItem>();
        for (TCSScheduleItem item : items) {
            if (item.getStartDate().getMonthOfYear() == onDate.getMonthOfYear()) {
                filteredItems.add(item);
            }
        }

        return filteredItems;
    }

    private List<TCSScheduleItem> getItemsFromNowOn() {
        List<TCSScheduleItem> filteredItems = new ArrayList<>();
        LocalDate today = new DateTime().toLocalDate();
        for (TCSScheduleItem item : items) {
            if (item.getEndDate().isAfter(today.toDateTimeAtStartOfDay())) {
                filteredItems.add(item);
            }
        }

        Collections.sort(filteredItems, new TCSScheduleItem.TCSScheduleItemStartDateComparator());

        return filteredItems;
    }

    private List<TCSScheduleItem> findByDate(final DateTime onDate) {
        List<TCSScheduleItem> filteredItems = new ArrayList<TCSScheduleItem>();
        for (TCSScheduleItem item : items) {
            if (Days.daysBetween(item.getStartDate(), onDate).getDays() == 0) {
                filteredItems.add(item);
            }
        }

        Collections.sort(filteredItems, new TCSScheduleItem.TCSScheduleItemStartDateComparator());

        return filteredItems;
    }

    private void syncScheduleItems() {
        if (!isRefreshing)
            progressContainer.setVisibility(View.VISIBLE);
        TCSScheduledEventsAPI.getScheduledEventItems("", false, new TCSScheduledEventsAPI.TCSScheduleItemsSuccesDelegate() {
            @Override
            public void done(boolean success, List<TCSScheduleItem> scheduleItems) {
                if (success) {
                    items = new ArrayList<>();
                    items.addAll(scheduleItems);
                    List<TCSScheduleItem> filteredEvents = getItemsFromNowOn();
                    if (filteredEvents.size() > 0) {
                        adapter.setScheduleItems(filteredEvents);
                        adapter.notifyDataSetChanged();
                    } else {
                        missingContent.setVisibility(View.VISIBLE);
                    }

                    if (isRefreshing) {
                        isRefreshing = false;
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        progressContainer.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public void itemClicked(View view, final TCSScheduleItem item) {
        Intent i = new Intent(this, ScheduleItemDetailActivity.class);
        i.putExtra(ScheduleItemDetailActivity.EVENT_ITEM, item);
        startActivity(i);
        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
    }

    @Override
    public void customizeViews(TCSSkin skinTemplate) {
        super.customizeViews(skinTemplate);
        titleView.setText("Events");
        sadFace.setColorFilter(Color.parseColor(skinTemplate.getColor()));
    }

    @Override
    public void onRefresh() {
        isRefreshing = true;
        syncScheduleItems();
    }
}
