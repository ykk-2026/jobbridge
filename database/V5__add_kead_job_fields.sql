ALTER TABLE job_posting
    MODIFY COLUMN company_member_id BIGINT NULL,
    MODIFY COLUMN work_type VARCHAR(20) NULL DEFAULT NULL;

ALTER TABLE job_posting
    ADD COLUMN IF NOT EXISTS salary_amount BIGINT NULL AFTER salary_min,
    ADD COLUMN IF NOT EXISTS salary_type VARCHAR(20) NULL AFTER salary_amount,
    ADD COLUMN IF NOT EXISTS accessibility_verified BOOLEAN NOT NULL DEFAULT TRUE AFTER assistive_device_support,
    ADD COLUMN IF NOT EXISTS source VARCHAR(20) NOT NULL DEFAULT 'LOCAL' AFTER accessibility_verified,
    ADD COLUMN IF NOT EXISTS external_job_id VARCHAR(64) NULL AFTER source,
    ADD COLUMN IF NOT EXISTS contact_number VARCHAR(50) NULL AFTER external_job_id,
    ADD COLUMN IF NOT EXISTS entry_type VARCHAR(30) NULL AFTER contact_number,
    ADD COLUMN IF NOT EXISTS managing_agency VARCHAR(150) NULL AFTER entry_type,
    ADD COLUMN IF NOT EXISTS recruitment_start_date DATE NULL AFTER managing_agency,
    ADD COLUMN IF NOT EXISTS external_apply_date DATE NULL AFTER recruitment_start_date,
    ADD COLUMN IF NOT EXISTS external_registered_date DATE NULL AFTER external_apply_date;

CREATE UNIQUE INDEX IF NOT EXISTS uk_job_posting_external
    ON job_posting (source, external_job_id);

UPDATE job_posting
   SET work_type = NULL
 WHERE source = 'KEAD'
   AND work_type = 'ANY';
