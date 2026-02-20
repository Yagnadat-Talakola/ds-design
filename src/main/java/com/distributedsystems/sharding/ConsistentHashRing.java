package com.distributedsystems.sharding;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * ConsistentHashRing implements the core logic for sharding.
 * It maps both keys and nodes onto a virtual ring.
 *
 * @param <T> The type of the node (e.g., Node ID or StorageEngine instance)
 */
public class ConsistentHashRing<T> {
    private final SortedMap<Integer, T> ring = new TreeMap<>();
    private final int numberOfReplicas; // Number of virtual nodes per physical node
    private final Function<Object, Integer> hashFunction;

    public ConsistentHashRing(int numberOfReplicas, Function<Object, Integer> hashFunction) {
        this.numberOfReplicas = numberOfReplicas;
        this.hashFunction = hashFunction;
    }

    public void addNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            // Create a unique identifier for each virtual node to ensure uniform distribution
            ring.put(hashFunction.apply(node.toString() + "_VN_" + i), node);
        }
    }

    public void removeNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            ring.remove(hashFunction.apply(node.toString() + "_VN_" + i));
        }
    }

    public T getNode(Object key) {
        if (ring.isEmpty()) {
            return null;
        }
        int hash = hashFunction.apply(key);
        if (!ring.containsKey(hash)) {
            // tailMap(hash) gives us all nodes with hash >= key's hash
            SortedMap<Integer, T> tailMap = ring.tailMap(hash);
            // If tailMap is empty, wrap around to the first node in the ring
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);
    }
}
