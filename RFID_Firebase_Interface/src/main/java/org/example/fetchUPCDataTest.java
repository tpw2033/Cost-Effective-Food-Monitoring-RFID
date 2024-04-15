package org.rfidinterface;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class fetchUPCDataTest {
    public static void main(String[] args) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");

        String upcString = "034000114702";
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
                walmartImageUrl = images.getString(1);
                break;
            }
        }

        System.out.println("Walmart Title: " + walmartTitle);
        System.out.println("Walmart Image URL: " + walmartImageUrl);

    }
}
