-- 관심 공고에는 회원/채용공고 관계만 저장하고 공고 상세 정보는 job_posting에서 조회한다.
-- 실제 공고를 찾을 수 없는 과거 데이터는 FK 적용 전에 별도 테이블에 보관한다.
CREATE TABLE IF NOT EXISTS interest_job_orphan_archive LIKE interest_job;

INSERT IGNORE INTO interest_job_orphan_archive
SELECT I.*
  FROM interest_job I
 WHERE NOT EXISTS (
       SELECT 1
         FROM job_posting J
        WHERE J.company_name = I.company_name
          AND J.title = I.title
 );

ALTER TABLE interest_job
    ADD COLUMN IF NOT EXISTS job_id BIGINT DEFAULT NULL AFTER member_id;

UPDATE interest_job I
  JOIN (
       SELECT company_name, title, MAX(id) AS job_id
         FROM job_posting
        GROUP BY company_name, title
  ) J ON J.company_name = I.company_name
     AND J.title = I.title
   SET I.job_id = J.job_id
 WHERE I.job_id IS NULL;

DELETE FROM interest_job WHERE job_id IS NULL;

ALTER TABLE interest_job
    DROP INDEX IF EXISTS uk_interest_job_member_post,
    MODIFY COLUMN job_id BIGINT NOT NULL,
    DROP COLUMN IF EXISTS company_name,
    DROP COLUMN IF EXISTS title,
    DROP COLUMN IF EXISTS job_category,
    DROP COLUMN IF EXISTS employment_type,
    DROP COLUMN IF EXISTS location,
    DROP COLUMN IF EXISTS salary_min,
    DROP COLUMN IF EXISTS salary_max,
    DROP COLUMN IF EXISTS experience_level,
    DROP COLUMN IF EXISTS education_level,
    DROP COLUMN IF EXISTS description,
    DROP COLUMN IF EXISTS requirements,
    DROP COLUMN IF EXISTS preferred_qualifications,
    DROP COLUMN IF EXISTS accessibility_info,
    DROP COLUMN IF EXISTS wheelchair_accessible,
    DROP COLUMN IF EXISTS accessible_restroom,
    DROP COLUMN IF EXISTS disabled_parking,
    DROP COLUMN IF EXISTS remote_available,
    DROP COLUMN IF EXISTS flexible_work_available,
    DROP COLUMN IF EXISTS assistive_device_support,
    DROP COLUMN IF EXISTS deadline,
    DROP COLUMN IF EXISTS status,
    DROP COLUMN IF EXISTS updated_at,
    ADD UNIQUE INDEX uk_interest_job_member_post (member_id, job_id),
    ADD CONSTRAINT fk_interest_job_posting
        FOREIGN KEY (job_id) REFERENCES job_posting(id)
        ON DELETE CASCADE ON UPDATE CASCADE;
