package com.audalics.unotes;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import static com.audalics.unotes.R.drawable.ic_android_black_24dp;

/**
 * Created by alexb on 5/17/2017.
 */

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.N)
public class QuickNoteService extends TileService {

    @Override
    public void onTileAdded() {
        Toast.makeText(this, "Tile Added!", Toast.LENGTH_LONG);
        super.onTileAdded();
    }

    @Override
    public void onStartListening() {
        Toast.makeText(this, "Tile Listening!", Toast.LENGTH_LONG);
        super.onStartListening();
    }

    @Override
    public void onClick() {
        QuickNoteDialog.Builder dialogBuilder =
                new QuickNoteDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme));

        QuickNoteDialog dialog = dialogBuilder
                .setClickListener(new QuickNoteDialog.QuickNoteDialogListener() {

                    @Override
                    public void onDialogPositiveClick(DialogFragment dialog) {
                        Log.d("QS", "Positive registed");
                        createNewNote();
                    }

                    @Override
                    public void onDialogNegativeClick(DialogFragment dialog) {
                        Log.d("QS", "Negative registered");

                        // The user is cancelled the dialog box.
                        // We can't do anything to the dialog box here,
                        // but we can do any cleanup work.
                    }
                })
                .create();

        Bundle args = new Bundle();
        showDialog(dialog.onCreateDialog(args));

        super.onClick();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Toast.makeText(this, "Tile Removed!", Toast.LENGTH_LONG);
    }

    private void createNewNote() {
    }
}