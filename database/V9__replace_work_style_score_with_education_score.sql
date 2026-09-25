ALTER TABLE ai_job_posting_recommendation
    CHANGE COLUMN work_style_score education_score INT NOT NULL;

UPDATE job_posting
SET education_level = CASE TRIM(education_level)
    WHEN '무관' THEN 'ANY'
    WHEN '학력 무관' THEN 'ANY'
    WHEN '초졸' THEN 'ELEMENTARY_SCHOOL'
    WHEN '초졸 이상' THEN 'ELEMENTARY_SCHOOL'
    WHEN '중졸' THEN 'MIDDLE_SCHOOL'
    WHEN '중졸 이상' THEN 'MIDDLE_SCHOOL'
    WHEN '고졸' THEN 'HIGH_SCHOOL'
    WHEN '고졸 이상' THEN 'HIGH_SCHOOL'
    WHEN '초대졸' THEN 'COLLEGE'
    WHEN '초대졸 이상' THEN 'COLLEGE'
    WHEN '전문대졸' THEN 'COLLEGE'
    WHEN '전문대졸 이상' THEN 'COLLEGE'
    WHEN '대졸' THEN 'UNIVERSITY'
    WHEN '대졸 이상' THEN 'UNIVERSITY'
    WHEN '석사' THEN 'MASTER'
    WHEN '석사 이상' THEN 'MASTER'
    WHEN '박사' THEN 'DOCTOR'
    WHEN '박사 이상' THEN 'DOCTOR'
    ELSE education_level
END
WHERE education_level IS NOT NULL;
