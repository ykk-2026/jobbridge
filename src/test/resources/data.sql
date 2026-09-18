-- 테스트용 회원 : 비밀번호 1234 를 EncryptUtil.encHashSHA256 으로 암호화한 값
MERGE INTO member (id, login_id, password, name, birth_date, gender, email, phone, role, status)
KEY (id)
VALUES (
    1,
    'minjun.kim',
    '5a8702bdcc88f00df8538b14b492dd7fe2501d40a3b1e4aa94f5470c48ddbb71',
    '테스트 사용자',
    DATE '1998-05-23',
    'OTHER',
    'minjun.kim@example.com',
    '010-1234-5678',
    'JOB_SEEKER',
    'ACTIVE'
);
