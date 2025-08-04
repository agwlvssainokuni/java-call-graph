-- Initial data for task management demo
INSERT INTO tasks (id, title, description, status, created_at)
VALUES (1, 'Setup Development Environment', 'Install and configure development tools', 'COMPLETED', 1640995200000),
       (2, 'Design Database Schema', 'Create entity relationship diagram and table structures', 'COMPLETED',
        1640995200000),
       (3, 'Implement REST API', 'Create controllers, services, and repositories', 'IN_PROGRESS', 1640995200000),
       (4, 'Write Unit Tests', 'Create comprehensive test coverage', 'PENDING', 1640995200000),
       (5, 'Deploy to Production', 'Configure CI/CD pipeline and deploy', 'PENDING', 1640995200000);
