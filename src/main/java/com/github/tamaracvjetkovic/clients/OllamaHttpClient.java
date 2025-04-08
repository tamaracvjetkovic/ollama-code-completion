package com.github.tamaracvjetkovic.clients;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OllamaHttpClient {
    private final HttpClient client;
    private final String ollamaCompletionUrl = "http://localhost:11434/api/generate";

    public OllamaHttpClient(HttpClient client) {
        this.client = client;
    }

    /**
     * Creates a new JSON object with the model and given prompt, and sends an HTTP POST
     * request to the locally running Ollama API (at localhost:11434) to get the code completion.
     *
     * @param prefix The given prefix code used for generating the completion.
     * @return The completion generated based on the given prefix.
     * @throws IOException If an error occurs during the request or while processing the response.
     * @throws InterruptedException If an error occurs during the request or while processing the response.
     * @throws IllegalArgumentException If the response code is not 200 (OK).
     */
    public String requestCompletion(String prefix) throws IOException, InterruptedException {
        JSONObject json = new JSONObject();
        json.put("model", "granite-code:3b");
        json.put("prompt", "Continue the following code exactly where it left off. Do NOT explain anything. Do NOT include comments. Only return the code continuation based on the context:\n\n" + prefix);
        json.put("stream", false);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ollamaCompletionUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            return jsonResponse.getString("response");

        } else {
            throw new IllegalArgumentException("An unexpected error occurred: " + response.statusCode());
        }
    }
}
