/*
 *  Copyright (C) 2018 Team Gateship-One
 *  (Hendrik Borghorst & Frederik Luetkes)
 *
 *  The AUTHORS.md file contains a detailed contributors list:
 *  <https://github.com/gateship-one/malp/blob/master/AUTHORS.md>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.mtdev.musicbox.application.callbacks;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.fragments.TextDialog;
import com.mtdev.musicbox.mpdservice.handlers.serverhandler.MPDQueryHandler;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDFileEntry;
import com.mtdev.musicbox.application.fragments.TextDialog;

public class AddPathToPlaylist implements OnSaveDialogListener {
    MPDFileEntry mFile;

    Context mContext;

    public AddPathToPlaylist(MPDFileEntry file, Context context) {
        mFile = file;
        mContext = context;
    }

    @Override
    public void onSaveObject(String title) {
        if ( null != mFile) {
            MPDQueryHandler.addURLToSavedPlaylist(title, mFile.getPath());
        }
    }

    @Override
    public void onCreateNewObject() {
        // open dialog in order to save the current playlist as a playlist in the mediastore
        TextDialog textDialog = new TextDialog();
        Bundle args = new Bundle();
        args.putString(TextDialog.EXTRA_DIALOG_TITLE, mContext.getResources().getString(R.string.dialog_save_playlist));
        args.putString(TextDialog.EXTRA_DIALOG_TEXT, mContext.getResources().getString(R.string.default_playlist_title));

        textDialog.setCallback(text -> MPDQueryHandler.addURLToSavedPlaylist(text, mFile.getPath()));
        textDialog.setArguments(args);
        textDialog.show(((AppCompatActivity)mContext).getSupportFragmentManager(), "SavePLTextDialog");
    }
}