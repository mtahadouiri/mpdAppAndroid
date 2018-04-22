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

package com.mtdev.musicbox.application.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.fragments.LicensesDialog;
import com.mtdev.musicbox.application.utils.ThemeUtils;
import com.mtdev.musicbox.mpdservice.mpdprotocol.MPDException;

public class AboutActivity extends GenericActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getWindow().setStatusBarColor(ThemeUtils.getThemeColor(this,R.attr.malp_color_primary_dark));


        String versionName = "";
        // get version from manifest
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.activity_about_version)).setText(versionName);

        findViewById(R.id.button_contributors).setOnClickListener(view -> {
            Intent myIntent = new Intent(AboutActivity.this, ContributorsActivity.class);

            startActivity(myIntent);
        });

        findViewById(R.id.logo_musicbrainz).setOnClickListener(view -> {
            Intent urlIntent = new Intent(Intent.ACTION_VIEW);
            urlIntent.setData(Uri.parse(getResources().getString(R.string.url_musicbrainz)));
            startActivity(urlIntent);
        });

        findViewById(R.id.logo_lastfm).setOnClickListener(view -> {
            Intent urlIntent = new Intent(Intent.ACTION_VIEW);
            urlIntent.setData(Uri.parse(getResources().getString(R.string.url_lastfm)));
            startActivity(urlIntent);
        });

        findViewById(R.id.logo_fanarttv).setOnClickListener(view -> {
            Intent urlIntent = new Intent(Intent.ACTION_VIEW);
            urlIntent.setData(Uri.parse(getResources().getString(R.string.url_fanarttv)));
            startActivity(urlIntent);
        });

        findViewById(R.id.thirdparty_licenses).setOnClickListener(view -> LicensesDialog.newInstance().show(getFragmentManager(), LicensesDialog.class.getSimpleName()));
    }

    @Override
    protected void onConnected() {

    }

    @Override
    protected void onDisconnected() {

    }

    @Override
    protected void onMPDError(MPDException.MPDServerException e) {

    }

    @Override
    protected void onMPDConnectionError(MPDException.MPDConnectionException e) {

    }
}
