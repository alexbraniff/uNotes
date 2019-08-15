package com.audalics.unotes;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by alexb on 6/14/2017.
 */

public class NoteRootNode implements Node {
    private NoteRoot noteRootData;
    private ArrayList<NoteNode> children = new ArrayList<>();

    public NoteRootNode() {
        super();
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
            case "NoteRootNode":
                NoteRootNode n = (NoteRootNode) obj;
                return (((NoteRoot) n.getData())._UUID == ((NoteRoot) this.getData())._UUID);
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
    public Node getParent() { return null; }

    @Override
    public void setParent(Node parent) {

    }

    @Override
    public List<Node> getChildren() {
        return null;
    }

    @Override
    public Object getData() {
        return this.noteRootData;
    }

    @Override
    public void setData(Object data) {
        if (data.getClass().getName() == "NoteRoot") {
            this.noteRootData = (NoteRoot) data;
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
