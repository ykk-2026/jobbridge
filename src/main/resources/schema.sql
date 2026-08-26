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

-- Keep existing local H2 databases compatible with the current member model.
ALTER TABLE member ADD COLUMN IF NOT EXISTS login_id VARCHAR(100);
ALTER TABLE member ADD COLUMN IF NOT EXISTS password VARCHAR(255);
ALTER TABLE member ADD COLUMN IF NOT EXISTS role VARCHAR(30) DEFAULT 'JOB_SEEKER';
ALTER TABLE member ADD COLUMN IF NOT EXISTS status VARCHAR(30) DEFAULT 'ACTIVE';
CREATE UNIQUE INDEX IF NOT EXISTS ux_member_login_id ON member(login_id);

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
    hybrid_preferred BOOLEAN DEFAULT FALSE,
    onsite_preferred BOOLEAN DEFAULT FALSE,
    contact_time_start TIME,
    contact_time_end TIME,
    contact_method VARCHAR(50),
    introduction VARCHAR(2000),
    profile_public BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_profile_member
        FOREIGN KEY (member_id) REFERENCES member(id)
);

CREATE TABLE IF NOT EXISTS members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    login_id VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS jobs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    company_name VARCHAR(150) NOT NULL,
    location VARCHAR(150),
    employment_type VARCHAR(50),
    salary VARCHAR(100),
    deadline DATE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id BIGINT NOT NULL,
    applicant_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    cover_letter TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_applications_job_email UNIQUE (job_id, email),
    CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs(id)
);
