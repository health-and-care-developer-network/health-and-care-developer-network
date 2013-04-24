-- Mac setup:
-- brew install postgres
-- initdb /usr/local/var/postgres -E utf8
-- postgres -D /usr/local/var/postgres
-- psql --host=localhost --port=5432 --password --user=raph --dbname=postgres
-- CREATE USER postgres SUPERUSER UNENCRYPTED PASSWORD 'postgres';

-- Check if available
-- SELECT * FROM pg_available_extensions;

-- We may need to do apt-get install postgresql-contrib
DROP EXTENSION IF EXISTS "uuid-ossp" CASCADE;

-- SPLIT

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- SPLIT

CREATE OR REPLACE FUNCTION provider_identifier() RETURNS UUID AS
$$
DECLARE

    provider_identifier CONSTANT UUID NOT NULL := '2DBF298F-EED9-474D-BF8B-D70F68B83417';

BEGIN

    RETURN provider_identifier;

END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- SPLIT

CREATE OR REPLACE FUNCTION repository_identifier() RETURNS UUID AS
$$
DECLARE

    repository_identifier CONSTANT UUID NOT NULL := '3b942abe-3ec0-4830-bdb9-d1197dadf520';

BEGIN

    RETURN provider_identifier;

END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- SPLIT

CREATE OR REPLACE FUNCTION stuff_identifier() RETURNS UUID AS
$$
DECLARE

    stuff_identifier CONSTANT UUID NOT NULL := '599dbd25-3c3e-4b7a-868b-37b653f394dd';

BEGIN

    RETURN stuff_identifier;

END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- SPLIT

CREATE OR REPLACE FUNCTION parse_trigger_operation(trigger_operation VARCHAR) RETURNS VARCHAR AS
$$
BEGIN

    RETURN CASE trigger_operation
        WHEN 'INSERT' THEN 'Created'

        WHEN 'UPDATE' THEN 'Updated'

        WHEN 'DELETE' THEN 'Removed'

        WHEN 'TRUNCATE' THEN 'Removed'

        ELSE 'Unknown'
    END;

END;
$$ LANGUAGE plpgsql;

