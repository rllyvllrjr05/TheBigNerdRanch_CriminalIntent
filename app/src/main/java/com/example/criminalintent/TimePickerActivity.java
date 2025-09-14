package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.sql.Time;

public class TimePickerActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Time date = (Time) getIntent().getSerializableExtra(TimePickerFragment.EXTRA_TIME);
        return DatePickerFragment.newInstance(date);
    }

    public static Intent newIntent(Context context, Time time) {
        Intent intent = new Intent(context, DatePickerActivity.class);
        intent.putExtra(TimePickerFragment.EXTRA_TIME, time);
        return intent;
    }
}
