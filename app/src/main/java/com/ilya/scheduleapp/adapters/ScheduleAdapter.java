package com.ilya.scheduleapp.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.containers.Week;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private static Week schedule;

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;

        ViewHolder(final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.schedule_item_title);
        }
    }

    public ScheduleAdapter() {
        setHasStableIds(true);
        schedule = new Week();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.schedule_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.title.setText("Люда - хуй");

        LinearLayout root = (LinearLayout) viewHolder.itemView;

        if (root.getChildCount() > 1) return;

        LayoutInflater inflater = (LayoutInflater) viewHolder.itemView.getContext().
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (String period : schedule.get(position)) {
            Button button = (Button) inflater.inflate(R.layout.schedule_item_child, null);
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
}
