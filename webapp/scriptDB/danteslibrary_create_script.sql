create database if not exists danteslibrary;
use danteslibrary;

create table if not exists roles(
	role_id int auto_increment primary key,
	name varchar(50) not null
);

create table if not exists managers(
    email varchar(100) primary key,
    password varchar(191) not null, /* 191 poiche' verra' cifrata */
    name varchar(30) not null,
    surname varchar(30) not null,
    address varchar(100) not null,
    phone varchar(10) not null
);

create table if not exists managers_roles(
	email varchar(100),
    role_id int,
    primary key(email, role_id),
	foreign key(email) references managers(email)
    on update cascade on delete cascade,
    foreign key(role_id) references roles(role_id)
    on update cascade on delete cascade
);

create table if not exists users(
    name varchar(30) not null,
    surname varchar(30) not null,
    email varchar(100) primary key,
    password varchar(191) not null,
    codice_fiscale char(16) unique not null,
    address varchar(100) not null
);

create table if not exists card(
	card_id int auto_increment primary key,
    codice_fiscale char(16) not null unique,
    associated boolean not null default false
);

create table if not exists books(
	book_id int auto_increment primary key,
    title varchar(100) not null,
    description text,
	publisher varchar(100) not null,
    quantity int not null,
    cover varchar(191) default "./images/cover/default.png"
);

create table if not exists authors(
	author_id int auto_increment primary key,
    name varchar(100) not null
);

create table if not exists books_authors(
	book_id int, 
    author_id int,
    primary key(book_id, author_id),
    foreign key(book_id) references books(book_id)
    on update cascade on delete cascade,
    foreign key(author_id) references authors(author_id)
    on update cascade on delete cascade
);

create table if not exists genres(
	genre_id int auto_increment primary key,
	name varchar(100) not null
);

create table if not exists books_genres(
	book_id int, 
    genre_id int,
    primary key(book_id, genre_id),
    foreign key(book_id) references books(book_id)
    on update cascade on delete cascade,
    foreign key(genre_id) references genres(genre_id)
    on update cascade on delete cascade
);

create table if not exists booking_states(
	state_id int auto_increment primary key,
    name varchar(100) unique not null 
);

create table if not exists bookings(
	booking_id int auto_increment primary key,
    start_date date not null,
    end_date date not null,
    state_id int not null,
    foreign key(state_id) references booking_states(state_id)
);

create table if not exists library(
	name varchar(100) primary key,
    logo varchar(191) default "./images/logo.png",
    contacts text not null 
);