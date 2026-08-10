CREATE TABLE IF NOT EXISTS member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    birth_date DATE,
    gender VARCHAR(20),
    email VARCHAR(255),
    phone VARCHAR(30)
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
