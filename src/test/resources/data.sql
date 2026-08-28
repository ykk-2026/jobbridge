MERGE INTO member (id, login_id, password, name, birth_date, gender, email, phone, role, status)
KEY (id)
VALUES (
    1,
    'minjun.kim',
    '$2a$10$hYnASJFCavu3k8/zvud10Os1vWhNFlUq6XWRwRzLPw4ItUO5XbrIu',
    '테스트 사용자',
    DATE '1998-05-23',
    'OTHER',
    'minjun.kim@example.com',
    '010-1234-5678',
    'JOB_SEEKER',
    'ACTIVE'
);
