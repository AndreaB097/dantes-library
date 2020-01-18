create database if not exists danteslibrary;
use danteslibrary;

create table if not exists roles(
	role_name varchar(50) primary key
);

create table if not exists managers(
    email varchar(100) primary key,
    password varchar(191) not null, /* 191 poiche' verra' cifrata */
    name varchar(30) not null,
    surname varchar(30) not null,
    address varchar(100) not null,
    phone varchar(10) not null,
    tmp_link varchar(100) unique
);

create table if not exists managers_roles(
    email varchar(100),
    role_name varchar(50) not null,
    primary key(email, role_name),
    foreign key(email) references managers(email)
    on update cascade on delete cascade,
    foreign key(role_name) references roles(role_name)
    on update cascade on delete cascade
);

create table if not exists customers(
    name varchar(30) not null,
    surname varchar(30) not null,
    email varchar(100) primary key,
    password varchar(191) not null,
    codice_fiscale char(16) unique not null,
    address varchar(100) not null,
    tmp_link varchar(100) unique
);

create table if not exists cards(
	card_id int auto_increment primary key,
    codice_fiscale char(16) not null unique,
    associated boolean not null default false
);
alter table cards AUTO_INCREMENT = 10000; /*Auto increment parte da 10000*/

create table if not exists books(
	book_id int auto_increment primary key,
    title varchar(100) not null,
    description text,
	publisher varchar(100) not null,
    quantity int unsigned not null,
    cover varchar(191) default "./images/no_image.png"
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
	genre_name varchar(100) primary key
);

create table if not exists books_genres(
	book_id int, 
    genre_name varchar(100) not null,
    primary key(book_id, genre_name),
    foreign key(book_id) references books(book_id)
    on update cascade on delete cascade,
    foreign key(genre_name) references genres(genre_name)
    on update cascade on delete cascade
);

create table if not exists booking_states(
    state_name varchar(100) primary key
);

create table if not exists bookings(
	booking_id int auto_increment primary key,
    email varchar(100), /*facoltativo per gli utenti non registrati*/
    start_date date not null,
    end_date date not null,
    state_name varchar(100),
    card_id int not null,
    book_id int not null,
    title varchar(100) not null,
    foreign key(state_name) references booking_states(state_name)
    on update cascade on delete set null
);

create table if not exists library(
	name varchar(100) primary key default "Dante's Library",
    logo varchar(191) not null default "./images/default_logo.png",
    contacts text
);