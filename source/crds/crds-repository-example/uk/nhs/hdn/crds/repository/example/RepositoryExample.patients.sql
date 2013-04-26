
DROP TABLE IF EXISTS patients CASCADE;

-- SPLIT

CREATE TABLE IF NOT EXISTS patients(patient_identifier_nhs_number CHAR(10) NOT NULL, firstname VARCHAR(32), lastname VARCHAR(32), address VARCHAR(64));

-- SPLIT

ALTER TABLE patients OWNER TO postgres;

-- SPLIT

CREATE OR REPLACE FUNCTION patients_create_notify() RETURNS trigger AS
$$
DECLARE

    provider_identifier CONSTANT UUID NOT NULL := provider_identifier();

    repository_identifier CONSTANT UUID NOT NULL := provider_identifier();

    stuff_identifier CONSTANT UUID NOT NULL := provider_identifier();

    patient_identifier_nhs_number CONSTANT CHAR(10) NOT NULL := CASE TG_OP WHEN 'DELETE' THEN OLD.patient_identifier_nhs_number ELSE NEW.patient_identifier_nhs_number END;

    -- There is also the possibility that a patient's id is changed. That should generate 2 messages - Create and Delete

    our_uuid CONSTANT UUID NOT NULL := uuid_generate_v4();

    our_timestamp CONSTANT TIMESTAMPTZ NOT NULL := CURRENT_TIMESTAMP(3);
    our_seconds_rounded_down CONSTANT INT8 NOT NULL := trunc(EXTRACT(SECOND FROM our_timestamp));
    our_milliseconds CONSTANT INT8 NOT NULL := EXTRACT(MILLISECONDS FROM our_timestamp);
    incremental_milliseconds CONSTANT INT8 NOT NULL := our_milliseconds - our_seconds_rounded_down;
    epoch_in_milliseconds CONSTANT INT8 NOT NULL := EXTRACT(epoch FROM our_timestamp) * 1000 + incremental_milliseconds;

    repository_event CONSTANT VARCHAR NOT NULL := parse_trigger_operation(TG_OP);

    message CONSTANT VARCHAR NOT NULL := '"' || CAST(provider_identifier AS VARCHAR) || '","' || CAST(repository_identifier AS VARCHAR) || CAST(stuff_identifier AS VARCHAR) || '","' || patient_identifier_nhs_number || '","' || CAST(our_uuid AS VARCHAR) || '","' || CAST(epoch_in_milliseconds AS VARCHAR) || '","' || repository_event || '"';

BEGIN

    PERFORM pg_notify('patients', message);

    RETURN NULL;

END;
$$ LANGUAGE plpgsql VOLATILE COST 100;

-- SPLIT

ALTER FUNCTION patients_create_notify() OWNER TO postgres;

-- SPLIT

DROP TRIGGER IF EXISTS patients_create_notify ON patients;

-- SPLIT

CREATE TRIGGER patients_create_notify AFTER INSERT OR UPDATE OR DELETE ON patients FOR EACH ROW EXECUTE PROCEDURE patients_create_notify();

