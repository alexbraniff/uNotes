package com.audalics.unotes;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.*;
import java.util.TreeMap;

/**
 * Created by alexb on 6/14/2017.
 */

public class NoteNode implements Node {
    private Note noteData;
    private Node parent;
    private List<NoteNode> children;

    public NoteNode(Node parent) {
        this(null, parent);
    }

    public NoteNode(Note noteData, Node parent) {
        super();
        this.noteData = noteData;
        this.parent = parent;
        this.noteData.setParent(this.parent);
    }

    public TreeMap<Integer, Note> getNoteMap() {
        TreeMap<Integer, Note> noteMap = new TreeMap<>();
        if (this.children != null) {
            for (NoteNode nn : this.children) {
                TreeMap<Integer, Note> childMap = nn.getNoteMap();
                for (int cnPos : childMap.keySet()) {
                    noteMap.put(cnPos, childMap.get(cnPos));
                }
                Note n;
                try {
                    n = (Note) nn.getData();
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), "getNoteMap: ", e);
                    continue;
                }
                noteMap.put(n.getPosition(), n);
            }
        }
        return noteMap;
    }

    @Override
    public boolean equals(Object obj) {
        switch (obj.getClass().getName()) {
            case "NoteNode":
                NoteNode n = (NoteNode) obj;
                return (((Note) n.getData()).getUUID() == ((Note) this.getData()).getUUID());
            default:
                return false;
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public Node getParent() {
        return null;
    }

    @Override
    public void setParent(Node parent) {
        this.parent = (NoteNode) parent;
        this.noteData.setParent(this.parent);
    }

    @Override
    public List<Node> getChildren() {
        return (List) this.children;
    }

    @Override
    public Object getData() {
        return this.noteData;
    }

    @Override
    public void setData(Object data) {
        if (data.getClass().getName() == "Note") {
            this.noteData = (Note) data;
        }
    }

    @Override
    public void setChildren(List children) {
        try {
            this.children = (ArrayList<NoteNode>) children;
        } catch (Exception e) {
            Log.e(this.getClass().getName(), "Invalid type for children", e);
        }
    }
}
