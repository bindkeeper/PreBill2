package com.ap.bindkeeper.prebill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {

    ArrayList<Item> items = new ArrayList<>();
    ArrayList<RecyclerAdapter.Holder> holders = new ArrayList<>();
    Context context;

    public RecyclerAdapter(ArrayList<Item> items, Context context) {
        this.items = items;
        this.context = context;

        DataChangedReceiver receiver = new DataChangedReceiver();
        IntentFilter filter = new IntentFilter(DBHelper.dataChanged);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, filter);
    }

    @Override
    public RecyclerAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_layout_2, parent, false);
        Holder holder = new Holder(v);
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.Holder holder, int position) {
        holder.bindText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener, View.OnLongClickListener {

        private TextView name, price;
        private TextView id;

        public Holder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.newItemName);
            price = (TextView) itemView.findViewById(R.id.newItemprice);
            id = (TextView) itemView.findViewById(R.id.newNumber);
            itemView.setOnLongClickListener(this);
        }

        public void bindText (Item item) {
            name.setText(item.getName());
            price.setText(""+item.getPrice());
            id.setText(""+(getAdapterPosition()+1));
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                DBHelper helper = new DBHelper(context);
                switch (v.getId()) {
                    case R.id.itemName:
                        String newName = ((EditText) v).getText().toString();

                        items.get(getAdapterPosition()).setName(newName);

                        helper.updateName(items.get(getAdapterPosition()).getId(), newName);
                        notifyItemChanged(getAdapterPosition());
                        break;
                    case R.id.itemPrice:
                        String newPrice = ((EditText) v).getText().toString();

                        items.get(getAdapterPosition()).setPrice(Float.parseFloat(newPrice));

                        helper.updatePrice(items.get(getAdapterPosition()).getId(), newPrice);
                        notifyItemChanged(getAdapterPosition());
                        break;
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            Item item = items.get(getAdapterPosition());
            EditItemDialog dialog = new EditItemDialog(context, item.getName(), item.getPrice(), item.getId());
            dialog.show();
            return true;
        }
    }

    class DataChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DBHelper helper = new DBHelper(context);
            items = helper.getItems();
            notifyDataSetChanged();
        }
    }
}
