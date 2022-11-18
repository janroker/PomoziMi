CREATE TABLE Lokacija
(
  ID_Lokacija BIGINT AUTO_INCREMENT NOT NULL,
  duljina NUMERIC(30,26) NOT NULL,
  sirina NUMERIC(30,26) NOT NULL,
  drzava VARCHAR(50) NOT NULL,
  naselje VARCHAR(60) NOT NULL,
  adresa VARCHAR(80) NOT NULL,
  PRIMARY KEY (ID_Lokacija),
  UNIQUE (duljina, sirina)
);

CREATE TABLE Kandidatura
( 
  ID_Kandidatura BIGINT AUTO_INCREMENT NOT NULL,
  godina INT NOT NULL,
  ID_Lokacija BIGINT NOT NULL,
  PRIMARY KEY (ID_Kandidatura),
  FOREIGN KEY (ID_Lokacija) REFERENCES Lokacija(ID_Lokacija),
  UNIQUE (ID_Lokacija, godina)
);

CREATE TABLE Uloga
(
  ID_Uloga BIGINT AUTO_INCREMENT NOT NULL,
  naziv VARCHAR(15) NOT NULL,
  PRIMARY KEY (ID_Uloga),
  UNIQUE (naziv)
);

CREATE TABLE Korisnik
(
  ID_Korisnik BIGINT AUTO_INCREMENT NOT NULL,
  ime VARCHAR(50) NOT NULL,
  prezime VARCHAR(50) NOT NULL,
  lozinka VARCHAR(70) NOT NULL,
  email VARCHAR(250) NOT NULL,
  aktivan BOOLEAN NOT NULL,
  token VARCHAR(1000),
  slika VARCHAR(1000),
  ID_Lokacija BIGINT,
  PRIMARY KEY (ID_Korisnik),
  FOREIGN KEY (ID_Lokacija) REFERENCES Lokacija(ID_Lokacija) ON DELETE CASCADE,
  UNIQUE (email)
);

CREATE TABLE Zahtjev
(
  ID_Zahtjev BIGINT AUTO_INCREMENT NOT NULL,
  opis VARCHAR(2000) NOT NULL,
  tstmp TIMESTAMP,
  status VARCHAR(15) NOT NULL,
  brojMobitela VARCHAR(15),
  ID_Lokacija BIGINT,
  ID_Autor BIGINT NOT NULL,
  ID_Izvrsitelj BIGINT, -- ako nije null, a izvrsen false, onda odabran...
  execTstmp TIMESTAMP,
  potvrden BOOLEAN NOT NULL,
  PRIMARY KEY (ID_Zahtjev),
  FOREIGN KEY (ID_Autor) REFERENCES Korisnik(ID_Korisnik) ON DELETE CASCADE,
  FOREIGN KEY (ID_Izvrsitelj) REFERENCES Korisnik(ID_Korisnik) ON DELETE SET NULL,
  FOREIGN KEY (ID_Lokacija) REFERENCES Lokacija(ID_Lokacija) ON DELETE SET NULL
);

CREATE TABLE Ocjenjivanje
(
  ID_Ocjenjivanje BIGINT AUTO_INCREMENT NOT NULL,
  ocjena INT NOT NULL,
  komentar VARCHAR(250),
  ID_Ocjenjivac BIGINT NOT NULL,
  ID_Ocjenjeni BIGINT NOT NULL,
  ID_Zahtjev BIGINT,
  PRIMARY KEY (ID_Ocjenjivanje),
  FOREIGN KEY (ID_Ocjenjivac) REFERENCES Korisnik(ID_Korisnik) ON DELETE CASCADE,
  FOREIGN KEY (ID_Ocjenjeni) REFERENCES Korisnik(ID_Korisnik) ON DELETE CASCADE,
  FOREIGN KEY (ID_Zahtjev) REFERENCES Zahtjev(ID_Zahtjev) ON DELETE CASCADE,
  CONSTRAINT CHK_OCJENA CHECK (OCJENA BETWEEN 1 AND 5)
);

CREATE TABLE Kandidiranje
(
  ID_Korisnik BIGINT NOT NULL,
  ID_Kandidatura BIGINT NOT NULL,
  PRIMARY KEY (ID_Korisnik, ID_Kandidatura),
  FOREIGN KEY (ID_Korisnik) REFERENCES Korisnik(ID_Korisnik) ON DELETE CASCADE,
  FOREIGN KEY (ID_Kandidatura) REFERENCES Kandidatura(ID_Kandidatura) ON DELETE CASCADE
);

CREATE TABLE ImaUlogu
(
  ID_Korisnik BIGINT NOT NULL,
  ID_Uloga BIGINT NOT NULL,
  PRIMARY KEY (ID_Korisnik, ID_Uloga),
  FOREIGN KEY (ID_Korisnik) REFERENCES Korisnik(ID_Korisnik) ON DELETE CASCADE,
  FOREIGN KEY (ID_Uloga) REFERENCES Uloga(ID_Uloga) ON DELETE CASCADE
);

CREATE TABLE Obavijest
(
  ID_Obavijest BIGINT AUTO_INCREMENT NOT NULL,
  obavijest VARCHAR(2000) NOT NULL,
  primljena BOOLEAN NOT NULL,
  ID_Korisnik BIGINT NOT NULL,
  tstmp TIMESTAMP NOT NULL,
  PRIMARY KEY (ID_Obavijest),
  FOREIGN KEY (ID_Korisnik) REFERENCES Korisnik(ID_Korisnik) ON DELETE CASCADE
);

CREATE OR REPLACE ALIAS CALCULATE_DISTANCE AS '
double calculateDistanceInKM(BigDecimal latb1, BigDecimal lonb1, BigDecimal latb2, BigDecimal lonb2) {
		if(latb1 == null || lonb1 == null || latb2 == null || lonb2 == null)
			return 0;

		double lat1 = latb1.doubleValue(), lon1 = lonb1.doubleValue();
		double lat2 = latb2.doubleValue(), lon2 = lonb2.doubleValue();

		//Haversine formula 
		// distance between latitudes and longitudes
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		// convert to radians 
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		// apply formulae 
		double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
		double rad = 6371; //radius zemlje
		double c = 2 * Math.asin(Math.sqrt(a));
		return rad * c;
	} ';

	/*CREATE OR REPLACE FUNCTION CALCULATE_DISTANCE(lat1 NUMERIC(30,26), lon1 NUMERIC(30,26), lat2 NUMERIC(30,26), lon2 NUMERIC(30,26))
RETURNS float AS $dist$
    declare
    	dist float;
        dLat float;
        dLon float;
       	rlat1 float;
       	rlat2 float;
    BEGIN
	    dist = 0;
	   
        dLat = pi() * (lat2 - lat1) / 180;
        dLon = pi() * (lon2 - lon1) / 180;
        
        rlat1 = pi() * lat1 / 180;
        rlat2 = pi() * lat2 / 180;
        
        dist = power(sin(dLat / 2), 2) + power(sin(dLon / 2), 2) * cos(rlat1) * cos(rlat2);
        dist = 2 * asin(sqrt(dist));
        dist = dist * 6371;
       
        return dist;
    END; $dist$ 
LANGUAGE plpgsql;
*/
