

package com.mtdev.musicbox.application.listviewitems;


import android.content.Context;
import android.widget.TextView;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.ScrollSpeedAdapter;

public class ImageListItem extends AbsImageListViewItem {

    TextView mMainView;
    TextView mDetailsView;


    public ImageListItem(Context context, String text, String details, ScrollSpeedAdapter adapter) {
        super(context, R.layout.listview_item_image, R.id.item_image, R.id.item_image_viewswitcher, adapter);

        mMainView = findViewById(R.id.item_text);
        mDetailsView = findViewById(R.id.item_details);

        if ( details == null || details.isEmpty() ) {
            mDetailsView.setVisibility(GONE);
        } else {
            mDetailsView.setText(details);
        }

        mMainView.setText(text);

    }

    public void setText(String text) {
        mMainView.setText(text);
    }

    public void setDetails(String text) {
        if (null != mDetailsView && !text.isEmpty()) {
            mDetailsView.setText(text);
            mDetailsView.setVisibility(VISIBLE);
        } else if ( null != mDetailsView ) {
            mDetailsView.setText("");
            mDetailsView.setVisibility(GONE);
        }
    }

}
