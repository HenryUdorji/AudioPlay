package com.codemountain.audioplay.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bullhead.equalizer.EqualizerFragment;
import com.codemountain.audioplay.R;


public class EqualizerActivity extends AppCompatActivity {
    private int sessionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);

        /*if (getParent() == MusicPlayerActivity.getInstance()) {
            sessionID = MusicPlayerActivity.getInstance().audioSessionID();
        }
        else if (getParent() == MainActivity.getInstance()){
            sessionID = MainActivity.getInstance().audioSessionID();
        }
        else if (getParent() == AlbumsDetailActivity.getInstance()){
            sessionID = AlbumsDetailActivity.getInstance().audioSessionID();
        }
        else if (getParent() == ArtistDetailActivity.getInstance()){
            sessionID = ArtistDetailActivity.getInstance().audioSessionID();
        }
        else if (getParent() == SettingsActivity.getInstance()){
            sessionID = SettingsActivity.getInstance().audioSessionID();
        }*/
        sessionID = MusicPlayerActivity.getInstance().audioSessionID();

        /*Intent intent = getIntent();
        int sessionID = intent.getIntExtra("AUDIO_SESSION_ID", 0);*/
        EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                .setAccentColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setAudioSessionId(sessionID)
                .build();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.equalizerContainer, equalizerFragment)
                .commit();
    }
}