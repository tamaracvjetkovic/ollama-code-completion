package com.github.tamaracvjetkovic.unit;

import com.github.tamaracvjetkovic.cache.OllamaCompletionCache;
import com.github.tamaracvjetkovic.clients.OllamaCompletionClient;
import com.github.tamaracvjetkovic.clients.OllamaHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OllamaCompletionClientTest {
    @BeforeEach
    public void clearCacheBeforeTest() {
        OllamaCompletionCache.clear();
    }

    @Test
    public void getCompletion_PrefixValid_ReturnsCompletionAndCaches() throws IOException, InterruptedException {
        OllamaCompletionCache.put("public static void mai", "n(String[] args) { System.out.println(args); }");

        OllamaHttpClient mockOllamaClient = mock(OllamaHttpClient.class);
        when(mockOllamaClient.requestCompletion("public static void main"))
                .thenReturn("(String[] args) { System.out.println(args); }");

        OllamaCompletionClient client = new OllamaCompletionClient(mockOllamaClient);

        String result = client.getCompletion("public static void main");

        assertNotNull(result);
        assertEquals("(String[] args) { System.out.println(args); }", result);
        assertEquals("(String[] args) { System.out.println(args); }", OllamaCompletionCache.get("public static void main"));
    }

    @Test
    public void getCompletion_PrefixCached_ReturnsCachedValueWithoutCallingHttpClient() throws IOException, InterruptedException {
        OllamaCompletionCache.put("System.out.print", "(\"Hello World\");");

        OllamaHttpClient mockOllamaClient = mock(OllamaHttpClient.class);
        OllamaCompletionClient client = new OllamaCompletionClient(mockOllamaClient);

        String result = client.getCompletion("System.out.print");

        verify(mockOllamaClient, never()).requestCompletion(any());
        assertEquals("(\"Hello World\");", result);
    }

    @Test
    public void getCompletion_HttpClientThrowsIOException_ThrowsRuntimeException() throws IOException, InterruptedException {
        OllamaHttpClient mockOllamaClient = mock(OllamaHttpClient.class);
        when(mockOllamaClient.requestCompletion("int x = 5;")).thenThrow(new IOException("Error!"));

        OllamaCompletionClient client = new OllamaCompletionClient(mockOllamaClient);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.getCompletion("int x = 5;");
        });
        assertTrue(exception.getMessage().contains("An error occurred while getting completion"));
    }

    @Test
    public void getCompletion_HttpClientThrowsInterruptedException_ThrowsRuntimeException() throws IOException, InterruptedException {
        OllamaHttpClient mockOllamaClient = mock(OllamaHttpClient.class);
        when(mockOllamaClient.requestCompletion("double y = ")).thenThrow(new InterruptedException("Error!"));

        OllamaCompletionClient client = new OllamaCompletionClient(mockOllamaClient);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            client.getCompletion("double y = ");
        });
        assertTrue(exception.getMessage().contains("An error occurred while getting completion"));
    }


    @Test
    public void getCompletion_PrefixNull_ThrowsRuntimeException() throws IOException, InterruptedException {
        OllamaHttpClient mockOllamaClient = mock(OllamaHttpClient.class);

        OllamaCompletionClient client = new OllamaCompletionClient(mockOllamaClient);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            client.getCompletion(null);
        });
        assertTrue(exception.getMessage().contains("Prefix cannot be null"));
    }

    @Test
    public void getCompletion_CalledTwiceWithSamePrefix_UsesCacheSecondTime() throws IOException, InterruptedException {
        String prefix = "class Test {";
        String completion = "}";

        OllamaHttpClient mockOllamaClient = mock(OllamaHttpClient.class);
        when(mockOllamaClient.requestCompletion(prefix)).thenReturn(completion);

        OllamaCompletionClient client = new OllamaCompletionClient(mockOllamaClient);

        String result1 = client.getCompletion(prefix); // 1. "calls" HTTP

        assertEquals("}", OllamaCompletionCache.get("class Test {")); // now it exists in cache
        String result2 = client.getCompletion(prefix); // 2. uses cache

        assertEquals(completion, result1);
        assertEquals(completion, result2);
        verify(mockOllamaClient, times(1)).requestCompletion(prefix);
    }
}
