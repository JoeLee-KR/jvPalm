--
-- Oasis Palm, works
-- July 2023
--
-- using set of SNOWFLAKE.ACCOUNT_USAGE.QUERY_HISTORY
SELECT 
	QUERY_ID,
	QUERY_TEXT,
	DATABASE_NAME,
	SCHEMA_NAME,
	QUERY_TYPE,				-- 5
	SESSION_ID,
	USER_NAME,
	ROLE_NAME, 
	WAREHOUSE_NAME,	
	WAREHOUSE_SIZE,			-- 10
	WAREHOUSE_TYPE,
	CLUSTER_NUMBER,
	EXECUTION_STATUS,
	ERROR_CODE,
	ERROR_MESSAGE,			-- 15
	START_TIME,
	END_TIME,
	TOTAL_ELAPSED_TIME,
	BYTES_SCANNED,
	PERCENTAGE_SCANNED_FROM_CACHE,	-- 20
	COMPILATION_TIME,
	EXECUTION_TIME,
	CREDITS_USED_CLOUD_SERVICES	,
	RELEASE_VERSION,
	TRANSACTION_ID,			-- 25
	CHILD_QUERIES_WAIT_TIME,
	ROLE_TYPE				-- 27, 28 AS NOWTS
FROM SNOWFLAKE.ACCOUNT_USAGE.QUERY_HISTORY
WHERE START_TIME >= to_TIMESTAMP('2023-07-06 00:00:00')
    AND START_TIME < to_TIMESTAMP('2023-07-07 00:00:00')
    -- AND START_TIME >= to_TIMESTAMP('2023-07-06 09:00:00')
--    AND TOTAL_ELAPSED_TIME >= 120000
--    AND QUERY_TYPE = 'SELECT'
--    AND EXECUTION_STATUS <> 'SUCCESS'
--    AND SCHEMA_NAME <> 'INFORMATION_SCHEMA'
--    AND USER_NAME = 'xjoelee@sk.com'
ORDER BY START_TIME DESC
LIMIT 100
;

--
-- create table inform at mysql-style for SF.query_history
QUERY_ID        varchar(256) primary key,
QUERY_TEXT      text,				-- other varchar(4096)
DATABASE_NAME   varchar(256),
SCHEMA_NAME     varchar(256),
QUERY_TYPE      varchar(256),

SESSION_ID      decimal(38,0),    -- BigDecimal@java 16 digits
USER_NAME       varchar(256),
ROLE_NAME		varchar(256),
WAREHOUSE_NAME		varchar(256),
WAREHOUSE_SIZE		varchar(256),

WAREHOUSE_TYPE		varchar(256),
CLUSTER_NUMBER		decimal(38,0),
EXECUTION_STATUS	varchar(256),
ERROR_CODE			varchar(256),
ERROR_MESSAGE		varchar(256),

START_TIME			timestamp,
END_TIME			timestamp,
TOTAL_ELAPSED_TIME	decimal(38,0),
BYTES_SCANNED		decimal(38,0),
PERCENTAGE_SCANNED_FROM_CACHE	DOUBLE,   -- must double AT SF

COMPILATION_TIME	decimal(38,0),
EXECUTION_TIME		decimal(38,0),
CREDITS_USED_CLOUD_SERVICES		DOUBLE,		-- must double AT SF
RELEASE_VERSION		varchar(256),
TRANSACTION_ID		decimal(38,0),	-- BigDecimal@java

CHILD_QUERIES_WAIT_TIME		decimal(38,0),
ROLE_TYPE			varchar(256)


--
-- some selects
SELECT * -- count(*)
FROM SNOWFLAKE.ACCOUNT_USAGE.QUERY_HISTORY
WHERE   START_TIME >= to_TIMESTAMP('2023-07-06 00:00:00')
    AND START_TIME < to_TIMESTAMP('2023-07-07 00:00:00')
GROUP BY WAREHOUSE_NAME
;

DESC TABLE SNOWFLAKE.ACCOUNT_USAGE.QUErY_hiSTORY;

select QUERY_TEXT, start_TIME -- * --count(*)
from SNOWFLAKE.ACCOUNT_USAGE.QUERY_HISTORY
WHERE USER_NAME = 'PALMADMIN'
order by start_time DESC
LIMIT 100;
;

-- DURATION: Today and before 1 hour
select QUERY_ID, START_TIME, USER_NAME, COMPILATION_TIME, EXECUTION_TIME, CREDITS_USED_CLOUD_SERVICES 
from SNOWFLAKE.ACCOUNT_USAGE.QUERY_HISTORY
where 	start_time >= to_timestamp( date(CURRENT_TIMESTAMP()) )
	-- and start_time < to_timestamp( dateadd( DAY, 1, date(CURRENT_TIMESTAMP()) ) )
	and start_time >= to_timestamp( dateadd( minute, -20, CURRENT_TIMESTAMP())  )
order by start_time desc
limit 100
;

-- DURATION: specified datetime
select QUERY_ID, START_TIME, USER_NAME, COMPILATION_TIME, EXECUTION_TIME, CREDITS_USED_CLOUD_SERVICES 
from SNOWFLAKE.ACCOUNT_USAGE.QUERY_HISTORY
where 	start_time >= to_timestamp( '2023-07-12 14:41:14' )
	and start_time < to_timestamp( '2023-07-12 14:56:14')
order by start_time desc
limit 100
;

--
-- time range handle example
-- line 1,2: now_day0 > now_day0+1
-- line 3,4: now_day0+Hour0 > now_day0+Hour1
-- line 5,6: now_time >> now_time-x minutes
SELECT CURRENT_TIMESTAMP(),
	to_timestamp( date(CURRENT_TIMESTAMP()) ) AS "NOW_day+0", 
	to_timestamp( dateadd( DAY, 1, date(CURRENT_TIMESTAMP()) ) ) AS "NOW_day+1",
	to_timestamp( dateadd( HOUR, 7,     date(CURRENT_TIMESTAMP()) ) ) AS "NOW_day+Xhour",
	to_timestamp( dateadd( HOUR, (7)+1, date(CURRENT_TIMESTAMP()) ) ) AS "NOW_day+Xhour+1",
	to_timestamp( CURRENT_TIMESTAMP() ) AS "NOW_TIME+0", 
	to_timestamp( dateadd( minute, -(12), CURRENT_TIMESTAMP() ) ) AS "NOW_TIME-X",
	CURRENT_TIMESTAMP() AS NOW
	;

SELECT CURRENT_TIMESTAMP(),
  dateadd( DAY,1, current_timestamp() ) 								AS "NOW datetime + 1d",
  date(CURRENT_TIMESTAMP())											AS "NOW date",
  to_timestamp( date(CURRENT_TIMESTAMP()))							AS "NOW datetime 00:00:00",
  dateadd( DAY,  0, to_timestamp( date(CURRENT_TIMESTAMP())) )		AS "NOW datetime 00:00:00 +0",
  dateadd( DAY, -1, to_timestamp( date(CURRENT_TIMESTAMP())) )		AS "NOW datetime 00:00:00 -1d";

-- count, sum within duration
SELECT count(*), sum( CREDITS_USED_CLOUD_SERVICES )
FROM SNOWFLAKE.ACCOUNT_USAGE.QUERY_HISTORY
WHERE 	start_time >= dateadd( DAY, -1, to_timestamp( date(CURRENT_TIMESTAMP())) )
	AND start_time <  dateadd( DAY, 0, to_timestamp( date(CURRENT_TIMESTAMP())) )
	;

-- at WAREHOUSE_METERING_HISTORY
SELECT SUM(CREDITS_USED), SUM(CREDITS_USED_COMPUTE), SUM(CREDITS_USED_CLOUD_SERVICES)
FROM SNOWFLAKE.ACCOUNT_USAGE.WAREHOUSE_METERING_HISTORY
WHERE 	start_time >= dateadd( DAY, -4, to_timestamp( date(CURRENT_TIMESTAMP())) )
	AND start_time <  dateadd( DAY, -3, to_timestamp( date(CURRENT_TIMESTAMP())) )
ORDER BY START_TIME DESC
;

-- at WAREHOUSE_METERING_HISTORY
SELECT *
FROM SNOWFLAKE.READER_ACCOUNT_USAGE.WAREHOUSE_METERING_HISTORY  
WHERE 	START_TIME >= dateadd( DAY, -1, to_timestamp( date(CURRENT_TIMESTAMP())) )
	AND START_TIME <  dateadd( DAY, -0, to_timestamp( date(CURRENT_TIMESTAMP())) )
ORDER BY START_TIME DESC 
;




SELECT *
FROM SNOWFLAKE.ACCOUNT_USAGE.USERS
WHERE DELETED_ON IS NULL
ORDER BY USER_ID DESC
-- WHERE EMAIL <> 'NULL'
;



