# CROSS: an exChange oRder bOokS Service
Reti e Laboratorio: Modulo Laboratorio 3

Progetto di Fine Corso A.A. 2024/25

Versione 1.3

## Usage

### [MAVEN]
To execute and run with maven:
1a. mvn clean install exec:java -Pserver
[different terminal]
1b. mvn exec:java -Pclient

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
