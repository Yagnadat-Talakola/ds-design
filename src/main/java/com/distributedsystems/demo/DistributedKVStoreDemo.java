package com.distributedsystems.demo;

import com.distributedsystems.consensus.ClusterManager;
import com.distributedsystems.consensus.ClusterNode;
import com.distributedsystems.replication.ReplicatedKVStore;
import com.distributedsystems.storage.InMemoryStorageEngine;
import com.distributedsystems.storage.StorageEngine;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DistributedKVStoreDemo {
    public static void main(String[] args) {
        System.out.println("=== DISTRIBUTED KV STORE SYSTEM DEMO ===\n");

        // 1. Initialize Cluster
        ClusterManager clusterManager = new ClusterManager();
        List<StorageEngine> storageNodes = new ArrayList<>();

        System.out.println("--- STEP 1: Creating a 5-node cluster ---");
        for (int i = 1; i <= 5; i++) {
            ClusterNode node = new ClusterNode(i);
            storageNodes.add(new InMemoryStorageEngine("StorageNode-" + i));
            clusterManager.addNode(node);
        }

        // 2. Initialize Store with Replication Factor 3
        ReplicatedKVStore store = new ReplicatedKVStore(10, 3, storageNodes);

        // 3. Perform Writes
        System.out.println("\n--- STEP 2: Writing data with Replication Factor = 3 ---");
        store.put("session_123", "User_Data_A");
        store.put("session_456", "User_Data_B");

        // 4. Verify Data
        System.out.println("\n--- STEP 3: Verifying data exists ---");
        System.out.println("Value for 'session_123': " + store.get("session_123").orElse("Not Found"));

        // 5. Simulate Failure
        System.out.println("\n--- STEP 4: Simulating failure of the Leader (Node 5) ---");
        clusterManager.onNodeFailure(5);

        // 6. Verify Availability
        System.out.println("\n--- STEP 5: Verifying data availability after node failure ---");
        System.out.println("Value for 'session_123' after Node 5 failure: " + store.get("session_123").orElse("Not Found"));

        // 7. Extreme Failure
        System.out.println("\n--- STEP 6: Simulating extreme failure (killing Nodes 4 and 3) ---");
        clusterManager.onNodeFailure(4);
        clusterManager.onNodeFailure(3);
        System.out.println("Value for 'session_456' after multiple failures: " + store.get("session_456").orElse("Not Found"));

        System.out.println("\n=== DEMO COMPLETED SUCCESSFULLY ===");
    }
}
