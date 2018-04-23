package com.mtdev.musicbox.application.utils;

import com.mtdev.musicbox.Client.Entities.LocalTrack;

import java.util.Comparator;

public class LocalMusicComparator implements Comparator<LocalTrack> {

    @Override
    public int compare(LocalTrack lhs, LocalTrack rhs) {
        return lhs.getTitle().toString().compareTo(rhs.getTitle().toString());
    }
}
