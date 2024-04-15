package com.tpw.foodmonitoringapp;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;
import com.google.zxing.common.StringUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AddModifyItemFragment extends Fragment {


    public AddModifyItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        View rootView = inflater.inflate(R.layout.fragment_add_modify_item, container, false);
        String userPath = getArguments().getString("userPath");


        TextView purchaseDateField = rootView.findViewById(R.id.purchaseDateField);
        TextView expirationDateField = rootView.findViewById(R.id.expirationDateField);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        String formattedDate = currentDate.format(formatter);
        purchaseDateField.setText(formattedDate);

        purchaseDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(

                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                purchaseDateField.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);

                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });

        expirationDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();


                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // on below line we are setting date to our text view.
                                expirationDateField.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);

                            }
                        },

                        year, month, day);
                datePickerDialog.show();
            }
        });


        Button addItemButton = rootView.findViewById(R.id.addOwnedItemButton);


        Button scanBarcode = rootView.findViewById(R.id.scanBarcode);
        TextView itemNameField = rootView.findViewById(R.id.itemNameField);
        ImageView itemPhoto = rootView.findViewById(R.id.itemPhoto);
        Picasso.get().load("https://st3.depositphotos.com/23594922/31822/v/450/depositphotos_318221368-stock-illustration-missing-picture-page-for-website.jpg")
                .into(itemPhoto);
        TextView gtinField = rootView.findViewById(R.id.itemGTINField);
        TextView imageURLField = rootView.findViewById(R.id.itemImageURLField);

        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                String addItemPath = userPath + "/ownedItems/" +
                        gtinField.getText().toString() + "/";

                db.getReference(addItemPath + "isRFID/").setValue("false");
                db.getReference(addItemPath + "imageURL/").setValue(imageURLField.getText().toString());
                db.getReference(addItemPath + "itemExpDate/").setValue(convertYYMMDD(expirationDateField.getText().toString()));
                db.getReference(addItemPath + "itemGTIN/").setValue(gtinField.getText().toString());
                db.getReference(addItemPath + "itemName/").setValue(itemNameField.getText().toString());
                db.getReference(addItemPath + "itemPurDate/").setValue(convertYYMMDD(purchaseDateField.getText().toString()));

                makeToast("Item Added!");

                Bundle bundle = new Bundle();
                bundle.putString("userPath", userPath);
                TabFragment tab = new TabFragment();
                tab.setArguments(bundle);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, tab)
                        .addToBackStack(null)
                        .commit();

            }
        });

        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_UPC_A)
                        .build();

                GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(getActivity());

                scanner
                        .startScan()
                        .addOnSuccessListener(
                                barcode -> {

                                    String rawValue = barcode.getRawValue();
                                    gtinField.setText(rawValue);
                                    try {
                                        OkHttpClient client = new OkHttpClient().newBuilder().build();
                                        MediaType mediaType = MediaType.parse("application/json");

                                        String upcString = rawValue;
                                        RequestBody body = RequestBody.create(mediaType, "{\"upc\": \"" + upcString + "\"}");
                                        Request request = new Request.Builder()
                                                .url("https://api.upcitemdb.com/prod/trial/lookup")
                                                .method("POST", body)
                                                .addHeader("Content-Type", "application/json")
                                                .addHeader("Accept", "application/json")
                                                .build();

                                        Response response = client.newCall(request).execute();
                                        ResponseBody responseBody = response.body();

                                        String jsonStr = responseBody.string();


                                        JSONObject jsonObj = new JSONObject(jsonStr);
                                        JSONArray items = jsonObj.getJSONArray("items");
                                        JSONObject item = items.getJSONObject(0);
                                        JSONArray offers = item.getJSONArray("offers");

                                        String walmartTitle = "";
                                        String walmartImageUrl = "";

                                        for (int i = 0; i < offers.length(); i++) {
                                            JSONObject offer = offers.getJSONObject(i);
                                            String domain = offer.getString("domain");
                                            if (domain.equals("walmart.com")) {
                                                walmartTitle = offer.getString("title");
                                                JSONArray images = item.getJSONArray("images");
                                                for (int j = 0; j < images.length(); j++) {
                                                    String imageUrl = images.getString(j);
                                                    if (imageUrl.contains("walmartimages")) {
                                                        walmartImageUrl = imageUrl;
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }

                                        if (!walmartImageUrl.equals("")) {
                                            Picasso.get().load(walmartImageUrl).into(itemPhoto);
                                            imageURLField.setText(walmartImageUrl);
                                        } else {
                                            String notFoundImage = "https://st3.depositphotos.com/23594922/31822/v/450/depositphotos_318221368-stock-illustration-missing-picture-page-for-website.jpg";
                                            Picasso.get().load(notFoundImage)
                                                    .into(itemPhoto);
                                            imageURLField.setText(notFoundImage);
                                        }

                                        if (walmartTitle.equals("")) {
                                            itemNameField.setText("Item Name N/A");
                                        } else {
                                            itemNameField.setText(walmartTitle);
                                        }
                                    } catch (Exception ignored) {
                                    }


                                })
                        .addOnCanceledListener(
                                () -> {
                                    // Task canceled
                                })
                        .addOnFailureListener(
                                e -> {
                                    // Task failed with an exception
                                });


            }
        });


        return rootView;
    }

    private String convertYYMMDD(String inputDate) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        LocalDate date = LocalDate.parse(inputDate, inputFormatter);


        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyMMdd");
        return date.format(outputFormatter);
    }

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s,
                Toast.LENGTH_LONG).show();
    }
}