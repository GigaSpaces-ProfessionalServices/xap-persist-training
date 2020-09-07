# GigaSpaces Service Schema Evolution On ElasticGrid - Demo
This project is a demonstration of how to implement service schema evolution in GigaSpaces
The starting point is a running service called v1-service. The service is persistent with mongodb used as the persistence layer. Data is also persisteed to Apache Kafka cluster, which acts as a buffer between the space and mongodb. 
Entries of type Person are continuously written to the service by the v1-feeder service. 

##### The v2 service
In the demo a new service called v2-service is deployed side by side with v1. 
In v2, there is a new version of Person with the following changes:
1. Two new fields are added: calculatedField and newField.
2. Field typeChangeField is of type Integer, where in v1 it was of type String.
This service also contains an implementation of the SpaceTypeSchemaAdapter interface, used to adapt old v1 data to its new schema 

### The lab flow
The flow is composed of the following steps:
0. Have a look at v1-mirror PU , v1-temporary-mirror and v1-final-mirror Pus.and in v2-load-v1-db code.
1. Start default gsctl grid 
2. Run mvn clean install
3. Go to the ops manager use gs-admin as user and token from gsctl as password 
4. Deploy v1-service, v1-mirror, feeder using the ops manager - see Person structure (for spaces add pu property: pu.dynamic-partitioning=true for repartitioning)
5. Deploy v2-service, v2-mirror.
6. Undeploy v1-mirror & deploy v1-temporary-mirror 
7. Load data from db1 to v2 space by deploying stateless pu v2-load-v1-db
8. Have a look at v2 number of Persons & its structure
9. Undeploy v1-mirror & deploy v1-final-mirror
10. Have a look at number of persons in both V1-service & V2-Service


Good Luck
 

