CREATE DATABASE IF NOT EXISTS STUDENTS;
USE STUDENTS;

SET foreign_key_checks = 0;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS course;
DROP TABLE IF EXISTS prereqCourse;
DROP TABLE IF EXISTS courseInstances;
DROP TABLE IF EXISTS studentCoursesTaken;
SET foreign_key_checks = 1;

CREATE TABLE student
( banner varchar(20) NOT NULL,
fn varchar(50) NOT NULL,
ln varchar(50) NOT NULL,
mn varchar(50) NOT NULL,
pre varchar(10) NOT NULL,
classification varchar(10) NOT NULL,
PRIMARY KEY ( banner )
);

CREATE TABLE course
( code varchar(20) NOT NULL,
hours int NOT NULL,
prereqGPA float(5) NOT NULL,
prereqClass varchar(50) NOT NULL,
prereqEarnedHours int(5) NOT NULL,
PRIMARY KEY (code)
);

CREATE TABLE prereqCourse
( codePre varchar(20) NOT NULL,
codePost varchar(20) NOT NULL,
grade varchar(10) NOT NULL,
PRIMARY KEY (codePre,codePost),
FOREIGN KEY (codePre) references course(code),
FOREIGN KEY (codePost) references course(code)
);

CREATE TABLE courseInstances
( CRN varchar(50) NOT NULL,
code varchar(20) NOT NULL,
instructor varchar(50) NOT NULL,
PRIMARY KEY (CRN, code),
FOREIGN KEY (code) references course(code)
);

CREATE TABLE studentCoursesTaken
( banner varchar(20) NOT NULL,
CRN varchar(50) NOT NULL,
code varchar(20) NOT NULL,
earnedHrs int NOT NULL,
grade varchar(10) NOT NULL,
PRIMARY KEY (CRN,code,banner),
FOREIGN KEY (code) references course(code),
FOREIGN KEY (CRN) references courseInstances(CRN),
FOREIGN KEY (banner) references student(banner)
);
