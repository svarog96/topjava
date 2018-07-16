DELETE FROM user_roles;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password) VALUES
  ('User', 'user@yandex.ru', 'password'),
  ('Admin', 'admin@gmail.com', 'admin');

INSERT INTO user_roles (role, user_id) VALUES
  ('ROLE_USER', 100000),
  ('ROLE_ADMIN', 100001);

INSERT INTO meals (user_id, description, calories, date_time) values
    (100001, 'Завтрак', '600', '2018-07-16 09:00:00'),
    (100001, 'Обед', '1500', '2018-07-16 14:00:00'),
    (100001, 'Ужин', '700', '2018-07-16 18:00:00'),
    (100001, 'Завтрак', '500', '2018-07-17 09:00:00'),
    (100001, 'Обед', '900', '2018-07-16 14:00:00'),
    (100001, 'Ужин', '600', '2018-07-16 18:00:00')
