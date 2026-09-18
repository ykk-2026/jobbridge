CREATE TABLE IF NOT EXISTS ai_job_posting_recommendation (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    total_score INT NOT NULL,
    job_score INT NOT NULL,
    job_match_source VARCHAR(30) NOT NULL DEFAULT 'RULE_FALLBACK',
    job_match_reason VARCHAR(1000) NOT NULL DEFAULT '',
    region_score INT NOT NULL,
    employment_score INT NOT NULL,
    career_score INT NOT NULL,
    salary_score INT NOT NULL,
    work_style_score INT NOT NULL,
    accessibility_score INT NOT NULL,
    recommendation_reason VARCHAR(2000) NOT NULL,
    mismatch_reason VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_ai_posting_recommendation_member_job UNIQUE (member_id, job_id),
    CONSTRAINT fk_ai_posting_recommendation_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_ai_posting_recommendation_job FOREIGN KEY (job_id) REFERENCES job_posting(id)
);

-- Compatibility for databases created by the earlier runtime-DDL implementation.
ALTER TABLE ai_job_posting_recommendation
    ADD COLUMN IF NOT EXISTS job_match_source VARCHAR(30) NOT NULL DEFAULT 'RULE_FALLBACK',
    ADD COLUMN IF NOT EXISTS job_match_reason VARCHAR(1000) NOT NULL DEFAULT '';
