package com.tpw.foodmonitoringapp;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class itemAdapter extends FirebaseRecyclerAdapter<FoodItem, itemAdapter.itemViewHolder> {

    private Context aContext;
    private String userPathValue;
    public itemAdapter(@NonNull Context context , @NonNull FirebaseRecyclerOptions<FoodItem> options, String userPath)
    {
        super(options);
        aContext = context;
        userPathValue = userPath;
    }

    @Override
    protected void
    onBindViewHolder(@NonNull itemViewHolder holder,
                     int position, @NonNull FoodItem model)
    {

        if(Objects.equals(model.getIsRFID(), "true")) {
            holder.rfidSymbol.setVisibility(View.VISIBLE);
        } else {
            holder.rfidSymbol.setVisibility(View.GONE);
        }



        holder.barcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String barcodeData =  model.getItemGTIN();
                Bitmap barcodeBitmap = generateBarcode(barcodeData);
                showBarcodeDialog(barcodeBitmap);
            }
        });

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder.setMessage("Are you sure you want to delete " + model.getItemName() + "?");
                builder.setTitle("Delete Item");
                builder.setCancelable(false);

                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    String itemPath = userPathValue + "/ownedItems/" +
                            model.getItemGTIN() + "/";
                    db.getReference(itemPath).setValue(null);
                    Toast.makeText(v.getContext(), "Item deleted.",
                            Toast.LENGTH_LONG).show();
                });

                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });



        holder.itemName.setText(model.getItemName());
        holder.itemPurDate.setText(model.getItemPurDate());
        holder.itemGTIN.setText(model.getItemGTIN());
        holder.itemExpDate.setText(model.getItemExpDate());
        Picasso.get().load(model.getImageURL()).into(holder.imagePhoto);




        String inputDateString = model.getItemExpDate();

        // Define input date format
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyMMdd");

        try {

            Date date = inputDateFormat.parse(inputDateString);


            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);


            String formattedDate = outputDateFormat.format(date);


            holder.itemExpDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        inputDateString = model.getItemPurDate();
        inputDateFormat = new SimpleDateFormat("yyMMdd");

        try {

            Date date = inputDateFormat.parse(inputDateString);


            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);


            String formattedDate = outputDateFormat.format(date);


            holder.itemPurDate.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    @NonNull
    @Override
    public itemViewHolder
    onCreateViewHolder(@NonNull ViewGroup parent,
                       int viewType)
    {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new itemViewHolder(view);
    }


    class itemViewHolder
            extends RecyclerView.ViewHolder {
        TextView itemName, itemCal, itemExpDate, itemPurDate, itemGTIN;
        Button barcodeButton;
        ImageView imagePhoto;
        ImageButton deleteItem;

        ImageView rfidSymbol;
        public itemViewHolder(@NonNull View itemView)
        {
            super(itemView);
            rfidSymbol = itemView.findViewById(R.id.rfidSymbol);
            itemName = itemView.findViewById(R.id.itemName);
            itemGTIN = itemView.findViewById(R.id.itemGTIN);
            itemExpDate = itemView.findViewById(R.id.itemExpDate);
            itemPurDate = itemView.findViewById(R.id.itemPurDate);
            barcodeButton = itemView.findViewById(R.id.barcodeButton);
            imagePhoto = itemView.findViewById(R.id.itemPhoto);
            deleteItem = itemView.findViewById(R.id.deleteItemButton);

        }
    }

    private Bitmap generateBarcode(String data) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(multiFormatWriter.encode(data, BarcodeFormat.UPC_A, 800, 400));
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showBarcodeDialog(Bitmap barcodeBitmap) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(aContext);
        ImageView imageView = new ImageView(aContext);
        imageView.setImageBitmap(barcodeBitmap);
        dialogBuilder.setView(imageView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
