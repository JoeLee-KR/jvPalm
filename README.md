# jvPalm
Prometheus & Grafana study sandbox for private...
- current work: 
  - with Zulu jdk11 for x64
  - relay data trasfer from  snowflake to mysql (query_history:mvSFQH)
  - add key-pair to mvSFQH as v2 (user PALMADMIN@snowflake)

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
jvPalm(main)✔$ ll lib
total 170752
-rw-r--r--  1 doogie  staff   252K Oct 31  2023 gson-2.8.9.jar
-rw-r--r--  1 doogie  staff   627K Oct 31  2023 mariadb-java-client-3.1.4.jar
-rw-r--r--  1 doogie  staff   2.4M Oct 31  2023 mysql-connector-j-8.0.33.jar
drwxr-xr-x  4 doogie  staff   128B Nov  6 17:11 old
-rw-r--r--@ 1 doogie  staff    67M Nov  6 16:40 snowflake-jdbc-3.14.5.jar
jvPalm(main)✔$
```

### help for .gitignore
```
out/
lib/
_keys/
!**/src/main/**/out/
!**/src/test/**/out/
.DS_Store
```

+==
