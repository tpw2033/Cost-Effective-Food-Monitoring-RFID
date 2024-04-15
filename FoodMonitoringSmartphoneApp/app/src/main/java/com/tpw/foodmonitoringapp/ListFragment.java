package com.tpw.foodmonitoringapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ListFragment extends Fragment {



    private RecyclerView recyclerView;
    itemAdapter adapter;
    DatabaseReference ref;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        String userPath = getArguments().getString("userPath");
        ref = FirebaseDatabase.getInstance().getReference(userPath + "/ownedItems");

        recyclerView = rootView.findViewById(R.id.recycler1);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity()));

        FirebaseRecyclerOptions<FoodItem> options
                = new FirebaseRecyclerOptions.Builder<FoodItem>()
                .setQuery(ref, FoodItem.class)
                .build();

        adapter = new itemAdapter(getActivity(), options, userPath);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = rootView.findViewById(R.id.addItemButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("userPath" , userPath);
                AddModifyItemFragment addFragment = new AddModifyItemFragment();
                addFragment.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, addFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }
}