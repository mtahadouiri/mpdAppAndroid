

package com.mtdev.musicbox.application.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.mtdev.musicbox.R;
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
