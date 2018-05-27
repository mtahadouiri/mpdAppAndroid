package com.mtdev.musicbox.Client.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.mtdev.musicbox.Client.Utils.ClickItemTouchListener;
import com.mtdev.musicbox.Client.Utils.ImageLoader;
import com.mtdev.musicbox.Client.Utils.ViewAllPlaylistsRecyclerAdapter;
import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.activities.MainActivity;
import com.mtdev.musicbox.application.callbacks.FABFragmentCallback;
import com.mtdev.musicbox.application.utils.AllPlaylists;
import com.mtdev.musicbox.application.utils.CommonUtils;

import static android.content.Context.MODE_PRIVATE;
import static com.mtdev.musicbox.application.activities.MainActivity.allPlaylists;
import static com.mtdev.musicbox.application.activities.MainActivity.themeColor;

public class PlayList extends Fragment {
    public static final String TAG = PlayList.class.getSimpleName();
    private FABFragmentCallback mFABCallback = null;
    public RecyclerView allPlaylistRecycler;
    public ViewAllPlaylistsRecyclerAdapter vpAdapter;
    LinearLayout noPlaylistContent;
    LinearLayoutManager mLayoutManager2;
    FloatingActionButton addPlaylistFAB;
    ImageLoader imgLoader;
    public SharedPreferences mPrefs;
    private SharedPreferences.Editor prefsEditor;
    private Gson gson;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_play_list, container, false);
        mPrefs = getContext().getSharedPreferences("a",MODE_PRIVATE);
        prefsEditor = mPrefs.edit();
        gson = new Gson();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        noPlaylistContent = (LinearLayout) view.findViewById(R.id.noPlaylistContent);

        allPlaylistRecycler = (RecyclerView) view.findViewById(R.id.all_playlists_recycler);

        addPlaylistFAB = (FloatingActionButton) view.findViewById(R.id.new_playlist_fab);
        addPlaylistFAB.setBackgroundTintList(ColorStateList.valueOf(themeColor));
        addPlaylistFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mFABCallback.newPlaylistListener();
                FragmentManager fragmentManager = getFragmentManager();

                // clear backstack
                fragmentManager.popBackStackImmediate("", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                Fragment fragment = new NewPlaylistFragment();
                String fragmentTag = "NewPlaylistFragment";
                // Do the actual fragment transaction
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, fragment, fragmentTag);
                transaction.commit();
            }
        });
        allPlaylists = new AllPlaylists();
        if (allPlaylists.getPlaylists().size() == 0) {
            allPlaylistRecycler.setVisibility(View.INVISIBLE);
            noPlaylistContent.setVisibility(View.VISIBLE);
            addPlaylistFAB.setVisibility(View.VISIBLE);
        } else {
            allPlaylistRecycler.setVisibility(View.VISIBLE);
            noPlaylistContent.setVisibility(View.INVISIBLE);
            addPlaylistFAB.setVisibility(View.INVISIBLE);
        }
        new loadSavedData().execute();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (null != mFABCallback) {
            mFABCallback.setupFAB(false, null);
                mFABCallback.setupToolbar(getString(R.string.menu_statistic), false, true, false);
        }
    }

    /**
     * Called when the fragment is first attached to its context.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mFABCallback = (FABFragmentCallback) context;
        } catch (ClassCastException e) {
            mFABCallback = null;
        }
    }


    private void getSavedData() {
        try {
            Gson gson = new Gson();
            Log.d("TIME", "start");
            String json2 = mPrefs.getString("allPlaylists", "");
            allPlaylists = gson.fromJson(json2, AllPlaylists.class);
            Log.d("All playlists",allPlaylists.getPlaylists().size()+"");
            Log.d("TIME", "allPlaylists");
            String json3 = mPrefs.getString("queue", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class loadSavedData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            getSavedData();
            return "done";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
           getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    try {
                        if (Build.VERSION.SDK_INT >= 21) {
                            Window window = ((Activity) getActivity()).getWindow();
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(CommonUtils.getDarkColor(themeColor));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (allPlaylists == null) {
                        allPlaylists = new AllPlaylists();
                    }

                    vpAdapter = new ViewAllPlaylistsRecyclerAdapter(allPlaylists.getPlaylists(), getContext());
                    mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    allPlaylistRecycler.setLayoutManager(mLayoutManager2);
                    allPlaylistRecycler.setItemAnimator(new DefaultItemAnimator());
                    allPlaylistRecycler.setAdapter(vpAdapter);

                    allPlaylistRecycler.addOnItemTouchListener(new ClickItemTouchListener(allPlaylistRecycler) {
                        @Override
                        public boolean onClick(RecyclerView parent, View view, int position, long id) {
                            // mFABCallback.onPlaylistSelected(position);
                            return true;
                        }

                        @Override
                        public boolean onLongClick(RecyclerView parent, View view, final int position, long id) {
                            PopupMenu popup = new PopupMenu(getContext(), view);
                            popup.getMenuInflater().inflate(R.menu.playlist_popup, popup.getMenu());

                            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (item.getTitle().equals("Delete")) {
                                        allPlaylists.getPlaylists().remove(position);
                                        if (vpAdapter != null) {
                                            vpAdapter.notifyItemRemoved(position);
                                        }
                                        if (allPlaylists.getPlaylists().size() == 0) {
                                            noPlaylistContent.setVisibility(View.VISIBLE);
                                        }
                                        new MainActivity.SavePlaylists().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                        MainActivity.pAdapter.notifyItemRemoved(position);
                                    } else if (item.getTitle().equals("Rename")) {
                                        //MainActivity.renamePlaylistNumber = position;
                                        //mCallback.onPlaylistRename();
                                    }
                                    return true;
                                }
                            });
                            popup.show();
                            return true;
                        }

                        @Override
                        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                        }
                    });
                }
            });
        }
    }



}

