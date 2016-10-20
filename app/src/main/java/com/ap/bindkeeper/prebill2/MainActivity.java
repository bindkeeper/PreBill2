package com.ap.bindkeeper.prebill2;


import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BillFragment.OnFragmentInteractionListener, BillsRecyclerAdapter.BillClickedListener{

    DBHelper helper;
    RecyclerView billsRecyclerView;
    BillsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new DBHelper(this);
        billsRecyclerView = (RecyclerView) findViewById(R.id.billsRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        billsRecyclerView.setLayoutManager( linearLayoutManager);
        setAdapter();
    }


    private void setAdapter() {
        adapter = new BillsRecyclerAdapter(new ArrayList<Bill>(helper.getBills()), this);
        billsRecyclerView.setAdapter(adapter);
        billsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menuAddReceipt:

                AddBillDialog dialog = new AddBillDialog(this, "");
                dialog.show();
                //Toast.makeText(MainActivity.this, "Add Clicked", Toast.LENGTH_SHORT).show();
                break;



        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void billClicked(int bill) {
        Intent intent = new Intent(MainActivity.this, BillActivity.class);
        intent.putExtra("billId", bill);
        startActivity(intent);
    }
}
