--
-- mysql general
show variables;
use performance_schema;
show tables;
select version() as fromJAVA;
select * from users;
select * from mysql.user;

use palmdb;
select * from palmdemo;

insert into palmdemo values (now(), 81);

show databases;
show tables;

--
-- create table at mysql-style for SF.query_history
--
CREATE TABLE palmdb.sf_query_history (
QUERY_ID        varchar(256) not null primary key,
QUERY_TEXT      text,				-- QUERY_TEXT, or varchar(4096),
DATABASE_NAME   varchar(256),
SCHEMA_NAME     varchar(256),
QUERY_TYPE      varchar(256),		-- 5
SESSION_ID      decimal(38,0),  	-- SEAASION ID must BigDeciaml@java
USER_NAME       varchar(256),
ROLE_NAME		varchar(256),
WAREHOUSE_NAME		varchar(256),
WAREHOUSE_SIZE		varchar(256),	-- 10
WAREHOUSE_TYPE		varchar(256),
CLUSTER_NUMBER		decimal(38,0),
EXECUTION_STATUS	varchar(256),
ERROR_CODE			varchar(256),
ERROR_MESSAGE		varchar(256),	-- 15
START_TIME			timestamp not null,		-- w/index
END_TIME			timestamp,
TOTAL_ELAPSED_TIME	decimal(38,0),
BYTES_SCANNED		decimal(38,0),
PERCENTAGE_SCANNED_FROM_CACHE	DOUBLE,	-- 20, not float
COMPILATION_TIME	decimal(38,0),
EXECUTION_TIME		decimal(38,0),
CREDITS_USED_CLOUD_SERVICES		DOUBLE,
RELEASE_VERSION		varchar(256),
TRANSACTION_ID				decimal(38,0),	-- 25, TXID must BigDecimal@java
CHILD_QUERIES_WAIT_TIME		decimal(38,0),
ROLE_TYPE			varchar(256),
INSERTION_TIME		timestamp
);

show index from palmdb.sf_query_history;
create index start_time_ix
on palmdb.sf_query_history(start_time);
drop index start_time_ix
on palmdb.sf_query_history;


--
-- drop, delete table
drop table palmdb.sf_query_history;
truncate table palmdb.sf_query_history;
delete from palmdb.sf_query_history ;

-- 
-- insert something
insert into palmdb.sf_query_history 
(CREDITS_USED_CLOUD_SERVICES, QUERY_ID, START_TIME, INSERTION_TIME,EXECUTION_TIME )
values (
0.0007,
'2234123005', 
SUBDATE( now(), interval 1 day),
SUBDATE( now(), interval -1 day),
123
);

delete from palmdb.sf_query_history 
where QUERY_ID = '223412389';

--
-- select table
select count(*) from palmdb.sf_query_history;

select *
from palmdb.sf_query_history
order by start_time desc 
;
select QUERY_ID, START_TIME, CREDITS_USED_CLOUD_SERVICES *1000000, INSERTION_TIME  
from palmdb.sf_query_history 
order by start_time desc 
;

select QUERY_ID, START_TIME, EXECUTION_TIME, CREDITS_USED_CLOUD_SERVICES * 1000000 as TX,CREDITS_USED_CLOUD_SERVICES as TXo, USER_NAME, INSERTION_TIME 
from palmdb.sf_query_history 
order by start_time desc 
;

select QUERY_ID, START_TIME, EXECUTION_TIME, COMPILATION_TIME, USER_NAME, INSERTION_TIME
from palmdb.sf_query_history 
order by start_time desc 
limit 10;



--
-- using data, time at mysql
--
select 
adddate(now(), interval 1 day)
;

select date(now());
select 
	timestamp( date(now()) )
	, adddate( timestamp( date(now()) ), interval 1 day) 
;

select QUERY_ID, QUERY_TEXT, START_TIME
from palmdb.sf_query_history
where 	start_time >= timestamp( date(now()) )
	and start_time < adddate( timestamp( date(now()) ), interval 1 day)
	-- and start_time >= timestamp( '2023-07-06 16:30:00')
order by start_time desc
-- limit 1
;

--
-- EOF


