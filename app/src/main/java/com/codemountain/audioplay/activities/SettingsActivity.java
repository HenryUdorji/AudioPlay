package com.codemountain.audioplay.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.codemountain.audioplay.MainActivity;
import com.codemountain.audioplay.R;
import com.codemountain.audioplay.service.MediaPlayerSingleton;
import com.codemountain.audioplay.utils.Extras;
import com.travijuu.numberpicker.library.NumberPicker;

import java.util.Locale;

import static com.codemountain.audioplay.utils.Constants.FADE_IN_OUT_DURATION;
import static com.codemountain.audioplay.utils.Constants.FADE_SWITCH;
import static com.codemountain.audioplay.utils.Constants.FADE_TIMER;
import static com.codemountain.audioplay.utils.Constants.FADE_TRACK;
import static com.codemountain.audioplay.utils.Constants.LANGUAGE;
import static com.codemountain.audioplay.utils.Constants.THEME_SWITCH;
import static com.codemountain.audioplay.utils.Constants.TIMER_DURATION;
import static com.codemountain.audioplay.utils.Constants.VISUALIZER;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = "SettingsActivity";
    private static SettingsActivity settingsActivity;
    public static long COUNTDOWN = 0;
    private Toolbar toolbar;
    private SwitchCompat timerSwitch, crossFadeSwitch, themeSwitch;
    private NumberPicker numberPicker, timerPicker;
    private LinearLayout numberLayout, timerLayout, styleLayout, languageLayout;
    private TextView countDownText, themeText, visualizer, languageText;
    CountDownTimer countDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        boolean value = Extras.getInstance().getSwitchFade();
        boolean timerValue = Extras.getInstance().getSwitchTimer();
        boolean themeValue = Extras.getInstance().getSwitchTheme();
        String waveValue = Extras.getInstance().getVisualizerValue();
        String languageValue = Extras.getInstance().getLanguageValue();


        int timeValueCount = Integer.parseInt(Extras.getInstance().getTimerDuration());
        int numValue = Integer.parseInt(Extras.getInstance().getFadeDuration());
        Log.d(TAG, "onCreate: " + numValue);
        Log.d(TAG, "onCreate Timer: " + timeValueCount);


        themeSwitch = findViewById(R.id.themeSwitch);
        timerSwitch = findViewById(R.id.timerSwitch);
        crossFadeSwitch = findViewById(R.id.crossFadeSwitch);
        numberPicker = findViewById(R.id.numberPicker);
        timerPicker = findViewById(R.id.timerNumPicker);
        numberLayout = findViewById(R.id.numberLayout);
        timerLayout = findViewById(R.id.timerLayout);
        styleLayout = findViewById(R.id.styleLayout);
        languageLayout = findViewById(R.id.langLayout);
        countDownText = findViewById(R.id.countDownText);
        themeText = findViewById(R.id.theme);
        visualizer = findViewById(R.id.visualizer);
        languageText = findViewById(R.id.languageText);

        /*switch (languageValue){
            case "English":
                languageText.setText("English");
                break;
            case "French":
                languageText.setText("French");
                break;
            case "Spanish":
                languageText.setText("Spanish");
                break;
            case "Mandarin Chinese":
                languageText.setText("Mandarin Chinese");
                break;
        }*/

        /*if (waveValue.equals("wave")){
            visualizer.setText("Wave");
        }
        else if (waveValue.equals("bars")){
            visualizer.setText("Bars");
        }*/


        setCrossFade(value, numValue);
        setStopTimer(timerValue, timeValueCount);
        setVisualizerStyleDialog(waveValue);
        //setThemeStyle(themeValue);
        //setLanguageDialog(languageValue);
    }

    private void setLanguageDialog(String languageValue) {

        languageLayout.setOnClickListener(v -> {
            Dialog dialog = new Dialog(SettingsActivity.this);
            dialog.setContentView(R.layout.language_dialog);
            ImageButton closeImageBtn = dialog.findViewById(R.id.dialogCloseBtn);
            RadioGroup dialogRg = dialog.findViewById(R.id.languageRadioGroup);
            RadioButton englishRBtn = dialog.findViewById(R.id.englishRBtn);
            RadioButton frenchRBtn = dialog.findViewById(R.id.frenchRBtn);
            RadioButton spanishRBtn = dialog.findViewById(R.id.spanishRBtn);
            RadioButton mandarinChinese = dialog.findViewById(R.id.mandarinChinese);

            switch (languageValue){
                case "English":
                    englishRBtn.setChecked(true);
                    frenchRBtn.setChecked(false);
                    spanishRBtn.setChecked(false);
                    mandarinChinese.setChecked(false);
                    break;
                case "French":
                    englishRBtn.setChecked(false);
                    frenchRBtn.setChecked(true);
                    spanishRBtn.setChecked(false);
                    mandarinChinese.setChecked(false);
                    break;
                case "Spanish":
                    englishRBtn.setChecked(false);
                    frenchRBtn.setChecked(false);
                    spanishRBtn.setChecked(true);
                    mandarinChinese.setChecked(false);
                    break;
                case "Mandarin Chinese":
                    englishRBtn.setChecked(false);
                    frenchRBtn.setChecked(false);
                    spanishRBtn.setChecked(false);
                    mandarinChinese.setChecked(true);
                    break;
            }

            dialogRg.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId){
                    case R.id.englishRBtn:
                        Extras.getInstance().putString(LANGUAGE, "English");
                        Log.d(TAG, "setLanguageDialog: ENGLISH");
                        languageText.setText("English");
                        dialog.dismiss();
                        break;
                    case R.id.frenchRBtn:
                        Extras.getInstance().putString(LANGUAGE, "French");
                        Log.d(TAG, "setLanguageDialog: FRENCH");
                        languageText.setText("French");
                        dialog.dismiss();
                        break;
                    case R.id.spanishRBtn:
                        Extras.getInstance().putString(LANGUAGE, "Spanish");
                        Log.d(TAG, "setLanguageDialog: SPANISH");
                        languageText.setText("Spanish");
                        dialog.dismiss();
                        break;
                    case R.id.mandarinChinese:
                        Extras.getInstance().putString(LANGUAGE, "Mandarin Chinese");
                        Log.d(TAG, "setLanguageDialog: MANDARIN CHINESE");
                        languageText.setText("Mandarin Chinese");
                        dialog.dismiss();
                        break;


                }
            });

            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            closeImageBtn.setOnClickListener(v1 -> dialog.dismiss());

        });
    }

    private void setThemeStyle(boolean themeValue) {
        if (themeValue) {
            themeSwitch.setChecked(true);
            themeText.setText("Light");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setLightMode();

        }
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                Extras.getInstance().putBoolean(THEME_SWITCH, true);
                themeText.setText("Light");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                setLightMode();
                recreate();

            }
            else {
                Extras.getInstance().putBoolean(THEME_SWITCH, false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                themeText.setText("Dark");
            }
        });
    }

    private void setLightMode() {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO){
            setTheme(R.style.LightTheme);
        }
        else {
            setTheme(R.style.AppTheme);
        }
    }

    private void setVisualizerStyleDialog(String waveValue) {

        styleLayout.setOnClickListener(v -> {
            Dialog dialog = new Dialog(SettingsActivity.this);
            dialog.setContentView(R.layout.style_dialog);
            ImageButton closeImageBtn = dialog.findViewById(R.id.dialogCloseBtn);
            RadioGroup dialogRg = dialog.findViewById(R.id.styleRadioGroup);
            RadioButton waveRBtn = dialog.findViewById(R.id.waveRBtn);
            RadioButton barsRBtn = dialog.findViewById(R.id.barsRBtn);

            if (waveValue.equals("wave")){
                waveRBtn.setChecked(true);
                barsRBtn.setChecked(false);
            }
            else if (waveValue.equals("bars")){
                waveRBtn.setChecked(false);
                barsRBtn.setChecked(true);
            }


            dialogRg.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId){
                    case R.id.waveRBtn:
                        Extras.getInstance().putString(VISUALIZER, "wave");
                        Log.d(TAG, "setVisualizerStyleDialog: WAVE");
                        visualizer.setText("Wave");
                        dialog.dismiss();
                        break;

                    case R.id.barsRBtn:
                        Extras.getInstance().putString(VISUALIZER, "bars");
                        Log.d(TAG, "setVisualizerStyleDialog: BARS");
                        visualizer.setText("Bars");
                        dialog.dismiss();
                        break;
                }
            });

            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
            closeImageBtn.setOnClickListener(v1 -> dialog.dismiss());

        });
    }

    public static SettingsActivity getInstance(){
        return settingsActivity;
    }

    public int audioSessionID() {
        int audioID = MediaPlayerSingleton.getInstance().getMediaPlayer().getAudioSessionId();
        return audioID;
    }

    private void setStopTimer(boolean timerValue, int timeValueCount) {
        if (timerValue) {
            timerLayout.setVisibility(View.VISIBLE);
            timerSwitch.setChecked(true);
            timerPicker.setValue(timeValueCount);
            showCountDown(timeValueCount);
        }
        timerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                timerLayout.setVisibility(View.VISIBLE);
                Extras.getInstance().putBoolean(FADE_TIMER, true);
                showCountDown(timeValueCount);
            }
            else {
                timerLayout.setVisibility(View.GONE);
                Extras.getInstance().putBoolean(FADE_TIMER, false);
            }
        });

        int seconds = timerPicker.getValue();
        Extras.getInstance().putString(TIMER_DURATION, String.valueOf(seconds));
        timerPicker.setValueChangedListener((value1, action) ->{
            Extras.getInstance().putString(TIMER_DURATION, String.valueOf(value1));
            if (countDownTimer != null){
                countDownTimer.cancel();
            }
            showCountDown(value1);
                });

    }

    private void showCountDown(int timeValueCount) {
        switch (timeValueCount){
            case 10:
                COUNTDOWN = 600000;
                break;
            case 20:
                COUNTDOWN = 1200000;
                break;
            case 30:
                COUNTDOWN = 1800000;
                break;
            case 40:
                COUNTDOWN = 2400000;
                break;
            case 50:
                COUNTDOWN = 3000000;
                break;
            case 60:
                COUNTDOWN = 3600000;
                break;
        }
        long timeLeftInMillis = COUNTDOWN;
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTextView(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                closeAudioPlay();
                countDownTimer.cancel();
            }
        }.start();
    }

    private void closeAudioPlay() {
        finishAffinity();
        System.exit(0);
    }

    private void updateTextView(long millisUntilFinished) {
        int minutes = (int) ((millisUntilFinished / 1000) / 60);
        int seconds = (int) ((millisUntilFinished / 1000) % 60);

        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        countDownText.setText(formattedTime);
    }

    private void setCrossFade(boolean value, int numValue) {
        if (value) {
            numberLayout.setVisibility(View.VISIBLE);
            crossFadeSwitch.setChecked(true);
            numberPicker.setValue(numValue);
        }
        crossFadeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked){
                numberLayout.setVisibility(View.VISIBLE);
                Extras.getInstance().putBoolean(FADE_SWITCH, true);
                Extras.getInstance().putBoolean(FADE_TRACK, true);
            }
            else {
                numberLayout.setVisibility(View.GONE);
                Extras.getInstance().putBoolean(FADE_SWITCH, false);
                Extras.getInstance().putBoolean(FADE_TRACK, false);
            }
        });

        int seconds = numberPicker.getValue();
        Extras.getInstance().putString(FADE_IN_OUT_DURATION, String.valueOf(seconds));
        numberPicker.setValueChangedListener((value1, action) ->
                Extras.getInstance().putString(FADE_IN_OUT_DURATION, String.valueOf(value1)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            startActivity(new Intent(SettingsActivity.this, MainActivity.class));
        }
        /*Extras extras = Extras.getInstance();
        switch (item.getItemId()) {
            case R.id.menu_sort_by_az:
                extras.setAlbumSortOrder(SortOrder.AlbumSongSortOrder.SONG_A_Z);
                this.recreate();
                break;
            case R.id.menu_sort_by_za:
                extras.setAlbumSortOrder(SortOrder.AlbumSongSortOrder.SONG_Z_A);
                this.recreate();
                break;
            case R.id.menu_sort_by_duration:
                extras.setAlbumSortOrder(SortOrder.AlbumSongSortOrder.SONG_DURATION);
                this.recreate();
                break;
        }*/
        return super.onOptionsItemSelected(item);
    }

}