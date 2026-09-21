# Word War Z: A Distributed Multi-Language Hangman Game using CORBA
![WordWarZ](./res/images/others/word%20war%20z%20logo.png)

---
## Table of Contents
1. [Game Concept](#gameconcept)
2. [Architecture](#architecture)
3. [Distributed Languages](#distributedlanguages)
4. [Collaborators](#collaborators)

---

## Game Concept
### How does the program work?

**1. Authentication and Roles**
- *Players:*
    1. Players must log in with credentials stored in MySQL Database.
    2. New players must request an account from the administration.
    3. Only one active session per account.
- *Administrators:*
    1. Administrators have CRUD+Search Operations: they can create, update, delete and search for players.
    2. Administrators can adjust the game settings whereas they can modify the waiting time for players and round duration.

**2. Game Lobby**
- After logging in, players have 3 major functionalities to press:
  1. **Play:** Initiates or joins a queue for a game. 
  2. **Leaderboard:** Check the top 5 players with the most wins.
  3. **Logout:** Exits the game.
- The Java implementation also consists of these features:
  1. **Character Selection:** Player can select the character sprite they want to play in.
  2. **How to Play:** A simple overlay that shows the rules of the game.
  3. **Credits:** A simple overlay that shows gratitude from which the assets are extracted from for the game.

**3. Starting a Game**
- A player selects **"Play"** and either creates a queue or joins one, depending if a queue already exists.
- **Queue rules:**
  1. At least **2 players** must join within the set time for the game to begin.
  2. If no one joins, the server sends an exception, and the player returns to the lobby.
- **Concurrent games:** Multiple games can run simultaneously, each with independent word pools.

**4. Gameplay**
   - *Word Selection:*
     - Server selects a random word from a pool of words. 
     - Players must guess the word letter by letter.
   - *Guessing Rules:*
     - Players can only guess **one letter** at a time. 
     - Correct guesses reveal all instances of the letter in the word.
     - Only **five** incorrect guesses are allowed.

**5. Leaderboard and Data Persistence**
   - *Leaderboard:*
     - Top **five** players with the most wins are displayed.
   - *Data Persistence:*
     - Players' data is stored in a **MySQL database**.
     - Player information includes their username, wins, and credentials.

---
## Architecture
**1. CORBA IDL Interfaces**
- **AdminService.idl:** Defines the interface for the admin operations.
- **GameService.idl:** Defines the interface for the game operations.

**2. Server Implementations**
- Thread-safe design to handle concurrent games.
- MySQL Integration stores player data, game status, and game history.
- Word Randomization that selects a random word from words.txt per game round.

**3. Client Implementations**
- *Java Client (Player and Admin):*
  - GUI: Swing/JavaFX for the user interface.
  - Features: Login, Play, Leaderboard, How to Play, Credits, Character Selection, Logout.
- *Python Client (Player Only):*
  - Lightweight CLI: supports core gameplay features.

---
## Distributed Languages

| Component        | Language | Key Features                                  |  
|------------------|----------|-----------------------------------------------|  
| **Server**       | Java     | CORBA ORB, MySQL connector, multi-threading   |  
| **Player Client**| Java     | GUI, animations              |  
| **Admin Client** | Java     | GUI, admin controls                          |
| **Player Client**| Python   | Minimalist CLI, CORBA stubs via `omniORB`     |  

---
## Acknowledgments
- Visual assets and sprites used in this game were extracted from the game **Dead Ahead**.

---
## Collaborators
- Adame, Noelle Lorraine
- Atis, Jan Christian
- Balagot, Clarenz William
- Bambao, Johana Izabelle
- Miranda, Frances Julia
- Terre, Jorge Frederic

---
## Lessons Learned & Possible Future Improvements

This project was built during our 2nd year for an Integrative Technologies course, where using **CORBA** was a strict academic requirement to learn the fundamentals of distributed systems and language interoperability. 

While the project successfully met all requirements and taught us a lot about multithreading, concurrency, and RPC mechanisms, there are several areas for improvement if we were to rebuild it today:

1. **Modernizing the Architecture:** CORBA is a legacy technology. Today, implementing the networking layer using modern alternatives like **gRPC** (with Protocol Buffers) or **WebSockets** will be more efficient, real-time bidirectional communication between the clients and the server.
2. **Refactoring "God Objects":** The game logic is currently heavily centralized in single classes (like `GameServant`). It is ideal to refactor this into smaller, single-responsibility services (e.g., separating the Matchmaker Service, Game State Manager, and Connection Monitor).
3. **Security Enhancements:** 
   - **Password Hashing:** Currently, passwords are treated as plaintext strings. Integrating a library like `jBCrypt` to hash and salt passwords before storing them in the database would be a security improvement.
   - **Environment Variables:** Hardcoded configurations (like database credentials in `DBConnection`) would be moved to environment variables or a configuration file.