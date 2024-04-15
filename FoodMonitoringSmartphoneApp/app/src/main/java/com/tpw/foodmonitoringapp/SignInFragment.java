package com.tpw.foodmonitoringapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {




    public SignInFragment() {
        // Required empty public constructor
    }


    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);

        Button signIn = rootView.findViewById(R.id.signInButton);
        Button signUp = rootView.findViewById(R.id.signUpButton);
        TextView user = rootView.findViewById(R.id.usernameField);
        TextView pass = rootView.findViewById(R.id.passwordField);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userValue = user.getText().toString();
                String passValue = pass.getText().toString();

                String md5Pass = "";


                try {

                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(passValue.getBytes());
                    byte[] hashBytes = md.digest();

                    StringBuilder sb = new StringBuilder();
                    for (byte b : hashBytes) {
                        sb.append(String.format("%02x", b));
                    }
                    md5Pass = sb.toString();

                } catch (NoSuchAlgorithmException e) {
                    System.err.println("MD5 generate error");
                }


                if(userValue.equals("") || passValue.equals("")) {
                    makeToast("Please enter username and password!");

                } else {

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    String userSignUpPath = "/users/" + userValue + "/" + md5Pass + "/signUpDate";
                    DatabaseReference signUpDateRef = db.getReference(userSignUpPath);

                    signUpDateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                            if(value == null) {
                                LocalDate currentDate = LocalDate.now();
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
                                String yyMMdd = currentDate.format(formatter);

                                signUpDateRef.setValue(yyMMdd);
                                makeToast("Sign up successful!");
                            } else {
                                makeToast("User already exists!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//
                }
            }
        });


        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userValue = user.getText().toString();
                String passValue = pass.getText().toString();

                String md5Pass = "";


                try {

                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(passValue.getBytes());
                    byte[] hashBytes = md.digest();
                    StringBuilder sb = new StringBuilder();
                    for (byte b : hashBytes) {
                        sb.append(String.format("%02x", b));
                    }
                    md5Pass = sb.toString();

                } catch (NoSuchAlgorithmException e) {
                    System.err.println("MD5 generate error");
                }

                if(userValue.equals("") || passValue.equals("")) {
                    makeToast("Please enter username and password!");

                } else {

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    String userSignUpPath = "/users/" + userValue + "/" + md5Pass + "/signUpDate";
                    DatabaseReference signUpDateRef = db.getReference(userSignUpPath);

                    signUpDateRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String value = dataSnapshot.getValue(String.class);
                            if(value == null) {
                                makeToast("Username/Password is not valid.");
                            } else {
                                makeToast("Sign in successful!");

                                String md5PassIn = "";


                                try {

                                    MessageDigest md = MessageDigest.getInstance("MD5");
                                    md.update(passValue.getBytes());
                                    byte[] hashBytes = md.digest();
                                    StringBuilder sb = new StringBuilder();
                                    for (byte b : hashBytes) {
                                        sb.append(String.format("%02x", b));
                                    }
                                    md5PassIn = sb.toString();

                                } catch (NoSuchAlgorithmException e) {
                                    System.err.println("MD5 algorithm not found.");
                                }

                                Bundle bundle = new Bundle();
                                bundle.putString("userPath" , "/users/" + userValue + "/" + md5PassIn);
                                TabFragment tab = new TabFragment();
                                tab.setArguments(bundle);

                                getParentFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container, tab)
                                        .addToBackStack(null)
                                        .commit();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        return rootView;
    }

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s,
                Toast.LENGTH_LONG).show();
    }
}
