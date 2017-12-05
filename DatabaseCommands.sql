drop table if exists userbook;
drop table if exists tempSymKeys;
drop table if exists authorSymKeys;
drop table if exists authorKeys;
drop table if exists salt;
drop table if exists book;
drop table if exists author;

CREATE TABLE author(
    authorName VARCHAR(100) NOT NULL,
    authorPass VARCHAR(100) NOT NULL,
    PRIMARY KEY(authorName)
);

CREATE TABLE salt(
	authorName VARCHAR(100) NOT NULL,
	salt VARCHAR(100) NOT NULL,
	PRIMARY KEY(authorName),
	FOREIGN KEY (authorName) REFERENCES author(authorName) ON DELETE CASCADE    
);

CREATE TABLE authorKeys(
	authorName VARCHAR(100) NOT NULL,
	keyType VARCHAR(15) NOT NULL,
	secretKey VARCHAR(4000) NOT NULL,
	PRIMARY KEY(authorName, keyType),
	FOREIGN KEY (authorName) REFERENCES author(authorName) ON DELETE CASCADE   
);

CREATE TABLE book(
    bookId INT NOT NULL AUTO_INCREMENT,
    title VARCHAR(1000) NOT NULL,
    content MEDIUMTEXT,
    PRIMARY KEY(bookId)
);

create table authorSymKeys(
	authorName varchar(100) not null,
	bookId INT NOT NULL,
	secretKey varchar(5000) not null,
	PRIMARY KEY(authorName, bookId),	
    FOREIGN KEY (authorName) REFERENCES author(authorName) ON DELETE CASCADE ON UPDATE CASCADE,   
	FOREIGN KEY (bookId) REFERENCES book(bookId) ON DELETE CASCADE ON UPDATE CASCADE
);

create table tempSymKeys(
	authorName varchar(100) not null,
	bookId INT NOT NULL,
	secretKey varchar(5000) not null,
	PRIMARY KEY(authorName, bookId),	
    FOREIGN KEY (authorName) REFERENCES author(authorName) ON DELETE CASCADE ON UPDATE CASCADE,   
	FOREIGN KEY (bookId) REFERENCES book(bookId) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE userbook(
    bookId INT NOT NULL,
    authorName VARCHAR(100) NOT NULL,
	authorization INT NOT NULL,
    PRIMARY KEY(bookId, authorName),
    FOREIGN KEY (bookId) REFERENCES book(bookId) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (authorName) REFERENCES author(authorName) ON DELETE CASCADE ON UPDATE CASCADE    
);


