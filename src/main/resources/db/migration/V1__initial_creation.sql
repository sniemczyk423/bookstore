create table roles (
                       id bigserial primary key,
                       name varchar(50) not null unique
);

create table users (
                       id bigserial primary key,
                       email varchar(120) not null unique,
                       password varchar(255) not null,
                       first_name varchar(80) not null,
                       last_name varchar(80) not null,
                       role_id bigint not null,
                       created_at timestamp not null default current_timestamp,

                       constraint fk_users_role
                           foreign key (role_id)
                               references roles(id)
);

create table books (
                       id bigserial primary key,
                       title varchar(200) not null,
                       author varchar(150) not null,
                       isbn varchar(30) not null unique,
                       category varchar(100) not null,
                       description text,
                       available_copies integer not null default 0,
                       created_at timestamp not null default current_timestamp
);

create table reservations (
                              id bigserial primary key,
                              user_id bigint not null,
                              book_id bigint not null,
                              status varchar(30) not null,
                              reserved_at timestamp not null default current_timestamp,
                              cancelled_at timestamp,

                              constraint fk_reservations_user
                                  foreign key (user_id)
                                      references users(id),

                              constraint fk_reservations_book
                                  foreign key (book_id)
                                      references books(id)
);

insert into roles (name) values ('USER');
insert into roles (name) values ('ADMIN');

insert into users (email, password, first_name, last_name, role_id)
values (
           'admin@bookstore.com',
           '$2a$12$N7TuPg7skg0mwODfikOuj.EmayBfeST4/WnwLgot8GvhTOx0T0gAm',
           'admin',
           'user',
           (select id from roles where name = 'ADMIN')
       );