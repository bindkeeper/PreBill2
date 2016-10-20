package com.ap.bindkeeper.prebill2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class BillActivity extends AppCompatActivity implements BillFragment.OnFragmentInteractionListener{

    int billId;
    DBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        helper = new DBHelper(this);
        billId = intent.getIntExtra("billId", 0);
        setContentView(R.layout.activity_bill);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bill_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDelete:
                deleteCurrentReceipt();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCurrentReceipt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                helper.delAll(billId);
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel selected
            }
        });

        builder.setMessage(R.string.are_you_sure)
                .setTitle(R.string.delete_bill_dialog_title);
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public int getBillId() {
        return billId;
    }
}
