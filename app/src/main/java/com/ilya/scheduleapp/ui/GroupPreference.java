package com.ilya.scheduleapp.ui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;

import androidx.preference.Preference;

import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.activities.AllGroupsActivity;
import com.ilya.scheduleapp.activities.MainActivity;
import com.ilya.scheduleapp.helpers.StorageHelper;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class GroupPreference extends Preference {
    private static final String GROUP_NAME = "group_name";

    public GroupPreference(Context context) {
        super(context);
        setIcon(R.drawable.more_ic_change_group_24dp);
        setKey(GROUP_NAME);
    }

    public GroupPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setIcon(R.drawable.more_ic_change_group_24dp);
        setKey(GROUP_NAME);
    }

    public GroupPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setIcon(R.drawable.more_ic_change_group_24dp);
        setKey(GROUP_NAME);
    }


    @Override
    protected void onClick() {
        super.onClick();
        Intent intent = new Intent(getContext(), AllGroupsActivity.class);
        MainActivity activity = (MainActivity) getContext();

        intent.putExtra("change_group", true);
        startActivityForResult(activity, intent, 1, null);
    }



    @Override
    public CharSequence getSummary() {
        String summary = StorageHelper.findStringInShared(getContext(), GROUP_NAME);

        if (summary != null && !summary.equals("")) return summary;

        return getContext().getResources().getString(R.string.preference_not_specified);
    }
}
