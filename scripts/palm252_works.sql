--
-- Oasis Palm, works
-- July 2023
--
-- mysql general
show variables;
use performance_schema;
show tables;
select version() as fromJAVA;
select * from users;
select * from mysql.user;

delete from palmdemo;
select * from palmdemo;
insert into palmdemo values (now(), 55);
commit;

show databases;
show tables;

--
-- create table at mysql-style for SF.query_history
-- and handle index 
-- and handle drop, delete
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
INSERTION_TIME		timestamp,
STARTTIME			timestamp	-- cut seconds
);

show index from palmdb.sf_query_history;

create index start_time_ix
on palmdb.sf_query_history(start_time);
create index starttime_ix
on palmdb.sf_query_history(starttime);

drop index start_time_ix
on palmdb.sf_query_history;
drop index starttime_ix
on palmdb.sf_query_history;

drop table palmdb.sf_query_history;

delete from palmdb.sf_query_history ;

-- 
-- insert something to table
insert into palmdb.sf_query_history 
(CREDITS_USED_CLOUD_SERVICES, QUERY_ID, START_TIME, INSERTION_TIME,EXECUTION_TIME )
values (
0.0007,
'2234123005', 
SUBDATE( now(), interval 1 day),
SUBDATE( now(), interval -1 day),
123
);


--
-- select table

select * -- count(*)
from palmdb.sf_query_history
order by start_time desc 
;

select QUERY_ID, START_TIME, CREDITS_USED_CLOUD_SERVICES *1000000, INSERTION_TIME  
from palmdb.sf_query_history 
order by start_time desc 
;

select -- QUERY_ID, 
	START_TIME, STARTTIME, EXECUTION_TIME, CREDITS_USED_CLOUD_SERVICES * 1000000 as TX,
	CREDITS_USED_CLOUD_SERVICES as TXo, USER_NAME, INSERTION_TIME 
from palmdb.sf_query_history 
order by start_time desc 
limit 10
;

select QUERY_ID, START_TIME, EXECUTION_TIME, COMPILATION_TIME, USER_NAME, INSERTION_TIME
from palmdb.sf_query_history 
order by start_time desc 
limit 10
;

select count(*), date_format( starttime, "%Y-%m-%d %H:%i")  from palmdemo
group by date_format( starttime, "%Y-%m-%d %H:%i");

--
-- using data, time at mysql
--

select date(now()), 
	timestamp( now() ),
	timestamp( date(now()) ),
	adddate( timestamp( date(now()) ), interval 1 day) 
;
select timestamp( now() )
	, adddate( timestamp( date(now()) ), interval 1 day) as "x+1d"
	, adddate( timestamp( date(now()) ), interval 1 hour) as "x+1h"
	, adddate( timestamp( now() ), interval 1 hour) as "+1h"
	, time( now() ) as nowtime
	, hour ( time(now()) ) as h
	, minute ( time(now()) ) as m
	, second ( time(now()) ) as s
;

select QUERY_ID, QUERY_TEXT, START_TIME
from palmdb.sf_query_history
where 	start_time >= timestamp( date(now()) )
	and start_time < adddate( timestamp( date(now()) ), interval 1 day)
	-- and start_time >= timestamp( '2023-07-06 16:30:00')
order by start_time desc
-- limit 1
;

select timestamp( date_format( datetime(now()),  '%Y-%m-%d %H:%i:00');  -- not WORK
select            date_format( timestamp(now()), '%Y-%m-%d %H:%i:00');

SELECT date_format(  adddate(START_TIME, interval 9 hour), "%H:%i:%s") as T
FROM palmdb.sf_query_history
ORDER BY START_TIME DESC
LIMIT 1
;


SELECT 
-- date_format(START_TIME, "%Y-%m-%d %H:%i") as CutTime,
-- timestamp( date_format(START_TIME, "%Y-%m-%d %H:%i") ),
 date_format(START_TIME, "%Y-%m-%d %H:%i"),
 sum(CREDITS_USED_CLOUD_SERVICES) as sumCredit
FROM palmdb.sf_query_history 
group by date_format(START_TIME, "%Y-%m-%d %H:%i")
;

SELECT 
 date_format(START_TIME, "%Y-%m-%d %H"),
 sum(CREDITS_USED_CLOUD_SERVICES) as sumCredit
FROM palmdb.sf_query_history 
group by date_format(START_TIME, "%Y-%m-%d %H")
;

select count(*), sum(CREDITS_USED_CLOUD_SERVICES)
from palmdb.sf_query_history
where start_time >= adddate(timestamp(date( now() )), interval -1 day)
	and start_time < adddate(timestamp(date( now() )), interval 0 day)
order by start_time desc
-- limit 1
;
--
-- Others

select now(), @@system_time_zone, @@Global.time_zone, @@session.time_zone;

--
--
