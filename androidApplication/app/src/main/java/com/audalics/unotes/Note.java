package com.audalics.unotes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by alexb on 5/14/2017.
 */

public class Note extends Fragment {

    // Private properties
    private StatusDataSource statusDS;
//    private NoteDataSource noteDS;
//    private TagDataSource tagDS;
    private UUID _uuid;

    private Context _context;

    private NoteViewAdapter _adapter;

    private NoteRootNode _root;
    private NoteNode _node;
    private Node _parent;

    private int _position;

    private String _title;
    private String _description;
    private boolean _bChecked;
    private Status _status;
    private TreeMap<String, Note> _children;
    private List<Tag> _tags;

    // View elements
    private CardView _Card;

    private TextView _Title;
    private TextView _Description;
    private CheckBox _Checkbox;
    private ProgressBar _Status;

    public Note() {}

    public static Note newInstance(NoteNode self, Node parent, UUID uuid, String title, String description, boolean checked, Status status, TreeMap<String, Note> children, ArrayList<Tag> tags) {
        Note n = new Note();
        n._node = self;
        n._parent = parent;
        n._uuid = uuid;
        n._title = title;
        n._description = description;
        n._bChecked = checked;
        n._status = status;
        n._children = children;
        n._tags = tags;

        Bundle args = new Bundle();
        args.putString("uuid", uuid.toString());
        args.putString("title", title);
        args.putString("description", description);
        args.putLong("statusID", status.getId());
        if (parent instanceof NoteNode) {
            args.putString("parentUUID", parent != null ? (((Note) parent.getData()).getUUID() != null ? ((Note) parent.getData()).getUUID().toString() : "") : "");
        } else {
            args.putString("parentUUID", "");
        }
        args.putStringArray("childUUIDs", children != null ? Arrays.copyOf(children.keySet().toArray(), children.keySet().size(), String[].class) : new String[]{});

        ArrayList<Long> tagIdList = new ArrayList<>();
        for (Tag t : tags) {
            tagIdList.add(t.getId());
        }
        Long[] tagIds = tagIdList.toArray(new Long[tagIdList.size()]);
        long[] tagIdsPrimitive = new long[tagIds.length];
        for (int i = 0; i < tagIds.length; i++) {
            tagIdsPrimitive[i] = tagIds[i];
        }

        args.putLongArray("tagIDs", tagIdsPrimitive);
        n.setArguments(args);
        return n;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.statusDS = new StatusDataSource(this.getActivity());
        this.statusDS.open(true);

        try {
            this._uuid = UUID.fromString(getArguments().getString("uuid"));
        } catch(Exception e) {
            Log.e(this.getClass().getName(), "onCreate: ", e);
        }
        String title = getArguments().getString("title");
        this._title = title;
        this._description = getArguments().getString("description");
        this._bChecked = getArguments().getBoolean("bChecked");
        this._status = this.lookupStatus(getArguments().getInt("statusID"));
        String parentUUIDString = getArguments().getString("parentUUID");
        if (parentUUIDString != "") {
            try {
                UUID parentUUID = UUID.fromString(parentUUIDString);
                Note parentNote = this.lookupNote(parentUUID);
                if (parentNote != null) {
                    this._parent = parentNote.getSelfNode();
                }
            } catch (Exception e) {
                Log.e(this.getClass().getName(), "onCreate: ", e);
            }
        }
        String[] childUUIDs = getArguments().getStringArray("childUUIDs");
        this._children = new TreeMap<>();
        for (String uuidString : childUUIDs) {
            UUID uuid;
            try {
                uuid = UUID.fromString(uuidString);
            } catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
                continue;
            }
            this._children.put(uuidString, this.lookupNote(uuid));
        }
        long[] tagIDs = getArguments().getLongArray("tagIDs");
        this._tags = new ArrayList<>();
        for (long id : tagIDs) {
            Tag tag = this.lookupTag(id);
            if (tag != null) {
                this._tags.add(tag);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_row, container);
        this._Card = (CardView) view.findViewById(R.id.noteCardView);
        this._Title = (TextView) view.findViewById(R.id.title);
        this._Description = (TextView) view.findViewById(R.id.description);
        this._Checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        this._Status = (ProgressBar) view.findViewById(R.id.status);
        return view;
    }

    @Override
    public String toString() {
        return _title != null ? _title : "Note " + String.valueOf(this.getPosition());
    }

    // Private Methods
    private Status lookupStatus(long id) {
        return statusDS.readStatus(id);
    }

    private Note lookupNote(UUID uuid) {
        return null;
    }

    private Tag lookupTag(long id) {
        return null;
    }

    // Public Methods
    public void hide(boolean hideChildren) {
        if (hideChildren) {
            this.hideChildren();
        } else {
            this.showChildren();
        }
        this._Card.setVisibility(View.GONE);
    }
    public void show(boolean showChildren) {
        if (showChildren) {
            this.showChildren();
        } else {
            this.hideChildren();
        }
        this._Card.setVisibility(View.VISIBLE);
    }
    public void toggle(boolean showChildren) {
        if (this._Card.getVisibility() == View.GONE) {
            this.show(showChildren);
        } else {
            this.hide(!showChildren);
        }
    }
    public void hideChildren() {
        for (TreeMap.Entry<String, Note> noteSet : this._children.entrySet()) {
            noteSet.getValue().hide(true);
        }
    }
    public void showChildren() {
        for (TreeMap.Entry<String, Note> noteSet : this._children.entrySet()) {
            noteSet.getValue().show(true);
        }
    }
    public boolean hasChild(Note child) {
        UUID childUUID = child.getUUID();
        if (!this._children.containsKey(childUUID.toString())) {
            return true;
        }
        return false;
    }
    public boolean hasChild(UUID childUUID) {
        if (this._children.containsKey(childUUID.toString())) {
            return true;
        }
        return false;
    }
    public boolean hasChild(String childUUIDString) {
        try {
            if (UUID.fromString(childUUIDString).toString() == childUUIDString && this._children.containsKey(childUUIDString)) {
                return true;
            }
        } catch (Exception e) {
            // Log error and fall through
            Log.d(this.getClass().getName(), "Did not find child", e);
        }
        return false;
    }
    public void addChild(Note child) {
        UUID childUUID = child.getUUID();
        if (!this.hasChild(childUUID)) {
            Note n = this._children.put(childUUID.toString(), child);
            if (this._adapter != null) {
                this._adapter.insertAfter(n, this);
            }
        }
    }
    public Note removeChild(Note child) {
        UUID childUUID = child.getUUID();
        if (this.hasChild(childUUID)) {
            return this._children.remove(childUUID.toString());
        }
        return null;
    }
    public Note removeChild(UUID childUUID) {
        if (this.hasChild(childUUID)) {
            return this._children.remove(childUUID.toString());
        }
        return null;
    }
    public Note removeChild(String childUUID) {
        if (this.hasChild(childUUID)) {
            return this._children.remove(childUUID);
        }
        return null;
    }
    public void updatePosition() {
    }

    // Accessors
    public NoteNode getSelfNode() {
        return this._node;
    }
    public int getPosition() {
        return this._position;
    }
    public UUID getUUID() { return this._uuid; }
    public String getTitle() { return this._title; }
    public String getDescription() { return this._description; }
    public Boolean getChecked() { return this._bChecked; }
    public Status getStatus() { return this._status; }
    public Node getParent() { return this._parent; }
    public TreeMap<String, Note> getChildren() { return this._children; }
    public List<Tag> getTags() { return this._tags; }
    public CardView getCardView() { return this._Card; }
    public TextView getTitleView() { return this._Title; }
    public TextView getDescriptionView() { return this._Description; }
    public CheckBox getCheckboxView() { return this._Checkbox; }
    public ProgressBar getStatusView() { return this._Status; }
    public int getIndent() {
        Node current = this._parent;
        int indent = 0;
        while (!(current instanceof NoteRootNode)) {
            indent++;
            current = current.getParent();
        }
        return indent;
    }

    // Mutators
    public void setPosition(int position) { this._position = position; }
    public void setSelfNode(NoteNode self) {
        this._node = self;
    }
    public void setRoot (NoteRootNode root) {
        this._root = root;
    }
    public void setContext(Context context) { this._context = context; }
    public void setAdapter(NoteViewAdapter adapter) { this._adapter = adapter; }
    public void setCardView(CardView cardView) {
        this._Card = cardView;
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) this._Card.getLayoutParams();
        int lMargin = this._context.getResources().getInteger(R.integer.note_indent_default) + (this.getIndent() * this._context.getResources().getInteger(R.integer.note_indent_additional));
        DisplayMetrics display = new DisplayMetrics();
        ((Activity) this._context).getWindowManager().getDefaultDisplay().getMetrics(display);
        p.width = display.widthPixels - ((lMargin + p.rightMargin) * 2);
        p.setMargins(lMargin, p.topMargin, p.rightMargin, p.bottomMargin);

        GradientDrawable border = new GradientDrawable();
        border.setStroke(1, ContextCompat.getColor(this._context, R.color.colorPrimary));
        border.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        Drawable[] layers = {border};
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        layerDrawable.setLayerInset(0, 1, -2, -2, -2);
        this._Card.setBackground(layerDrawable);
    }
    public void setTitleView(TextView title) {
        SpannableString ss = new SpannableString(title.getText());
        ss.setSpan(new NoteLeadingMarginSpan2(1, 240), 0, title.getText().length(), 0);
        title.setText(ss);
        this._Title = title;
    }
    public void setDescriptionView(TextView desc) {
        this._Description = desc;
    }
    public void setStatusView(ProgressBar status) {
        this._Status = status;
    }
    public void setCheckboxView(CheckBox checkbox) {
        this._Checkbox = checkbox;
    }
    public void setTitle(String title) {
        this._title = title;
        SpannableString ss = new SpannableString(title);
        ss.setSpan(new NoteLeadingMarginSpan2(1, 78), 0, title.length(), 0);
        this._Title.setText(ss);
    }
    public void setDescription(String desc) {
        this._description = desc;
        this._Description.setText(desc);
    }
    public void setChecked(boolean checked) {
        this._bChecked = checked;
        this._Checkbox.setChecked(checked);
    }
    public void setStatus(Status status) {
        this._status = status;
        this._Status.getProgressDrawable().setColorFilter(status.getColor(), PorterDuff.Mode.MULTIPLY);
        this._Status.setProgress(status.getProgress());
    }
    public void setParent(Node parent) {
        this._parent = parent;
    }
    public void setChildren(TreeMap<String, Note> children) {
        this._children = children;
    }
}
