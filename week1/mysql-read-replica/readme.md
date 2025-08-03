# MySQL Read Replica Setup Guide

This guide walks you through setting up MySQL read replicas on macOS.

## Step 1: Prerequisites

- macOS 15 with MySQL 9.3 installed
- Administrator privileges
- Basic knowledge of MySQL and terminal commands

## Step 2: Create Directory Structure for Replica Instance

```bash
# Create directories for replica instance
sudo mkdir -p /usr/local/mysql-replica/data
sudo mkdir -p /usr/local/mysql-replica/logs

# Set proper ownership
sudo chown -R _mysql:_mysql /usr/local/mysql-replica

# Verify ownership
ls -la /usr/local/mysql-replica
```

## Step 3: Create Configuration Files

### Source Configuration File

```bash
# Create source config
sudo nano /usr/local/mysql-replica/my-source.cnf
```

Add this content:

```ini
[mysqld]
server-id=1
port=3306
socket=/tmp/mysql-source.sock
datadir=/usr/local/mysql/data
log-bin=mysql-bin
log-error=/usr/local/mysql/data/mysql-source.err
pid-file=/usr/local/mysql/data/mysql-source.pid
```

### Replica Configuration File

```bash
# Create replica config
sudo nano /usr/local/mysql-replica/my-replica.cnf
```

Add this content:

```ini
[mysqld]
server-id=2
port=3307
socket=/tmp/mysql-replica.sock
datadir=/usr/local/mysql-replica/data
log-bin=mysql-replica-bin
log-error=/usr/local/mysql-replica/logs/mysql-replica.err
pid-file=/usr/local/mysql-replica/data/mysql-replica.pid
relay-log=mysql-replica-relay-bin
```

## Step 4: Initialize Replica Data Directory

```bash
# Initialize replica database
sudo /usr/local/mysql/bin/mysqld --defaults-file=/usr/local/mysql-replica/my-replica.cnf --initialize --user=_mysql
```

## Step 5: Environment Variable Setup

```bash
# Add MySQL to PATH (for Zsh)
echo 'export PATH="/usr/local/mysql/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify PATH
which mysql
echo $PATH
```

## Step 6: Start Both MySQL Instances

### Start Source Server (Port 3306)

```bash
# Stop any existing MySQL
sudo launchctl unload -w /Library/LaunchDaemons/com.oracle.oss.mysql.mysqld.plist

# Start source server
sudo /usr/local/mysql/bin/mysqld_safe --defaults-file=/usr/local/mysql-replica/my-source.cnf --user=_mysql &

# Verify source is running
sudo lsof -i :3306
```

### Start Replica Server (Port 3307)

```bash
# Start replica server
sudo /usr/local/mysql/bin/mysqld_safe --defaults-file=/usr/local/mysql-replica/my-replica.cnf --user=_mysql &

# Verify replica is running
sudo lsof -i :3307

# Check both processes
ps aux | grep mysqld
```

## Step 7: Configure Source Database

### Connect to Source

```bash
mysql -u root -p --socket=/tmp/mysql-source.sock
```

### Create Database and Replication User

```sql
-- Create test database
CREATE DATABASE hertever;
USE hertever;

-- Create test table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50),
    email VARCHAR(100)
);

-- Insert sample data
INSERT INTO users (name, email) VALUES 
('Alice', 'alice@example.com'),
('Bob', 'bob@example.com');

-- Create replication user
CREATE USER 'root'@'localhost' IDENTIFIED BY 'password_is_password';
GRANT REPLICATION SLAVE ON *.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

-- Verify configuration
SHOW VARIABLES LIKE 'log_bin'; -- this should be on
SELECT @@server_id; -- this should be different than that of replication server.
SHOW DATABASES;
```

## Step 8: Get Binary Log Coordinates and Data Snapshot

### Lock Tables and Get Position

```sql
-- Lock tables (keep this session open!)
FLUSH TABLES WITH READ LOCK;
```

```sql
-- Get binary log coordinates
 SHOW BINARY LOG STATUS\G;
```

**Record the File and Position values!** Example:

```
*************************** 1. row ***************************
             File: mysql-bin.000001
         Position: 1805
     Binlog_Do_DB: 
 Binlog_Ignore_DB: 
Executed_Gtid_Set: 
1 row in set (0.000 sec)
```

```sql
-- Exit this session
EXIT;
```

## Step 9: Create Data Snapshot
```bash
# Create database dump with replication coordinates
sudo mysqldump -u root -p --socket=/tmp/mysql-source.sock --databases hertever --source-data > ~/hertever_dump.sql

# Verify dump file was created
ls -la ~/hertever_dump.sql
```

### Release Table Lock

```sql
-- In the first session where tables are locked
UNLOCK TABLES;
EXIT;
```

## Step 10: Configure Replica Server

### Connect to Replica and Change Password

```bash
mysql -u root -p --socket=/tmp/mysql-replica.sock
# Use temporary password from initialization
```

```sql
-- Change root password
ALTER USER 'root'@'localhost' IDENTIFIED BY 'password_is_password';
EXIT;
```

### Import Data to Replica

```bash
# Import the database dump
mysql -u root -p --socket=/tmp/mysql-replica.sock < ~/hertever_dump.sql
```

## Step 11: Set Up Replication

### Connect to Replica

```bash
mysql -u root -p --socket=/tmp/mysql-replica.sock
```

### Configure Replication

```sql
-- Configure replication source (use your SHOW MASTER STATUS values)
CHANGE REPLICATION SOURCE TO
    SOURCE_HOST='localhost',
    SOURCE_PORT=3306,
    SOURCE_USER='replication_user',
    SOURCE_PASSWORD='replication_password',
    SOURCE_LOG_FILE='mysql-bin.000001',  -- Your File value
    SOURCE_LOG_POS=1234;                 -- Your Position value

-- Verify server ID is different
SELECT @@server_id; -- this should be different than that of source.

-- Start replication
START REPLICA;

-- Check replication status
SHOW REPLICA STATUS\G
```


## Additional Commands

Connect directly to the hertever database:

```bash
mysql -h host -u user -p hertever
``` 



