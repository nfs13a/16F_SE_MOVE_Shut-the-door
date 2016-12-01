CREATE DATABASE IF NOT EXISTS COURSES;
USE COURSES;

SET foreign_key_checks = 0;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS instructor;
DROP TABLE IF EXISTS courseInstances;
DROP TABLE IF EXISTS studentCoursesTaken;
DROP TABLE IF EXISTS InstructorCoursesTaught;
SET foreign_key_checks = 1;
CREATE TABLE student
( banner varchar(20) NOT NULL,
PRIMARY KEY ( banner )
);

CREATE TABLE instructor 
( name varchar(100) NOT NULL,
PRIMARY KEY (name)
);

CREATE TABLE courseInstances
( CRN varchar(50) NOT NULL,
code varchar(20) NOT NULL,
days varchar(10) NOT NULL,
startTime varchar(50) NOT NULL,
endTime varchar(50) NOT NULL,
semester varchar(50) NOT NULL,
building varchar(50) NOT NULL,
room varchar(50) NOT NULL,
maxStudents int NOT NULL,
PRIMARY KEY (CRN, code)
);

CREATE TABLE studentCoursesTaken
( banner varchar(20) NOT NULL,
CRN varchar(50) NOT NULL,
code varchar(20) NOT NULL,
classification varchar(10) NOT NULL,
PRIMARY KEY (banner,CRN,code),
FOREIGN KEY (banner) references student(banner),
FOREIGN KEY (CRN, code) references courseInstances(CRN, code)
);

CREATE TABLE InstructorCoursesTaught 
( name varchar(100) NOT NULL,
CRN varchar(50) NOT NULL,
code varchar(20) NOT NULL,
PRIMARY KEY (name,CRN,code),
FOREIGN KEY (name) references instructor(name),
FOREIGN KEY (CRN, code) references courseInstances(CRN, code)
);

CREATE TABLE KnownRoom
( building varchar(50) NOT NULL,
room varchar(50) NOT NULL,
seats int NOT NULL,
PRIMARY KEY (building, room)
);