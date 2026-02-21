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
            SortedMap<Integer, T> tailMap = ring.tailMap(hash);
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);
    }

    /**
     * Returns the next N unique nodes on the ring for replication.
     */
    public java.util.List<T> getNodes(Object key, int count) {
        if (ring.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        
        java.util.List<T> result = new java.util.ArrayList<>();
        int hash = hashFunction.apply(key);
        
        // Find the starting point
        SortedMap<Integer, T> tailMap = ring.tailMap(hash);
        Integer currentHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        
        while (result.size() < count && result.size() < ring.size()) {
            T node = ring.get(currentHash);
            if (!result.contains(node)) {
                result.add(node);
            }
            
            // Move to the next node in the ring
            SortedMap<Integer, T> nextMap = ring.tailMap(currentHash + 1);
            currentHash = nextMap.isEmpty() ? ring.firstKey() : nextMap.firstKey();
            
            // Safety break for infinite loops if ring is small
            if (currentHash.equals(hash) && !result.isEmpty()) break;
        }
        return result;
    }
}
