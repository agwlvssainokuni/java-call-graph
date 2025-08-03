package com.example.warapp.dao;

import com.example.warapp.model.Product;
import com.example.warapp.util.DatabaseUtil;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Data Access Object for Product entity operations.
 * This represents Level 5 in the call hierarchy.
 */
public class ProductDao {

    private final Map<Long, Product> database = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final DatabaseUtil databaseUtil;

    public ProductDao() {
        this.databaseUtil = new DatabaseUtil();
        
        // Level 6: Initialize DAO
        initializeDao();
        
        // Level 6: Create sample data
        createSampleData();
    }

    /**
     * Find all products - Level 5 method called from service
     */
    public List<Product> findAll() {
        System.out.println("DAO: Finding all products");
        
        // Level 6: Database query execution
        List<Product> products = executeQuery("SELECT * FROM products");
        
        // Level 6: Post-query processing
        processQueryResults(products);
        
        return products;
    }

    /**
     * Find product by ID - Level 5 method
     */
    public Product findById(Long id) {
        System.out.println("DAO: Finding product by ID: " + id);
        
        // Level 6: Database query execution
        Product product = executeSingleQuery("SELECT * FROM products WHERE id = ?", id);
        
        if (product != null) {
            // Level 6: Post-retrieval processing
            processRetrievedProduct(product);
        }
        
        return product;
    }

    /**
     * Save product - Level 5 method
     */
    public Product save(Product product) {
        System.out.println("DAO: Saving product: " + product.getName());
        
        // Level 6: Pre-save processing
        preprocessForSave(product);
        
        if (product.getId() == null) {
            // Level 6: Insert operation
            return executeInsert(product);
        } else {
            // Level 6: Update operation  
            return executeUpdate(product);
        }
    }

    /**
     * Update product - Level 5 method
     */
    public Product update(Product product) {
        System.out.println("DAO: Updating product: " + product.getId());
        
        // Level 6: Pre-update processing
        preprocessForUpdate(product);
        
        // Level 6: Update execution
        Product updatedProduct = executeUpdate(product);
        
        if (updatedProduct != null) {
            // Level 6: Post-update processing
            postprocessAfterUpdate(updatedProduct);
        }
        
        return updatedProduct;
    }

    /**
     * Delete by ID - Level 5 method
     */
    public boolean deleteById(Long id) {
        System.out.println("DAO: Deleting product by ID: " + id);
        
        // Level 6: Pre-deletion processing
        preprocessForDeletion(id);
        
        // Level 6: Delete execution
        boolean deleted = executeDelete(id);
        
        if (deleted) {
            // Level 6: Post-deletion processing
            postprocessAfterDeletion(id);
        }
        
        return deleted;
    }

    /**
     * Count all products - Level 5 method
     */
    public int count() {
        System.out.println("DAO: Counting all products");
        
        // Level 6: Count query execution
        return executeCountQuery("SELECT COUNT(*) FROM products");
    }

    /**
     * Find products by category - Level 5 method
     */
    public List<Product> findByCategory(String category) {
        System.out.println("DAO: Finding products by category: " + category);
        
        // Level 6: Filtered query execution
        List<Product> products = executeFilteredQuery("SELECT * FROM products WHERE category = ?", category);
        
        // Level 6: Post-query processing
        processQueryResults(products);
        
        return products;
    }

    /**
     * Find products by price range - Level 5 method
     */
    public List<Product> findByPriceRange(Double minPrice, Double maxPrice) {
        System.out.println("DAO: Finding products by price range: " + minPrice + " - " + maxPrice);
        
        // Level 6: Range query execution
        return executeRangeQuery("SELECT * FROM products WHERE price BETWEEN ? AND ?", minPrice, maxPrice);
    }

    // Level 6 DAO implementation methods

    /**
     * Initialize DAO - Level 6 method
     */
    private void initializeDao() {
        System.out.println("DAO: Initializing ProductDao");
        
        // Level 7: Database connection setup
        setupDatabaseConnection();
        
        // Level 7: Initialize database schema
        initializeDatabaseSchema();
    }

    /**
     * Create sample data - Level 6 method
     */
    private void createSampleData() {
        System.out.println("DAO: Creating sample data");
        
        // Level 7: Sample data creation
        generateSampleProducts();
    }

    /**
     * Execute query - Level 6 method
     */
    private List<Product> executeQuery(String sql) {
        System.out.println("DAO: Executing query: " + sql);
        
        // Level 7: Database operation (simulated with in-memory data)
        return performDatabaseQuery();
    }

    /**
     * Execute single query - Level 6 method
     */
    private Product executeSingleQuery(String sql, Long id) {
        System.out.println("DAO: Executing single query: " + sql);
        
        // Level 7: Database operation
        return performSingleDatabaseQuery(id);
    }

    /**
     * Execute insert - Level 6 method
     */
    private Product executeInsert(Product product) {
        System.out.println("DAO: Executing insert operation");
        
        // Level 7: Insert operation
        return performInsertOperation(product);
    }

    /**
     * Execute update - Level 6 method
     */
    private Product executeUpdate(Product product) {
        System.out.println("DAO: Executing update operation");
        
        // Level 7: Update operation
        return performUpdateOperation(product);
    }

    /**
     * Execute delete - Level 6 method
     */
    private boolean executeDelete(Long id) {
        System.out.println("DAO: Executing delete operation");
        
        // Level 7: Delete operation
        return performDeleteOperation(id);
    }

    /**
     * Execute count query - Level 6 method
     */
    private int executeCountQuery(String sql) {
        System.out.println("DAO: Executing count query: " + sql);
        
        // Level 7: Count operation
        return performCountOperation();
    }

    /**
     * Execute filtered query - Level 6 method
     */
    private List<Product> executeFilteredQuery(String sql, String category) {
        System.out.println("DAO: Executing filtered query: " + sql);
        
        // Level 7: Filtered operation
        return performFilteredOperation(category);
    }

    /**
     * Execute range query - Level 6 method
     */
    private List<Product> executeRangeQuery(String sql, Double min, Double max) {
        System.out.println("DAO: Executing range query: " + sql);
        
        // Level 7: Range operation
        return performRangeOperation(min, max);
    }

    /**
     * Process query results - Level 6 method
     */
    private void processQueryResults(List<Product> products) {
        System.out.println("DAO: Processing query results");
        
        // Level 7: Result processing
        enhanceQueryResults(products);
    }

    /**
     * Process retrieved product - Level 6 method
     */
    private void processRetrievedProduct(Product product) {
        System.out.println("DAO: Processing retrieved product");
        
        // Level 7: Product enhancement
        enhanceProductData(product);
    }

    /**
     * Preprocess for save - Level 6 method
     */
    private void preprocessForSave(Product product) {
        System.out.println("DAO: Preprocessing for save");
        
        // Level 7: Pre-save validation and setup
        validateForSave(product);
        setupForSave(product);
    }

    /**
     * Preprocess for update - Level 6 method
     */
    private void preprocessForUpdate(Product product) {
        System.out.println("DAO: Preprocessing for update");
        
        // Level 7: Pre-update validation and setup
        validateForUpdate(product);
        setupForUpdate(product);
    }

    /**
     * Preprocess for deletion - Level 6 method
     */
    private void preprocessForDeletion(Long id) {
        System.out.println("DAO: Preprocessing for deletion");
        
        // Level 7: Pre-deletion validation and setup
        validateForDeletion(id);
        setupForDeletion(id);
    }

    /**
     * Postprocess after update - Level 6 method
     */
    private void postprocessAfterUpdate(Product product) {
        System.out.println("DAO: Postprocessing after update");
        
        // Level 7: Post-update operations
        logUpdateOperation(product);
        notifyUpdateListeners(product);
    }

    /**
     * Postprocess after deletion - Level 6 method
     */
    private void postprocessAfterDeletion(Long id) {
        System.out.println("DAO: Postprocessing after deletion");
        
        // Level 7: Post-deletion operations
        logDeleteOperation(id);
        notifyDeleteListeners(id);
    }

    // Level 7 database utility methods (deepest data access level)

    /**
     * Setup database connection - Level 7 method
     */
    private void setupDatabaseConnection() {
        System.out.println("DAO: Setting up database connection");
        
        // Level 8: Utility call
        databaseUtil.initializeConnection();
    }

    /**
     * Initialize database schema - Level 7 method
     */
    private void initializeDatabaseSchema() {
        System.out.println("DAO: Initializing database schema");
        
        // Level 8: Utility call
        databaseUtil.createSchema();
    }

    /**
     * Generate sample products - Level 7 method
     */
    private void generateSampleProducts() {
        System.out.println("DAO: Generating sample products");
        
        save(new Product("Laptop", "High-performance laptop", 999.99, 10, "Electronics"));
        save(new Product("Book", "Programming guide", 29.99, 50, "Books"));
        save(new Product("Coffee", "Premium coffee beans", 19.99, 100, "Food"));
    }

    /**
     * Perform database query - Level 7 method
     */
    private List<Product> performDatabaseQuery() {
        System.out.println("DAO: Performing database query");
        return new ArrayList<>(database.values());
    }

    /**
     * Perform single database query - Level 7 method
     */
    private Product performSingleDatabaseQuery(Long id) {
        System.out.println("DAO: Performing single database query");
        return database.get(id);
    }

    /**
     * Perform insert operation - Level 7 method
     */
    private Product performInsertOperation(Product product) {
        System.out.println("DAO: Performing insert operation");
        
        product.setId(idGenerator.getAndIncrement());
        database.put(product.getId(), product);
        
        // Level 8: Utility call
        databaseUtil.logOperation("INSERT", product.getId());
        
        return product;
    }

    /**
     * Perform update operation - Level 7 method
     */
    private Product performUpdateOperation(Product product) {
        System.out.println("DAO: Performing update operation");
        
        database.put(product.getId(), product);
        
        // Level 8: Utility call
        databaseUtil.logOperation("UPDATE", product.getId());
        
        return product;
    }

    /**
     * Perform delete operation - Level 7 method
     */
    private boolean performDeleteOperation(Long id) {
        System.out.println("DAO: Performing delete operation");
        
        boolean removed = database.remove(id) != null;
        
        if (removed) {
            // Level 8: Utility call
            databaseUtil.logOperation("DELETE", id);
        }
        
        return removed;
    }

    /**
     * Perform count operation - Level 7 method
     */
    private int performCountOperation() {
        System.out.println("DAO: Performing count operation");
        return database.size();
    }

    /**
     * Perform filtered operation - Level 7 method
     */
    private List<Product> performFilteredOperation(String category) {
        System.out.println("DAO: Performing filtered operation");
        
        return database.values().stream()
                .filter(p -> category.equals(p.getCategory()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Perform range operation - Level 7 method
     */
    private List<Product> performRangeOperation(Double min, Double max) {
        System.out.println("DAO: Performing range operation");
        
        return database.values().stream()
                .filter(p -> p.getPrice() >= min && p.getPrice() <= max)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Enhance query results - Level 7 method
     */
    private void enhanceQueryResults(List<Product> products) {
        System.out.println("DAO: Enhancing query results");
        
        // Level 8: Utility call
        databaseUtil.enhanceResults(products);
    }

    /**
     * Enhance product data - Level 7 method
     */
    private void enhanceProductData(Product product) {
        System.out.println("DAO: Enhancing product data");
        
        // Level 8: Utility call
        databaseUtil.enhanceProduct(product);
    }

    /**
     * Validate for save - Level 7 method
     */
    private void validateForSave(Product product) {
        System.out.println("DAO: Validating for save");
        
        // Level 8: Utility call
        databaseUtil.validateProduct(product);
    }

    /**
     * Setup for save - Level 7 method
     */
    private void setupForSave(Product product) {
        System.out.println("DAO: Setting up for save");
        
        // Level 8: Utility call
        databaseUtil.prepareForSave(product);
    }

    /**
     * Validate for update - Level 7 method
     */
    private void validateForUpdate(Product product) {
        System.out.println("DAO: Validating for update");
        
        // Level 8: Utility call
        databaseUtil.validateProduct(product);
    }

    /**
     * Setup for update - Level 7 method
     */
    private void setupForUpdate(Product product) {
        System.out.println("DAO: Setting up for update");
        
        // Level 8: Utility call
        databaseUtil.prepareForUpdate(product);
    }

    /**
     * Validate for deletion - Level 7 method
     */
    private void validateForDeletion(Long id) {
        System.out.println("DAO: Validating for deletion");
        
        // Level 8: Utility call
        databaseUtil.validateForDeletion(id);
    }

    /**
     * Setup for deletion - Level 7 method
     */
    private void setupForDeletion(Long id) {
        System.out.println("DAO: Setting up for deletion");
        
        // Level 8: Utility call
        databaseUtil.prepareForDeletion(id);
    }

    /**
     * Log update operation - Level 7 method
     */
    private void logUpdateOperation(Product product) {
        System.out.println("DAO: Logging update operation");
        
        // Level 8: Utility call
        databaseUtil.logOperation("POST_UPDATE", product.getId());
    }

    /**
     * Notify update listeners - Level 7 method
     */
    private void notifyUpdateListeners(Product product) {
        System.out.println("DAO: Notifying update listeners");
        
        // Level 8: Utility call
        databaseUtil.notifyListeners("UPDATE", product.getId());
    }

    /**
     * Log delete operation - Level 7 method
     */
    private void logDeleteOperation(Long id) {
        System.out.println("DAO: Logging delete operation");
        
        // Level 8: Utility call
        databaseUtil.logOperation("POST_DELETE", id);
    }

    /**
     * Notify delete listeners - Level 7 method
     */
    private void notifyDeleteListeners(Long id) {
        System.out.println("DAO: Notifying delete listeners");
        
        // Level 8: Utility call
        databaseUtil.notifyListeners("DELETE", id);
    }
}