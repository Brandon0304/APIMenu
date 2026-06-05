package com.restaurant.menu.infrastructure.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager();
        manager.registerCustomCache("categories", Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .maximumSize(200)
            .recordStats()
            .build());

        manager.registerCustomCache("menuItems", Caffeine.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .maximumSize(500)
            .recordStats()
            .build());

        manager.registerCustomCache("menus", Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .maximumSize(100)
            .recordStats()
            .build());

        manager.registerCustomCache("ingredients", Caffeine.newBuilder()
            .expireAfterWrite(120, TimeUnit.SECONDS)
            .maximumSize(200)
            .recordStats()
            .build());

        manager.registerCustomCache("allergens", Caffeine.newBuilder()
            .expireAfterWrite(300, TimeUnit.SECONDS)
            .maximumSize(100)
            .recordStats()
            .build());

        manager.registerCustomCache("modifierGroups", Caffeine.newBuilder()
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .maximumSize(200)
            .recordStats()
            .build());

        return manager;
    }

    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public WebMvcConfigurer cacheControlConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new CacheControlInterceptor());
            }
        };
    }

    record CacheTtl(int seconds) {}

    static class CacheControlInterceptor implements org.springframework.web.servlet.HandlerInterceptor {

        @Override
        public void postHandle(jakarta.servlet.http.HttpServletRequest request,
                                jakarta.servlet.http.HttpServletResponse response,
                                Object handler, org.springframework.web.servlet.ModelAndView modelAndView) {
            if ("GET".equals(request.getMethod()) && response.getStatus() == 200) {
                String path = request.getRequestURI();
                CacheControl cc = resolveCacheControl(path);
                if (cc != null) {
                    response.setHeader("Cache-Control", cc.getHeaderValue());
                }
            }
        }

        private CacheControl resolveCacheControl(String path) {
            if (path.contains("/categories")) return CacheControl.maxAge(60, TimeUnit.SECONDS);
            if (path.contains("/menu-items")) return CacheControl.maxAge(30, TimeUnit.SECONDS);
            if (path.contains("/menus")) return CacheControl.maxAge(60, TimeUnit.SECONDS);
            if (path.contains("/ingredients")) return CacheControl.maxAge(120, TimeUnit.SECONDS);
            if (path.contains("/allergens")) return CacheControl.maxAge(300, TimeUnit.SECONDS);
            if (path.contains("/modifier-groups")) return CacheControl.maxAge(60, TimeUnit.SECONDS);
            return null;
        }
    }
}
