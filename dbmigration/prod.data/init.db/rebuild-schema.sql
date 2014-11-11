DROP TABLE IF EXISTS @schema@.databasechangelog;
DROP TABLE IF EXISTS @schema@.databasechangeloglock;
DROP SCHEMA IF EXISTS @schema@ CASCADE;
CREATE SCHEMA @schema@ AUTHORIZATION @schema@;