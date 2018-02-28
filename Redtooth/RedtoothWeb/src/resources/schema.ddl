create database redtooth;

use redtooth;

create table devices(deviceID varchar(20) NOT NULL, updateTimestamp long, priority int NOT NULL, playing BOOL NOT NULL, PRIMARY KEY(deviceID));
