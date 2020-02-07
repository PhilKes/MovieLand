/*
INSERT INTO MOVIE (NAME, RELEASE_DATE,DESCRIPTION) VALUES ('Star Wars', '2019-12-16', 'A long time ago');
INSERT INTO MOVIE (NAME, RELEASE_DATE,DESCRIPTION) VALUES ('Star Wars 2', '2021-12-16', 'ABSDFV dsfg');
INSERT INTO MOVIE (NAME, RELEASE_DATE,DESCRIPTION) VALUES ('Indiana Jones', '1982-10-12', 'Harrison Ford out in the jungle');
INSERT INTO MOVIE (NAME, RELEASE_DATE,DESCRIPTION) VALUES ('Whiplash', '2014-10-05', 'A young drummer trying');
INSERT INTO MOVIE (NAME, RELEASE_DATE,DESCRIPTION) VALUES ('Harry Potter', '2001-10-10', 'The boy who lived');

INSERT INTO USER (USERNAME, PASSWORD) VALUES ('admin', 'admin');
INSERT INTO USER (USERNAME, PASSWORD) VALUES ('user', 'user');

INSERT INTO MOVIE_SHOW (MOVIE_ID, RELEASE_DATE) VALUES ((SELECT MOVIE_ID FROM MOVIE WHERE NAME = 'Star Wars'),'2020-02-01 20:00:00');
INSERT INTO MOVIE_SHOW (MOVIE_ID, RELEASE_DATE) VALUES ((SELECT MOVIE_ID FROM MOVIE WHERE NAME = 'Whiplash'),'2020-02-02 15:00:00');

INSERT INTO RESERVATION (SHOW_ID, USER_ID) VALUES ((SELECT SHOW_ID FROM MOVIE_SHOW WHERE RELEASE_DATE = '2020-02-01 20:00:00'), (SELECT USER_ID FROM USER WHERE USERNAME = 'admin'));
INSERT INTO RESERVATION (SHOW_ID, USER_ID) VALUES ((SELECT SHOW_ID FROM MOVIE_SHOW WHERE RELEASE_DATE = '2020-02-01 20:00:00'), (SELECT USER_ID FROM USER WHERE USERNAME = 'user'));
INSERT INTO RESERVATION (SHOW_ID, USER_ID) VALUES ((SELECT SHOW_ID FROM MOVIE_SHOW WHERE RELEASE_DATE = '2020-02-02 15:00:00'), (SELECT USER_ID FROM USER WHERE USERNAME = 'user'));


*/

INSERT INTO USER(name, username, email, password)
VALUES ('Phil Key', 'admin', 'admin@mail.com', 'admin');
INSERT INTO USER(name, username, email, password)
VALUES ('User1', 'user', 'user@mail.com', 'user');

INSERT INTO ROLE(name)
VALUES ('USER');
INSERT INTO ROLE(name)
VALUES ('ADMIN');

INSERT INTO USER_ROLES (user_id, roles_id)
VALUES ((SELECT id FROM USER WHERE name = 'admin'), (SELECT id FROM ROLES WHERE name = 'ADMIN'));
INSERT INTO USER_ROLES (user_id, roles_id)
VALUES ((SELECT id FROM USER WHERE name = 'admin'), (SELECT id FROM ROLES WHERE name = 'USER'));

INSERT INTO USER_ROLES (user_id, roles_id)
VALUES ((SELECT id FROM USER WHERE name = 'user'), (SELECT id FROM ROLES WHERE name = 'USER'));



