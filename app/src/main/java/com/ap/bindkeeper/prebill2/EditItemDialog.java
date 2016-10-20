package com.ap.bindkeeper.prebill2;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 *
 */
public class EditItemDialog extends Dialog {
    public EditItemDialog(final Context context, String currentName, float currentPrice, int id) {
        super(context);
        setContentView(R.layout.edit_item_dialog);
        final int cid = id;


        final EditText editText = (EditText) findViewById(R.id.editDialogText);
        editText.setText(currentName);
        Button dialogOK = (Button) findViewById(R.id.editDialogButtonOK);



        final EditText dialogPrice = (EditText) findViewById(R.id.editDialogPrice);
        dialogPrice.setText(""+currentPrice);
        Button dailogCancel = (Button) findViewById(R.id.editDialogCancel);
        Button dialogDelete = (Button) findViewById(R.id.editDialogDelete);

        dialogOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editText.getText().toString();
                float price;
                try {
                    price = Float.parseFloat(dialogPrice.getText().toString());
                } catch (Exception e) {
                    price = 0.0f;
                }
                DBHelper helper = new DBHelper(context);
                helper.updateItem(cid, name, ""+price);
                dismiss();
            }
        });
        dailogCancel.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dialogDelete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DBHelper helper = new DBHelper(context);
                helper.removeItem(cid);
                dismiss();
            }
        });
    }
}
