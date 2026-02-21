package com.distributedsystems;

import com.distributedsystems.replication.ReplicatedKVStore;
import com.distributedsystems.storage.InMemoryStorageEngine;
import com.distributedsystems.storage.StorageEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Optional;

public class ReplicationTest {
    @Test
    public void testDataReplicatedToMultipleNodes() {
        StorageEngine node1 = new InMemoryStorageEngine();
        StorageEngine node2 = new InMemoryStorageEngine();
        
        ReplicatedKVStore store = new ReplicatedKVStore(1, 2, Arrays.asList(node1, node2));
        store.put("shared-key", "secret-value");

        // Verify both nodes have the data
        assertEquals(Optional.of("secret-value"), node1.get("shared-key"));
        assertEquals(Optional.of("secret-value"), node2.get("shared-key"));
    }
}
