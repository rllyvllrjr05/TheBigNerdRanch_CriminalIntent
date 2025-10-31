package com.example.criminalintent;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String CRIME_FRAGMENT = "CrimeFragment";

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_IMAGE = "DialogImage";

    public static final String EXTRA_DATE = "date";
    public static final String EXTRA_TIME = "time";


    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;
    public static final int ACTIVITY_REQUEST_DATE = 3;
    public static final int ACTIVITY_REQUEST_TIME = 4;
    private static final int REQUEST_CONTACT = 5;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 6;

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_delete, menu); // make sure delete_crime is in this menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_crime) {
            CrimeLab.get(getActivity()).deleteCrime(mCrime);
            Toast.makeText(getActivity(), R.string.toast_delete_crime, Toast.LENGTH_SHORT)
                    .show();
            getActivity(); // close detail screen
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        setHasOptionsMenu(true);
        mTitleField = v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                    // This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mDateButton = v.findViewById(R.id.crime_date);
        final Date currentDate = mCrime.getDate();

//        String formattedDate = DateFormat.format("EEEE, MMM dd, yyyy", currentDate).toString();
//        mDateButton.setText(formattedDate);
//
        mTimeButton = v.findViewById(R.id.crime_time);
//        String formattedTime = DateFormat.format("HH:mm", currentDate).toString();
//        mTimeButton.setText(formattedTime);

        java.text.DateFormat formattedDate =
                java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, Locale.getDefault());
        java.text.DateFormat formattedTime =
                java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT, Locale.getDefault());

        mDateButton.setText(formattedDate.format(currentDate));
        mTimeButton.setText(formattedTime.format(currentDate));


        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isTablet(getContext())) {
                    // Show as dialog
                    FragmentManager manager = getFragmentManager();
                    DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                    dialog.show(manager, DIALOG_DATE);
                } else {
                    // Show as full activity
                    Intent intent = DatePickerActivity.newIntent(getActivity(), mCrime.getDate());
                    intent.putExtra(EXTRA_DATE, mCrime.getDate());
                    startActivityForResult(intent, ACTIVITY_REQUEST_DATE);
                }
            }
        });


        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTablet(getContext())) {
                    FragmentManager manager = getFragmentManager();
                    TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                    dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                    dialog.show(manager, DIALOG_TIME);
                } else {
                    // Screen is smaller - display dialog as full screen activity
                    Intent intent = TimePickerActivity.newIntent(getContext(), mCrime.getDate());
                    intent.putExtra(EXTRA_TIME, mCrime.getDate());
                    startActivityForResult(intent, ACTIVITY_REQUEST_TIME);
                }
            }
        });

        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { startActivityForResult(pickContact, REQUEST_CONTACT); }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use intent builder to create a send action
                // that opens a populated email
                ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText(getCrimeReport())
                        .setSubject(getString(R.string.crime_report_subject))
                        .setChooserTitle(getString(R.string.send_report))
                        .startChooser();
            }
        });


        mCallButton = v.findViewById(R.id.crime_call_suspect);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCrime.getSuspect() == null) {
                    Toast.makeText(getActivity(), getString(R.string.crime_report_no_suspect), Toast.LENGTH_SHORT).show();
                    return;
                }
                checkReadContactsPermission();
            }
        });


        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(true);
        }

        mPhotoButton = v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(true);


        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.criminalintent.fileprovider",
                        mPhotoFile);

                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = v.findViewById(R.id.crime_photo);
        mPhotoView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Now width/height are available
                        updatePhotoView();

                        // Remove the listener so it doesn’t keep firing
                        mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        mPhotoView.setOnClickListener(view -> {
            if (mPhotoFile != null && mPhotoFile.exists()) {
                PhotoFragment.newInstance(mPhotoFile)
                        .show(getParentFragmentManager(), "PhotoDialog");
            }
        });
        updatePhotoView();

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_DATE || requestCode == REQUEST_TIME ||
                requestCode == ACTIVITY_REQUEST_DATE || requestCode == ACTIVITY_REQUEST_TIME) {
            final Date date;
            if (requestCode == REQUEST_DATE || requestCode == ACTIVITY_REQUEST_DATE) {
                date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            } else {
                date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            }

            mCrime.setDate(date);
            mDateButton.setText(DateFormat.format("EEEE, MMM dd, yyyy", date));
            mTimeButton.setText(DateFormat.format("HH:mm", date));
            updateCrime();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }
                // Pull out the first column of the first row of data -
                // that is your suspect's name
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);
            } finally {
                    c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.criminalintent.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updateCrime();
            updatePhotoView();
        }
    }

    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void checkReadContactsPermission() {// Here, thisActivity is the current activity
        Log.i(CRIME_FRAGMENT, "checkReadContactsPermission: start");
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.i(CRIME_FRAGMENT, "checkReadContactsPermission: called");
            callSuspect();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                Toast.makeText(getActivity(), "Dial with phone successful", Toast.LENGTH_SHORT).show();
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    callSuspect();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void callSuspect() {
        Cursor c = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{String.valueOf(mCrime.getSuspectId())},
                null
        );
        // Dial suspect if one exists

        try {
            if (c == null || c.getCount() == 0) {
                Log.i(CRIME_FRAGMENT, "callSuspect cursor null or zero");
                return;
            }
            c.moveToFirst();

            String phoneNumber = c.getString(0);

            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
            startActivity(intent);

        } finally {
            c.close();
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            return;
        }

        int width = mPhotoView.getWidth();
        int height = mPhotoView.getHeight();

        if (width == 0 || height == 0) {
            // Dimensions not ready yet
            return;
        }

        Bitmap bitmap = PictureUtils.getScaledBitmap(
                mPhotoFile.getPath(), width, height);
        mPhotoView.setImageBitmap(bitmap);
    }


    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,
                mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}

