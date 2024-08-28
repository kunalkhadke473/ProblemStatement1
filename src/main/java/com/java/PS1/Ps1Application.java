package com.java.PS1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
public class Ps1Application {

    public static void main(String[] args) {
        if (args.length != 3) {
        	System.out.println(args.length+"...............");
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN> <JSON_FILE_PATH>");
            return;
        }

        String prnNumber = args[1].toLowerCase().replaceAll("\\s+", "");  
        String jsonFilePath = args[2];

        try {
            String destinationValue = findDestinationValue(jsonFilePath); 

            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            String randomString = generateRandomString(8);  
            String concatenatedString = prnNumber + destinationValue + randomString;
            String md5Hash = generateMD5Hash(concatenatedString);  

            System.out.println(md5Hash + ";" + randomString);  

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String findDestinationValue(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(jsonFilePath));
        return traverseJson(rootNode);
    }

    private static String traverseJson(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (entry.getKey().equals("destination")) {
                    return entry.getValue().asText();
                }
                String result = traverseJson(entry.getValue());
                if (result != null) {
                    return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayItem : node) {
                String result = traverseJson(arrayItem);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : messageDigest) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
