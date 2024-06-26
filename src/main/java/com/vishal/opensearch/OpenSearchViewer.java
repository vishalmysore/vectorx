package com.vishal.opensearch;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class OpenSearchViewer {

    private static final String INDEX_URL = "https://vishalmysore-opensearch.hf.space/vishal_1/_search";

    public static void main(String[] args) throws Exception {
        viewDocumentEmbeddings();
    }

    private static void viewDocumentEmbeddings() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String query = new JSONObject()
                .put("_source", new String[]{"title", "content", "embedding"})
                .put("query", new JSONObject().put("match_all", new JSONObject()))
                .toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(INDEX_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(query))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
    }
}

