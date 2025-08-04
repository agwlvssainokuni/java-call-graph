package com.example.warapp.service;

import com.example.warapp.model.Product;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for product caching operations.
 * This represents Level 5 in the call hierarchy.
 */
public class ProductCacheService {

    private final Map<Long, Product> cache = new ConcurrentHashMap<>();
    private final Map<String, List<Product>> categoryCache = new ConcurrentHashMap<>();

    public ProductCacheService() {
        // Level 6: Initialize cache
        initializeCache();
    }

    /**
     * Get from cache - Level 5 method
     */
    public Product getFromCache(Long id) {
        System.out.println("Cache: Getting product from cache: " + id);

        // Level 6: Cache lookup
        Product product = performCacheLookup(id);

        if (product != null) {
            // Level 6: Update access statistics
            updateAccessStatistics(id);
        }

        return product;
    }

    /**
     * Put in cache - Level 5 method
     */
    public void putInCache(Product product) {
        System.out.println("Cache: Putting product in cache: " + product.getId());

        // Level 6: Cache storage
        performCacheStorage(product);

        // Level 6: Update cache statistics
        updateCacheStatistics(product.getId());
    }

    /**
     * Update cache entry - Level 5 method
     */
    public void updateCacheEntry(Product product) {
        System.out.println("Cache: Updating cache entry: " + product.getId());

        // Level 6: Update cache
        performCacheUpdate(product);

        // Level 6: Invalidate category cache
        invalidateCategoryCache(product.getCategory());
    }

    /**
     * Remove from cache - Level 5 method
     */
    public void removeFromCache(Long id) {
        System.out.println("Cache: Removing product from cache: " + id);

        // Level 6: Get product before removal (for category invalidation)
        Product product = cache.get(id);

        // Level 6: Cache removal
        performCacheRemoval(id);

        if (product != null) {
            // Level 6: Invalidate category cache
            invalidateCategoryCache(product.getCategory());
        }
    }

    /**
     * Update cache - Level 5 method
     */
    public void updateCache(List<Product> products) {
        System.out.println("Cache: Updating cache with product list");

        // Level 6: Bulk cache update
        performBulkCacheUpdate(products);

        // Level 6: Update category cache
        updateCategoryCache(products);
    }

    /**
     * Clear cache - Level 5 method
     */
    public void clearCache() {
        System.out.println("Cache: Clearing all cache");

        // Level 6: Cache clearing
        performCacheClear();

        // Level 6: Reset statistics
        resetCacheStatistics();
    }

    // Level 6 cache implementation methods

    /**
     * Initialize cache - Level 6 method
     */
    private void initializeCache() {
        System.out.println("Cache: Initializing cache system");

        // Level 7: Cache configuration
        configureCacheSettings();

        // Level 7: Setup cache monitoring
        setupCacheMonitoring();
    }

    /**
     * Perform cache lookup - Level 6 method
     */
    private Product performCacheLookup(Long id) {
        System.out.println("Cache: Performing cache lookup");

        // Level 7: Direct cache access
        return executeCacheLookup(id);
    }

    /**
     * Perform cache storage - Level 6 method
     */
    private void performCacheStorage(Product product) {
        System.out.println("Cache: Performing cache storage");

        cache.put(product.getId(), product);

        // Level 7: Update storage metrics
        updateStorageMetrics(product.getId());
    }

    /**
     * Perform cache update - Level 6 method
     */
    private void performCacheUpdate(Product product) {
        System.out.println("Cache: Performing cache update");

        cache.put(product.getId(), product);

        // Level 7: Update modification metrics
        updateModificationMetrics(product.getId());
    }

    /**
     * Perform cache removal - Level 6 method
     */
    private void performCacheRemoval(Long id) {
        System.out.println("Cache: Performing cache removal");

        cache.remove(id);

        // Level 7: Update removal metrics
        updateRemovalMetrics(id);
    }

    /**
     * Perform bulk cache update - Level 6 method
     */
    private void performBulkCacheUpdate(List<Product> products) {
        System.out.println("Cache: Performing bulk cache update");

        for (Product product : products) {
            cache.put(product.getId(), product);
        }

        // Level 7: Update bulk metrics
        updateBulkUpdateMetrics(products.size());
    }

    /**
     * Update category cache - Level 6 method
     */
    private void updateCategoryCache(List<Product> products) {
        System.out.println("Cache: Updating category cache");

        // Level 7: Group by category and cache
        groupAndCacheByCategory(products);
    }

    /**
     * Invalidate category cache - Level 6 method
     */
    private void invalidateCategoryCache(String category) {
        System.out.println("Cache: Invalidating category cache: " + category);

        categoryCache.remove(category);

        // Level 7: Update invalidation metrics
        updateInvalidationMetrics(category);
    }

    /**
     * Perform cache clear - Level 6 method
     */
    private void performCacheClear() {
        System.out.println("Cache: Performing cache clear");

        cache.clear();
        categoryCache.clear();

        // Level 7: Update clear metrics
        updateClearMetrics();
    }

    /**
     * Update access statistics - Level 6 method
     */
    private void updateAccessStatistics(Long id) {
        System.out.println("Cache: Updating access statistics");

        // Level 7: Record access event
        recordAccessEvent(id);
    }

    /**
     * Update cache statistics - Level 6 method
     */
    private void updateCacheStatistics(Long id) {
        System.out.println("Cache: Updating cache statistics");

        // Level 7: Record cache event
        recordCacheEvent(id);
    }

    /**
     * Reset cache statistics - Level 6 method
     */
    private void resetCacheStatistics() {
        System.out.println("Cache: Resetting cache statistics");

        // Level 7: Reset all metrics
        resetAllMetrics();
    }

    // Level 7 cache utility methods (deepest cache level)

    /**
     * Configure cache settings - Level 7 method
     */
    private void configureCacheSettings() {
        System.out.println("Cache: Configuring cache settings");
    }

    /**
     * Setup cache monitoring - Level 7 method
     */
    private void setupCacheMonitoring() {
        System.out.println("Cache: Setting up cache monitoring");
    }

    /**
     * Execute cache lookup - Level 7 method
     */
    private Product executeCacheLookup(Long id) {
        System.out.println("Cache: Executing cache lookup");
        return cache.get(id);
    }

    /**
     * Update storage metrics - Level 7 method
     */
    private void updateStorageMetrics(Long id) {
        System.out.println("Cache: Updating storage metrics");
    }

    /**
     * Update modification metrics - Level 7 method
     */
    private void updateModificationMetrics(Long id) {
        System.out.println("Cache: Updating modification metrics");
    }

    /**
     * Update removal metrics - Level 7 method
     */
    private void updateRemovalMetrics(Long id) {
        System.out.println("Cache: Updating removal metrics");
    }

    /**
     * Update bulk update metrics - Level 7 method
     */
    private void updateBulkUpdateMetrics(int count) {
        System.out.println("Cache: Updating bulk update metrics");
    }

    /**
     * Group and cache by category - Level 7 method
     */
    private void groupAndCacheByCategory(List<Product> products) {
        System.out.println("Cache: Grouping and caching by category");
    }

    /**
     * Update invalidation metrics - Level 7 method
     */
    private void updateInvalidationMetrics(String category) {
        System.out.println("Cache: Updating invalidation metrics");
    }

    /**
     * Update clear metrics - Level 7 method
     */
    private void updateClearMetrics() {
        System.out.println("Cache: Updating clear metrics");
    }

    /**
     * Record access event - Level 7 method
     */
    private void recordAccessEvent(Long id) {
        System.out.println("Cache: Recording access event");
    }

    /**
     * Record cache event - Level 7 method
     */
    private void recordCacheEvent(Long id) {
        System.out.println("Cache: Recording cache event");
    }

    /**
     * Reset all metrics - Level 7 method
     */
    private void resetAllMetrics() {
        System.out.println("Cache: Resetting all metrics");
    }
}
