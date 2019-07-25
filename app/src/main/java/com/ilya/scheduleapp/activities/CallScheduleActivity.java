package com.ilya.scheduleapp.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.ilya.scheduleapp.R;
import com.ilya.scheduleapp.helpers.AppStyleHelper;

public class CallScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = AppStyleHelper.getDefaultTheme(this);

        if (theme != Integer.MIN_VALUE)
            setTheme(theme);

        setContentView(R.layout.activity_call_schedule);
        setTitle(R.string.nav_call_schedule);

        AppStyleHelper.setDefaultBackground(this, getSupportActionBar());
    }
}
