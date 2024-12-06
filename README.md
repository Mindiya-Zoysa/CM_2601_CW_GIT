1. Creating the database.
   CREATE DATABASE newscollector;

2. Creating the Articles Table.
   CREATE TABLE article
   (
   articleId							INTEGER AUTO_INCREMENT,
   categoryName							VARCHAR(255)		NOT NULL,  
   title								VARCHAR(255)		NOT NULL,
   author								VARCHAR(255)		NOT NULL,
   content							TEXT				NOT NULL,
   PublishedDate						DATE				NOT NULL,
   sourceName								VARCHAR(255)		NOT NULL,
   CONSTRAINT	at_aid_pk	PRIMARY KEY	(articleId)
   );

3. Creating the User Details Table.
   CREATE TABLE user_detail
   (
   userId							INTEGER AUTO_INCREMENT,
   userName								VARCHAR(255)		NOT NULL,
   userPassword							VARCHAR(255)		NOT NULL,
   registrationDate						DATE				NOT NULL,
   CONSTRAINT	ur_uid_pk	PRIMARY KEY	(userId)
   );


4. Creating the Article Ratings Table.
   CREATE TABLE article_rating
   (
   ratingId							INTEGER AUTO_INCREMENT,
   userId							INTEGER,
   articleId							INTEGER,
   rating							INTEGER(10),
   timeincident								DATETIME		NOT NULL,
   CONSTRAINT	ar_rid_pk	PRIMARY KEY	(ratingId),
   CONSTRAINT ar_uid_fk	FOREIGN KEY (userId)	REFERENCES user_detail(userId),
   CONSTRAINT ar_aid_fk	FOREIGN KEY (articleId)	REFERENCES article(articleId)
   );

5. Creating the User Preferences Table.
   CREATE TABLE user_preference
   (
   preferenceId							INTEGER AUTO_INCREMENT,
   userId							INTEGER,
   categoryName							VARCHAR(70)		NOT NULL,
   CONSTRAINT	up_pid_pk	PRIMARY KEY	(preferenceId),
   CONSTRAINT up_uid_fk	FOREIGN KEY (userId)	REFERENCES user_detail(userId)
   );

6. Inserting values to the User Details Table.
   INSERT INTO user_detail (userId, userName, userPassword, registrationDate)
   VALUES
   (1, 'Mindiya', 'Hello@01', '2024-11-25'),
   (2, 'Shevon', 'Hello@02', '2024-11-23'),
   (3, 'Nadith', 'Hello@03', '2024-11-22'),
   (4, 'Arosha', 'Hello@04', '2024-11-20'),
   (5, 'Mahesh', 'Hello@05', '2024-11-18'),
   (6, 'Angel', 'Hello@06', '2024-11-26');

7. Inserting values to the User Preferences Table.
   INSERT INTO user_preference (preferenceId, userId, categoryName)
   VALUES
   (11, 1, 'Sports'),
   (12, 1, 'Technology'),
   (13, 1, 'Entertainment'),
   (14, 2, 'Sports'),
   (15, 2, 'Business'),
   (16, 3, 'Technology'),
   (17, 3, 'Business'),
   (18, 4, 'Health'),
   (19, 4, 'Entertainment'),
   (20, 5, 'Politics'),
   (21, 5, 'Health'),
   (22, 5, 'Technology'),
   (23, 6, 'Health'),
   (24, 6, 'Entertainment'),
   (25, 6, 'Business'),
   (26, 6, 'Politics'),
   (27, 7, 'Politics');

So these are the commands, to create the database as well as the tables. 
Should do these in the sql with my username and password then i can work on this project. 
I will be using MYSQL.
