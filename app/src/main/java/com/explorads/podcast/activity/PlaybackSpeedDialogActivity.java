package com.explorads.podcast.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import com.explorads.podcast.core.preferences.ThemeSwitcher;
import com.explorads.podcast.dialog.VariableSpeedDialog;

public class PlaybackSpeedDialogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(ThemeSwitcher.getTranslucentTheme(this));
        super.onCreate(savedInstanceState);
        VariableSpeedDialog speedDialog = new InnerVariableSpeedDialog();
        speedDialog.show(getSupportFragmentManager(), null);
    }

    public static class InnerVariableSpeedDialog extends VariableSpeedDialog {
        @Override
        public void onDismiss(@NonNull DialogInterface dialog) {
            super.onDismiss(dialog);
            getActivity().finish();
        }
    }
}
