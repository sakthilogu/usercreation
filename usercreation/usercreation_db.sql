drop database useraccountdb;
drop user useraccount;
create user useraccount with password 'password';
create database useraccountdb with template=template0  owner=useraccount;
\connect useraccountdb;
alter default privileges grant all on tables to useraccount;
alter default privileges grant all on sequences to useraccount;
create table ua_users(
                         user_id integer primary key not null,
                         first_name varchar(20) not null,
                         last_name varchar(20) not null,
                         email varchar(30) not null,
                         password text not null
);
create sequence ua_users_seq increment 1 start 1;