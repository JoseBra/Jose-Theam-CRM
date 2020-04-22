alter table Users
drop COLUMN IF EXISTS roles CASCADE;

create TABLE users_roles (
    user_id varchar(255) REFERENCES Users(id),
    roles varchar(255)
);