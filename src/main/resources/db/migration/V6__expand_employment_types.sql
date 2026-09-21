-- 기업 채용공고와 구직자 프로필에서 사용하는 고용형태 코드를 동일하게 맞춘다.
-- INTERNSHIP은 기존 데이터 호환을 위해 남겨 두고, 신규 저장은 INTERN을 사용한다.
ALTER TABLE job_seeker_profile
    MODIFY COLUMN employment_type ENUM(
        'ANY',
        'FULL_TIME',
        'CONTRACT',
        'PERMANENT_CONTRACT',
        'FULL_TIME_CONVERSION',
        'PART_TIME',
        'INTERN',
        'INTERNSHIP',
        'DISPATCH',
        'FREELANCER'
    ) NOT NULL DEFAULT 'ANY';

ALTER TABLE job_posting
    MODIFY COLUMN employment_type ENUM(
        'ANY',
        'FULL_TIME',
        'CONTRACT',
        'PERMANENT_CONTRACT',
        'FULL_TIME_CONVERSION',
        'PART_TIME',
        'INTERN',
        'INTERNSHIP',
        'DISPATCH',
        'FREELANCER'
    ) NOT NULL;
