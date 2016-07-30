package com.ap.bindkeeper.prebill;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DBHelper helper;
    RecyclerAdapter adapter;
    EditText editName, editPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager( linearLayoutManager);

        helper = new DBHelper(this);

        editName = (EditText) findViewById(R.id.editNewName);
        editPrice = (EditText) findViewById(R.id.editNewPrice);

        setAdapter();
        findViewById(R.id.btnEnter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addItem();
            }
        });

        calcSum();
        IntentFilter filter = new IntentFilter(DBHelper.dataChanged);
        RecalcSum recalcSum = new RecalcSum();
        LocalBroadcastManager.getInstance(this).registerReceiver(recalcSum, filter);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            case R.id.settings:
//                break;

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
                helper.delAll();
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

    private void setAdapter() {
        adapter = new RecyclerAdapter(helper.getItems(), this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void addItem() {
        String name = editName.getText().toString();
        if (name.length() == 0) {
            Toast.makeText(MainActivity.this, "Please Enter Item Name", Toast.LENGTH_SHORT).show();
            return;
        }
        float price;
        String priceStr = editPrice.getText().toString();
        if (priceStr.length() == 0) {
            Toast.makeText(MainActivity.this, "Please Enter Item Price", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            price = Float.parseFloat(priceStr);
        } catch (Exception e) {
            price = 0.0f;
        }
        DBHelper helper = new DBHelper(this);
        helper.addItem(new Item(name , price));
        editName.setText("");
        editPrice.setText("");
    }

    private void calcSum() {
        ArrayList<Item> items = helper.getItems();
        int size = items.size();
        float sum = 0.0f;
        for ( int i = 0; i < size; i++) {
            float price = items.get(i).getPrice();
            sum = sum + price;
        }
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(2);

        ((TextView)findViewById(R.id.txt_sum)).setText(""+formatter.format(sum));
    }

    public class RecalcSum extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            calcSum();
        }
    }
}
