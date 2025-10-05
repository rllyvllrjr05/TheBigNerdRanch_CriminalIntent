package com.example.criminalintent;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import java.util.Date;

public class TimePickerActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Date date = (Date) getIntent().getSerializableExtra(TimePickerFragment.EXTRA_TIME);
        return TimePickerFragment.newInstance(date);
    }

    public static Intent newIntent(Context context, Date time) {
        Intent intent = new Intent(context, TimePickerActivity.class);
        intent.putExtra(TimePickerFragment.EXTRA_TIME, time);
        return intent;
    }
}
