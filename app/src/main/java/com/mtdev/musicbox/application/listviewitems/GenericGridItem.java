

package com.mtdev.musicbox.application.listviewitems;


import android.content.Context;
import android.widget.TextView;

import com.mtdev.musicbox.R;
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
