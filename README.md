# Cost-Effective Food Monitoring using RFID tags

This project aims to tackle the ever-growing problem of food being wasted when the expiration date comes and goes without the consumer realizing it. I am providing a brief summary below, but it is best explained through the PowerPoint in the documentation folder, as well as the screenshots below.

The central hub of the project is a computer-based Java interface, which reads in temperature/humidity data from an Arduino Nano over serial console. RFID tags are also read from a keyboard-input RFID reader. The interface looks up item data using a UPC API, and grabs the UPC itself and expiration date from the RFID data, in SGTIN+ format. Additionally, emails are sent using SMTP and GMail credentials, notifying users of impending expiration dates.

The Java-based interface inserts/retrieves/modifies data using Firebase, a free-to-use Google tool that also has realtime database support. This realtime database is especially useful for a smartphone app, in which listeners can be utilized to update items added over RFID with near-zero delay. 

The smartphone app was designed in Java using Android Studio, and tested on a OnePlus 9 running Android 14. Here are a few of the major features of the smartphone app:

* User login/signup system, to allow for multiple users to utilize the application, each with their own set of items.
* Item list, getting RFID-based items from the computer interface, as well as manually added items by the user.
* Items can be added manually by scanning UPC barcode, which makes a request to UPC API to retrieve item name and a stock photo to show on the item list.
* Fridge status display with temperature and humidity, and a switchable graph view showing the changes over the last 5 hours.
* AI-generated recipes from items that the user selects. Notes can be optionally entered to fine-tune the recipes (ex. spicy, cold, etc.)

# Running the code

To run this project, you will need the appropriate Firebase real-time database JSON configuration files and URLs for both the computer interface and the Android app (ex. google-services.json). This provides the authentication details required to modify and retrieve data off of a specific Firebase database. Additionally, a keyboard-input RFID reader is utilized, along with a Arduino Nano and a DHT11 temperature/humidity sensor. Lastly, if desired, GMail or other email credentials will be required to use the SMTP functionality to send food expiration reminders.

# Screenshots

The screenshots below should help give a good look at how this project works on a Android smartphone:

<div style="display:flex;">
    <img src="https://github.com/tpw2033/Cost-Effective-Food-Monitoring-RFID/blob/main/Screenshots/login.jpg" width=25% height=25%>
    <img src="https://github.com/tpw2033/Cost-Effective-Food-Monitoring-RFID/blob/main/Screenshots/itemList.jpg" width=25% height=25%>
    <img src="https://github.com/tpw2033/Cost-Effective-Food-Monitoring-RFID/blob/main/Screenshots/fridgeStatus.jpg" width=25% height=25%>
    <img src="https://github.com/tpw2033/Cost-Effective-Food-Monitoring-RFID/blob/main/Screenshots/barcodeScan.jpg" width=25% height=25%>
    <img src="https://github.com/tpw2033/Cost-Effective-Food-Monitoring-RFID/blob/main/Screenshots/aiRecipe.jpg" width=25% height=25%>
    <img src="https://github.com/tpw2033/Cost-Effective-Food-Monitoring-RFID/blob/main/Screenshots/aiRecipeCustomized.jpg" width=25% height=25%>
</div>

# Resources Utilized

The RFID decoding is heavily based upon the work of this repository, pared down to only decode GTIN/Expiration Date from a hexadecimal value:
https://github.com/Obscure2020/SaladMan/blob/main/Main.java 

Additionally, the following resources were used to get information on formatting, setup, and general procedures:

* Preventing Wasted Food At Home, https://www.epa.gov/recycle/preventing-wasted-food-home
* Radio Frequency Identification (RFID), https://www.fda.gov/radiation-emitting-products/electromagnetic-compatibility-emc/radio-frequency-identification-rfid
* What is a GTIN?, https://www.gs1us.org/upcs-barcodes-prefixes/what-is-a-gtin
* UPC Code GTIN-12, https://www.barcode.graphics/gtin-12/
* Avery Dennison Monarch Tabletop Printer 2, https://printers.averydennison.com/content/dam/averydennison/rbis/global/printer/product-information/printers/ADTP2-product-information.pdf
* GS1 US RFID Foodservice Implementation Guideline, https://gs1ca.org/gs1ca-components/documents/GS1US-Foodservice-RFID-Guideline.pdf
* GS1 TDS 2.0 Information,  https://github.com/wrightedu/Make-IT-Wright-2023/tree/main/GS1%20TDS%202.0%20Information
* UPCitemdb Documentation, https://www.UPCitemdb.com/wp/docs/main/development/responses/
* Firebase Realtime Database, https://firebase.google.com/docs/database
* How long would it take to crack your password?,  https://spycloud.com/blog/how-long-would-it-take-to-crack-your-password/ 

