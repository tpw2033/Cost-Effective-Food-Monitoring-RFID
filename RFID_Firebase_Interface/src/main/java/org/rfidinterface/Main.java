package org.rfidinterface;

import com.fazecast.jSerialComm.SerialPort;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.simple.SimpleLogger;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {


        String username = "username";
        String password = "password";

        String md5Pass = "";

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            md.update(password.getBytes());

            byte[] hashBytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            md5Pass = sb.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("MD5 algorithm not found.");
        }

        String userPath = "/users/" + username + "/" + md5Pass;


        System.out.println("Initializing Firebase connection.....");

        FileInputStream serviceAccount =
                new FileInputStream("src/main/serviceAccountKey.json");


        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://food-database-b1cbe-default-rtdb.firebaseio.com")
                .build();
        FirebaseApp.initializeApp(options);


        Scanner scanner = new Scanner(System.in);

        HashMap<FoodItem, LocalDateTime> currentItems = new HashMap<FoodItem, LocalDateTime>();
        boolean run = true;

        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        InputStream in = comPort.getInputStream();


        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

        Thread workerThread = new Thread(() -> {
            while (true) {
                try {
                    Runnable task = taskQueue.take();
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        workerThread.start();



        while (run) {

            String sensorData = "";
            try {
                for(int i = 0; i < 5; i++) {
                    sensorData = sensorData + (char) in.read();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            String[] sensorArray = sensorData.split(",");

            String fTemp = sensorArray[0];
            String fHumidity = sensorArray[1];
            int correctedTemp = Integer.parseInt(fTemp) - 6;
            String sensorFinal = correctedTemp + "," + fHumidity;



            LocalTime currentTime = LocalTime.now();


            int currentHour = currentTime.getHour();

            FirebaseDatabase.getInstance().getReference(userPath+ "/sensorData/sensorDataOverTime").child(String.valueOf(currentHour))
                    .setValue(sensorFinal, (databaseError, databaseReference) -> {

                    });


            FirebaseDatabase.getInstance().getReference(userPath+ "/sensorData/TemperatureReadout").child("data")
                    .setValue(sensorFinal, (databaseError, databaseReference) -> {

                    });



            String input;
            if (System.in.available() > 0) {
                input = scanner.nextLine();

            } else {

                input = "";
            }


            if(!Objects.equals(input, "")) {

                taskQueue.add(() -> {
                    // On input
                    String gtin = RFIDEncodeDecode.getGTINfromRFID(input);
                    String expiration = RFIDEncodeDecode.getExpirationfromRFID(input);

                    FoodItem f = new FoodItem();
                    f.setItemGTIN(gtin);
                    f.setItemExpDate(expiration);
                    f.setItemPurDate("240403");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                    try {
                        OkHttpClient client = new OkHttpClient().newBuilder().build();
                        MediaType mediaType = MediaType.parse("application/json");

                        String upcString = gtin;
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
                            f.setImageURL(walmartImageUrl);
                        } else {
                            String notFoundImage = "https://st3.depositphotos.com/23594922/31822/v/450/depositphotos_318221368-stock-illustration-missing-picture-page-for-website.jpg";
                            f.setImageURL(notFoundImage);
                        }

                        if (walmartTitle.equals("")) {

                            f.setItemName("Item Name N/A");
                        } else {
                            f.setItemName(walmartTitle);

                        }
                    } catch (Exception ignored) {
                    }
                    if (f.getItemName() != null) {
                        f.setIsRFID("true");
                        addToOwnedItems(f, userPath);
                        currentItems.put(f, LocalDateTime.now());
                    }
                });

            }




        }


    }




    private static void removeFromOwnedItems(FoodItem item, String userPath) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userPath+ "/ownedItems/");
        ref.child(item.getItemGTIN()).removeValue((databaseError, databaseReference) -> {
            if (databaseError != null) {
//                System.err.println("Error removing item from ownedItems: " + databaseError.getMessage());
            } else {
//                System.out.println(item.getItemName() + " has been removed from ownedItems\n");
            }
        });
    }

    private static void addToOwnedItems(FoodItem item, String userPath) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userPath+ "/ownedItems/");

        ref.child(item.getItemGTIN()).setValue(item, (databaseError, databaseReference) -> {

        });


        Timer timer = new Timer();
        String dateString = item.getItemExpDate(); // YYMMDD format
        Date date = null;
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
            date = sdf.parse(dateString);



        } catch (ParseException e) {
            e.printStackTrace();
        }


        Calendar calendar = Calendar.getInstance();


        calendar.setTime(date);


        calendar.add(Calendar.DAY_OF_YEAR, -7);


        Date sevenDaysBefore = calendar.getTime();

        timer.schedule(new emailTask("REDACTED", "A Food Item is expiring soon!", item.getItemName() + "\n is " +
                "expiring soon! Open the app for recipes and more details."), sevenDaysBefore);


    }

    static class emailTask extends TimerTask {
        private String toEmail;
        private String emailSubject;
        private String emailMessage;


        public emailTask(String email, String subject, String message) {
            this.toEmail = email;
            this.emailSubject = subject;
            this.emailMessage = message;
        }

        @Override
        public void run() {

            String to = toEmail;


            String from = "REDACTED";
            final String username = "REDACTED";
            final String password = "REDACTED";


            String host = "smtp.gmail.com";

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");



            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            try {

                Message message = new MimeMessage(session);


                message.setFrom(new InternetAddress(from));


                message.setRecipients(Message.RecipientType.TO,
                        InternetAddress.parse(to));

                message.setSubject(emailSubject);


                message.setText(emailMessage);


                Transport.send(message);



            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
    }


}