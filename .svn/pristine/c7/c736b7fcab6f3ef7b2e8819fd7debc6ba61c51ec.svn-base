CREATE TABLE korisnik (
	id int NOT NULL AUTO_INCREMENT,
	eMail varchar(30) NOT NULL,
	username varchar(20) NOT NULL,
	password varchar(20) NOT NULL,
	PRIMARY KEY (id)
)

CREATE TABLE dogadjaj (
	id int NOT NULL AUTO_INCREMENT,
	tipDogadjaja varchar(10) NOT NULL,
	vrstaIzvodjac varchar(30),
	kratakOpis varchar(40),
	lokacija varchar(40),
	datumVreme datetime,
	opis varchar(300),
	slike varchar(500),
	PRIMARY KEY (id)
)

CREATE TABLE inicijativa (
	id int NOT NULL AUTO_INCREMENT,
	tipInicijative varchar(25) NOT NULL,
	vrstaRazlog varchar(30),
	brojPrijavljenih int,
	lokacija varchar(40),
	datumVreme datetime,
	opis varchar(300),
	slike varchar(500),
	PRIMARY KEY (id)
)

CREATE TABLE problem (
	id int NOT NULL AUTO_INCREMENT,
	tipProblema varchar(25) NOT NULL,
	lokacija varchar(40),
	opis varchar(300),
	slike varchar(500),
	PRIMARY KEY (id)
)