

package com.mtdev.musicbox.application.listviewitems;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.mtdev.musicbox.R;

public class OutputListItem extends LinearLayout {

    CheckedTextView mMainView;

    public OutputListItem(Context context, String outputName, boolean active, int outputid) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.listview_item_output, this, true);

        mMainView = findViewById(R.id.item_output_name);
        mMainView.setText(outputName);

        mMainView.setChecked(active);
    }

    public void setName(String name) {
        mMainView.setText(name);
    }

    public void setChecked(boolean checked) {
        mMainView.setChecked(checked);
    }

}
