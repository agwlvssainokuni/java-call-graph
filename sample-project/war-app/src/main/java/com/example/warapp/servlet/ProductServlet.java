package com.example.warapp.servlet;

import com.example.warapp.service.ProductService;
import com.example.warapp.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Product management servlet for traditional WAR deployment.
 * This represents Level 2 in the call hierarchy (after Tomcat servlet container).
 * <p>
 * Call hierarchy levels:
 * 1. Tomcat Container → Servlet methods
 * 2. ProductServlet methods
 * 3. ProductService methods
 * 4. ProductDao methods
 * 5. Database/utility operations
 */
@WebServlet(name = "ProductServlet", urlPatterns = {"/api/products", "/api/products/*"})
public class ProductServlet extends HttpServlet {

    private ProductService productService;
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        System.out.println("Servlet: Initializing ProductServlet");

        // Level 3: Initialize service layer (manual dependency injection)
        this.productService = new ProductService();
        this.objectMapper = new ObjectMapper();

        // Level 3: Additional initialization
        initializeServletResources();

        System.out.println("Servlet: ProductServlet initialized successfully");
    }

    /**
     * Handle GET requests - Level 2 method (called by Tomcat)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Servlet: Handling GET request");

        String pathInfo = request.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Level 3: Get all products
                getAllProducts(request, response);
            } else {
                // Level 3: Get specific product
                getProductById(request, response, pathInfo);
            }

            // Level 3: Log request
            logRequest("GET", request.getRequestURI());

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    /**
     * Handle POST requests - Level 2 method (called by Tomcat)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Servlet: Handling POST request");

        try {
            // Level 3: Create new product
            createProduct(request, response);

            // Level 3: Log request
            logRequest("POST", request.getRequestURI());

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    /**
     * Handle PUT requests - Level 2 method (called by Tomcat)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Servlet: Handling PUT request");

        try {
            // Level 3: Update product
            updateProduct(request, response);

            // Level 3: Log request
            logRequest("PUT", request.getRequestURI());

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    /**
     * Handle DELETE requests - Level 2 method (called by Tomcat)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Servlet: Handling DELETE request");

        try {
            // Level 3: Delete product
            deleteProduct(request, response);

            // Level 3: Log request
            logRequest("DELETE", request.getRequestURI());

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    // Level 3 request handling methods

    /**
     * Get all products - Level 3 method
     */
    private void getAllProducts(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("Servlet: Getting all products");

        // Level 4: Service call
        List<Product> products = productService.findAllProducts();

        // Level 4: Additional processing
        int totalCount = productService.getProductCount();

        // Response processing
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Level 4: JSON serialization
        String jsonResponse = createProductListResponse(products, totalCount);
        response.getWriter().write(jsonResponse);
    }

    /**
     * Get product by ID - Level 3 method
     */
    private void getProductById(HttpServletRequest request, HttpServletResponse response, String pathInfo)
            throws IOException {
        System.out.println("Servlet: Getting product by ID");

        try {
            Long id = extractIdFromPath(pathInfo);

            // Level 4: Service call
            Product product = productService.findProductById(id);

            if (product != null) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");

                // Level 4: JSON serialization
                String jsonResponse = objectMapper.writeValueAsString(product);
                response.getWriter().write(jsonResponse);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
        }
    }

    /**
     * Create new product - Level 3 method
     */
    private void createProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("Servlet: Creating new product");

        // Level 4: Parse request body
        Product product = parseProductFromRequest(request);

        // Level 4: Service call for creation
        Product createdProduct = productService.createProduct(product);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Level 4: JSON serialization
        String jsonResponse = objectMapper.writeValueAsString(createdProduct);
        response.getWriter().write(jsonResponse);
    }

    /**
     * Update product - Level 3 method
     */
    private void updateProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("Servlet: Updating product");

        String pathInfo = request.getPathInfo();
        Long id = extractIdFromPath(pathInfo);

        // Level 4: Parse request body
        Product product = parseProductFromRequest(request);
        product.setId(id);

        // Level 4: Service call for update
        Product updatedProduct = productService.updateProduct(product);

        if (updatedProduct != null) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            // Level 4: JSON serialization
            String jsonResponse = objectMapper.writeValueAsString(updatedProduct);
            response.getWriter().write(jsonResponse);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
        }
    }

    /**
     * Delete product - Level 3 method
     */
    private void deleteProduct(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("Servlet: Deleting product");

        String pathInfo = request.getPathInfo();
        Long id = extractIdFromPath(pathInfo);

        // Level 4: Service call for deletion
        boolean deleted = productService.deleteProduct(id);

        if (deleted) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
        }
    }

    // Level 4 utility methods

    /**
     * Initialize servlet resources - Level 3 method
     */
    private void initializeServletResources() {
        System.out.println("Servlet: Initializing servlet resources");

        // Level 4: Setup configurations
        configureObjectMapper();

        // Level 4: Initialize monitoring
        setupRequestMonitoring();
    }

    /**
     * Create product list response - Level 4 method
     */
    private String createProductListResponse(List<Product> products, int totalCount) throws IOException {
        System.out.println("Servlet: Creating product list response");

        // Level 5: Build response object
        var response = new ProductListResponse(products, totalCount);

        return objectMapper.writeValueAsString(response);
    }

    /**
     * Parse product from request - Level 4 method
     */
    private Product parseProductFromRequest(HttpServletRequest request) throws IOException {
        System.out.println("Servlet: Parsing product from request");

        // Level 5: Read request body
        String requestBody = readRequestBody(request);

        return objectMapper.readValue(requestBody, Product.class);
    }

    /**
     * Extract ID from path - Level 4 method
     */
    private Long extractIdFromPath(String pathInfo) {
        System.out.println("Servlet: Extracting ID from path");

        // Level 5: Path parsing
        return parseIdFromPath(pathInfo);
    }

    /**
     * Log request - Level 3 method
     */
    private void logRequest(String method, String uri) {
        System.out.println("Servlet: Logging request - " + method + " " + uri);

        // Level 4: Detailed logging
        writeAccessLog(method, uri, System.currentTimeMillis());
    }

    /**
     * Handle error - Level 3 method
     */
    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        System.err.println("Servlet: Handling error - " + e.getMessage());

        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Internal server error: " + e.getMessage());

        // Level 4: Error logging
        logError(e);
    }

    // Level 5 utility methods (deepest servlet level)

    /**
     * Configure object mapper - Level 4 method
     */
    private void configureObjectMapper() {
        System.out.println("Servlet: Configuring ObjectMapper");
    }

    /**
     * Setup request monitoring - Level 4 method
     */
    private void setupRequestMonitoring() {
        System.out.println("Servlet: Setting up request monitoring");
    }

    /**
     * Read request body - Level 5 method
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        System.out.println("Servlet: Reading request body");

        StringBuilder buffer = new StringBuilder();
        String line;

        while ((line = request.getReader().readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }

    /**
     * Parse ID from path - Level 5 method
     */
    private Long parseIdFromPath(String pathInfo) {
        System.out.println("Servlet: Parsing ID from path");

        String[] pathSegments = pathInfo.split("/");
        return Long.parseLong(pathSegments[pathSegments.length - 1]);
    }

    /**
     * Write access log - Level 4 method
     */
    private void writeAccessLog(String method, String uri, long timestamp) {
        System.out.println("Servlet: Writing access log entry");
    }

    /**
     * Log error - Level 4 method
     */
    private void logError(Exception e) {
        System.err.println("Servlet: Logging error details");
        e.printStackTrace();
    }

    // Response DTO
    private record ProductListResponse(List<Product> products, int totalCount) {
    }
}
