package com.example.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.criminalintent.Crime;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.DATE));
        long time = getLong(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.TIME));
        int isSolved = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SUSPECT));
        boolean isRequiresPolice = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.REQUIRES_POLICE)) != 0;


        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setDate(new Time(time));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        crime.setRequiresPolice(isRequiresPolice);

        return crime;
//        return null;
    }
}
