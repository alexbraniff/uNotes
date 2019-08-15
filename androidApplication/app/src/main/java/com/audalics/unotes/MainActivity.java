package com.audalics.unotes;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import static android.content.pm.PermissionInfo.PROTECTION_NORMAL;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private StatusDataSource statusDS;

    FloatingActionButton addTop;
    FloatingActionButton addAbove;
    FloatingActionButton addBelow;
    FloatingActionButton addBottom;

    private RecyclerView rv;
    private CardView cv;
    private NoteRootNode noteTreeRoot;
    private NoteViewAdapter nva;
    private RecyclerView.LayoutManager lm;

    private int selectedNoteIndex = -1;
    private Note selectedNote;

    private final int REQUEST_PERMISSION_BIND_QUICK_SETTINGS_TILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = (RecyclerView) findViewById(R.id.rv);
        lm = new LinearLayoutManager(this);
        rv.setLayoutManager(lm);

        addTop = (FloatingActionButton) findViewById(R.id.fabAddTop);
        addAbove = (FloatingActionButton) findViewById(R.id.fabAddAbove);
        addBelow = (FloatingActionButton) findViewById(R.id.fabAddBelow);
        addBottom = (FloatingActionButton) findViewById(R.id.fabAddBottom);

        addAbove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Get all statuses
        statusDS = new StatusDataSource(this);
        statusDS.open(false);

        List<Status> statuses = statusDS.getAllStatuses();

        Status sNew;
        Status sUrgent;
        Status sNormal;
        if (statuses.size() == 0) {
            sNew = statusDS.createStatus("New Status", "Status for new notes", Color.BLUE, 10);
            sUrgent = statusDS.createStatus("Urgent Status", "Status for notes that need attention immediately", Color.RED, 90);
            sNormal = statusDS.createStatus("Normal Status", "Status for notes of normal priority", Color.GREEN, 35);
        } else {
            sNew = statuses.get(0);
            sUrgent = statuses.get(1);
            sNormal = statuses.get(2);
            statusDS.updateStatus(sNew.getId(), sNew.getName(), sNew.getDescription(), Color.WHITE, 10);
            statusDS.updateStatus(sUrgent.getId(), sUrgent.getName(), sUrgent.getDescription(), Color.RED, 95);
            statusDS.updateStatus(sNormal.getId(), sNormal.getName(), sNormal.getDescription(), Color.GREEN, 35);
            sNew = statuses.get(0);
            sUrgent = statuses.get(1);
            sNormal = statuses.get(2);
        }

        noteTreeRoot = new NoteRootNode();
        noteTreeRoot.setData(new NoteRoot());

        ArrayList<NoteNode> noteTreeRootChildren = new ArrayList<>();

        NoteNode noteNode1 = new NoteNode(noteTreeRoot);
        Note note1 = Note.newInstance(noteNode1, noteTreeRoot, UUID.randomUUID(), "Note 1", "This is Note 1", false, sNew, new TreeMap<String, Note>(), new ArrayList<Tag>());
        noteTreeRootChildren.add(noteNode1);

        NoteNode noteNode2 = new NoteNode(noteNode1);
        Note note2 = Note.newInstance(noteNode2, noteNode1, UUID.randomUUID(), "Note 2", "This is the first child of Note 1", false, sNew, new TreeMap<String, Note>(), new ArrayList<Tag>());
        noteNode2.setData(noteNode2);
        noteTreeRootChildren.add(noteNode2);
        note1.addChild(note2);

        NoteNode noteNode3 = new NoteNode(noteNode2);
        Note note3 = Note.newInstance(noteNode3, noteNode2, UUID.randomUUID(), "Note 3", "This is the first child of Note 2", false, sNew, new TreeMap<String, Note>(), new ArrayList<Tag>());
        noteNode3.setData(noteNode2);
        noteTreeRootChildren.add(noteNode3);
        note2.addChild(note3);

        noteTreeRoot.setChildren(noteTreeRootChildren);

        nva = new NoteViewAdapter(noteTreeRoot, this);

        rv.setAdapter(nva);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void showExplanation(String title, String message, final String permission, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_BIND_QUICK_SETTINGS_TILE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final MainActivity self = this;
        nva.setOnItemClickListener(new NoteClickListener() {
            @Override
            public void onNoteClick(int position, View v) {
                self.toggleSelectedNote(position);
            }
        });
    }

    public void toggleSelectedNote(int position) {
        if (this.selectedNoteIndex == position || this.selectedNote == null) {
            this.selectedNoteIndex = position;
            this.selectedNote = nva.getNote(position);

            TextView title = this.selectedNote.getTitleView();
            TextView desc = this.selectedNote.getDescriptionView();
            if (desc.getVisibility() == GONE) {
                this.selectedNote.showChildren();
                desc.setVisibility(VISIBLE);
                title.setMaxLines(Integer.MAX_VALUE);
                CardView cv = this.selectedNote.getCardView();
                cv.setCardBackgroundColor(this.getResources().getColor(R.color.colorGrey));
                if (position > 0)
                    addAbove.setVisibility(VISIBLE);
                else
                    addAbove.setVisibility(GONE);
                if (position < nva.getItemCount() - 1)
                    addBelow.setVisibility(VISIBLE);
                else
                    addBelow.setVisibility(GONE);
            } else {
                this.selectedNote.hideChildren();
                desc.setVisibility(GONE);
                title.setLines(1);
                CardView cv = this.selectedNote.getCardView();
                cv.setCardBackgroundColor(this.getResources().getColor(R.color.colorGreyDark));
                addAbove.setVisibility(GONE);
                addBelow.setVisibility(GONE);
            }
        } else {
            TextView title = this.selectedNote.getTitleView();
            TextView desc = this.selectedNote.getDescriptionView();
            CardView cv = this.selectedNote.getCardView();
            cv.setCardBackgroundColor(this.getResources().getColor(R.color.colorGreyDark));

            this.selectedNoteIndex = position;
            this.selectedNote = nva.getNote(position);

            this.selectedNote.showChildren();
            title = this.selectedNote.getTitleView();
            desc = this.selectedNote.getDescriptionView();
            desc.setVisibility(VISIBLE);
            title.setMaxLines(Integer.MAX_VALUE);
            cv = this.selectedNote.getCardView();
            cv.setCardBackgroundColor(this.getResources().getColor(R.color.colorGrey));
            if (position > 0)
                addAbove.setVisibility(VISIBLE);
            else
                addAbove.setVisibility(GONE);
            if (position < nva.getItemCount() - 1)
                addBelow.setVisibility(VISIBLE);
            else
                addBelow.setVisibility(GONE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
