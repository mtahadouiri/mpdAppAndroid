

package com.mtdev.musicbox.application.activities;


import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.utils.ThemeUtils;
import com.mtdev.musicbox.mpdservice.mpdprotocol.MPDException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContributorsActivity extends GenericActivity {

    private static final String CONTRIBUTOR_NAME_KEY = "name";

    private static final String CONTRIBUTOR_TYPE_KEY = "type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contributors);

        getWindow().setStatusBarColor(ThemeUtils.getThemeColor(this, R.attr.malp_color_primary_dark));

        ListView contributors = findViewById(R.id.contributors_listview);

        String[] contributors_names = getResources().getStringArray(R.array.contributors);
        String[] contributors_types = getResources().getStringArray(R.array.contributors_type);

        List<Map<String, String>> contributors_list = new ArrayList<>();
        Map<String, String> map;

        for (int i = 0; i < contributors_names.length; i++) {
            map = new HashMap<>();
            map.put(CONTRIBUTOR_NAME_KEY, contributors_names[i]);
            map.put(CONTRIBUTOR_TYPE_KEY, contributors_types[i]);
            contributors_list.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, contributors_list, R.layout.listview_item,
                new String[] {CONTRIBUTOR_NAME_KEY, CONTRIBUTOR_TYPE_KEY}, new int[] { R.id.item_title, R.id.item_subtitle });

        contributors.setAdapter(adapter);
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
