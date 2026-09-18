-- job_application은 채용공고 ID만 보관하고, 회사명/공고명은 job_posting에서 조회한다.
-- 현재 공고가 존재하지 않는 과거 지원서는 FK 전환 전에 별도 보관하여 데이터 유실을 막는다.
CREATE TABLE IF NOT EXISTS job_application_orphan_archive LIKE job_application;

INSERT IGNORE INTO job_application_orphan_archive
SELECT A.*
  FROM job_application A
  LEFT JOIN job_posting J ON J.id = CAST(A.job_id AS UNSIGNED)
 WHERE J.id IS NULL;

DELETE A
  FROM job_application A
  LEFT JOIN job_posting J ON J.id = CAST(A.job_id AS UNSIGNED)
 WHERE J.id IS NULL;

ALTER TABLE job_application
    MODIFY COLUMN job_id BIGINT NOT NULL,
    DROP COLUMN company_name,
    DROP COLUMN job_title,
    ADD INDEX idx_job_application_job_id (job_id),
    ADD CONSTRAINT fk_job_application_job
        FOREIGN KEY (job_id) REFERENCES job_posting(id)
        ON DELETE RESTRICT ON UPDATE CASCADE;
