Reti e Laboratorio: Modulo Laboratorio 3
CROSS: an exChange oRder bOokS Service
Progetto di Fine Corso A.A. 2024/25
Versione 1.3

- Definizione delle scelte effettuate (dove interpretare)

- Schema generale dei thread attivati sia server che client

- Definizione delle strutture dati utilizzate sia server che client

- Descrizione delle primitive di sincronizzazione utilizzate dai thread per le strutture condivise

- Istruzione su come compilare ed eseguire (argomenti, librerie, sintassi dei comandi delle operazioni) -> Manuale di istruzioni

- all stop orders are implemented as day orders: they expire at the end of the current session (namely at server closure)

- user logic and market and limit orders work
- udp work
- tcp work
- persistence work
- stop orders not working
- price history to be finished
- to comment
- to document

Da consegnare:
1. Codice sorgente commentato di tutte le classi e test, deve funzionare con javac
2. Classi con main hanno "Main" nel nome
3. File JAR per client e file JAR per server
4. Parametri di input SOLO da dei file di configurazione
5. Rimuovere file e directory creati dall'IDE
6. Librerie esterne in JAR

[MAVEN] To execute and run with maven:
1a. mvn clean install exec:java -Pserver
[different terminal]
1b. mvn exec:java -Pclient

[JAVAC] To execute and run javac/java from maven project:
1. mvn dependency:copy-dependencies
2. $cp = 'target/classes;target/dependency/*'
3. javac -d target/classes -cp $cp src/main/java/cross/*.java
4a. java -cp $cp cross.ServerMain
[different terminal]
4b. java -cp $cp cross.ClientMain

[JAR] To execute and run javac/java directly from .jar file:
1a. mvn clean package -Pserver
2a. java -jar ./target/cross-server.jar
[different terminal]
1b. mvn package -Pclient
2b. java -jar ./target/cross-client.jar
