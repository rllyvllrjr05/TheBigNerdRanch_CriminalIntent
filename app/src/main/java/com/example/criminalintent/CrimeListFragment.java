package com.example.criminalintent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class CrimeListFragment extends Fragment {
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private int mUpdatedPosition = RecyclerView.NO_POSITION;

    private LinearLayout mEmptyView;
    private Button mAddCrimeButton;
    private Button mAddSeriousCrimeButton;
    private Callbacks mCallbacks;

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

//    public void onCrimeUpdated(Crime crime) {
//        CrimeLab crimeLab = CrimeLab.get(getActivity());
//        List<Crime> crimes = crimeLab.getCrimes();
//
//        if (mAdapter == null) {
//            mAdapter = new CrimeAdapter(crimes);
//            mCrimeRecyclerView.setAdapter(mAdapter);
//        } else {
//            mAdapter.notifyDataSetChanged();
//        }
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mEmptyView = (LinearLayout) view.findViewById(R.id.empty_view);
        mAddCrimeButton = (Button) view.findViewById(R.id.add_crime_button);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        mAddCrimeButton.setOnClickListener(v -> {
            Crime crime = new Crime();
            CrimeLab.get(getActivity()).addCrime(crime);
            Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
            startActivity(intent);
        });

        updateUI();

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                Crime crime = mAdapter.getCrimes().get(position);

                CrimeLab.get(getActivity()).deleteCrime(crime);

                updateUI();
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(mCrimeRecyclerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUpdatedPosition != RecyclerView.NO_POSITION) {
            Crime updatedCrime = CrimeLab.get(getActivity()).getCrimes().get(mUpdatedPosition);
            mAdapter.getCrimes().set(mUpdatedPosition, updatedCrime);
            mAdapter.notifyItemChanged(mUpdatedPosition);
            mUpdatedPosition = RecyclerView.NO_POSITION;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.add_new_crime) {
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            } else if (item.getItemId() == R.id.add_serious_crime){
                Crime crime = new Crime();
                crime.setRequiresPolice(true);
                CrimeLab.get(getActivity()).addCrime(crime);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            } else if (item.getItemId() == R.id.show_subtitle) {
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            } else return super.onOptionsItemSelected(item);
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
//        int crimeCount = crimeLab.getCrimes().size();
//        String subtitle = getString(R.string.subtitle_format, crimeCount);

        int crimeSize = crimeLab.getCrimes().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeSize, crimeSize);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.getCrimes().clear();
            mAdapter.getCrimes().addAll(crimes);
            mAdapter.notifyDataSetChanged();
        }

        if (crimes.isEmpty()) {
            mCrimeRecyclerView.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mCrimeRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }

        updateSubtitle();
    }




    private class CrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mTimeTextView;
        private Crime mCrime;
        private ImageView mSolvedImageView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mTimeTextView = (TextView) itemView.findViewById(R.id.crime_time);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);

            itemView.setOnClickListener(v -> {
                if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
                    // Phone layout: open full screen
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivity(intent);
                } else {
                    // Tablet layout: show fragment side-by-side
                    Fragment newDetail = CrimeFragment.newInstance(mCrime.getId());
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.detail_fragment_container, newDetail)
                            .commit();
                }
            });
        }

        public void bind(Crime crime, int position) {
            mCrime = crime;
            mUpdatedPosition = position;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());

//            String formattedDate = DateFormat.format("EEEE, MMM dd, yyyy", mCrime.getDate()).toString();
            java.text.DateFormat formattedDate =
                    java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, Locale.getDefault());
            mDateTextView.setText(formattedDate.format(mCrime.getDate()));

//            String formattedTime = DateFormat.format("HH:mm", mCrime.getDate()).toString();
            java.text.DateFormat formattedTime =
                    java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT, Locale.getDefault());
            mTimeTextView.setText(formattedTime.format(mCrime.getDate()));

            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);

            StringBuilder description = new StringBuilder();
            description.append("This crime is less serious, ")
                    .append(crime.getTitle())
                    .append(", ")
                    .append(formattedDate.format(crime.getDate()))
                    .append(", ")
                    .append(formattedTime.format(crime.getDate()));

            if (crime.isSolved()) {
                description.append(", case solved");
            } else {
                description.append(", case not solved");
            }

            itemView.setContentDescription(description.toString());

        }

        public void onClick(View view) {
            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_NORMAL = 0;
        private static final int VIEW_TYPE_SERIOUS = 1;
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);
            return crime.isRequiresPolice() ? VIEW_TYPE_SERIOUS : VIEW_TYPE_NORMAL;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            if (viewType == VIEW_TYPE_SERIOUS) {
                View view = layoutInflater.inflate(R.layout.list_item_crime_police, parent, false);
                return new SeriousCrimeHolder(view);
            } else {
                View view = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
                return new CrimeHolder(layoutInflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Crime crime = mCrimes.get(position);

            if (holder instanceof SeriousCrimeHolder) {
                ((SeriousCrimeHolder) holder).bind(crime);
            } else if (holder instanceof CrimeHolder) {
                ((CrimeHolder) holder).bind(crime, position);
            }
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public List<Crime> getCrimes() {
            return mCrimes;
        }

    }


    private class SeriousCrimeHolder extends RecyclerView.ViewHolder {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mTimeTextView;
        private Button mContactPoliceButton;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public SeriousCrimeHolder(View itemView) {
            super(itemView);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mTimeTextView = itemView.findViewById(R.id.crime_time);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);

            mContactPoliceButton = itemView.findViewById(R.id.contact_police_button);


            itemView.setOnClickListener(v -> {
                if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
                    // Phone layout: open full screen
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivity(intent);
                } else {
                    // Tablet layout: show fragment side-by-side
                    Fragment newDetail = CrimeFragment.newInstance(mCrime.getId());
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.detail_fragment_container, newDetail)
                            .commit();
                }
            });

            mContactPoliceButton.setOnClickListener(v -> {
                Toast.makeText(getActivity(), "Calling police for " + mCrime.getTitle(),
                        Toast.LENGTH_SHORT).show();
            });
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());

            java.text.DateFormat formattedDate =
                    java.text.DateFormat.getDateInstance(java.text.DateFormat.FULL, Locale.getDefault());
            mDateTextView.setText(formattedDate.format(mCrime.getDate()));

            java.text.DateFormat formattedTime =
                    java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT, Locale.getDefault());
            mTimeTextView.setText(formattedTime.format(mCrime.getDate()));

            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);

            StringBuilder description = new StringBuilder();
            description.append("This crime is serious, ")
                    .append(crime.getTitle())
                    .append(", ")
                    .append(formattedDate.format(crime.getDate()))
                    .append(", ")
                    .append(formattedTime.format(crime.getDate()));

            if (crime.isSolved()) {
                description.append(", case solved");
            } else {
                description.append(", case not solved");
            }

            itemView.setContentDescription(description.toString());
        }
    }

}
