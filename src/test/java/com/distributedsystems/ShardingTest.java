package com.distributedsystems;

import com.distributedsystems.sharding.ConsistentHashRing;
import com.distributedsystems.storage.InMemoryStorageEngine;
import com.distributedsystems.storage.StorageEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class ShardingTest {
    @Test
    public void testConsistentHashingSuccessor() {
        ConsistentHashRing<String> ring = new ConsistentHashRing<>(1, Object::hashCode);
        ring.addNode("Node-A"); // Assume hash code is fixed for this test
        ring.addNode("Node-B");
        
        String node = ring.getNode("some-key");
        assertNotNull(node);
        assertTrue(node.equals("Node-A") || node.equals("Node-B"));
    }

    @Test
    public void testReplicationNodesUnique() {
        ConsistentHashRing<String> ring = new ConsistentHashRing<>(3, Object::hashCode);
        ring.addNode("Node-A");
        ring.addNode("Node-B");
        ring.addNode("Node-C");

        List<String> nodes = ring.getNodes("key1", 2);
        assertEquals(2, nodes.size());
        assertNotEquals(nodes.get(0), nodes.get(1), "Replicas must be unique physical nodes");
    }
}
