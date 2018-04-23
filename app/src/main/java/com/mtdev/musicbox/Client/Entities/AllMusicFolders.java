package com.mtdev.musicbox.Client.Entities;

import com.mtdev.musicbox.application.utils.MusicFolder;

import java.util.ArrayList;
import java.util.List;

public class AllMusicFolders {
    List<MusicFolder> musicFolders;

    public AllMusicFolders() {
        musicFolders = new ArrayList<>();
    }

    public List<MusicFolder> getMusicFolders() {
        return musicFolders;
    }

    public void setMusicFolders(List<MusicFolder> musicFolders) {
        this.musicFolders = musicFolders;
    }
}
