# lab9-solution - Redolog in SQLite

## Lab Goals

•	Configure Redolog, explore Redolog content when configured with sqlite, Flush memory portion to disk, replay redolog . <br />


## Lab Description
•	During this lab you will deploy space configured with mirror without deploying a mirror to generate a redolog
    We will run Writer to write data into space <br />  
•	We will use a distributed task to flush redolog data into the disk <br />
•	We will use a ProccesRedolog to read data from sqlite and replay it<br />
•	We will use a SQLite Browser  to see redolog Data<br />
•	This example shows space with one partition, for multiple partitions you should run ProccesRedolog per ecah redolog file on all machines<br />



## Lab setup

1. Start grid with 2 GSC (./gs.sh host run-agent --auto --gsc=2)
2. Have a look at CustomSpaceConfig in my-app-space module verify configuration is clear
3. Deploy my-app-space (1 ha)
4. Have a look at Writer in redolo-client module verify you understand the flow and FlushRedoLogTask
5. Run Writer
6. Check redolog size and space data using ui-tool (export query results)
7. Run FlushRedologToDisk
8. Install sqlite browser https://sqlitebrowser.org/dl/
9. Open sqlite browser with file: gs-home/work/redo-log/redolog/sqlite_storage_redo_log_redolog_container1 (redolog is name of the space)
10. Copy files under  gs-home/work/redo-log/redolog to a backup location backup/work/redo-log/redolog
9. Shutdown the grid
10. Start the grid again  (./gs.sh host run-agent --auto --gsc=2)
11. Deploy my-app-space (1 ha) see all is empty as expected
12. Run ProccesRedolog to write all data back to space, point it to gs-home which is backup location by setting vm arg, eg:-Dcom.gs.home="/home/backup", 
    and relevant space and container names as program args e.g redolog redolog_container1
13. Compare space data with original data

