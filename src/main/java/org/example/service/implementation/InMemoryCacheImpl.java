package org.example.service.implementation;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.example.service.InMemoryCache;
import org.springframework.stereotype.Component;

@Component
public class InMemoryCacheImpl<K, V> implements InMemoryCache<K, V> {
    private static final int MAXIMUM_CACHE_SIZE = 4;
    private HashMap<K, V> cache = new HashMap<>(MAXIMUM_CACHE_SIZE);
    private HashMap<K, LocalDateTime> timeCache = new HashMap<>(MAXIMUM_CACHE_SIZE);

    @Override
    public Optional<V> get(K id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public V put(K id, V value) {
        if (value == null) {
            return null;
        }
        if (cache.size() >= MAXIMUM_CACHE_SIZE && !cache.containsKey(id)) {
            cache.remove(timeCache.entrySet().stream().min(Map.Entry
                    .comparingByValue()).get().getKey());
        }
        cache.put(id, value);
        timeCache.put(id, LocalDateTime.now());
        return value;
    }

    @Override
    public void del(K id) {
        cache.remove(id);
    }

    @Override
    public Collection<V> getAllValues() {
        return cache.values();
    }
}