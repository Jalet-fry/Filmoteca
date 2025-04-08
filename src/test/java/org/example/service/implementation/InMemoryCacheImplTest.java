package org.example.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryCacheImplTest {

    private InMemoryCacheImpl<Long, String> cache;

    @BeforeEach
    void setUp() {
        cache = new InMemoryCacheImpl<>();
    }

    @Test
    void shouldAddAndRetrieveItem() {
        cache.put(1L, "Test");
        assertTrue(cache.get(1L).isPresent());
        assertEquals("Test", cache.get(1L).get());
    }

    @Test
    void shouldEvictOldestItemWhenFull() {
        for (long i = 1; i <= 5; i++) {
            cache.put(i, "Item " + i);
            try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        }
        assertFalse(cache.get(1L).isPresent());
        assertTrue(cache.get(5L).isPresent());
    }

    @Test
    void shouldNotCacheNullValues() {
        cache.put(1L, null);
        assertFalse(cache.get(1L).isPresent());
    }

    @Test
    void getAllValues_ShouldReturnCurrentCacheContents() {
        cache.put(1L, "One");
        cache.put(2L, "Two");

        Collection<String> values = cache.getAllValues();
        assertEquals(2, values.size());
        assertTrue(values.containsAll(List.of("One", "Two")));
    }

    @Test
    void del_ShouldRemoveItemFromCache() {
        cache.put(1L, "Test");
        cache.del(1L);
        assertFalse(cache.get(1L).isPresent());
    }
}