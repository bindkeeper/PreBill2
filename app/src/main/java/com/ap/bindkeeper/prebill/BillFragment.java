package com.ap.bindkeeper.prebill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BillFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BillFragment#newBillFragmentInstance} factory method to
 * create an instance of this fragment.
 */
public class BillFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView recyclerView;
    DBHelper helper;
    RecyclerAdapter adapter;
    EditText editName, editPrice;
    TextView txt_sum;

    private OnFragmentInteractionListener mListener;

    public BillFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillFragment newBillFragmentInstance(String param1, String param2) {
        BillFragment fragment = new BillFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bill, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager( linearLayoutManager);

        helper = new DBHelper(getContext());
        txt_sum = ((TextView) view.findViewById(R.id.txt_sum));
        editName = (EditText) view.findViewById(R.id.editNewName);
        editPrice = (EditText) view.findViewById(R.id.editNewPrice);

        setAdapter();
        view.findViewById(R.id.btnEnter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        calcSum();
        IntentFilter filter = new IntentFilter(DBHelper.dataChanged);
        RecalcSum recalcSum = new RecalcSum();
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(recalcSum, filter);



        return view;
    }

    private void setAdapter() {
        adapter = new RecyclerAdapter(helper.getItems(), getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
    }


    private void addItem() {
        String name = editName.getText().toString();
        if (name.length() == 0) {
            Toast.makeText(getContext(), "Please Enter Item Name", Toast.LENGTH_SHORT).show();
            return;
        }
        float price;
        String priceStr = editPrice.getText().toString();
        if (priceStr.length() == 0) {
            Toast.makeText(getContext(), "Please Enter Item Price", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            price = Float.parseFloat(priceStr);
        } catch (Exception e) {
            price = 0.0f;
        }
        DBHelper helper = new DBHelper(getContext());
        helper.addItem(new Item(name , price));
        editName.setText("");
        editPrice.setText("");
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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

        txt_sum.setText(""+formatter.format(sum));
    }

    public class RecalcSum extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            calcSum();
        }
    }
}
