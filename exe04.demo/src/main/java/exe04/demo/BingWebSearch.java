package exe04.demo;

import java.net.*;
import java.util.*;
import java.io.*;
import javax.net.ssl.HttpsURLConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class BingWebSearch{
	// Enter a valid subscription key.
	static String subscriptionKey = "5159d5a2547f4f03957999e36f6cf528";

	/*
	 * If you encounter unexpected authorization errors, double-check these values
	 * against the endpoint for your Bing Web search instance in your Azure
	 * dashboard.
	 */
	static String host = "https://api.cognitive.microsoft.com";
	static String path = "/bing/v7.0/search";
	static String searchTerm = "Microsoft Cognitive Services";
	
	@SuppressWarnings("resource")
	public static SearchResults SearchWeb (String searchQuery) throws Exception {
	    // Construct the URL.
	    URL url = new URL(host + path + "?q=" +  URLEncoder.encode(searchQuery, "UTF-8"));

	    // Open the connection.
	    HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
	    connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);

	    // Receive the JSON response body.
	    InputStream stream = connection.getInputStream();
	    String response = new Scanner(stream).useDelimiter("\\A").next();

	    // Construct the result object.
	    SearchResults results = new SearchResults(new HashMap<String, String>(), response);

	    // Extract Bing-related HTTP headers.
	    Map<String, List<String>> headers = connection.getHeaderFields();
	    for (String header : headers.keySet()) {
	        if (header == null) continue;      // may have null key
	        if (header.startsWith("BingAPIs-") || header.startsWith("X-MSEdge-")){
	            results.relevantHeaders.put(header, headers.get(header).get(0));
	        }
	    }
	    stream.close();
	    return results;
	}
	
	public static String prettify(String json_text) {
	    JsonParser parser = new JsonParser();
	    JsonObject json = parser.parse(json_text).getAsJsonObject();
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    return gson.toJson(json);
	}
	
	public static void main (String[] args) {
	    // Confirm the subscriptionKey is valid.
	    if (subscriptionKey.length() != 32) {
	        System.out.println("Invalid Bing Search API subscription key!");
	        System.out.println("Please paste yours into the source code.");
	        System.exit(1);
	    }

	    // Call the SearchWeb method and print the response.
	    try {
	        System.out.println("Searching the Web for: " + searchTerm);
	        SearchResults result = SearchWeb(searchTerm);
	        System.out.println("\nRelevant HTTP Headers:\n");
	        for (String header : result.relevantHeaders.keySet())
	        System.out.println(header + ": " + result.relevantHeaders.get(header));
	        System.out.println("\nJSON Response:\n");
	        System.out.println(prettify(result.jsonResponse));
	    }
	    catch (Exception e) {
	        e.printStackTrace(System.out);
	        System.exit(1);
	    }
	}
}

class SearchResults{
    HashMap<String, String> relevantHeaders;
    String jsonResponse;
    SearchResults(HashMap<String, String> headers, String json) {
        relevantHeaders = headers;
        jsonResponse = json;
    }
}