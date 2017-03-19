package laurenzsoft.com.efficiencymanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Laurenz on 01.03.2017.
 */

public class NewPageDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.page_dialog, null);
        final EditText pageNum = (EditText) view.findViewById(R.id.pageDialogEditText);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DataBaseHolder.getInstance(getActivity().getApplicationContext()).setPage(getArguments().getInt("bookId"), Integer.parseInt("0" + pageNum.getText().toString()));
                Log.d("Wow dialog worked", "Dialog worked");
                Intent intent = new Intent(getActivity(), WorkScreen.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
