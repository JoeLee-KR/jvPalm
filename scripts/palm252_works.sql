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

select query_text -- * -- count(*)
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

select QUERY_ID, START_TIME, STARTTIME, EXECUTION_TIME, COMPILATION_TIME, USER_NAME, INSERTION_TIME

select count(*)
from palmdb.sf_query_history 
where start_time >= ('2023-07-15 00:00:00')
  and start_time < ('2023-07-15 01:00:00')
order by start_time desc
;

delete from palmdb.sf_query_history 
where start_time >= ('2023-07-15 00:00:00')
  and start_time < ('2023-07-15 00:02:00')

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
SELECT 
	STARTTIME,
	USER_NAME, 
	WAREHOUSE_NAME,
	sum(TOTAL_ELAPSED_TIME)/1000 as TT,
	sum(EXECUTION_TIME)/1000 as TE,
	sum(COMPILATION_TIME)/1000 as TC,
	sum(CHILD_QUERIES_WAIT_TIME)/1000 as TW,
	sum(BYTES_SCANNED) as Scan,
	COUNT(*)
from PALMDB.SF_QUERY_HISTORY
where start_time >= adddate(timestamp(now()), interval -24 hour)
	and start_time < adddate(timestamp(now()), interval 0 day)
group by STARTTIME, USER_NAME, WAREHOUSE_NAME
order by STARTTIME DESC
-- limit 10
;

SELECT  
	STARTTIME,
	USER_NAME,
	-- ROLE_NAME,
	COUNT(*)
from PALMDB.SF_QUERY_HISTORY
-- where start_time >= adddate(timestamp(now()), interval -2 hour)
--	and start_time < adddate(timestamp(now()), interval 0 day)
group by STARTTIME, USER_NAME
order by STARTTIME DESC
limit 1000
;

select date( now() ) as TDATE,
	user_name,
	count(*) as TCOUNT
from palmdb.sf_query_history 
where date( start_time ) = date( now() )
group by user_name
order by TCOUNT DESC
;

SELECT  
	COUNT(*) AS OTHERS
from palmdb.sf_query_history 
where USER_NAME not in ('MSTR_PRD', 'WEB_EXA_PRD', 'CDC_PRD', 'MDWDBA', 'PRD_DEVELOPER1', 'DQMADMIN') 
	and date( start_time ) = date( now() )

-- order by STARTTIME
;
SELECT  
	STARTTIME,
	USER_NAME,
	COUNT(*) AS OTHERS
from PALMDB.SF_QUERY_HISTORY
-- where STARTTIME = timestamp('2023-07-21 08:30:00')
 where USER_NAME not in ('MSTR_PRD', 'WEB_EXA_PRD', 'CDC_PRD', 'MDWDBA', 'PRD_DEVELOPER1', 'DQMADMIN', 'SYSTEM')
group by STARTTIME, USER_NAME
order by STARTTIME DESC


select * -- count(*)
from PALMDB.SF_QUERY_HISTORY
where START_TIME >=('2023-07-21 11:00:00')
	and START_TIME <('2023-07-21 12:00:00')
	-- and TOTAL_ELAPSED_TIME > 30000
	and user_name = 'DQMADMIN'
	and cluster_number=2
-- order by start_time asc
	order by start_time, TOTAL_ELAPSED_TIME desc
	;

--
-- pivot
select *
from (
	select STARTTIME, USER_NAME, TOTAL_ELAPSED_TIME as QCNT from PALMDB.SF_QUERY_HISTORY group by STARTTIME, USER_NAME order by STARTTIME desc
) as result
pivot (
	sum(TOTAL_ELAPSED_TIME) for USER_NAME in ('MSTR_PRD', 'PRD_DEVELOPER1', 'PALMADMIN')
) as pivot_result
order by starttime 
;

--
-- pivot

select 
	starttime,
	sum(ALLUSERS)	as ALLUSERS,
	sum(DQMADMIN)	as DQMADMINS,
	sum(INSYSTEM)	as SYSTEMS,
	sum(DEVELOPER)	as DEVELOPERS,
	sum(MSTR) 		as MSTRS
from (
	select
		STARTTIME,
		case when USER_NAME = 'DQMADMIN' then 1
			else 0
			end DQMADMIN,
		case when USER_NAME = 'SYSTEM'  then 1
			-- when USER_NAME = 'PALMADMIN' then 1
			when USER_NAME = 'CDC_PRD' then 1
			when USER_NAME = 'WEB_EXA_PRD' then 1
			else 0
			end INSYSTEM,
		case when USER_NAME = 'PRD_DEVELOPER1' then 1
			when USER_NAME = 'DEV_DEVELOPER1' then 1
			else 0
			end DEVELOPER,
		case when USER_NAME = 'MSTR_PRD' then 1
			else 0
			end MSTR,
		case when USER_NAME <> 'NULL' then 1
			else 0
			end ALLUSERS
	from PALMDB.SF_QUERY_HISTORY
) result
group by STARTTIME
order by STARTTIME desc
;

select 
	starttime,
  	sum(ALLUSERS) /1000	as ALLUSERS,
	sum(DQMADMIN) /1000	as DQMADMINS,
	sum(PALMADMIN) /1000 as PALMADMINS,
	sum(DEVELOPER) /1000 as DEVELOPERS,
	sum(MSTR) /1000		as MSTRS
from (
	select
		STARTTIME,
		case when USER_NAME = 'DQMADMIN' then TOTAL_ELAPSED_TIME
			else 0
			end DQMADMIN,
		case when USER_NAME = 'PALMADMIN' then TOTAL_ELAPSED_TIME
			else 0
			end PALMADMIN,
		case when USER_NAME = 'PRD_DEVELOPER1' then TOTAL_ELAPSED_TIME
			else 0
			end DEVELOPER,
		case when USER_NAME = 'MSTR_PRD' then TOTAL_ELAPSED_TIME
			else 0
			end MSTR,
		case when USER_NAME <> 'NULL' then TOTAL_ELAPSED_TIME
			else 0
			end ALLUSERS
	from palmdb.sf_query_history sqh
) result
group by STARTTIME
order by STARTTIME desc
;

--
-- WH_PRD_LOAD_XS, WH_PRD_XS, WH_DEV_XS, WH_DEV_DQM_TEST
--
SELECT 
  START_TIME,
  CLUSTER_NUMBER as WH_PRD_LOAD_XS,
  WAREHOUSE_NAME 
FROM palmdb.sf_query_history
WHERE WAREHOUSE_NAME='WH_PRD_LOAD_XS'
;

select 
	start_time,
	CNWH_DQM as WH_DQM,
	CNWH_DEV as WH_DEV,
	CNWH_LOAD as WH_LOAD,
	CNWH_PRD	as WH_PRD
from (
	select
		start_TIME,
		case when WAREHOUSE_NAME = 'WH_DEV_DQM_TEST' then CLUSTER_NUMBER
			else 0
			end CNWH_DQM,
		case when WAREHOUSE_NAME = 'WH_DEV_XS' then CLUSTER_NUMBER
			else 0
			end CNWH_DEV,
		case when WAREHOUSE_NAME = 'WH_PRD_LOAD_XS' then CLUSTER_NUMBER
			else 0
			end CNWH_LOAD,
		case when WAREHOUSE_NAME = 'WH_PRD_XS' then CLUSTER_NUMBER
			else 0
			end CNWH_PRD
	from palmdb.sf_query_history sqh
) result
-- group by START_TIME
order by START_TIME desc
;


