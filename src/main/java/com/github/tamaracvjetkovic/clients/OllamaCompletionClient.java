package com.github.tamaracvjetkovic.clients;

import com.github.tamaracvjetkovic.cache.OllamaCompletionCache;

import java.io.IOException;
import java.net.http.HttpClient;

public class OllamaCompletionClient {
    private HttpClient client;

    public OllamaCompletionClient(HttpClient client) {
        this.client = client;
    }
    /**
     * Returns a code completion based on the code prefix.
     *
     * This method first checks if the prefix already exists in the cache, and if it does, it returns it.
     * If not, it sends the prefix to the OllamaHttpClient which requests the completion.
     *
     * The method also caches the result, so it can be reused quickly.
     *
     * @param prefix The prefix code used for generating the completion.
     * @return The generated completion based on the given prefix.
     * @throws RuntimeException If an error occurs during the completion request or while processing the response.
     */
    public String getCompletion(String prefix) {
        String cached = OllamaCompletionCache.get(prefix);
        if (cached != null) {
            return cached;
        }

        try {
            String completion = new OllamaHttpClient(client).requestCompletion(prefix);
            OllamaCompletionCache.put(prefix, completion);
            return completion;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("An error occurred while getting completion: " + e.getMessage(), e);
        }
    }
}
