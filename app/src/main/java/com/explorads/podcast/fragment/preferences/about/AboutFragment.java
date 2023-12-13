package com.explorads.podcast.fragment.preferences.about;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import com.explorads.podcast.BuildConfig;
import com.explorads.podcast.R;
import com.explorads.podcast.core.util.IntentUtils;
import com.google.android.material.snackbar.Snackbar;

import com.explorads.podcast.activity.PreferenceActivity;

public class AboutFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_about);

        findPreference("about_version").setSummary(String.format(
                "%s", BuildConfig.VERSION_NAME));
        findPreference("about_privacy_policy").setOnPreferenceClickListener((preference) -> {
//            IntentUtils.openInBrowser(getContext(), "https://antennapod.org/privacy/");
            IntentUtils.openInBrowser(getContext(), "https://hiddenpod.org/privacy/");
            return true;
        });
        findPreference("about_licenses").setOnPreferenceClickListener((preference) -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.settingsContainer, new LicensesFragment())
                    .addToBackStack(getString(R.string.translators)).commit();
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((PreferenceActivity) getActivity()).getSupportActionBar().setTitle(R.string.about_pref);
    }
}
