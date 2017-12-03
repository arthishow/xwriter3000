DROP TABLE IF EXISTS userbook;
DROP TABLE IF EXISTS author;
DROP TABLE IF EXISTS book;

CREATE TABLE author(
    authorName VARCHAR(128) NOT NULL,
    authorPass VARCHAR(128) NOT NULL,
    PRIMARY KEY(authorName)
);

CREATE TABLE book(
    bookId INT NOT NULL AUTO_INCREMENT,
    title VARCHAR(1000) NOT NULL,
    content MEDIUMTEXT,
    PRIMARY KEY(bookId)
);

CREATE TABLE userbook(
    bookId INT NOT NULL,
    authorName VARCHAR(128) NOT NULL,
    authorization INT NOT NULL,
    PRIMARY KEY(bookId, authorName),
    FOREIGN KEY (bookId) REFERENCES book(bookId) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (authorName) REFERENCES author(authorName) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT CHK_level CHECK(authorization >= 0 AND authorization <=2)
);
