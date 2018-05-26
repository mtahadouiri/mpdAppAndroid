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

package com.mtdev.musicbox.application.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import com.mtdev.musicbox.AppConfig;
import com.mtdev.musicbox.Client.Activities.EditProfileActivity;
import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.adapters.ProfileAdapter;
import com.mtdev.musicbox.application.callbacks.FABFragmentCallback;
import com.mtdev.musicbox.application.callbacks.ProfileManageCallbacks;
import com.mtdev.musicbox.application.loaders.ProfilesLoader;
import com.mtdev.musicbox.application.utils.ThemeUtils;
import com.mtdev.musicbox.mpdservice.ConnectionManager;
import com.mtdev.musicbox.mpdservice.profilemanagement.MPDProfileManager;
import com.mtdev.musicbox.mpdservice.profilemanagement.MPDServerProfile;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilesFragment extends Fragment{
    public final static String TAG = ProfilesFragment.class.getSimpleName();
     TextView pseudo, nom, email;
     View bottomMarginLayout;

     ImageView backBtn;
     TextView fragmentTitle;
     public SharedPreferences settings;

     public ProfilesFragment() {
     // Required empty public constructor
     }

     @Override
     public void onAttach(Context context) {
     super.onAttach(context);
     }

     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
     Bundle savedInstanceState) {
     // Inflate the layout for this fragment
     return inflater.inflate(R.layout.fragment_profile, container, false);
     }


     @Override
     public void onViewCreated(View view, Bundle savedInstanceState) {
         super.onViewCreated(view, savedInstanceState);
         pseudo = (TextView) view.findViewById(R.id.pseudo);
         nom = (TextView) view.findViewById(R.id.nom);
         email = (TextView) view.findViewById(R.id.email);
         settings = getContext().getSharedPreferences("USER", 0);
         pseudo.setText(settings.getString("pseudo", null));
         email.setText(settings.getString("email", null));
         nom.setText(settings.getString("firstname", null) + " " + settings.getString("lastname", null));
         CircleImageView imgPi = (CircleImageView) view.findViewById(R.id.profilePic);
         Log.e("image path", AppConfig.URL_GETIMG_PREFIX + settings.getString("img", null));
         Picasso.with(getContext()).load(AppConfig.URL_GETIMG_PREFIX + settings.getString("img", null)).into(imgPi);
         ImageView img = (ImageView) view.findViewById(R.id.edit);
         img.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i = new Intent(getContext(), EditProfileActivity.class);
                 startActivity(i);
             }
         });

     }
}
