# CROSS: an exChange oRder bOokS Service
Reti e Laboratorio: Modulo Laboratorio 3

Progetto di Fine Corso A.A. 2024/25

Versione 1.3

## Write down about
- Definizione delle scelte effettuate (dove interpretare)
- Schema generale dei thread attivati sia server che client
- Definizione delle strutture dati utilizzate sia server che client
- Descrizione delle primitive di sincronizzazione utilizzate dai thread per le strutture condivise
- Istruzione su come compilare ed eseguire (argomenti, librerie, sintassi dei comandi delle operazioni) -> Manuale di istruzioni
- all stop orders are implemented as day orders: they expire at the end of the current session (namely at server closure)

## Status
- user logic and market and limit orders work
- udp work
- tcp work
- persistence work
- stop orders not working
- price history to be finished
- to comment
- to document

## Da consegnare
1. Codice sorgente commentato di tutte le classi e test, deve funzionare con javac
2. Classi con main hanno "Main" nel nome
3. File JAR per client e file JAR per server
4. Parametri di input SOLO da dei file di configurazione
5. Rimuovere file e directory creati dall'IDE
6. Librerie esterne in JAR

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
