package com.tpw.foodmonitoringapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeGenerateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeGenerateFragment extends Fragment {

    public RecipeGenerateFragment() {
    }

    public static RecipeGenerateFragment newInstance(String param1, String param2) {
        RecipeGenerateFragment fragment = new RecipeGenerateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_third, container, false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Button b = rootView.findViewById(R.id.generateButton);
        TextView notesInput = rootView.findViewById(R.id.notesField);

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        String userPath = getArguments().getString("userPath");
        DatabaseReference itemPath = db.getReference(userPath + "/ownedItems");

        ListView listView = rootView.findViewById(R.id.itemChoiceView);

        itemPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> currentItems = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    FoodItem item = snap.getValue(FoodItem.class);
                    currentItems.add(item.getItemName());
                }

                ArrayAdapter<String> arrayAdapter = new
                        ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice,
                        currentItems);
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int len = listView.getCount();
                        SparseBooleanArray checked = listView.getCheckedItemPositions();
                        String checkedItems = "";
                        for (int i = 0; i < len; i++)
                            if (checked.get(i)) {
                                checkedItems = checkedItems + "," + (String) listView.getAdapter().getItem(i);
                            }
                        String aiResponse = AIRequest.chatGPT("Generate 5 recipes using only these items. " +
                                "Start each recipe with a dashed point - , and give a list of ingredients, instructions," +
                                "and prep seperately. Keep each recipe under 7 sentences. ABSOLUTELY MUST Seperate each recipe by 10 t characters: tttttttttt "
                                +
                                checkedItems + " Also take into these notes (if not blank):" +
                                notesInput.getText().toString()).replaceAll("\n", "");

                        aiResponse = aiResponse.replace("\\n", "\n");
                        List<String> recipeList = new ArrayList<>();
                        recipeList.add("Incorrect Format");
                        try {
                            String[] aiArray = aiResponse.split("tttttttttt");
                            recipeList = Arrays.asList(aiArray);
                        } catch (Exception e) {
                            System.out.println("Incorrect Format Error");
                        }


                        List<String> finalRecipeList = recipeList;
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                ListView recipeView = rootView.findViewById(R.id.recipeListView);
                                ArrayAdapter<String> arrayAdapter = new
                                        ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                                        finalRecipeList);

                                recipeView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                                recipeView.setAdapter(arrayAdapter);

                            }
                        });
                    }
                });
                thread.start();

            }
        });


        return rootView;
    }

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s,
                Toast.LENGTH_LONG).show();
    }
}