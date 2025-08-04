package com.example.warapp.service;

import com.example.warapp.dao.ProductDao;
import com.example.warapp.model.Product;

import java.util.List;

/**
 * Service layer for product management operations.
 * This represents Level 4 in the call hierarchy.
 */
public class ProductService {

    private final ProductDao productDao;
    private final ProductValidationService validationService;
    private final ProductCacheService cacheService;

    public ProductService() {
        // Manual dependency injection for call graph clarity
        this.productDao = new ProductDao();
        this.validationService = new ProductValidationService();
        this.cacheService = new ProductCacheService();

        // Level 5: Service initialization
        initializeService();
    }

    /**
     * Find all products - Level 4 method called from servlet
     */
    public List<Product> findAllProducts() {
        System.out.println("Service: Finding all products");

        // Level 5: DAO call
        List<Product> products = productDao.findAll();

        // Level 5: Post-processing
        enrichProductList(products);

        // Level 5: Cache update
        cacheService.updateCache(products);

        return products;
    }

    /**
     * Find product by ID - Level 4 method
     */
    public Product findProductById(Long id) {
        System.out.println("Service: Finding product by ID: " + id);

        // Level 5: Validation
        validationService.validateProductId(id);

        // Level 5: Check cache first  
        Product cachedProduct = cacheService.getFromCache(id);
        if (cachedProduct != null) {
            return cachedProduct;
        }

        // Level 5: DAO call
        Product product = productDao.findById(id);

        if (product != null) {
            // Level 5: Enrich product data
            enrichProductData(product);

            // Level 5: Update cache
            cacheService.putInCache(product);
        }

        return product;
    }

    /**
     * Create new product - Level 4 method
     */
    public Product createProduct(Product product) {
        System.out.println("Service: Creating new product: " + product.getName());

        // Level 5: Validation
        validationService.validateProduct(product);

        // Level 5: Pre-creation processing
        preprocessForCreation(product);

        // Level 5: DAO call
        Product createdProduct = productDao.save(product);

        // Level 5: Post-creation processing
        postprocessAfterCreation(createdProduct);

        // Level 5: Cache update
        cacheService.putInCache(createdProduct);

        return createdProduct;
    }

    /**
     * Update product - Level 4 method
     */
    public Product updateProduct(Product product) {
        System.out.println("Service: Updating product: " + product.getId());

        // Level 5: Validation
        validationService.validateProduct(product);
        validationService.validateProductExists(product.getId());

        // Level 5: Pre-update processing
        preprocessForUpdate(product);

        // Level 5: DAO call
        Product updatedProduct = productDao.update(product);

        if (updatedProduct != null) {
            // Level 5: Post-update processing
            postprocessAfterUpdate(updatedProduct);

            // Level 5: Cache update
            cacheService.updateCacheEntry(updatedProduct);
        }

        return updatedProduct;
    }

    /**
     * Delete product - Level 4 method
     */
    public boolean deleteProduct(Long id) {
        System.out.println("Service: Deleting product ID: " + id);

        // Level 5: Validation
        validationService.validateProductId(id);
        validationService.validateProductExists(id);

        // Level 5: Check for dependencies
        if (hasDependentData(id)) {
            throw new IllegalStateException("Cannot delete product with dependent data");
        }

        // Level 5: Pre-deletion processing
        preprocessForDeletion(id);

        // Level 5: DAO call
        boolean deleted = productDao.deleteById(id);

        if (deleted) {
            // Level 5: Post-deletion cleanup
            cleanupAfterDeletion(id);

            // Level 5: Cache removal
            cacheService.removeFromCache(id);
        }

        return deleted;
    }

    /**
     * Get product count - Level 4 method
     */
    public int getProductCount() {
        System.out.println("Service: Getting product count");

        // Level 5: DAO call
        return productDao.count();
    }

    /**
     * Search products by category - Level 4 method
     */
    public List<Product> findProductsByCategory(String category) {
        System.out.println("Service: Finding products by category: " + category);

        // Level 5: Validation
        validationService.validateCategory(category);

        // Level 5: DAO call
        List<Product> products = productDao.findByCategory(category);

        // Level 5: Post-processing
        enrichProductList(products);

        return products;
    }

    // Level 5 internal methods

    /**
     * Initialize service - Level 5 method
     */
    private void initializeService() {
        System.out.println("Service: Initializing ProductService");

        // Level 6: Service setup
        setupServiceConfiguration();

        // Level 6: Initialize connections
        initializeConnections();
    }

    /**
     * Enrich product list - Level 5 method
     */
    private void enrichProductList(List<Product> products) {
        System.out.println("Service: Enriching product list");

        for (Product product : products) {
            // Level 6: Individual enrichment
            enrichProductData(product);
        }

        // Level 6: List-level processing
        sortProductList(products);
    }

    /**
     * Enrich product data - Level 5 method
     */
    private void enrichProductData(Product product) {
        System.out.println("Service: Enriching product data for ID: " + product.getId());

        // Level 6: Add computed fields
        addComputedFields(product);

        // Level 6: Add metadata
        addProductMetadata(product);
    }

    /**
     * Preprocess for creation - Level 5 method
     */
    private void preprocessForCreation(Product product) {
        System.out.println("Service: Preprocessing for creation");

        // Level 6: Set default values
        setDefaultValues(product);

        // Level 6: Generate identifiers
        generateProductIdentifiers(product);
    }

    /**
     * Postprocess after creation - Level 5 method
     */
    private void postprocessAfterCreation(Product product) {
        System.out.println("Service: Postprocessing after creation");

        // Level 6: Trigger events
        triggerCreatedEvent(product);

        // Level 6: Update indices
        updateSearchIndices(product);
    }

    /**
     * Preprocess for update - Level 5 method
     */
    private void preprocessForUpdate(Product product) {
        System.out.println("Service: Preprocessing for update");

        // Level 6: Preserve certain fields
        preserveImmutableFields(product);

        // Level 6: Update timestamps
        updateModificationTimestamp(product);
    }

    /**
     * Postprocess after update - Level 5 method
     */
    private void postprocessAfterUpdate(Product product) {
        System.out.println("Service: Postprocessing after update");

        // Level 6: Trigger events
        triggerUpdatedEvent(product);

        // Level 6: Update indices
        updateSearchIndices(product);
    }

    /**
     * Check for dependent data - Level 5 method
     */
    private boolean hasDependentData(Long productId) {
        System.out.println("Service: Checking for dependent data");

        // Level 6: Check various dependencies
        return checkOrderDependencies(productId) || checkInventoryDependencies(productId);
    }

    /**
     * Preprocess for deletion - Level 5 method
     */
    private void preprocessForDeletion(Long id) {
        System.out.println("Service: Preprocessing for deletion");

        // Level 6: Archive data
        archiveProductData(id);

        // Level 6: Notify dependent systems
        notifyDependentSystems(id);
    }

    /**
     * Cleanup after deletion - Level 5 method
     */
    private void cleanupAfterDeletion(Long id) {
        System.out.println("Service: Cleaning up after deletion");

        // Level 6: Remove related data
        removeRelatedData(id);

        // Level 6: Update indices
        removeFromSearchIndices(id);
    }

    // Level 6 utility methods (deepest business logic level)

    /**
     * Setup service configuration - Level 6 method
     */
    private void setupServiceConfiguration() {
        System.out.println("Service: Setting up service configuration");
    }

    /**
     * Initialize connections - Level 6 method
     */
    private void initializeConnections() {
        System.out.println("Service: Initializing connections");
    }

    /**
     * Sort product list - Level 6 method
     */
    private void sortProductList(List<Product> products) {
        System.out.println("Service: Sorting product list");
        products.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
    }

    /**
     * Add computed fields - Level 6 method
     */
    private void addComputedFields(Product product) {
        System.out.println("Service: Adding computed fields");
    }

    /**
     * Add product metadata - Level 6 method
     */
    private void addProductMetadata(Product product) {
        System.out.println("Service: Adding product metadata");
    }

    /**
     * Set default values - Level 6 method
     */
    private void setDefaultValues(Product product) {
        System.out.println("Service: Setting default values");
    }

    /**
     * Generate product identifiers - Level 6 method
     */
    private void generateProductIdentifiers(Product product) {
        System.out.println("Service: Generating product identifiers");
    }

    /**
     * Trigger created event - Level 6 method
     */
    private void triggerCreatedEvent(Product product) {
        System.out.println("Service: Triggering created event");
    }

    /**
     * Update search indices - Level 6 method
     */
    private void updateSearchIndices(Product product) {
        System.out.println("Service: Updating search indices");
    }

    /**
     * Preserve immutable fields - Level 6 method
     */
    private void preserveImmutableFields(Product product) {
        System.out.println("Service: Preserving immutable fields");
    }

    /**
     * Update modification timestamp - Level 6 method
     */
    private void updateModificationTimestamp(Product product) {
        System.out.println("Service: Updating modification timestamp");
    }

    /**
     * Trigger updated event - Level 6 method
     */
    private void triggerUpdatedEvent(Product product) {
        System.out.println("Service: Triggering updated event");
    }

    /**
     * Check order dependencies - Level 6 method
     */
    private boolean checkOrderDependencies(Long productId) {
        System.out.println("Service: Checking order dependencies");
        return false; // Simulate no dependencies
    }

    /**
     * Check inventory dependencies - Level 6 method
     */
    private boolean checkInventoryDependencies(Long productId) {
        System.out.println("Service: Checking inventory dependencies");
        return false; // Simulate no dependencies
    }

    /**
     * Archive product data - Level 6 method
     */
    private void archiveProductData(Long id) {
        System.out.println("Service: Archiving product data");
    }

    /**
     * Notify dependent systems - Level 6 method
     */
    private void notifyDependentSystems(Long id) {
        System.out.println("Service: Notifying dependent systems");
    }

    /**
     * Remove related data - Level 6 method
     */
    private void removeRelatedData(Long id) {
        System.out.println("Service: Removing related data");
    }

    /**
     * Remove from search indices - Level 6 method
     */
    private void removeFromSearchIndices(Long id) {
        System.out.println("Service: Removing from search indices");
    }
}
