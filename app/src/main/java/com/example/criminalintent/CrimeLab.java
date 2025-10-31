package com.example.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.criminalintent.database.CrimeBaseHelper;
import com.example.criminalintent.database.CrimeCursorWrapper;
import com.example.criminalintent.database.CrimeDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
//    private List<Crime> mCrimes;
    private HashMap<UUID, Crime> mCrimeMap;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }
    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext)
                .getWritableDatabase();

//        mCrimes = new ArrayList<>();
        mCrimeMap = new HashMap<>();

//        for (int i = 0; i < 100; i++) {
//            Crime crime = new Crime();
//            crime.setTitle("Crime #" + i);
//            crime.setRequiresPolice(i % 2 != 0);
//            crime.setSolved(i % 2 == 0);
//
//            mCrimeMap.add(crime);
//            mCrimeMap.put(crime.getId(), crime);
//        }
//
//        for (int i = 0; i < 100; i++) {
//            Crime crime = new Crime();
//            crime.setTitle("Crime #" + i);
//            crime.setRequiresPolice(i % 2 == 0);
//            crime.setSolved(i % 2 == 0);
//            mCrimes.add(crime);
//            mCrimeMap.put(crime.getId(), crime);
//        }
    }

    public void addCrime(Crime c) {
//        mCrimes.add(c);
        ContentValues values = getContentValues(c);

        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
    }
    public List<Crime> getCrimes() {
//        return mCrimes;
//        return new ArrayList<>();

        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id) {
//        return mCrimeMap.get(id);
//        for (Crime crime : mCrimes) {
//            if (crime.getId().equals(id)) {
//                return crime;
//            }
//        }
//        return null;

        CrimeCursorWrapper cursor = queryCrimes(
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Crime crime) {
        File filesDir = mContext.getFilesDir();
        if (filesDir == null) {
            return null;
        }
        return new File(filesDir, crime.getId().toString() + ".jpg");
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeDbSchema.CrimeTable.NAME,
                null, // columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new CrimeCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.TIME, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeDbSchema.CrimeTable.Cols.REQUIRES_POLICE, crime.isRequiresPolice() ? 1 : 0);

        return values;
    }

    public void deleteCrime(Crime crime) {
        String uuidString = crime.getId().toString();

        mDatabase.delete(
                CrimeDbSchema.CrimeTable.NAME,
                CrimeDbSchema.CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString }
        );
    }

}
