# Alternative: Native MySQL Setup

This lab's main [README](README.md) uses a Dockerized PostgreSQL instance for the mirror service's
database. If you prefer to install MySQL natively instead, follow the instructions below.

**2.1**   Setup MySQL DB for this lesson.

##### Windows	
	
1.	Go to https://dev.mysql.com/downloads/mysql and download available GA MySQL Community Server.<br /> 	
2.  Extract it to: c:\mysql <br />	
3.	Make sure you shut down any prior existing mysqls in your system. <br />	
4.	Open a command window <br />	
5.	Navigate c:\mysql\bin: <br /> 	

     cd C:\mysql\mysql-5.5.48-winx64\bin	

6.	Run MySQL server: 	

     mysqld --console	


##### Linux

1. Download MySQL <br />

    yum install mysql-server (or sudo apt-get install mysql-server)
          	
2.	Run MySQL server <br />	

     /sbin/service mysqld start (or sudo service mysql start)


##### Mac	
	
1.  Download MySQL from: https://dev.mysql.com/downloads/mysql/ <br/>
2.  Open MySQL package installer, which is provided on a disk image (.dmg) that includes the main MySQL installation package file.<br>	
	Double-click the disk image to open it <br />	
3.	Start MySQL service (if you wish to stop or restart run the same command with stop or restart at the end)<br />	
	
    sudo /usr/local/mysql/support-files/mysql.server start
    
    output:
    Starting MySQL
        .. SUCCESS! 


**2.1** Database configuration and setup

1. Run the secure installation (below command gives option set to root password)
```
    mysql_secure_installation
    # Suggested password 'Giga1234$'
    # Mac and Linux users don't forget to use sudo
```
2. Create database and user
	1. Create the database. In a terminal window,  
	   mysqladmin --user=root create jbillbuddy (located in /usr/bin)
	2. Create user and privileges
```
       mysql -u root -p
       # Mac and Linux users, don't forget to use sudo
       # In mysql session
       CREATE USER 'jbillbuddy'@'%' IDENTIFIED BY 'Giga1234$';
       GRANT ALL PRIVILEGES ON jbillbuddy.* TO 'jbillbuddy'@'%';

       # Verify
       SELECT user,host FROM mysql.user; SHOW GRANTS for jbillbuddy;
       exit

       # Optionally, check if you can login with the new user
       mysql -u jbillbuddy -p
```
3. Validate that your instance has been created:
	1.  Open terminal
	2.  Run: mysql jbillbuddy -u root -p
	3.  Run: show tables;
	4.  Verify no tables exist.

## Code changes needed to switch BillBuddyPersistency back to MySQL

If you go this route, three things need to change from what the module currently ships with
(PostgreSQL). Note the target Hibernate version is `7.1.0.Final` (bumped as part of the jakarta
migration) — that matters below, since it dropped the old `MySQL8Dialect` class.

**1. Driver dependency** — `pom.xml` (root) and `BillBuddyPersistency/pom.xml`:

Root `pom.xml`, swap the `postgresql.version` property for `mysql.version`:
```xml
<mysql.version>8.0.28</mysql.version>
```

`BillBuddyPersistency/pom.xml`, swap the `org.postgresql:postgresql` dependency for:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>${mysql.version}</version>
</dependency>
```

**2. Dialect** — `BillBuddyPersistency/src/main/resources/META-INF/spring/pu.xml`, in the
`sessionFactory` bean's `hibernateProperties`:
```xml
<prop key="hibernate.dialect">org.hibernate.dialect.MySQLDialect</prop>
```
Do **not** use `org.hibernate.dialect.MySQL8Dialect` — it existed in the Hibernate 6.x line but was
removed in `7.1.0.Final` (verified by inspecting the jar: only `MySQLDialect` remains). The unversioned
`MySQLDialect` auto-detects the MySQL version via JDBC, same pattern as `PostgreSQLDialect`.

Also remove (or leave — it's harmless, just unnecessary) the Postgres-specific reserved-word fix:
```xml
<prop key="hibernate.globally_quoted_identifiers">true</prop>
```
This was added because `User` is a reserved word in PostgreSQL and unquoted `CREATE TABLE User (...)`
DDL fails there. MySQL doesn't have this problem, so it's not required for MySQL — but if you keep it,
identifiers just get backtick-quoted instead of double-quoted, which still works fine.

**3. `sessionFactory` bean's `dataSource`** — same file, in the `dataSource` bean:
```xml
<property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
<property name="url" value="jdbc:mysql://localhost:3306/jbillbuddy"/>
```
(`com.mysql.cj.jdbc.Driver` is the modern driver class; the legacy `com.mysql.jdbc.Driver` still works
via a deprecated shim in `mysql-connector-java` 8.x but logs a deprecation warning.) `username`/`password`
stay as `jbillbuddy` / `Giga1234$` — no change needed there.

Everything else — the `LocalSessionFactoryBean` relocation to `org.springframework.orm.jpa.hibernate`,
and bundling `spring-orm` as a `compile`-scope dependency in `BillBuddyPersistency/pom.xml` so it lands
in the PU's own `lib/` — is a Spring/Hibernate version issue unrelated to which database you use, so
those stay as-is regardless of MySQL vs PostgreSQL. See the "Known Gotchas" section in the main
[README](README.md) for why those are needed.
