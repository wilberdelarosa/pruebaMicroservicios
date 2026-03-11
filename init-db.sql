-- Create tables for all services sharing the same database
-- JPA/Hibernate will auto-create tables, but this ensures the DB exists

-- No additional SQL needed since all services use the same 'eventos_db' database
-- and Hibernate ddl-auto=update will create tables automatically.
SELECT 'Database eventos_db initialized successfully' AS status;
