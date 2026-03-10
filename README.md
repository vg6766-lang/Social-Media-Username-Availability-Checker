 ### Project Overview

* **Name:** UsernameCheckerApp
* **Objective:** Design a real-time system to check username availability for a platform with 10 million users.
* **Performance Goal:** $O(1)$ time complexity for lookups and handling 1,000 concurrent requests per second.

---

### Core Technical Features

* **Constant-Time Lookup:** Utilizes `java.util.HashMap` to achieve $O(1)$ average time complexity for availability checks regardless of user base size.
* **Conflict Resolution:** Employs Java's internal collision handling (Chaining and Red-Black Trees) to maintain performance during high-density data mapping.
* **Suggestion Engine:** Automatically generates up to 3 alternative usernames by appending numerical suffixes when a primary choice is taken.
* **Popularity Tracking:** A secondary `HashMap` tracks the frequency of every attempt to identify the most searched usernames.

---

### Execution Instructions

1. **Save File:** Save the provided source code as `UsernameCheckerApp.java`.
2. **Compile:** Run `javac UsernameCheckerApp.java` in the terminal.
3. **Run:** Execute the program using `java UsernameCheckerApp`.
4. **Interaction:** Enter a username when prompted. The system will return availability, suggestions if taken, and current trending statistics. Type `exit` to close.

---

### Complexity Analysis

* **Availability Check:** $O(1)$ average case.
* **Alternative Generation:** $O(k)$ where $k$ is the number of suggestions required.
* **Popularity Retrieval:** $O(n)$ where $n$ is the number of unique attempted usernames.
* **Space Complexity:** $O(N)$ where $N$ is the number of registered users (approximately 1GB - 2GB RAM for 10 million users).

---

### Recommended Production Optimizations

* **Concurrency:** Replace `HashMap` with `ConcurrentHashMap` for thread-safe operations in multi-user environments.
* **Memory Management:** Implement a **Bloom Filter** to verify availability before querying the main registry to save memory and CPU cycles.
* **Distributed Storage:** Use an in-memory data store like **Redis** to scale across multiple server nodes.

