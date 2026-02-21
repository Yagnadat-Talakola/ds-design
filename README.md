# Distributed Key-Value Store (Educational Model)

This project is a simplified, modular implementation of a distributed key-value store in Java. It is designed to demonstrate the core architectural patterns of distributed systems using clean abstractions.

## 🏗 Project Structure

- `com.distributedsystems.storage`: **Module 1 - Local Storage.** Defines how data is stored on a single machine.
- `com.distributedsystems.sharding`: **Module 2 - Partitioning.** Defines how data is distributed across multiple machines.
- `com.distributedsystems.replication`: **Module 3 - Redundancy.** Defines how data is copied to prevent loss.

---

## 🟢 Module 1: Storage Engine (The "File Cabinet")
The foundation of the system is the `StorageEngine` interface. 
- **Concept**: A node shouldn't care if it's part of a cluster; it only needs to know how to save and retrieve data locally.
- **Implementation**: We use `InMemoryStorageEngine`, which wraps a `ConcurrentHashMap`. This allows us to focus on distributed logic without worrying about disk I/O or database file formats.

## 🟡 Module 2: Sharding (The "Office Map")
To scale horizontally, we need to split data across multiple nodes. We use **Consistent Hashing** to achieve this.

### 1. The Hash Ring
We imagine the entire range of hash values as a circle. Nodes and Keys are both hashed onto this circle. 
- **The Successor Rule**: A key is stored on the first node encountered when moving clockwise from the key's position.

### 2. Why TreeMap?
We use Java's `TreeMap` because it is a **Red-Black Tree** that keeps keys sorted.
- `tailMap(hash)`: Quickly finds all nodes "clockwise" from a point.
- `firstKey()`: Handles the "wrap-around" (going from the end of the circle back to the start).
- **Efficiency**: Lookup, insertion, and deletion all happen in **O(log N)** time.

### 3. Virtual Nodes
To prevent "hotspots" (where one node gets way more data than others), we map each physical node to multiple points on the ring (e.g., 100 virtual locations). This ensures a uniform distribution of data.

## 🔴 Module 3: Replication (The "Safety Net")
Sharding provides scale, but if a node dies, the data is lost. Replication solves this.

### 1. Successor Strategy
In our model, a key isn't just stored on one node; it is stored on the primary node and the next `N-1` unique physical nodes on the ring.

### 2. Consistency Models
- **Synchronous (Our implementation)**: We write to all replicas before confirming success. This ensures high consistency (all nodes have the data) but increases latency.
- **Asynchronous**: Write to one, then sync in the background. Faster, but carries a risk of data loss.

---

## 🚀 How the Gateway Works (`ShardedKVStore`)
The `ShardedKVStore` is the entry point. It acts as a **Proxy**:
1. It receives a `put(key, value)` request.
2. It asks the `ConsistentHashRing` for the correct node(s).
3. It forwards the request to the `StorageEngine` of those nodes.
4. The user never knows they are interacting with a complex cluster.

---

## 🔵 Module 4: Consensus (The "Board of Directors")
Even in a distributed system, someone often needs to be "in charge" to coordinate tasks like rebalancing the ring or managing metadata.

### 1. Leader Election
We implemented a simplified **Bully Algorithm** to ensure the cluster always has a leader.
- **Identity**: Every node has a unique numeric ID.
- **Hierarchy**: Higher ID = Higher Priority.
- **The "Bully" Logic**: The node with the highest ID that is currently alive is automatically the leader.
- **Failure Detection**: If the leader (e.g., Node 5) fails, the system detects the failure and immediately promotes the next highest-ranking alive node (e.g., Node 4).
- **Recovery**: If Node 5 comes back online, it "bullies" Node 4 out of the leader position and resumes control.

---

## 🚩 Milestone 1: The Foundations (Current State)
We have a functional, in-memory distributed KV store. 
- ✅ **Sharding**: Horizontal scaling via Consistent Hashing.
- ✅ **Replication**: Fault tolerance via the Successor Strategy.
- ✅ **Consensus**: High availability via the Bully Leader Election.
- ✅ **Validation**: Integrated demo simulating real-world failure scenarios.

## 🗺 Future Roadmap
When we resume, we will tackle the "Hard Problems" of Distributed Systems:

1.  **Module 5: Gossip Protocols (Decentralization)**  
    Remove the central `ClusterManager`. Let nodes discover each other and detect failures through "rumors."
2.  **Module 6: Quorum & Consistency (CAP Theorem in Practice)**  
    Implement `R + W > N`. Allow the user to choose between "Fast Reads" (Eventual Consistency) and "Strong Reads" (Linearizability).
3.  **Module 7: Versioning (Vector Clocks)**  
    Handle "Split Brain" scenarios where two nodes accept different values for the same key.
4.  **Module 8: Persistence (LSM-Trees)**  
    Move beyond memory. Implement a Write-Ahead Log (WAL) and SSTables to survive a full cluster power-off.
