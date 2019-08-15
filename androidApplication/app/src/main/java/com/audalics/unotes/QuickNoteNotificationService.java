package com.audalics.unotes;

import android.app.Notification;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by alexb on 5/17/2017.
 */

public class QuickNoteNotificationService extends TileService {

    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private static final String PREFERENCES_KEY = "com.audalics.notes2.notes2";
    private static final int SERVICE_ID = 117;

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override
    public void onClick() {
        toggleTile();
        super.onClick();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        updateTile();
    }

    private void showNotification() {
        startForeground(SERVICE_ID, new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setContentTitle(getString(R.string.qnns_content_title))
                .setContentText(getString(R.string.qnns_content))
                .setSmallIcon(R.drawable.ic_mode_edit_black_24dp)
                .setTicker(getString(R.string.qnns_content_title))
                .build());
    }

    private void hideNotification() {
        stopForeground(true);
        stopSelf();
    }

    private void updateTile() {
        Tile t = getQsTile();
        if (getServiceStatus()) {
            t.setLabel(getString(R.string.qnns_label_active));
            showNotification();
        } else {
            t.setLabel(getString(R.string.qnns_label_inactive));
            hideNotification();
        }
    }

    private void toggleTile() {
        Tile tile = this.getQsTile();
        boolean isActive = toggleServiceStatus();

        Icon newIcon;
        String newLabel;
        int newState;

        // Change the tile to match the service status.
        if (isActive) {

            newLabel = String.format(Locale.US,
                    "%s",
                    getString(R.string.qnns_label_active));

            newIcon = Icon.createWithResource(getApplicationContext(),
                    R.drawable.ic_view_day_black_24dp);

            newState = Tile.STATE_ACTIVE;

            // Set up ongoing notification

            showNotification();

        } else {
            newLabel = String.format(Locale.US,
                    "%s",
                    getString(R.string.qnns_label_inactive));

            newIcon =
                    Icon.createWithResource(getApplicationContext(),
                            R.drawable.ic_view_day_black_24dp);

            newState = Tile.STATE_INACTIVE;

            hideNotification();
        }

        // Change the UI of the tile.
        tile.setLabel(newLabel);
        tile.setIcon(newIcon);
        tile.setState(newState);

        // Need to call updateTile for the tile to pick up changes.
        tile.updateTile();
    }

    private boolean toggleServiceStatus() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
    }

    private boolean getServiceStatus() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        return prefs.getBoolean(SERVICE_STATUS_FLAG, false);
    }
}