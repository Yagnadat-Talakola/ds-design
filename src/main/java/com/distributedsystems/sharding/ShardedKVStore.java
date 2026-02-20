package com.distributedsystems.sharding;

import com.distributedsystems.storage.StorageEngine;
import java.util.List;
import java.util.Optional;

/**
 * ShardedKVStore demonstrates the abstraction of a distributed key-value store.
 * It uses a ConsistentHashRing to route requests to the correct local StorageEngine.
 */
public class ShardedKVStore {
    private final ConsistentHashRing<StorageEngine> hashRing;

    public ShardedKVStore(int virtualNodesPerNode, List<StorageEngine> initialNodes) {
        // Using a simple hashCode for demonstration. 
        this.hashRing = new ConsistentHashRing<>(virtualNodesPerNode, Object::hashCode);
        for (StorageEngine node : initialNodes) {
            hashRing.addNode(node);
        }
    }

    public void put(String key, String value) {
        StorageEngine targetNode = hashRing.getNode(key);
        if (targetNode != null) {
            targetNode.put(key, value);
        }
    }

    public Optional<String> get(String key) {
        StorageEngine targetNode = hashRing.getNode(key);
        return targetNode != null ? targetNode.get(key) : Optional.empty();
    }
    
    public void addNode(StorageEngine node) {
        hashRing.addNode(node);
    }
}
