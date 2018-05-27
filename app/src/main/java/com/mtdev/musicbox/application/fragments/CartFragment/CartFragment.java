package com.mtdev.musicbox.application.fragments.CartFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mtdev.musicbox.Client.Activities.Login;
import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.activities.MainActivity;
import com.mtdev.musicbox.application.entities.Cart;
import com.mtdev.musicbox.application.fragments.MenuFragment.ProductsFragment;

import static com.mtdev.musicbox.application.SQLiteHandler.paymentPrix;
import static com.mtdev.musicbox.application.activities.MainActivity.db;


public class CartFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    CartRecyclerAdapter adapter;
    CartFragment.confirmCommand mCallback;


    RecyclerView lv;
    LinearLayoutManager mLayoutManager2;

    View bottomMarginLayout;
    ImageView backBtn;
    public ImageView searchIcon;
    public TextView fragTitle, totalP;
    public Button confirmCommandBtn;


    public boolean isSearchboxVisible = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context ctx;
    MainActivity activity;


    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductsFragment newInstance(String param1, String param2) {
        ProductsFragment fragment = new ProductsFragment();
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
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        totalP = (TextView) view.findViewById(R.id.total);
        confirmCommandBtn = (Button) view.findViewById(R.id.confirmCommandAdd);
        confirmCommandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onConfirmCommand();
                db.deleteProducts();
                Intent intent = new Intent(getContext(),
                        MainActivity.class);
                startActivity(intent);
              //  Toast.makeText(getApplicationContext(), "Command successfully sent. Please wait for your order!", Toast.LENGTH_LONG).show();

            }
        });
        // ((MainActivity) getActivity()).onQueryTextChange("");
        isSearchboxVisible = false;

        backBtn = (ImageView) view.findViewById(R.id.local_fragment_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        lv = (RecyclerView) view.findViewById(R.id.localMusicList);
        adapter = new CartRecyclerAdapter(db.getProducts(), getContext());
        mLayoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        lv.setLayoutManager(mLayoutManager2);
        lv.setItemAnimator(new DefaultItemAnimator());
        lv.setAdapter(adapter);
        Log.d(" prix total ! ", paymentPrix + " DT");
        totalP.setText("TOTAL PRICE: " + paymentPrix + " DT");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (CartFragment.confirmCommand) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface confirmCommand {
        // TODO: Update argument type and name
        void onConfirmCommand();
    }
}