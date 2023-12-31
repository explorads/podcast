package com.explorads.podcast.asynctask;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.explorads.podcast.core.export.ExportWriter;
import com.explorads.podcast.core.storage.DBReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import com.explorads.podcast.storage.preferences.UserPreferences;

import io.reactivex.Observable;

/**
 * Writes an OPML file into the export directory in the background.
 */
public class ExportWorker {

    private static final String EXPORT_DIR = "export/";
    private static final String TAG = "ExportWorker";
    private static final String DEFAULT_OUTPUT_NAME = "antennapod-feeds";

    private final @NonNull ExportWriter exportWriter;
    private final @NonNull File output;
    private final Context context;

    public ExportWorker(@NonNull ExportWriter exportWriter, Context context) {
        this(exportWriter, new File(UserPreferences.getDataFolder(EXPORT_DIR),
                DEFAULT_OUTPUT_NAME + "." + exportWriter.fileExtension()), context);
    }

    private ExportWorker(@NonNull ExportWriter exportWriter, @NonNull File output, Context context) {
        this.exportWriter = exportWriter;
        this.output = output;
        this.context = context;
    }

    public Observable<File> exportObservable() {
        if (output.exists()) {
            boolean success = output.delete();
            Log.w(TAG, "Overwriting previously exported file: " + success);
        }
        return Observable.create(subscriber -> {
            OutputStreamWriter writer = null;
            try {
                writer = new OutputStreamWriter(new FileOutputStream(output), Charset.forName("UTF-8"));
                exportWriter.writeDocument(DBReader.getFeedList(), writer, context);
                subscriber.onNext(output);
            } catch (IOException e) {
                subscriber.onError(e);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                }
                subscriber.onComplete();
            }
        });
    }

}
