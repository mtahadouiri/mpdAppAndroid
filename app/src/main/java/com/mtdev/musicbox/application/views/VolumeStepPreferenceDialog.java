

package com.mtdev.musicbox.application.views;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;


import com.mtdev.musicbox.R;


public class VolumeStepPreferenceDialog extends DialogFragment implements SeekBar.OnSeekBarChangeListener {
    private static final int WARNING_THRESHOLD = 10;

    private SeekBar mSeekBar;

    private TextView mVolumeLabel;
    private TextView mWarningLabel;

    private int mVolumeStepSize;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.volume_step_preference_dialog, container, false);

        mSeekBar = rootView.findViewById(R.id.volume_seekbar);
        mVolumeLabel = rootView.findViewById(R.id.volume_text);
        mWarningLabel = rootView.findViewById(R.id.volume_warning_text);

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        mVolumeStepSize = sharedPreferences.getInt(getString(R.string.pref_volume_steps_key), getResources().getInteger(R.integer.pref_volume_steps_default));

        mSeekBar.setProgress(mVolumeStepSize);
        mSeekBar.setOnSeekBarChangeListener(this);

        rootView.findViewById(R.id.button_ok).setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.pref_volume_steps_key), mVolumeStepSize == 0 ? 1 : mVolumeStepSize);
            editor.apply();
            dismiss();
        });

        rootView.findViewById(R.id.button_cancel).setOnClickListener(v -> dismiss());

        updateLabels();

        return rootView;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        /*mVolumeStepSize = progress;
        updateLabels();*/
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void updateLabels() {
        if (mVolumeStepSize > WARNING_THRESHOLD) {
            mWarningLabel.setVisibility(View.VISIBLE);
        } else {
            mWarningLabel.setVisibility(View.INVISIBLE);
        }
        mVolumeLabel.setText(getString(R.string.volume_step_size_dialog_title, mVolumeStepSize));
    }
}
