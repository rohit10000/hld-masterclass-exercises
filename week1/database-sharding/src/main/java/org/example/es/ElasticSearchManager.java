package org.example.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.hc.core5.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Map;

public class ElasticSearchManager {

    private final ElasticsearchClient client;

    public ElasticSearchManager() {
        RestClient restClient = RestClient.builder(
                String.valueOf(new HttpHost("localhost", 9200))).build();

        // Step 2: Build transport and high-level client
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        this.client = new ElasticsearchClient(transport);
    }

    public IndexResponse putData(String index, String value) {
        Map<String, Object> doc = Map.of(
                "content", value
        );

        // Step 4: Index the document
        IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                .index(index)
                .id("1") // optional: provide custom ID
                .document(doc)
        );
        try {
            IndexResponse response = client.index(request);
            System.out.println("Document indexed with ID: " + response.id());
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


