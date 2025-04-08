package com.github.tamaracvjetkovic.unit;

import com.github.tamaracvjetkovic.cache.OllamaCompletionCache;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OllamaCompletionCacheTest {
    @BeforeEach
    public void setUp() {
        OllamaCompletionCache.clear();
    }

    @Test
    public void putAndGet_ValidPrefix_ReturnsCorrectCompletion() {
        OllamaCompletionCache.put("for(int i = 0;", " i < 10; i++)");
        String result = OllamaCompletionCache.get("for(int i = 0;");
        assertEquals(" i < 10; i++)", result);
    }

    @Test
    public void get_InvalidPrefix_ReturnsNull() {
        assertNull(OllamaCompletionCache.get("nothing_here"));
    }

    @Test
    public void clear_AfterPut_CacheIsEmpty() {
        OllamaCompletionCache.put("System.out.println", "(\"Hello World\")");
        OllamaCompletionCache.clear();
        assertNull(OllamaCompletionCache.get("System.out.println"));
    }

    @Test
    public void put_CacheIsFull_OldestEntryIsRemoved() {
        for (int i = 0; i < OllamaCompletionCache.CACHE_SIZE; i++) {
            OllamaCompletionCache.put("key" + i, "" + i);
        }

        OllamaCompletionCache.put("key" + OllamaCompletionCache.CACHE_SIZE , "" + OllamaCompletionCache.CACHE_SIZE);

        assertNull(OllamaCompletionCache.get("key0"));
        assertEquals("80", OllamaCompletionCache.get("key80"));
    }

    @Test
    public void get_UpdatesUsageOrder_PreventsOldestEntryRemoval() {
        for (int i = 0; i < OllamaCompletionCache.CACHE_SIZE; i++) {
            OllamaCompletionCache.put("key" + i, "" + i);
        }

        assertEquals("0", OllamaCompletionCache.get("key0"));

        OllamaCompletionCache.put("key" + OllamaCompletionCache.CACHE_SIZE , "" + OllamaCompletionCache.CACHE_SIZE);

        assertNull(OllamaCompletionCache.get("key1"));
        assertEquals("0", OllamaCompletionCache.get("key0"));
        assertEquals("80", OllamaCompletionCache.get("key80"));
    }
}
