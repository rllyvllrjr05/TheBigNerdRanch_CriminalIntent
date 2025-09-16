package com.example.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_TIME = "com.example.criminalintent.time";
    private static final String ARG_TIME = "time";

    private TimePicker mTimePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_time, container, false);

        Date time = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();

        if (time != null) {
            calendar.setTime(time);
        } else {
            // fallback to "now"
            time = new Date(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0);
            calendar.setTime(time);
        }


        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker_widget);
        mTimePicker.setIs24HourView(true);

        if (Build.VERSION.SDK_INT >= 26) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }

        Button okButton = v.findViewById(R.id.button_ok);
        okButton.setOnClickListener(view -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            int h;
            int m;

            if (Build.VERSION.SDK_INT >= 26) {
                h = mTimePicker.getHour();
                m = mTimePicker.getMinute();
            } else {
                h = mTimePicker.getCurrentHour();
                m = mTimePicker.getCurrentMinute();
            }

            Date resultTime = new GregorianCalendar(year, day, month ,h, m).getTime();
            sendResult(Activity.RESULT_OK, resultTime);
        });

        return v;
    }

    public static TimePickerFragment newInstance(Date time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void sendResult(int resultCode, Date time) {
        if (getTargetFragment() != null) {

            Intent intent = new Intent();
            intent.putExtra(EXTRA_TIME, time);
            getTargetFragment()
                    .onActivityResult(getTargetRequestCode(), resultCode, intent);
            dismiss();
            return;
        }

        // Otherwise fragment was called from an activity
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, time);
        getActivity().setResult(resultCode, intent);
        getActivity().finish();
    }
}
