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

import com.mtdev.musicbox.application.listviewitems.OutputListItem;
import com.mtdev.musicbox.mpdservice.mpdprotocol.mpdobjects.MPDOutput;

public class OutputAdapter extends GenericSectionAdapter<MPDOutput> {
    private Context mContext;

    public OutputAdapter(Context context) {
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MPDOutput output = (MPDOutput)getItem(position);

        // Profile name
        String outputName = output.getOutputName();

        int outputId = output.getID();
        boolean active = output.getOutputState();

        if ( convertView == null ) {
            // Create a new View and not reuse it
            convertView = new OutputListItem(mContext, outputName, active, outputId);
        } else {
            OutputListItem outputItem = (OutputListItem)convertView;
            outputItem.setName(outputName);
            outputItem.setChecked(active);
        }

        return convertView;
    }

    public void setOutputActive(int index, boolean active ) {
        MPDOutput output = (MPDOutput)getItem(index);
        output.setOutputState(active);
        notifyDataSetChanged();
    }
}
