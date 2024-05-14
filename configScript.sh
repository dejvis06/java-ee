#!/bin/bash

# Admin credentials
ADMIN_USER=admin
ADMIN_PASSWORD=admin

# Start Payara Server
asadmin start-domain --verbose

# Authenticate to Payara Server
asadmin login --user=$ADMIN_USER --passwordfile=/opt/payara/passwordfile

# Create JDBC connection pool
asadmin --user $ADMIN_USER --passwordfile=/opt/payara/passwordfile create-jdbc-connection-pool \
    --datasourceclassname org.postgresql.ds.PGSimpleDataSource \
    --restype javax.sql.DataSource \
    --property ServerName=postgres:PortNumber=5432:DatabaseName=postgres:User=postgres:Password=postgres:URL=jdbc\\:postgresql\\://postgres\\:5432/postgres \
    PostgresPool

# Create JDBC resource
asadmin --user $ADMIN_USER --passwordfile=/opt/payara/passwordfile create-jdbc-resource \
    --connectionpoolid PostgresPool \
    jdbc/postgresDataSource

# Reference the resource in the server
asadmin --user $ADMIN_USER --passwordfile=/opt/payara/passwordfile set resources.jdbc-connection-pool.PostgresPool.property.URL="jdbc:postgresql://postgres:5432/postgres"

# Deploy the application
asadmin --user $ADMIN_USER --passwordfile=/opt/payara/passwordfile deploy /opt/payara/deployments/java-ee-0.0.1-SNAPSHOT.war

# Keep the Payara Server running in the foreground
tail -f /opt/payara/appserver/glassfish/domains/domain1/logs/server.log