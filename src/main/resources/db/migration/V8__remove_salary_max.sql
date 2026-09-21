-- 채용공고 급여는 단일 연봉(salary_min)만 사용한다.
ALTER TABLE job_posting
    DROP COLUMN IF EXISTS salary_max;
