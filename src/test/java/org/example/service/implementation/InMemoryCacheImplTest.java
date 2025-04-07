package org.example.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

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
        }
        assertFalse(cache.get(1L).isPresent()); // Первый элемент должен быть вытеснен
        assertTrue(cache.get(5L).isPresent()); // Последний элемент должен быть в кэше
    }

    @Test
    void shouldRemoveItem() {
        cache.put(1L, "Test");
        cache.del(1L);
        assertFalse(cache.get(1L).isPresent());
    }
}