package com.example.warapp.service;

import com.example.warapp.model.Product;

/**
 * Service for product validation operations.
 * This represents Level 5 in the call hierarchy.
 */
public class ProductValidationService {

    /**
     * Validate product - Level 5 method
     */
    public void validateProduct(Product product) {
        System.out.println("Validation: Validating product");

        // Level 6: Individual field validations
        validateProductName(product.getName());
        validateProductPrice(product.getPrice());
        validateProductQuantity(product.getQuantity());
        validateProductCategory(product.getCategory());

        // Level 6: Business rule validations
        validateBusinessRules(product);
    }

    /**
     * Validate product ID - Level 5 method
     */
    public void validateProductId(Long id) {
        System.out.println("Validation: Validating product ID");

        // Level 6: ID validation
        checkIdFormat(id);
        checkIdRange(id);
    }

    /**
     * Validate product exists - Level 5 method
     */
    public void validateProductExists(Long id) {
        System.out.println("Validation: Validating product exists");

        // Level 6: Existence check
        checkProductExistence(id);
    }

    /**
     * Validate category - Level 5 method
     */
    public void validateCategory(String category) {
        System.out.println("Validation: Validating category");

        // Level 6: Category validation
        checkCategoryFormat(category);
        checkCategoryExists(category);
    }

    // Level 6 validation methods

    /**
     * Validate product name - Level 6 method
     */
    private void validateProductName(String name) {
        System.out.println("Validation: Validating product name");

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }

        if (name.length() > 100) {
            throw new IllegalArgumentException("Product name too long");
        }

        // Level 7: Character validation
        validateNameCharacters(name);
    }

    /**
     * Validate product price - Level 6 method
     */
    private void validateProductPrice(Double price) {
        System.out.println("Validation: Validating product price");

        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }

        if (price > 999999.99) {
            throw new IllegalArgumentException("Product price too high");
        }

        // Level 7: Precision validation
        validatePricePrecision(price);
    }

    /**
     * Validate product quantity - Level 6 method
     */
    private void validateProductQuantity(Integer quantity) {
        System.out.println("Validation: Validating product quantity");

        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative");
        }

        if (quantity > 999999) {
            throw new IllegalArgumentException("Product quantity too high");
        }
    }

    /**
     * Validate product category - Level 6 method
     */
    private void validateProductCategory(String category) {
        System.out.println("Validation: Validating product category");

        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Product category cannot be empty");
        }

        // Level 7: Category format validation
        checkCategoryFormat(category);
    }

    /**
     * Validate business rules - Level 6 method
     */
    private void validateBusinessRules(Product product) {
        System.out.println("Validation: Validating business rules");

        // Level 7: Specific business rule checks
        checkPriceCategoryRules(product.getPrice(), product.getCategory());
        checkQuantityPriceRules(product.getQuantity(), product.getPrice());
    }

    /**
     * Check ID format - Level 6 method
     */
    private void checkIdFormat(Long id) {
        System.out.println("Validation: Checking ID format");

        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        // Level 7: Format validation
        validateIdFormat(id);
    }

    /**
     * Check ID range - Level 6 method
     */
    private void checkIdRange(Long id) {
        System.out.println("Validation: Checking ID range");

        if (id <= 0) {
            throw new IllegalArgumentException("Product ID must be positive");
        }

        // Level 7: Range validation
        validateIdRange(id);
    }

    /**
     * Check product existence - Level 6 method
     */
    private void checkProductExistence(Long id) {
        System.out.println("Validation: Checking product existence");

        // Level 7: Existence verification
        verifyProductExists(id);
    }

    /**
     * Check category format - Level 6 method
     */
    private void checkCategoryFormat(String category) {
        System.out.println("Validation: Checking category format");

        if (category.length() > 50) {
            throw new IllegalArgumentException("Category name too long");
        }

        // Level 7: Format checks
        validateCategoryCharacters(category);
    }

    /**
     * Check category exists - Level 6 method
     */
    private void checkCategoryExists(String category) {
        System.out.println("Validation: Checking category exists");

        // Level 7: Category existence validation
        verifyCategoryExists(category);
    }

    // Level 7 validation methods (deepest validation level)

    /**
     * Validate name characters - Level 7 method
     */
    private void validateNameCharacters(String name) {
        System.out.println("Validation: Validating name characters");
        // Simulate character validation
    }

    /**
     * Validate price precision - Level 7 method
     */
    private void validatePricePrecision(Double price) {
        System.out.println("Validation: Validating price precision");
        // Simulate precision validation
    }

    /**
     * Check price category rules - Level 7 method
     */
    private void checkPriceCategoryRules(Double price, String category) {
        System.out.println("Validation: Checking price-category rules");
        // Simulate business rule validation
    }

    /**
     * Check quantity price rules - Level 7 method
     */
    private void checkQuantityPriceRules(Integer quantity, Double price) {
        System.out.println("Validation: Checking quantity-price rules");
        // Simulate business rule validation
    }

    /**
     * Validate ID format - Level 7 method
     */
    private void validateIdFormat(Long id) {
        System.out.println("Validation: Validating ID format");
        // Simulate ID format validation
    }

    /**
     * Validate ID range - Level 7 method
     */
    private void validateIdRange(Long id) {
        System.out.println("Validation: Validating ID range");
        // Simulate ID range validation
    }

    /**
     * Verify product exists - Level 7 method
     */
    private void verifyProductExists(Long id) {
        System.out.println("Validation: Verifying product exists");
        // Simulate existence check
    }

    /**
     * Validate category characters - Level 7 method
     */
    private void validateCategoryCharacters(String category) {
        System.out.println("Validation: Validating category characters");
        // Simulate character validation
    }

    /**
     * Verify category exists - Level 7 method
     */
    private void verifyCategoryExists(String category) {
        System.out.println("Validation: Verifying category exists");
        // Simulate category existence check
    }
}
