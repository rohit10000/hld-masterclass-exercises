# ProxySQL Docker Setup Guide

ProxySQL is a high-performance MySQL proxy with advanced features like load balancing, connection pooling, and read/write splitting. This guide shows you how to set up ProxySQL using Docker and configure it for MySQL load balancing.

## Quick Start

### 1. Pull and Run ProxySQL Container

```bash
# Pull the latest ProxySQL image
docker pull proxysql/proxysql:latest

# Run ProxySQL container with basic port mapping
docker run -d --name proxysql -p 6033:6033 -p 6032:6032 proxysql/proxysql:latest
```

### 2. Verify Installation

```bash
# Access the container shell
docker exec -it proxysql bash

# Check ProxySQL version
proxysql --version
```

### 3. Connect to Admin Interface

```bash
# Connect to ProxySQL admin interface
docker exec -it proxysql mysql -u admin -padmin -h 127.0.0.1 -P 6032 --prompt='ProxySQLAdmin> '
```

## Advanced Configuration

### Port Mapping Explained

For production deployments with custom port mappings:

```bash
docker run -p 16032:6032 -p 16033:6033 -p 16070:6070 -d \
  -v /path/to/proxysql.cnf:/etc/proxysql.cnf \
  proxysql/proxysql
```

**Port Configuration:**
- **Admin Interface**: `16032` (host) → `6032` (container) - Administrative MySQL interface
- **MySQL Proxy**: `16033` (host) → `6033` (container) - Main MySQL proxy port for applications
- **Web Interface**: `16070` (host) → `6070` (container) - Web-based statistics and monitoring

## Basic Load Balancing Setup

Once connected to the admin interface, configure your MySQL infrastructure:

### 1. Add MySQL Servers

```sql
-- Add primary MySQL server (hostgroup 1 for writes)
INSERT INTO mysql_servers(hostgroup_id, hostname, port, weight) VALUES 
(1, 'mysql-primary.com', 3306, 1000);

-- Add replica MySQL server (hostgroup 2 for reads)
INSERT INTO mysql_servers(hostgroup_id, hostname, port, weight) VALUES 
(2, 'mysql-replica.com', 3306, 1000);
```

### 2. Configure Application Users

```sql
-- Create user with default hostgroup for writes
INSERT INTO mysql_users(username, password, default_hostgroup, max_connections) VALUES 
('app_user', 'secure_password', 1, 200);
```

### 3. Set Up Read/Write Splitting

```sql
-- Route SELECT queries to read replicas (hostgroup 2)
INSERT INTO mysql_query_rules(rule_id, active, match_pattern, destination_hostgroup, apply) VALUES 
(10, 1, '^SELECT.*', 2, 1);

-- Route write operations to primary (hostgroup 1) - implicit default
-- INSERT, UPDATE, DELETE will go to default_hostgroup (1)
```

### 4. Apply Configuration

```sql
-- Load configuration to runtime
LOAD MYSQL SERVERS TO RUNTIME;
LOAD MYSQL USERS TO RUNTIME;
LOAD MYSQL QUERY RULES TO RUNTIME;

-- Persist configuration to disk
SAVE MYSQL SERVERS TO DISK;
SAVE MYSQL USERS TO DISK;
SAVE MYSQL QUERY RULES TO DISK;
```

## Testing Your Setup

### Connect Your Application

Update your application's database connection to point to ProxySQL:

```bash
# Instead of connecting directly to MySQL:
mysql -u app_user -p -h mysql-primary.com -P 3306

# Connect through ProxySQL:
mysql -u app_user -p -h your-docker-host -P 6033
```

### Monitor Traffic

```sql
-- Check server statistics
SELECT * FROM stats_mysql_connection_pool;

-- View query routing
SELECT * FROM stats_mysql_query_rules;

-- Monitor server health
SELECT * FROM mysql_servers;
```

## Common Configuration Patterns

### Health Checks

```sql
-- Enable health checks for all servers
UPDATE mysql_servers SET max_replication_lag = 10 WHERE hostgroup_id = 2;
```

### Connection Limits

```sql
-- Set per-server connection limits
UPDATE mysql_servers SET max_connections = 100;
```

### Custom Routing Rules

```sql
-- Route specific database queries
INSERT INTO mysql_query_rules(rule_id, active, match_pattern, destination_hostgroup, apply) VALUES 
(20, 1, '^SELECT.*FROM analytics.*', 3, 1);  -- Analytics to dedicated server
```

## Troubleshooting

### View Logs
```bash
docker logs proxysql
```

### Common Issues
- **Connection refused**: Check port mappings and firewall rules
- **Authentication failed**: Verify mysql_users configuration
- **Queries not routing**: Check mysql_query_rules are loaded and active

### Reset Configuration
```sql
-- Clear and reload default configuration
LOAD MYSQL VARIABLES FROM DISK;
LOAD MYSQL SERVERS FROM DISK;
LOAD MYSQL USERS FROM DISK;
LOAD MYSQL QUERY RULES FROM DISK;
```

## Production Considerations

- Mount configuration files as volumes for persistence
- Use environment variables for sensitive credentials
- Implement proper monitoring and alerting
- Configure SSL/TLS for secure connections
- Set up proper logging and log rotation

## Useful Resources

- [ProxySQL Documentation](https://proxysql.com/documentation/)
- [MySQL Query Rules Reference](https://proxysql.com/documentation/proxysql-configuration/)
- [ProxySQL GitHub Repository](https://github.com/sysown/proxysql)