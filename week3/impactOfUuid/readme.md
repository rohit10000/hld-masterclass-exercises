# MySQL Bulk Data Insert Procedures

This repository contains MySQL stored procedures and examples for efficiently inserting large datasets with different primary key strategies.

## Table of Contents
- [Overview](#overview)
- [Table Schemas](#table-schemas)
- [Stored Procedures](#stored-procedures)
- [Performance Metrics](#performance-metrics)
- [Index Management](#index-management)
- [Data Manipulation Examples](#data-manipulation-examples)
- [Monitoring and Analysis](#monitoring-and-analysis)

## Overview

This project demonstrates efficient bulk data insertion techniques in MySQL using stored procedures with different primary key strategies:
- Auto-increment integer primary keys
- Random integer primary keys with duplicate handling
- UUID primary keys

## Table Schemas

### 1. Auto-increment Primary Key Table
```sql
CREATE TABLE user_details(
    id INT AUTO_INCREMENT PRIMARY KEY, 
    age INT
);
```

### 2. Manual Integer Primary Key Table
```sql
CREATE TABLE user_details1(
    id INT PRIMARY KEY, 
    age INT
);
```

### 3. UUID Primary Key Table
```sql
CREATE TABLE user_details2(
    id CHAR(36) PRIMARY KEY, 
    age INT,
    auto_id INT AUTO_INCREMENT UNIQUE
);
```

## Stored Procedures

### 1. Auto-increment Bulk Insert (10M records)
Inserts 10 million records with auto-increment IDs and random ages (18-82).

```sql
DELIMITER $$
CREATE PROCEDURE InsertMillionUsers()
BEGIN
    DECLARE i INT DEFAULT 1;
    SET autocommit = 0;
    
    WHILE i <= 10000000 DO
        INSERT INTO user_details (age) VALUES (FLOOR(18 + RAND() * 65));
        
        IF i % 10000 = 0 THEN
            COMMIT;
        END IF;
        
        SET i = i + 1;
    END WHILE;
    
    COMMIT;
    SET autocommit = 1;
END$$
DELIMITER ;
```

**Performance:** Completed in 1 minute 16 seconds

### 2. UUID Bulk Insert (10M records)
Inserts 10 million records with UUID primary keys.

```sql
DELIMITER $$
CREATE PROCEDURE InsertMillionUsersUUID()
BEGIN
    DECLARE i INT DEFAULT 1;
    SET autocommit = 0;
    
    WHILE i <= 10000000 DO
        INSERT INTO user_details2 (id, age) 
        VALUES (UUID(), FLOOR(18 + RAND() * 65));
        
        IF i % 10000 = 0 THEN
            COMMIT;
        END IF;
        
        SET i = i + 1;
    END WHILE;
    
    COMMIT;
    SET autocommit = 1;
END$$
DELIMITER ;
```

## Usage

### Execute Procedures
```sql
-- Auto-increment insert
CALL InsertMillionUsers();

-- Random ID insert
CALL InsertMillionRandomUsers();

-- UUID insert
CALL InsertMillionUsersUUID();
```

### List Procedures
```sql
SHOW PROCEDURE STATUS WHERE Db = DATABASE();
```

### View Procedure Definition
```sql
SHOW CREATE PROCEDURE InsertMillionUsersUUID;
```

## Performance Metrics

### Table Size Analysis
```sql
SELECT 
    TABLE_NAME as table_name,
    ENGINE,
    TABLE_ROWS as estimated_rows,
    ROUND(DATA_LENGTH / 1024 / 1024, 2) as data_size_mb,
    ROUND(INDEX_LENGTH / 1024 / 1024, 2) as index_size_mb,
    ROUND((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024, 2) as total_size_mb,
    ROUND(INDEX_LENGTH / (DATA_LENGTH + INDEX_LENGTH) * 100, 2) as index_ratio_percent,
    AUTO_INCREMENT as next_auto_increment
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
AND TABLE_TYPE = 'BASE TABLE'
ORDER BY total_size_mb DESC;
```

## Index Management

### Create Index on Age Column
```sql
CREATE INDEX idx_age ON user_details(age);
```

### View Index Information
```sql
SHOW INDEX FROM user_details;
```

### Analyze Index Size
```sql
SELECT 
    INDEX_NAME as index_name,
    GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) as columns,
    INDEX_TYPE,
    CASE 
        WHEN INDEX_NAME = 'PRIMARY' THEN 'Primary Key'
        WHEN NON_UNIQUE = 0 THEN 'Unique Index'
        ELSE 'Regular Index'
    END as type,
    MAX(CARDINALITY) as cardinality,
    ROUND(MAX(CARDINALITY) * 4 / 1024 / 1024, 2) as estimated_size_mb
FROM INFORMATION_SCHEMA.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE()
AND TABLE_NAME = 'user_details'
GROUP BY INDEX_NAME, INDEX_TYPE, NON_UNIQUE
ORDER BY INDEX_NAME;
```

### Update Index Statistics
```sql
ANALYZE TABLE user_details2;
```

## Data Manipulation Examples

### ON DUPLICATE KEY UPDATE
```sql
INSERT INTO user_details(id, age) 
VALUES(3256734, -2) 
ON DUPLICATE KEY UPDATE age = 0;
```

### REPLACE INTO
```sql
REPLACE INTO user_details(id, age) 
VALUES(3256734, -1);
```

### Query Specific Records
```sql
SELECT * FROM user_details WHERE id = 3256734;
```

## Key Features

### Performance Optimizations
- **Batch commits**: Commits every 10,000 rows to balance performance and memory usage
- **Disabled autocommit**: Reduces transaction overhead during bulk operations
- **Duplicate key handling**: Robust error handling for random ID collisions

### Data Characteristics
- **Age range**: All procedures generate ages between 18-82 (65 possible values)
- **Random distribution**: Uses `FLOOR(18 + RAND() * 65)` for age generation
- **UUID format**: Standard UUID v1 format for unique identification

### Index Statistics
- **Cardinality tracking**: Monitors unique value counts in indexes
- **Size estimation**: Calculates approximate index storage requirements
- **Performance impact**: Analyzes index-to-data size ratios


## Notes

- All procedures include proper error handling and transaction management
- Index statistics may require manual refresh using `ANALYZE TABLE`
- Performance varies based on hardware and MySQL configuration
- UUID primary keys have larger storage overhead compared to integers

## Best Practices

1. **Use batch commits** for large bulk operations
2. **Disable autocommit** during bulk inserts
3. **Monitor index cardinality** for query optimization
4. **Analyze tables** after bulk operations to refresh statistics
5. **Consider primary key strategy** based on application requirements