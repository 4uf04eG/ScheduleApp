package com.ilya.scheduleapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.containers.Week;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private final Date firstDayOfWeek;
    private static Week schedule;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;

        ViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.schedule_item_title);
        }
    }

    public ScheduleAdapter(Context context) {
        setHasStableIds(true);
        schedule = new Week();
        this.context = context;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        firstDayOfWeek = c.getTime();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.schedule_item, viewGroup, false));
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        LinearLayout root = (LinearLayout) viewHolder.itemView;

        if (root.getChildCount() > 1) return;

        String curDayOfWeek = getDayOfWeekName(position);
        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
        Calendar cal = Calendar.getInstance();
        int realDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        cal.setTime(firstDayOfWeek);
        cal.add(Calendar.DATE, position);

        String printStr = String.format("%s | %s", curDayOfWeek, format.format(cal.getTime()));
        viewHolder.title.setText(printStr);

        if(cal.get(Calendar.DAY_OF_WEEK) == realDayOfWeek)
            viewHolder.itemView.setBackgroundResource(R.color.colorPrimary);


        LayoutInflater inflater = (LayoutInflater) viewHolder.itemView.getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (String period : schedule.get(position)) {
            TextView button = (TextView) inflater.inflate(R.layout.schedule_item_child, null);
            button.setText(period);
            root.addView(button);
        }
    }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public int getItemViewType(int position) { return position; }

    @Override
    public int getItemCount() { return schedule.size(); }

    public void refreshData(Week newData) {
        schedule = newData;
        notifyDataSetChanged();
    }

    private String getDayOfWeekName(int index) {
        switch (index) {
            case 0:
                return context.getString(R.string.monday);
            case 1:
                return context.getString(R.string.tuesday);
            case 2:
                return context.getString(R.string.wednesday);
            case 3:
                return context.getString(R.string.thursday);
            case 4:
                return context.getString(R.string.friday);
            case 5:
                return context.getString(R.string.saturday);
            case 6:
                return context.getString(R.string.sunday);
            default:
                return null;
        }
    }
}
