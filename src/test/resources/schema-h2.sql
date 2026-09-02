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
    profile_image_url VARCHAR(1000),
    residence_region VARCHAR(100),
    desired_job VARCHAR(100),
    desired_region VARCHAR(100),
    employment_type VARCHAR(50),
    career_type VARCHAR(50),
    career_years INT,
    min_salary INT,
    remote_preferred BOOLEAN DEFAULT FALSE,
    flexible_preferred BOOLEAN DEFAULT FALSE,
    wheelchair_required BOOLEAN DEFAULT FALSE,
    accessible_restroom_required BOOLEAN DEFAULT FALSE,
    disabled_parking_required BOOLEAN DEFAULT FALSE,
    assistive_device_required BOOLEAN DEFAULT FALSE,
    hybrid_preferred BOOLEAN DEFAULT FALSE,
    onsite_preferred BOOLEAN DEFAULT FALSE,
    contact_time_start TIME,
    contact_time_end TIME,
    contact_method VARCHAR(50),
    introduction VARCHAR(2000),
    profile_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_profile_member FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE IF NOT EXISTS interest_job (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    job_category VARCHAR(100) NOT NULL,
    employment_type VARCHAR(30) NOT NULL,
    location VARCHAR(150) NOT NULL,
    salary_min INT,
    salary_max INT,
    experience_level VARCHAR(50),
    education_level VARCHAR(50),
    description CLOB,
    requirements CLOB,
    preferred_qualifications CLOB,
    accessibility_info CLOB,
    wheelchair_accessible BOOLEAN DEFAULT FALSE,
    accessible_restroom BOOLEAN DEFAULT FALSE,
    disabled_parking BOOLEAN DEFAULT FALSE,
    remote_available BOOLEAN DEFAULT FALSE,
    flexible_work_available BOOLEAN DEFAULT FALSE,
    assistive_device_support BOOLEAN DEFAULT FALSE,
    deadline DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_interest_job_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT uk_interest_job_member_post UNIQUE (member_id, company_name, title)
);

CREATE TABLE IF NOT EXISTS job_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    job_id VARCHAR(50) NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    job_title VARCHAR(200) NOT NULL,
    applicant_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    employment_type VARCHAR(50),
    status VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_job_application_member FOREIGN KEY (member_id) REFERENCES member(id),
    CONSTRAINT uk_job_application_member_job UNIQUE (member_id, job_id)
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
