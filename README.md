# Kitchen Command Center - DSA Enterprise Dashboard

**Course/Assessment:** ATOM Post-Assessment Project  
**Language:** Java (JDK 8+)  
**Domain:** Data Structures & Algorithms (DSA) / Systems Engineering  

---

## 1. Problem Statement
Fast-food management systems handle multiple operational tasks simultaneously: prioritizing urgent VIP orders, assembling food items sequentially, managing drive-thru traffic linearly, and looking up customer data instantly. Standard linear data structures fail to satisfy all these operational constraints efficiently. 

This project implements an **Enterprise Kitchen Command Dashboard** web application utilizing Java's core Data Structures to handle live operations in real-time with optimal time complexities.

---

## 2. Objectives
* Build a functional HTTP server in Java without external framework dependencies.
* Demonstrate practical applications of key DSA concepts (**Priority Queue, Stack, Queue, HashMap**).
* Render a real-time web dashboard updating operations every 3 seconds.
* Provide an interactive UI for evaluating real-time operations, logic execution, and state manipulation.

---

## 3. Data Structures Used & Time Complexities

| Data Structure | Operational Role | Selected Implementation | Time Complexity |
| :--- | :--- | :--- | :--- |
| **PriorityQueue** | Priority Order Processing | Min-Heap (`PriorityQueue<KitchenOrder>`) | O(log N) Insertion/Deletion |
| **Stack** | LIFO Burger Assembly | `Stack<String>` | O(1) Push / Pop |
| **Queue** | FIFO Drive-Thru Lane | `LinkedList<String>` | O(1) Enqueue / Dequeue |
| **HashMap** | Loyalty Customer Lookup | `HashMap<String, String>` | O(1) Average Lookup |

---

## 4. Algorithm & Core Logic

### A. Priority Queue Processing (Heapify Strategy)
1. **Order Creation:** `KitchenOrder` implements `Comparable<KitchenOrder>`. Priority values range from 1 (VIP/Express) to 5 (Standard Dine-In).
2. **Sorting Mechanism:** Overridden `compareTo` method compares orders based on numeric priority.
3. **Execution:** Adding a VIP order reorganizes the binary heap in O(log N) time, ensuring high-priority tickets remain at the root for execution (`poll()`).

### B. Drive-Thru Lane (FIFO Protocol)
1. Vehicles enter the drive-thru and join the rear of the queue (`add()`).
2. Order fulfillment processes sequentially from the front (`poll()`), guaranteeing first-come, first-served execution in O(1) time.

---

## 5. Test Cases & Expected Outcomes

| Test Case | Trigger Action | Input Data | Expected Result | DSA Operation |
| :---: | :--- | :--- | :--- | :--- |
| **TC-01** | Add VIP Order | Click `+ Add Express VIP Order` | High-priority order jumps ahead of standard orders in UI. | `PriorityQueue.add()` (O(log N)) |
| **TC-02** | Fulfill Order | Click `🍔 Cook & Poll Priority Ticket` | Highest priority order removed; "Total Served" increments. | `PriorityQueue.poll()` (O(log N)) |
| **TC-03** | Drive-Thru Arrival | Click `🚗 Vehicle Arrives` | New car appends to the bottom of the Drive-Thru queue. | `Queue.add()` (O(1)) |
| **TC-04** | Loyalty Lookup | Click `🔍 Lookup Customer` | Details for phone key `555-0199` retrieved instantly in log. | `HashMap.get()` (O(1)) |

---

## 6. How to Run the Project

1. **Save Source Code:** Ensure `DashboardServer.java` is compiled and present.
2. **Compile:**
   ```bash
   javac DashboardServer.java
