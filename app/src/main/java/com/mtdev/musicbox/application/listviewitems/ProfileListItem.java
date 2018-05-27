

package com.mtdev.musicbox.application.listviewitems;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mtdev.musicbox.R;

public class ProfileListItem extends LinearLayout {
    TextView mProfileNameView;
    TextView mHostnameView;
    TextView mPortView;

    RadioButton mRadioButton;

    public ProfileListItem(Context context, String profilename, String hostname, String port, boolean checked) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_item_profile, this, true);

        mProfileNameView = findViewById(R.id.item_profile_name);
        mProfileNameView.setText(profilename);

        mHostnameView = findViewById(R.id.item_profile_hostname);
        mHostnameView.setText(hostname);

        mPortView = findViewById(R.id.item_profile_port);
        mPortView.setText(port);

        mRadioButton = findViewById(R.id.item_profile_radiobtn);
        mRadioButton.setChecked(checked);
    }

    public void setProfileName(String profilename) {
        mProfileNameView.setText(profilename);
    }

    public void setHostname(String hostname) {
        mHostnameView.setText(hostname);
    }

    public void setPort(String port) {
        mPortView.setText(port);
    }

    public void setChecked(boolean checked) {
        mRadioButton.setChecked(checked);
    }
}
