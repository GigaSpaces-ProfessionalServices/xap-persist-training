# Alternative: Native MySQL Setup

This lab's main [README](README.md) uses a Dockerized PostgreSQL instance. If you prefer to install
MySQL natively instead, follow the instructions below.

2.1   Setup MySQL DB for this lesson.
      Please follow the instructions as they appear in section 2.1 of lab 4's README (or its
      [MYSQL_SETUP.md](../lab04-initial_load-solution/MYSQL_SETUP.md) if you've followed this training's
      Docker/PostgreSQL path there).

a.  Create BillBuddy database <br />

    cd /usr/local/mysql/bin	
    ./mysqladmin --user=root create custbillbuddy	

b.	Validate that your instance has been created <br />	    

    cd /usr/local/mysql/bin	
    ./mysql custbillbuddy -u root (no password is required)
    
    output:
    Welcome to the MySQL monitor.  Commands end with ; or \g.
    Your MySQL connection id is 2
    Server version: 5.5.49 MySQL Community Server (GPL)
    
    Copyright (c) 2000, 2016, Oracle and/or its affiliates. All rights reserved.
    
    Oracle is a registered trademark of Oracle Corporation and/or its
    affiliates. Other names may be trademarks of their respective
    owners.
    
    Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.
    
    mysql> 

    
c. Verify no tables exist <br />
	
    show tables;
    
    output:
    Empty set (0.00 sec)

**Note:** if you go this route, `BillBuddyPersistency` and `BillBuddy_Space`'s `pu.xml` `dataSource` beans
need to be reverted to MySQL (`com.mysql.jdbc.Driver`, `jdbc:mysql://localhost:3306/custbillbuddy`), and
the `pom.xml`s need `org.postgresql:postgresql` swapped back for `mysql:mysql-connector-java`. You'd also
want to revert the DAO SQL in `BillBuddyModel`'s `com.c123.billbuddy.dao` package — the `int(11)`/`double`/
`datetime` column types and the `user`→`app_user` table rename were changed specifically for PostgreSQL
compatibility (see the "Known Gotchas" section in the main README).
