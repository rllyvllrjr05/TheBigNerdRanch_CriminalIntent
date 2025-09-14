package com.example.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.sql.Time;
import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME = "com.example.criminalintent.time";
    private static final String ARG_TIME = "time";

    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Time time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_time, container, false);

        Time time = (Time) getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        mTimePicker = v.findViewById(R.id.dialog_time_picker_widget);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minute);

        Button okButton = v.findViewById(R.id.button_ok);
        okButton.setOnClickListener(view -> {
            int h = mTimePicker.getHour();
            int m = mTimePicker.getMinute();

            Time resultTime = new Time(h, m, 0);
            sendResult(Activity.RESULT_OK, resultTime);
            dismiss();
        });

        return v;
    }

    private void sendResult(int resultCode, Time time) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);

        if (getTargetFragment() != null) {
            // Tablet case: dialog
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        } else {
            // Phone case: hosted in activity
            getActivity().setResult(resultCode, intent);
            getActivity().finish();
        }
    }
}
