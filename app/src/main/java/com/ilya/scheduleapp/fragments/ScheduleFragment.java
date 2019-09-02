package com.ilya.scheduleapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.activities.MainActivity;
import com.ilya.scheduleapp.adapters.SchedulePagerAdapter;
import com.ilya.scheduleapp.containers.ScheduleContainer;
import com.ilya.scheduleapp.helpers.AppStyleHelper;
import com.ilya.scheduleapp.helpers.StorageHelper;
import com.ilya.scheduleapp.listeners.ScheduleAsyncTaskListener;
import com.ilya.scheduleapp.parsers.ScheduleParser;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class ScheduleFragment extends Fragment implements ScheduleAsyncTaskListener {
    private static final String NUM_OF_WEEK = "current_week";
    private static final String WEEK_COUNT = "week_count";
    private static final String ARG_SELECTED_ITEM = "view_pager_previous_item";

    private SwipeRefreshLayout refreshLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppStyleHelper.restoreTabLayoutStyle(requireActivity());

        SchedulePagerAdapter schedulePagerAdapter = new SchedulePagerAdapter(
                requireContext(), getChildFragmentManager(), 0);
        viewPager = requireView().findViewById(R.id.view_pager);
        viewPager.setAdapter(schedulePagerAdapter);

        if (savedInstanceState != null) {
            viewPager.setCurrentItem(savedInstanceState.getInt(ARG_SELECTED_ITEM));
        } else {
            selectTodayViewPagerItem(viewPager, schedulePagerAdapter);
        }

        TabLayout tabs = requireView().findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        refreshLayout = view.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(this::tryLoadSchedule);

        view.findViewById(R.id.retry_refresh_button).setOnClickListener(v -> onRefreshButtonClick());

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs) {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (!refreshLayout.isRefreshing()) {
                    refreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
                }
            }
        });

        ScheduleContainer schedule = StorageHelper.findScheduleInShared(requireContext());

        if (schedule == null) {
            ((MainActivity) requireActivity()).changeToolbarSubtitle(false);
            tryLoadSchedule();
        } else {
            addScheduleToView(schedule);
            finishRefreshing();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_SELECTED_ITEM, viewPager.getCurrentItem());
    }

    @Override
    public void addScheduleToView(ScheduleContainer schedule) {
        if (!isAdded()) return;

        ViewPager view = requireActivity().findViewById(R.id.view_pager);
        SchedulePagerAdapter adapter = (SchedulePagerAdapter) view.getAdapter();
        int numOfWeek = StorageHelper.findIntInShared(getContext(), NUM_OF_WEEK);

        if (numOfWeek < schedule.size() && adapter != null && numOfWeek != Integer.MIN_VALUE) {
            adapter.refreshData(schedule.get(numOfWeek));
        }

        if (isVisible()) {
            ((MainActivity) requireActivity()).changeToolbarSubtitle(true);
        }
    }

    @Override
    public void storeSchedule(Context context, ScheduleContainer scheduleContainer) {
        StorageHelper.addScheduleToShared(context, scheduleContainer);
        StorageHelper.addToShared(context, WEEK_COUNT, scheduleContainer.size());
    }

    @Override
    public void showErrorToast(Context context) {
        Toast.makeText(context,
                context.getString(R.string.connection_error), Toast.LENGTH_LONG).show();

        refreshLayout.setVisibility(View.GONE);

        if (!isAdded()) return;

        requireActivity().findViewById(R.id.all_progress_bar).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.tabs).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.retry_refresh_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void finishRefreshing() {
        refreshLayout.setRefreshing(false);
        refreshLayout.setVisibility(View.VISIBLE);

        if (!isAdded()) return;

        requireActivity().findViewById(R.id.tabs).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.all_progress_bar).setVisibility(View.GONE);
    }

    private void tryLoadSchedule() {
        final ScheduleAsyncTaskListener listener = this;

        new Thread(() -> {
            try {
                new ScheduleParser(listener).execute().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void onRefreshButtonClick() {
        requireActivity().findViewById(R.id.all_progress_bar).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.retry_refresh_button).setVisibility(View.GONE);
        tryLoadSchedule();
    }

    private void selectTodayViewPagerItem(ViewPager viewPager, SchedulePagerAdapter adapter) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 2;

        if (dayOfWeek >= 0 && dayOfWeek < adapter.getCount()) {
            viewPager.setCurrentItem(dayOfWeek);
        }
    }
}
