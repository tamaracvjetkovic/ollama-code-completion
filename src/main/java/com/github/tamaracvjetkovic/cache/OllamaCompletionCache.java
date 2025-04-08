package com.github.tamaracvjetkovic.cache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU Cache to store code completions for frequently used prefixes.
 *
 * The class uses a LinkedHashMap with access-order policy, where the most recent prefixes are stored first.
 * Once the cache is full and the size reaches certain cache size, the "eldest entry" will be removed.
 *
 * The cache speeds up the process of returning the code completion, since new API requests will
 * not be made if the prefix is already present in the cache.
 *
 * Constants:
 * @code CACHE_SIZE: The size of the cache.
 * @code LOAD_FACTOR: The factor at which the map will resize.
 * @code ACCESS_ORDER: Determines whether the elements will be ordered by access order (true) or insertion order (false).
 */
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
