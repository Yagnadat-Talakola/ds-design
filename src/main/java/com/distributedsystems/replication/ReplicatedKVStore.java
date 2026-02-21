package com.distributedsystems.replication;

import com.distributedsystems.sharding.ConsistentHashRing;
import com.distributedsystems.storage.StorageEngine;
import java.util.List;
import java.util.Optional;

/**
 * ReplicatedKVStore adds redundancy to the sharded store.
 * Every key is stored on 'replicationFactor' number of nodes.
 */
public class ReplicatedKVStore {
    private final ConsistentHashRing<StorageEngine> hashRing;
    private final int replicationFactor;

    public ReplicatedKVStore(int virtualNodesPerNode, int replicationFactor, List<StorageEngine> initialNodes) {
        this.hashRing = new ConsistentHashRing<>(virtualNodesPerNode, Object::hashCode);
        this.replicationFactor = replicationFactor;
        for (StorageEngine node : initialNodes) {
            hashRing.addNode(node);
        }
    }

    /**
     * Write to all replicas (Synchronous Replication logic).
     */
    public void put(String key, String value) {
        List<StorageEngine> replicas = hashRing.getNodes(key, replicationFactor);
        System.out.println("[Replication] Writing key '" + key + "' to " + replicas.size() + " replicas.");
        for (StorageEngine replica : replicas) {
            replica.put(key, value);
        }
    }

    /**
     * Read from the first available replica.
     */
    public Optional<String> get(String key) {
        List<StorageEngine> replicas = hashRing.getNodes(key, replicationFactor);
        for (StorageEngine replica : replicas) {
            Optional<String> val = replica.get(key);
            if (val.isPresent()) {
                return val;
            }
        }
        return Optional.empty();
    }
}
