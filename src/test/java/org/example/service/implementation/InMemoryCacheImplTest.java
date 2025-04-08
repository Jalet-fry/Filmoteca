package org.example.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
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
    void get_ShouldReturnEmptyOptionalForNonExistentKey() {
        assertTrue(cache.get(1L).isEmpty());
    }

    @Test
    void putAndGet_ShouldWorkCorrectly() {
        cache.put(1L, "Test");
        assertEquals("Test", cache.get(1L).get());
    }

    @Test
    void put_ShouldNotCacheNullValues() {
        cache.put(1L, null);
        assertTrue(cache.get(1L).isEmpty());
    }

    @Test
    void put_ShouldEvictOldestItemWhenFull() throws InterruptedException {
        for (long i = 1; i <= 5; i++) {
            cache.put(i, "Item " + i);
            Thread.sleep(10); // Ensure different timestamps
        }
        assertFalse(cache.get(1L).isPresent());
        assertTrue(cache.get(5L).isPresent());
        assertEquals(4, cache.getAllValues().size());
    }

    @Test
    void put_ShouldNotEvictWhenUpdatingExistingKey() {
        cache.put(1L, "First");
        cache.put(2L, "Second");
        cache.put(1L, "Updated");

        assertEquals("Updated", cache.get(1L).get());
        assertEquals(2, cache.getAllValues().size());
    }

    @Test
    void del_ShouldRemoveItem() {
        cache.put(1L, "Test");
        cache.del(1L);
        assertTrue(cache.get(1L).isEmpty());
    }

    @Test
    void del_ShouldHandleNonExistentKey() {
        cache.del(999L);
        assertTrue(cache.get(999L).isEmpty());
    }

    @Test
    void getAllValues_ShouldReturnAllCachedValues() {
        cache.put(1L, "One");
        cache.put(2L, "Two");

        Collection<String> values = cache.getAllValues();
        assertEquals(2, values.size());
        assertTrue(values.containsAll(List.of("One", "Two")));
    }
}