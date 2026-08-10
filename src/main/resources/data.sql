MERGE INTO member (id, name, birth_date, gender, email, phone)
KEY (id)
VALUES (
    1,
    '김민준',
    DATE '1998-05-23',
    '남성',
    'minjun.kim@example.com',
    '010-1234-5678'
);
