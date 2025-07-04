
### [JAVAC]
To compile with javac and run with java:

javac -d target/classes -cp lib/* @sources.txt

[on terminal 1] Server:
java -cp target/classes;lib/* it.unipi.cross.server.ServerMain

[on terminal 2] Client:
java --enable-native-access=ALL-UNNAMED -cp target/classes;lib/* it.unipi.cross.client.ClientMain



### [JAR]
To execute and run directly from .jar file:

[on terminal 1] Server:
java -jar ./cross-server.jar

[on terminal 2] Client:
java --enable-native-access=ALL-UNNAMED -jar ./cross-client.jar 
