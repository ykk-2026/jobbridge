MERGE INTO member (id, login_id, password, name, birth_date, gender, email, phone, role, status)
KEY (id)
VALUES (
    1,
    'minjun.kim',
    '$2a$10$3LOunRqVBPjIl1lKAvbzued6VFjl4Oho3b1IjOv9f/Y/dDPasuqyO',
    '테스트 사용자',
    DATE '1998-05-23',
    'OTHER',
    'minjun.kim@example.com',
    '010-1234-5678',
    'JOB_SEEKER',
    'ACTIVE'
);
