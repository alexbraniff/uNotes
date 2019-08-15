package com.audalics.unotes;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import static java.lang.System.in;

/**
 * Created by alexb on 5/6/2017.
 */

public class NoteViewAdapter extends RecyclerView.Adapter<NoteViewAdapter.NoteViewHolder> {

    private Context context;
    private FragmentManager noteManager;
    private TreeMap<Integer, Note> notes;
    private String[] noteUUIDs;
    private static NoteClickListener clickListener;

    public static class NoteViewHolder extends RecyclerView.ViewHolder
                                        implements View.OnClickListener {
        private Context context;
        private CardView cv;
        private TextView title;
        private CheckBox checkbox;
        private TextView description;
        private ProgressBar status;

        NoteViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            cv = (CardView) itemView.findViewById(R.id.noteCardView);
            title = (TextView) itemView.findViewById(R.id.title);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            status = (ProgressBar) itemView.findViewById(R.id.status);
            description = (TextView) itemView.findViewById(R.id.description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onNoteClick(getAdapterPosition(), v);
        }
    }

    NoteViewAdapter(NoteRootNode root, Context context) {
        this.context = context;
        this.noteManager = ((Activity) this.context).getFragmentManager();

        this.notes = root.getNoteMap();
        this.noteUUIDs = new String[0];
        int maxPosition = 0;
        // For each note position found in the treemap ...
        for (Iterator<Integer> i = this.notes.keySet().iterator(); i.hasNext();) {
            int position = i.next();

            if (this.notes.get(position) == null) {
                continue;
            }

            // ... first, update the max position if this Note position is higher ...
            if (position > maxPosition) {
                maxPosition = position;
            }

            // ... then, if the maxPosition won't fit in array, update size and maintain data ...
            if (this.noteUUIDs.length <= maxPosition) {
                String [] tmp = this.noteUUIDs.clone();
                this.noteUUIDs = new String[maxPosition + 1];
                for (int j = 0; j < tmp.length; j++) {
                    this.noteUUIDs[j] = tmp[j];
                }
            }

            // ... finally, insert the Note's UUID in the correct position.
            this.noteUUIDs[position] = this.notes.get(position).getUUID().toString();
        }
    }

    public void setOnItemClickListener(NoteClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public Note getNote(int position) {
        if (this.notes.containsKey(position)) {
            return this.notes.get(position);
        }
        return null;
    }

    public Note insertBefore(Note insert, Note before) {
        int bPos = before.getPosition();
        if (this.notes.containsKey(bPos)) {
            // Update UUID array size and maintain data ...
            String [] tmp = this.noteUUIDs.clone();
            this.noteUUIDs = new String[tmp.length + 1];
            for (int j = 0; j < tmp.length; j++) {
                this.noteUUIDs[j] = tmp[j];
            }

            // For each Note UUID starting at the end...
            int i;
            for (i = this.noteUUIDs.length - 1; i >= bPos; i--) {
                // .. first, skip this index if no UUID exists
                Note cur = this.notes.get(i);
                if (cur == null) {
                    continue;
                }

                // ... then, move Note down the view (move i to i + 1)
                cur = this.notes.remove(i);
                cur.setPosition(i + 1);
                this.notes.put(i + 1, cur);
                this.noteUUIDs[i + 1] = this.noteUUIDs[i];

                // Reset Note at i to make room for next Note
                this.noteUUIDs[i] = null;
            }
            // Insert new Note at old position
            this.noteUUIDs[bPos] = insert.getUUID().toString();
            return this.notes.put(bPos, insert);
        }
        return null;
    }

    public Note insertAfter(Note insert, Note after) {
        int aPos = after.getPosition();
        if (this.notes.containsKey(aPos)) {
            // Update UUID array size and maintain data ...
            String [] tmp = this.noteUUIDs.clone();
            this.noteUUIDs = new String[tmp.length + 1];
            for (int j = 0; j < tmp.length; j++) {
                this.noteUUIDs[j] = tmp[j];
            }

            // For each Note UUID starting at the end...
            int i;
            for (i = this.noteUUIDs.length - 1; i > aPos; i--) {
                // .. first, skip this index if no UUID exists
                Note cur = this.notes.get(i);
                if (cur == null) {
                    continue;
                }

                // ... then, move Note down the view (move i to i + 1)
                cur.setPosition(i + 1);
                this.notes.put(i + 1, cur);
                this.noteUUIDs[i + 1] = this.noteUUIDs[i];

                // Reset Note at i to make room for next Note
                this.noteUUIDs[i] = null;
            }
            // Insert new Note at last moved index
            this.noteUUIDs[i] = insert.getUUID().toString();
            return this.notes.put(i, insert);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_row, viewGroup, false);
        NoteViewHolder nvh = new NoteViewHolder(v);
        return nvh;
    }

    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        Note n = this.notes.get(position);
        holder.title.setText(n.getTitle());
        holder.description.setText(n.getDescription());
        holder.checkbox.setChecked(n.getChecked());
        holder.status.getProgressDrawable().setColorFilter(n.getStatus().getColor(), PorterDuff.Mode.MULTIPLY);
        n.setContext(holder.context);
        n.setPosition(position);
        n.setCardView(holder.cv);
        n.setTitleView(holder.title);
        n.setDescriptionView(holder.description);
        n.setCheckboxView(holder.checkbox);
        n.setStatusView(holder.status);
        this.noteUUIDs[position] = n.getUUID().toString();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void hideNote(String uuid) {
        if (this.notes.containsKey(uuid)) {
            this.notes.get(uuid).hide(true);
        }
    }
}
