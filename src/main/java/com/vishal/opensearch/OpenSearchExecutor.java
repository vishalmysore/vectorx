package com.vishal.opensearch;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.opensearch.action.admin.indices.delete.DeleteIndexRequest;
import org.opensearch.action.delete.DeleteRequest;
import org.opensearch.action.delete.DeleteResponse;
import org.opensearch.action.get.GetRequest;
import org.opensearch.action.get.GetResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.action.index.IndexResponse;
import org.opensearch.action.support.master.AcknowledgedResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestClientBuilder;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.common.settings.Settings;
import org.opensearch.client.sniff.SniffOnFailureListener;
import org.opensearch.client.sniff.OpenSearchNodesSniffer;
import org.opensearch.client.sniff.NodesSniffer;
import org.opensearch.client.sniff.Sniffer;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OpenSearchExecutor {

    public static void main(String[] args) throws IOException {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
       // credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "yourpassword-this is not the real one"));

        RestClientBuilder builder = RestClient.builder(
                        HttpHost.create("https://vishalmysore-opensearch.hf.space:443"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    public HttpAsyncClientBuilder customizeHttpClient(
                            final HttpAsyncClientBuilder httpAsyncClientBuilder) {
                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });

        RestHighLevelClient client = new RestHighLevelClient(builder);

        NodesSniffer nodesniffer = new OpenSearchNodesSniffer(
                client.getLowLevelClient(),
                TimeUnit.SECONDS.toMillis(5),
                OpenSearchNodesSniffer.Scheme.HTTPS);

        Sniffer sniffer = Sniffer.builder(client.getLowLevelClient())
                .setNodesSniffer(nodesniffer)
                .build();

        SniffOnFailureListener listener = new SniffOnFailureListener();
        listener.setSniffer(sniffer);

        String index = "vishal_1";
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(index);

        createIndexRequest.settings(Settings.builder()
                .put("index.number_of_shards", 4)
                .put("index.number_of_replicas", 3)
        );

        HashMap<String, String> typeMapping = new HashMap<String,String>();
        typeMapping.put("type", "integer");
        HashMap<String, Object> ageMapping = new HashMap<String, Object>();
        ageMapping.put("age", typeMapping);
        HashMap<String, Object> mapping = new HashMap<String, Object>();
        mapping.put("properties", ageMapping);
        createIndexRequest.mapping(mapping);
        //CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println("\nCreating index:");
       // System.out.println("Is client acknowledged?" + ((createIndexResponse.isAcknowledged())? " Yes" : " No"));

        IndexRequest request = new IndexRequest(index);
        request.id("1");

        HashMap<String, String> stringMapping = new HashMap<String, String>();
        stringMapping.put("message:", "Artificial Intelligence Tutorial by Vishal Mysore ");
        request.source(stringMapping);
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
        System.out.println("\nAdding document:");
        System.out.println(indexResponse);

        GetRequest getRequest = new GetRequest(index, "1");
        GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println("\nSearch results:");
        System.out.println(response.getSourceAsString());

        DeleteRequest deleteDocumentRequest = new DeleteRequest(index, "1");
        DeleteResponse deleteResponse = client.delete(deleteDocumentRequest, RequestOptions.DEFAULT);
        System.out.println("\nDeleting document:");
        System.out.println(deleteResponse);

        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(index);
        AcknowledgedResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println("\nDeleting index:");
        System.out.println("Is client acknowledged?" + ((deleteIndexResponse.isAcknowledged())? " Yes" : " No"));

        client.close();
    }
}
