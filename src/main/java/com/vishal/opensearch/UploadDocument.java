package com.vishal.opensearch;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UploadDocument {

    private static final String INDEX_URL = "https://vishalmysore-opensearch.hf.space/vishal_1/_doc/2";

    public static void main(String[] args) throws Exception {
        JSONObject document = new JSONObject();
        document.put("title", "Example Document");
        document.put("content", "This is an example document indexed in OpenSearch.");

        indexDocument(document);
    }

    private static void indexDocument(JSONObject document) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(INDEX_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(document.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}
