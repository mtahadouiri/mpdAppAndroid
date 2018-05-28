
package com.mtdev.musicbox.application.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.callbacks.FABFragmentCallback;
import com.mtdev.musicbox.application.views.VolumeStepPreferenceDialog;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = SettingsFragment.class.getSimpleName();

    private FABFragmentCallback mFABCallback = null;
    private OnArtworkSettingsRequestedCallback mArtworkCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // add listener to open artwork settings
        Preference openArtwork = findPreference(getString(R.string.pref_artwork_settings_key));
        openArtwork.setOnPreferenceClickListener(preference -> {
            mArtworkCallback.openArtworkSettings();
            return true;
        });

        Preference openVolumeStepDialog = findPreference(getString(R.string.pref_volume_steps_dialog_key));
        openVolumeStepDialog.setOnPreferenceClickListener(preference -> {
            VolumeStepPreferenceDialog dialog = new VolumeStepPreferenceDialog();
            dialog.show(getFragmentManager(), "Volume steps");
            return true;
        });
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

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mArtworkCallback = (OnArtworkSettingsRequestedCallback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnArtworkSettingsRequestedCallback");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        if (null != mFABCallback) {
            mFABCallback.setupFAB(false, null);
            mFABCallback.setupToolbar(getString(R.string.menu_settings), false, true, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.main_settings);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.main_settings, false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_theme_key)) || key.equals(getString(R.string.pref_dark_theme_key))) {
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            startActivity(intent);
        }
    }

    public interface OnArtworkSettingsRequestedCallback {
        void openArtworkSettings();
    }
}
