-- 이전에는 자바 코드(Service)에서 실행 시점에 테이블/컬럼을 만들었으나,
-- 강의 구조에 맞춰 SQL은 Mapper XML에만 두고, 테이블 생성/변경은 Flyway 마이그레이션 파일로 관리함

-- 입사 지원 테이블
CREATE TABLE IF NOT EXISTS job_application (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    job_id VARCHAR(50) NOT NULL,
    company_name VARCHAR(100) NOT NULL,
    job_title VARCHAR(200) NOT NULL,
    applicant_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    employment_type VARCHAR(50) DEFAULT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_job_application_member_job (member_id, job_id),
    CONSTRAINT fk_job_application_member FOREIGN KEY (member_id)
        REFERENCES member(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 예전 버전으로 만들어진 job_application 테이블에 없는 컬럼 추가
ALTER TABLE job_application
    ADD COLUMN IF NOT EXISTS company_name VARCHAR(100) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS job_title VARCHAR(200) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS applicant_name VARCHAR(50) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS phone VARCHAR(20) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS email VARCHAR(100) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS employment_type VARCHAR(50) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS status VARCHAR(30) NOT NULL DEFAULT 'APPLIED',
    ADD COLUMN IF NOT EXISTS created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 구직자 프로필 테이블에 회원 기본 정보 컬럼 추가
ALTER TABLE job_seeker_profile
    ADD COLUMN IF NOT EXISTS name VARCHAR(50) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS birth_date DATE DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS gender VARCHAR(20) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS email VARCHAR(100) DEFAULT NULL,
    ADD COLUMN IF NOT EXISTS phone VARCHAR(20) DEFAULT NULL;
