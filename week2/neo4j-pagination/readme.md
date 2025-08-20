# Neo4j Setup and Operations Guide

A comprehensive guide for installing, configuring, and working with Neo4j graph database on macOS.

## Table of Contents
- [Installation](#installation)
- [Starting Neo4j](#starting-neo4j)
- [Accessing Neo4j](#accessing-neo4j)
- [Basic Operations](#basic-operations)
- [Data Import](#data-import)
- [Pagination Examples](#pagination-examples)
- [Troubleshooting](#troubleshooting)

## Installation

### Install Neo4j using Homebrew
```bash
brew install neo4j
```

### Verify Installation
```bash
neo4j version
```

## Starting Neo4j

### Option 1: Start as a Service (Recommended)
```bash
brew services start neo4j
```

### Option 2: Start Manually
```bash
neo4j start
```

### Stop Neo4j
```bash
# If running as service
brew services stop neo4j

# If started manually
neo4j stop
```

### Check Status
```bash
neo4j status
```

## Accessing Neo4j

### Web Browser Interface
- **URL:** http://localhost:7474/
- **Username:** `neo4j`
- **Password:** `rohitsingh`

### Command Line Interface
```bash
cypher-shell
```

## Basic Operations

### 1. Test Connection
```cypher
RETURN "Hello Neo4j!" as greeting;
```

### 2. Create Sample Data
```cypher
CREATE (p:Person {name: 'Alice', age: 30, city: 'San Francisco'});
```

### 3. Query Data
```cypher
-- Retrieve all persons
MATCH (p:Person) RETURN p;

-- Sort by age (descending) with limit
MATCH (p:Person) 
RETURN p.age 
ORDER BY p.age DESC 
LIMIT 10;
```

### 4. Filtered Queries
```cypher
-- Example query (Note: This searches for Product nodes, not Person)
MATCH (p:Product) 
WHERE p.name = 'Alice' 
RETURN p.name, p.age;
```

## Data Import

### 1. Check Import Configuration
```cypher
CALL dbms.listConfig()
YIELD name, value
WHERE name CONTAINS 'import'
RETURN name, value;
```

### 2. Prepare CSV File
Copy your CSV file to the Neo4j import directory:

```bash
# Copy CSV to import directory
cp /Users/shwetasingh/Downloads/persons_2000_records.csv /opt/homebrew/Cellar/neo4j/2025.07.1/libexec/import/

# Set proper permissions
chmod 644 /opt/homebrew/Cellar/neo4j/2025.07.1/libexec/import/persons_2000_records.csv
```

> **Note:** The exact path may vary depending on your Neo4j version. Use the path returned by `dbms.listConfig()`.

### 3. Import CSV Data
```cypher
LOAD CSV WITH HEADERS FROM 'file:///persons_2000_records.csv' AS row
CREATE (p:Person {
  name: row.name,
  city: row.city,
  age: toInteger(row.age)
});
```

### 4. Verify Import
```cypher
-- Count imported records
MATCH (p:Person) RETURN count(p) as totalPersons;

-- Show sample data
MATCH (p:Person) 
RETURN p.name, p.city, p.age 
LIMIT 10;
```

## Pagination Examples

### Basic Pagination
```cypher
-- Page 1: First 100 records
MATCH (p:Person)
RETURN p.name, p.city, p.age
ORDER BY p.name
LIMIT 100;

-- Page 2: Next 100 records  
MATCH (p:Person)
RETURN p.name, p.city, p.age
ORDER BY p.name
SKIP 100
LIMIT 100;

-- Page N: Skip to specific page
MATCH (p:Person)
RETURN p.name, p.city, p.age
ORDER BY p.name
SKIP 2000 
LIMIT 100;
```

### Advanced Pagination
```cypher
-- Pagination with filtering
MATCH (p:Person)
WHERE p.age > 30
RETURN p.name, p.city, p.age
ORDER BY p.age DESC
SKIP 0
LIMIT 50;

-- Cursor-based pagination (more efficient for large datasets)
MATCH (p:Person)
WHERE p.name > 'LastNameFromPreviousPage'
RETURN p.name, p.city, p.age
ORDER BY p.name
LIMIT 100;
```

## Useful Commands

### Database Information
```cypher
-- Show all node labels
CALL db.labels();

-- Show all relationship types
CALL db.relationshipTypes();

-- Count nodes by label
MATCH (n)
RETURN labels(n) as nodeType, count(n) as count
ORDER BY count DESC;
```

### Performance Monitoring
```cypher
-- Analyze query performance
PROFILE MATCH (p:Person)
RETURN p.name, p.age
ORDER BY p.name
SKIP 100
LIMIT 10;

-- Explain query execution plan
EXPLAIN MATCH (p:Person)
WHERE p.age > 25
RETURN p.name, p.city;
```

### Index Management
```cypher
-- Create indexes for better performance
CREATE INDEX person_name FOR (p:Person) ON (p.name);
CREATE INDEX person_age FOR (p:Person) ON (p.age);
CREATE INDEX person_city FOR (p:Person) ON (p.city);

-- Show all indexes
SHOW INDEXES;

-- Drop an index
DROP INDEX person_age;
```

## Troubleshooting

### Common Issues

#### 1. Neo4j Won't Start
```bash
# Check if port is already in use
lsof -i :7474
lsof -i :7687

# Check Neo4j logs
tail -f /opt/homebrew/var/log/neo4j.log
```

#### 2. CSV Import Errors
```bash
# Verify file exists and has correct permissions
ls -la /opt/homebrew/Cellar/neo4j/*/libexec/import/

# Check file format
head -5 /path/to/your/file.csv
```

#### 3. Permission Issues
```bash
# Fix file permissions
chmod 644 /path/to/neo4j/import/your-file.csv

# Fix directory permissions
chmod 755 /path/to/neo4j/import/
```

### Helpful Commands
```bash
# Find Neo4j installation directory
brew --prefix neo4j

# Check Neo4j process
ps aux | grep neo4j

# View Neo4j configuration
cat /opt/homebrew/etc/neo4j/neo4j.conf
```

## Configuration Files

### Important Paths
- **Configuration:** `/opt/homebrew/etc/neo4j/neo4j.conf`
- **Data Directory:** `/opt/homebrew/var/neo4j/data`
- **Import Directory:** `/opt/homebrew/Cellar/neo4j/[version]/libexec/import`
- **Logs:** `/opt/homebrew/var/log/neo4j.log`

### Common Configuration Changes
```bash
# Edit configuration file
nano /opt/homebrew/etc/neo4j/neo4j.conf

# Enable CSV import from any location
dbms.security.allow_csv_import_from_file_urls=true

# Increase memory (if needed)
server.memory.heap.initial_size=1G
server.memory.heap.max_size=2G
```

## Best Practices

1. **Always use ORDER BY** with pagination queries
2. **Create indexes** on frequently queried properties
3. **Use PROFILE/EXPLAIN** to optimize query performance
4. **Prefer cursor-based pagination** for large datasets
5. **Regular backups** of your database
6. **Monitor memory usage** for large imports

## Additional Resources

- [Neo4j Documentation](https://neo4j.com/docs/)
- [Cypher Query Language Guide](https://neo4j.com/docs/cypher-manual/)
- [Neo4j Browser Guide](https://neo4j.com/docs/browser-manual/)

---

**Author:** Your Name  
**Last Updated:** $(date)  
**Neo4j Version:** 2025.07.1