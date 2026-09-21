ALTER TABLE job_seeker_profile
    ADD COLUMN IF NOT EXISTS work_type VARCHAR(20) NOT NULL DEFAULT 'ANY'
        COMMENT '희망 근무방식: OFFICE(출근), REMOTE(재택), HYBRID(하이브리드), ANY(무관)';

UPDATE job_seeker_profile
SET work_type = CASE
    WHEN hybrid_preferred = TRUE OR (remote_preferred = TRUE AND onsite_preferred = TRUE) THEN 'HYBRID'
    WHEN remote_preferred = TRUE THEN 'REMOTE'
    WHEN onsite_preferred = TRUE THEN 'OFFICE'
    ELSE 'ANY'
END;

ALTER TABLE job_seeker_profile
    ADD CONSTRAINT chk_job_seeker_profile_work_type
        CHECK (work_type IN ('OFFICE', 'REMOTE', 'HYBRID', 'ANY'));

ALTER TABLE job_posting
    ADD COLUMN IF NOT EXISTS work_type VARCHAR(20) NOT NULL DEFAULT 'ANY'
        COMMENT '공고 근무방식: OFFICE(출근), REMOTE(재택), HYBRID(하이브리드), ANY(무관)';

UPDATE job_posting
SET work_type = CASE
    WHEN remote_available = TRUE AND flexible_work_available = TRUE THEN 'HYBRID'
    WHEN remote_available = TRUE THEN 'REMOTE'
    ELSE 'OFFICE'
END;

ALTER TABLE job_posting
    ADD CONSTRAINT chk_job_posting_work_type
        CHECK (work_type IN ('OFFICE', 'REMOTE', 'HYBRID', 'ANY'));
