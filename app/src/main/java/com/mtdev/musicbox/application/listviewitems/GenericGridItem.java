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

package com.mtdev.musicbox.application.listviewitems;


import android.content.Context;
import android.widget.TextView;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.ScrollSpeedAdapter;
import com.mtdev.musicbox.application.adapters.ScrollSpeedAdapter;

public class GenericGridItem extends AbsImageListViewItem {

    protected final TextView mTitleView;



    public GenericGridItem(Context context, String labelText, ScrollSpeedAdapter adapter) {
        super(context,R.layout.gridview_item, R.id.item_artists_cover_image, R.id.item_grid_viewswitcher, adapter);

        mTitleView = findViewById(R.id.item_grid_text);

        mTitleView.setText(labelText);
    }


    /*
    * Sets the title for the GridItem
     */
    public void setTitle(String text) {
        mTitleView.setText(text);
    }
}
