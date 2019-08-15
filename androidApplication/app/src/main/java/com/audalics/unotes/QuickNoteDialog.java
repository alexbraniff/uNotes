package com.audalics.unotes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by alexb on 5/29/2017.
 */

public class QuickNoteDialog extends DialogFragment {

    public static final String TILE_STATE_KEY = "tileState";

    private Context _context;
    private QuickNoteDialogListener _listener;

    /**
     * An inner class used to pass context into the dialog.
     */
    public static class Builder {

        private Context _context;
        private QuickNoteDialogListener _listener;

        public Builder(Context context){
            this._context = context;
        }

        public Builder setClickListener(QuickNoteDialogListener listener) {
            if (listener instanceof QuickNoteDialogListener) {
                this._listener = listener;
            }
            return this;
        }

        public QuickNoteDialog create() {
            QuickNoteDialog dialog = new QuickNoteDialog().setContext(this._context).setClickListener(this._listener);
            return dialog;
        }
    }

    /**
     * A public interface for communication between the
     * dialog and the QSDialogService.
     */
    public interface QuickNoteDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    public QuickNoteDialog() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        _context = context;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedState){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this._context);
        LayoutInflater inflater = LayoutInflater.from(_context);

        View view = inflater.inflate(R.layout.dialog_quicknote, null);

        AlertDialog dialog = alertBuilder
            .setView(view)

            // OnAttach doesn't get called on the dialog;
            // we have to apply our click event handlers here.
            .setNegativeButton(R.string.qn_dialog_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("QS", "Dialog cancel");
                            _listener.onDialogNegativeClick(QuickNoteDialog.this);
                        }
                    })
            .setPositiveButton(R.string.qn_dialog_save,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.d("QS", "Dialog action taken");
                            dialog.dismiss();

                            _listener.onDialogPositiveClick(QuickNoteDialog.this);
                        }
                    })
            .create();

        return  dialog;
    }

    private QuickNoteDialog setClickListener(QuickNoteDialogListener listener) {
        this._listener = listener;
        return this;
    }

    private QuickNoteDialog setContext(Context context){
        this._context = context;
        return this;
    }
}
