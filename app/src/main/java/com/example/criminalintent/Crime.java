package com.example.criminalintent;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;

public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
//    private Time mTime;
    private boolean mSolved;
    private String mSuspect;
    private long mSuspectId;
    private boolean mRequiresPolice;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
//    public Time getTime() { return mTime; }
//    public void setTime(Time time) { mTime = time; }
    public Date getDate() { return mDate; }
    public void setDate(Date date) {
        mDate = date;
    }
    public boolean isSolved() {
        return mSolved;
    }
    public void setSolved(boolean solved) {
        mSolved = solved;
    }
    public String getSuspect() {
        return mSuspect;
    }
    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }
    public long getSuspectId() {
        return mSuspectId;
    }
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }
    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }
}
