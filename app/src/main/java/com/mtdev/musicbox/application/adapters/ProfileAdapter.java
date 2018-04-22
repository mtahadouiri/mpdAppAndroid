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

package com.mtdev.musicbox.application.adapters;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.mtdev.musicbox.application.listviewitems.ProfileListItem;
import com.mtdev.musicbox.mpdservice.profilemanagement.MPDServerProfile;

public class ProfileAdapter extends GenericSectionAdapter<MPDServerProfile> {
    private Context mContext;

    public ProfileAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MPDServerProfile profile = (MPDServerProfile)getItem(position);

        // Profile name
        String profileName = profile.getProfileName();

        int port = profile.getPort();
        String portString = String.valueOf(port);

        String hostname = profile.getHostname();

        boolean checked = profile.getAutoconnect();

        if (convertView != null) {
            ProfileListItem profileListItem = (ProfileListItem) convertView;

            profileListItem.setProfileName(profileName);
            profileListItem.setHostname(hostname);
            profileListItem.setPort(portString);
            profileListItem.setChecked(checked);
        } else {
            convertView = new ProfileListItem(mContext, profileName, hostname, portString, checked);
        }

        return convertView;
    }

    public void setActive(int position, boolean active) {
        for(MPDServerProfile profile: mModelData) {
            profile.setAutoconnect(false);
        }
        ((MPDServerProfile)getItem(position)).setAutoconnect(active);
        notifyDataSetChanged();
    }
}
