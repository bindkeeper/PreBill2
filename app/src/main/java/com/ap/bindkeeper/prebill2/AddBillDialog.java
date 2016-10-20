package com.ap.bindkeeper.prebill2;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by User on 17/10/2016.
 */
public class AddBillDialog extends Dialog {
    public AddBillDialog(final Context context, String billName) {
        super(context);
        setContentView(R.layout.edit_bill_dialog);
        final EditText editText = (EditText) findViewById(R.id.editBillText);

        editText.setText("");
        Button dialogOK = (Button) findViewById(R.id.editBillOkButton);
        Button dailogCancel = (Button) findViewById(R.id.editBillCancelButton);

        dialogOK.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                DBHelper helper = new DBHelper(context);
                helper.addBill(name);
                dismiss();
            }
        });

        dailogCancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
