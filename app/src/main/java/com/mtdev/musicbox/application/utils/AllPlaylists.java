package com.mtdev.musicbox.application.utils;

import com.mtdev.musicbox.Client.Entities.Playlist;

import java.util.ArrayList;
import java.util.List;

public class AllPlaylists {
    private List<Playlist> allPlaylists;

    public AllPlaylists() {
        allPlaylists = new ArrayList<>();
    }

    public List<Playlist> getPlaylists() {
        return allPlaylists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.allPlaylists = playlists;
    }

    public void addPlaylist(Playlist pl){
        allPlaylists.add(pl);
    }
}
