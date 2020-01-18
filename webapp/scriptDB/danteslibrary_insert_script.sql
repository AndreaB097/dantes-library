USE danteslibrary;

INSERT INTO booking_states(state_name) VALUES("Non ancora ritirato");
INSERT INTO booking_states(state_name) VALUES("Ritirato");
INSERT INTO booking_states(state_name) VALUES("Riconsegnato");
INSERT INTO booking_states(state_name) VALUES("Annullata");

INSERT INTO books(book_id, title, description, publisher, quantity, cover) VALUES (1, "I Promessi Sposi", "Romanzo di un amore contrastato nell'Italia del Seicento, \"I Promessi Sposi\" sono anche il sillabario della nostra 
modernità: mettono alla prova i valori del cattolicesimo, l'etica borghese, gli ideali risorgimentali, e per le tecniche narrative che adoperano e la lingua viva che inventano segnano un nuovo inizio per la letteratura 
italiana. Questa edizione, diretta da Francesco de Cristofaro e realizzata da un'equipe multidisciplinare di studiosi, offre una aggiornata panoramica sul capolavoro manzoniano, spaziando dagli aspetti lessicali e 
interpretativi a quelli linguistici e stilistici. Il volume include la \"Storia della Colonna infame\" e propone, per la prima volta, una sistematica analisi delle illustrazioni di Gonin & Co. che scandiscono, secondo 
precisa regia manzoniana, l'edizione definitiva del romanzo.","BUR Biblioteca", 10, "./images/covers/promessi.png");
INSERT INTO books(book_id, title, description, publisher, quantity, cover) VALUES (2, "Necronomicon","Redatto nell’VIII secolo a Damasco da un pazzo yemenita, il Necronomicon contiene 
la storia dei Grandi Antichi e le parole per invocarli. È un libro maledetto, demoniaco. Soprattutto è un libro inventato dalla fervida fantasia di H.P. Lovecraft.", "Oscar Mondadori", 10, "./images/covers/necronomicon.png");
INSERT INTO books(book_id, title, description, publisher, quantity, cover) VALUES (3, "Sistemi operativi. Concetti ed esempi","Il libro fornisce una solida base teorica per la comprensione 
dei sistemi operativi – da quelli mobili ai multicore –, spiegando i concetti fondamentali e discutendone le implementazioni attuali senza concentrarsi su un particolare sistema operativo 
o hardware.", "Pearson", 10, "./images/covers/sistemi.png");
INSERT INTO books(book_id, title, description, publisher, quantity, cover) VALUES (4, "1984 (Nineteen Eighty-Four)", "1984. Il mondo è diviso in tre superstati in guerra fra loro: Oceania, Eurasia 
ed Estasia. L’Oceania, la cui capitale è Londra, è governata dal Grande Fratello, che tutto vede e tutto sa. I suoi occhi sono le telecamere che spiano di continuo nelle case, il suo braccio 
la psicopolizia che interviene al minimo sospetto. Tutto è permesso, non c’è legge scritta. Niente, apparentemente, è proibito. Tranne pensare. Tranne amare. Tranne divertirsi. Insomma: tranne 
vivere, se non secondo i dettami del Grande Fratello. Dal loro rifugio, in uno scenario desolante, solo Winston Smith e Julia lottano disperatamente per conservare un granello di umanità…", 
"Oscar Mondadori", 10, "./images/covers/1984.png");

INSERT INTO genres(genre_name) VALUES ("Azione");
INSERT INTO genres(genre_name) VALUES ("Autobiografia");
INSERT INTO genres(genre_name) VALUES ("Biografia");
INSERT INTO genres(genre_name) VALUES ("Avventura");
INSERT INTO genres(genre_name) VALUES ("Fantasy");
INSERT INTO genres(genre_name) VALUES ("Fantascienza");
INSERT INTO genres(genre_name) VALUES ("Romanzo Storico");
INSERT INTO genres(genre_name) VALUES ("Giallo");
INSERT INTO genres(genre_name) VALUES ("Distopia");
INSERT INTO genres(genre_name) VALUES ("Horror e Gotico");
INSERT INTO genres(genre_name) VALUES ("Romanzo Rosa");
INSERT INTO genres(genre_name) VALUES ("Erotico");
INSERT INTO genres(genre_name) VALUES ("Umoristico");
INSERT INTO genres(genre_name) VALUES ("Religione");
INSERT INTO genres(genre_name) VALUES ("Economia e Finanza");
INSERT INTO genres(genre_name) VALUES ("Psicologia");
INSERT INTO genres(genre_name) VALUES ("Sociologia");
INSERT INTO genres(genre_name) VALUES ("Poesia");
INSERT INTO genres(genre_name) VALUES ("Sport");
INSERT INTO genres(genre_name) VALUES ("Cucina");
INSERT INTO genres(genre_name) VALUES ("Didattica");

INSERT INTO authors(author_id, name) VALUES (1, "Alessandro Manzoni");
INSERT INTO authors(author_id, name) VALUES (2, "Howard Phillips Lovecraft");
INSERT INTO authors(author_id, name) VALUES (3, "Abraham Silberschatz");
INSERT INTO authors(author_id, name) VALUES (4, "Peter Baer Galvin");
INSERT INTO authors(author_id, name) VALUES (5, "Greg Gagne");
INSERT INTO authors(author_id, name) VALUES (6, "George Orwell");

INSERT INTO books_authors(book_id, author_id) VALUES (1, 1);
INSERT INTO books_authors(book_id, author_id) VALUES (2, 2);
INSERT INTO books_authors(book_id, author_id) VALUES (3, 3);
INSERT INTO books_authors(book_id, author_id) VALUES (3, 4);
INSERT INTO books_authors(book_id, author_id) VALUES (3, 5);
INSERT INTO books_authors(book_id, author_id) VALUES (4, 6);

INSERT INTO books_genres(book_id, genre_name) VALUES (1, "Romanzo Storico");
INSERT INTO books_genres(book_id, genre_name) VALUES (2, "Horror e Gotico");
INSERT INTO books_genres(book_id, genre_name) VALUES (3, "Didattica");
INSERT INTO books_genres(book_id, genre_name) VALUES (4, "Distopia");

INSERT INTO roles(role_name) VALUES("Gestore Biblioteca");
INSERT INTO roles(role_name) VALUES("Gestore Clienti");
INSERT INTO roles(role_name) VALUES("Gestore Libri");
INSERT INTO roles(role_name) VALUES("Gestore Prenotazioni");
INSERT INTO roles(role_name) VALUES("Gestore Tessere");

INSERT INTO managers(email, password, name, surname, address, phone) VALUES("admin@admin.it", "$2a$10$vI9Ii5PISsqeDkI5Z8Aq9upp2aZiOGDGmlrAKFfWZbWIlH6eDjCe2", "Michele", "Almanacco", "Viale Meraviglia 10", "123456789");
INSERT INTO managers_roles(email, role_name) VALUES("admin@admin.it", "Gestore Biblioteca");

INSERT INTO cards(codice_fiscale) values("1111111111111111");
INSERT INTO cards(codice_fiscale) values("2222222222222222");
INSERT INTO cards(codice_fiscale) values("3333333333333333");
INSERT INTO cards(codice_fiscale) values("4444444444444444");

INSERT INTO customers(email, password, name, surname, codice_fiscale, address) VALUES("a@a.it", "$2a$10$vI9Ii5PISsqeDkI5Z8Aq9upp2aZiOGDGmlrAKFfWZbWIlH6eDjCe2", "Lino", "Gargiulo", "1111111111111111", "Via dei Principati 20");
INSERT INTO customers(email, password, name, surname, codice_fiscale, address) VALUES("b@b.it", "$2a$10$vI9Ii5PISsqeDkI5Z8Aq9upp2aZiOGDGmlrAKFfWZbWIlH6eDjCe2", "Gennaro", "Mellia", "2222222222222222", "Via Poligono 30");
INSERT INTO customers(email, password, name, surname, codice_fiscale, address) VALUES("c@c.it", "$2a$10$vI9Ii5PISsqeDkI5Z8Aq9upp2aZiOGDGmlrAKFfWZbWIlH6eDjCe2", "Enza", "Amico", "3333333333333333", "Piazza Albertone 15");