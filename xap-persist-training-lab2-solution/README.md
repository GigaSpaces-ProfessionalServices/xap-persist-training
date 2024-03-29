# xap-persist-training - lab2-solution

## Lab Goals

1. Experience an application deployment process. <br />
2. Get familiar with the BillBuddy application <br />

## Lab Description
In this lab we will focus on deployment and the application and not be concerned with code, therefore simply focus on the deployment process, you will use this process throughout the labs here on.


## 1	Start gs-agent and gs-ui 

1.1 Navigate to %XAP_HOME/bin <br />
        
1.2 Start gs-agent with one GSM, one LUS and 2 GSCs.

    ./gs.sh host run-agent --auto --gsc=5
    
1.3 Start gs-ui.

    ./gs-ui.sh
    
## 2	Deploy BillBuddy_Space
    
2.1 Open %XAP_TRAINING_HOME%/xap-persist-training-lab2-solution project with intellij (open pom.xml) <br />
2.2 Run mvn install <br />

    ~/xap-persist-training/xap-persist-training-lab2-solution$ mvn install
    
    
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary:
    [INFO] 
    [INFO] BillBuddyModel ..................................... SUCCESS [  1.025 s]
    [INFO] BillBuddy_Space .................................... SUCCESS [  0.211 s]
    [INFO] BillBuddyAccountFeeder ............................. SUCCESS [  0.217 s]
    [INFO] BillBuddyCurrentProfitDistributedExecutor .......... SUCCESS [  0.199 s]
    [INFO] BillBuddyWebApplication ............................ SUCCESS [  0.337 s]
    [INFO] BillBuddyPaymentFeeder ............................. SUCCESS [  0.205 s]
    [INFO] Lab2-solution ...................................... SUCCESS [  0.003 s]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS


2.3 IntelliJ path Variables

###### Add GS_LOOKUP_GROUPS & GS_LOOKUP_LOCATORS

2.4 Run mvn xap:intellij

###### This will add the predefined Run Configuration Application to your Intellij IDE.

    ~/xap-persist-training/xap-persist-training-lab2-solution$ mvn xap:intellij
    
    [INFO] ------------------------------------------------------------------------
    [INFO] Reactor Summary:
    [INFO] 
    [INFO] BillBuddyModel ..................................... SKIPPED
    [INFO] BillBuddy_Space .................................... SKIPPED
    [INFO] BillBuddyAccountFeeder ............................. SKIPPED
    [INFO] BillBuddyCurrentProfitDistributedExecutor .......... SKIPPED
    [INFO] BillBuddyWebApplication ............................ SKIPPED
    [INFO] BillBuddyPaymentFeeder ............................. SKIPPED
    [INFO] Lab2-solution ...................................... SUCCESS [  0.486 s]
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS


2.5 Open a new Terminal and navigate to %XAP_TRAINING_HOME%/gigaspaces-xap/bin/ <br />

    cd %XAP_HOME/bin
           
2.6 Use XAP CLI to deploy BillBuddy_Space
 
    ./gs.sh pu deploy BillBuddy-Space /Users/aharonmoll/xap-persist-training/xap-persist-training-lab2-solution/BillBuddy_Space/target/BillBuddy_Space.jar 

## 3	Run BillBuddyAccountFeeder from Intellij

3.1 From the Intellij run configuration select BillBuddyAccountFeeder and run it.

###### This application writes Users and Merchants to the Space
 
3.2 Validate Users and Merchants were written to the space using gs-ui. <br />
 Go to: Space Browser Tab -> Clusters -> Operations -> Data Types. <br />
 Examine the list of classes from which objects were written to the space.
 
![Screenshot](./Pictures/Picture1.png)

3.3 Query the list of Users by executing the following SQL: <br />
Choose the query option and copy the following SQL command to the SQL area: <br />

    SELECT * FROM com.c123.billbuddy.model.User
    
###### Note: Fully qualified class name is required. (You can use copy paste in the gs-ui)

![Screenshot](./Pictures/Picture2.png)

## 4	Run BillBuddyPaymentFeeder project
The BillBuddyPaymentFeeder application creates payments by randomly choosing a user, 
a merchant and an amount and performs the initial process of a payment. 
This includes deposit and withdrawal updates of each party’s balance appropriately. 
After the payment is initially processed it is written to the space for further processing. 
You will be further introduced with this application in a later lesson in greater detail. 
A new Payment is created every second.
 
4.1 Run the BillBuddyPaymentFeeder using Intellij: 
Use the same instructions as used for the BillBuddyAcountFeeder.

4.2 Validate Payments were written to the space using gs-ui. 
You may choose to view Payment Objects using the Query operation of gs-ui.
 
4.3 Go to the statistics operation and see that a payment is actually added every second.
You might be required to modify the sample rate and start the automatic refresh.

![Screenshot](./Pictures/Picture3.png)

4.4 Go to the Data Types view under Operations. Which object counts are increasing?

## 5 Deploy BillBuddyWebApplication project

5.1 Open a new Terminal and navigate to %XAP_TRAINING_HOME%/gigaspaces-xap/bin/

5.2 Use XAP CLI to deploy BillBuddyWebApplication
 
    ./gs.sh pu deploy BillBuddyWebApplication /Users/aharonmoll/xap-persist-training/xap-persist-training-lab2-solution/BillBuddyWebApplication/target/BillBuddyWebApplication.war

5.3 Validate the application is deployed. 
Go to Deployed Processing Units tab and expand the BillBuddyWebApplication PU.

![Screenshot](./Pictures/Picture4.png)

5.4 The URL is for the application home page URL. 
Click on it and get to the application. 

![Screenshot](./Pictures/Picture5.png)

5.5 Congratulations,you have successfully deployed the BillBuddy application.<br>
Navigate through the application pages and investigate it.
