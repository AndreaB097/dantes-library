create database if not exists danteslibrary;
use danteslibrary;

create table if not exists roles(
	name varchar(50) primary key
);

create table if not exists managers(
    email varchar(100) primary key,
    password varchar(191) not null, /* 191 poiche' verra' cifrata */
    name varchar(30) not null,
    surname varchar(30) not null,
    address varchar(100) not null,
    phone varchar(10) not null,
    role varchar(50) not null,
    foreign key(role) references roles(name)
);

create table if not exists users(
    name varchar(30) not null,
    surname varchar(30) not null,
    email varchar(100) primary key,
    password varchar(191) not null,
    codice_fiscale char(16) not null,
    address varchar(100) not null
);

create table if not exists card(
	card_code int auto_increment primary key,
    codice_fiscale char(16) not null unique,
    associated boolean not null default false
);

create table if not exists books(
	book_code int auto_increment primary key,
    title varchar(100) not null,
	publisher varchar(100) not null,
    quantity int not null,
    cover varchar(191) default "./images/cover/default.png"
);

create table if not exists authors(
    name varchar(100) primary key
);

create table if not exists books_authors(
	book_code int, 
    name varchar(100),
    primary key(book_code, name),
    foreign key(book_code) references books(book_code)
    on update cascade on delete cascade,
    foreign key(name) references authors(name)
    on update cascade on delete cascade
);

create table if not exists genres(
	name varchar(100) primary key
);

create table if not exists books_genres(
	book_code int, 
    name varchar(100),
    primary key(book_code, name),
    foreign key(book_code) references books(book_code)
    on update cascade on delete cascade,
    foreign key(name) references genres(name)
    on update cascade on delete cascade
);

create table if not exists booking_states(
	state_id int auto_increment primary key,
    name varchar(100) unique not null 
);

create table if not exists bookings(
	booking_code int auto_increment primary key,
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