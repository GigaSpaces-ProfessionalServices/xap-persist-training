# lab7-exercise - Custom Persistency 

## Lab Goals

•	Implement and configure persistency for Space class using custom persistency implementation. <br />


## Lab Description
•	During this lab you will deploy Bill Buddy Application & persist data from the space to a relational database using custom persistency hook implementation. <br />  
•	We will utilize standard JDBC calls instead of hibernate. <br />
•	The lab already include implemented DAO with all JDBC code in it. You are only required to modify the relevant XAP files and pu.xml <br />
•	You can use this demo as a reference for any other implementation you require.
•	Once Persisting space class to the database you will configure initial load to load space class previously stored the relational database. <br /> 


## Lab setup	
Make sure you restart gs-agent and gs-ui (or at least undeploy all Processing Units using gs-ui) <br />
##### In our Lab we will cover
a.	Creation of database instance in MySQL database. <br />
b.	Configuration of the space & mirror service to use custom made persistency for persisting space classes & loading them in initial load via JDBC (not using Hibernate). <br />
c.	Implement a set of classes to support custom persistency. <br />

    
## 7.1	Clone and build the project lab

7.1.1 Create lab directory

    mkdir ~/XAPPersistTraining/labs/lab7-exercise
      
7.1.2 Clone the project from git
    
    cd ~/XAPPersistTraining/labs/lab7-exercise
    git clone https://github.com/GigaSpaces-ProfessionalServices/xap-persist-training.git 
    
7.1.3 Checkout lab7-exercise
    
    cd xap-persist-training
    git checkout lab7-exercise
    
7.1.4 Verify that the branch has been checked out.
    
    git branch
    * lab7-exercise
      master 
    
7.1.5 Open xap-persist-training project with intellij <br />

#### Notice the following 5 modules in Intellij: ####

##### BillBuddy-Space #####
Contains a processing Unit with embedded space and business logic <br />

##### BillBuddyModel #####
Defines all declarations that are required, in space side as well as the client application side.
This project should be deployed with all other projects since all other projects are dependent on the model. <br />

##### BillBuddyAccountFeeder #####
A client application (PU) that will be executed in Eclipse. This application is responsible for writing Users and Merchants to the space. <br />

##### BillBuddyPaymentFeeder #####
A client application that simulates an initial payment process. It creates a payment every second. <br />

##### BillBuddyPersistency #####
The data source configuration
       
7.1.6 Run mvn install <br />

    yuval-pc:xap-persist-training yuval$ mvn install
    
    
       [INFO] ------------------------------------------------------------------------
       [INFO] Reactor Summary:
       [INFO] 
       [INFO] BillBuddyModel ..................................... SUCCESS [  3.624 s]
       [INFO] lab7-exercise 1.0-SNAPSHOT ......................... SUCCESS [  0.049 s]
       [INFO] BillBuddy_Space .................................... SUCCESS [  2.404 s]
       [INFO] BillBuddyAccountFeeder ............................. SUCCESS [  1.628 s]
       [INFO] BillBuddyPaymentFeeder ............................. SUCCESS [  1.397 s]
       [INFO] BillBuddyPersistency 1.0-SNAPSHOT .................. SUCCESS [  1.999 s]
       [INFO] ------------------------------------------------------------------------
       [INFO] BUILD SUCCESS
       [INFO] ------------------------------------------------------------------------


7.1.7   Run mvn xap:intellij <br />
######This will add the predefined Run Configuration Application to your Intellij IDE.

    yuval-pc:xap-persist-training yuval$ mvn xap:intellij
    
      [INFO] Reactor Summary:
      [INFO] 
      [INFO] lab7-exercise 1.0-SNAPSHOT ......................... SUCCESS [  0.812 s]
      [INFO] BillBuddyModel ..................................... SKIPPED
      [INFO] BillBuddy_Space .................................... SKIPPED
      [INFO] BillBuddyAccountFeeder ............................. SKIPPED
      [INFO] BillBuddyPaymentFeeder ............................. SKIPPED
      [INFO] BillBuddyPersistency 1.0-SNAPSHOT .................. SKIPPED
      [INFO] ------------------------------------------------------------------------
      [INFO] BUILD SUCCESS
      [INFO] ------------------------------------------------------------------------


## 7.2	Database setup

7.2.1   Setup MySQL DB for this lesson.

###### Windows	
	
a.	Go to https://dev.mysql.com/downloads/mysql and download aviable GA MySQL Community Server.<br /> 	
b.  Extract it to: c:\mysql <br />	
c.	Make sure you shut down any prior existing mysqls in your system. <br />	
d.	Open a command window <br />	
e.	Navigate c:\mysql\bin: <br /> 	

     cd C:\mysql\mysql-5.5.48-winx64\bin	

f.	Run MySQL server: 	

     mysqld --console	

g.	Open another command window <br/>	
h.	Navigate to the same Bin directory you navigated to at section (e) <br />	
i.	Run the following command to create BillBuddy database:	

     mysqladmin.exe --user=root create jbillbuddy	


###### Linux

a. Download MySQL <br />

    yum install mysql-server (or sudo apt-get install mysql-server)
          	
b.	Run MySQL server <br />	

     /sbin/service mysqld start (or sudo service mysql start)

c.  Create BillBuddy database <br />

    /usr/bin/mysqladmin --user=root create jbillbuddy	

d.	Validate that your instance has been created 	
	
    /usr/bin/mysql jbillbuddy-u root –p (no password is required)
    
e. Verify no tables exist
	
    show tables;	
	

###### Mac	
	
a.  Download MySQL from here: http://dev.mysql.com/downloads/file/?id=462024 <br />
   	
b.  Open MySQL package installer, which is provided on a disk image (.dmg) that includes the main MySQL installation package file.	
	Double-click the disk image to open it <br />	
c.	Start MySQL service (if you wish to stop or restart run the same command with stop or restart at the end)<br />	
	
    sudo /usr/local/mysql/support-files/mysql.server start
    
    output:
    Starting MySQL
        .. SUCCESS! 
    	
d.  Create BillBuddy database <br />

    cd /usr/local/mysql/bin	
    ./mysqladmin --user=root create jbillbuddy	

e.	Validate that your instance has been created <br />	    

    cd /usr/local/mysql/bin	
    ./mysql jbillbuddy -u root (no password is required)
    
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

    
f. Verify no tables exist <br />
	
    show tables;
    
    output:
    Empty set (0.00 sec)
    
  
## 7.3	Mirror Service Configuration and Setup
7.3.1	Open BillBuddyPersistency project <br /> 

a.	Implement BillBuddySpaceSynchronizationEndpoint <br />
&nbsp;  a.	FIX the TODO <br />
&nbsp;  b.	onOperationsBatchSynchronization()
you are required to store all objects that are taking part in a transaction. <br />
We have provided you a storeObject(Object obj) that is a part of the class. <br />
&nbsp;&nbsp;    i.	Examine the storeObject(Object obj) method. The method is utilizing an already implemented native DAO layer that deals with all JDBC commands. Feel free to examine the DAO code as well. <br />
&nbsp;&nbsp;    ii.	Fix missing implementation the method gets as an input a list of objects. <br />
&nbsp;&nbsp;&nbsp;  iii.	Store each of those object using private method storeObject(Object obj) <br /> 
b.	Edit PU.xml
&nbsp;  a.	FIX TODO <br />
&nbsp;  b.	Add spring scanning for the package where the SpaceSynchronizationEndpoint exists. <br /> 
&nbsp;  c.	Add definition for the supported space classes as part of the custom  SpaceSynchronizationEndpoint (bean supportedManageSpaceClasses). <br />
&nbsp;  d.	For bean billBuddySpaceSynchronizationEndpoint fix: <br /> 
&nbsp;&nbsp;    i.	property datasource to point to the database data source. <br />
&nbsp;&nbsp;    ii.	Class name for the SpaceSynchronizationEndpoint. <br />
&nbsp;  e.	For os-core:mirror fix property space-sync-endpoint to reference the custom SpaceSynchronizationEndpoint (bean billBuddySpaceSynchronizationEndpoint). <br />

## 7.4	Space Configuration For Initial Load
7.4.1	Open BillBuddy_Space project <br /> 

a.	Implement BillBuddySpaceDataSource <br />
&nbsp;   a.	This class will be used to load data from the database <br />
&nbsp;   b.	FIX the TODO <br />
&nbsp;   c.	Method initialDataLoad <br />
&nbsp;&nbsp;    i.	Fix missing implementation of the method gets as an input a list of objects. <br />
&nbsp;&nbsp;    ii.	Load all object from the database using the different DAO object (method readFromDB() ). Hint see check private member to see what DAO are available to you. <br />
b.	Implement CustomDataIterator (No code changes are required. Review code). <br />
&nbsp;  a.	This class is used to return the results. No fix is required, but you can review the simple implementation. <br /> 
c.	Implement BillBuddyCustomFactoryBean
&nbsp;  a.	This class will be used as a factory to create the custom space data source. <br />
&nbsp;  b.	FIX the “TODO”s
&nbsp;  c.	Method getObject()
&nbsp;&nbsp;    i.	Fix missing implementation of creating initiating the private member billBuddySpaceDataSource
&nbsp;  d.	Method getObjectType()
&nbsp;&nbsp;    i.	Fix missing implementation, return BillBuddySpaceDataSource.class
d.	Edit PU.xml
&nbsp;  a.	FIX TODO
&nbsp;  b.	Add spring scanning the for the package that hold the definition of BillBuddyCustomFactoryBean & BillBuddySpaceDataSource exists. <br /> 
&nbsp;  c.	Add definition for the supported space classes as part of the custom  SpaceDataSource (bean supportedManageSpaceClasses). <br />
&nbsp;  d.	For bean billBuddySpaceDataSource fix: 
&nbsp;&nbsp;    i.	property datasource to point to the database data source. <br />
&nbsp;&nbsp;    ii.	Class name for the CustomFactoryBean. <br />
e.	For os-core:space fix property space-data-source to reference the custom CustomFactoryBean ( bean billBuddySpaceDataSource)

## 7.5	Test Solution

7.5.1	Testing Mirror <br />

a.	Make sure the MySQL database service is up and running. <br />
b.  Run gs-agent <br />

    ./gs.sh host run-agent --manager --gsc=2
    
c.  Run gs-ui <br />
d.  Deploy BillBuddy_space to the service grid: <br />

    cd $XAP_HOME/bin
    ./gs.sh pu deploy BillBuddy-Space ~/XAPPersistTraining/labs/lab7-exercise/xap-persist-training/BillBuddy_Space/target/BillBuddy_Space.jar
    
    [BillBuddy_Space.jar] successfully uploaded
    ·····
    Instance [BillBuddy-Space~2_1] successfully deployed
    Instance [BillBuddy-Space~1_1] successfully deployed
    ·
    Instance [BillBuddy-Space~1_2] successfully deployed
    Instance [BillBuddy-Space~2_2] successfully deployed
    
    Processing Unit [BillBuddy-Space] was successfully deployed at 2019-08-19 11:33:2
    
    
e.	Deploy BillBuddPersistency to the service grid

    yuval-pc:bin yuval$ ./gs.sh pu deploy BillBuddyPersistency /Users/yuval/XAPPersistTraining/labs/lab7-exercise/xap-persist-training/BillBuddyPersistency/target/BillBuddyPersistency.jar
    
    [BillBuddyPersistency.jar] successfully uploaded
    ··
    Instance [BillBuddyPersistency~1] successfully deployed
  
f. From the Intellij run configuration select BillBuddyAccountFeeder and run it. <br />
g. From the Intellij run configuration select BillBuddyPaymentFeeder and run it. <br >        
h.	Open MySQL (Windows) Relational Database <br />

###### Windows

&nbsp;  a.	Open windows command window <br />
&nbsp;  b.	Windows Navigate Mysql bin directory: <br />
 
    a.	cd C:\mysql\mysql-5.1.61-winx64\bin 
    
&nbsp;  c.	Run the following command in order to connect to the BillBuddy database: <br />

    mysql -u root -p custbillbuddy
    
&nbsp;  d.  When prompt for a password click <enter>  

###### Linux

i.	Open MySQL Relational Database <br />
&nbsp;  a.	Open terminal <br /> 
&nbsp;  b.	Run: /usr/bin/mysql custbillbuddy -u root –p <br />
&nbsp;  No password is required
j.	Query The Relational Database: <br />
&nbsp;  a.	Use the command in order to view your table list. These tables were created by NHibernate mappings. <br />
 
    show tables; 
    
&nbsp;  b.	Select the content of any table by issuing the following command: <br /> 

    select * from merchant;
    
&nbsp;  c.	Validate the results

  
   ![snapshot](Pictures/Picture1.png)	
   
      
7.5.2	Testing Initial Load <br />

a.	Stop Payment feeder <br />
b.	Kill gs-agent & gs-ui <br />
c.	Make sure the MySQL database service is up and running. <br />
d.	Run gs-agent <br />
e.	Run gs-ui <br />
f.	Deploy BillBuddy_ Space to the service grid. <br />
g.	Check that space load Users, Merchants, Payments, Processing Fee <br />


   ![snapshot](Pictures/Picture2.png)


h.	Execute SQL statement & count that all object have been loaded into the space: <br />

&nbsp;  a.	Connect to MySQL database
&nbsp;  b.	mysql -u root -p custbillbuddy
&nbsp;  c.	Run “select count(*) from payment;”