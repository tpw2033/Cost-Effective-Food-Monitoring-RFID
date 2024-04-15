package com.tpw.foodmonitoringapp;

public class FoodItem {

    // Name, GTIN, Purchase Date (YYMMDD), Expiration Date (YYMMDD)

    private String itemName;

    public String getIsRFID() {
        return isRFID;
    }

    public void setIsRFID(String isRFID) {
        this.isRFID = isRFID;
    }

    private String isRFID;

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    private String imageURL;


    public String getItemGTIN() {
        return itemGTIN;
    }

    public void setItemGTIN(String itemGTIN) {
        this.itemGTIN = itemGTIN;
    }

    private String itemGTIN;


    private String itemExpDate;

    public String getItemPurDate() {
        return itemPurDate;
    }

    public void setItemPurDate(String itemPurDate) {
        this.itemPurDate = itemPurDate;
    }

    private String itemPurDate;


    public FoodItem() {}


    public String getItemName()
    {
        return itemName;
    }
    public void setItemName(String itemName)
    {
        this.itemName = itemName;
    }

    public String getItemExpDate()
    {
        return itemExpDate;
    }
    public void setItemExpDate(String itemExpDate)
    {
        this.itemExpDate = itemExpDate;
    }
}
