# Alternative: Native MongoDB Setup

This lab's main [README](README.md) uses a Dockerized MongoDB instance. If you prefer to install MongoDB
natively instead, follow the instructions below.

**2.1** Shutdown/kill all XAP and MongoDB processes. <br />
**2.2** [Download MongoDB Community Edition](https://www.mongodb.com/download-center/community)

**2.3** [Download MongoDB Shell](https://www.mongodb.com/try/download/shell)

**2.4** [Follow MongoDB Community installation instructions](https://docs.mongodb.com/manual/administration/install-community)

Note: Run `mongod` if you don't intend to install the service. The data files are written to `C:/data/db`.

**Note:** the app connects to MongoDB with no authentication (just host/port, see `mongoClient` bean in
each `pu.xml`), so no user/database setup step is required either way — MongoDB creates the `mnbillbuddy`
database automatically on first write, whether native or Dockerized.
