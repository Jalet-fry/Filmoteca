package org.example.config;

import org.example.annotations.CacheBean;
import org.example.model.db.Actor;
import org.example.model.db.Director;
import org.example.model.db.Film;
import org.example.service.InMemoryCache;
import org.example.service.implementation.InMemoryCacheImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LabsConfiguration {
    @Bean
    public @CacheBean("actors") InMemoryCache<Long, Actor> actorCache() {
        return new InMemoryCacheImpl<>();
    }

    @Bean
    public @CacheBean("directors") InMemoryCache<Long, Director> directorCache() {
        return new InMemoryCacheImpl<>();
    }

    @Bean
    public @CacheBean("films") InMemoryCache<Long, Film> filmCache() {
        return new InMemoryCacheImpl<>();
    }
}
