package com.github.tamaracvjetkovic.client;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OllamaCompletionClient {
    private static String ollamaCompletionUrl = "http://localhost:11434/api/generate";

    /**
     * Based on the code prefix, requests a code completion from the Ollama API.
     *
     * This method first checks if the prefix already exists in the cache, and if it does, it returns it.
     * If not, it creates a new JSON object with the model and given prompt, and sends an HTTP POST
     * request to the locally running Ollama API (at localhost:11434) to get the code completion.
     *
     * The method also caches the result, so it can be reused quickly.
     *
     * @param prefix The prefix code used for generating the completion.
     * @return The completion based on the given prefix.
     * @throws RuntimeException If an error occurs during the request or while processing the response.
     * @throws IllegalArgumentException If the response code is not 200 (OK).
     */
    public static String requestCompletion(String prefix) {
        String cached = OllamaCompletionCache.get(prefix);
        if (cached != null) {
            return cached;
        }

        try {
            HttpClient client = HttpClient.newHttpClient();

            JSONObject json = new JSONObject();
            json.put("model", "granite-code:3b");
            //json.put("prompt", "Based on the given code, which may contain errors, autocomplete the rest of the code WITHOUT any extra information and WITHOUT explanatory comments, even if the function is incorrect or incomplete.\n" + prefix);
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
                String completion = jsonResponse.getString("response");

                OllamaCompletionCache.put(prefix, completion);

                return completion;

            } else {
                 throw new IllegalArgumentException("An unexpected error occurred: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("An error occurred: " + e.getMessage(), e);
        }
    }
}
