
INSERT IGNORE INTO MOVIE (id,name, date,description) VALUES (1,'Star Wars', '2019-12-16', 'A long time ago');
INSERT IGNORE INTO MOVIE  (id,name, date,description)VALUES (2,'Star Wars 2', '2021-12-16', 'ABSDFV dsfg');
INSERT IGNORE INTO MOVIE  (id,name, date,description)VALUES (3,'Indiana Jones', '1982-10-12', 'Harrison Ford out in the jungle');
INSERT IGNORE INTO MOVIE (id,name, date,description) VALUES (4,'Whiplash', '2014-10-05', 'A young drummer trying');
INSERT IGNORE INTO MOVIE  (id,name, date,description) VALUES (5,'Harry Potter', '2001-10-10', 'The boy who lived');

INSERT IGNORE INTO ROLE(id,name)
VALUES (1,'ROLE_USER');
INSERT IGNORE INTO ROLE(id,name)
VALUES (2,'ROLE_ADMIN');

INSERT IGNORE INTO USER(id,name, username, email, password,created_at,updated_at)
/*Passwords: admin123, user123*/
VALUES (1,'Phil Key', 'admin', 'admin@mail.com', '$2a$10$j76.2qh8PJvEeehtkC/0cekU8Z7MvzT2WaP95tacIRpK3a6AOjm0.',curdate(),curdate());
INSERT IGNORE INTO USER(id,name, username, email, password,created_at,updated_at)
VALUES (2,'User1', 'user', 'user@mail.com', '$2a$10$CclMO.EdyWx9apJM8OZvFOU7ZgVsNPYzmakhIzmU8wFEiJ.Bp9MWK',curdate(),curdate());

INSERT IGNORE INTO USER_ROLES (user_id, role_id)
VALUES ((SELECT id FROM USER WHERE username = 'admin'), (SELECT id FROM ROLE WHERE name = 'ROLE_ADMIN'));
INSERT IGNORE INTO USER_ROLES (user_id, role_id)
VALUES ((SELECT id FROM USER WHERE username = 'admin'), (SELECT id FROM ROLE WHERE name = 'ROLE_USER'));

INSERT IGNORE INTO USER_ROLES (user_id, role_id)
VALUES ((SELECT id FROM USER WHERE username = 'user'), (SELECT id FROM ROLE WHERE name = 'ROLE_USER'));

/*
INSERT INTO USER (USERNAME, PASSWORD) VALUES ('admin', 'admin');
INSERT INTO USER (USERNAME, PASSWORD) VALUES ('user', 'user');

INSERT INTO MOVIE_SHOW (MOVIE_ID, RELEASE_DATE) VALUES ((SELECT MOVIE_ID FROM MOVIE WHERE NAME = 'Star Wars'),'2020-02-01 20:00:00');
INSERT INTO MOVIE_SHOW (MOVIE_ID, RELEASE_DATE) VALUES ((SELECT MOVIE_ID FROM MOVIE WHERE NAME = 'Whiplash'),'2020-02-02 15:00:00');

INSERT INTO RESERVATION (SHOW_ID, USER_ID) VALUES ((SELECT SHOW_ID FROM MOVIE_SHOW WHERE RELEASE_DATE = '2020-02-01 20:00:00'), (SELECT USER_ID FROM USER WHERE USERNAME = 'admin'));
INSERT INTO RESERVATION (SHOW_ID, USER_ID) VALUES ((SELECT SHOW_ID FROM MOVIE_SHOW WHERE RELEASE_DATE = '2020-02-01 20:00:00'), (SELECT USER_ID FROM USER WHERE USERNAME = 'user'));
INSERT INTO RESERVATION (SHOW_ID, USER_ID) VALUES ((SELECT SHOW_ID FROM MOVIE_SHOW WHERE RELEASE_DATE = '2020-02-02 15:00:00'), (SELECT USER_ID FROM USER WHERE USERNAME = 'user'));
*/



