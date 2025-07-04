# CROSS: Exchange Order Book Service

**Author:** Ernesto Cioli  
**Academic Year:** 2024/25  
**Course:** Network Lab  
**Language:** Java 11+  

---

## Overview

CROSS is an educational implementation of a centralized exchange order book service supporting trading operations for the BTC/USD pair. It demonstrates core concepts of networked client-server architecture, concurrent programming, and financial trading algorithms. The project consists of a multithreaded server and a CLI-based client, communicating via TCP and UDP, and using JSON for data serialization and persistence.

---

## Features

### Core Functionality

- **User Registration & Authentication:**  
  - Registration and login with username/password, including password updates and logout.
  - Passwords are in plain text for simplicity purposes: in case of a serious implementation, encryption must be added.

- **Order Management:**  
  - **Limit Orders:** Place bid/ask orders at specific prices.
  - **Market Orders:** Buy/sell immediately at the best available price.
  - **Stop Orders:** Conditional orders that become market orders if a price threshold is reached.
  - **Order Cancellation:** Cancel specified unfilled orders.

- **Order Book & Matching:**  
  - Real-time order book maintenance with price/time priority matching.
  - Aggregation of orders at the same price, fair and efficient order matching.
  - Immediate execution for market orders and triggered stop orders.

- **Trade Notifications:**  
  - Asynchronous notifications to users upon order completion via UDP.
  - Triggered stop orders final result is also notified via UDP.

- **Historical Price Data:**  
  - Query daily open, close, min, and max prices per specified month.
  - Useful for candlestick charting.

- **Persistence:**  
  - Users, active limit orders, and trade history are stored in JSON files.
  - Periodic autosave for resilience and data consistency.

### Technical Highlights

- **Multithreaded Server:**  
  Uses a cached thread pool for handling concurrent client connections.
- **Concurrency:**  
  Thread-safe data structures: `ConcurrentHashMap`, `ConcurrentSkipListSet`, and atomic variables.
- **Robust Network Handling:**  
  Graceful handling of server/client disconnects, timeouts, and shutdown.
- **CLI Client:**  
  Command-line interface for all user operations.
- **Configuration Files:**  
  All network and operational parameters are loaded from text files.
  Useful for rapid change of parameters without touching the actual code.

---

## System Architecture

### Server-Side Threads

- **ServerMain:** Initializes server, networking, and data structures.
- **TcpWorker Pool:** Manages client requests.
- **UdpNotifier:** Sends asynchronous UDP notifications (inserts a slight delay to prioritize TCP responses).
- **PersistenceManager:** Periodic autosave of users, orders, and trades.
- **ShutdownHook:** Graceful resource cleanup on exit.

### Client-Side Threads

- **ClientMain:** CLI interface and configuration loader.
- **TcpReceiver:** Asynchronously receives server responses (main purpose: handles server disconnection).
- **UdpListener:** Listens for server UDP notifications.
- **keepAlive:** Maintains active TCP session via periodic messages (blocks client timeout allowing trade system analysis via stop orders).
- **ShutdownHook:** Graceful exit for client resources.

---

## Data Structures

- **Order Book:**  
  - `ConcurrentSkipListSet` for bid/ask sides, auto-sorted for price/time priority.
- **User & Order Tracking:**  
  - `ConcurrentHashMap` for users, orders, and trades.
- **Other:**  
  - `AtomicInteger` for order IDs, `LinkedHashMap` and lists for supporting data.

---

## Usage

### Prerequisites

- Java 11 or newer.
- External libraries (included in `lib/`):
  - [Gson](https://github.com/google/gson) for JSON (lib/gson-2.13.1.jar)
  - [Jansi](https://github.com/fusesource/jansi) for CLI coloring (lib/jansi-2.4.2.jar)
- Command-line terminal (Windows CMD or Linux shell supported).

### Build

From the project root (`/cross/`):

```sh
javac -d target/classes -cp "lib/*" @sources.txt
```

Or use the precompiled JARs if provided.

### Run

**Server:**
```sh
java -cp target/classes;lib/* it.unipi.cross.server.ServerMain
# or
java -jar ./cross-server.jar
```

**Client:**
```sh
java --enable-native-access=ALL-UNNAMED -cp target/classes;lib/* it.unipi.cross.client.ClientMain
# or
java --enable-native-access=ALL-UNNAMED -jar ./cross-client.jar
```

### Configuration

All configuration files are in `src/main/resources/`:
- `config.properties` for server/client network settings
- `storedUsers.json`, `storedOrders.json`, `storedTrades.json` for persistence
- `sources.txt` (exception: it's in root folder) for compilation paths

---

## Client Commands

| Command | Description |
|---------|-------------|
| `register(username, password)` | Register new user |
| `login(username, password)` | Login |
| `logout()` | Logout and close session |
| `updateCredentials(username, oldPwd, newPwd)` | Change password |
| `insertLimitOrder(type, size, limitPrice)` | Place limit order |
| `insertMarketOrder(type, size)` | Place market order |
| `insertStopOrder(type, size, stopPrice)` | Place stop order |
| `cancelOrder(orderID)` | Cancel order by ID |
| `getPriceHistory(month)` | Get candlestick data for a month (`MMYYYY`) |
| `help()` | Show command help |
| `exit()` | Exit client |

- **Order parameters**:  
  - `type`: `bid` or `ask`  
  - `size`, `price`, `stopPrice`: positive integers (in thousandths of BTC/USD)

---

## File Formats

- **JSON** for all persistent data and protocol messages.
- See project sources for exact message formats (see also ALLEGATO 1 in `Progetto LAB2425Versione1.3.pdf`).

---

## Testing

- Test users, orders, and trade history are provided in the `stored*.json` files.
- The system initializes `storedTrades.json` from a provided historical dataset (`src/main/resources/storicoOrdini.json`) if empty at start.

---

## Notes

- All server-client messages strictly follow the JSON format described in the project specification.
- No interactive or command-line arguments for configuration; all settings are read from config files.
- GUI is not implemented (yet); all interaction is via command-line interface.
- To be modified:
  - Opening price of daily stats in PriceHistory could be linked to the last day of the previous month for continuity.
  - Concurrency handling could be done better, some structures are unused or unnecessary.
  - On Command Prompt execution, some unhandled exceptions are raised at Ctrl+C.

---

## License

This project was created for educational purposes in the Network Lab course at di.unipi.

---

**For any questions, refer to the project report PDFs or contact the repository maintainer @thetouchyhermes**

---
