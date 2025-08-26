package com.vivekk.authservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.io.IOException;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CommandLineRunner checkCacheManager(CacheManager cacheManager) {
        return args -> log.info("CacheManager in use: " + cacheManager.getClass().getName());
    }

    @Bean
    public CacheManager cacheManager() throws IOException {
        CachingProvider provider = Caching.getCachingProvider();
        ClassPathResource ehcacheConfig = new ClassPathResource("/ehcache.xml");
        if (!ehcacheConfig.exists()) {
            throw new IllegalStateException("Ehcache configuration not found at classpath:/ehcache.xml");
        }
        javax.cache.CacheManager jCacheManager = provider.getCacheManager(
                ehcacheConfig.getURI(), getClass().getClassLoader()
        );
        jCacheManager.enableManagement("refreshTokens", true); // Enforce strict checks
        return new JCacheCacheManager(jCacheManager);
    }
}
