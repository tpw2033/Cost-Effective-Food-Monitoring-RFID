package com.tpw.foodmonitoringapp;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;


public class TabFragment extends Fragment {

    ConstraintLayout frameLayout;
    TabLayout tabLayout;

    TextView tempHumidityDisplay;

    FloatingActionButton createButton;

    ListFragment first;
    FridgeStatusFragment second;
    RecipeGenerateFragment third;

    public TabFragment() {
        // Required empty public constructor
    }

//    public static TabFragment newInstance(String param1, String param2) {
//        TabFragment fragment = new TabFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab, container, false);

        String userPath = getArguments().getString("userPath");
//        makeToast(userPath);


        first = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userPath" , userPath);
        first.setArguments(bundle);


        second = new FridgeStatusFragment();
        second.setArguments(bundle);

        third = new RecipeGenerateFragment();
        third.setArguments(bundle);



        frameLayout = (ConstraintLayout) rootView.findViewById(R.id.coordinatorLayout);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tablayout);



        // Load first fragment by default
        getParentFragmentManager().beginTransaction().replace(R.id.coordinatorLayout, first)
                .addToBackStack(null)
                .commit();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch(tab.getPosition()) {
                    case 0:
                        fragment = first;
                        break;
                    case 1:
                        fragment = second;
                        break;
                    case 2:
                        fragment = third;
                        break;
                }

                getParentFragmentManager().beginTransaction().replace(R.id.coordinatorLayout, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_NONE)
                        .commit();
            }



            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return rootView;
    }
    private void makeToast(String s) {
        Toast.makeText(getActivity(), s,
                Toast.LENGTH_LONG).show();
    }
}