INSERT INTO Users (id, username, password)
VALUES ('240b253a-5616-4fa1-a60d-810d2ad5ceb2', 'admin', '$2a$12$yfz0Lt7yIy4ODvJo3Jf7de8JdGw6vzlq5O3Kwe8oU.bIdgPj3kCBy');

INSERT INTO users_roles (user_id, roles)
values ('240b253a-5616-4fa1-a60d-810d2ad5ceb2', 'ROLE_USER');

INSERT INTO users_roles (user_id, roles)
values ('240b253a-5616-4fa1-a60d-810d2ad5ceb2', 'ROLE_ADMIN');