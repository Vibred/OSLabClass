package OSLabClass;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DomainScanner {

    private static final String WHOIS_API_URL = "https://api.domainsdb.info/v1/domains/search?domain=";

    public static void main(String[] args) {
        List<String> domains = generateDomains();
        for (String domain : domains) {
            if (isDomainAvailable(domain)) {
                System.out.println("Available: " + domain);
            }
        }
    }

    private static List<String> generateDomains() {
        List<String> domains = new ArrayList<>();
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        // Generate 2-character domains
        for (int i = 0; i < chars.length(); i++) {
            for (int j = 0; j < chars.length(); j++) {
                domains.add(chars.charAt(i) + "" + chars.charAt(j) + ".de");
            }
        }
        // Generate 3-character domains
        for (int i = 0; i < chars.length(); i++) {
            for (int j = 0; j < chars.length(); j++) {
                for (int k = 0; k < chars.length(); k++) {
                    domains.add(chars.charAt(i) + "" + chars.charAt(j) + "" + chars.charAt(k) + ".de");
                }
            }
        }
        return domains;
    }

    private static boolean isDomainAvailable(String domain) {
        try {
            URL url = new URL(WHOIS_API_URL + domain);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            return parseResponse(content.toString(), domain);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean parseResponse(String response, String domain) {
        // Check if the domain is mentioned in the response
        return !response.contains(domain);
    }
}