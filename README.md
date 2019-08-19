# Persistency – Mirror Service - Lab4-exercise

## Lab Goals

1. Understand the tasks involved in implementing a mirror service. <br />
2. implement a mirror service. <br />

## Lab Description
This lab includes 1 solution in which we will perform the tasks required to implement a mirror service. 	
Use the slides from the lesson as a reference.

## Lab setup	
Make sure you restart gs-agent and gs-ui (or at least undeploy all Processing Units using gs-ui)
    
## 4.1	Clone and build the project lab

4.1.1 Create lab directory

    mkdir ~/XAPPersistTraining/labs/Lab4-exercise
      
4.1.2 Clone the project from git
    
    cd ~/XAPPersistTraining/labs/Lab4-exercise
    git clone https://github.com/GigaSpaces-ProfessionalServices/xap-persist-training.git 
    
4.1.3 Checkout Lab4-exercise
    
    cd xap-persist-training
    git checkout Lab4-exercise
    
4.1.4 Verify that the branch has been checked out.
    
    git branch
    * Lab4-exercise
      master 
    
4.1.5 Open xap-persist-training project with intellij <br />

#### Notice the following 5 modules in Eclipse: ####

##### BillBuddy-Space #####
Contains a processing Unit with embedded space and business logic <br />

##### BillBuddyModel #####
Defines all declarations that are required, in space side as well as the client application side.
This project should be deployed with all other projects since all other projects are dependent on the model. <br />

##### BillBuddyAccountFeeder #####
A client application (PU) that will be executed in Eclipse. This application is responsible for writing Users and Merchants to the space. <br />

##### BillBuddyPaymentFeeder #####
A client application that simulates an initial payment process. It creates a payment every second. <br />

##### BillBuddyWebApplication #####
The Bill Buddy Web Application <br />


       
4.1.6 Run mvn install <br />

    yuval-pc:xap-persist-training yuval$ mvn install
    
    
       [INFO] ------------------------------------------------------------------------
       [INFO] Reactor Summary:
       [INFO] 
       [INFO] BillBuddyModel ..................................... SUCCESS [  3.624 s]
       [INFO] Lab4-exercise 1.0-SNAPSHOT ......................... SUCCESS [  0.049 s]
       [INFO] BillBuddy_Space .................................... SUCCESS [  2.404 s]
       [INFO] BillBuddyAccountFeeder ............................. SUCCESS [  1.628 s]
       [INFO] BillBuddyPaymentFeeder ............................. SUCCESS [  1.397 s]
       [INFO] BillBuddyPersistency 1.0-SNAPSHOT .................. SUCCESS [  1.999 s]
       [INFO] ------------------------------------------------------------------------
       [INFO] BUILD SUCCESS
       [INFO] ------------------------------------------------------------------------


4.1.7   Run mvn xap:intellij <br />
######This will add the predefined Run Configuration Application to your Intellij IDE.

    yuval-pc:xap-persist-training yuval$ mvn xap:intellij
    
       [INFO] Reactor Summary:
       [INFO] 
       [INFO] BillBuddyModel ..................................... SKIPPED
       [INFO] Lab4-exercise 1.0-SNAPSHOT ......................... SUCCESS [  1.476 s]
       [INFO] BillBuddy_Space .................................... SKIPPED
       [INFO] BillBuddyAccountFeeder ............................. SKIPPED
       [INFO] BillBuddyPaymentFeeder ............................. SKIPPED
       [INFO] BillBuddyPersistency 1.0-SNAPSHOT .................. SKIPPED
       [INFO] ------------------------------------------------------------------------
       [INFO] BUILD SUCCESS
       [INFO] ------------------------------------------------------------------------

    
## 4.2	Persistency – Mirror Service Implementation

4.2.1   Setup MySQL DB for this lesson.

###### Windows	
	
a.	Go to https://dev.mysql.com/downloads/mysql and download aviable GA MySQL Community Server.<br /> 	
b.  Extract it to: c:\mysql <br />	
c.	Note: Make sure you shut down any prior existing mysqls in your system. <br />	
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
	
    /usr/bin/mysql jbillbuddy-u root –p (No password is required)
    
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

    vi ~/.bash_profile
    export XAP_TRAINING_HOME=~/XAPPersistTraining
    source ~/.bash_profile
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
   
4.2.2   Configure your space to be mirror service aware. <br />.	
a. Modify your embedded Space pu.xml. mirrored="true" space element tag (Hint: BillBuddy_space pu.xml) <br />	

4.2.3   Map the data model to tables (using Hibernate. we will use annotations.) <br />	
a.	Search the data model to see which POJOs were chosen for persistency for our demo <br />	
b.	Examine specifically the User and Address relationship and try to figure out the meaning of the hibernate annotations. <br />	

4.2.4	Configure the mirror service. <br />	
The mirror service requires having to be configured appropriately. 	
The lab is already configured correctly for you. 	
Your task is to locate the file in which the configuration is defined.	
Basically you should be able to answer the following questions prior to configuring the environment. <br /> 	
a.	What space am I Mirroring? <br />	
###### Answer: BillBuddy-space <br />	
b.	Which POJOs am I to persist? <br />	
###### Answer: In this lab we will persist: User, Merchant, Payment, ProcessingFee and Contract	
Package Name: com.c123.billbuddy.model <br />	
c.	What is the database (in most cases) that I am persisting to? <br /> 	
###### Answer: we will use MySQL DB for demo purposes. <br />			
d.	What are the DB user name, DB password, JDBC URL and JDBC Driver? <br />	
###### Answer: 	

   ![snapshot](Pictures/Picture1.png)	

4.2.5  The following tasks will make it clearer how to implement a Mirror service. <br />	
Hint: Use slides from the lesson as a reference. Most tasks are already implemented. <br />	
a.	Expand BillBuddyPersisitency and open the pu.xml file. <br />	
b.	Locate the data source bean (DB Connection properties). 	
Write down the user and the password for the MySQL DB database 	
(You will use it later). <br />	
c.	Specify Space Components to be mapped using package scanning. 	

Configure Spring to locate your hibernate annotated classes. <br />	
1.	Fill in the package to be scanned where your persistent 	
POJOs are located 	
(Search the POJOs in the model that were annotated with @Entity and write their full name in the SessionFactory bean). 	
<property name="packagesToScan" value="com.c123.billbuddy.model" />	
2.	Hint: 4 classes only for this demo (but all in same package)	
d.	Specify the mirror to recognize the mirror space (This step is already implemented)	
1.	Complete the os-core:mirroros-core:source-space	
2.	Use slides from the lesson as a reference. <br />	

4.2.6   Make sure you have a Database ready for use.	
We will using MySQL db instance. <br />	
a.	Make sure you have the MySQL instance up and running.

4.2.7   Jar the BillBuddyPersistency project. Make sure to include the BillBuddyModel in the Jar. <br />	
4.2.8   Deploy and test the Mirror service (and your space). <br />	

a.	Run gs-agent <br />
b.	Run gs-ui <br />	
c.	Deploy BillBuddy_space to the service grid. <br />	
d.	Deploy BillBuddPersistency to the service grid (Remember to include BillBuddy model Project. See below) <br />	
e.	Validate Mirror service deployed using gs-ui <br />	

 ![snapshot](./Pictures/Picture2.png)	


f.  Use the gs-ui to locate the GSC which contain the deployed mirror service. <br /> 
Check the GSC log and validate successful deployment. <br />	
Search for the following message in both GSCs that contain primary space instances:

    2014-02-07 14:16:14,884 BillBuddy_space.2 [1] INFO [com.gigaspaces.replication.channel.BillBuddy-space2.primary-backup-reliable-async-mirror-2.mirror-service] - Outgoing Replication Channel: moved to state: CONNECTED 	

 2.2.4 Clone the project from git
![snapshot](./Pictures/Picture3.png)