-- 근무방식은 work_type 단일 컬럼으로만 관리한다.
ALTER TABLE job_seeker_profile
    DROP COLUMN IF EXISTS remote_preferred,
    DROP COLUMN IF EXISTS flexible_preferred,
    DROP COLUMN IF EXISTS hybrid_preferred,
    DROP COLUMN IF EXISTS onsite_preferred;

ALTER TABLE job_posting
    DROP COLUMN IF EXISTS remote_available,
    DROP COLUMN IF EXISTS flexible_work_available;
