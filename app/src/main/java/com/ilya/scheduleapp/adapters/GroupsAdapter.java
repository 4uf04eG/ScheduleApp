package com.ilya.scheduleapp.adapters;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ilya.scheduleapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    private static List<String> groups;

    public GroupsAdapter() {
        groups = new ArrayList<>();
    }

    public GroupsAdapter(List<String> groups) {
        GroupsAdapter.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_groups, parent, false));
        }

        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header_groups, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return Character.isDigit(groups.get(position).charAt(0)) ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if (groups == null) return;

        List<Integer> colors = generateHeaderColors();
        char firstChar;

        if (Character.isDigit(firstChar = groups.get(position).charAt(0))) {
            int index = Integer.parseInt(Character.toString(firstChar)) - 1;
            int curColor;

            if (index > 5) {
                curColor = colors.get(new Random().nextInt(5));
            } else {
                curColor = colors.get(index);
            }

            String year = holder.itemView.getResources().getString(R.string.groups_year);
            holder.parent.setText(String.format(Locale.ENGLISH, "%c %s", firstChar, year));
            holder.parent.setBackgroundColor(curColor);
            holder.arrow.getBackground().setColorFilter(curColor, PorterDuff.Mode.SRC_ATOP);
        } else {
            holder.parent.setText(groups.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    //TODO: Write tests to check if it really updates data. Yes, it should but i've never checked
    public void refreshData(List<String> newData) {
        if (newData != null) {
            groups = newData;
            notifyDataSetChanged();
        }
    }

    private List<Integer> generateHeaderColors() {
        return Arrays.asList(
                Color.parseColor("#e32322"),
                Color.parseColor("#f28e1c"),
                Color.parseColor("#454e99"),
                Color.parseColor("#008f5a"),
                Color.parseColor("#c5037d"),
                Color.parseColor("#8dbb25"));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView parent;
        private final View arrow;

        ViewHolder(final View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parent);
            arrow = itemView.findViewById(R.id.image_arrow);
        }
    }
}
