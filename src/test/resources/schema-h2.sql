CREATE TABLE IF NOT EXISTS member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    birth_date DATE,
    gender VARCHAR(20),
    email VARCHAR(255),
    phone VARCHAR(30),
    role VARCHAR(30) NOT NULL DEFAULT 'JOB_SEEKER',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS job_seeker_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL UNIQUE,
    name VARCHAR(50),
    birth_date DATE,
    gender VARCHAR(20),
    email VARCHAR(100),
    phone VARCHAR(20),
    profile_image_url VARCHAR(1000),
    residence_region VARCHAR(100),
    desired_job VARCHAR(100),
    desired_region VARCHAR(100),
    employment_type VARCHAR(50),
    career_type VARCHAR(50),
    career_years INT,
    min_salary INT,
    work_type VARCHAR(20) NOT NULL DEFAULT 'ANY' CHECK (work_type IN ('OFFICE', 'REMOTE', 'HYBRID', 'ANY')),
    education_level VARCHAR(30) NOT NULL DEFAULT 'ANY',
    wheelchair_required BOOLEAN DEFAULT FALSE,
    accessible_restroom_required BOOLEAN DEFAULT FALSE,
    disabled_parking_required BOOLEAN DEFAULT FALSE,
    assistive_device_required BOOLEAN DEFAULT FALSE,
    rest_area_required BOOLEAN DEFAULT FALSE,
    elevator_required BOOLEAN DEFAULT FALSE,
    contact_time_start TIME,
    contact_time_end TIME,
    contact_method VARCHAR(50),
    introduction VARCHAR(2000),
    profile_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_profile_member FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE IF NOT EXISTS job_posting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_member_id BIGINT,
    company_name VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    job_category VARCHAR(100) NOT NULL,
    employment_type VARCHAR(30) NOT NULL,
    location VARCHAR(150) NOT NULL,
    salary_min INT,
    salary_amount BIGINT,
    salary_type VARCHAR(20),
    work_type VARCHAR(20) CHECK (work_type IN ('OFFICE', 'REMOTE', 'HYBRID', 'ANY')),
    experience_level VARCHAR(50),
    education_level VARCHAR(50),
    description CLOB,
    requirements CLOB,
    preferred_qualifications CLOB,
    accessibility_info CLOB,
    wheelchair_accessible BOOLEAN NOT NULL DEFAULT FALSE,
    accessible_restroom BOOLEAN NOT NULL DEFAULT FALSE,
    disabled_parking BOOLEAN NOT NULL DEFAULT FALSE,
    assistive_device_support BOOLEAN NOT NULL DEFAULT FALSE,
    rest_area_available BOOLEAN NOT NULL DEFAULT FALSE,
    elevator_available BOOLEAN NOT NULL DEFAULT FALSE,
    accessibility_verified BOOLEAN NOT NULL DEFAULT TRUE,
    source VARCHAR(20) NOT NULL DEFAULT 'LOCAL',
    external_job_id VARCHAR(64),
    contact_number VARCHAR(50),
    entry_type VARCHAR(30),
    managing_agency VARCHAR(150),
    recruitment_start_date DATE,
    external_apply_date DATE,
    external_registered_date DATE,
    deadline DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_posting_member FOREIGN KEY (company_member_id) REFERENCES member(id),
    CONSTRAINT uk_job_posting_external UNIQUE (source, external_job_id)
);

CREATE TABLE IF NOT EXISTS interest_job (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interest_job_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_interest_job_posting FOREIGN KEY (job_id) REFERENCES job_posting(id) ON DELETE CASCADE,
    CONSTRAINT uk_interest_job_member_post UNIQUE (member_id, job_id)
);

CREATE TABLE IF NOT EXISTS ai_job_posting_recommendation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    education_score INT NOT NULL,
    accessibility_score INT NOT NULL,
    recommendation_reason VARCHAR(2000) NOT NULL,
    mismatch_reason VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_ai_posting_recommendation_member_job UNIQUE (member_id, job_id),
    CONSTRAINT fk_ai_posting_recommendation_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_ai_posting_recommendation_job FOREIGN KEY (job_id) REFERENCES job_posting(id)
);

CREATE TABLE IF NOT EXISTS job_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    applicant_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    employment_type VARCHAR(50),
    cover_letter CLOB,
    status VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_application_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT fk_job_application_job FOREIGN KEY (job_id) REFERENCES job_posting(id),
    CONSTRAINT uk_job_application_member_job UNIQUE (member_id, job_id)
);

CREATE TABLE IF NOT EXISTS company_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL UNIQUE,
    company_name VARCHAR(100) NOT NULL,
    business_number VARCHAR(50) NOT NULL UNIQUE,
    representative_name VARCHAR(50) NOT NULL,
    industry VARCHAR(100),
    company_address VARCHAR(255) NOT NULL,
    company_detail_address VARCHAR(255),
    company_phone VARCHAR(30),
    website_url VARCHAR(255),
    logo_url VARCHAR(1000),
    company_description CLOB,
    employee_count INT,
    established_date DATE,
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_company_profile_member FOREIGN KEY (member_id) REFERENCES member(id)
);
 
CREATE TABLE IF NOT EXISTS community_post (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    category VARCHAR(30) NOT NULL DEFAULT 'FREE',
    title VARCHAR(200) NOT NULL,
    content CLOB NOT NULL,
    view_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_community_post_member FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE IF NOT EXISTS community_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_community_comment_post FOREIGN KEY (post_id) REFERENCES community_post(id),
    CONSTRAINT fk_community_comment_member FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE IF NOT EXISTS community_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    reporter_member_id BIGINT NOT NULL,
    reason VARCHAR(30) NOT NULL,
    detail VARCHAR(1000),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_community_report_post FOREIGN KEY (post_id) REFERENCES community_post(id),
    CONSTRAINT fk_community_report_member FOREIGN KEY (reporter_member_id) REFERENCES member(id),
    CONSTRAINT uk_community_report UNIQUE (post_id, reporter_member_id)
);
