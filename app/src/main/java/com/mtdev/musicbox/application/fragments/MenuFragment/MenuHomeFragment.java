package com.mtdev.musicbox.application.fragments.MenuFragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mtdev.musicbox.Client.Utils.ClickItemTouchListener;
import com.mtdev.musicbox.R;
import com.mtdev.musicbox.application.activities.MainActivity;
import com.mtdev.musicbox.application.entities.ProductType;
import com.mtdev.musicbox.application.utils.ProductListJSONParser;
import com.mtdev.musicbox.application.utils.ProductTypeListJSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.mtdev.musicbox.AppConfig.URL_SELECTPRODUCTS;
import static com.mtdev.musicbox.AppConfig.URL_SELECT_TYPE_PRODUCTS;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuHomeFragment.OnMenuHomeSelectedListener} interface
 * to handle interaction events.
 * Use the {@link MenuHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuHomeFragment extends Fragment {
    public final static String TAG = MenuHomeFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public ProductTypesRecyclerAdapter abAdapter;

    public RecyclerView rv;

    public MenuHomeFragment.onProductTypeCLickListener mCallback;
    GridLayoutManager glManager;

    View bottomMarginLayout;
    ImageView backBtn;
    public ImageView searchIcon;
    public EditText searchBox;

    public boolean isSearchboxVisible = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Context ctx;
    MainActivity activity;
    List<ProductType> productList;
    ProductTypesRecyclerAdapter adapter;
    public interface onProductTypeCLickListener {
        public void onProductTypeCLick();
    }
    public MenuHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuHomeFragment newInstance(String param1, String param2) {
        MenuHomeFragment fragment = new MenuHomeFragment();
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
        return inflater.inflate(R.layout.fragment_menu_home, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isSearchboxVisible = false;

        backBtn = (ImageView) view.findViewById(R.id.local_fragment_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        searchBox = (EditText) view.findViewById(R.id.local_fragment_search_box);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // ((MainActivity) getActivity()).onQueryTextChange(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchIcon = (ImageView) view.findViewById(R.id.local_fragment_search_icon);
        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearchboxVisible) {
                    searchBox.setText("");
                    searchBox.setVisibility(View.INVISIBLE);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    searchIcon.setImageResource(R.drawable.ic_search);
                 //   fragTitle.setVisibility(View.VISIBLE);
                } else {
                    searchBox.setVisibility(View.VISIBLE);
                    searchBox.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    searchIcon.setImageResource(R.drawable.ic_cross_white);
                //    fragTitle.setVisibility(View.INVISIBLE);
                }
                isSearchboxVisible = !isSearchboxVisible;
            }
        });


        rv = (RecyclerView) view.findViewById(R.id.albums_recycler);
        getProductList();

        rv.addOnItemTouchListener(new ClickItemTouchListener(rv) {
            @Override
            public boolean onClick(RecyclerView parent, View view, int position, long id) {
                MainActivity.tempMenu = productList.get(position);
                mCallback.onProductTypeCLick();
                return true;
            }

            @Override
            public boolean onLongClick(RecyclerView parent, View view, int position, long id) {
                return false;
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });



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
    public interface OnMenuHomeSelectedListener {
        // TODO: Update argument type and name
        void OnMenuHomeSelected(Uri uri);
    }

    private void getProductList() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url =  URL_SELECT_TYPE_PRODUCTS;
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        productList = new ArrayList<>();
                        productList = ProductTypeListJSONParser.parseData(response);
                        abAdapter = new ProductTypesRecyclerAdapter(productList, getContext());
                        glManager = new GridLayoutManager(getContext(), 2);
                        rv.setLayoutManager(glManager);
                        rv.setItemAnimator(new DefaultItemAnimator());
                        rv.setAdapter(abAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error",""+ error.getMessage());
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (MenuHomeFragment.onProductTypeCLickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }
    public void updateAdapter() {
        if (abAdapter != null)
            abAdapter.notifyDataSetChanged();
    }

}
