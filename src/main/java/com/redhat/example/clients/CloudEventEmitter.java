package com.redhat.example.clients;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.util.UUID;
import java.net.http.HttpResponse;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CloudEventEmitter {
    @ConfigProperty(name = "cloudevents.url")
    private String url;

    private HttpClient client = HttpClient.newHttpClient();

    /**
     * Send a CloudEvent via HTTP POST
     * 
     * @param wfId      workflow instance id that will receive the CloudEvent
     * @param eventType the type of event expected by the workflow instance
     * @param payload   information about the task outcomes
     */
    public void send(String wfId, String eventType, String payload) {
        BodyPublisher body = HttpRequest.BodyPublishers.ofString(payload);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("ce-id", UUID.randomUUID().toString())
                .header("ce-specversion", "1.0")
                .header("ce-source", "tasks")
                .header("ce-type", eventType)
                .header("ce-kogitoprocrefid", wfId)
                .POST(body)
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
