CREATE TABLE IF NOT EXISTS job_posting (
    id BIGINT NOT NULL AUTO_INCREMENT,
    company_member_id BIGINT NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    job_category VARCHAR(100) NOT NULL,
    employment_type VARCHAR(30) NOT NULL,
    location VARCHAR(150) NOT NULL,
    salary_min INT DEFAULT NULL,
    salary_max INT DEFAULT NULL,
    experience_level VARCHAR(50) DEFAULT NULL,
    education_level VARCHAR(50) DEFAULT NULL,
    description TEXT DEFAULT NULL,
    requirements TEXT DEFAULT NULL,
    preferred_qualifications TEXT DEFAULT NULL,
    accessibility_info TEXT DEFAULT NULL,
    wheelchair_accessible BOOLEAN NOT NULL DEFAULT FALSE,
    accessible_restroom BOOLEAN NOT NULL DEFAULT FALSE,
    disabled_parking BOOLEAN NOT NULL DEFAULT FALSE,
    remote_available BOOLEAN NOT NULL DEFAULT FALSE,
    flexible_work_available BOOLEAN NOT NULL DEFAULT FALSE,
    assistive_device_support BOOLEAN NOT NULL DEFAULT FALSE,
    deadline DATE DEFAULT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_job_posting_member
        FOREIGN KEY (company_member_id) REFERENCES member(id),
    INDEX idx_job_posting_open_list (status, deadline, created_at),
    INDEX idx_job_posting_company (company_member_id, status, created_at)
);

-- Compatibility for databases created by the earlier runtime-DDL implementation.
ALTER TABLE job_posting
    ADD COLUMN IF NOT EXISTS company_name VARCHAR(100) DEFAULT NULL AFTER company_member_id;

ALTER TABLE job_posting
    ADD INDEX IF NOT EXISTS idx_job_posting_open_list (status, deadline, created_at),
    ADD INDEX IF NOT EXISTS idx_job_posting_company (company_member_id, status, created_at);
