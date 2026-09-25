ALTER TABLE job_seeker_profile
    ADD COLUMN IF NOT EXISTS education_level VARCHAR(30) NOT NULL DEFAULT 'ANY'
    AFTER work_type;
