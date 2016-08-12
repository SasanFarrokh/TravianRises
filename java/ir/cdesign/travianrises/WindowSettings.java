package ir.cdesign.travianrises;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class WindowSettings extends Fragment {

    Float mVolume = 50f;
    Float sVolume = 50f;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.window_settings, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        SeekBar musicSeekBar = (SeekBar) getActivity().findViewById(R.id.music_volume);
        SeekBar soundSeekBar = (SeekBar) getActivity().findViewById(R.id.sound_volume);

        mVolume = MainActivity.pref.getFloat(MainActivity.MVOLUME,0.5f) * 100;
        sVolume = MainActivity.pref.getFloat(MainActivity.SVOLUME,1f) * 100;

        musicSeekBar.setProgress(mVolume.intValue());
        soundSeekBar.setProgress(sVolume.intValue());
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float volume;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                mVolume = ((Integer) progress).floatValue();
                volume = mVolume / 100;
                ((MainActivity) getActivity()).mediaPlayer.setVolume(volume,volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = MainActivity.pref.edit();
                editor.putFloat(MainActivity.MVOLUME, volume);
                editor.apply();
            }
        });
        soundSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float volume;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sVolume = ((Integer) progress).floatValue();
                volume = sVolume / 100;
                ((MainActivity) getActivity()).soundfxVol = volume;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = MainActivity.pref.edit();
                editor.putFloat(MainActivity.SVOLUME, volume);
                editor.apply();
            }
        });
    }
}
