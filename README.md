# jvPalm
Prometheus & Grafana study sandbox for private...
- current work: 
  - with Zulu jdk11 for x64
  - relay data trasfer from  snowflake to mysql (query_history:mvSFQH)
  - add key-pair to mvSFQH as v2 (user PALMADMIN@snowflake)
- snowflake jdbc driver is TOO BIG
  - use a snowflake jdbc thin driver
### toDo
  - find more tables to monitoring... for snowflake

### Remarks
- Depot/promBook: Prometheus Books' workbook
- Depot/promSUNI: Prometheus mySunis' workbook 

### if u have git pull toruble ...
```
$ git pull --no-ff 
$ gac
$ git push
```

### lib directory
```
jvPalm(main)✗$ ll lib
total 16744
-rw-r--r--@ 1 doogie  staff   280K Nov  7 14:02 gson-2.13.1.jar
-rw-r--r--@ 1 doogie  staff   742K Nov  7 14:02 mariadb-java-client-3.5.6.jar
-rw-r--r--@ 1 doogie  staff   2.5M Nov  7 14:02 mysql-connector-j-9.5.0.jar
drwxr-xr-x  6 doogie  staff   192B Nov  7 14:07 old
-rw-r--r--@ 1 doogie  staff   4.7M Nov  7 14:02 snowflake-jdbc-thin-3.14.5.jar
jvPalm(main)✗$ 

```

### help for .gitignore
```
out/
_keys/
!**/src/main/**/out/
!**/src/test/**/out/
.DS_Store
```

+==
