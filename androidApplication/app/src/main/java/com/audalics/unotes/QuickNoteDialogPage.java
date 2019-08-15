package com.audalics.unotes;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by alexb on 6/2/2017.
 */

public class QuickNoteDialogPage extends Fragment {
    private String title;
    private int page;

    public QuickNoteDialogPage() {}

    public static QuickNoteDialogPage newInstance(int page, int title) {
        QuickNoteDialogPage p = new QuickNoteDialogPage();
        Bundle args = new Bundle();
        args.putInt("pageNumber", page);
        args.putInt("pageTitle", title);
        p.setArguments(args);
        return p;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("pageNumber", 0);
        title = getString(getArguments().getInt("pageTitle", 0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        switch(page) {
            case R.layout.fragment_quick_note_text_page:
                view = inflater.inflate(R.layout.fragment_quick_note_text_page, container);
                break;
            case R.layout.fragment_quick_note_drawing_page:
                view = inflater.inflate(R.layout.fragment_quick_note_text_page, container);
                break;
            case R.layout.fragment_quick_note_other_page:
                view = inflater.inflate(R.layout.fragment_quick_note_other_page, container);
                break;
            default:
                view = inflater.inflate(R.layout.fragment_quick_note_text_page, container);
                break;
        }
        EditText edit = (EditText) view.findViewById(R.id.edit);
        edit.setText("What up doe");
        return view;
    }

    @Override
    public String toString() {
        return title.toString();
    }
}
