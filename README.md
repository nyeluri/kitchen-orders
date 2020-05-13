# CloudKitchen

What it is?
-----------
A simple java app (Java 9 compatible) that simulates a cloud kitchen with 4 actors
- Receptionist Worker : Who takes orders and notifies kitchen and 
- Kitchen Shelf : Holds Hot, Cold, Frozen & Overflow shelves
- Kitchen Worker : Who cooks the order and places it on Kitchen Shelves
- Courier Worker : Who picks the order from Kitchen Shelves and delivers it

How to Run it?
--------------
Unzip the file and navigate to folder location
mvn package
java -cp "target/kitchen-orders-1.0-SNAPSHOT-jar-with-dependencies.jar" com.cloudkitchen.CloudKitchen <ingestion_rate>

How to go through logs
----------------------
- All the logs with be displayed on console
- Search by an order no to see all the states it went through
- A summary of orders received, cooked, placed on shelf, removed, decayed , picked and delivered is printed at the very end for validation purpose.

  `10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders received:132
  10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders cooked:132
  10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders placed on hot shelf:56
  10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders placed on cold shelf:38
  10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders placed on frozen shelf:38
  10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders placed on overflow Shelf:0
  10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders removed from overflow Shelf:0
  10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders moved from overflow Shelf:0
  10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders picked:132
  10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders decayed:0
  10:51:13.100 [main] INFO  com.cloudkitchen.CloudKitchen - Total orders delivered:132`

Final Note
----------
There is more to do this, but owing to time constraints I am limiting the extensibility of it. 