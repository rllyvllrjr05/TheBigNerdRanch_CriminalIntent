package com.example.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

//import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {
    public static final String EXTRA_DATE = "com.example.criminalintent.date";
    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;

    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minutes = calendar.get(Calendar.MINUTE);

        View v = inflater.inflate(R.layout.dialog_date, container, false);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        Button okButton = (Button) v.findViewById(R.id.button_ok);
        okButton.setOnClickListener(view -> {
            int y = mDatePicker.getYear();
            int m = mDatePicker.getMonth();
            int d = mDatePicker.getDayOfMonth();
            Date resultDate = new GregorianCalendar(y, m, d, hour, minutes).getTime();
            sendResult(Activity.RESULT_OK, resultDate);
            dismiss();
        });

        return v;
    }

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() != null) {
            // Case: Shown as a dialog on tablet
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, date);
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
        } else {
            // Case: Shown as an activity on phone
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DATE, date);
            getActivity().setResult(resultCode, intent);
            getActivity().finish();
        }
    }
}
