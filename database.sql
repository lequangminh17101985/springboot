CREATE DATABASE LogParser ;

CREATE TABLE LogParser.accesslog (
	ip varchar(100) NULL,
	datetime varchar(100) NULL,
	request varchar(100) NULL,
	status int NULL,
	user_agent varchar(500) NULL
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci ;

CREATE TABLE LogParser.blockip (
	ip varchar(100) NULL,
	comment varchar(100) NULL
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci ;

