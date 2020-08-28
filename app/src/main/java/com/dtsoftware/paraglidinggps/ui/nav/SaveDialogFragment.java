package com.dtsoftware.paraglidinggps.ui.nav;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.dtsoftware.paraglidinggps.R;

public class SaveDialogFragment extends DialogFragment {

    // Use this instance of the interface to deliver action events
    private SaveDialogListener listener;
    private EditText et_name;


    public interface SaveDialogListener {
        public void onDialogSaveClick(String flightName);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.save_flight_dialog, null);

        et_name = view.findViewById(R.id.et_FlightName);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setTitle("Save Flight?")
                .setPositiveButton(R.string.save_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                       sendBackResult();
                    }
                })
                .setNegativeButton(R.string.discard_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SaveDialogFragment.this.dismiss();
                    }
                });


        return builder.create();

    }


    // Call this method to send the data back to the parent fragment
    public void sendBackResult() {
        // Notice the use of `getTargetFragment` which will be set when the dialog is displayed
        SaveDialogListener listener = (SaveDialogListener) getTargetFragment();
        listener.onDialogSaveClick(et_name.getText().toString());
        dismiss();
    }


}
