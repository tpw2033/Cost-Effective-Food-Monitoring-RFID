package org.rfidinterface;

import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class RFIDEncodeDecode {

    private static DateTimeFormatter humanFormat = DateTimeFormatter.ofPattern("MMMM d, uuuu");

    public static void main(String[] args) throws Exception {

        ArrayList<HashMap<String,String>> decodedInputs = new ArrayList<>();

        String gtin = "078742374895";
        String date = "240727";


        String generateRFID = encodeRFID(gtin, date);
        System.out.println("Generated hexadecimal string: " + generateRFID);
    }

    public static String getGTINfromRFID(String rfidHex) {
        String binData = new BigInteger(rfidHex, 16).toString(2);
        String[] b = trimChars(binData, 8);
        b = trimChars(b[1], 1);
        b = trimChars(b[1], 3);
        b = trimChars(b[1], 56);
        return stringPad(Long.toString(Long.parseLong(b[0],2),16), '0', 14);
    }

    public static String getExpirationfromRFID(String rfidHex) {
        String binData = new BigInteger(rfidHex, 16).toString(2);
        String[] b = trimChars(binData, 8);
        b = trimChars(b[1], 1);
        b = trimChars(b[1], 3);
        b = trimChars(b[1], 56);
        b = variLengthDecode(b[1]);
        return b[0];
    }

    private static String encodeRFID(String GTIN, String date) {

        String header = "11110111";
        String plusData = "0";
        String filterValue = "001";

        long gtinLong = Long.parseLong(GTIN, 16);
        String binaryGTIN = Long.toBinaryString(gtinLong);
        binaryGTIN = stringPad(binaryGTIN, '0', 56);

        String encoder = "000";
        String dateLength = "00110";

        int dateInt = Integer.parseInt(date);
        String binaryDate = Integer.toBinaryString(dateInt);
        binaryDate = stringPad(binaryDate, '0', 20);

        String binData = header + plusData + filterValue + binaryGTIN + encoder + dateLength + binaryDate;

        BigInteger binaryBigInt = new BigInteger(binData, 2);
        return binaryBigInt.toString(16);
    }

    private static HashMap<String, String> decodeRFID(String binData){
        HashMap<String, String> output = new HashMap<>();

        // First 8 chars in [0], rest in [1]
        String[] b = trimChars(binData, 8);

        // Parse int using radix 2 (binary) = 247
        int header = Integer.parseInt(b[0], 2);

        // Header is 247 (SGTIN+)
        if(header == 247){
            output.put("01 - RFID Type", "SGTIN+"); // 247 is SGTIN+
        }

        // We went through header, so go through remaining binary, grab 1 char (001)
        b = trimChars(b[1], 1);

        // No plus data
        boolean plusData = b[0].equals("1");


        b = trimChars(b[1], 3);
        int filterValue = Integer.parseInt(b[0], 2);
        switch(filterValue){
            case 1:
                output.put("02 - Filter Value", "Point of Sale Trade Item");
                break;
            case 2:
                output.put("02 - Filter Value", "Full Case for Transport");
                break;
            default:
                output.put("02 - Filter Value", "Unknown");
        }
        int countUp = 3;
        String numerate = "";

        b = trimChars(b[1], 56);
        String gtin = stringPad(Long.toString(Long.parseLong(b[0],2),16), '0', 14);
        numerate = stringPad(Integer.toString(countUp++), '0', 2);
        output.put(numerate + " - GTIN", gtin);

        b = variLengthDecode(b[1]);
        if(b == null) return null;
        numerate = stringPad(Integer.toString(countUp++), '0', 2);
        output.put(numerate + " - Serial Number", b[0]);

        return output;
    }

    private static String[] variLengthDecode(String input){
        String[] b = trimChars(input, 3);
        b = trimChars(b[1], 5);
            int binaryLen = 20;
            b = trimChars(b[1], binaryLen);
            return new String[] {new BigInteger(b[0], 2).toString(), b[1]};
    }

    private static String[] trimChars(String input, int num){
        String[] arr = new String[2];
        arr[0] = input.substring(0,num);
        arr[1] = input.substring(num);
        return arr;
    }
    private static String stringPad(String str, char c, int length){
        StringBuilder sb = new StringBuilder(str);
        while(sb.length() < length) sb.insert(0, c);
        return sb.toString();
    }
}
