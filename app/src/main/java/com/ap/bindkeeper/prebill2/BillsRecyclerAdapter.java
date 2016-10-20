package com.ap.bindkeeper.prebill2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by User on 06/10/2016.
 */
public class BillsRecyclerAdapter extends RecyclerView.Adapter<BillsRecyclerAdapter.Holder> {
    ArrayList<Bill> bills = new ArrayList<>();
    ArrayList<BillsRecyclerAdapter.Holder> holders = new ArrayList<>();
    Context context;

    public BillsRecyclerAdapter(ArrayList<Bill> bills, Context context) {
        this.bills = bills;
        this.context = context;

        BillsChangedReceiver receiver = new BillsChangedReceiver();
        IntentFilter filter = new IntentFilter(DBHelper.billsChanged);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);

    }

    @Override
    public BillsRecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.bills_layout, parent, false);
        Holder holder = new Holder(v);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.bindText(bills.get(position));
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }


    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView name , idText;
        public Holder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.billName);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bindText(Bill bill) {
            name.setText(bill.getBillName());


        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            ((BillClickedListener)context).billClicked(bills.get(pos).getId());

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    public interface BillClickedListener {
        void billClicked(int bill);
    }

    class BillsChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DBHelper helper = new DBHelper(context);
            bills = helper.getBills();
            notifyDataSetChanged();
        }
    }
}
