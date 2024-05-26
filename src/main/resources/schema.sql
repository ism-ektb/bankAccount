alter table if exists accounts
    drop constraint if exists FKnjuop33mo69pd79ctplkck40n;
drop table if exists accounts cascade;
drop table if exists users cascade;
drop sequence if exists user_id_seq;
create sequence user_id_seq start with 1 increment by 1;
create table accounts (
                          balance bigint not null,
                          count bigint not null,
                          id bigserial not null,
                          user_id bigint unique,
                          primary key (id),
                          check (balance >= 0 AND count < 16)
);
create table users (
                       birthday date not null,
                       id bigint not null,
                       email varchar(255) unique,
                       firstname varchar(255) not null,
                       lastname varchar(255) not null,
                       password varchar(255) not null,
                       phone varchar(255) unique,
                       role varchar(255) not null check (role in ('ROLE_USER','ROLE_ADMIN')),
                       username varchar(255) not null unique,
                       primary key (id)
);
alter table if exists accounts
    add constraint FKnjuop33mo69pd79ctplkck40n
        foreign key (user_id)
            references users;


