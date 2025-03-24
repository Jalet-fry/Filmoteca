package org.example.service;

import java.util.Optional;

public interface InMemoryCache<K, V>  {
    Optional<V> get(K id);

    V put(K id, V value);

    void del(K id);
}
