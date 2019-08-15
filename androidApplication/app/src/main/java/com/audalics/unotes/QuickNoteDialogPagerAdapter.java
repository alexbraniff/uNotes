package com.audalics.unotes;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by alexb on 6/3/2017.
 */

public class QuickNoteDialogPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<QuickNoteDialogPage> pages;

    public QuickNoteDialogPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.pages = new ArrayList<QuickNoteDialogPage>();
        this.pages.add(new QuickNoteDialogPage().newInstance(R.layout.fragment_quick_note_text_page, R.string.qn_dialog_page_title_text_note));
        this.pages.add(new QuickNoteDialogPage().newInstance(R.layout.fragment_quick_note_drawing_page, R.string.qn_dialog_page_title_drawing_note));
        this.pages.add(new QuickNoteDialogPage().newInstance(R.layout.fragment_quick_note_other_page, R.string.qn_dialog_page_title_other_note));
        this.notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int pos) {
        return pages.get(pos);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

}
