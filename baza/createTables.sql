CREATE TABLE dogadjaj (
	id int NOT NULL AUTO_INCREMENT,
	tip varchar(10) NOT NULL,
	vrsta varchar(30) NOT NULL,
	kratakOpis varchar(40),
	lokacija varchar(40) NOT NULL,
	datumVreme datetime NOT NULL,
	opis varchar(300) NOT NULL,
	slike varchar(500),
	PRIMARY KEY (id)
);

CREATE TABLE inicijativa (
	id int NOT NULL AUTO_INCREMENT,
	tip varchar(25) NOT NULL,
	vrsta varchar(30) NOT NULL,
	kratakOpis varchar(40) NOT NULL,
	lokacija varchar(40) NOT NULL,
	datumVreme datetime NOT NULL,
	opis varchar(300) NOT NULL,
	slike varchar(500),
	PRIMARY KEY (id)
);

CREATE TABLE problem (
	id int NOT NULL AUTO_INCREMENT,
	tip varchar(25) NOT NULL,
	lokacija varchar(40) NOT NULL,
	opis varchar(300) NOT NULL,
	slike varchar(500),
	PRIMARY KEY (id)
);

CREATE TABLE korisnik (
	id int NOT NULL AUTO_INCREMENT,
	eMail varchar(30) NOT NULL,
	username varchar(20) NOT NULL,
	password varchar(20) NOT NULL,
	PRIMARY KEY (id)
);