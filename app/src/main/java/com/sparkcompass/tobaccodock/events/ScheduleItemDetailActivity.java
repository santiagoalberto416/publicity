package com.sparkcompass.tobaccodock.events;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pacificfjord.pfapi.TCSAppInstance;
import com.pacificfjord.pfapi.TCSScheduleItem;
import com.pacificfjord.pfapi.views.TCSSkin;
import com.sparkcompass.tobaccodock.R;
import com.squareup.picasso.Picasso;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Aaron Vega on 3/24/15.
 */
public class ScheduleItemDetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EVENT_ITEM = "event_item";

    private TCSScheduleItem eventItem;
    @Bind(R.id.event_image)
    protected ImageView eventImage;
    @Bind(R.id.btn_map)
    protected FloatingActionButton btnMap;
    @Bind(R.id.details_toolbar)
    protected Toolbar detailsToolbar;
    @Bind(R.id.date)
    protected TextView eventDate;
    @Bind(R.id.description)
    protected TextView eventDescription;
    @Bind(R.id.location)
    protected TextView eventLocation;
    @Bind(R.id.progress_container)
    protected RelativeLayout progressContainer;
    @Bind(R.id.duration)
    protected TextView duration;
    private ScheduleItemMapDialogFragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_item_detail);

        ButterKnife.bind(this);

        setSupportActionBar(detailsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTranslucentBar(true);
        setToolbarElevation(false);

        detailsToolbar.setNavigationOnClickListener(this);

        if (savedInstanceState != null) {
            eventItem = savedInstanceState.getParcelable(EVENT_ITEM);
        } else if (getIntent().getExtras() != null) {
            eventItem = getIntent().getExtras().getParcelable(EVENT_ITEM);
        } else {
            onBackPressed();
        }

        getSupportActionBar().setTitle(eventItem.getName());

        customizeViews(TCSAppInstance.getInstance().getSelectedSkin());

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (mapFragment == null) {
                    mapFragment = ScheduleItemMapDialogFragment.newInstance(eventItem);
                }
                mapFragment.show(fragmentManager, "ScheduleItemMapDialogFragment");
            }
        });

        fillEventInformation();


    }

    private void setTranslucentBar(boolean enabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        Window w = getWindow();
        int translucentStatusFlag = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        if (enabled) {
            w.setFlags(translucentStatusFlag, translucentStatusFlag);
        } else {
            w.clearFlags(translucentStatusFlag);
        }
    }

    private void setToolbarElevation(boolean enabled) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (enabled) {
            getSupportActionBar().setElevation(4);
        } else {
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(EVENT_ITEM, eventItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
        super.onBackPressed();
    }


    public void fillEventInformation() {

        Picasso.with(this).load(eventItem.getUrl()).into(eventImage);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMM dd yyyy, h:mm a");
        String dateString = formatter.print(eventItem.getStartDate());
        eventDate.setText(dateString);
        eventDescription.setText(eventItem.getDescription());
        if (eventItem.getFacility() != null && eventItem.getFacility().getName() != null) {
            eventLocation.setText(eventItem.getFacility().getName());
        } else {
            eventLocation.setText("Undefined location");
        }


        String timeLapse = "Undefined";
        Days days = Days.daysBetween(eventItem.getStartDate(), eventItem.getEndDate());
        Hours hours = Hours.hoursBetween(eventItem.getStartDate(), eventItem.getEndDate());
        if (days.getDays() > 0) {
            timeLapse = days.getDays() == 1 ? days.getDays() + " day" : days.getDays() + " days";
        } else if (hours.getHours() > 0) {
            timeLapse = hours.getHours() == 1 ? hours.getHours() + " hr" : hours.getHours() + " hrs";
        }

        duration.setText(timeLapse);
    }

    public void customizeViews(TCSSkin skinTemplate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(skinTemplate.getPrimaryColorDark()));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                onBackPressed();
                break;
        }
    }
}
