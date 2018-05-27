

package com.mtdev.musicbox.application.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtdev.musicbox.AppConfig;
import com.mtdev.musicbox.Client.Activities.EditProfileActivity;
import com.mtdev.musicbox.R;
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
