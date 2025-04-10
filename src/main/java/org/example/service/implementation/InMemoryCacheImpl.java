package org.example.service.implementation;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.example.service.InMemoryCache;
import org.springframework.stereotype.Component;

@Component
public class InMemoryCacheImpl<K, V> implements InMemoryCache<K, V> {
    private static final int MAXIMUM_CACHE_SIZE = 4;
    private final Map<K, V> cache = new HashMap<>(MAXIMUM_CACHE_SIZE);
    private final Map<K, LocalDateTime> timeCache = new HashMap<>(MAXIMUM_CACHE_SIZE);

    @Override
    public Optional<V> get(K key) {
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(key));
    }

    @Override
    public V put(K key, V value) {
        if (key == null || value == null) {
            return null;
        }

        if (cache.size() >= MAXIMUM_CACHE_SIZE && !cache.containsKey(key)) {
            evictOldestEntry();
        }

        cache.put(key, value);
        timeCache.put(key, LocalDateTime.now());
        return value;
    }

    @Override
    public void del(K key) {
        if (key != null) {
            cache.remove(key);
            timeCache.remove(key);
        }
    }

    @Override
    public Collection<V> getAllValues() {
        return List.copyOf(cache.values()); // Возвращаем неизменяемую копию
    }

    private void evictOldestEntry() {
        timeCache.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .ifPresent(entry -> {
                    cache.remove(entry.getKey());
                    timeCache.remove(entry.getKey());
                });
    }
}