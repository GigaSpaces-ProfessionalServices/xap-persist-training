# xap-persist-training - lab07-schema_evolution

This project demonstrates how to implement service schema evolution in GigaSpaces.

The starting point is a running service called v1-service. The service is persistent, with MongoDB used
as the persistence layer. Data is also persisted to an Apache Kafka cluster, which acts as a buffer
between the space and MongoDB. Entries of type `Person` are continuously written to the service by the
feeder service.

## The v2 service

In the demo, a new service called v2-service is deployed side by side with v1. In v2, there is a new
version of `Person` with the following changes:

1. Two new fields are added: `calculatedField` and `newField`.
2. Field `typeChangeField` is of type `Integer`, where in v1 it was of type `String`.

This service also contains an implementation of the `SpaceTypeSchemaAdapter` interface, used to adapt old
v1 data to its new schema.

## Local environment setup

This lab was originally built for a cloud/Nomad ElasticGrid deployment (see `scripts/`, kept as a
historical reference but not runnable against this checkout - its `deploy_space`/`deploy_stateless_pu`
helpers pull PU jars from a different GitHub repo, not this project's own build output). The steps below
are what's actually verified to work against a local XAP grid.

1. Start MongoDB and Kafka via Docker:

   ```
   docker run -d --name billbuddy-mongo -p 27017:27017 mongo:7
   docker run -d --name schema-evo-kafka -p 9092:9092 -e CLUSTER_ID=<any-valid-cluster-id> apache/kafka:3.9.0
   ```

   (Or, if the containers already exist from a previous run: `docker start billbuddy-mongo schema-evo-kafka`.)
   `v1-mirror`/`v1-final-mirror` persist to the `v1-db` Mongo database, `v2-mirror` to `v2-db` - both in
   the same MongoDB instance. Kafka needs no listener overrides; the image's KRaft-mode defaults work fine
   for a single-node broker.
2. Start the local XAP grid. Between `v1-service` and `v2-service` (2 instances each: 1 partition + 1
   backup) plus `v1-mirror`, `v2-mirror`, and the feeder (1 instance each), you need at least 7 free GSCs
   at peak:

   ```
   cd $GS_HOME/bin
   ./gs.sh host run-agent --auto --gsc=7
   ```

   If a later deploy step fails with "no suitable container available," add more via
   `gs.sh container create --count=N localhost` rather than assuming something is broken.
3. Build the reactor:

   ```
   mvn install
   ```

## The lab flow

The flow is composed of the following steps:

0. Have a look at the `v1-mirror`, `v1-temporary-mirror`, and `v1-final-mirror` PUs, and at the
   `v2-load-v1-db` code.
1. Deploy `v1-service` (partitioned, 1 partition with 1 backup) and `v1-mirror` (stateless, 1 instance):

   ```
   ./gs.sh pu deploy --partitions=1 --ha v1-service <path-to-this-directory>/v1-service/target/v1-service.jar
   ./gs.sh pu deploy --instances=1 v1-mirror <path-to-this-directory>/v1-mirror/target/v1-mirror.jar
   ```
2. Deploy `v2-service` (partitioned, 1 partition with 1 backup) and `v2-mirror` (stateless, 1 instance):

   ```
   ./gs.sh pu deploy --partitions=1 --ha v2-service <path-to-this-directory>/v2-service/target/v2-service.jar
   ./gs.sh pu deploy --instances=1 v2-mirror <path-to-this-directory>/v2-mirror/target/v2-mirror.jar
   ```
3. Deploy the feeder (stateless, 1 instance):

   ```
   ./gs.sh pu deploy --instances=1 feeder <path-to-this-directory>/feeder/target/feeder.jar
   ```

   Watch the `Person` count grow in v1-service; v2-service should have no objects yet.
4. Undeploy `v1-mirror` and deploy `v1-temporary-mirror` in its place (same PU name, different jar):

   ```
   ./gs.sh pu undeploy v1-mirror
   ./gs.sh pu deploy --instances=1 v1-mirror <path-to-this-directory>/v1-temporary-mirror/target/v1-temporary-mirror.jar
   ```
5. Load data from the v1 database into the v2 space by deploying the stateless `v2-load-v1-db` PU:

   ```
   ./gs.sh pu deploy --instances=1 v2-load-v1-db <path-to-this-directory>/v2-load-v1-db/target/v2-load-v1-db.jar
   ```
6. Have a look at the number of `Person` objects and their structure in v2-service - you should see a
   different structure and a different object count than in v1-service.
7. Undeploy `v1-temporary-mirror` and deploy `v1-final-mirror` in its place (same PU name, different jar):

   ```
   ./gs.sh pu undeploy v1-mirror
   ./gs.sh pu deploy --instances=1 v1-mirror <path-to-this-directory>/v1-final-mirror/target/v1-final-mirror.jar
   ```
8. Have a look at the number of `Person` objects in both v1-service and v2-service - both should now have
   a similar number of objects.

Good luck!
