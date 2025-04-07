package com.github.tamaracvjetkovic.client;

import java.util.LinkedHashMap;
import java.util.Map;

public class OllamaCompletionCache {
    private static final int CACHE_SIZE = 80;
    private static final float LOAD_FACTOR = 0.8f;
    private static final boolean ACCESS_ORDER = true;

    private static final LinkedHashMap<String, String> cache = new LinkedHashMap<>(CACHE_SIZE, LOAD_FACTOR, ACCESS_ORDER) {
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > CACHE_SIZE;
        }
    };

    public static String get(String prefix) {
        return cache.get(prefix);
    }

    public static void put(String prefix, String completion) {
        cache.put(prefix, completion);
    }
}
