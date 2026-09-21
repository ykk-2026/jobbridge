-- 단일 work_type 값과 예전 근무방식 boolean 컬럼이 서로 다르지 않도록 기존 데이터를 정리한다.
UPDATE job_seeker_profile
SET remote_preferred = (work_type = 'REMOTE'),
    flexible_preferred = FALSE,
    hybrid_preferred = (work_type = 'HYBRID'),
    onsite_preferred = (work_type = 'OFFICE');
